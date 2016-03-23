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

import net.sf.json.JSONObject;
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by ckc on 21/03/2016.
 */

@Controller
public class OntologyBrowserController {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getCanonicalName());

    @Resource(name = "globalConfiguration")
    private Map<String, String> config;

    @Autowired
    @Qualifier("komp2DataSource")
    DataSource komp2DataSource;


    @RequestMapping(value = "/ontologyBrowser", method = RequestMethod.GET)
    public String getParams(

            @RequestParam(value = "termId", required = true) String termId,
            HttpServletRequest request,
            Model model)
            throws IOException, URISyntaxException, SQLException {

        model.addAttribute("termId", termId);

        return "ontologyBrowser";
    }


    //@ResponseBody
    @RequestMapping(value = "/ontologyBrowser2", method = RequestMethod.POST)
    public ResponseEntity<String> getTreeJson(
            @RequestParam(value = "node", required = true) String rootId,
            @RequestParam(value = "termId", required = true) String termId,
            HttpServletRequest request,
            Model model)
            throws IOException, URISyntaxException, SQLException {

        String ontologyName = null;
        if ( termId.startsWith("MA:") ){
            ontologyName = "ma";
        }
        else if (termId.startsWith("MP:") ){
            ontologyName = "mp";
        }

        TreeHelper helper = getTreeHelper(request, ontologyName, termId);

        List<JSONObject> tree = createTreeJson(helper, rootId, null, termId);

        return new ResponseEntity<String>(tree.toString(), createResponseHeaders(), HttpStatus.CREATED);
    }

    public List<JSONObject> createTreeJson(TreeHelper helper, String rootId, String childNodeId, String termId) throws SQLException {

        //List<TreeNodeObject> tn = new ArrayList<>();
        List<JSONObject> tn = new ArrayList<>();

        String sql = fetchNextLevelChildrenSql(helper, rootId, childNodeId);

        //System.out.println(">>>>>> " + sql);

        try (Connection conn = komp2DataSource.getConnection(); PreparedStatement p = conn.prepareStatement(sql)) {

            ResultSet resultSet = p.executeQuery();

            List<String> endNodes = new ArrayList<>();

            while (resultSet.next()) {

                String nodeId = resultSet.getString("node_id");

                System.out.println("ROOT node id now: " + nodeId);
                System.out.println("PATH NODES: " + helper.getPathNodes());
                if ( helper.getPreOpenNodes().containsKey(nodeId) ){
                    // check if this is the node to start fetching it children recursively
                    // the tree should be expanded until the query term
                    // eg. 5267 = [{0=5344}, {0=5353}], a node could have same top node but diff. end node
                    String topNodeId = null;
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

                            //String thisNodeId = resultSet2.getString("node_id");
                            //TreeNodeObject thisNode = fetchNodeInfo(resultSet2);
                            JSONObject thisNode = fetchNodeInfo(helper, resultSet2);

                            //System.out.println("2nd Level node id now: " + thisNode.getId());
                            System.out.println("2nd Level node id now: " + thisNode.getString("id"));

                            //if (!thisNode.isLeaf()) {
                            if (!thisNode.getBoolean("leaf")) {
                                thisNode = fetchChildNodes(helper, thisNode);
                            }
                            //echo print_r($thisTerm);

                            tn.add(thisNode);
                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
                else {
                    // just fetch the term of this node
                    //TreeNodeObject thisNode = fetchNodeInfo(resultSet);
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


    //public TreeNodeObject fetchChildNodes(TreeNodeObject nodeObj) throws SQLException {
    public JSONObject fetchChildNodes(TreeHelper helper, JSONObject nodeObj) throws SQLException {
        //String parentNodeId = nodeObj.getId();
        String parentNodeId = nodeObj.getString("id");
        String childNodeId = null;

        String sql = fetchNextLevelChildrenSql(helper, parentNodeId, childNodeId);

        //List<TreeNodeObject> children = new ArrayList<>();
        List<JSONObject> children = new ArrayList<>();

        try (Connection conn = komp2DataSource.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {

            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {

                //TreeNodeObject thisNode = fetchNodeInfo(resultSet);

                if ( helper.getPathNodes().contains(Integer.toString(resultSet.getInt("node_id")))) {

                    JSONObject thisNode = fetchNodeInfo(helper, resultSet);

                    //System.out.println("3rd Level node id now: " + thisNode.getId());
                    System.out.println("3rd Level node id now: " + thisNode.getString("id"));

                    //if ( ! thisNode.isLeaf() ){
                    if (!thisNode.getBoolean("leaf")) {
                        thisNode = recursiveFetchChildNodes(helper, thisNode);
                        //thisNode = fetchChildNodes(thisNode);
                    }

                    children.add(thisNode);
                    //nodeObj.setChildren(children);
                    nodeObj.put("children", children);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return nodeObj;
    }

    public JSONObject recursiveFetchChildNodes(TreeHelper helper, JSONObject nodeObj) throws SQLException {
        //String parentNodeId = nodeObj.getId();
        String parentNodeId = nodeObj.getString("id");
        String childNodeId = null;

        String sql = fetchNextLevelChildrenSql(helper, parentNodeId, childNodeId);

        //List<TreeNodeObject> children = new ArrayList<>();
        List<JSONObject> children = new ArrayList<>();

        try (Connection conn = komp2DataSource.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {

            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {

                if ( helper.getPathNodes().contains(Integer.toString(resultSet.getInt("node_id")))) {
                    //TreeNodeObject thisNode = fetchNodeInfo(resultSet);
                    JSONObject thisNode = fetchNodeInfo(helper, resultSet);

                    //System.out.println("4th Level node id now: " + thisNode.getId());
                    System.out.println("4th Level node id now: " + thisNode.getString("id"));

                    //if ( ! thisNode.isLeaf() ){
                    if (!thisNode.getBoolean("leaf")) {
                        thisNode = recursiveFetchChildNodes(helper, thisNode);
                        //thisNode = fetchChildNodes(thisNode);
                    }

                    children.add(thisNode);
                    //nodeObj.setChildren(children);
                    nodeObj.put("children", children);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return nodeObj;
    }

//   public TreeNodeObject recursiveFetchChildNodes(TreeNodeObject nodeObj) throws SQLException {
//
//        String parentNodeId = nodeObj.getId();
//        String childNodeId = null;
//
//        String sql = fetchNextLevelChildrenSql(parentNodeId, childNodeId);
//
//        List<TreeNodeObject> children = new ArrayList<>();
//
//        try (PreparedStatement p = conn.prepareStatement(sql)) {
//
//            ResultSet resultSet = p.executeQuery();
//            while (resultSet.next()) {
//
//                TreeNodeObject thisNode = fetchNodeInfo(resultSet);
//
//                System.out.println("4th Level node id now: " + thisNode.getId());
//                if ( ! thisNode.isLeaf() ){
//                    thisNode = recursiveFetchChildNodes(thisNode);
//                }
//
//                children.add(thisNode);
//                nodeObj.setChildren(children);
//            }
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//
//        return nodeObj;
//    }

    public String fetchNextLevelChildrenSql(TreeHelper helper, String parentNodeId, String childNodeId) throws SQLException {

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


    public TreeHelper getTreeHelper(HttpServletRequest request, String ontologyName, String termId) throws SQLException {

        String query = "SELECT CONCAT (fullpath , ' ' , node_id) AS path "
                + "FROM " + ontologyName + "_node_backtrace_fullpath "
                + "WHERE node_id IN "
                + "(SELECT node_id FROM " + ontologyName + "_node2term WHERE term_id = ?)";

//        System.out.println("*****QUERY: "+ query);
//        System.out.println("*****TERM: "+ termId);

        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("ma", "anatomy");
        nameMap.put("mp", "phenotypes");

        String baseUrl = request.getAttribute("baseUrl").toString();
        String pageBaseUrl = baseUrl + "/" + nameMap.get(ontologyName);

        Set<String> pathNodes = new HashSet<>();
        Set<String> expandNodeIds = new HashSet<>();
        Map<String, List<Map<String, String>>> preOpenNodes = new HashMap<>();

        try ( Connection conn = komp2DataSource.getConnection();

            PreparedStatement p = conn.prepareStatement(query)) {

            p.setString(1, termId);

            ResultSet resultSet = p.executeQuery();

            int topIndex = 1; // 2nd in the fullpath is the one below the real root in obo

            while (resultSet.next()) {
                String fullpath = resultSet.getString("path");
                System.out.println("PATH: " + fullpath);
                String[] nodes = fullpath.split(" ");

                pathNodes.addAll(Arrays.asList(nodes));

                String topNodeId = nodes[topIndex]; // 2nd in fullpath
                String endNodeId = nodes[nodes.length - 1]; // last in fullpath

                expandNodeIds.add(endNodeId);

                if ( ! preOpenNodes.containsKey(topNodeId) ) {
                    preOpenNodes.put(topNodeId, new ArrayList<Map<String, String>>());
                }

                Map<String, String> nodeStartEnd = new HashMap<>();
                nodeStartEnd.put(nodes[0], endNodeId);
                preOpenNodes.get(topNodeId).add(nodeStartEnd);

            }
        } catch (Exception e) {
        }

//        Iterator it = preOpenNodes.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry)it.next();
//            System.out.println(pair.getKey() + " = " + pair.getValue());
//            it.remove(); // avoids a ConcurrentModificationException
//        }

        TreeHelper th = new TreeHelper();
        th.setPathNodes(pathNodes);
        th.setExpandNodeIds(expandNodeIds);
        th.setPreOpenNodes(preOpenNodes);
        th.setPageBaseUrl(pageBaseUrl);
        th.setOntologyName(ontologyName);

        return th;

    }

    //public TreeNodeObject fetchNodeInfo(ResultSet resultSet) throws SQLException {
    public JSONObject fetchNodeInfo(TreeHelper helper, ResultSet resultSet) throws SQLException {

        JSONObject node = new JSONObject();
        String nodeId = Integer.toString(resultSet.getInt("node_id"));
        String termId = resultSet.getString("term_id");

//        TreeNodeObject thisNode = new TreeNodeObject();
//        thisNode.setId(Integer.toString(resultSet.getInt("node_id")));
//        thisNode.setLeaf(resultSet.getString("node_type").equals("folder") ? false : true);
//        thisNode.setTermId(resultSet.getString("term_id"));

        String name = resultSet.getString("name");
        String termDisplayText = null;
        //if ( expandNodeIds.contains(thisNode.getId()) ){
        if ( helper.getExpandNodeIds().contains(nodeId) ){
            termDisplayText = "<span class='qryTerm'>" + name + "</span>";
        }
        else {
            termDisplayText = name;
        }

        String url = "<a target='_blank' href='" + helper.getPageBaseUrl() + "/" + termId + "'>" + termDisplayText + "</a>";
        //thisNode.setText(url);
        node.put("text", url);

        node.put("qtip", nodeId + "- " + termId);

        node.put("id", Integer.toString(resultSet.getInt("node_id")));
        node.put("term_id", resultSet.getString("term_id"));

        node.put("expandNodeIds", helper.getExpandNodeIds());

        node.put("text", url);
        node.put("leaf", resultSet.getString("node_type").equals("folder") ? false : true);
        //node.put("children", n.getChildren());


        //return thisNode;
        return node;
    }

    private class TreeHelper {

        Set<String> pathNodes;
        Map<String, List<Map<String, String>>> preOpenNodes;
        Set<String> expandNodeIds;
        String baseUrl;
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

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
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


        private class TreeNodeObject {

        String id;
        String term_id;
        List<String> expandNodeIds;
        List<TreeNodeObject> children;
        String text;
        Boolean leaf;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTermId() {
            return term_id;
        }

        public void setTermId(String term_id) {
            this.term_id = term_id;
        }

        public List<String> getExpandNodeIds() {
            return expandNodeIds;
        }

        public void setExpandNodeIds(List<String> expandNodeIds) {
            this.expandNodeIds = expandNodeIds;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Boolean isLeaf() {
            return leaf;
        }

        public void setLeaf(Boolean leaf) {
            this.leaf = leaf;
        }

        public List<TreeNodeObject> getChildren() {
            return children;
        }

        public void setChildren(List<TreeNodeObject> children) {
            this.children = children;
        }

        @Override
        public String toString() {
            return "TreeNodeObject{" +
                    "id='" + id + '\'' +
                    ", term_id='" + term_id + '\'' +
                    ", expandNodeIds=" + expandNodeIds +
                    ", children=" + children +
                    ", text='" + text + '\'' +
                    ", leaf=" + leaf +
                    '}';
        }
    }

    private HttpHeaders createResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return responseHeaders;
    }
}
