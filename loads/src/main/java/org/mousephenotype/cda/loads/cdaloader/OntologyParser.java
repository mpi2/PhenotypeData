package org.mousephenotype.cda.loads.cdaloader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.mousephenotype.cda.db.pojo.ConsiderId;
import org.mousephenotype.cda.db.pojo.DatasourceEntityId;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.db.pojo.Synonym;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.search.EntitySearcher;

public class OntologyParser {

	List<OntologyTerm> terms = new ArrayList<>();

	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	OWLDataFactory factory = manager.getOWLDataFactory();
	OWLOntology ontology;
	
	OWLAnnotationProperty LABEL_ANNOTATION;
	OWLAnnotationProperty ALT_ID;	
	OWLAnnotationProperty X_REF;
	OWLAnnotationProperty REPLACEMENT;
	OWLAnnotationProperty CONSIDER;
	ArrayList<OWLAnnotationProperty> IS_OBSOLETE;
	ArrayList<OWLAnnotationProperty> SYNONYM_ANNOTATION;
	ArrayList<OWLAnnotationProperty> DEFINITION_ANNOTATION;
	
	
	public OntologyParser(String pathToOwlFile, String prefix) 
	throws OWLOntologyCreationException{
		
		setUpParser(pathToOwlFile);
		terms = new ArrayList<>();
		
	    Set<OWLClass> allClasses = ontology.getClassesInSignature();
	    for (OWLClass cls : allClasses){
	    	if (prefix == null || getIdentifierShortForm(cls).startsWith(prefix + ":")){
	    		OntologyTerm term = new OntologyTerm();
	    		DatasourceEntityId id = new DatasourceEntityId();
	    		id.setAccession(getIdentifierShortForm(cls)); // i.e. MA:0100084
	    		term.setId(id);
	    		term.setName(getLabel(cls));
	    		term.setDescription(getDefinition(cls));
	    		List<Synonym> synonyms = new ArrayList<>();
	    		for (String synonym: getSynonyms(cls)){
	    			Synonym s = new Synonym();
	    			s.setSymbol(synonym);
	    			synonyms.add(s);
	    		}
	    		term.setSynonyms(synonyms);
	    		term.setIsObsolete(isObsolete(cls));
	    		if (term.getIsObsolete()){
	    			if((getReplacementId(cls) != null)){
		    			term.setReplacementAcc(getReplacementId(cls));
	    			}
	    			if(getConsiderIds(cls) != null && getConsiderIds(cls).size() > 0){
	    				term.setConsiderIds(getConsiderIds(cls));
	    			}
	    		}
	    		
	    		terms.add(term);
	    	}
	    }   
	}
	
	public List<OntologyTerm> getTerms(){
		return terms;
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
		
		ontology = manager.loadOntologyFromOntologyDocument(IRI.create(new File(pathToOwlFile)));
		
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
	private List<String> getSynonyms (OWLClass cls){
		
		List<String> synonyms = new ArrayList<>();
		
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
	private List<ConsiderId> getConsiderIds(OWLClass cls){

		Collection<OWLAnnotation> res = EntitySearcher.getAnnotations(cls, ontology, CONSIDER);
		List<ConsiderId> ids = new ArrayList<>();
		
		res.stream()
			.filter(item->(item.getValue() instanceof OWLLiteral))
			.forEach(item->ids.add(getConsiderObj(item, cls)));
		
		return ids;

	}
	
	private ConsiderId getConsiderObj(OWLAnnotation ann, OWLClass cls){
		
		ConsiderId consider = new ConsiderId();
		if (ann.getValue() instanceof OWLLiteral){
			consider.setOntologyTermAcc(getIdentifierShortForm(cls));
			consider.setAcc(((OWLLiteral) ann.getValue()).getLiteral());
		}
		return consider;
	}
}