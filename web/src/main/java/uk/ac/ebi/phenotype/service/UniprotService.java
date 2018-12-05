package uk.ac.ebi.phenotype.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.springframework.stereotype.Component;

import uk.ac.ebi.uniprot.jaxb.CommentType;
import uk.ac.ebi.uniprot.jaxb.DbReferenceType;
import uk.ac.ebi.uniprot.jaxb.Entry;
import uk.ac.ebi.uniprot.jaxb.PropertyType;
import uk.ac.ebi.uniprot.jaxb.Uniprot;

/**
 * @since /10/2015
 * @author ilinca
 *
 */
@Component
public class UniprotService {

	
	/**
	 * @author ilinca
	 * @since 14/10/2015
	 * @return dto to populate geneSummary page with uniprot info
	 * @throws IOException 
	 * @throws JAXBException 
	 */
	public UniprotDTO getUniprotData(GeneDTO gene) throws JAXBException, IOException{
		
		String id = gene.getUniprotHumanCanonicalAcc();
	    UniprotDTO dto = new UniprotDTO();

	    if (id != null){ // not all genes have a human entry in uniprot
	    	dto = readXml("https://www.uniprot.org/uniprot/" + id + ".xml", dto);
	    }
	    
	    return dto;
	}
	
	
	/**
	 * @author ilinca
	 * @since 14/10/2015
	 * @param xml
	 * @param dto
	 * @return
	 * @throws JAXBException
	 * @throws IOException
	 */
	public UniprotDTO readXml(String xml, UniprotDTO dto) 
	throws JAXBException, IOException{
		
		JAXBContext context = JAXBContext.newInstance(Uniprot.class);
	    URL url = new URL(xml);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/xml");
		Unmarshaller u = context.createUnmarshaller();
		InputStream conn = connection.getInputStream();
	    Uniprot uniprot = (Uniprot) u.unmarshal(conn);
	    
	    for (Entry entry: uniprot.getEntry()){
		    for (CommentType comment : entry.getComment()){
		    	if (comment.getType().equals("function")){
		    		dto.setFunction(comment.getText().get(0).getValue());
		    	}
		    }
	//	    dto.setName(entry.getName().get(0));
		    for (DbReferenceType dbref : entry.getDbReference()){
		    	if (dbref.getType().equalsIgnoreCase("GO")){
		    		for (PropertyType property : dbref.getProperty()){
		    			if (property.getType().equalsIgnoreCase("term")){
		    				String prop = property.getValue();
		    				if (prop.startsWith("P:")){
		    					dto.addGoProcess(prop.replaceFirst("P:", ""));
		    				} else if (prop.startsWith("C:")){
		    					dto.addGoCell(prop.replaceFirst("C:", ""));
		    				} else if (prop.startsWith("F:")){
		    					dto.addGoMolecularFunction(prop.replaceFirst("F:", ""));
		    				}
		    			}
		    		}
		    	}
		    }
	    }
	    
	    return dto;
	    
	}

	//http://www.uniprot.org/uniprot/Q6ZNJ1.xml
	
}
