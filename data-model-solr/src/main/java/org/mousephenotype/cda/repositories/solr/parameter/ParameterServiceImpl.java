package org.mousephenotype.cda.repositories.solr.parameter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParameterServiceImpl implements ParameterService {

	@Autowired
	ParameterRepository parameterRepository;
	
	@Override
	public List<Parameter> findByStableId(String stableId){
		return parameterRepository.findByStableId(stableId);
		
	}
	
	public void setParemeterRepository(ParameterRepository parameterRepository){
		this.parameterRepository=parameterRepository;
	}
}
