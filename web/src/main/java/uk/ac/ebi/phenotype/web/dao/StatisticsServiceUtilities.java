package uk.ac.ebi.phenotype.web.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mousephenotype.cda.db.pojo.StatisticalResult;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.service.dto.ExperimentDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;

import uk.ac.ebi.phenotype.stats.model.Point;
import uk.ac.ebi.phenotype.stats.model.Statistics;

public class StatisticsServiceUtilities {
	
	public static ExperimentDTO convertToExperiment(String parameterStableId, Statistics stat) {
		
		ExperimentDTO exp = new ExperimentDTO();
		exp.setAlleleAccession(stat.getAlleleAccession());
		exp.setMetadataGroup(stat.getMetaDataGroup());
		exp.setParameterStableId(parameterStableId);
		exp.setProcedureStableId(stat.getProcedureStableId());
		exp.setAlleleSymobl(stat.getAllele());
		// question will this code suffice - probably not - need to set for both stats
		// files in one experiment DTO?
		String zygosity = stat.getZygosity();// only one zygosity per stats object which we can then set for all
												// observations
		// loop over points and then asssing to observation types ??
		ZygosityType zyg = ZygosityType.valueOf(stat.getZygosity());
		Set<ZygosityType> zygs = new HashSet();
		zygs.add(zyg);
		exp.setZygosities(zygs);

		List<Point> allPoints = stat.getResult().getDetails().getPoints();
		Set<ObservationDTO> controls = new HashSet<>();
		Set<ObservationDTO> mutants = new HashSet<>();

		exp.setControls(new HashSet<ObservationDTO>());
		exp.setHomozygoteMutants(new HashSet<ObservationDTO>());
		exp.setHeterozygoteMutants(new HashSet<ObservationDTO>());
		exp.setHemizygoteMutants(new HashSet<ObservationDTO>());
		Set<SexType> sexes = new HashSet<>();

		for (Point point : allPoints) {
			ObservationDTO obs = new ObservationDTO();
			String sampleType = point.getSampleType();
			obs.setObservationType(sampleType);
			String sex = point.getSex();
			obs.setSex(sex);
			String value = point.getValue();
			obs.setDataPoint(Float.valueOf(value));

			Float bw = point.getBodyWeight();
			obs.setWeight(bw);
			obs.setSex(sex);

			sexes.add(SexType.valueOf(sex));// question are all stats files for both??? if so setting it here???
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			if (point.getDateOfExperiment() != null) {
				try {
					obs.setDateOfExperiment(dateFormat.parse(point.getDateOfExperiment()));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (sampleType.equals("control")) {
				exp.getControls().add(obs);
			} else if (sampleType.equals("experimental")) {
				obs.setZygosity(zygosity);
				if (zyg.equals(ZygosityType.heterozygote)) {
					exp.getHeterozygoteMutants().add(obs);
				} else if (ZygosityType.valueOf(obs.getZygosity()).equals(ZygosityType.homozygote)) {
					exp.getHomozygoteMutants().add(obs);
				} else if (ZygosityType.valueOf(obs.getZygosity()).equals(ZygosityType.hemizygote)) {
					exp.getHemizygoteMutants().add(obs);
				}

			}

		}
		exp.setSexes(sexes);

		//set the overview stats here
		//stat.getResult().getDetails().get
		List<? extends StatisticalResult> results = new ArrayList<>();
		StatisticalResult result = new StatisticalResult();
		//result.setStatisticalMethod();
		exp.setResults(results);
		// question - hard coding this here until hamed puts in a field of
		// observation_type in the stats file in next version...
		exp.setObservationType(ObservationType.unidimensional);
		return exp;
	}
	


}
