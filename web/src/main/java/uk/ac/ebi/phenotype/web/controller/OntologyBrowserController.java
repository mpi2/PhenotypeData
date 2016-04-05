/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/

package uk.ac.ebi.phenotype.web.controller;

import java.io.IOException;
import java.net.URISyntaxException;
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.solr.service.MpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import net.sf.json.JSONObject;

/**
 * Created by ckc on 21/03/2016.
 * @author ilinca //edits
 */

@Controller
public class OntologyBrowserController {

    @Resource(name = "globalConfiguration")
    private Map<String, String> config;

    @Autowired
    @Qualifier("komp2DataSource")
    DataSource komp2DataSource;
    
    
    @Autowired
    MpService ms;
    
    @RequestMapping(value = "/ontologyBrowser", method = RequestMethod.GET)
    public String getParams(

            @RequestParam(value = "termId", required = true) String termId,
            HttpServletRequest request,
            Model model)
            throws IOException, URISyntaxException, SQLException {

        model.addAttribute("termId", termId);

        return "ontologyBrowser";
    }


    @RequestMapping(value = "/ontologyBrowser2", method = RequestMethod.GET)
    public ResponseEntity<String> getTreeJson(
            @RequestParam(value = "node", required = true) String rootId,
            @RequestParam(value = "termId", required = true) String termId,
            @RequestParam(value = "expandNodeIds", required = false) List<String> expandNodes,
            HttpServletRequest request,
            Model model)
            throws IOException, URISyntaxException, SQLException, SolrServerException {

        String ontologyName = null;
        if ( termId.startsWith("MA:") ){
            ontologyName = "ma";
        } else if (termId.startsWith("MP:") ){
            ontologyName = "mp";
        }
        rootId = rootId.equals("src") ? "0" : rootId;
        if (rootId.equalsIgnoreCase("0")){
        	return new ResponseEntity<String>(ms.getSearchTermJson(termId), createResponseHeaders(), HttpStatus.CREATED);
        } else {
        	TreeHelper helper = getTreeHelper(request, ontologyName, termId, expandNodes);
            List<JSONObject> tree = createTreeJson(helper, rootId, null, termId, expandNodes);
            return new ResponseEntity<String>(tree.toString(), createResponseHeaders(), HttpStatus.CREATED);
        }
    }

    
    
    public List<JSONObject> createTreeJson(TreeHelper helper, String rootId, String childNodeId, String termId, List<String> expandNodes) 
    throws SQLException {

        List<JSONObject> tn = new ArrayList<>();
        String sql = fetchNextLevelChildrenSql(helper, rootId, childNodeId);

        try (Connection conn = komp2DataSource.getConnection(); PreparedStatement p = conn.prepareStatement(sql)) {

            ResultSet resultSet = p.executeQuery();
            
            while (resultSet.next()) {

                String nodeId = resultSet.getString("node_id");
                if ( helper.getPreOpenNodes().containsKey(nodeId) || ( expandNodes!= null && expandNodes.contains(nodeId) )){   // check if this is the node to start fetching it children recursively
                    // the tree should be expanded until the query term eg. 5267 = [{0=5344}, {0=5353}], a node could have same top node but diff. end node
                	String topNodeId = "0";
					for ( Map<String, String> topEnd : helper.getPreOpenNodes().get(nodeId) ) {
                        for (String thisTopNodeId : topEnd.keySet()) {
                            topNodeId = thisTopNodeId;
                            break;  // do only once: should have a better way
                        }
                    }
                    String thisSql = fetchNextLevelChildrenSql(helper, topNodeId, nodeId);
                    try (PreparedStatement p2 = conn.prepareStatement(thisSql)) {

                        ResultSet resultSet2 = p2.executeQuery();
                        while (resultSet2.next()) {

                            JSONObject thisNode = fetchNodeInfo(helper, resultSet2);
                            if (thisNode.getBoolean("children")) {
                                thisNode = fetchChildNodes(helper, thisNode);
                            	thisNode.accumulate("state", getState(true, false));
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
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return tn;
    }

    private JSONObject getState(boolean opened, boolean selected){
    	JSONObject state = new JSONObject();
    	state.accumulate("opened", opened);
    	state.accumulate("selected", selected);
    	return state;
    }
    

    public JSONObject fetchChildNodes(TreeHelper helper, JSONObject nodeObj) 
    throws SQLException {

    	String parentNodeId = nodeObj.getString("id");
        String childNodeId = null;
        String sql = fetchNextLevelChildrenSql(helper, parentNodeId, childNodeId);
        List<JSONObject> children = new ArrayList<>();

        try (Connection conn = komp2DataSource.getConnection(); PreparedStatement p = conn.prepareStatement(sql)) {

			ResultSet resultSet = p.executeQuery();			
			while (resultSet.next()) {
	
				if (helper.getPathNodes().contains(Integer.toString(resultSet.getInt("node_id")))) {
					JSONObject thisNode = fetchNodeInfo(helper, resultSet);
					if (thisNode.getBoolean("children")) {
						thisNode = recursiveFetchChildNodes(helper, thisNode, conn);
						thisNode.accumulate("state", getState(true, false));
					} else {
						thisNode.accumulate("state", getState(false, false));
					}
					children.add(thisNode);
				}
			}
	
			nodeObj.put("children", children);
        }

        return nodeObj;
        
    }

	public JSONObject recursiveFetchChildNodes(TreeHelper helper, JSONObject nodeObj, Connection conn) 
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
						thisNode = recursiveFetchChildNodes(helper, thisNode, conn);
						thisNode.accumulate("state", getState(true, false));
					} else {
						thisNode.accumulate("state", getState(false, false));
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

    public String fetchNextLevelChildrenSql(TreeHelper helper, String parentNodeId, String childNodeId)
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

    public TreeHelper getTreeHelper(HttpServletRequest request, String ontologyName, String termId, List<String> openNodeIds) 
    throws SQLException {

        String query = "SELECT CONCAT (fullpath , ' ' , node_id) AS path "
                + "FROM " + ontologyName + "_node_backtrace_fullpath "
                + "WHERE node_id IN "
                + "(SELECT node_id FROM " + ontologyName + "_node2term WHERE term_id = ?)";
        

        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("ma", "anatomy");
        nameMap.put("mp", "phenotypes");

        String baseUrl = request.getAttribute("baseUrl").toString();
        String pageBaseUrl = baseUrl + "/" + nameMap.get(ontologyName);

        Set<String> pathNodes = new HashSet<>();
        Set<String> expandNodeIds = new HashSet<>();
        Map<String, List<Map<String, String>>> preOpenNodes = new HashMap<>();
        if (openNodeIds != null){
	        for (String nodeId: openNodeIds){
	        	preOpenNodes.put(nodeId, new ArrayList<Map<String, String>>());
	        }
        }
        
        try (Connection conn = komp2DataSource.getConnection(); PreparedStatement p = conn.prepareStatement(query)) {

	        p.setString(1, termId);
		    ResultSet resultSet = p.executeQuery();	
		    int topIndex = 1; // 2nd in the fullpath is the one below the real root in obo
	
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

    public JSONObject fetchNodeInfo(TreeHelper helper, ResultSet resultSet) 
    throws SQLException {

        JSONObject node = new JSONObject();
        String nodeId = Integer.toString(resultSet.getInt("node_id"));
        String termId = resultSet.getString("term_id");
        String name = resultSet.getString("name");
        String termDisplayText = null;
        
        if ( helper.getExpandNodeIds().contains(nodeId) ){
            termDisplayText = "<span class='qryTerm'>" + name + "</span>";
        } else {
            termDisplayText = name;
        }

        String url = "<a target='_blank' href='" + helper.getPageBaseUrl() + "/" + termId + "'>" + termDisplayText + "</a>";
        node.put("text", url);
        node.put("id", Integer.toString(resultSet.getInt("node_id")));
        node.put("term_id", resultSet.getString("term_id"));
        node.put("children", resultSet.getString("node_type").equals("folder") ? true : false);
        node.put("href", helper.getPageBaseUrl() + "/" + termId);
        node.put("hrefTarget", "_blank");
        
        return node;
    }

    private class TreeHelper {

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


    private HttpHeaders createResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return responseHeaders;
    }
}
