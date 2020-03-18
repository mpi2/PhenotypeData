package org.mousephenotype.cda.owl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.*;

/**
 * Created by ilinca on 28/03/2017.
 */
public class TreeJsHelper {


    private static final Logger logger = LoggerFactory.getLogger(TreeJsHelper.class);

        public TreeJsHelper(){	}

        public static String getScrollTo(List<JSONObject> tree) throws JSONException {

            for (JSONObject topLevel: tree){
                if (topLevel.has("state") && topLevel.getJSONObject("state").has("opened") && topLevel.getJSONObject("state").getBoolean("opened") == true){
                    return "" + topLevel.getInt("id");
                }
            }
            return "";
        }

        private static JSONObject getState(boolean opened) throws JSONException{
            JSONObject state = new JSONObject();
            state.accumulate("opened", opened);
            return state;
        }


        public static List<JSONObject> getChildrenJson(OntologyTermDTO term, String baseUrl, OntologyParser parser, Map<String, Integer> mpGeneVariantCount) throws JSONException{

            List<JSONObject> children = new ArrayList<>();

            if (term.getChildIds() != null){
                for (String childId : term.getChildIds()){
                    OntologyTermDTO child = parser.getOntologyTerm(childId);
                    if (child.getNodeIds() != null) { // terms higher than our "root" will be null, for example MA_0003000, because the root is MA_
                        List<Integer> nodeIds = new ArrayList<>();
                        nodeIds.add(child.getNodeIds().iterator().next()); // For children it doesn't matter which node id we use.
                        children.add(getJson(nodeIds, baseUrl, parser, "", new HashMap<>(), mpGeneVariantCount));
                    }
                }
            }

            return children;
        }

        private static Set<String> knownMissingRootPaths = new HashSet<>(Arrays.asList(new String[] {
                "EMAPA:0",      // Anatomical structure
                "MA:0000001",   // mouse anatomical entity
                "MA:0003000"    // anatomical structure
        }));
        public static List<JSONObject> createTreeJson(OntologyTermDTO term, String baseUrl, OntologyParser parser, Map<String, Integer> mpGeneVariantCount, List<String> treeBrowserTopLevels) throws JSONException{

            Map<Integer, JSONObject> nodes = new HashMap<>();
            if (term.getPathsToRoot() != null) {
                for (List<Integer> path : term.getPathsToRoot().values()) {
                    getJson(path, baseUrl, parser, term.getAccessionId(), nodes, mpGeneVariantCount);
                }
            } else {
                if (! knownMissingRootPaths.contains(term.getAccessionId())) {
                    logger.info("No path to root for " + term.getAccessionId() + ". It's OK to have some terms outside, i.e. EMAPA:0, but if you see lots investigate the issue.");
                }
            }

            // root is 0, mammalian phenotype for MP
            return addTopLevels(nodes.get(0), baseUrl, parser, nodes, mpGeneVariantCount, treeBrowserTopLevels);

        }

        /**
         * Add all top levels to tree, regardless if the term one searched for is in it or not. That's how we display it at the moment.
         * @param tree
         * @throws JSONException 
         */
        private static List<JSONObject> addTopLevels(JSONObject tree, String baseUrl, OntologyParser parser, Map<Integer, JSONObject> nodes,  Map<String, Integer> mpGeneVariantCount, List<String> treeBrowserTopLevels) throws JSONException{

            List<OntologyTermDTO> topLevels = new ArrayList<>();
            for (String id: treeBrowserTopLevels){
                topLevels.add(parser.getOntologyTerm(id));
            }
            List<JSONObject> termsToDisplay =  new ArrayList<>();
            if (tree != null && tree.has("children") && tree.get("children") instanceof JSONArray) {
                JSONArray children = tree.getJSONArray("children");
                Map<String, JSONObject> topLevelsUsed = new HashMap<>();
                for (int i = 0; i < children.length(); i++) {
                    topLevelsUsed.put(children.getJSONObject(i).getString("term_id"), children.getJSONObject(i));
                }

                for (OntologyTermDTO topLevel : topLevels) {
                    if (topLevelsUsed.containsKey(topLevel.getAccessionId())) {
                        termsToDisplay.add(topLevelsUsed.get(topLevel.getAccessionId()));
                    } else {
                        List<Integer> tlNodeId = new ArrayList<>();
                        tlNodeId.add(topLevel.getNodeIds().iterator().next());
                        termsToDisplay.add(getJson(tlNodeId, baseUrl, parser, "", nodes, mpGeneVariantCount));
                    }
                }
            }
            return termsToDisplay;
        }

        /**
         *
         * @param baseUrl /data/phenotypes/ for mp links
         * @return
         * @throws JSONException 
         */
        private static JSONObject getJson( List<Integer> path,  String baseUrl, OntologyParser parser, String searchTermId, Map<Integer, JSONObject> nodes, Map<String, Integer> mpGeneVariantCount) throws JSONException{

            List<Integer> remainingPath = new ArrayList<>(path); // don't modify original
            // remove the part of the path that was already added to the JSON object
            for (int i = 0; i < path.size()-1 && nodes.containsKey(path.get(i+1)); i++){
                remainingPath.remove(path.get(i));
            }

            if (remainingPath.size() > 0) {

                Integer id = remainingPath.get(0);
                remainingPath.remove(0);
                OntologyTermDTO term = parser.getOntologyTerm(id);
                JSONObject current = getJsonObjectWithBasicInfo(term, searchTermId, baseUrl, id, nodes, mpGeneVariantCount);

                if (remainingPath.size() > 0) {
                    JSONArray children = current.has("children") && ! (current.get("children") instanceof Boolean) ? current.getJSONArray("children") : new JSONArray();
                    children.put(getJson(remainingPath, baseUrl, parser, searchTermId, nodes, mpGeneVariantCount));
                    current.put("children", children);
                    current.put("state", getState(path.size() > 0));
                } else {
                    current.put("children", hasChildren(term));
                }
                return current;
            }
            return null;
        }


        private static JSONObject getJsonObjectWithBasicInfo(OntologyTermDTO term, String searchTermId, String baseUrl, Integer id, Map<Integer, JSONObject> nodes, Map<String, Integer> mpGeneVariantCount) throws JSONException{

            JSONObject current;
            if (nodes.containsKey(id)){
                current = nodes.get(id);
            } else {
                current = new JSONObject();
                current.put("text", getText(term, searchTermId, baseUrl, mpGeneVariantCount));
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

        private static String getText(OntologyTermDTO term, String searchTermId, String baseUrl, Map<String, Integer> mpGeneVariantCount){

            StringBuffer text = new StringBuffer("<a target='_blank' href='").append(baseUrl).append(term.getAccessionId()).append( "'>");
            if (searchTermId.equals(term.getAccessionId())){
                text.append("<span class='qryTerm'>").append(term.getName()).append("</span>");
            } else {
                text.append(term.getName());
            }
            if (mpGeneVariantCount != null) {
                text.append("(<span class='gpAssoc'>")
                        .append(mpGeneVariantCount.get(term.getAccessionId()) != null ? mpGeneVariantCount.get(term.getAccessionId()) : "0")
                        .append("</span>)");
            }
            text.append("</a>");

            return text.toString();

        }

        private static boolean hasChildren(OntologyTermDTO term){

            return term.getChildIds() != null && term.getChildIds().size() > 0;

        }


}
