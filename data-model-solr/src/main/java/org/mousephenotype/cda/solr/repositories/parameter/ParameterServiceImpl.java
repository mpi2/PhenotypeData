package org.mousephenotype.cda.solr.repositories.parameter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
