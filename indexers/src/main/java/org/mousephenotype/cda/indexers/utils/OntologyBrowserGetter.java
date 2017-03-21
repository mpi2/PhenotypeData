package org.mousephenotype.cda.indexers.utils;

import org.apache.solr.client.solrj.SolrServerException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mousephenotype.cda.owl.OntologyParser;
import org.mousephenotype.cda.owl.OntologyTermDTO;
import org.mousephenotype.cda.solr.service.PostQcService;
import org.mousephenotype.cda.solr.service.PreQcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@EnableAutoConfiguration
public class OntologyBrowserGetter {

	@Autowired
	@Qualifier("ontodbDataSource")
	DataSource ontodbDataSource;

    @Autowired
    @Qualifier("postqcService")
    PostQcService postqcService;

    @Autowired
    @Qualifier("preqcService")
    PreQcService preqcService;

	public OntologyBrowserGetter(DataSource ontodbDataSource){

		this.ontodbDataSource = ontodbDataSource;

	}

	public OntologyBrowserGetter(){	}

	public List<JSONObject> createTreeJson(TreeHelper helper, String rootNodeId, String childNodeId, String termId, Map<String, Integer> mpGeneVariantCount)
			throws SQLException {

		List<JSONObject> tn = new ArrayList<>();
		String sql = fetchNextLevelChildrenSql(helper, rootNodeId, childNodeId);
		//System.out.println("SQL1: "+ sql);
		try (Connection conn = ontodbDataSource.getConnection(); PreparedStatement p = conn.prepareStatement(sql)) {

			ResultSet resultSet = p.executeQuery();

            //System.out.println("PATH: " + helper.getPathNodes().size());
			while (resultSet.next()) {

				String nodeId = resultSet.getString("node_id");  // child_node_id
                String nodeIdTermId = resultSet.getString("term_id");
				//System.out.println("open: " + helper.getPreOpenNodes() + " vs node id --- "+ nodeId);

				if ( helper.getPreOpenNodes().containsKey(nodeId)){   // check if this is the node to start fetching its children recursively
					// the tree should be expanded until the query term eg. 5267 = [{0=5344}, {0=5353}], a node could have same top node but diff. end node
					String topNodeId = rootNodeId;
					for ( Map<String, String> topEnd : helper.getPreOpenNodes().get(nodeId) ) {
						for (String thisTopNodeId : topEnd.keySet()) {
							topNodeId = thisTopNodeId;
							break;  // do only once: should have a better way
						}
					}

					String thisSql = fetchNextLevelChildrenSql(helper, topNodeId, nodeId);
					//System.out.println("SQL2: "+ thisSql);
					try (PreparedStatement p2 = conn.prepareStatement(thisSql)) {

						ResultSet resultSet2 = p2.executeQuery();
						while (resultSet2.next()) {

							JSONObject thisNode = fetchNodeInfo(helper, resultSet2, mpGeneVariantCount);
							//System.out.println(nodeId + " -- THIS NODE: "+ thisNode.toString());
							if ( thisNode != null ) {
								if (thisNode.getBoolean("children")) {
									thisNode = fetchChildNodes(helper, thisNode, termId, mpGeneVariantCount);
									//System.out.println("CHILD TERM ID: "+thisNode.getString("term_id"));

									if (termId.equalsIgnoreCase(thisNode.getString("term_id"))) {
										thisNode.accumulate("state", getState(false));
									} else {
										thisNode.accumulate("state", getState(true));  // whether its subtree is open or closed
									}

                                    if (termId.startsWith("MP:") && helper.getPathNodes().size()==2){
                                        thisNode.put("children", true);
                                        JSONObject jstate = (JSONObject) thisNode.get("state");
                                        jstate.put("opened", true);
                                    }

                                    //System.out.println("check children: "+ thisNode.toString());

								}
								if (termId.equalsIgnoreCase(thisNode.getString("term_id"))) {
									thisNode.accumulate("type", "selected"); // shows as blue


								}
								tn.add(thisNode);
							}
						}

					} catch(Exception e){
						e.printStackTrace();
					}
				}
				else {
					// just fetch the term of this node
					JSONObject thisNode = fetchNodeInfo(helper, resultSet, mpGeneVariantCount);
					if ( thisNode != null ) {
						tn.add(thisNode);
					}
				}
			}
		}  catch (Exception e){
			e.printStackTrace();
		}

		return tn;
	}

	public String getScrollTo(List<JSONObject> tree){

		for (JSONObject topLevel: tree){
			if (topLevel.has("state") && topLevel.getJSONObject("state").has("opened") && topLevel.getJSONObject("state").getString("opened").equalsIgnoreCase("true")){
				return topLevel.getString("id");
			}
		}
		return "";
	}

	private JSONObject fetchChildNodes(TreeHelper helper, JSONObject nodeObj, String termId, Map<String, Integer> mpGeneVariantCount)
            throws SQLException, SolrServerException, IOException, URISyntaxException {

		String parentNodeId = nodeObj.getString("id");
		String childNodeId = null;
		String sql = fetchNextLevelChildrenSql(helper, parentNodeId, childNodeId);
		//System.out.println("CHILD NODE SQL: "+ sql);
		List<JSONObject> children = new ArrayList<>();

		try (Connection conn = ontodbDataSource.getConnection(); PreparedStatement p = conn.prepareStatement(sql)) {

			ResultSet resultSet = p.executeQuery();
			while (resultSet.next()) {

				if (helper.getPathNodes().contains(Integer.toString(resultSet.getInt("node_id")))) {
					JSONObject thisNode = fetchNodeInfo(helper, resultSet, mpGeneVariantCount);

					if (thisNode != null ) {
						if (thisNode.getBoolean("children")) {
							thisNode = recursiveFetchChildNodes(helper, thisNode, conn, termId, mpGeneVariantCount);
							if (termId.equalsIgnoreCase(thisNode.getString("term_id"))) {
								thisNode.accumulate("state", getState(false));
							} else {
								thisNode.accumulate("state", getState(true));
							}
						} else {
							thisNode.accumulate("state", getState(false));
						}
						if (termId.equalsIgnoreCase(thisNode.getString("term_id"))) {
							thisNode.accumulate("type", "selected");
						}
						children.add(thisNode);
					}
				}
			}

			nodeObj.put("children", children);
		}

		return nodeObj;

	}

	private JSONObject recursiveFetchChildNodes(TreeHelper helper, JSONObject nodeObj, Connection conn, String termId, Map<String, Integer> mpGeneVariantCount)
			throws SQLException {

		String parentNodeId = nodeObj.getString("id");
		String childNodeId = null;
		String sql = fetchNextLevelChildrenSql(helper, parentNodeId, childNodeId);
		List<JSONObject> children = new ArrayList<>();

		try ( PreparedStatement p = conn.prepareStatement(sql)) {

			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {


				if (helper.getPathNodes().contains(Integer.toString(resultSet.getInt("node_id")))) {

					JSONObject thisNode = fetchNodeInfo(helper, resultSet, mpGeneVariantCount);

					if ( thisNode != null ) {
						if (thisNode.getBoolean("children")) {
							thisNode = recursiveFetchChildNodes(helper, thisNode, conn, termId, mpGeneVariantCount);
							if (thisNode.getString("term_id").equalsIgnoreCase(termId)) {
								thisNode.accumulate("state", getState(false));
							} else {
								thisNode.accumulate("state", getState(true));
							}
						} else {
							thisNode.accumulate("state", getState(false));
						}

						if (termId.equalsIgnoreCase(thisNode.getString("term_id"))) {
							thisNode.accumulate("type", "selected");
						}
						children.add(thisNode);
						nodeObj.put("children", children);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return nodeObj;
	}

	private JSONObject getState(boolean opened){
		JSONObject state = new JSONObject();
		state.accumulate("opened", opened);
		return state;
	}

	private String fetchNextLevelChildrenSql(TreeHelper helper, String parentNodeId, String childNodeId)
			throws SQLException {

		// return a query to get all children of [parentNodeId]

		String ontologyName = helper.getOntologyName();
		String subqry = "SELECT child_node_id "
				+ "FROM " + ontologyName + "_parent_children "
				+ "WHERE parent_node_id = " + parentNodeId;

		if ( childNodeId != null ){
			subqry += " AND child_node_id = " + childNodeId;
		}

		String sql = "SELECT n.node_id, n.term_id, t.name, nt.node_type "
				+ "FROM " + ontologyName + "_node2term n, " + ontologyName + "_term_infos t, " + ontologyName + "_node_id_type nt "
				+ "WHERE n.term_id=t.term_id "
				+ "AND n.node_id=nt.node_id "
				+ "AND n.node_id IN (" + subqry + ") "
				+ "ORDER BY t.name";

		//System.out.println("NEXT LVL SQL: " + sql);
		return sql;
	}

	public TreeHelper getTreeHelper( String ontologyName, String termId)
			throws SQLException {

		String query = "SELECT CONCAT (fullpath , ' ' , node_id) AS path " + "FROM " + ontologyName
				+ "_node_backtrace_fullpath " + "WHERE node_id IN " + "(SELECT node_id FROM " + ontologyName
				+ "_node2term WHERE term_id = ?)";


		//System.out.println(termId + " -- TREE HELPER QUERY: " + query);
		Map<String, String> nameMap = new HashMap<>();
		nameMap.put("ma", "/data/anatomy");
		nameMap.put("emapa", "/data/anatomy");
		nameMap.put("mp", "/data/phenotypes");


		String pageBaseUrl =  nameMap.get(ontologyName);

		Set<String> pathNodes = new HashSet<>();
		Set<String> expandNodeIds = new HashSet<>();
		Map<String, List<Map<String, String>>> preOpenNodes = new HashMap<>();


		try ( Connection conn = ontodbDataSource.getConnection();
          PreparedStatement p = conn.prepareStatement(query)) {

			p.setString(1, termId);
            ResultSet resultSet = p.executeQuery();


			int topIndex = 0;
			int startNodeIndex = 0;
			int minPathLen = 0;

			if ( ontologyName.equals("ma")){
				topIndex = 2; // 3rd in the fullpath is the one below the real root in obo
				startNodeIndex = 1;  // mouse anatomical structure (under mouse anatomical entity)
				minPathLen = 3;
			}
			else if (ontologyName.equals("emapa")){
				topIndex = 2; // 3rd in the fullpath is the one below the real root in obo
				startNodeIndex = 1;
				minPathLen = 3;
			}

			else if (ontologyName.equals("mp")){
				topIndex = 1; // 2nd in the fullpath is the one below the real root (mammalian phenotype: node=0) in obo
				startNodeIndex = 0;
				minPathLen = 2;
			}

			while (resultSet.next()) {

				String fullpath = resultSet.getString("path");
				//System.out.println("Path: " + fullpath);
				String[] nodes = fullpath.split(" ");

				if ( nodes.length >= minPathLen ) {
					pathNodes.addAll(Arrays.asList(nodes));

					String topNodeId = nodes[topIndex]; // 2nd in fullpath
					String endNodeId = nodes[nodes.length - 1]; // last in fullpath

					//System.out.println("startnode index: "+startNodeIndex);
					//System.out.println("topnode: "+topNodeId);

					expandNodeIds.add(endNodeId);


					if (!preOpenNodes.containsKey(topNodeId)) {
						preOpenNodes.put(topNodeId, new ArrayList<Map<String, String>>());
					}

					Map<String, String> nodeStartEnd = new HashMap<>();

					nodeStartEnd.put(nodes[startNodeIndex], endNodeId);

					preOpenNodes.get(topNodeId).add(nodeStartEnd);

				}
			}
		}


		TreeHelper th = new TreeHelper();
		th.setPathNodes(pathNodes);
		th.setExpandNodeIds(expandNodeIds);
		th.setPreOpenNodes(preOpenNodes);
		th.setPageBaseUrl(pageBaseUrl);
		th.setOntologyName(ontologyName);
		//System.out.println(th.toString());
		return th;

	}

	public List<String> getExcludedNodeIds() throws SQLException {

		List<String> excludedNodeIds = new ArrayList<>();

		String query = "SELECT node_id FROM ma_node2term nt, ma_term_infos t "
				+ "WHERE nt.term_id=t.term_id "
				+ "AND t.name != \"organ system\" "
				+ "AND nt.node_id IN "
				+ "(SELECT child_node_id FROM ma_parent_children WHERE parent_node_id = 1)";

		try (Connection conn = ontodbDataSource.getConnection();
			 PreparedStatement p = conn.prepareStatement(query)) {

			ResultSet resultSet = p.executeQuery();
			while (resultSet.next()) {
				excludedNodeIds.add(Integer.toString(resultSet.getInt("node_id")));
			}
		}

		return excludedNodeIds;
	}

	private JSONObject fetchNodeInfo(TreeHelper helper, ResultSet resultSet, Map<String, Integer> mpGeneVariantCount) throws SQLException, SolrServerException, IOException, URISyntaxException {

		JSONObject node = new JSONObject();

		String nodeId = Integer.toString(resultSet.getInt("node_id"));

		String termId = resultSet.getString("term_id");
		String name = resultSet.getString("name");
		String termDisplayText = null;

		if (helper.getExpandNodeIds().contains(nodeId)) {
			termDisplayText = "<span class='qryTerm'>" + name + "</span>";
		} else {
			termDisplayText = name;
		}

		String url = "<a target='_blank' href='" + helper.getPageBaseUrl() + "/" + termId + "'>" + termDisplayText + "</a>";

		if ( termId.startsWith("MP:")) {
			int gvCount = mpGeneVariantCount.containsKey(termId) ? mpGeneVariantCount.get(termId) : 0;
			//System.out.println(termId + " -- " +gvCount);
			url = "<a target='_blank' href='" + helper.getPageBaseUrl() + "/" + termId + "'>" + termDisplayText + " (<span class='gpAssoc'>" + gvCount + "</span>)"	+ "</a>";
		}
		node.put("text", url);
		node.put("id", Integer.toString(resultSet.getInt("node_id")));
		node.put("term_id", resultSet.getString("term_id"));
		node.put("children", resultSet.getString("node_type").equals("folder") ? true : false);
		node.put("href", helper.getPageBaseUrl() + "/" + termId);
		node.put("hrefTarget", "_blank");

		return node;
	}

	public class TreeHelper {

		Set<String> pathNodes;
		Map<String, List<Map<String, String>>> preOpenNodes;
		Set<String> expandNodeIds;
		String pageBaseUrl;
		String ontologyName;
		List<String> excludedNodeIds;

		public Set<String> getPathNodes() {
			return pathNodes;
		}

		public void setPathNodes(Set<String> pathNodes) {
			this.pathNodes = pathNodes;
		}

		public Map<String, List<Map<String, String>>> getPreOpenNodes() {
			return preOpenNodes;
		}

		public void setPreOpenNodes(Map<String, List<Map<String, String>>> preOpenNodes) {
			this.preOpenNodes = preOpenNodes;
		}

		public Set<String> getExpandNodeIds() {
			return expandNodeIds;
		}

		public void setExpandNodeIds(Set<String> expandNodeIds) {
			this.expandNodeIds = expandNodeIds;
		}

		public String getPageBaseUrl() {
			return pageBaseUrl;
		}

		public void setPageBaseUrl(String pageBaseUrl) {
			this.pageBaseUrl = pageBaseUrl;
		}

		public String getOntologyName() {
			return ontologyName;
		}

		public void setOntologyName(String ontologyName) {
			this.ontologyName = ontologyName;
		}

		public List<String> getExcludedNodeIds() {
			return excludedNodeIds;
		}

		public void setExcludedNodeIds(List<String> excludedNodeIds) {
			this.excludedNodeIds = excludedNodeIds;
		}


		@Override
		public String toString() {
			return "TreeHelper{" +
					"pathNodes=" + pathNodes +
					", preOpenNodes=" + preOpenNodes +
					", expandNodeIds=" + expandNodeIds +
					", pageBaseUrl='" + pageBaseUrl + '\'' +
					", ontologyName='" + ontologyName + '\'' +
					", excludedNodeIds=" + excludedNodeIds +
					'}';
		}
	}


	public List<JSONObject> createTreeJson(OntologyTermDTO term, String baseUrl, OntologyParser parser){

		List<JSONObject> tree = new ArrayList<>();
		Map<Integer, JSONObject> nodes = new HashMap<>();
		if (term.getPathsToRoot() != null) {
			for (List<Integer> path : term.getPathsToRoot().values()) {
				getJson(path, baseUrl, parser, term.getAccessionId(), nodes);
			}
		} else {
			System.out.println("No path to root for " + term.getAccessionId());
		}

		tree.add(nodes.get(0)); // mammalian phenotype for MP, root is 0

		// TODO add json objects for other top levels ? ? ?
		return tree;
	}

	/**
	 *
	 * @param baseUrl /data/phenotypes/ for mp links
	 * @return
	 */
	JSONObject getJson( List<Integer> path,  String baseUrl, OntologyParser parser, String searchTermId, Map<Integer, JSONObject> nodes){

		List<Integer> remainingPath = new ArrayList<>(path); // don't modify original
		// remove the part of the path that was already added to the JSON object
		for (int i = 0; i < path.size()-1 && nodes.containsKey(path.get(i+1)); i++){
			remainingPath.remove(path.get(i));
		}

		if (remainingPath.size() > 0) {

			Integer id = remainingPath.get(0);
			remainingPath.remove(0);
			OntologyTermDTO term = parser.getOntologyTerm(id);
			JSONObject current = getJsonObjectWithBasicInfo(term, searchTermId, baseUrl, id, nodes);

			if (remainingPath.size() > 0) {
				JSONArray children = current.has("children") && ! (current.get("children") instanceof Boolean) ? current.getJSONArray("children") : new JSONArray();
				children.put(getJson(remainingPath, baseUrl, parser, searchTermId, nodes));
				current.put("children", children);
				current.put("state", getState(path.size() > 0));
			} else {
				current.put("children", hasChildren(term));
			}
			return current;
		}
		return null;
	}


	private JSONObject getJsonObjectWithBasicInfo(OntologyTermDTO term, String searchTermId, String baseUrl, Integer id, Map<Integer, JSONObject> nodes){

		JSONObject current;
		if (nodes.containsKey(id)){
			current = nodes.get(id);
		} else {
			current = new JSONObject();
			current.put("text", getText(term, searchTermId, baseUrl));
			current.put("id", id);
			current.put("term_id", term.getAccessionId());
			current.put("hrefTarget", "_blank");
			if (searchTermId.equals(term.getAccessionId())){
				current.put("type", "selected"); // blue dot
			}
			nodes.put(id, current);
		}

		return current;

	}

	private String getText (OntologyTermDTO term, String searchTermId, String baseUrl){

		String text = "<a target='_blank' href='" + baseUrl + term.getAccessionId() + "'>";
		if (searchTermId.equals(term.getAccessionId())){
			text += "<span class='qryTerm'>" + term.getName() + "</span>";
		} else {
			text += term.getName();
		}
		text += "</a>";

		return text;

	}

	private boolean hasChildren(OntologyTermDTO term){

		return term.getChildIds() != null && term.getChildIds().size() > 0;

	}

}
