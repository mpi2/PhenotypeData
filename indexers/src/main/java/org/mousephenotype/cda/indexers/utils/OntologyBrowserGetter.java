package org.mousephenotype.cda.indexers.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import net.sf.json.JSONObject;


public class OntologyBrowserGetter {

    DataSource ontodbDataSource;

    public OntologyBrowserGetter(DataSource ontodbDataSource){
    	this.ontodbDataSource = ontodbDataSource;
    }

    
    public List<JSONObject> createTreeJson(TreeHelper helper, String rootNodeId, String childNodeId, String termId)
    throws SQLException {

        List<JSONObject> tn = new ArrayList<>();
        String sql = fetchNextLevelChildrenSql(helper, rootNodeId, childNodeId);
		//System.out.println("SQL1: "+ sql);
		try (Connection conn = ontodbDataSource.getConnection(); PreparedStatement p = conn.prepareStatement(sql)) {

            ResultSet resultSet = p.executeQuery();
            
            while (resultSet.next()) {

                String nodeId = resultSet.getString("node_id");  // child_node_id
                if ( helper.getPreOpenNodes().containsKey(nodeId)){   // check if this is the node to start fetching it children recursively
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

                            JSONObject thisNode = fetchNodeInfo(helper, resultSet2);
                            if (thisNode.getBoolean("children")) {
                                thisNode = fetchChildNodes(helper, thisNode, termId);
                                if (termId.equalsIgnoreCase(thisNode.getString("term_id"))){
                                	thisNode.accumulate("state", getState(false));    
                                } else {
                                	thisNode.accumulate("state", getState(true));
                                }
                            } 
        					if (termId.equalsIgnoreCase(thisNode.getString("term_id"))){ 
        						thisNode.accumulate("type", "selected");
        					}
                            tn.add(thisNode);
                        }
                        
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }
                else {
                    // just fetch the term of this node
                    JSONObject thisNode = fetchNodeInfo(helper, resultSet);
                    tn.add(thisNode);
                }
            }
        }  catch (Exception e){
            e.printStackTrace();
        }

        return tn;
    }
    
    public String getScrollTo(List<JSONObject> tree){
    	
    	for (JSONObject topLevel: tree){
    		if (topLevel.containsKey("state") && topLevel.getJSONObject("state").containsKey("opened") && topLevel.getJSONObject("state").getString("opened").equalsIgnoreCase("true")){
    			return topLevel.getString("id");
    		}
    	}
    	return "";
    }

    private JSONObject fetchChildNodes(TreeHelper helper, JSONObject nodeObj, String termId) 
    throws SQLException {

    	String parentNodeId = nodeObj.getString("id");
        String childNodeId = null;
        String sql = fetchNextLevelChildrenSql(helper, parentNodeId, childNodeId);
        List<JSONObject> children = new ArrayList<>();

        try (Connection conn = ontodbDataSource.getConnection(); PreparedStatement p = conn.prepareStatement(sql)) {

			ResultSet resultSet = p.executeQuery();			
			while (resultSet.next()) {
	
				if (helper.getPathNodes().contains(Integer.toString(resultSet.getInt("node_id")))) {
					JSONObject thisNode = fetchNodeInfo(helper, resultSet);
					if (thisNode.getBoolean("children")) {
						thisNode = recursiveFetchChildNodes(helper, thisNode, conn, termId);
                        if (termId.equalsIgnoreCase(thisNode.getString("term_id"))){
                        	thisNode.accumulate("state", getState(false)); 
                        } else {
                        	thisNode.accumulate("state", getState(true));
                        }
					} else {
						thisNode.accumulate("state", getState(false));
					}
					if (termId.equalsIgnoreCase(thisNode.getString("term_id"))){ 
						thisNode.accumulate("type", "selected");
					}
					children.add(thisNode);
				}
			}
	
			nodeObj.put("children", children);
        }

        return nodeObj;
        
    }

	private JSONObject recursiveFetchChildNodes(TreeHelper helper, JSONObject nodeObj, Connection conn, String termId) 
	throws SQLException {
		
		String parentNodeId = nodeObj.getString("id");
		String childNodeId = null;
		String sql = fetchNextLevelChildrenSql(helper, parentNodeId, childNodeId);
		List<JSONObject> children = new ArrayList<>();
		
		try ( PreparedStatement p = conn.prepareStatement(sql)) {

			ResultSet resultSet = p.executeQuery();
			
			while (resultSet.next()) {
				
				
				if (helper.getPathNodes().contains(Integer.toString(resultSet.getInt("node_id")))) {
					
					JSONObject thisNode = fetchNodeInfo(helper, resultSet);
					
					if (thisNode.getBoolean("children")) {
						thisNode = recursiveFetchChildNodes(helper, thisNode, conn, termId);
						if (thisNode.getString("term_id").equalsIgnoreCase(termId)){
							thisNode.accumulate("state", getState(false));
						} else {
							thisNode.accumulate("state", getState(true));
						}
					} else {
						thisNode.accumulate("state", getState(false));
					}

					if (termId.equalsIgnoreCase(thisNode.getString("term_id"))){ 
						thisNode.accumulate("type", "selected");
					}
					children.add(thisNode);
					nodeObj.put("children", children);
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
        
        return sql;
    }

	public TreeHelper getTreeHelper( String ontologyName, String termId) 
	throws SQLException {

		String query = "SELECT CONCAT (fullpath , ' ' , node_id) AS path " + "FROM " + ontologyName
				+ "_node_backtrace_fullpath " + "WHERE node_id IN " + "(SELECT node_id FROM " + ontologyName
				+ "_node2term WHERE term_id = ?)";

		//System.out.println("QUERY: "+ query);
		Map<String, String> nameMap = new HashMap<>();
		nameMap.put("ma", "/anatomy");
		nameMap.put("mp", "/phenotypes");

		String pageBaseUrl =  nameMap.get(ontologyName);

		Set<String> pathNodes = new HashSet<>();
		Set<String> expandNodeIds = new HashSet<>();
		Map<String, List<Map<String, String>>> preOpenNodes = new HashMap<>();
		
		try (Connection conn = ontodbDataSource.getConnection(); PreparedStatement p = conn.prepareStatement(query)) {

			p.setString(1, termId);
			ResultSet resultSet = p.executeQuery();
			int topIndex = 1; // 2nd in the fullpath is the one below the real
								// root in obo

			while (resultSet.next()) {

				String fullpath = resultSet.getString("path");
				String[] nodes = fullpath.split(" ");

				pathNodes.addAll(Arrays.asList(nodes));

				String topNodeId = nodes[topIndex]; // 2nd in fullpath
				String endNodeId = nodes[nodes.length - 1]; // last in fullpath

				expandNodeIds.add(endNodeId);

				if (!preOpenNodes.containsKey(topNodeId)) {
					preOpenNodes.put(topNodeId, new ArrayList<Map<String, String>>());
				}

				Map<String, String> nodeStartEnd = new HashMap<>();
				nodeStartEnd.put(nodes[0], endNodeId);
				preOpenNodes.get(topNodeId).add(nodeStartEnd);

			}
		}

		TreeHelper th = new TreeHelper();
		th.setPathNodes(pathNodes);
		th.setExpandNodeIds(expandNodeIds);
		th.setPreOpenNodes(preOpenNodes);
		th.setPageBaseUrl(pageBaseUrl);
		th.setOntologyName(ontologyName);

		return th;

	}

	private JSONObject fetchNodeInfo(TreeHelper helper, ResultSet resultSet) throws SQLException {

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

		String url = "<a target='_blank' href='" + helper.getPageBaseUrl() + "/" + termId + "'>" + termDisplayText
				+ "</a>";
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

	}
    
	
}
