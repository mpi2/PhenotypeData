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

import org.mousephenotype.cda.annotations.ComponentScanNonParticipant;
import org.mousephenotype.cda.db.beans.OntologyTermBean;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;


/**
 * Encapsulates the code and data necessary to represent an ontology service,
 * such as maintaining an ancestor map for each ontology flavour, used to serve
 * up hierarchical ontology queries such as getTopLevel, getParent,
 * getAncestors, getChildren, getDescendents, etc.
 *
 * This class is intended to be Spring-managed and to have a single shared
 * instance.
 *
 * <pre>
 * Definitions:
 *      leaf            - node with no children
 *      topLevel        - List of top-level ids, exclusive of self.
 *      ancestors       - List of all ids between the top and self, inclusive of
 *                        top but exclusive of self.
 *      parents         - List of all ids immediately above self.
 *      intermediates   - List of all ids between the top and self, exclusive of
 *                        top and exclusive of self.
 *      children        - List of all ids immediately below self.
 *      descendents     - List of all ids between self and leaf, exclusive of
 *                        self.
 * </pre>
 *
 * @authors mrelac, ckchen
 */
@ComponentScanNonParticipant
public abstract class OntologyDAO {

    protected Map<String, OntologyTermBean>   allTermsMap = null;

    protected final HashMap<Integer, String>  ancestorMap = new HashMap<>();
    protected final HashMap<Integer, String>  selectedAncestorMap = new HashMap<>();
    protected Map<String, List<List<String>>> ancestorGraphsMap = null;
    protected Map<String, List<List<String>>> selectedAncestorGraphsMap = null;

    protected CommonUtils                     commonUtils = new CommonUtils();
    protected Connection                      connection;
    protected final Map<String, List<String>> id2nodesMap = new HashMap<>();
    protected final HashMap<String, String>   node2termMap = new HashMap<>();
    protected Map<String, List<String>>       synonymsMap = null;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final int MAX_ROWS = 1000000;
    public static final int BATCH_SIZE = 2000;
    // number of levels to use when selecting top levels
    public static final int ANATOMY_LEVELS = 2;
    public static final int PHENOTYPE_LEVELS = 1;

    @Autowired
    @Qualifier("ontodbDataSource")
    DataSource ontodbDataSource;


    public OntologyDAO() {

    }

    /**
     * Returns this term's selected-top-level terms.
     *
     * @return this term's selected-top-level terms.
     */
    public OntologyDetail getSelectedTopLevelDetails(String id) {
        int level = 1;
        List<OntologyTermBean> beans = getSelectedTopLevel(id, level);
        OntologyDetail detail = new OntologyDetail(beans);

        return detail;
    }

    /**
     * Returns this term's top-level terms.
     *
     * @return this term's top-level terms.
     */
    public OntologyDetail getTopLevel(Integer level, String id) {
        List<OntologyTermBean> beans = getTopLevel(id, level);
        OntologyDetail detail = new OntologyDetail(beans);

        return detail;
    }

    /**
     * Returns this term's top-level terms.
     *
     * @return this term's top-level terms.
     */
    public OntologyDetail getTopLevelDetail(String id) {
        List<OntologyTermBean> beans = getTopLevel(id);
        OntologyDetail detail = new OntologyDetail(beans);

        return detail;
    }

    /**
     * Returns this term's top-level terms at level <code>level</code>.
     *
     * @param level the 1-relative level below the top level (i.e. 1 = top
     * level, 2 = top-level - 1, etc.)
     *
     * @return this term's top-level terms at level <code>level</code>
     */
    public OntologyDetail getTopLevels(int level, String id) {
        List<OntologyTermBean> beans = getTopLevel(id, level);
        OntologyDetail detail = new OntologyDetail(beans);

        return detail;
    }

    /**
     * Returns this term's ancestors.
     *
     * @return this term's ancestors.
     */
    public OntologyDetail getAncestorsDetail(String id) {
        List<OntologyTermBean> beans = getAncestors(id);
        OntologyDetail detail = new OntologyDetail(beans);

        return detail;
    }

    /**
     * Returns this term's parents.
     *
     * @return this term's parents.
     */
    public OntologyDetail getParentsDetails(String id) {
        List<OntologyTermBean> beans = getParents(id);
        OntologyDetail detail = new OntologyDetail(beans);

        return detail;
    }

    /**
     * Returns this term's intermediates.
     *
     * @return this term's intermediates.
     */
    public OntologyDetail getIntermediatesDetail(String id) {
        List<OntologyTermBean> beans = getIntermediates(id);
        OntologyDetail detail = new OntologyDetail(beans);

        return detail;
    }

    /**
     * Returns this term's children.
     *
     * @return this term's children.
     */
    public OntologyDetail getChildrenDetails(String id) {
        List<OntologyTermBean> beans = getChildren(id);
        OntologyDetail detail = new OntologyDetail(beans);

        return detail;
    }

    /**
     * Returns this term's descendents.
     *
     * @return this term's descendents.
     */
    public OntologyDetail getDescendentsDetails(String id) {
        List<OntologyTermBean> beans = getDescendents(id);
        OntologyDetail detail = new OntologyDetail(beans);

        return detail;
    }

    /**
     * Returns this term's descendents at level <code>level</code>.
     *
     * @param level the 1-relative level below this term (i.e. 1 = descendent-
     * level 1, 2 = descendent-level - 1, etc.)
     *
     * @return this term's descendents at level <code>level</code>
     */
    public OntologyDetail getDescendents(int level, String id) {
        List<OntologyTermBean> beans = getDescendents(id, level);
        OntologyDetail detail = new OntologyDetail(beans);

        return detail;
    }


    /**
     * Returns the <code>OntologyTermBean</code> matching <code>id</code>, if
     * found; null otherwise.
     *
     * @param id the id to query
     *
     * @return the <code>OntologyTermBean</code> matching <code>id</code>, if
     * found; null otherwise.
     */
    public OntologyTermBean getTerm(String id) {
        if (allTermsMap.containsKey(id)) {
            return allTermsMap.get(id);
        }

        return null;
    }


    protected List<List<String>> getAncestorGraphs(String id) {
        return ancestorGraphsMap.get(id);
    }

    protected abstract List<List<String>> getDescendentGraphs(String id);

    protected abstract void populateAllTerms()      throws SQLException;
    protected abstract void populateAncestorMap()   throws SQLException;
    protected abstract void populateNode2TermMap()  throws SQLException;
    protected abstract void populateSynonyms()      throws SQLException;
    public abstract List<String>  getAnatomyMappings(String mpId);

    /**
     * Methods annotated with @PostConstruct are executed just after the constructor
     * is run and spring is initialised.
     *
     * @throws RuntimeException - PostConstruct forbids throwing checked exceptions,
     * so SQLException is re-mapped to a RuntimeException if a failure occurs.
     */
    @PostConstruct
    public void initialize() throws RuntimeException {
        try {
            connection = ontodbDataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        try {
            populateSynonyms();                                                 // This must come first, as other methods depend on it.
            populateAllTerms();                                                 // This must come before populating the ancestor map.

            OntologyDAO.this.populateNode2TermMap();
            OntologyDAO.this.populateAncestorMap();
            populateAncestorGraph();

        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    public List<OntologyTermBean> getAllTerms() {
        return new ArrayList<>(allTermsMap.values());
    }


    public List<String> getAltTermIds(String mpId) {
        OntologyTermBean ontologyTermBean = allTermsMap.get(mpId);
        //System.out.println("BEAN: " + ontologyTermBean.toString());
        if ( ontologyTermBean.getAltIds() != null ) {
            return ontologyTermBean.getAltIds();
        }
        return null;
    }


    /**
     * Dumps out the <code>OntologyTermBean</code> map, prepending the <code>
     * what</code> string for map identification.
     * @param map the map to dump
     * @param what a string identifying the map, prepended to the output.
     */
    public void dumpOntologyTermMap(Map<String, List<OntologyTermBean>> map, String what) {
        List<OntologyTermRecord> termList = new ArrayList<>();
        Iterator<Entry<String, List<OntologyTermBean>>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, List<OntologyTermBean>> entry = it.next();
            OntologyTermRecord rec = new OntologyTermRecord(entry);
            termList.add(rec);
        }

        Collections.sort(termList, new OntologyTermRecordComparator());

        System.out.println(what);
        System.out.format("%10.10s\t%10.10s\t%s\t%s\n", "KEY", "TERM_ID", "TERM_NAME", "[SYNONYMS]");
        for (OntologyTermRecord record : termList) {
            int recordIndex = 0;

            for (OntologyTermBean bean : record.value) {
                System.out.format("%10.10s\t%10.10s\t", recordIndex == 0 ? record.key : "", bean.getId());
                System.out.print(bean.getName() + "\t");
                List<String> synonyms = synonymsMap.get(bean.getId());
                int synonymCount = 0;
                for (String synonym : synonyms) {
                    if (synonymCount == 0) {
                        System.out.print("[");
                    } else {
                        System.out.print(", ");
                    }
                    synonymCount++;
                    System.out.print(synonym);
                    if (synonymCount == synonyms.size()) {
                        System.out.print("]");
                    }
                }
                recordIndex++;

                System.out.println();
            }

            System.out.println();
        }
    }

    /**
     * Returns a <code>List&lt;String&gt;</code> of <code>id</code>'s synonyms,
     * or an empty list if there are none.
     *
     * @param id id of synonyms to return.
     *
     * @return a <code>List&lt;String&gt;</code> of <code>id</code>'s synonyms,
     * or an empty list if there are none.
     */
    public List<String> getSynonyms(String id) {
        return (synonymsMap.containsKey(id) ? synonymsMap.get(id) : new ArrayList<String>());
    }

    /**
     * Returns a <code>List&lt;OntologyTerm&gt;</code> of <code>id</code>'s
     * top-level terms, exclusive of self, or an empty list if there are none.
     *
     * @param id id of top-level terms to return.
     *
     * @return a <code>List&lt;OntologyTerm&gt;</code> of <code>id</code>'s
     * top-level terms, exclusive of self, or an empty list if there are none.
     */
    public List<OntologyTermBean> getTopLevel(String id) {
        return getTopLevel(id, 1);
    }

    /**
     * Returns a <code>List&lt;OntologyTerm&gt;</code> of <code>id</code>'s
     * top-level terms at level <code>level</code>, exclusive of self, or an
     * empty list if there are none.
     *
     * @param id id of top-level relative terms to return.
     * @param level the 1-relative level below the top level (i.e. 1 = top
     * level, 2 = top-level - 1, etc.)
     *
     * @return a <code>List&lt;OntologyTerm&gt;</code> of <code>id</code>'s
     * top-level terms at level <code>level</code>, exclusive of self, or an
     * empty list if there are none.
     */
    public List<OntologyTermBean> getTopLevel(String id, int level) {
        return getHigherLevels(id,level, ancestorGraphsMap);
    }


    public List<OntologyTermBean> getSelectedTopLevel(String id, int level) {
        return getHigherLevels(id,level, selectedAncestorGraphsMap);
    }

    public List<OntologyTermBean> getHigherLevels(String id, int level, Map<String, List<List<String>>> ancestorGraphMap) {

        // should be the first one in the graph, so level should be 1
        if (level <= 0) {
            throw new RuntimeException("Level must be > 0. level was " + level);
        }

        Set<OntologyTermBean> beans = new LinkedHashSet<>();
        List<List<String>> selectedAncestorGraphsId = ancestorGraphMap.get(id);

        if (selectedAncestorGraphsId != null) {
            for (List<String> ancestorGraphId : selectedAncestorGraphsId) {
                if ((!ancestorGraphId.isEmpty()) && (ancestorGraphId.size() >= level)) {
                    String topTermId = ancestorGraphId.get(level - 1);

                    //if ( ! id.equals(topTermId)) {                              // Don't include self in top-level list.
                    beans.add(allTermsMap.get(topTermId));
                    //}
                }
            }
        }

        return new ArrayList<>(beans);
    }



    /**
     * Returns a <code>List&lt;OntologyTerm&gt;</code> of all of
     * <code>id</code>'s terms between the top level (inclusive) and
     * <code>id</code>'s parent terms, or an empty list if there are none.
     *
     * @param id id of ancestor terms to return.
     *
     * @return a <code>List&lt;OntologyTerm&gt;</code> of all of
     * <code>id</code>'s terms between the top level (inclusive) and
     * <code>id</code>/s parent terms, or an empty list if there are none.
     */
    public List<OntologyTermBean> getAncestors(String id) {
        Set<OntologyTermBean> beans = new LinkedHashSet<>();

        List<List<String>> ancestorGraphsId = ancestorGraphsMap.get(id);
        if (ancestorGraphsId != null) {
            for (List<String> ancestorGraphId : ancestorGraphsId) {
                if ( ! ancestorGraphId.isEmpty()) {
                    for (String ancestorTermId : ancestorGraphId) {
                        if ((ancestorTermId != null) && ( ! ancestorTermId.isEmpty())) {
                            beans.add(allTermsMap.get(ancestorTermId));
                        }
                    }
                }
            }
        }

        return new ArrayList<>(beans);
    }

    /**
     * Returns a <code>List&lt;OntologyTerm&gt;</code> of all of
     * <code>id</code>'s parent terms, or an empty list if there are none.
     *
     * @param id id of parent terms to return.
     *
     * @return a <code>List&lt;OntologyTerm&gt;</code> of all of
     * <code>id</code>'s parent terms, or an empty list if there are none.
     */
    public List<OntologyTermBean> getParents(String id) {

        Set<OntologyTermBean> beans = new LinkedHashSet<>();
        List<List<String>> ancestorGraphsId = ancestorGraphsMap.get(id);

        if (ancestorGraphsId != null) {
            for (List<String> ancestorGraphId : ancestorGraphsId) {
	            if (ancestorGraphId.size() >= 1){
	            	OntologyTermBean bean = allTermsMap.get(ancestorGraphId.get(ancestorGraphId.size() - 1));
                    if ( bean !=  null) {
                        beans.add(bean);
                    }
	            }
            }
        }

        return new ArrayList<>(beans);
    }

    /**
     * Returns a <code>List&lt;OntologyTerm&gt;</code> of all of
     * <code>id</code>'s terms between the top level (exclusive) and
     * <code>id</code>'s parent terms, or an empty list if there are none.
     *
     * @param id id of intermediate terms to return.
     *
     * @return a <code>List&lt;OntologyTerm&gt;</code> of all of
     * <code>id</code>'s terms between the top level (exclusive) and
     * <code>id</code>'s parent terms, or an empty list if there are none.
     *
     */

    public List<OntologyTermBean> getIntermediates(String id) {
        Set<OntologyTermBean> beans = new LinkedHashSet<>();

        List<List<String>> ancestorGraphsId = ancestorGraphsMap.get(id);
        if (ancestorGraphsId != null) {
            for (List<String> ancestorGraphId : ancestorGraphsId) {
                if ( ! ancestorGraphId.isEmpty()) {
                    boolean firstTerm = true;
                    for (String ancestorTermId : ancestorGraphId) {
                        if ((ancestorTermId != null) && ( ! ancestorTermId.isEmpty())
                                                   && ( ! firstTerm))
                        {
                            beans.add(allTermsMap.get(ancestorTermId));
                        }

                        firstTerm = false;
                    }
                }
            }
        }

        return new ArrayList<>(beans);
    }

    /**
     * Returns a <code>List&lt;OntologyTerm&gt;</code> of all of
     * <code>id</code>'s child terms, or an empty list if there are none.
     *
     * @param id id of child terms to return.
     *
     * @return a <code>List&lt;OntologyTerm&gt;</code> of all of
     * <code>id</code>'s child terms, or an empty list if there are none.
     */
    public List<OntologyTermBean> getChildren(String id) {
        Set<OntologyTermBean> beans = new LinkedHashSet<>();

        List<List<String>> descendentGraphsId = getDescendentGraphs(id);
        if (descendentGraphsId != null) {
            for (List<String> descendentGraphId : descendentGraphsId) {
                if ( ! descendentGraphId.isEmpty()) {
                    String childId = descendentGraphId.get(0);
                    beans.add(allTermsMap.get(childId));
                }
            }
        }

        return new ArrayList<>(beans);
    }

    /**
     * Returns a <code>List&lt;OntologyTerm&gt;</code> of all of
     * <code>id</code>'s terms between the child level (inclusive) and the leaf
     * terms (inclusive), or an empty list if there are none.
     *
     * @param id id of descendent terms to return.
     *
     * @return a <code>List&lt;OntologyTerm&gt;</code> of all of
     * <code>id</code>'s terms between the child level (inclusive) and the leaf
     * terms (inclusive), or an empty list if there are none.
     */
    public List<OntologyTermBean> getDescendents(String id) {
        Set<OntologyTermBean> beans = new LinkedHashSet<>();

        List<List<String>> descendentGraphsId = getDescendentGraphs(id);
        if (descendentGraphsId != null) {
            for (List<String> descendentGraphId : descendentGraphsId) {
                if ( ! descendentGraphId.isEmpty()) {
                    for (String descendentTermId : descendentGraphId) {
                        beans.add(allTermsMap.get(descendentTermId));
                    }
                }
            }
        }

        return new ArrayList<>(beans);
    }

    /**
     * Returns a <code>List&lt;OntologyTerm&gt;</code> of <code>id</code>'s
     * descendent terms at level <code>level</code>, exclusive of self, or an
     * empty list if there are none.
     *
     * @param id id of child-relative terms to return.
     * @param level the 1-relative level below the current level (i.e. 1 = child
     * level, 2 = child level + 1, etc.)
     *
     * @return a <code>List&lt;OntologyTerm&gt;</code> of <code>id</code>'s
     * descendent terms at level <code>level</code>, exclusive of self, or an
     * empty list if there are none.
     */
    public List<OntologyTermBean> getDescendents(String id, int level) {
        if (level <= 0) {
            throw new RuntimeException("Level must be > 0. level was " + level);
        }

        Set<OntologyTermBean> beans = new LinkedHashSet<>();

        List<List<String>> descendentGraphsId = getDescendentGraphs(id);
        if (descendentGraphsId != null) {
            for (List<String> descendentGraphId : descendentGraphsId) {
                if (( ! descendentGraphId.isEmpty()) && (descendentGraphId.size() >= level)) {
                    String term = descendentGraphId.get(level - 1);
                    beans.add(allTermsMap.get(term));
                }
            }
        }

        return new ArrayList<>(beans);
    }


    // PROTECTED METHODS


    /**
     * Populate all terms, keyed by id.
     *
     * @param query the query to be executed to populate the list.
     *
     * @throws SQLException
     *
     * Side Effects: this method populates a map, indexed by id, of each id's
     *               node ids, which is later used to create the ancestor list.
     */
    protected final void populateAllTerms(String query) throws SQLException {

        //System.out.println("POPULATE ALL TERMS: "+ query);
        // need to preserve oder of mysql query result so that ontologyBrowser.createTreeJson()
        // would work as it works from top of the tree, which corresponds to the order of the mysql query
        Map<String, OntologyTermBean> map = new LinkedHashMap<>();


        try (final PreparedStatement ps = connection.prepareStatement(query)) {

            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                String mapKey = resultSet.getString("termId");
                OntologyTermBean bean = new OntologyTermBean();
                bean.setId(mapKey);
                bean.setName(resultSet.getString("termName"));
                bean.setDefinition(resultSet.getString("termDefinition") == null
                            ? "" : resultSet.getString("termDefinition"));

                // alternative ID
                String alt_ids = resultSet.getString("alt_ids");

                if (! resultSet.wasNull() ) {
                    bean.setAltIds(Arrays.asList(alt_ids.split(",")));
                }

                // list of node IDs

                List<String> nodeIdsOfTerm = Arrays.asList(resultSet.getString("nodes").split(","));
                Set<Integer> iNodeIdsSet = new HashSet<>();
                for (String sNodeId : nodeIdsOfTerm) {
                    iNodeIdsSet.add(Integer.parseInt(sNodeId));
                }

                bean.setNodeIds(new ArrayList<Integer>(iNodeIdsSet));

                map.put(mapKey, bean);

                // key: termId, value:
                id2nodesMap.put(mapKey, nodeIdsOfTerm);
            }

            ps.close();
        }

        this.allTermsMap = map;

    }

    /**
     * Using the local ancestor node ids created by <code>populateAllTerms()
     * </code>, returns the set of ancestor graphs for each id.
     *
     * NOTE: the root element is removed from all ancestor records.
     */
    protected final void populateAncestorGraph() {
        Map<String, List<List<String>>> ancestorsMap = new HashMap<>();
        Map<String, List<List<String>>> selectedAncestorsMap = new HashMap<>();

        Set<Map.Entry<String, List<String>>> entrySet = id2nodesMap.entrySet();

        for (Map.Entry<String, List<String>> entry : entrySet) {
            List<List<String>> ancestorList = new ArrayList<>();
            List<List<String>> selectedAncestorList = new ArrayList<>();

            for (String sNodeId : entry.getValue()) {
                List<String> topTermIds = new ArrayList<>();
                List<String> selectedToptermIds = new ArrayList<>();
                Integer nodeId = commonUtils.tryParseInt(sNodeId);

                String ancestorNodeIdConcat = ancestorMap.get(nodeId);

                if (ancestorNodeIdConcat !=  null) {
                    String[] sAncestorNodeIds = ancestorNodeIdConcat.split(" ");

                    for (String sAncestorNodeId : sAncestorNodeIds) {
                        if (commonUtils.tryParseInt(sAncestorNodeId) != 0) {

                            topTermIds.add(node2termMap.get(sAncestorNodeId));
                        }
                    }
                    ancestorList.add(topTermIds);
                }

                String selectedAncestorNodeIdConcat = selectedAncestorMap.get(nodeId);
                if (selectedAncestorNodeIdConcat !=  null) {

                    String[] sSelectedAncestorNodeIds = selectedAncestorNodeIdConcat.split(" ");
                    for (String sSelectedAncestorNodeId : sSelectedAncestorNodeIds) {
                        if (commonUtils.tryParseInt(sSelectedAncestorNodeId) != 0) {

                            selectedToptermIds.add(node2termMap.get(sSelectedAncestorNodeId));
                        }
                    }
                    selectedAncestorList.add(selectedToptermIds);
                }

            }

            ancestorsMap.put(entry.getKey(), ancestorList);

            if ( selectedAncestorList.size() > 0) {
                selectedAncestorsMap.put(entry.getKey(), selectedAncestorList);
            }
        }

        ancestorGraphsMap = ancestorsMap;
        selectedAncestorGraphsMap = selectedAncestorsMap;
    }

    /**
     * Populates each node's ancestor map.
     *
     * @param query the query to be executed to populate the map.
     *
     * @throws SQLException
     */
    protected void populateAncestorMap(String query) throws SQLException {
        try (final PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                ancestorMap.put(resultSet.getInt("node_id"), resultSet.getString("fullpath"));
            }

            ps.close();
        }

    }

    /**
     * Returns the set of descendent graphs for the given id.
     *
     * @param query the query to be executed to populate the map.
     *
     * @return the set of descendent graphs for the given id.
     */
    protected final List<List<String>> getDescendentGraphsInternal(String query) {
        List<List<String>> descendentsIdList = new ArrayList<>();
        List<String> descendentNodes;
        try {
            descendentNodes = getDescendentNodes(query);
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
        for (String graph : descendentNodes) {
            String[] descendentGraphNodes = graph.split(" ");
            List<String> descendentIds = new ArrayList<>();
            for (String descendentGraphNode : descendentGraphNodes) {
                descendentIds.add(node2termMap.get(descendentGraphNode));
            }
            descendentsIdList.add(descendentIds);
        }

        return descendentsIdList;
    }

    /**
     * Populates the node2term map with the term matching each node.
     *
     * @param query the query to be executed to populate the map.
     *
     * @throws SQLException
     */
    protected void populateNode2TermMap(String query) throws SQLException {
        try (final PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                node2termMap.put(Integer.toString(resultSet.getInt("node_id")), resultSet.getString("term_id"));
            }

            ps.close();
        }
    }

    /**
     * Query the database, returning a map of all synonyms indexed by term id
     *
     * @param query the query to be executed to populate the map.
     *
     * @throws SQLException when a database exception occurs
     */
    protected final void populateSynonyms(String query) throws SQLException {
        Map<String, List<String>> map = new HashMap<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                String termId = resultSet.getString("term_id");
                String synonym = resultSet.getString("syn_name");
                if ( ! map.containsKey(termId)) {
                    map.put(termId, new ArrayList<String>());
                }
                List<String> termSynonyms = map.get(termId);
                if ( ! termSynonyms.contains(synonym)) {
                    termSynonyms.add(synonym);
                }
            }

            ps.close();
        }

        synonymsMap = map;
    }


    // PRIVATE METHODS


    /**
     * Returns a <code>List&lt;String&gt; of all descendent nodes.
     *
     * @param query the query to be executed to populate the list.
     *
     * @return a <code>List&lt;String&gt; of all descendent nodes.
     *
     * @throws SQLException
     */
    private List<String> getDescendentNodes(String query) throws SQLException {
        List<String> descendentNodes = new ArrayList<>();

        try (final PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                String fullpath = resultSet.getString("fullpath");
                // Remove the first element, which is self.
                int pos = fullpath.indexOf(" ");
                if (pos > 0) {
                    fullpath = fullpath.substring(pos + 1);
                    descendentNodes.add(fullpath);
                }
            }

            ps.close();
        }

        return descendentNodes;
    }


    // INTERNAL CLASSES


    public static class OntologyTermRecord {
        public String key;
        public List<OntologyTermBean> value;

        public OntologyTermRecord(Map.Entry<String, List<OntologyTermBean>> entry) {
            key = entry.getKey();
            value = entry.getValue();
        }
    }

    public static class OntologyTermRecordComparator implements Comparator<OntologyTermRecord> {

        @Override
        public int compare(OntologyTermRecord thisTerm, OntologyTermRecord thatTerm) {
            if (thisTerm.key.equalsIgnoreCase(thatTerm.key)) {
                OntologyTermBeanComparator ontologyTermBeanComparator = new OntologyTermBeanComparator();
                Collections.sort(thisTerm.value, ontologyTermBeanComparator);
                Collections.sort(thatTerm.value, ontologyTermBeanComparator);
                return 0;
            } else {
                return thisTerm.key.compareTo(thatTerm.key);
            }
        }
    }

    public static class OntologyTermBeanComparator implements Comparator<OntologyTermBean> {
        @Override
        public int compare(OntologyTermBean thisBean, OntologyTermBean thatBean) {
            if (thisBean.getId().equalsIgnoreCase(thatBean.getId())) {
                if (thisBean.getName().equalsIgnoreCase(thatBean.getName())) {
                    return 0;
                } else {
                    return thisBean.getName().compareTo(thatBean.getName());
                }
            } else {
                return thisBean.getId().compareTo(thatBean.getId());
            }
        }
    }

}