package org.mousephenotype.cda.indexers.beans;

import java.util.List;

import org.mousephenotype.cda.db.beans.OntologyTermBean;
import org.mousephenotype.cda.db.dao.MpOntologyDAO;

public class OntologyTermHelperMp extends OntologyTermHelper {

	public OntologyTermHelperMp(MpOntologyDAO mpOntologyService, String id) {
	    super(mpOntologyService, id);
	}
	    
	public List<String> getAnatomyMappings(){
	    return ontologyService.getAnatomyMappings(id);
    }
	
	public OntologyTermBean getOntologyTerm(){
		return ontologyService.getTerm(id);
	}
}