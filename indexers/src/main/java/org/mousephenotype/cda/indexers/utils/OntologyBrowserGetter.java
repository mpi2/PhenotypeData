package org.mousephenotype.cda.indexers.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mousephenotype.cda.owl.OntologyParser;
import org.mousephenotype.cda.owl.OntologyTermDTO;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import java.util.*;
import java.util.stream.Collectors;

@EnableAutoConfiguration
public class OntologyBrowserGetter {

	public OntologyBrowserGetter(){	}

	public String getScrollTo(List<JSONObject> tree){

		for (JSONObject topLevel: tree){
			if (topLevel.has("state") && topLevel.getJSONObject("state").has("opened") && topLevel.getJSONObject("state").getBoolean("opened") == true){
				return "" + topLevel.getInt("id");
			}
		}
		return "";
	}

	private JSONObject getState(boolean opened){
		JSONObject state = new JSONObject();
		state.accumulate("opened", opened);
		return state;
	}


	public List<JSONObject> getChildrenJson(OntologyTermDTO term, String baseUrl, OntologyParser parser, Map<String, Integer>  mpGeneVariantCount){

		List<JSONObject> children = new ArrayList<>();

		if (term.getChildIds() != null){
			for (String childId : term.getChildIds()){
				OntologyTermDTO child = parser.getOntologyTerm(childId);
				List<Integer> nodeIds = new ArrayList<>();
				nodeIds.add(child.getNodeIds().iterator().next()); // For children it doesn't matter which node id we use.
				children.add(getJson(nodeIds, baseUrl, parser, "", new HashMap<>(), mpGeneVariantCount));

			}
		}
		System.out.println();

		return children;
	}


	public List<JSONObject> createTreeJson(OntologyTermDTO term, String baseUrl, OntologyParser parser, Map<String, Integer> mpGeneVariantCount){

		Map<Integer, JSONObject> nodes = new HashMap<>();
		if (term.getPathsToRoot() != null) {
			for (List<Integer> path : term.getPathsToRoot().values()) {
				getJson(path, baseUrl, parser, term.getAccessionId(), nodes, mpGeneVariantCount);
			}
		} else {
			System.out.println("No path to root for " + term.getAccessionId());
		}

		// root is 0, mammalian phenotype for MP
		//TODO fix this, it's not working the same with MA.
		return addTopLevels(nodes.get(0), baseUrl, parser, nodes, mpGeneVariantCount);

	}

	/**
	 * Add all top levels to tree, regardless if the term one searched for is in it or not. That's how we display it at the moment.
	 * @param tree
	 */
	private List<JSONObject> addTopLevels(JSONObject tree, String baseUrl, OntologyParser parser, Map<Integer, JSONObject> nodes,  Map<String, Integer> mpGeneVariantCount){

		List<OntologyTermDTO> topLevels = parser.getTopLevelTerms();
		List<JSONObject> termsToDisplay =  new ArrayList<>();
		if (tree.has("children") && tree.get("children") instanceof JSONArray) {
			JSONArray children = tree.getJSONArray("children");
			Map<String, JSONObject> topLevelsUsed = new HashMap<>();
			for (int i = 0; i < children.length(); i++) {
				topLevelsUsed.put(children.getJSONObject(i).getString("term_id"), children.getJSONObject(i));
			}

			for (OntologyTermDTO topLevel : topLevels) {
				if (topLevelsUsed.containsKey(topLevel.getAccessionId())) {
					termsToDisplay.add(topLevelsUsed.get(topLevel.getAccessionId()));
				} else {
					List<Integer> tlNodeId = topLevel.getNodeIds().stream().collect(Collectors.toList());
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
	 */
	private JSONObject getJson( List<Integer> path,  String baseUrl, OntologyParser parser, String searchTermId, Map<Integer, JSONObject> nodes, Map<String, Integer> mpGeneVariantCount){

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


	private JSONObject getJsonObjectWithBasicInfo(OntologyTermDTO term, String searchTermId, String baseUrl, Integer id, Map<Integer, JSONObject> nodes, Map<String, Integer> mpGeneVariantCount){

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

	private String getText(OntologyTermDTO term, String searchTermId, String baseUrl, Map<String, Integer> mpGeneVariantCount){

		StringBuffer text = new StringBuffer("<a target='_blank' href='").append(baseUrl).append(term.getAccessionId()).append( "'>");
		if (searchTermId.equals(term.getAccessionId())){
			text.append("<span class='qryTerm'>").append(term.getName()).append("</span>");
		} else {
			text.append(term.getName());
		}
		text.append("(<span class='gpAssoc'>")
			.append(mpGeneVariantCount.get(term.getAccessionId()) != null ? mpGeneVariantCount.get(term.getAccessionId()) : "0")
			.append("</span>)")
			.append("</a>");

		return text.toString();

	}

	private boolean hasChildren(OntologyTermDTO term){

		return term.getChildIds() != null && term.getChildIds().size() > 0;

	}

}
