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

package org.mousephenotype.cda.db.dao;

import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.db.beans.OntologyTermBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * This class encapsulates the code and data necessary to serve a EMAP ontology
 *
 *
 * @author ckchen based on mrelac
 */

@Repository
public class EmapaOntologyDAO extends OntologyDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Map<String, String> subsets = new HashMap();
    private Map<String, List<Integer>> term2NodesMap = null;
    private boolean showAncestorMapWarnings = false;
    private boolean hasAncestorMapWarnings = false;

    public EmapaOntologyDAO() {

    }

    /**
     * Returns the set of descendent graphs for the given id.
     *
     * @param id the emapa id to query
     *
     * @return the set of descendent graphs for the given id.
     */
    @Override
    public final List<List<String>> getDescendentGraphs(String id) {
        String nodeIds = StringUtils.join(id2nodesMap.get(id), ",");
        String query =
                "SELECT *\n"
                        + "FROM emapa_node_subsumption_fullpath_concat\n"
                        + "WHERE node_id IN (" + nodeIds + ")\n";

        return getDescendentGraphsInternal(query);
    }

    /**
     * Methods annotated with @PostConstruct are executed just after the constructor
     * is run and spring is initialised.
     *
     * @throws RuntimeException PostConstruct forbids throwing checked exceptions,
     * so SQLException is re-mapped to a RuntimeException if a failure occurs.
     */
    @Override
    @PostConstruct
    public void initialize() throws RuntimeException {
        super.initialize();

        try {
            populateSubsets();
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    public List<Integer> getNodeIds(String emapaTermId){
        return term2NodesMap.get(emapaTermId);
    }

    public boolean hasAncestorMapWarnings() {
        return hasAncestorMapWarnings;
    }

    public boolean getShowAncestorMapWarnings() {
        return showAncestorMapWarnings;
    }

    public void setShowAncestorMapWarnings(boolean showAncestorMapWarnings) {
        this.showAncestorMapWarnings = showAncestorMapWarnings;
    }

    /**
     * Returns a list of ma subsets, indexed by ma id.
     *
     * @return a list of ma subsets, indexed by ma id.
     */
    public Map<String, String> getSubsets() {
        return subsets;
    }

    /**
     * Returns the emapa subset for the given id.
     *
     * @param id the emapa id to query
     *
     * @return the emapa subset for the given id.
     */
    public String getSubset(String id) {
        return subsets.get(id);
    }

    /**
     * Returns the emapa subset for the given id in the provided list.
     *
     * @param id the emapa id to query
     *
     * @param list the list to which the subset is appended.
     *
     * @return the emapa subset for the given id.
     */
    public List<String> getSubset(String id, List<String> list) {
        list.add(subsets.get(id));

        return list;
    }


    // PROTECTED METHODS


    /**
     * Populate all terms, keyed by id.
     *
     * Side Effects: this method populates a map, indexed by id, of each id's 
     *               node ids, which is later used to create the ancestor list.
     *
     * @throws SQLException
     */
    @Override
    protected final void populateAllTerms() throws SQLException {
        String query =
                "SELECT\n"
                        + "  n2t.term_id               AS termId\n"
                        + ", GROUP_CONCAT(n2t.node_id) AS nodes\n"
                        + ", ti.name                   AS termName \n"
                        + ", ti.definition             AS termDefinition\n"
                        + ", GROUP_CONCAT(DISTINCT alt.alt_id) AS alt_ids\n"
                        + "FROM emapa_node2term n2t\n"
                        + "LEFT OUTER JOIN emapa_term_infos ti ON ti.term_id = n2t.term_id\n"
                        + "LEFT OUTER JOIN emapa_alt_ids alt ON ti.term_id = alt.term_id\n"
                        + "WHERE (n2t.term_id != 'EMAPA:0' AND n2t.term_id not like 'TS%')\n"
                        + "GROUP BY n2t.term_id\n"
                        + "ORDER BY n2t.term_id, n2t.node_id\n";

        populateAllTerms(query);
    }

    /**
     * Populates each node's ancestor hash.
     *
     * @throws SQLException
     */
    @Override
    protected void populateAncestorMap() throws SQLException {

        // Populate backtraceMap so the fullpath can be modified to contain the selected top-level terms.
        String query = "SELECT * FROM emapa_node_backtrace_fullpath\n";

        Map<Integer, String> backtraceMap = new HashMap();  // key = nodeId. value = fullpath.

        try (final PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Integer nodeId = resultSet.getInt("node_id");
                String fullpath = resultSet.getString("fullpath");
                //fullpath += " " + nodeId;            // append node_id to fullpath:   e.g. for node_id 1, fullpath "0" -> fullpath "0 1".

                backtraceMap.put(nodeId, fullpath);

                // get a full ancestor map
                ancestorMap.put(nodeId, fullpath);
            }

            ps.close();
        }

        // Dump Terry's selected top-levels into a map keyed by node.
        query = "SELECT \n"
              + "  node_id\n"
              + ", top_level_node_id\n"
              + "FROM emapa_node_2_selected_top_level_mapping\n";

        HashMap<Integer, Integer> selectedTopLevelNodeMap = new HashMap();

        try (final PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {

                int node = resultSet.getInt("node_id");
                int selectedTopLevelNodeId = resultSet.getInt("top_level_node_id");

                if (selectedTopLevelNodeId == 0) {
                    hasAncestorMapWarnings = true;
                    logger.warn("node " + node + " in emapa_node_2_selected_top_level_mapping has no top-level node.");
                    continue;
                }
                selectedTopLevelNodeMap.put(node, selectedTopLevelNodeId);
            }

            ps.close();
        }

        populateTerm2NodesMap();

        // For each term in node2term:
        // - For each of that term's nodes:
        //   - Look up the node in the selectedTopLevelNodeMap.
        //   - If found
        //     - get the node's selectedTopLevelNodeId.
        //     - Search the fullpath for selectedTopLevelNodeId.
        //     - If found
        //       - set selectedTopLevelFound = true.
        //       - remove the nodes to the left of the selectedTopLevelNodeId.
        //     - else
        //       - Log a warning that this node has no fullpath entry.
        //       - Add self to ancestorMap.
        // - if ! selectedTopLevelFound
        //   - Do any child nodes have a selected top-level term?
        //     - Yes: Ok to ignore.
        //     - No:  Log a warning that this term has no selected top level. Terry needs to choose one.

        Map<String, String> missingFromFullpathMap = new HashMap();
        Map<String, List<Integer>> missingFromFullPathNodesList = new HashMap();
        Map<String, String> needsReviewByTerryCountMap = new HashMap();
        Map<String, List<Integer>> needsReviewByTerryCountNodesMap = new HashMap();

        for (OntologyTermBean otBean : allTermsMap.values()) {
            String termId = otBean.getId();

            boolean selectedTopLevelFound = false;
            for (Integer nodeId : term2NodesMap.get(termId)) {
                String[] fullpath = backtraceMap.get(nodeId).split(" ");

                List<Integer> fullpathNodeList = new ArrayList();
                for (String sNode : fullpath) {
                    int node = Integer.parseInt(sNode);
                    if (node > 0 ) {
                        fullpathNodeList.add(node);
                    }
                }

                if (selectedTopLevelNodeMap.containsKey(nodeId)) {
                    // get partial ancestor map with the top level being the selected_top_level

                    selectedTopLevelFound = true;

                    int newSelectedTopLevelNode = selectedTopLevelNodeMap.get(nodeId);
                    int newTopNodeIndex = fullpathNodeList.indexOf(newSelectedTopLevelNode);
                    if (newTopNodeIndex >= 0) {

                        // New top level was found in fullpath (i.e. entry.getValue()).
                        fullpathNodeList.subList(0, newTopNodeIndex).clear();
                        String newFullpath = "";

                        for (Integer node : fullpathNodeList) {
                            if ( ! newFullpath.isEmpty())
                                newFullpath += " ";
                            newFullpath += Integer.toString(node);
                        }

                        selectedAncestorMap.put(nodeId, newFullpath);

                    } else {

                        String ancestorTermId = node2termMap.get(Integer.toString(nodeId));


                        OntologyTermBean ot = allTermsMap.get(ancestorTermId);
                        String ancestorTerm = (ot != null ? ot.getName() : "<unknown>");
                        if ( ! missingFromFullpathMap.containsKey(ancestorTermId)) {
                            missingFromFullpathMap.put(ancestorTermId, ancestorTerm);
                        }
                        if ( ! missingFromFullPathNodesList.containsKey(ancestorTermId)) {
                            missingFromFullPathNodesList.put(ancestorTermId, new ArrayList<Integer>());
                        }
                        List<Integer> nodes = missingFromFullPathNodesList.get(ancestorTermId);
                        nodes.add(nodeId);
                        missingFromFullPathNodesList.put(ancestorTermId, nodes);

                        // New top level not found in fullpath. Replace fullpath with ancestor node.
                        selectedAncestorMap.put(nodeId, Integer.toString(nodeId));
                    }
                }

                if ( ! selectedTopLevelFound) {

                    if (getTopLevelTermIdCount(termId) <= 0) {
                        String ancestorTermId = node2termMap.get(Integer.toString(nodeId));
                        OntologyTermBean ot = allTermsMap.get(ancestorTermId);
                        String ancestorTerm = (ot != null ? ot.getName() : "<unknown>");
                        if ( ! needsReviewByTerryCountMap.containsKey(ancestorTermId)) {
                            needsReviewByTerryCountMap.put(ancestorTermId, ancestorTerm);
                        }
                        if ( ! needsReviewByTerryCountNodesMap.containsKey(ancestorTermId)) {
                            needsReviewByTerryCountNodesMap.put(ancestorTermId, new ArrayList<Integer>());
                        }
                        List<Integer> nodes = needsReviewByTerryCountNodesMap.get(ancestorTermId);
                        nodes.add(nodeId);
                        needsReviewByTerryCountNodesMap.put(ancestorTermId, nodes);
                    }
                }
            }
        }

        if (showAncestorMapWarnings) {
            List<String> ancestorTermIdList = Arrays.asList(missingFromFullpathMap.keySet().toArray(new String[0]));
            Collections.sort(ancestorTermIdList);
            System.out.println("missingFromFullpath: " + ancestorTermIdList.size());
            for (String ancestorTermId : ancestorTermIdList) {
                List<Integer> ancestorNodeIdList = missingFromFullPathNodesList.get(ancestorTermId);
                Collections.sort(ancestorNodeIdList);
                String nodes = StringUtils.join(ancestorNodeIdList, ",");
                String ancestorTerm = missingFromFullpathMap.get(ancestorTermId);
                String s = ancestorTermId + " ('" + ancestorTerm + "', nodes " + nodes + ") is in topLevelNodeMap but not in the fullpath.";

                hasAncestorMapWarnings = true;
                logger.warn(s);
            }

            System.out.println();

            ancestorTermIdList = Arrays.asList(needsReviewByTerryCountNodesMap.keySet().toArray(new String[0]));
            Collections.sort(ancestorTermIdList);
            System.out.println("needsReviewByTerryCount: " + ancestorTermIdList.size());
            for (String ancestorTermId : ancestorTermIdList) {
                List<Integer> ancestorNodeIdList = needsReviewByTerryCountNodesMap.get(ancestorTermId);
                Collections.sort(ancestorNodeIdList);
                String nodes = StringUtils.join(ancestorNodeIdList, ",");
                String ancestorTerm = needsReviewByTerryCountMap.get(ancestorTermId);
                String s = ancestorTermId + " ('" + ancestorTerm + "', nodes " + nodes + ") needs to be reviewed by Terry for a selected top-level term.";

                hasAncestorMapWarnings = true;
                logger.warn(s);
            }
        }

    }

    /**
     * Executes a query that returns the count of all of <code>nodeId</code>'s
     * child nodes that have a selected top-level term.
     *
     * @param termId the term whose children are queried for selected top-level
     *               term count
     *
     * @return the count of selected top-level terms for all of <code>nodeId
     * </code>'s child nodes.
     */
    private int getTopLevelTermIdCount(String termId) {
        int retVal = 0;

        String query =
                "SELECT COUNT(DISTINCT top_level_term_id) AS topLevelTermIdCount\n"
                        + "FROM emapa_node_2_selected_top_level_mapping stlm\n"
                        + "WHERE node_id IN (\n"
                        + "        SELECT DISTINCT child_node_id FROM emapa_node_subsumption_fullpath sf\n"
                        + "    WHERE sf.node_id IN (\n"
                        + "        SELECT node_id FROM emapa_node2term WHERE term_id = ?\n"
                        + "    )\n"
                        + ")\n";

        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, termId);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                retVal = resultSet.getInt("topLevelTermIdCount");
            }

            ps.close();
        } catch (SQLException sqle) {
            String errorMessage = "";
            Iterator it = sqle.iterator();
            while (it.hasNext()) {
                SQLException ex = (SQLException)it.next();
                if ((ex != null) && (ex.getLocalizedMessage() != null)) {
                    if ( ! errorMessage.isEmpty())
                        errorMessage += "\n";
                    errorMessage += ex.getLocalizedMessage();
                }
            }

            logger.error("SQLException: " + errorMessage);
        }

        return retVal;
    }

    /**
     * Populates the node2term hash with the term matching each node.
     *
     * @throws SQLException
     */
    @Override
    protected void populateNode2TermMap() throws SQLException {
        String query =
                "SELECT *\n"
                        + "FROM emapa_node2term\n"
                        + "ORDER BY term_id\n";

        populateNode2TermMap(query);
    }

    protected void populateTerm2NodesMap() throws SQLException {
        if (term2NodesMap == null) {
            term2NodesMap = new HashMap();

            String query =
                    "SELECT\n"
                            + "  term_id\n"
                            + ", GROUP_CONCAT(node_id) AS nodeIdList\n"
                            + "FROM emapa_node2term\n"
                            //+ "WHERE term_id != 'EMAPA:0'\n"
                            + "GROUP BY term_id\n"
                            + "ORDER BY term_id\n";

            //System.out.println("TERM2NODE QUERY: "+query);
            try (final PreparedStatement ps = connection.prepareStatement(query)) {
                ResultSet resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    String[] nodeIdArray = resultSet.getString("nodeIdList").split(",");
                    List<Integer> nodeIdList = new ArrayList();
                    for (String nodeId : nodeIdArray) {
                        nodeIdList.add(Integer.parseInt(nodeId));
                    }

                    term2NodesMap.put(resultSet.getString("term_id"), nodeIdList);
                }

                ps.close();
            }
        }
    }

    /**
     * Query the database, returning a map of all synonyms indexed by term id
     *
     * @throws SQLException when a database exception occurs
     */
    @Override
    protected final void populateSynonyms() throws SQLException {
        String query =
                "SELECT\n"
                        + "  term_id\n"
                        + ", syn_name\n"
                        + "FROM emapa_synonyms";

        populateSynonyms(query);
    }


    // PRIVATE METHODS


    /**
     * Query the database, returning a map of all ma subsets, indexed by term id.
     *
     * @throws SQLException when a database exception occurs
     */
    private void populateSubsets() throws SQLException {
        String query =
                "SELECT DISTINCT *\n"
                        + "FROM emapa_term_subsets\n"
                        + "ORDER BY term_id\n";

        try (final PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                subsets.put(resultSet.getString("term_id"), resultSet.getString("subset"));
            }

            ps.close();
        }
    }
}