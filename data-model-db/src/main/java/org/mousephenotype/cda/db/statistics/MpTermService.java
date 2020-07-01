package org.mousephenotype.cda.db.statistics;

import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.db.repositories.OntologyTermRepository;
import org.mousephenotype.cda.db.repositories.ParameterRepository;
import org.mousephenotype.cda.enumerations.SexType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MpTermService returns terms and options for IMPReSS parameters
 */
@Component
public class MpTermService {
	private static final Logger logger = LoggerFactory.getLogger(MpTermService.class);

    private static final Map<String, MultiKeyMap> parameterCache = new ConcurrentHashMap<>();

	private Set<String> sexSpecificParameters = null;

    // 0.05 threshold per West, Welch and Galecki (see PhenStat documentation)
    private static final float BASE_SIGNIFICANCE_THRESHOLD = 0.05f;

    private OntologyTermRepository ontologyTermRepository;
    private ParameterRepository parameterRepository;

	@Inject
    public MpTermService(
            @NotNull OntologyTermRepository ontologyTermRepository,
            @NotNull ParameterRepository parameterRepository)
    {
		this.ontologyTermRepository = ontologyTermRepository;
		this.parameterRepository = parameterRepository;
    }

    public Map<String, OntologyTerm> getAllOptionsForParameter(Connection connection, OntologyTermRepository ontologyTermRepository, Parameter parameter, PhenotypeAnnotationType associationType) throws SQLException {

        Map<String, OntologyTerm> categoryToTerm = new HashMap<>();

        String query = "SELECT name, ontology_acc "
            + "FROM phenotype_parameter_ontology_annotation oa "
            + "INNER JOIN phenotype_parameter_lnk_option lo ON lo.option_id=oa.option_id "
            + "INNER JOIN phenotype_parameter_option ppo ON ppo.id=lo.option_id "
            + "WHERE oa.id IN "
            + "(SELECT annotation_id "
            + "FROM phenotype_parameter_lnk_ontology_annotation "
            + "WHERE parameter_id=?) "
            + "AND event_type=?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, parameter.getId());
            statement.setString(2, associationType.name());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                categoryToTerm.put(resultSet.getString("name"), ontologyTermRepository.getByAccAndShortName(resultSet.getString("ontology_acc"), Datasource.MP));
            }
        }

        return categoryToTerm;
    }

    /**
     * Populate the direction map with keys: abnormal increased decreased
     * mapping to their respective MP terms for this parameter
     * <p/>
     * Example: PhenoAssoc (ESLIM_001_001_172)
     * | +---> Increased
     * |   +---> bilaterally straight angle
     * | +---> Decreased
     * | +---> Abnormal
     * |   +---> left nail straight angle
     * |   +---> right nail straight angle
     * |   +---> ...
     * <p/>
     *
     * @param parameterStableId the parameter stable ID in question
     * @return map of the associated directions to their MP terms
     */
    public MultiKeyMap getAnnotationTypeMap(String parameterStableId, Connection connection, OntologyTermRepository ontologyTermRepository, ParameterRepository parameterRepository) throws SQLException {

        if (parameterCache.containsKey(parameterStableId)) {
            return parameterCache.get(parameterStableId);
        }

        Parameter parameter = parameterRepository.getFirstByStableId(parameterStableId);
        MultiKeyMap multiKeyMap = new MultiKeyMap();

        synchronized (this) {
            for (ParameterOntologyAnnotation a : parameter.getAnnotations()) {

	            String sex;

	            String query = "SELECT * " +
		            "FROM phenotype_parameter p " +
		            "INNER JOIN phenotype_parameter_lnk_ontology_annotation l ON l.parameter_id = p.id " +
		            "INNER JOIN phenotype_parameter_ontology_annotation o ON l.annotation_id = o.id " +
		            "WHERE p.stable_id=? AND o.event_type=? " ;

	            if (a.getOption()!=null) {
	            	query += "AND o.option_id=?";
	            }

	            try (PreparedStatement statement = connection.prepareStatement(query)) {
		            statement.setString(1, parameter.getStableId());
		            statement.setString(2, a.getType().toString());
		            if (a.getOption()!=null) {
			            statement.setInt(3, a.getOption().getId());
		            }

		            ResultSet resultSet = statement.executeQuery();
                    while (resultSet.next()) {

                        sex = resultSet.getString("sex");
                        if (sex == null) { sex = ""; }

                        if (a.getOption() != null) {

                            Map<String, OntologyTerm> categoryToTerm = getAllOptionsForParameter(connection, ontologyTermRepository, parameter, a.getType());
                            for (String category : categoryToTerm.keySet()) {
                                multiKeyMap.put(a.getType(), category, sex, categoryToTerm.get(category));
                            }

                        } else {

                            OntologyTerm term = ontologyTermRepository.getByAccAndShortName(resultSet.getString("ontology_acc"), Datasource.MP);
                            // Parameters without options get stored with an empty string placeholder
                            multiKeyMap.put(a.getType(), "", sex, term);
                        }
                    }
                }
            }

            parameterCache.put(parameterStableId, multiKeyMap);
        }

        return multiKeyMap;
    }

	/**
	 * Return the annotation term according to the values in the result
 	 */
    public OntologyTerm getMPTerm(String parameterStableId, ResultDTO res, SexType sex, Connection connection, float SIGNIFICANCE_THRESHOLD, Boolean tryHard) throws SQLException {

        // Short circuit if requesting a sex specific annotation but the result has no data for that sex
        if (res.getCategoryA()==null && sex != null && ((sex.equals(SexType.female) && res.getFemalePvalue() == null) || (sex.equals(SexType.male) && res.getMalePvalue() == null))) {
            return null;
        }

	    if (sexSpecificParameters==null) {
		    initializeSexSpecificMap(connection);
	    }

        MultiKeyMap annotations = getAnnotationTypeMap(parameterStableId, connection, ontologyTermRepository, parameterRepository);
	    logger.debug("Annotation type map for {} is {}", parameterStableId, annotations);

	    // Categorical result
        if (res.getCategoryA() != null) {
	        return getCategoricalOntologyTerm(res, SIGNIFICANCE_THRESHOLD, annotations, sex);
        }

	    String sexString = "";
	    if (sexSpecificParameters.contains(res.getParameterStableId())) {
		    sexString = res.getSex().getName();
	    }


	    // Unidimensional and RR+ results will have genotype or sex fields populated
        if (res.getGenotypeEffectSize() != null && res.getGenotypeEffectPvalue() != null) {
	        // no sex differentiation, overall genotype effect

            // determine direction
            if (res.getGenotypeEffectSize() < 0) {
                if (annotations.containsKey(PhenotypeAnnotationType.decreased, "", sexString)) {
                    return (OntologyTerm) annotations.get(PhenotypeAnnotationType.decreased, "", sexString);
                }
            }

            if (res.getGenotypeEffectSize() > 0) {
                if (annotations.containsKey(PhenotypeAnnotationType.increased, "", sexString)) {
                    return (OntologyTerm) annotations.get(PhenotypeAnnotationType.increased, "", sexString);
                }
            }

            // Default is to return abnormal annotation
            if (annotations.containsKey(PhenotypeAnnotationType.abnormal, "", sexString)) {
                return (OntologyTerm) annotations.get(PhenotypeAnnotationType.abnormal, "", sexString);
            }

//        } else if ( (sex != null && sex.equals(SexType.female)) || (sex == null && res.getMalePvalue()==null && res.getFemalePvalue()!=null && res.getFemalePvalue()<BASE_SIGNIFICANCE_THRESHOLD)) { // female effect
        } else if ( (sex != null && sex.equals(SexType.female)) ) { // female effect

            if (res.getFemalePvalue() == null) {

                if (res.getNullTestPvalue() != null && res.getNullTestPvalue() < SIGNIFICANCE_THRESHOLD) {
                    return (OntologyTerm) annotations.get(PhenotypeAnnotationType.abnormal, "", sexString);
                }
            }

            // determine direction
            if (
                    (res.getNullTestPvalue() != null && res.getNullTestPvalue() <= SIGNIFICANCE_THRESHOLD /*&& res.getFemalePvalue() < BASE_SIGNIFICANCE_THRESHOLD*/) ||
                    (res.getNullTestPvalue() == null && res.getFemalePvalue() != null && res.getFemalePvalue() < SIGNIFICANCE_THRESHOLD) // wilcoxon
                ) {

                if (res.getFemaleEffectSize() < 0) {
                    if (annotations.containsKey(PhenotypeAnnotationType.decreased, "", sexString)) {
                        return (OntologyTerm) annotations.get(PhenotypeAnnotationType.decreased, "", sexString);
                    }
                }

                if (res.getFemaleEffectSize() > 0) {
                    if (annotations.containsKey(PhenotypeAnnotationType.increased, "", sexString)) {
                        return (OntologyTerm) annotations.get(PhenotypeAnnotationType.increased, "", sexString);
                    }
                }

                // Default is to return abnormal annotation
                if (annotations.containsKey(PhenotypeAnnotationType.abnormal, "", sexString)) {
                    return (OntologyTerm) annotations.get(PhenotypeAnnotationType.abnormal, "", sexString);
                }
            }

//        } else if ( (sex != null && sex.equals(SexType.male)) || (sex == null && res.getFemalePvalue()==null && res.getMalePvalue()!=null && res.getMalePvalue()<BASE_SIGNIFICANCE_THRESHOLD)) { // male effect
        } else if ( (sex != null && sex.equals(SexType.male)) ) { // male effect

            if (res.getNullTestPvalue() != null && res.getMalePvalue() == null) {
                if (res.getNullTestPvalue() < SIGNIFICANCE_THRESHOLD) {
                    return (OntologyTerm) annotations.get(PhenotypeAnnotationType.abnormal, "", sexString);
                }
            }

            // determine direction
            if (
                    (res.getNullTestPvalue() != null && res.getNullTestPvalue() <= SIGNIFICANCE_THRESHOLD /*&& res.getMalePvalue() < BASE_SIGNIFICANCE_THRESHOLD*/) ||
                    (res.getNullTestPvalue() == null && res.getMalePvalue() != null && res.getMalePvalue() < SIGNIFICANCE_THRESHOLD) // wilcoxon
                ) {

                if (res.getMaleEffectSize() < 0) {
                    if (annotations.containsKey(PhenotypeAnnotationType.decreased, "", sexString)) {
                        return (OntologyTerm) annotations.get(PhenotypeAnnotationType.decreased, "", sexString);
                    }
                }

                if (res.getMaleEffectSize() > 0) {
                    if (annotations.containsKey(PhenotypeAnnotationType.increased, "", sexString)) {
                        return (OntologyTerm) annotations.get(PhenotypeAnnotationType.increased, "", sexString);
                    }
                }

                // Default is to try to return abnormal annotation
                if (annotations.containsKey(PhenotypeAnnotationType.abnormal, "", sexString)) {
                    return (OntologyTerm) annotations.get(PhenotypeAnnotationType.abnormal, "", sexString);
                }

            }

        }

        // Default is, if significant,  return the abnormal association if available, else return the inferred association
        // might mean there is not a more specific increased/decreased term
//        return (res.getNullTestPvalue()!=null && res.getNullTestPvalue() <= SIGNIFICANCE_THRESHOLD ? (OntologyTerm) annotations.get(PhenotypeAnnotationType.abnormal, "", sexString) : null);


        OntologyTerm ontologyTerm = null;

	    if (tryHard) {
            if (res.getNullTestPvalue() != null && res.getNullTestPvalue() <= SIGNIFICANCE_THRESHOLD) {
                ontologyTerm = (OntologyTerm) annotations.get(PhenotypeAnnotationType.abnormal, "", sexString);
                if (ontologyTerm == null) {
                    ontologyTerm = (OntologyTerm) annotations.get(PhenotypeAnnotationType.inferred, "", sexString);
                }
            }
        }

        return ontologyTerm;
    }

	void initializeSexSpecificMap(Connection connection) throws SQLException {

		// Don't re-initialize
		if (sexSpecificParameters != null) {
			return;
		}

		sexSpecificParameters = new HashSet<>();

		String query = "SELECT stable_id " +
			"FROM phenotype_parameter p " +
			"INNER JOIN phenotype_parameter_lnk_ontology_annotation l ON l.parameter_id = p.id " +
			"INNER JOIN phenotype_parameter_ontology_annotation o ON l.annotation_id = o.id " +
			"WHERE o.sex != '' " ;

		try (PreparedStatement statement = connection.prepareStatement(query)) {
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				sexSpecificParameters.add(resultSet.getString("stable_id"));
			}
		}

	}
    private OntologyTerm getCategoricalOntologyTerm(ResultDTO res, float SIGNIFICANCE_THRESHOLD, MultiKeyMap annotations, SexType sex) {

        // Short circuit if the result object sex field is set and different from the request
        if (sex != null && sex != res.getSex()) {
            return null;
        }

        // Categorical result
        if (Double.parseDouble(res.getNullTestPvalue().toString()) <= Double.parseDouble(String.valueOf(SIGNIFICANCE_THRESHOLD)) ||
                (res.getFemalePvalue()!=null && Double.parseDouble(res.getFemalePvalue().toString()) <= Double.parseDouble(String.valueOf(SIGNIFICANCE_THRESHOLD))) ||
                (res.getMalePvalue()!=null && Double.parseDouble(res.getMalePvalue().toString()) <= Double.parseDouble(String.valueOf(SIGNIFICANCE_THRESHOLD)))
                ) {

            if ( sexSpecificParameters.contains(res.getParameterStableId())) {

                final String sexString = (sex!=null) ? sex.getName() : res.getSex().getName();

                // Return the specific category / sex map
                if (annotations.containsKey(PhenotypeAnnotationType.abnormal, res.getCategoryA(), sexString)) {
                    return (OntologyTerm) annotations.get(PhenotypeAnnotationType.abnormal, res.getCategoryA(), sexString);
                }

                // If there is no specific category -> term map, return the default abnormal term
                if (annotations.containsKey(PhenotypeAnnotationType.abnormal, "", sexString)) {
                    return (OntologyTerm) annotations.get(PhenotypeAnnotationType.abnormal, "", sexString);
                }




                // Final effort -- find any abnormal key with the shortest length category
                PhenotypeAnnotationType k0 = null;
                String k1 = null, k2 = sexString;
                List<MultiKey> l = new ArrayList<MultiKey>(annotations.keySet()) ;
                for (MultiKey k : l) {
                    if ( k.getKey(0).equals(PhenotypeAnnotationType.abnormal) && k.getKey(2).equals(sexString)) {

                        if (k2 == null || ((String) k.getKey(2)).length()<k1.length()) {
                            k0 = (PhenotypeAnnotationType) k.getKey(0);
                            k1 = (String) k.getKey(1);
                            k2 = (String) k.getKey(2);
                            logger.debug("  Associating term " + annotations.get(k.getKey(0), k.getKey(1), k.getKey(2)) + " for event " + k0 + " parameter " + res.getParameterStableId() + " for sex " + sex);
                        }

                    }
                }

                OntologyTerm term =  (OntologyTerm) annotations.get(k0, k1, k2);

                if (term == null) {
                    for (MultiKey k : l) {
                        if ( k.getKey(0).equals(PhenotypeAnnotationType.increased) && k.getKey(2).equals(sexString)) {

                            if (k2 == null || ((String) k.getKey(2)).length()<k1.length()) {
                                k0 = ((PhenotypeAnnotationType) k.getKey(0));
                                k1 = (String) k.getKey(1);
                                k2 = (String) k.getKey(2);
                                logger.debug("  Associating term " + annotations.get(k.getKey(0), k.getKey(1), k.getKey(2)) + " for event " + k0 + " parameter " + res.getParameterStableId() + " for sex " + sex);
                            }

                        }
                    }

                    return (OntologyTerm) annotations.get(k0, k1, k2);
                }

                return term;

            }

            // In general, IMPReSS associates the ABNORMAL event type to categories, but there are some instances
            // where they are associated to other types (e.g. INCREASED), so check abnormal first, but we sill
            // need to check them all until we find one
            if (annotations.containsKey(PhenotypeAnnotationType.abnormal, res.getCategoryA(), "")) {

                return (OntologyTerm) annotations.get(PhenotypeAnnotationType.abnormal, res.getCategoryA(), "");

            } else if (annotations.containsKey(PhenotypeAnnotationType.increased, res.getCategoryA(), "")) {

                return (OntologyTerm) annotations.get(PhenotypeAnnotationType.increased, res.getCategoryA(), "");

            } else if (annotations.containsKey(PhenotypeAnnotationType.decreased, res.getCategoryA(), "")) {

                return (OntologyTerm) annotations.get(PhenotypeAnnotationType.decreased, res.getCategoryA(), "");

            } else if (annotations.containsKey(PhenotypeAnnotationType.inferred, res.getCategoryA(), "")) {

                return (OntologyTerm) annotations.get(PhenotypeAnnotationType.inferred, res.getCategoryA(), "");

            }

            // Default is when there was no distinct increased or decreased
            // assertion, but the statistics
            // indicate abnormal.  IMPRESS does not always directly associate
            // an "abnormal" option
            // to the ABNORMAL event. Try to return the default ABNORMAL
            // event MP term in that case that we haven't found anything more appropriate.
            if (annotations.containsKey(PhenotypeAnnotationType.abnormal, "", "")) {
                return (OntologyTerm) annotations.get(PhenotypeAnnotationType.abnormal, "", "");
            }


            if (annotations.containsKey(PhenotypeAnnotationType.abnormal, "abnormal", "")) {
                return (OntologyTerm) annotations.get(PhenotypeAnnotationType.abnormal, "abnormal", "");
            }

            // Final effort -- find any abnormal key with the shortest length category
            PhenotypeAnnotationType k0 = null;
            String k1 = null, k2 = null;
            List<MultiKey> l = new ArrayList<MultiKey>(annotations.keySet()) ;
            for (MultiKey k : l) {
                if ( k.getKey(0).equals(PhenotypeAnnotationType.abnormal)) {

                    if (k2 == null || ((String) k.getKey(2)).length()<k1.length()) {
                        k0 = (PhenotypeAnnotationType) k.getKey(0);
                        k1 = (String) k.getKey(1);
                        k2 = (String) k.getKey(2);
                        logger.debug("  Associating term " + annotations.get(k.getKey(0), k.getKey(1), k.getKey(2)) + " for event " + k0 + " parameter " + res.getParameterStableId() + " for sex " + sex);
                    }

                }
            }

            OntologyTerm term =  (OntologyTerm) annotations.get(k0, k1, k2);

            if (term == null) {
                for (MultiKey k : l) {
                    if ( k.getKey(0).equals(PhenotypeAnnotationType.increased)) {

                        if (k2 == null || ((String) k.getKey(2)).length()<k1.length()) {
                            k0 = ((PhenotypeAnnotationType) k.getKey(0));
                            k1 = (String) k.getKey(1);
                            k2 = (String) k.getKey(2);
                            logger.debug("  Associating term " + annotations.get(k.getKey(0), k.getKey(1), k.getKey(2)) + " for event " + k0 + " parameter " + res.getParameterStableId() + " for sex " + sex);
                        }

                    }
                }

                return (OntologyTerm) annotations.get(k0, k1, k2);
            }

        }
        return null;
    }


	/**
     * Return abnormal annotation for a result
     *
     */
    public OntologyTerm getAbnormalMPTerm(String parameterStableId, ResultDTO res, Connection connection, float SIGNIFICANCE_THRESHOLD) throws SQLException {

        MultiKeyMap annotations = getAnnotationTypeMap(parameterStableId, connection, ontologyTermRepository, parameterRepository);

        if (res.getCategoryA() != null) {

            // Categorical result
            if (res.getNullTestPvalue() <= SIGNIFICANCE_THRESHOLD) {

                if (annotations.containsKey(PhenotypeAnnotationType.abnormal, res.getCategoryA())) {

                    return (OntologyTerm) annotations.get(PhenotypeAnnotationType.abnormal, res.getCategoryA());

                } else if (annotations.containsKey(PhenotypeAnnotationType.abnormal, res.getCategoryB())) {

                    return (OntologyTerm) annotations.get(PhenotypeAnnotationType.abnormal, res.getCategoryB());

                }

            }
            return null;
        }


        if (annotations.containsKey(PhenotypeAnnotationType.abnormal, "")) {
            return (OntologyTerm) annotations.get(PhenotypeAnnotationType.abnormal, "");
        }

        return (res.getNullTestPvalue()!=null && res.getNullTestPvalue() <= SIGNIFICANCE_THRESHOLD ? (OntologyTerm) annotations.get(PhenotypeAnnotationType.abnormal, "") : null);
    }

	public OntologyTerm getMPTerm(String ontologyTermId) throws SQLException {

        return ontologyTermRepository.getByAccAndShortName(ontologyTermId, Datasource.MP);
	}
}