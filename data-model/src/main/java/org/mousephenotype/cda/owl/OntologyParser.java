package org.mousephenotype.cda.owl;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.EntitySearcher;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class OntologyParser {

    List<OntologyTermDTO> terms = new ArrayList<>();

    OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    OWLDataFactory factory = manager.getOWLDataFactory();
    OWLOntology ontology;

    OWLAnnotationProperty LABEL_ANNOTATION;
    OWLAnnotationProperty ALT_ID;
    OWLAnnotationProperty X_REF;
    OWLAnnotationProperty REPLACEMENT;
    OWLAnnotationProperty CONSIDER;
    OWLAnnotationProperty TERM_REPLACED_BY;
    ArrayList<OWLAnnotationProperty> IS_OBSOLETE;
    ArrayList<OWLAnnotationProperty> SYNONYM_ANNOTATION;
    ArrayList<OWLAnnotationProperty> DEFINITION_ANNOTATION;
    Set<OWLPropertyExpression> PART_OF;

    private Map<String, OntologyTermDTO> termMap = new HashMap<>(); // OBO-style ids because that's what we index.
    private Set<String> termsInSlim; // <ids of classes on slim>
    private Set<String> topLevelIds;


    /**
     *
     * @param pathToOwlFile
     * @param prefix
     * @param topLevelIds ontology ids to be used as top level (selected top level); Only need to pass this if you want top levels or intermediate terms up to the top level;
     * @param wantedIds ids to be used for the slim. If null whole ontology will be used.
     * @throws OWLOntologyCreationException
     */
    public OntologyParser(String pathToOwlFile, String prefix, Set<String> topLevelIds, Set<String> wantedIds)
    throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {

        setUpParser(pathToOwlFile);
        if (wantedIds != null){
            getTermsInSlim(wantedIds, prefix);
        }
        terms = new ArrayList<>();
        this.topLevelIds = topLevelIds;

        Set<OWLClass> allClasses = ontology.getClassesInSignature();
        for (OWLClass cls : allClasses){
            if (startsWithPrefix(cls, prefix)){
                OntologyTermDTO term = getDTO(cls);
                term.setEquivalentClasses(getEquivaletClasses(cls, prefix));
                terms.add(term);
                termMap.put(term.getAccessionId(), term);
            }
        }
    }

    private Boolean startsWithPrefix(OWLClass cls, Collection<String> prefix){
        if (prefix == null){
            return true; // when prefix is passed as null it means we don't care about it; take everything
        }
        for (String p: prefix) {
            if (!getIdentifierShortForm(cls).startsWith(p + ":")) {
                return false;
            }
        }
        return true;
    }

    private Boolean startsWithPrefix(OWLClass cls, String prefix){
        return (prefix == null || getIdentifierShortForm(cls).startsWith(prefix + ":"));
    }

    public List<OntologyTermDTO> getTerms(){
        return terms;
    }

    /**
     * The getters by id (below) are needed until we move to an OWL API indexer. Should get rid of them and use the getters on the objects directly after that.
     */

    public OntologyTermDTO getOntologyTerm (String accessionId){

        return termMap.get(accessionId);

    }


    /**
     * Set up properties for parsing ontology.
     * @throws OWLOntologyCreationException
     */
    private void setUpParser(String pathToOwlFile)
            throws OWLOntologyCreationException{

        LABEL_ANNOTATION = factory.getRDFSLabel();

        ALT_ID = factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#hasAlternativeId"));

        X_REF = factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#hasDbXref"));

        SYNONYM_ANNOTATION = new ArrayList<>();
        SYNONYM_ANNOTATION.add(factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#hasExactSynonym")));
        SYNONYM_ANNOTATION.add(factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#hasNarrowSynonym")));
        SYNONYM_ANNOTATION.add(factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#hasRelatedSynonym")));
        SYNONYM_ANNOTATION.add(factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#hasBroadSynonym")));

        DEFINITION_ANNOTATION = new ArrayList<>();
        DEFINITION_ANNOTATION.add(factory.getOWLAnnotationProperty(IRI.create("http://purl.obolibrary.org/obo/IAO_0000115")));

        IS_OBSOLETE = new ArrayList<>();
        IS_OBSOLETE.add(factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#isObsolete")));
        IS_OBSOLETE.add(factory.getOWLAnnotationProperty(IRI.create("http://www.w3.org/2002/07/owl#deprecated")));

        REPLACEMENT = factory.getOWLAnnotationProperty(IRI.create("http://purl.obolibrary.org/obo/IAO_0100001"));

        CONSIDER = factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#consider"));

        PART_OF = new HashSet<>();
        PART_OF.add(new OWLObjectPropertyImpl(IRI.create("http://purl.obolibrary.org/obo/ma#part_of")));
        PART_OF.add(new OWLObjectPropertyImpl(IRI.create("http://purl.obolibrary.org/obo/emap#part_of")));
        PART_OF.add(new OWLObjectPropertyImpl(IRI.create("http://purl.obolibrary.org/obo/part_of")));
        PART_OF.add(new OWLObjectPropertyImpl(IRI.create("http://purl.obolibrary.org/obo/BFO_0000050")));

        TERM_REPLACED_BY = factory.getOWLAnnotationProperty(IRI.create("http://purl.obolibrary.org/obo/IAO_0100001"));

        ontology = manager.loadOntologyFromOntologyDocument(IRI.create(new File(pathToOwlFile)));

    }

    /**
     * @param maxLevels how many levels to go down for subclasses; -1 for all.
     * @param cls
     * @return Set of labels + synonyms of all subclasses {maxLevels} away from cls. !! This method can be expensive so  values are not pe-loaded. Should only be used on leaf nodes !!
     */
    public Set<String> getNarrowSynonyms(OntologyTermDTO cls, int maxLevels){

        Set<OWLClass> descendents = new HashSet<>();
        TreeSet<String> res = new TreeSet<String>();
        descendents = getDescendentsPartOf(cls.getCls(), 1, 0, descendents);

        for (OWLClass desc : descendents){
            res.addAll(getSynonyms(desc));
            res.add(getLabel(desc));
        }

        return res;
    }


    private Set<OWLClass> getDescendentsPartOf(OWLClass cls, int maxLevels, int currentLevel, Set<OWLClass> children ){

        Set<OWLClass> subclasses = getChildrenPartOf(cls);
        currentLevel ++;
        if (!subclasses.isEmpty()){
            children.addAll(subclasses);
            if (currentLevel < maxLevels || maxLevels < 0){
                for (OWLClass subClass: subclasses ){
                    getDescendentsPartOf(subClass, maxLevels, currentLevel, children);
                }
            }
        }
        return children;

    }


    /**
     *  OWL identifiers look like "http://purl.obolibrary.org/obo/MA_0100084" and we want "MA:0100084", which is the old OBO style
     * @param cls
     * @return OBO-style id
     */
    private String getIdentifierShortForm (OWLClass cls){

        String id = cls.getIRI().toString();
        return id.split("/|#")[id.split("/|#").length-1].replace("_", ":");

    }


    /**
     *
     * @param cls
     * @return Set of alternative ids for the given class OR empty set when none available
     */
    private Set<String> getAltIds(OWLClass cls){

        Set<String> altIds = new HashSet<>();
        Collection<OWLAnnotation> annotations = EntitySearcher.getAnnotations(cls, ontology, ALT_ID);
        if (annotations != null && !annotations.isEmpty()){
            for (OWLAnnotation ann: annotations) {
                OWLLiteral altId = ((OWLLiteral)ann.getValue());
                altIds.add(altId.getLiteral());
            }
        }
        return altIds;

    }


    /**
     *
     * @param cls
     * @return term name (class label)
     */
    private String getLabel (OWLClass cls){
        if (EntitySearcher.getAnnotations(cls,ontology, LABEL_ANNOTATION) != null && !EntitySearcher.getAnnotations(cls,ontology, LABEL_ANNOTATION).isEmpty()){
            OWLLiteral label = ((OWLLiteral)EntitySearcher.getAnnotations(cls,ontology, LABEL_ANNOTATION).iterator().next().getValue());
            if (label != null){
                return label.getLiteral();
            }
        }
        return "";

    }


    /**
     *
     * @param cls
     * @return term definition/description
     */
    private String getDefinition (OWLClass cls){

        for (OWLAnnotationProperty definition: DEFINITION_ANNOTATION){
            for (OWLAnnotation annotation : EntitySearcher.getAnnotations(cls,ontology, definition)) {
                if (annotation.getValue() instanceof OWLLiteral) {
                    OWLLiteral val = (OWLLiteral) annotation.getValue();
                    return val.getLiteral();
                }
            }
        }
        return null;
    }


    /**
     * @param cls
     * @return List of synonyms as String
     */
    private Set<String> getSynonyms (OWLClass cls){

        Set<String> synonyms = new HashSet<>();

        for (OWLAnnotationProperty synonym: SYNONYM_ANNOTATION){
            for (OWLAnnotation annotation : EntitySearcher.getAnnotations(cls,ontology, synonym)) {
                if (annotation.getValue() instanceof OWLLiteral) {
                    OWLLiteral val = (OWLLiteral) annotation.getValue();
                    synonyms.add(val.getLiteral());
                }
            }
        }

        return synonyms;
    }


    private OntologyTermDTO getDTO(OWLClass cls){

        OntologyTermDTO term = new OntologyTermDTO();
        term.setAccessionId(getIdentifierShortForm(cls)); // i.e. MA:0100084
        term.setName(getLabel(cls));
        term.setDefinition(getDefinition(cls));
        term.setSynonyms(getSynonyms(cls));
        term.setObsolete(isObsolete(cls));
        term.setCls(cls);
        if (term.isObsolete()){
            String replacementId = getReplacementId(cls);
            if((replacementId != null)){
                term.setReplacementAccessionId(replacementId);
            }
            Set<String> considerIds = getConsiderIds(cls);
            if(considerIds != null && considerIds.size() > 0){
                term.setConsiderIds(considerIds);
            }
        }
        Set<String> altIds = getAltIds(cls);
        if ( altIds!= null && altIds.size() > 0){
            term.setAlternateIds(altIds);
        }
        term = addChildrenInfo(cls, term);
        term = addParentInfo(cls, term);
        return term;
    }


    /**
     *
     * @param cls
     * @return Set of equivalent *named* classes.
     */
    private Set<OntologyTermDTO> getEquivaletClasses(OWLClass cls, String prefix){

        Set<OntologyTermDTO> eqClasses = new HashSet<>();
        for (OWLClassExpression classExpression : EntitySearcher.getEquivalentClasses(cls, ontology)){
            if (classExpression.isClassExpressionLiteral() && !getIdentifierShortForm(classExpression.asOWLClass()).startsWith(prefix + ":")){
                eqClasses.add(getDTO(classExpression.asOWLClass()));
            }
        }
        return  eqClasses;
    }


    //TODO  private Set<OntologyTermDTO> getTopLevelTerms(OWLClass cls, Set<String> topLevelTerms){


    //TODO

    /**
     *
     * @param wantedIDs
     * @param prefix
     * @return Set of class ids that belond in the slim
     * @throws IOException
     * @throws OWLOntologyStorageException
     */
    public Set<String> getTermsInSlim(Set<String> wantedIDs, String prefix)
            throws IOException, OWLOntologyStorageException {

        // Cache it in termsInSlim so we don't have to re-compute this every time
        if (termsInSlim != null){
            return termsInSlim;
        }

        // Add replacement ids for deprecated classes to wanted ids
        for (OWLClass cls : ontology.getClassesInSignature()) {
            if (!cls.getIRI().isNothing() && EntitySearcher.getAnnotations(cls, ontology, TERM_REPLACED_BY) != null && wantedIDs.contains(getIdentifierShortForm(cls))) {
                for (OWLAnnotation annotation : EntitySearcher.getAnnotations(cls, ontology, TERM_REPLACED_BY)) {
                    if (annotation.getValue() instanceof OWLLiteral) {
                        wantedIDs.add(((OWLLiteral) annotation.getValue()).getLiteral());
                        wantedIDs.remove(getIdentifierShortForm(cls));
                    }
                }
            }
        }

        // Add the "seed" terms and their ancestors to the slim set.
        Set<String> classesInSlim = new HashSet<>();
        for (OWLClass cls : ontology.getClassesInSignature()) {
            if (wantedIDs.contains(getIdentifierShortForm(cls))) {
                if (startsWithPrefix(cls, prefix)) {
                    classesInSlim.add(getIdentifierShortForm(cls));
                    classesInSlim.addAll(getClassAncestors(cls, null, classesInSlim));
                }
            }
        }

        termsInSlim = classesInSlim;
        System.out.println("Set termsInSlim " + termsInSlim.size());
        return classesInSlim;

    }


    // TODO run reasoner on ontology first
    private Set<String> getClassAncestors(OWLClass cls, Set<String> prefixes, Set<String> ancestorIds){

       Collection<OWLClassExpression> superClasses = EntitySearcher.getSuperClasses(cls, ontology);
       for(OWLClassExpression superClass : superClasses){
            if (superClass.isClassExpressionLiteral()){
                ancestorIds.add(getIdentifierShortForm(superClass.asOWLClass()));
                getClassAncestors (superClass.asOWLClass(), prefixes, ancestorIds);
            } else {
                //TODO test part_of too
                if (superClass instanceof OWLObjectSomeValuesFrom){
                    OWLObjectSomeValuesFrom svf = (OWLObjectSomeValuesFrom) superClass;
                    if (PART_OF.contains(svf.getProperty().asOWLObjectProperty())){
                        if (svf.getFiller() instanceof OWLNamedObject){
                            ancestorIds.add(getIdentifierShortForm(svf.getFiller().asOWLClass()));
                            getClassAncestors (svf.getFiller().asOWLClass(), prefixes, ancestorIds);
                        }
                    }
                }
            }
        }

        return ancestorIds;

    }


    /**
     *
     * @param cls
     * @param term the ontology term dto where you want the child info to be added.
     * @return Return the cls dto with added information about child classes: childIds and childTerms. If terms for slim were provided to the parser the results will be restricted to slim classes.
     */
    private OntologyTermDTO addChildrenInfo(OWLClass cls, OntologyTermDTO term){

        Set<OWLClass> children = getChildrenPartOf(cls);
        if (termsInSlim != null){
            // Filter out child terms not in slim
            children = children.stream().filter(termCls -> { return termsInSlim.contains(getIdentifierShortForm(termCls));}).collect(Collectors.toSet());
        }
        for (OWLClass child : children){
            term.addChildId(getIdentifierShortForm(child.asOWLClass()));
            term.addChildName(getLabel(child.asOWLClass()));
        }
        return  term;
    }

    /**
     * @param term
     * @param cls
     * @return Return the cls dto with added information about child classes: childIds and childTerms.
     */
    private OntologyTermDTO addParentInfo(OWLClass cls, OntologyTermDTO term){

        // TODO add part_of
        for (OWLClassExpression classExpression : EntitySearcher.getSuperClasses(cls, ontology)){
            if (classExpression.isClassExpressionLiteral()){
                term.addParentId(getIdentifierShortForm(classExpression.asOWLClass()));
                term.addParentName(getLabel(classExpression.asOWLClass()));
            }
        }
        return  term;
    }



    private boolean isObsolete(OWLClass cls){

        for (OWLAnnotationProperty synonym: IS_OBSOLETE){
            if (EntitySearcher.getAnnotations(cls,ontology, synonym).size() > 0) {
                return true;
            }
        }
        return false;
    }


    /**
     *
     * @param cls
     * @return ID of replacement class for an obsolete one. It
     */
    private String getReplacementId(OWLClass cls){

        Collection<OWLAnnotation> res = EntitySearcher.getAnnotations(cls, ontology, REPLACEMENT);

        if (res.size() > 0){
            if (res.size() > 1){
                System.out.println("WARNING: more than 1 replacement terms for deprecated class " + getIdentifierShortForm(cls));
            }
            if (res.iterator().next().getValue() instanceof OWLLiteral) {
                return ((OWLLiteral) res.iterator().next().getValue()).getLiteral();
            }
        }
        return null;

    }


    /**
     *
     * @param cls
     * @return IDs of classes to consider using instead of an obsolete term. There can be multiple ids for each obsolete term.
     */
    private Set<String> getConsiderIds(OWLClass cls){

        Collection<OWLAnnotation> res = EntitySearcher.getAnnotations(cls, ontology, CONSIDER);
        Set<String> ids = new HashSet<>();

        res.stream()
                .filter(item->(item.getValue() instanceof OWLLiteral))
                .forEach(item->ids.add(getConsiderId(item, cls)));

        return ids;

    }


    private String getConsiderId(OWLAnnotation ann, OWLClass cls){

        String consider = null ;
        if (ann.getValue() instanceof OWLLiteral){
            consider = ((OWLLiteral) ann.getValue()).getLiteral();
        }
        return consider;

    }


    private Set<OWLClass> getChildrenPartOf(OWLClass cls){

        Set<OWLClass> children = new HashSet<>();

        for ( OWLClassExpression classExpression: EntitySearcher.getSubClasses(cls, ontology)){
            if (classExpression.isClassExpressionLiteral()){
                children.add(classExpression.asOWLClass());
            } else if (classExpression instanceof OWLObjectSomeValuesFrom){
                OWLObjectSomeValuesFrom svf = (OWLObjectSomeValuesFrom) classExpression;
                if (PART_OF.contains(svf.getProperty().asOWLObjectProperty())){
                    if (svf.getFiller() instanceof OWLNamedObject){
                        children.add(svf.getFiller().asOWLClass());
                    }
                }
            }
        }

        return children;

    }


}