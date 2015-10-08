package uk.ac.ebi.phenotype.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.stereotype.Component;

import uk.ac.ebi.uniprot.jaxb.CommentType;
import uk.ac.ebi.uniprot.jaxb.DbReferenceType;
import uk.ac.ebi.uniprot.jaxb.Entry;
import uk.ac.ebi.uniprot.jaxb.PropertyType;


@Component
public class UniprotService {

	// Do call to uniprot, ask id as param
	// stream xml
	// unmarshal xml
	
	public UniprotDTO readXml(String xml) 
	throws JAXBException, IOException{
		
		//http://www.uniprot.org/uniprot/Q6ZNJ1.xml
		JAXBContext context = JAXBContext.newInstance(Entry.class);
	    URL url = new URL(xml);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/xml");
		Unmarshaller u = context.createUnmarshaller();
		InputStream conn = connection.getInputStream();
	    Entry entry = (Entry) u.unmarshal(conn);
	    
	    UniprotDTO dto = new UniprotDTO();
	    
	    for (CommentType comment : entry.getComment()){
	    	if (comment.getType().equals("function")){
	    		dto.setFunction(comment.getText().toString());
	    	}
	    }
	    
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
	    
	    return dto;
	    
	}
	
	
}
