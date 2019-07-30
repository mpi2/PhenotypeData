package org.mousephenotype.cda.owl;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.IOException;
import java.util.*;

/**
 * Created by ilinca on 04/04/2017.
 *
 * The anatomogram uses a set of URBERON and EFO ids.
 * We want the "closest" mapping from MA ids to one of the anatomogram ids.
 * "Closest" is defined as the closest ancestor that has a mapping to a
 * UBERON id covered by the anatomogram.
 *
 * If the anatomogram list of ids changes, just update the list here.
 *
 * This can be easily extended to work with EMAPA ids as well as MA ids, just use the EMAPA prefix.
 */
public class AnatomogramMapper {

    private static Map<String, OWLClass> maMap = new HashMap<>();
    private static Map<String, String> nonUberonAnatomogramMap = new HashMap<>();


    // This list comes from Expression Atlas people.
    // They use 4 EFO ids but we do the mapping to UBERON and convert back those 4 ids so the anatomogram works. Not sure why they use EFO for them anyway.
    private static final List<String> anatomogramIds = Arrays.asList("EFO_0000530", "EFO_0000949", "EFO_0000962" , "EFO_0000973",
            "UBERON_0000004", "UBERON_0000007", "UBERON_0000010", "UBERON_0000029", "UBERON_0000369", "UBERON_0000473", "UBERON_0000945",
            "UBERON_0000947" , "UBERON_0000948" , "UBERON_0000955", "UBERON_0000956"  , "UBERON_0000970" , "UBERON_0000981" , "UBERON_0000989" ,
            "UBERON_0000990"  , "UBERON_0000995" , "UBERON_0000996", "UBERON_0000998", "UBERON_0001000", "UBERON_0001009" , "UBERON_0001043" ,
            "UBERON_0001103" , "UBERON_0001132" , "UBERON_0001153" , "UBERON_0001155" , "UBERON_0001211" , "UBERON_0001242"  , "UBERON_0001255" ,
            "UBERON_0001264" , "UBERON_0001301" , "UBERON_0001322" , "UBERON_0001347", "UBERON_0001348" , "UBERON_0001377" , "UBERON_0001645" ,
            "UBERON_0001723", "UBERON_0001736", "UBERON_0001831", "UBERON_0001891", "UBERON_0001894", "UBERON_0001896", "UBERON_0001897",
            "BERON_0001898", "UBERON_0001911", "UBERON_0001981", "UBERON_0002037", "UBERON_0002046", "UBERON_0002048", "UBERON_0002103", "UBERON_0002106",
            "UBERON_0002107", "UBERON_0002108 ", "UBERON_0002110", "UBERON_0002113", "UBERON_0002114", "UBERON_0002115", "UBERON_0002116", "UBERON_0002240 ",
            "UBERON_0002259", "UBERON_0002298", "UBERON_0002367", "UBERON_0002369",  "UBERON_0002370", "UBERON_0002371", "UBERON_0003126", "UBERON_0014892",
            // EFO_0000530 : hippocampus
            "UBERON_0001954",
            // EFO_0000949 : cartilage
            "UBERON_0002418",
            // EFO_0000962 : skin
            "UBERON_0002097",
            // EFO_0000973 : animal ovary
            "UBERON_0000992"
    );


    public static Map<String, Set<String>> getMapping(OntologyParser maParser, OntologyParser uberonParser, String prefix, String prefixOfWantedTerms) {

        nonUberonAnatomogramMap.put("UBERON_0001954", "EFO_0000530");
        nonUberonAnatomogramMap.put("UBERON_0002418", "EFO_0000949");
        nonUberonAnatomogramMap.put("UBERON_0002097", "EFO_0000962");
        nonUberonAnatomogramMap.put("UBERON_0001954", "EFO_0000973");

        Map<String, AnatomogramMapping> resMap = new HashMap<>(); // <MA, <UBERON>>

        for (OntologyTermDTO materm: maParser.getTerms()) {
            String maId = materm.getAccessionId();
            resMap.put(maId, new AnatomogramMapping(maId));
            maMap.put(maId, maParser.getOwlClass(maId));
        }

        for (OntologyTermDTO uberonTerm: uberonParser.getTerms()){
            getMappings(uberonParser.getOwlClass(uberonTerm.getAccessionId()), uberonParser, prefix, prefixOfWantedTerms, anatomogramIds, resMap);

        }

        for (OWLClass cls: maMap.values()){
            getMappingsForParents(cls, maParser, prefixOfWantedTerms, resMap);
        }


        Map<String, Set<String>> res = new HashMap<>();
        for (String id : resMap.keySet()){
            Set<String> current = new HashSet<>();
            for (String mappedId: resMap.get(id).getMappedIds()){
                // If it's EFO, replace mapped UBERON with corresponding EFO because anatomogram expects 4 EFO ids
                if (nonUberonAnatomogramMap.containsKey(mappedId)){
                    current.add(nonUberonAnatomogramMap.get(mappedId));
                } else {
                    current.add(mappedId);
                }
            }
            res.put(id.replace("_", ":"), current);
        }

        return res;
    }


    /**
     * Look for Xrefs going up the hierarchy in the main ontology.
     * @param cls
     * @param prefix
     * @param resMap
     */
    private static void getMappingsForParents(OWLClass cls, OntologyParser parser, String prefix, Map<String, AnatomogramMapping> resMap){

        Set<OWLClass> parentNodes = new HashSet<>();
        parentNodes.addAll(parser.getParents(cls, prefix, true));
        String clsId = parser.getIdentifierShortForm(cls);

        AnatomogramMapping currentMapping = resMap.get(clsId);
        int currentLevel = 1;

        while (currentLevel <= currentMapping.level && !parentNodes.isEmpty()){
            // Check existing mappings for parent classes are better (closer)
            for (OWLClass parent : parentNodes){
                AnatomogramMapping parentMapping = resMap.get(parser.getIdentifierShortForm(parent));
                if (currentMapping.level >= (currentLevel + parentMapping.level) && !parentMapping.getMappedIds().isEmpty()){
                    currentMapping.addMappedIds(parentMapping.getMappedIds(), currentLevel + parentMapping.level);
                    resMap.put(clsId, currentMapping);
                }
            }
            currentLevel ++;
            parentNodes = getParentsPartOf(parentNodes, prefix, parser);
        }

    }


    /**
     *
     * @param cls
     * @param prefix
     * @param toTerms
     * @return ids of nearest mapped classes found in {ont}, starting with {prefix}
     */
    private static void getMappings(OWLClass cls, OntologyParser parser, String prefix, String crossRefPrefix, List<String> toTerms, Map<String, AnatomogramMapping> resMap){

        Set<OWLClass> parentNodes = new HashSet<>();
        parentNodes.addAll(parser.getParents(cls, prefix, true));
        String clsId = parser.getIdentifierShortForm(cls).replace(":", "_");
        String xRefId = parser.getXref(cls, crossRefPrefix);
        if (xRefId != null){
            xRefId = xRefId.replace("_", ":");
            int currentLevel = 0;
            if (resMap.containsKey(xRefId)) {

                AnatomogramMapping mapping = resMap.get(xRefId);

                if (toTerms.contains(clsId)) {
                    if (mapping != null ) {
                        mapping.addMappedIds(clsId, currentLevel);
                    }
                }

                while (xRefId != null && mapping.getMappedIds().isEmpty() && !parentNodes.isEmpty()) {
                    currentLevel ++;
                    mapping.addMappedIds(getIds(parentNodes, toTerms, parser), currentLevel);
                    parentNodes = getParentsPartOf(parentNodes, prefix, parser);
                }

                resMap.put(xRefId, mapping);
            }
        }

    }


    private static Set<OWLClass> getParentsPartOf (Set<OWLClass> classes, String prefix, OntologyParser parser){

        Set<OWLClass> parents = new HashSet<>();
        for (OWLClass cls : classes){
            parents.addAll(parser.getParents(cls, prefix, true));
        }
        return parents;

    }


    private static Set<String> getIds(Set<OWLClass> classesToMap, List<String> toTerms, OntologyParser parser){

        Set<String> ids = new HashSet<>();
        for(OWLClass cls: classesToMap){
            String clsId = parser.getIdentifierShortForm(cls).replace(":", "_");
            if (toTerms.contains(clsId)) {
                ids.add(clsId);
            }
        }

        return ids;
    }



    static class AnatomogramMapping{

        String term;
        Set<String> mappedIds;
        int level;

        public AnatomogramMapping( String term ){
            this.term = term;
            mappedIds = new HashSet<>();
            level = Integer.MAX_VALUE;
        }

        public void addMappedIds(String id, int level) {
            if (level < this.level){
                this.level = level;
                this.mappedIds = new HashSet<>();
            }
            if (this.mappedIds == null){
                this.mappedIds = new HashSet<>();
            }
            if (this.level == level) {
                this.mappedIds.add(id);
            }
        }

        public void addMappedIds(Set<String> ids, int level) {
            if (level < this.level && !ids.isEmpty()){
                this.level = level;
                this.mappedIds = new HashSet<>();
            }
            if (this.mappedIds == null){
                this.mappedIds = new HashSet<>();
            }
            if (this.level == level) {
                this.mappedIds.addAll(ids);
            }
        }

        public Set<String> getMappedIds() {
            return mappedIds;
        }

        public void setMappedIds(Set<String> mappedIds) {
            this.mappedIds = mappedIds;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getTerm() {
            return term;
        }

        public void setTerm(String term) {
            this.term = term;
        }
    }


}
