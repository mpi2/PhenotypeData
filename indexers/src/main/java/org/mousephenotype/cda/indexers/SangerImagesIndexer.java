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
package org.mousephenotype.cda.indexers;

import org.apache.solr.client.solrj.SolrClient;
import org.mousephenotype.cda.db.repositories.OntologyTermRepository;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.indexers.utils.IndexerMap;
import org.mousephenotype.cda.indexers.utils.MpHpCsvReader;
import org.mousephenotype.cda.indexers.utils.SangerProcedureMapper;
import org.mousephenotype.cda.owl.OntologyParser;
import org.mousephenotype.cda.owl.OntologyParserFactory;
import org.mousephenotype.cda.owl.OntologyTermDTO;
import org.mousephenotype.cda.solr.bean.GenomicFeatureBean;
import org.mousephenotype.cda.solr.service.dto.AlleleDTO;
import org.mousephenotype.cda.solr.service.dto.SangerImageDTO;
import org.mousephenotype.cda.utilities.RunStatus;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Populate the images core
 */
@EnableAutoConfiguration
public class SangerImagesIndexer extends AbstractIndexer implements CommandLineRunner {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DateTimeFormatter YMD_HMS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	private OntologyParser maParser;
	private OntologyParser mpParser;

	private Map<String, List<AlleleDTO>>    alleles;
	private Map<String, AlleleBean>         alleleMpiMap   = new HashMap<>();
	private Map<Integer, List<Annotation>>  annotationsMap = new HashMap<>();
	private Map<Integer, DcfBean>           dcfMap         = new HashMap<>();
	private Map<Integer, ExperimentDict>    expMap         = new HashMap<>();
	private Map<String, GenomicFeatureBean> featuresMap    = new HashMap<>();
	private Map<Integer, MouseBean>         mouseMvMap     = new HashMap<>();
	private Map<String, Set<String>>		mpHpTermsMap   = new HashMap<>();
	private Map<String, String>             subtypeMap     = new HashMap<>();
	private Map<String, List<String>>       synonyms       = new HashMap<>();
	private Map<Integer, List<Tag>>         tags           = new HashMap<>();

	private Set<String> mpHpMappings = new HashSet<>();

	private SolrClient alleleCore;
	private SolrClient sangerImagesCore;

	protected SangerImagesIndexer() {

	}

	@Inject
	public SangerImagesIndexer(
			@NotNull DataSource komp2DataSource,
			@NotNull OntologyTermRepository ontologyTermRepository,
			@NotNull SolrClient alleleCore,
			@NotNull SolrClient sangerImagesCore)
	{
		super(komp2DataSource, ontologyTermRepository);
		this.alleleCore = alleleCore;
		this.sangerImagesCore = sangerImagesCore;
	}


	@Override
	public RunStatus validateBuild() throws IndexerException {
		return super.validateBuild(sangerImagesCore);
	}

	@Override
	public RunStatus run() throws IndexerException {
        long count = 0;
        RunStatus runStatus = new RunStatus();
		long start = System.currentTimeMillis();

		try (Connection connection = komp2DataSource.getConnection()) {

			try {
				OntologyParserFactory ontologyParserFactory = new OntologyParserFactory(komp2DataSource, owlpath);
				mpParser = ontologyParserFactory.getMpParser();
				maParser = ontologyParserFactory.getMaParser();
				mpHpTermsMap = IndexerMap.getMpToHpTerms(owlpath + "/" + MpHpCsvReader.MP_HP_CSV_FILENAME);
				if ((mpHpTermsMap == null) || mpHpTermsMap.isEmpty()) {
					throw new IndexerException("mp-hp error: Unable to open" + owlpath + "/" + MpHpCsvReader.MP_HP_CSV_FILENAME);
				}

			} catch (OWLOntologyCreationException | OWLOntologyStorageException e) {

				System.err.println();
				e.printStackTrace();
			}

            logger.debug("Start Executing populateDcfMap " + LocalDateTime.now().format(YMD_HMS));
            populateDcfMap(connection);
            logger.debug(" Done executing populateDcfMap " + LocalDateTime.now().format(YMD_HMS));

            logger.debug("Start Executing populateMouseMv " + LocalDateTime.now().format(YMD_HMS));
            populateMouseMv(connection);
            logger.debug(" Done executing populateMouseMv " + LocalDateTime.now().format(YMD_HMS));

            logger.debug("Start Executing populateAlleleMpi " + LocalDateTime.now().format(YMD_HMS));
            populateAlleleMpi(connection);
            logger.debug(" Done executing populateAlleleMpi " + LocalDateTime.now().format(YMD_HMS));

            logger.debug("Start Executing populateSynonyms " + LocalDateTime.now().format(YMD_HMS));
            populateSynonyms(connection);
            logger.debug(" Done executing populateSynonyms " + LocalDateTime.now().format(YMD_HMS));

            logger.debug("Start Executing populateGenomicFeature2 " + LocalDateTime.now().format(YMD_HMS));
            populateGenomicFeature2(connection);
            logger.debug(" Done executing populateGenomicFeature2 " + LocalDateTime.now().format(YMD_HMS));

            logger.debug("Start Executing populateExperiments " + LocalDateTime.now().format(YMD_HMS));
            populateExperiments(connection);
            logger.debug(" Done executing populateExperiments " + LocalDateTime.now().format(YMD_HMS));

            logger.debug("Start Executing populateTAGS " + LocalDateTime.now().format(YMD_HMS));
            populateTAGS(connection);
            logger.debug(" Done executing populateTAGS " + LocalDateTime.now().format(YMD_HMS));

            logger.debug("Start Executing populateAnnotations " + LocalDateTime.now().format(YMD_HMS));
            populateAnnotations(connection);
            logger.debug(" Done executing populateAnnotations " + LocalDateTime.now().format(YMD_HMS));

            logger.debug("Start Executing populateSubType " + LocalDateTime.now().format(YMD_HMS));
            populateSubType(connection);
            logger.debug(" Done executing populateSubType " + LocalDateTime.now().format(YMD_HMS));

            logger.debug("Start Executing populateAlleles " + LocalDateTime.now().format(YMD_HMS));
            populateAlleles();
            logger.debug(" Done executing populateAlleles " + LocalDateTime.now().format(YMD_HMS));


            logger.info("Populating images core " + LocalDateTime.now().format(YMD_HMS));
            count = populateSangerImagesCore(connection, runStatus);
            logger.info("Done Populating images core " + LocalDateTime.now().format(YMD_HMS));

		} catch (Exception e) {
			e.printStackTrace();
			throw new IndexerException(e);
		} finally {
			logger.info("mpIds with Hp mappings: {}", mpHpMappings.size());

            logger.info(" Added {} total beans in {}", count, commonUtils.msToHms(System.currentTimeMillis() - start));
        }

		return runStatus;
	}

	private int populateSangerImagesCore(Connection connection, RunStatus runStatus) throws IndexerException {

		int count = 0;
        Set<String> noTopLevelSet = new HashSet<>();
        Set<String> ontologyTermNotFound = new HashSet<>();


        String query = "SELECT 'images' as dataType, IMA_IMAGE_RECORD.ID, FOREIGN_TABLE_NAME, FOREIGN_KEY_ID, ORIGINAL_FILE_NAME, CREATOR_ID, CREATED_DATE, EDITED_BY, EDIT_DATE, CHECK_NUMBER, FULL_RESOLUTION_FILE_PATH, SMALL_THUMBNAIL_FILE_PATH, LARGE_THUMBNAIL_FILE_PATH, SUBCONTEXT_ID, QC_STATUS_ID, PUBLISHED_STATUS_ID, o.name as institute, IMA_EXPERIMENT_DICT.ID as experiment_dict_id FROM IMA_IMAGE_RECORD, IMA_SUBCONTEXT, IMA_EXPERIMENT_DICT, organisation o  WHERE IMA_IMAGE_RECORD.organisation=o.id AND IMA_IMAGE_RECORD.subcontext_id=IMA_SUBCONTEXT.id AND IMA_SUBCONTEXT.experiment_dict_id=IMA_EXPERIMENT_DICT.id AND IMA_EXPERIMENT_DICT.name!='Mouse Necropsy' ";// and

		try (PreparedStatement p = connection.prepareStatement(query, java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_READ_ONLY)) {

			sangerImagesCore.deleteByQuery("*:*");

			p.setFetchSize(Integer.MIN_VALUE);
			ResultSet r = p.executeQuery();
			while (r.next()) {


                SangerImageDTO o = new SangerImageDTO();
				int imageRecordId = r.getInt("IMA_IMAGE_RECORD.ID");
				o.setId(String.valueOf(imageRecordId));
				o.setDataType(r.getString("dataType"));
				o.setFullResolutionFilePath(r.getString("FULL_RESOLUTION_FILE_PATH"));
				o.setLargeThumbnailFilePath(r.getString("LARGE_THUMBNAIL_FILE_PATH"));
				o.setOriginalFileName(r.getString("ORIGINAL_FILE_NAME"));
				o.setSmallThumbnailFilePath(r.getString("SMALL_THUMBNAIL_FILE_PATH"));
				o.setInstitute(r.getString("institute"));
				DcfBean dcfInfo = dcfMap.get(imageRecordId);
				if (dcfInfo != null) {
					o.setDcfId(dcfInfo.dcfId);
					o.setDcfExpId(dcfInfo.dcfExpId);
					o.setSangerProcedureName(dcfInfo.sangerProcedureName);
					o.setExpNameExp(Collections.singletonList(dcfInfo.sangerProcedureName + "_exp"));
					o.setSangerProcedureId(dcfInfo.sangerProcedureId);
				}
				MouseBean mb = mouseMvMap.get(r.getInt("FOREIGN_KEY_ID"));
				if (mb != null) {
					o.setAgeInWeeks(mb.ageInWeeks);
					o.setGenotypeString(mb.genotypeString);
					o.setGenotype(mb.genotype);
					AlleleBean alBean = alleleMpiMap.get(mb.genotypeString);
					o.setMouseId(mb.mouseId);
					o.setSex(mb.sex);
					o.setColonyId(mb.colonyId);
					if (alBean != null) {
						o.setAllele_accession(alBean.allele_accession);
						o.setSangerSymbol(Collections.singletonList(alBean.sangerSymbol));
						if (featuresMap.containsKey(alBean.gf_acc)) {
							GenomicFeatureBean feature = featuresMap.get(alBean.gf_acc);
							o.setSymbol(Collections.singletonList(feature.getSymbol()));
							String symbolGene = feature.getSymbol() + "_" + feature.getAccession();
							o.setAccession(feature.getAccession());
							List<String> symbolGeneList = new ArrayList<>();
							symbolGeneList.add(symbolGene);
							o.setSymbolGene(symbolGeneList);
							String subtypeKey = feature.getSubtypeAccession() + "_" + feature.getSubtypeDbId();
							if (subtypeMap.containsKey(subtypeKey)) {
								o.setSubtype(Collections.singletonList(subtypeMap.get(subtypeKey)));
							}
							o.setGeneName(Collections.singletonList(feature.getName()));
							this.populateImageDtoStatuses(o, feature.getAccession());

							if (synonyms.containsKey(feature.getAccession())) {
								List<String> syns = synonyms.get(feature.getAccession());
								o.setSynonyms(syns);
								o.setMarkerSynonym(syns);
							}
						}
					}
				}
				if (expMap.containsKey(r.getInt("ID"))) {
					ExperimentDict expBean = expMap.get(r.getInt("ID"));
					o.setExpName(Collections.singletonList(expBean.name));
					o.setSangerProcedureName(expBean.name);
					List<String> procedureList = new ArrayList<>();
					procedureList.add(SangerProcedureMapper.getImpcProcedureFromSanger(expBean.name));
					o.setProcedureName(procedureList);
				}
				if (tags.containsKey(imageRecordId)) {
					List<Tag> annotationList = tags.get(imageRecordId);
					List<String> tagNames = new ArrayList<>();
					List<String> tagValues = new ArrayList<>();
					for (Tag tag : annotationList) {

						tagNames.add(tag.tagName);
						tagValues.add(tag.tagValue);

						if (annotationsMap.containsKey(tag.tagId)) {

							List<Annotation> annotations = annotationsMap.get(tag.tagId);
							List<String> annotationTermIds = new ArrayList<>();
							List<String> annotationTermNames = new ArrayList<>();
							ArrayList<String> annotatedHigherLevelMpTermId = new ArrayList<>();
							ArrayList<String> annotatedHigherLevelMpTermName = new ArrayList<>();

							List<String> ma_ids = new ArrayList<>();
							List<String> ma_terms = new ArrayList<>();
							List<String> mp_ids = new ArrayList<>();
							List<String> mp_terms = new ArrayList<>();
							List<String> mp_term_synonyms = new ArrayList<>();
							List<String> topLevelMpTermSynonyms = new ArrayList<>();

							List<String> associatedHpTerms = new ArrayList<>();

							List<String> maTopLevelTermIds = new ArrayList<>();
							List<String> maTopLevelTerms = new ArrayList<>();
							List<String> maTermSynonyms = new ArrayList<>();
							ArrayList<String> topLevelMaTermSynonyms = new ArrayList<>();

							for (Annotation annotation : annotations) {

								annotationTermIds.add(annotation.annotationTermId);
								annotationTermNames.add(annotation.annotationTermName);

								// Accumulate MA term information
								if (annotation.ma_id != null) {

									// get info about these MA terms.
									OntologyTermDTO ma = maParser.getOntologyTerm(annotation.ma_id);
									if (ma != null) {

										ma_ids.add(ma.getAccessionId());
										ma_terms.add(ma.getName());
										maTermSynonyms.addAll(new ArrayList<>(ma.getSynonyms()));

										for (String topLevelMaId : ma.getTopLevelIds()) {
											OntologyTermDTO topLevelMaTerm = maParser.getOntologyTerm(topLevelMaId);

											if (topLevelMaTerm != null) {
												maTopLevelTermIds.add(topLevelMaTerm.getAccessionId());
												maTopLevelTerms.add(topLevelMaTerm.getName());

												topLevelMaTermSynonyms.addAll(topLevelMaTerm.getSynonyms());

											} else {
												noTopLevelSet.add(topLevelMaId);
												logger.info("Top level MA not found in ontology : " + topLevelMaId);
											}
										}

									} else {
										if (! ontologyTermNotFound.contains(annotation.ma_id)) {
											ontologyTermNotFound.add(annotation.ma_id);
											logger.info("Term not found in MA : " + annotation.ma_id);
										}
									}
								}



								// Accumulate MP term information
								if (annotation.mp_id != null) {

									// get info about these MA terms.
									OntologyTermDTO mp = mpParser.getOntologyTerm(annotation.mp_id);
									if (mp != null) {

										mp_ids.add(mp.getAccessionId());
										mp_terms.add(mp.getName());
										mp_term_synonyms.addAll(new ArrayList<>(mp.getSynonyms()));

										for (String topLevelMpId : mp.getTopLevelIds()) {
											OntologyTermDTO topLevelMpTerm = mpParser.getOntologyTerm(topLevelMpId);

											if(topLevelMpTerm != null) {
												annotatedHigherLevelMpTermId.add(topLevelMpTerm.getAccessionId());
												annotatedHigherLevelMpTermName.add(topLevelMpTerm.getName());
												topLevelMpTermSynonyms.addAll(topLevelMpTerm.getSynonyms());
											} else {
												noTopLevelSet.add(topLevelMpId);
												logger.info("Top level MP term not found in ontology : " + topLevelMpId);
											}
										}

										// TODO - update this  comment.
                                        // add mp-hp mapping using Monarch's mp-hp hybrid ontology
										Set <String> hpTermNames = mpHpTermsMap.get(annotation.mp_id);
										if (hpTermNames != null) {
											associatedHpTerms.addAll(new ArrayList<>(hpTermNames));
											mpHpMappings.add(annotation.mp_id);
										}
									} else {

										if ( ! ontologyTermNotFound.contains(annotation.mp_id)) {
											ontologyTermNotFound.add(annotation.mp_id);
											logger.info("Term not found in MP : " + annotation.mp_id);
										}

									}
								}
							}

							o.setAnnotationTermId(annotationTermIds);
							o.setAnnotationTermName(annotationTermNames);

							o.setMaId(ma_ids);
							o.setMaTerm(ma_terms);
							o.setMaTermSynonym(maTermSynonyms);
							o.setMaTopLevelTermIds(maTopLevelTermIds);
							o.setMaTopLevelTerms(maTopLevelTerms);
							o.setSelectedTopLevelMaTermSynonym(topLevelMaTermSynonyms);

							o.setMp_id(mp_ids);
							o.setMpId(mp_ids);
							o.setMpTermName(mp_terms);
							o.setMpTerm(mp_terms);
							o.setMpSyns(mp_term_synonyms);
							o.setTopLevelMpTermSynonym(topLevelMpTermSynonyms);
							o.setAnnotatedHigherLevelMpTermId(annotatedHigherLevelMpTermId);
							o.setAnnotatedHigherLevelMpTermName(annotatedHigherLevelMpTermName);

							o.setHpTerm(associatedHpTerms);
						}
					}
					o.setTagNames(tagNames);
					o.setTagValues(tagValues);
				}

				expectedDocumentCount++;
				sangerImagesCore.addBean(o, 10000);

				count += 1;
			}

            // Return warnings, if any.
            List<String> noTopLevelList = new ArrayList<>(noTopLevelSet);
			if ( ! noTopLevelList.isEmpty()) {
				Collections.sort(noTopLevelList);
				logger.info(" Top level ontology term not found for: " + String.join(", ", noTopLevelList));
			}

			List<String> noOntologyList = new ArrayList<>(ontologyTermNotFound);
			if ( ! noOntologyList.isEmpty()) {
				Collections.sort(noOntologyList);
				logger.info(" Ontology term not found for: " + String.join(", ", noOntologyList));
			}

			// Final commit to save the rest of the docs
			sangerImagesCore.commit();

		} catch (Exception e) {
			e.printStackTrace();
			runStatus.addError(" Caught error indexing Sanger images core: " + e.getLocalizedMessage() );
			throw new IndexerException(e.getMessage());
		}

        return count;
	}

	/**
	 * Add all the relevant data to the Impress map
	 *
	 * @throws SQLException
	 *             when a database exception occurs
	 */
	private void populateDcfMap(Connection connection) throws SQLException {

		List<String> queries = new ArrayList<>();
		queries.add("SELECT ir.id as id, DCF_ID, NAME, PROCEDURE_ID, EXPERIMENT_ID, MOUSE_ID FROM `IMA_DCF_IMAGE_VW` dcf, IMA_IMAGE_RECORD ir, PHN_STD_OPERATING_PROCEDURE stdOp WHERE dcf.id=ir.id and dcf.dcf_id=stdOp.id");
		for (String query : queries) {

			try (PreparedStatement p = connection.prepareStatement(query)) {

				ResultSet resultSet = p.executeQuery();

				while (resultSet.next()) {

					DcfBean b = new DcfBean();
					b.dcfId = resultSet.getString("DCF_ID");
					b.dcfExpId = resultSet.getString("EXPERIMENT_ID");
					b.sangerProcedureName = resultSet.getString("NAME");
					b.sangerProcedureId = resultSet.getInt("PROCEDURE_ID");
					dcfMap.put(resultSet.getInt("id"), b);

				}

			}
		}
	}

	/**
	 * Populate the mouse view map from the sanger database tables
	 */
	private void populateMouseMv(Connection connection) {

		String query = "select MOUSE_ID, AGE_IN_WEEKS, ALLELE, GENOTYPE, GENDER, COLONY_ID from IMPC_MOUSE_ALLELE_MV";

		try (PreparedStatement p = connection.prepareStatement(query)) {
			ResultSet resultSet = p.executeQuery();
			while (resultSet.next()) {

				MouseBean b = new MouseBean();
				b.mouseId = resultSet.getInt("MOUSE_ID");
				b.ageInWeeks = resultSet.getString("AGE_IN_WEEKS");
				b.genotypeString = resultSet.getString("ALLELE");
				b.genotype = resultSet.getString("GENOTYPE");
				b.sex = resultSet.getString("gender");
				b.colonyId = resultSet.getInt("COLONY_ID");
				mouseMvMap.put(b.mouseId, b);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void populateAlleleMpi(Connection connection) {

		String query = "select * from `allele`";

		try (PreparedStatement p = connection.prepareStatement(query)) {

			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				AlleleBean b = new AlleleBean();
				b.gf_acc = resultSet.getString("gf_acc");
				b.sangerSymbol = resultSet.getString("symbol");
				b.allele_accession = resultSet.getString("acc");
				alleleMpiMap.put(b.sangerSymbol, b);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Populate the genomic Feature map
	 */
	private void populateGenomicFeature2(Connection connection) {

		String query = "SELECT * FROM genomic_feature";

		try (PreparedStatement p = connection.prepareStatement(query)) {
			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				GenomicFeatureBean gf = new GenomicFeatureBean();
				gf.setSymbol(resultSet.getString("symbol"));
				gf.setAccession(resultSet.getString("acc"));
				gf.setName(resultSet.getString("name"));
				gf.setSubtypeAccession(resultSet.getString("subtype_acc"));
				gf.setSubtypeDbId(resultSet.getString("subtype_db_id"));
				featuresMap.put(resultSet.getString("acc"), gf);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Populate the experiment map from the Sanger tables
	 */
	private void populateExperiments(Connection connection) {

		String query = "select IMA_IMAGE_RECORD.ID, IMA_EXPERIMENT_DICT.NAME, IMA_EXPERIMENT_DICT.DESCRIPTION, concat(IMA_EXPERIMENT_DICT.NAME,'_exp') as expName_exp FROM IMA_EXPERIMENT_DICT, IMA_SUBCONTEXT, IMA_IMAGE_RECORD where IMA_SUBCONTEXT.ID=IMA_IMAGE_RECORD.SUBCONTEXT_ID and IMA_EXPERIMENT_DICT.ID=IMA_SUBCONTEXT.EXPERIMENT_DICT_ID";

		try (PreparedStatement p = connection.prepareStatement(query)) {
			ResultSet resultSet = p.executeQuery();
			while (resultSet.next()) {
				ExperimentDict exp = new ExperimentDict();
				exp.name = resultSet.getString("NAME");
				exp.description = resultSet.getString("DESCRIPTION");
				expMap.put(resultSet.getInt("ID"), exp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class ExperimentDict {

		String name;
		String description;

	}

	/**
	 * Populate map with synonyms
	 */
	private void populateSynonyms(Connection connection) {

		String query = "SELECT * FROM synonym";

		try (PreparedStatement p = connection.prepareStatement(query)) {
			ResultSet resultSet = p.executeQuery();
			while (resultSet.next()) {
				String accession = resultSet.getString("acc");
				String symb = resultSet.getString("symbol");
				synonyms.computeIfAbsent(accession, (value) -> new ArrayList<>()).add(symb);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void populateTAGS(Connection connection) {

		String query = "select * from IMA_IMAGE_TAG";

		try (PreparedStatement p = connection.prepareStatement(query)) {

			ResultSet resultSet = p.executeQuery();
			while (resultSet.next()) {
				int irId = resultSet.getInt("IMAGE_RECORD_ID");

				Tag tag = new Tag();
				tag.tagId = resultSet.getInt("ID");
				tag.tagName = resultSet.getString("TAG_NAME");
				tag.tagValue = resultSet.getString("TAG_VALUE");

				tags.computeIfAbsent(irId, (value) -> new ArrayList<>()).add(tag);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Populate the annotations map from the Sanger database tables
	 */
	private void populateAnnotations(Connection connection) {

		String query = "select * from ANN_ANNOTATION  where FOREIGN_TABLE_NAME= 'IMA_IMAGE_TAG'";

		try (PreparedStatement p = connection.prepareStatement(query)) {
			ResultSet resultSet = p.executeQuery();
			while (resultSet.next()) {
				int id = resultSet.getInt("FOREIGN_KEY_ID");

				Annotation ann = new Annotation();
				String annotationTermId = resultSet.getString("TERM_ID");
				String annotationTermName = resultSet.getString("TERM_NAME");

				if (annotationTermId != null) {

					ann.annotationTermId = annotationTermId;
					ann.annotationTermName = annotationTermName;
					if (annotationTermId.startsWith("MA:")) {
						ann.ma_id = annotationTermId;
						ann.ma_term = annotationTermName;
					}
					if (annotationTermId.startsWith("MP:")) {
						ann.mp_id = annotationTermId;
						ann.mp_term = annotationTermName;
					}

					annotationsMap.computeIfAbsent(id, (value)->new ArrayList<>()).add(ann);

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected class Annotation {

		String annotationTermId;
		String annotationTermName;
		String ma_term;
		String ma_id;
		String mp_term;
		String mp_id;
	}

	protected class Tag {

		int tagId;
		String tagValue;
		String tagName;
	}

	private void populateSubType(Connection connection) {

		String query = "select  * from ontology_term";

		try (PreparedStatement p = connection.prepareStatement(query)) {
			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {

				String subtype = resultSet.getString("name");
				String acc = resultSet.getString("acc");
				int db_id = resultSet.getInt("db_id");
				String key = acc + "_" + db_id;
				subtypeMap.put(key, subtype);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected class AlleleBean {
		String gf_acc;
		String sangerSymbol;
		String allele_accession;
	}


	/**
	 * Internal class to act as Map value DTO for impress data
	 */
	protected class DcfBean {

		String  dcfId;
		String  dcfExpId;
		String  sangerProcedureName;
		Integer sangerProcedureId;

		@Override
		public String toString() {

			return "dcf=" + dcfId + " " + dcfExpId + " " + sangerProcedureName + " " + sangerProcedureId;
		}

	}

	protected class MouseBean {

		Integer colonyId;
		String  sex;
		String  genotype;
		Integer mouseId;
		String  ageInWeeks;
		String  genotypeString;

		@Override
		public String toString() {

			return "mouseId=" + mouseId + " " + ageInWeeks + " " + genotypeString;
		}
	}

	// need allele core mappings for status etc
	private void populateAlleles() throws IndexerException {

		alleles = IndexerMap.getGeneToAlleles(alleleCore);
	}

	private void populateImageDtoStatuses(SangerImageDTO img, String geneAccession) {

		if (alleles.containsKey(geneAccession)) {
			List<AlleleDTO> localAlleles = alleles.get(geneAccession);
			for (AlleleDTO allele : localAlleles) {
				if (allele.getMgiAccessionId() != null) {
					img.addMgiAccessionId(allele.getMgiAccessionId());
				}
				if (allele.getMarkerSymbol() != null) {
					img.addMarkerSymbol(allele.getMarkerSymbol());
				}

				if (allele.getMarkerName() != null) {
					img.addMarkerName(allele.getMarkerName());
				}
				if (allele.getMarkerSynonym() != null) {
					img.addMarkerSynonym(allele.getMarkerSynonym());
				}
				if (allele.getMarkerType() != null) {
					img.addMarkerType(allele.getMarkerType());
				}

				if (allele.getHumanGeneSymbol() != null) {
					img.addHumanGeneSymbol(allele.getHumanGeneSymbol());
				}
				if (allele.getStatus() != null) {
					img.addStatus(allele.getStatus());
				}
				if (allele.getImitsPhenotypeStarted() != null) {
					img.addImitsPhenotypeStarted(allele.getImitsPhenotypeStarted());
				}
				if (allele.getImitsPhenotypeComplete() != null) {
					img.addImitsPhenotypeComplete(allele.getImitsPhenotypeComplete());
				}
				if (allele.getImitsPhenotypeStatus() != null) {
					img.addImitsPhenotypeStatus(allele.getImitsPhenotypeStatus());
				}
				if (allele.getLegacyPhenotypeStatus() != null) {
					img.setLegacyPhenotypeStatus(allele.getLegacyPhenotypeStatus());
				}

				img.setLatestProductionCentre(allele.getLatestProductionCentre());
				img.setLatestPhenotypingCentre(allele.getLatestPhenotypingCentre());
				img.setAlleleName(allele.getAlleleName());
			}
		}
	}

	public static void main(String[] args) {

		ConfigurableApplicationContext context = new SpringApplicationBuilder(SangerImagesIndexer.class)
				.web(WebApplicationType.NONE)
				.bannerMode(Banner.Mode.OFF)
				.logStartupInfo(false)
				.run(args);

		context.close();
	}
}
