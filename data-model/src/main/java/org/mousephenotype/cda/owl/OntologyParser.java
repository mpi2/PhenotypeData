package org.mousephenotype.cda.owl;


import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class OntologyParser {

    private static final Logger logger = LoggerFactory.getLogger(OntologyParser.class);

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

    private Map<String, Set<OWLClass>> ancestorsCache;
    private Map<String, OntologyTermDTO> termMap = new HashMap<>(); // OBO-style ids because that's what we index.
    private Map<String, OWLClass> classMap = new HashMap<>(); // OBO id to OWLClass object. We need this to avoid pre-loading of referrenced classes (MAs from MP)
    private Set<String> termsInSlim; // <ids of classes on slim>
    private TreeSet<String> topLevelIds;
    private List<OntologyTermDTO> toplevelterms;
    private Map<Integer, OntologyTermDTO> nodeTermMap = new HashMap<>(); // <nodeId, ontologyId>

    public OntologyParser(String pathToOwlFile, String prefix, Collection<String> topLevelIds, Set<String> wantedIds)
            throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {

        setUpParser(pathToOwlFile);

        if (wantedIds != null){
            getTermsInSlim(wantedIds, prefix);
        }

        if (topLevelIds != null) {
            this.topLevelIds = new TreeSet<>(); // sort alphabetically
            this.topLevelIds.addAll(topLevelIds);
        }

        OWLReasoner r = null;
        if (pathToOwlFile.contains("mp-hp.owl")) {
            r = new ElkReasonerFactory().createReasoner(ontology);
        }

        Set<OWLClass> allClasses = ontology.getClassesInSignature();

        for (OWLClass cls : allClasses){
            if (startsWithPrefix(cls, prefix)) {

                OntologyTermDTO term = getDTO(cls, prefix);

                if (pathToOwlFile.contains("mp-hp.owl")) {

                    // use reasoner to get equivalent and subclasses

                    // get the children of MP term as subClasses (we call it narrow synonyms)
                    // level of 2 means starting from the first level child(ren) (ie, level 1) and the child(ren) of the first level child (level 2)
                    int level = 3;
                    Set<OntologyTermDTO> equivalentClasses = new HashSet<>();
                    Set<OntologyTermDTO> narrowSynonymClasses = new HashSet<>();

                    // using reasoner to get equivalent classes
                    Set<OWLClass> eqClasses = r.getEquivalentClasses(cls).getEntities();
                    for (OWLClass eqc : eqClasses) {
                        String humanPhenotypePrefix = "HP";  // MP-HP mapping happens here

                        if (eqc.getIRI().getShortForm().startsWith(humanPhenotypePrefix)) {
                            OntologyTermDTO thisTerm = getDTO(eqc, humanPhenotypePrefix);
                            //System.out.println(term.getAccessionId() + " - equivalent class: " + term.getAccessionId());
                            equivalentClasses.add(thisTerm);
                        }
                    }

                    // using reasoner to get subclasses (narrow synonym)
                    // getsubclasses(true) gets all direct subclasses (ie, only one level down)
                    // getssubclasses(false) gets ALL subclasses (ie, all recursive levels down)

                    if (! term.getAccessionId().equals("MP:0000001")) {  // exclude MP root term for narrow synonyms
                        Set<OWLClass> subClasses = r.getSubClasses(cls, true).getFlattened();
                        for (OWLClass sc : subClasses) {
                            OntologyTermDTO thisTerm = getDTO(sc, prefix);
                            String termId = thisTerm.getAccessionId();
                            if (termId.startsWith("MP") || termId.startsWith("HP")) {
                                //System.out.println(term.getAccessionId() + " -- narrow synonym class: " + termId);

                                // no need to distinguish inside or outside slim as we use full ontology
                                narrowSynonymClasses.add(thisTerm);  // this one is level 1
                                getNextLevelNarrowSynonyms(r, sc, level - 1, narrowSynonymClasses); // next whatever levels
                            }
                        }

                        StringBuilder equivAcc   = new StringBuilder("");
                        StringBuilder equivTerm  = new StringBuilder("");
                        StringBuilder narrowAcc  = new StringBuilder("");
                        StringBuilder narrowTerm = new StringBuilder("");

                        for (OntologyTermDTO t : equivalentClasses) {
                            if (equivAcc.length() > 0) {
                                equivAcc.append("|");
                                equivTerm.append("|");
                            }
                            equivAcc.append(t.accessionId);
                            equivTerm.append(t.name);
                        }

                        for (OntologyTermDTO t : narrowSynonymClasses) {
                            if (narrowAcc.length() > 0) {
                                narrowAcc.append("|");
                                narrowTerm.append("|");
                            }
                            narrowAcc.append(t.accessionId);
                            narrowTerm.append(t.name);
                        }

                        List<String> data = new ArrayList<>();
                        data.add(term.getAccessionId());
                        data.add(term.getName());
                        data.add(equivAcc.toString());
                        data.add(equivTerm.toString());
                        data.add(narrowAcc.toString());
                        data.add(narrowTerm.toString());

                        StringBuilder row = new StringBuilder();
                        for (String cell : data) {
                            if (row.length() > 0)
                                row.append(",");
                            row
                                .append("\"")
                                .append(cell)
                                .append("\"");
                        }

                        if ((equivAcc.length() > 0) && (narrowAcc.length() > 0)) {
                            mpHpTerms.add(row.toString());
                        }

                        term.setEquivalentClasses(equivalentClasses);
                        term.setNarrowSynonymClasses(narrowSynonymClasses);
                    }
                }
                else {
                    term.setEquivalentClasses(getEquivaletNamedClasses(cls, prefix));
                }

                // 20171018 // JM // Filtered _in_ the root term and top level terms (if supplied)
//                if ( ! (term.getChildIds().size() > 0 && term.getParentIds().size() == 0) &&
//                        (topLevelIds != null && ! topLevelIds.contains(term.getAccessionId()))
//                        && term.getTopLevelIds().size() == 0) {
//                    continue;
//                }

                termMap.put(term.getAccessionId(), term);
                classMap.put(term.getAccessionId(), cls);






                if (term.getEquivalentClasses() == null) term.setEquivalentClasses(new HashSet<>());
                if (term.getNarrowSynonymClasses() == null) term.setNarrowSynonymClasses(new HashSet<>());

                if ((!term.getEquivalentClasses().isEmpty()) && (!term.getNarrowSynonymClasses().isEmpty())) {
                    mpEquivNarrowMap.put(term.getAccessionId(), term);
                } else if (!term.getEquivalentClasses().isEmpty()) {
                    mpEquivMap.put(term.getAccessionId(), term);
                } else if (!term.getNarrowSynonymClasses().isEmpty()) {
                    mpNarrowMap.put(term.getAccessionId(), term);
                } else {
                    mpMap.put(term.getAccessionId(), term);
                }





                // Turn off annoying INFO messages.
                LogManager.getLogger("org.semanticweb.elk").setLevel(Level.WARN);
            }
        }

        // Save the data to a file
        Collections.sort(mpHpTerms);
        FileWriter writer = new FileWriter("mpHpTerms.csv");
        writer.write("\"Term acc\", \"Term\", \"Equiv acc\", \"Equiv term\", \"Narrow acc\", \"Narrow term\"" + System.lineSeparator());
        for (String row : mpHpTerms) {
            writer.write(row + System.lineSeparator());
        }
        writer.write(System.lineSeparator());
        writer.write("\"Equivalent Only count: " + mpEquivMap.size() + "\"" + System.lineSeparator());
        writer.write("\"Narrow Only count: " + mpNarrowMap.size() + "\"" + System.lineSeparator());
        writer.write("\"Equivalent and Narrow count: " + mpEquivNarrowMap.size() + "\"" + System.lineSeparator());
        writer.write("\"Mp Only count: " + mpMap.size() + "\"" + System.lineSeparator());
        writer.close();
    }

    private List<String> mpHpTerms = new ArrayList<>();
    private Map<String, OntologyTermDTO> mpMap = new HashMap<>();
    private Map<String, OntologyTermDTO> mpEquivMap = new HashMap<>();
    private Map<String, OntologyTermDTO> mpNarrowMap = new HashMap<>();
    private Map<String, OntologyTermDTO> mpEquivNarrowMap = new HashMap<>();














    private void getNextLevelNarrowSynonyms(OWLReasoner r,  OWLClass oc, Integer level, Set<OntologyTermDTO> narrowSynonymClasses ){

        if (level > 0){
            //System.out.println("count level back: " + level); // 2, 1, 0
            level--;
            Set<OWLClass> subClasses = r.getSubClasses(oc, true).getFlattened();
            for (OWLClass sc : subClasses) {
                if (sc.getIRI().getShortForm().startsWith("MP") || sc.getIRI().getShortForm().startsWith("HP")) {
                    String prefix = sc.getIRI().getShortForm().startsWith("MP") ? "MP" : "HP";
                    narrowSynonymClasses.add(getDTO(sc, prefix));

                    if (level > 0) {
                        getNextLevelNarrowSynonyms(r, sc, level, narrowSynonymClasses); // recursive
                    }
                }
            }
        }
    }

    public List<OntologyTermDTO> getTopLevelTerms(){
        if (toplevelterms == null){
            toplevelterms = new ArrayList<>();
            for (String topLevelId : topLevelIds){
                toplevelterms.add(termMap.get(topLevelId));
            }
        }
        return toplevelterms;

    }

    /**
     * Computes paths in the format needed for TreeJs, for the ontology browsers.
     * [!] This is not computed by default. If you want the trees, call this method on the parser first.
     * @throws JSONException 
     */
    public void fillJsonTreePath(String rootId, String pathToPage,  Map<String, Integer>  countsMap, List<String> treeBrowserTopLevels, Boolean withPartOf) throws JSONException {

        OWLClass root = classMap.get(rootId);
        // fill lists with nodes on path
        fillJsonTreePath(root, new ArrayList<>(), withPartOf, rootId);
        // use node list to generate JSON documents
        for ( String id : getTermsInSlim()) {
            OntologyTermDTO  term       = getOntologyTerm(id);
            List<JSONObject> searchTree = TreeJsHelper.createTreeJson(term, pathToPage, this, countsMap, treeBrowserTopLevels);
            term.setSeachJson(searchTree.toString());
            String scrollNodeId = TreeJsHelper.getScrollTo(searchTree);
            term.setScrollToNode(scrollNodeId);
            List<JSONObject> childrenTree = TreeJsHelper.getChildrenJson(term, pathToPage, this, countsMap);
            term.setChildrenJson(childrenTree.toString());
        }
    }

    /**
     *
     * @param cls - start from root.
     */
    private void fillJsonTreePath (OWLClass cls, List<Integer> pathFromRoot, Boolean withPartOf, String rootId){

        if (cls != null && (!getIdentifierShortForm(cls).equalsIgnoreCase(rootId) || nodeTermMap.size() == 0)){ // avoid starting over from root

            int nodeId = nodeTermMap.size();
            OntologyTermDTO ontDTO = getOntologyTerm(getIdentifierShortForm(cls));

            pathFromRoot.add(nodeId);
            Set<OWLClass> childrenPartOf = getChildren(cls, withPartOf);
            ontDTO.addPathsToRoot(nodeId, new ArrayList<>(pathFromRoot));
            nodeTermMap.put(nodeId, ontDTO);

            if (childrenPartOf != null){
                for (OWLClass child: childrenPartOf){
                    fillJsonTreePath(child, new ArrayList<>(pathFromRoot), withPartOf, rootId);
                }
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
        return termMap.values().stream().collect(Collectors.toList());
    }


    /**
     * The getters by id (below) are needed until we move to an OWL API indexer. Should get rid of them and use the getters on the objects directly after that.
     */
    public OntologyTermDTO getOntologyTerm (String accessionId){
        return termMap.get(accessionId);
    }

    /**
     * Only works if you filled the nodes first
     * @param nodeId
     * @return
     */
    public OntologyTermDTO getOntologyTerm (Integer nodeId){
        return nodeTermMap.get(nodeId);
    }

    /**
     * Set up properties for parsing ontology.
     * @throws OWLOntologyCreationException
     */
    private void setUpParser(String pathToOwlFile)
            throws OWLOntologyCreationException, OWLOntologyStorageException {

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
        PART_OF.add(new OWLObjectPropertyImpl(IRI.create("http://purl.obolibrary.org/obo/emapa#part_of")));
        PART_OF.add(new OWLObjectPropertyImpl(IRI.create("http://purl.obolibrary.org/obo/part_of")));
        PART_OF.add(new OWLObjectPropertyImpl(IRI.create("http://purl.obolibrary.org/obo/BFO_0000050")));

        TERM_REPLACED_BY = factory.getOWLAnnotationProperty(IRI.create("http://purl.obolibrary.org/obo/IAO_0100001"));

        ontology = setUpOntology(manager, pathToOwlFile);

        ancestorsCache = new HashMap<>();
    }

    private OWLOntology setUpOntology(OWLOntologyManager manager, String pathToOwlFile) throws OWLOntologyStorageException{

        final int MAX_RETRIES = 20;
        final int MAX_SLEEP_IN_SECONDS = 10;

        for (int i = 0; i <= MAX_RETRIES; i++) {

            try {
                return manager.loadOntologyFromOntologyDocument(IRI.create(new File(pathToOwlFile)));

            } catch (Exception e) {

                if (i < MAX_RETRIES) {
                    // Pause a random number of milliseconds.
                    Random random       = new Random();
                    long   milliseconds = new Double(random.nextInt(MAX_SLEEP_IN_SECONDS * 1000)).longValue();
                    logger.info("OntologyParser: PATH TO OWLFILE: {}. Retry attempt #{}. Sleeping for {} milliseconds.", pathToOwlFile, i, milliseconds);

                    try {
                        Thread.sleep(milliseconds);
                    } catch (Exception ee) {
                    }
                }
            }
        }

        throw new OWLOntologyStorageException("Maximum Retries exceeded. Unable to load ontology file " + pathToOwlFile);
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

        Set<OWLClass> subclasses = getChildren(cls, true);
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
    protected String getIdentifierShortForm (OWLClass cls){
        return cls.getIRI().getShortForm().replace("_", ":");
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

    private OntologyTermDTO getDTO(OWLClass cls, String prefix){

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
        addChildrenInfo(cls, term, prefix);
        addParentInfo(cls, term, prefix);
        addIntermediateInfo(cls, term, prefix);
        addTopLevelInfo(cls, term, prefix);

        return term;
    }


    /**
     *
     * @param cls
     * @return Set of equivalent *named* classes.
     */
    private Set<OntologyTermDTO> getEquivaletNamedClasses(OWLClass cls, String prefix){

        Set<OntologyTermDTO> eqClasses = new HashSet<>();
        for (OWLClassExpression classExpression : EntitySearcher.getEquivalentClasses(cls, ontology)){
            if (classExpression.isClassExpressionLiteral() && !getIdentifierShortForm(classExpression.asOWLClass()).startsWith(prefix + ":")){
                eqClasses.add(getDTO(classExpression.asOWLClass(), prefix));
            }
        }

        return  eqClasses;
    }

    public Set<String> getReferencedClasses(String clsId, Set<OWLObjectPropertyImpl> viaProperties, String prefixOfReferrencedClass){

        Set<OWLClass> res = new HashSet<>();

        if (classMap.containsKey(clsId)) {
            OWLClass cls = classMap.get(clsId);
            // get both equivalent classes and subclass of
            Collection<OWLClassExpression> expressions = EntitySearcher.getEquivalentClasses(cls, ontology);
            expressions.addAll(EntitySearcher.getSuperClasses(cls, ontology));
            Collection<OWLClassExpression> expressionsFromIntersection = new HashSet<>();

            for (OWLClassExpression classExpression : expressions) {
                // Most likely case, something like PATO_0000051 and ('inheres in' some MA_0000195) and (qualifier some PATO_0000460)
                // Treat this case first as it adds new expressions to the list processed in the nex
                if (classExpression instanceof OWLObjectIntersectionOf) {
                    OWLObjectIntersectionOf intersection = (OWLObjectIntersectionOf) classExpression;
                    // break down into simple class expressions and dd them to the set to be analysed
                    expressionsFromIntersection.addAll(intersection.asConjunctSet());
                }
            }
            expressions.addAll(expressionsFromIntersection);
            for (OWLClassExpression classExpression : expressions) {
                // Simplest case, something like ('inheres in' some MA_0000195)
                if (classExpression instanceof OWLObjectSomeValuesFrom){
                    OWLObjectSomeValuesFrom svf = (OWLObjectSomeValuesFrom) classExpression;
                    if (viaProperties.contains(svf.getProperty().asOWLObjectProperty())){
                        if (svf.getFiller() instanceof OWLNamedObject){
                            res.add(svf.getFiller().asOWLClass());
                        }
                    }
                }
            }
        }

        // Return the ids, but filter for the right prefix
        return  res.stream().filter(cls -> {return startsWithPrefix(cls, prefixOfReferrencedClass);})
            .map(cls -> {return getIdentifierShortForm(cls);}).collect(Collectors.toSet());
    }

    public Set<String> getTermsInSlim(){

        return termsInSlim;
    }

    /**
     *
     * @param wantedIDs
     * @param prefix
     * @return Set of class ids that belond in the slim
     * @throws IOException
     * @throws OWLOntologyStorageException
     */
    protected Set<String> getTermsInSlim(Set<String> wantedIDs, String prefix)
            throws IOException, OWLOntologyStorageException {

        // Cache it in termsInSlim so we don't have to re-compute this every time
        if (termsInSlim != null){
            return termsInSlim;
        }

        // Add replacement ids for deprecated classes to wanted ids
        for (OWLClass cls : ontology.getClassesInSignature()) {

            // add replacement terms for obslete terms
            if (!cls.getIRI().isNothing() && EntitySearcher.getAnnotations(cls, ontology, TERM_REPLACED_BY) != null && wantedIDs.contains(getIdentifierShortForm(cls))) {
                for (OWLAnnotation annotation : EntitySearcher.getAnnotations(cls, ontology, TERM_REPLACED_BY)) {
                    if (annotation.getValue() instanceof OWLLiteral) {
                        wantedIDs.add(((OWLLiteral) annotation.getValue()).getLiteral());
                    }
                }
            }
            // remove obseolete terms from slim
            if (isObsolete(cls)) {
                wantedIDs.remove(getIdentifierShortForm(cls));
            }
        }

        // Add the "seed" terms and their ancestors to the slim set.
        Set<String> classesInSlim = new HashSet<>();
        for (OWLClass cls : ontology.getClassesInSignature()) {
            if (wantedIDs.contains(getIdentifierShortForm(cls))) {
                if (startsWithPrefix(cls, prefix)) {
                    classesInSlim.add(getIdentifierShortForm(cls));
                    classesInSlim.addAll(getClassAncestors(cls, prefix).stream().map(item -> {return getIdentifierShortForm(item);}).collect(Collectors.toSet()));
                }
            }
        }

        termsInSlim = classesInSlim;
        logger.debug("Set termsInSlim " + termsInSlim.size());

        return classesInSlim;
    }

    protected OWLClass getOwlClass(String shortFormId){
        return classMap.get(shortFormId);
    }

    /**
     *
     * @param cls
     * @param prefix
     * @return
     */
    private Set<OWLClass> getClassAncestors(OWLClass cls, String prefix){

        if( ancestorsCache.containsKey(getIdentifierShortForm(cls))){
            return ancestorsCache.get(getIdentifierShortForm(cls));
        }
        Set<OWLClass> ancestorIds = new HashSet<>();
        Set<String> usedIds = new HashSet<>();
        ancestorIds.addAll(getClassAncestors(cls, prefix, ancestorIds, usedIds));
        ancestorsCache.put(getIdentifierShortForm(cls), ancestorIds);

        return ancestorIds;
    }

    protected String getXref(OWLClass cls,  String prefixOfCrossRef) {

        if (!cls.getIRI().isNothing() && EntitySearcher.getAnnotations(cls, ontology, X_REF) != null) {
            for (OWLAnnotation annotation : EntitySearcher.getAnnotations(cls, ontology, X_REF)) {
                if (annotation.getValue() instanceof OWLLiteral) {
                    OWLLiteral val = (OWLLiteral) annotation.getValue();
                    String id = val.getLiteral().replace(":", "_");
                    if (id.startsWith(prefixOfCrossRef + "_")) {
                        return id;
                    }
                }
            }
        }

        return null;
    }

    // TODO run reasoner on ontology first
    /**
     * This is the recursive method, use the other one as it caches results too
     * @param cls
     * @param prefix
     * @param ancestorIds
     * @return
     */
    private Set<OWLClass> getClassAncestors(OWLClass cls, String prefix, Set<OWLClass> ancestorIds, Set<String> usedIds){

        if (!usedIds.contains(getIdentifierShortForm(cls))){
            usedIds.add(getIdentifierShortForm(cls));
            Collection<OWLClassExpression> superClasses = EntitySearcher.getSuperClasses(cls, ontology);
            //Set<OWLClass> eqReason = r.getEquivalentClasses(cls).getFlattened();
            for(OWLClassExpression superClass : superClasses){
                if (superClass.isClassExpressionLiteral()){
                    if (startsWithPrefix(superClass.asOWLClass(), prefix)) {
                        ancestorIds.add(superClass.asOWLClass());
                        getClassAncestors(superClass.asOWLClass(), prefix, ancestorIds, usedIds);
                    }
                } else {
                    if (superClass instanceof OWLObjectSomeValuesFrom){
                        OWLObjectSomeValuesFrom svf = (OWLObjectSomeValuesFrom) superClass;
                        if (PART_OF.contains(svf.getProperty().asOWLObjectProperty())){
                            if (svf.getFiller() instanceof OWLNamedObject){
                                if (startsWithPrefix(svf.getFiller().asOWLClass(), prefix)) {
                                    ancestorIds.add(svf.getFiller().asOWLClass());
                                    getClassAncestors(svf.getFiller().asOWLClass(), prefix, ancestorIds, usedIds);
                                }
                            }
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
    private void addChildrenInfo(OWLClass cls, OntologyTermDTO term, String prefix){

        Set<OWLClass> children = getChildren(cls, true);
        if (termsInSlim != null){
            // Filter out child terms not in slim or with another prefix
            children = children.stream().filter(termCls -> { return termsInSlim.contains(getIdentifierShortForm(termCls)) || !startsWithPrefix(termCls, prefix);}).collect(Collectors.toSet());
        }
        for (OWLClass child : children){
            term.addChildId(getIdentifierShortForm(child.asOWLClass()));
            term.addChildName(getLabel(child.asOWLClass()));
        }
    }

    private void addTopLevelInfo (OWLClass cls, OntologyTermDTO term, String prefix){

        Set<OWLClass> classAncestors = getClassAncestors(cls, prefix);
        if (classAncestors != null && topLevelIds != null) {
            // Intersect list of ancestors with list of top Levels
            Set<OWLClass> localTopLevels = classAncestors.stream()
                    .filter(item -> {
                        return topLevelIds.contains(getIdentifierShortForm(item)); // filter out ancestors that are not top levels
                    }).collect(Collectors.toSet());
            for (OWLClass topLevel : localTopLevels) {
                term.addTopLevelId(getIdentifierShortForm(topLevel));
                term.addTopLevelName(getLabel(topLevel));
                term.addTopLevelSynonym(getSynonyms(topLevel));
                term.addTopLevelTermIdsConcatenated(getLabel(topLevel), getIdentifierShortForm(topLevel));
            }
        }
    }

    /**
     * [!] At the moment this adds ancestors - topLevels . So it can adds terms on top of the higher level too.
     * @param cls
     * @param term
     * @return
     */
    private void addIntermediateInfo(OWLClass cls, OntologyTermDTO term, String prefix ){

        Set<OWLClass> classAncestors = getClassAncestors(cls, prefix);
        if (classAncestors != null) {
            Set<OWLClass> intermediates = classAncestors;
            if (topLevelIds != null){
                // Remove top levels from ancestors list and terms with the wrong prefix
                // Intersect list of ancestors with list of top Levels
                intermediates = classAncestors.stream()
                        .filter(item -> {
                            return (!topLevelIds.contains(getIdentifierShortForm(item)) || !startsWithPrefix(item, prefix)) && ! getIdentifierShortForm(item).equals("MP:0000001");
                        }).collect(Collectors.toSet());
            }

            for (OWLClass intermediateTerm : intermediates) {
                term.addIntermediateIds(getIdentifierShortForm(intermediateTerm));
                term.addIntermediateNames(getLabel(intermediateTerm));
                term.addIntermediateSynonyms(getSynonyms(intermediateTerm));
            }
        }
    }


    /**
     * @param term
     * @param cls
     * @return Return the cls dto with added information about child classes: childIds and childTerms.
     */
    private void addParentInfo(OWLClass cls, OntologyTermDTO term, String prefix){

        for (OWLClass parent: getParents(cls, prefix, true)){
            term.addParentId(getIdentifierShortForm(parent));
            term.addParentName(getLabel(parent));
        }
    }

    protected Set<OWLClass> getParents(OWLClass cls, String prefix, Boolean partOfToo){

        Set<OWLClass> res = new HashSet<>();
        for (OWLClassExpression classExpression : EntitySearcher.getSuperClasses(cls, ontology)){
            if (classExpression.isClassExpressionLiteral() && startsWithPrefix(classExpression.asOWLClass(), prefix)){
                res.add(classExpression.asOWLClass());
            } else {
                if (partOfToo && classExpression instanceof OWLObjectSomeValuesFrom){
                    OWLObjectSomeValuesFrom svf = (OWLObjectSomeValuesFrom) classExpression;
                    if (PART_OF.contains(svf.getProperty().asOWLObjectProperty())){
                        OWLClassExpression filler = svf.getFiller();
                        if (filler instanceof OWLNamedObject && startsWithPrefix(filler.asOWLClass(), prefix)){
                            res.add(filler.asOWLClass());
                        }
                    }
                }
            }
        }
        return res;
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

    private Set<OWLClass> getChildren(OWLClass cls, Boolean withPartOf){

        Set<OWLClass> children = new HashSet<>();
        Collection<OWLAxiom> referencingAxioms = EntitySearcher.getReferencingAxioms(cls, ontology);

        if (withPartOf) {
            // add part of
            for (OWLAxiom axiom : referencingAxioms) {
                if (axiom.getAxiomType().equals(AxiomType.SUBCLASS_OF)) {
                    OWLSubClassOfAxiom ax = (OWLSubClassOfAxiom) axiom;
                    if (ax.getSubClass() instanceof OWLClass &&
                            !getIdentifierShortForm((OWLClass) ax.getSubClass()).equals(getIdentifierShortForm(cls)) &&
                            ax.getSuperClass() instanceof OWLObjectSomeValuesFrom) {
                        OWLObjectSomeValuesFrom svf = (OWLObjectSomeValuesFrom) ax.getSuperClass();
                        if (PART_OF.contains(svf.getProperty().asOWLObjectProperty())) {
                            if (svf.getFiller() instanceof OWLNamedObject && getIdentifierShortForm(svf.getFiller().asOWLClass()).equals(getIdentifierShortForm(cls))) {
                                children.add((OWLClass) ax.getSubClass());
                            }
                        }
                    }
                }
            }
        }

        // add simple parents (is-a)
        for ( OWLClassExpression classExpression: EntitySearcher.getSubClasses(cls, ontology)){
            if (classExpression.isClassExpressionLiteral()){
                children.add(classExpression.asOWLClass());
            }
        }

        return children;
    }
}
