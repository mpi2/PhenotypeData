package org.mousephenotype.cda.indexers.utils;
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


        import java.io.File;
        import java.io.IOException;
        import java.nio.charset.StandardCharsets;
        import java.nio.file.Files;
        import java.nio.file.Path;
        import java.nio.file.Paths;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.HashSet;
        import java.util.List;
        import java.util.Set;

        import org.semanticweb.owlapi.apibinding.OWLManager;
        import org.semanticweb.owlapi.model.IRI;
        import org.semanticweb.owlapi.model.OWLAnnotation;
        import org.semanticweb.owlapi.model.OWLAnnotationProperty;
        import org.semanticweb.owlapi.model.OWLClass;
        import org.semanticweb.owlapi.model.OWLDataFactory;
        import org.semanticweb.owlapi.model.OWLLiteral;
        import org.semanticweb.owlapi.model.OWLOntology;
        import org.semanticweb.owlapi.model.OWLOntologyCreationException;
        import org.semanticweb.owlapi.model.OWLOntologyManager;
        import org.semanticweb.owlapi.model.OWLOntologyStorageException;
        import org.semanticweb.owlapi.search.EntitySearcher;


/**
 * @since 2015/08/25
 * @author tudose
 * Get MA terms for the list of UBERON, EFO and CL terms for the anatogram from arrayExpress. These mappings are to be used for expression highlighting on gene page.
 */
public class OntologyMapper {

    private static HashMap<String, Set<String>> externalToMa = new HashMap<String, Set<String>>();
    private static final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    private static final OWLDataFactory factory = manager.getOWLDataFactory();
    public static final OWLAnnotationProperty X_REF = factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#hasDbXref"));
    public static final OWLAnnotationProperty MA_DEFINITION_CITATION = factory.getOWLAnnotationProperty(IRI.create("http://www.ebi.ac.uk/efo/MA_definition_citation"));

    public enum OntologyMapperPredefinedTypes {
        MA_MP
    }
/*
    public OntologyMapper(OntologyMapperPredefinedTypes type){

        switch (type){
            case MA_MP:

                ONTOLOGY_IRI = System.getProperty("user.home") + "/phis_ontologies/mp-ext-merged.owl";
                overProperties = new ArrayList<String>();
                overProperties.add("http://purl.obolibrary.org/obo/BFO_0000052");
                overProperties.add("http://purl.obolibrary.org/obo/BFO_0000070");
                overProperties.add("http://purl.obolibrary.org/obo/mp/mp-logical-definitions#inheres_in_part_of");
                baseUrl = "http://purl.obolibrary.org/obo";
                try{
                    anatomyGraph = readOntology(System.getProperty("user.home") + "/phis_ontologies/ma.owl");
                }catch(Exception e){
                    e.printStackTrace();
                    logger.error(e.getMessage());
                }
        }
        try{
            graph = readOntology(ONTOLOGY_IRI);
        }catch(Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
        }

    }

*/

    public static void main(String[] args)
            throws OWLOntologyStorageException, OWLOntologyCreationException, IOException {

        List<String> res = new ArrayList<>();
        // From http://purl.obolibrary.org/obo/uberon/ext.owl
        fillHashesFor("/Users/tudose/Documents/ontologies/uberon.owl") ;
        fillHashesFor("/Users/tudose/Documents/ontologies/efo.owl") ;
        // From http://purl.obolibrary.org/obo/cl.owl
        fillHashesFor("/Users/tudose/Documents/ontologies/cl.owl") ;

        Path path = Paths.get("/Users/tudose/git/PhenotypeData/indexers/src/main/resources/unique_both_sex_uberon_efo.txt");
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        for (String line : lines) {
            res.add(line + "," + ((externalToMa.get(line) != null) ? externalToMa.get(line).iterator().next() : ""));
        }

        path = Paths.get("/Users/tudose/git/PhenotypeData/indexers/src/main/resources/anatogram_mappings.txt");
        Files.write(path, res);

    }

    /**
     * @since 2015/08/26
     * @author tudose
     * @param path to ontology file
     * @throws OWLOntologyStorageException
     * @throws OWLOntologyCreationException
     *
     * If more ontologies are to be added to this we might need to add other annotation properties than just Xref or MA_DEFINITION_CITATION
     */
    private static void fillHashesFor(String path)
            throws OWLOntologyStorageException, OWLOntologyCreationException{

        System.out.println("Lading: " + path);
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(IRI.create(new File(path)));
        Set<OWLClass> classesSubSet = ontology.getClassesInSignature();

        for (OWLClass cls : classesSubSet){

            if (!cls.getIRI().isNothing()){
                Set<OWLAnnotation> annotations = null;
                if (EntitySearcher.getAnnotations(cls, ontology, X_REF) != null){
                    annotations = (Set<OWLAnnotation>) EntitySearcher.getAnnotations(cls, ontology, X_REF);
                } if (EntitySearcher.getAnnotations(cls, ontology, MA_DEFINITION_CITATION) != null){
                    if (annotations == null){
                        annotations = (Set<OWLAnnotation>) EntitySearcher.getAnnotations(cls, ontology, MA_DEFINITION_CITATION);
                    } else {
                        annotations.addAll(EntitySearcher.getAnnotations(cls, ontology, MA_DEFINITION_CITATION));
                    }
                }
                if (annotations != null){
                    for (OWLAnnotation annotation : annotations) {
                        if (annotation.getValue() instanceof OWLLiteral) {
                            OWLLiteral val = (OWLLiteral) annotation.getValue();
                            String maId = val.getLiteral().replace(":", "_");
                            if (maId.startsWith("MA_")){
                                String externalId = getIdentifierShortForm(cls);
                                if (!externalToMa.containsKey(externalId)){
                                    externalToMa.put(externalId, new HashSet());
                                }
                                externalToMa.get(externalId).add(maId);
                            }
                        }
                    }
                }
            }
        }

        manager.removeOntology(ontology);
    }


    private static String getIdentifierShortForm(OWLClass cls){
        String id = cls.getIRI().toString();
        return id.split("/|#")[id.split("/|#").length-1];
    }

}
