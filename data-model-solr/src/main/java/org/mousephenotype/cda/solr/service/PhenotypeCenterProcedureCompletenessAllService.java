/*******************************************************************************
 * Copyright 2019 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.solr.service.dto.StatisticalResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created this new class to duplicate functionality of PhenotypeCenterProcedureCompletenessService but with dataSource
 * ALL instead of just IMPC. While it's messy to duplicate the code, it's more of a risk for the ProcedureCompletenessAllReport
 * to accidentally call the IMPC-only methods in PhenotypeCenterService and quietly and incorrectly return only IMPC data.
 * This new class, in conjunction with PhenotypeCenterAllService, allows us to remove the ProcedureCompletenessAllReport
 * dependency on an IMPC-only service.
 *
 * Hopefully, someday this entire reposts module will be replaced with something more flexible.
 */

@Service
public class PhenotypeCenterProcedureCompletenessAllService extends BasicService {

	private final Logger                    logger = LoggerFactory.getLogger(this.getClass());
	private       PhenotypeCenterAllService phenotypeCenterAllService;
	private       SolrClient                statisticalResultCore;


	@Inject
	public PhenotypeCenterProcedureCompletenessAllService(
			PhenotypeCenterAllService phenotypeCenterAllService,
			@Qualifier("statisticalResultCore")
			SolrClient statisticalResultCore)
	{
	    super();
		this.phenotypeCenterAllService = phenotypeCenterAllService;
		this.statisticalResultCore = statisticalResultCore;
	}

	public PhenotypeCenterProcedureCompletenessAllService() {
	    super();
    }


    private Map<String, String>      procedureNamesById;
    private Map<String, String>      parameterNamesById;
    private Map<String, String>      mpNamesById;
    private Map<String, Integer>     missingMpTerms       = new HashMap<>();
    private Map<String, Set<String>> missingMpTermCenters = new HashMap<>();

    // If not null, specifies the path to which the persisted PhenotypeCenterAllServiceBean class instances (one for each center) is written and read from.
    // If null, the PhenotypeCenterAllServiceBean class is not persisted.
    private final String PERSIST_PATH = null;
//    private final String PERSIST_PATH = "/Users/mrelac/persistedData/";


	/**
	 * @author mrelac
	 * @return a {@List} of {@String[]} of each center's progress by Strain, suitable for display in a report
	 * @throws SolrServerException, IOException
	 */
	public List<String[]> getCentersProgressByStrainCsv() throws SolrServerException, IOException, URISyntaxException {

        procedureNamesById = phenotypeCenterAllService.getProcedureNamesById();
        parameterNamesById = phenotypeCenterAllService.getParameterNamesById();
        mpNamesById = phenotypeCenterAllService.getMpNamesById();


		List<String[]> results = new ArrayList<>();
		String[] temp = new String[1];
		List<String> header = new ArrayList<>();

		// Report rows are unique amongst the following fields:
		header.add("Phenotyping Center");
		header.add("Colony Id");
        header.add("MGI Gene Id");
		header.add("Gene Symbol");
		header.add("MGI Allele Id");
		header.add("Allele Symbol");
		header.add("Zygosity");
		header.add("Life Stage");

		// All remaining fields are either collapsed, comma-separated collections or a single count.
		header.add("Procedure Ids with Parameter Status Success");
		header.add("Procedure Names with Parameter Status Success");
		header.add("Parameter Ids with Status Success");
		header.add("Parameter Names with Status Success");
		header.add("Top Level MP Ids with Parameter Status Success");
		header.add("Top Level MP Names with Parameter Status Success");
		header.add("MP Ids with Parameter Status Success");
		header.add("MP Names with Parameter Status Success");
		header.add("Procedure Ids with Parameter Status Success Count");
		header.add("Parameter Ids with Status Success Count");
        header.add("Top Level MP Ids with Status Success Count");
        header.add("MP Ids with Status Success Count");

		header.add("Procedure Ids with Parameter Status Fail");
		header.add("Procedure Ids with Parameter Status Fail Count");
		header.add("Parameter Ids with Status Fail Count");

		header.add("Procedure Ids with Parameter Status Other");
		header.add("Procedure Ids with Parameter Status Other Count");
		header.add("Parameter Ids with Status Other Count");

		results.add(header.toArray(temp));


        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<String> centers = phenotypeCenterAllService.getPhenotypeCenters();

        for (String center : centers) {

            final Set<ProcedureCompletenessDTO>      dtos           = getProcedureCompletenessDTOs(center);
            final Set<PhenotypeCenterAllServiceBean> dataByCenter   = getCenterData(center);
            int                                      centerRowCount = 0;

            logger.debug("Processing center '{} - Start'", center);
            logger.debug(df.format(new Date()) + ": Processing center '" + center + "' - Start.");

            for (ProcedureCompletenessDTO dto : dtos) {

                // Filter data by lifeStageName, zygosity, and colonyId.
                List<PhenotypeCenterAllServiceBean> rowData =
                        dataByCenter
                                .stream()
                                .filter(phenotypeCenterAllServiceBean -> phenotypeCenterAllServiceBean.getLifeStageName().equalsIgnoreCase(dto.getLifeStageName()))
                                .filter(phenotypeCenterAllServiceBean -> phenotypeCenterAllServiceBean.getZygosity().equalsIgnoreCase(dto.getZygosity()))
                                .filter(phenotypeCenterAllServiceBean -> phenotypeCenterAllServiceBean.getColonyId().equalsIgnoreCase(dto.getColonyId()))
                                .collect(Collectors.toList());

                // Get set of bean rows for each status type
                Set<PhenotypeCenterAllServiceBean> rowDataSuccess = rowData
                        .stream()
                        .filter(phenotypeCenterAllServiceBean -> phenotypeCenterAllServiceBean.getStatus().equals(STATUSES.SUCCESS))
                        .collect(Collectors.toSet());
                Set<PhenotypeCenterAllServiceBean> rowDataFailed  = rowData
                        .stream()
                        .filter(phenotypeCenterAllServiceBean -> phenotypeCenterAllServiceBean.getStatus().equals(STATUSES.FAILED))
                        .collect(Collectors.toSet());
                Set<PhenotypeCenterAllServiceBean> rowDataOther   = rowData
                        .stream()
                        .filter(phenotypeCenterAllServiceBean -> phenotypeCenterAllServiceBean.getStatus().equals(STATUSES.OTHER))
                        .collect(Collectors.toSet());


                // Aggregate procedure, parameter, topLevelMp, and mp for status = SUCCESS
                final List<String> procedureIdsSuccess = rowDataSuccess
                        .stream()
                        .map(phenotypeCenterAllServiceBean -> phenotypeCenterAllServiceBean.getProcedureStableId())
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());

                final List<String> parameterIdsSuccess = rowDataSuccess
                        .stream()
                        .map(phenotypeCenterAllServiceBean -> phenotypeCenterAllServiceBean.getParameterStableId())
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());

                final List<String> parameterIdsFailed = rowDataFailed
                        .stream()
                        .map(phenotypeCenterAllServiceBean -> phenotypeCenterAllServiceBean.getParameterStableId())
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());

                final List<String> parameterIdsOther = rowDataOther
                        .stream()
                        .map(phenotypeCenterAllServiceBean -> phenotypeCenterAllServiceBean.getParameterStableId())
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());

                // Remove all null mp and topLevelMP ids from the master list.
                rowDataSuccess.removeIf(phenotypeCenterAllServiceBean -> phenotypeCenterAllServiceBean.getTopLevelMpTermId() == null);
                rowDataSuccess.removeIf(phenotypeCenterAllServiceBean -> phenotypeCenterAllServiceBean.getMpTermId() == null);

                // Don't use Sets for id/name collections, as Set order is not guaranteed.
                final List<String> topLevelMpIdsSuccess = rowDataSuccess
                        .stream()
                        .flatMap(topLevelMpId -> topLevelMpId.getTopLevelMpTermId()
                            .stream())
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());

                final List<String> mpIdsSuccess = rowDataSuccess
                        .stream()
                        .map(mpId -> mpId.getMpTermId())
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());

                final List<String> procedureNamesSuccess = procedureIdsSuccess
                        .stream()
                        .map(procedureStableId -> procedureNamesById.get(procedureStableId))
                        .collect(Collectors.toList());

                final List<String> parameterNamesSuccess = parameterIdsSuccess
                        .stream()
                        .map(parameterStableId -> parameterNamesById.get(parameterStableId))
                        .collect(Collectors.toList());

                final List<String> topLevelMpNamesSuccess = topLevelMpIdsSuccess
                        .stream()
                        .map(mpId -> mpNamesById.get(mpId))
                        .collect(Collectors.toList());

                final List<String> mpNamesSuccess = mpIdsSuccess
                        .stream()
                        .map(mpId -> {
                            String mpName = mpNamesById.get(mpId);
                            if (mpName == null) {
                                Integer count = missingMpTerms.get(mpId);
                                count = (count == null ? 1 : count + 1);
                                missingMpTerms.put(mpId, count);
                                Set<String> centerList = missingMpTermCenters.get(mpId);
                                if (centerList == null) {
                                    centerList = new HashSet<>();
                                }
                                centerList.add(center);
                                missingMpTermCenters.put(mpId, centerList);
                                mpName = "MISSING FROM MP CORE";
                            }
                            return mpName;
                        })
                        .collect(Collectors.toList());


                // Aggregate procedure for status = FAILED
                final Set<String> procedureIdsFailed = rowDataFailed
                        .stream()
                        .map(phenotypeCenterAllServiceBean -> phenotypeCenterAllServiceBean.getProcedureStableId())
                        .collect(Collectors.toSet());

                // Aggregate procedure for status = OTHER
                final Set<String> procedureIdsOther = rowDataOther
                        .stream()
                        .map(phenotypeCenterAllServiceBean -> phenotypeCenterAllServiceBean.getProcedureStableId())
                        .collect(Collectors.toSet());

                // Write the data
                final List<String> row = new ArrayList<>();

                row.add(center);
                row.add(rowData.get(0).getColonyId());
                row.add(rowData.get(0).getGeneAccessionId());
                row.add(rowData.get(0).getGeneSymbol());
                row.add(rowData.get(0).getAlleleAccessionId());
                row.add(rowData.get(0).getAlleleSymbol());
                row.add(rowData.get(0).getZygosity());
                row.add(rowData.get(0).getLifeStageName());


                // Write SUCCESS aggregates
                row.add(procedureIdsSuccess.stream().map(String::toString).collect(Collectors.joining(", ")));
                row.add(procedureNamesSuccess.stream().map(String::toString).collect(Collectors.joining(", ")));

                row.add(parameterIdsSuccess.stream().map(String::toString).collect(Collectors.joining(", ")));
                row.add(parameterNamesSuccess.stream().map(String::toString).collect(Collectors.joining(", ")));

                row.add(topLevelMpIdsSuccess.stream().map(String::toString).collect(Collectors.joining(", ")));
                row.add(topLevelMpNamesSuccess.stream().map(String::toString).collect(Collectors.joining(", ")));

                row.add(mpIdsSuccess.stream().map(String::toString).collect(Collectors.joining(", ")));
                    row.add(mpNamesSuccess.stream().map(String::toString).collect(Collectors.joining(", ")));

                row.add(Integer.toString(procedureIdsSuccess.size()));
                row.add(Integer.toString(parameterIdsSuccess.size()));
                row.add(Integer.toString(topLevelMpIdsSuccess.size()));
                row.add(Integer.toString(mpIdsSuccess.size()));


                // Write FAILED aggregates
                row.add(procedureIdsFailed.stream().map(String::toString).collect(Collectors.joining(", ")));
                row.add(Integer.toString(procedureIdsFailed.size()));
                row.add(Integer.toString(parameterIdsFailed.size()));


                // Write OTHER aggregates
                row.add(procedureIdsOther.stream().map(String::toString).collect(Collectors.joining(", ")));
                row.add(Integer.toString(procedureIdsOther.size()));
                row.add(Integer.toString(parameterIdsOther.size()));


                results.add(row.toArray(temp));
                centerRowCount++;
                if (centerRowCount % 1000 == 0) {
                    logger.debug("  Wrote " + centerRowCount + " rows.");
                }
            }

            logger.debug(df.format(new Date()) + ": Processing center '" + center + "' - End. " + centerRowCount + " rows added.");
        }

        if ( ! missingMpTerms.isEmpty()) {
            for (Map.Entry<String, Integer> entry : missingMpTerms.entrySet()) {
                logger.error("Missing mp term {}. Count: {}. Centers: {}", entry.getKey(), entry.getValue(),
                             missingMpTermCenters
                                     .get(entry.getKey())
                                     .stream()
                                     .map(String::toString)
                                     .collect(Collectors.joining(", ")));
            }
        }
		return results;
	}

	/**
	 * @return a Map of report row data, indexed by center
	 * @throws SolrServerException, IOException
	 */
	public Set<ProcedureCompletenessDTO> getProcedureCompletenessDTOs(String center) throws IOException, URISyntaxException {

		// The order of these filters is critical to solr performance: put collections with fewer elements at the
		// beginning, and collections with more elements at the end.
		String facetPivotFields =
				    StatisticalResultDTO.LIFE_STAGE_NAME
			+ "," + StatisticalResultDTO.ZYGOSITY
			+ "," + StatisticalResultDTO.COLONY_ID
				;

        SolrQuery query = new SolrQuery();

        query
			.setQuery("*:*")
			.setRows(0)
			.setFacet(true)
			.setFacetMinCount(1)
			.setFacetLimit(-1)
			.addFacetPivotField(facetPivotFields)
            .addFilterQuery("phenotyping_center:\"" + center + "\"")
            .add("facet.pivot", facetPivotFields)
        ;

        StringBuilder sb = new StringBuilder();

        try {
            QueryResponse response = statisticalResultCore.query(query);

            for( PivotField lifeStagePivot : response.getFacetPivot().get(facetPivotFields)) {

                String lifeStageValue = lifeStagePivot.getValue().toString();

                for (PivotField zygosityPivot : lifeStagePivot.getPivot()) {

                    String zygosityValue = zygosityPivot.getValue().toString();

                    for (PivotField colonyIdPivot : zygosityPivot.getPivot()) {

                        String colonyIdValue = colonyIdPivot.getValue().toString();

                        sb
                                .append("\"")
                                .append(lifeStageValue)
                                .append("\",")

                                .append("\"")
                                .append(zygosityValue)
                                .append("\",")

                                .append("\"")
                                .append(colonyIdValue)
                                .append("\"\n")
                        ;
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Exception: " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        String content = sb.toString();

		Set<ProcedureCompletenessDTO> procedureParameterStatuses;
		procedureParameterStatuses = Arrays.stream(content.split("\n"))
				.map(ProcedureCompletenessDTO::new)
				.collect(Collectors.toSet());

		return procedureParameterStatuses;
	}


	public class ProcedureCompletenessDTO {
        private String lifeStageName;
        private String zygosity;
        private String colonyId;

		public ProcedureCompletenessDTO(String data) {
			List<String> fields = Arrays.asList((data.split("\",")))
                    .stream()
                    .map(x -> x.replaceAll("\"", ""))
                    .collect(Collectors.toList());

			this.lifeStageName = fields.get(0);
			this.zygosity = fields.get(1);
			this.colonyId = fields.get(2);
		}

		public String getLifeStageName() {
			return lifeStageName;
		}

		public String getZygosity() {
			return zygosity;
		}

		public String getColonyId() {
			return colonyId;
		}
	}

	enum STATUSES {
		SUCCESS,
		FAILED,
		OTHER;

        public static STATUSES getStatus(String status) {
            return
                    status.equalsIgnoreCase("Success") ? STATUSES.SUCCESS :
                            status.toLowerCase().startsWith("failed") ? STATUSES.FAILED :
                                    STATUSES.OTHER;
        }
	}
	
	private void persistDataByCenter(Set<PhenotypeCenterAllServiceBean> dataByCenter, String center) {

	    try {

	        File persistPath = new File(PERSIST_PATH);
	        if ( ! persistPath.exists()) {
	            persistPath.mkdirs();
            }

	        final String persistedFilename = PERSIST_PATH + "PhenotypeCenterAllServiceBeans_" + center.replaceAll(" ", "_");

            FileOutputStream   fout = new FileOutputStream(persistedFilename);
            OutputStream os = new BufferedOutputStream(fout);
            ObjectOutputStream oos  = new ObjectOutputStream(os);
            oos.writeObject(dataByCenter);

        } catch (Exception e) {
	        System.err.println("Exception in persistDataByCenter: " + e.getLocalizedMessage());
	        e.printStackTrace();
        }
    }

    private Set<PhenotypeCenterAllServiceBean> loadDataByCenter(String center) {

        Set<PhenotypeCenterAllServiceBean> results = new HashSet<>();

        try {

            final String persistedFilename = PERSIST_PATH + "PhenotypeCenterAllServiceBeans_" + center.replaceAll(" ", "_");
            FileInputStream   fin = new FileInputStream(persistedFilename);
            InputStream is = new BufferedInputStream(fin);
            ObjectInputStream ios = new ObjectInputStream(is);
            results = (Set<PhenotypeCenterAllServiceBean>) ios.readObject();

        } catch (Exception e) {
            System.err.println("Exception in loadDataByCenter: " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        return results;
    }

    private boolean isPersistedDataByCenter(String center) {

        final String persistedFilename = PERSIST_PATH + "PhenotypeCenterAllServiceBeans_" + center.replaceAll(" ", "_");

        return Files.exists(Paths.get(persistedFilename));
    }

    private Set<PhenotypeCenterAllServiceBean> getCenterData(String center) {
        Set<PhenotypeCenterAllServiceBean> data = new HashSet<>();

        if ((PERSIST_PATH != null) && isPersistedDataByCenter(center)) {

            logger.debug("loading persisted data for center '{}'", center);
            data = loadDataByCenter(center);

        } else {

            try {

                data = phenotypeCenterAllService.getDataByCenter(center);
                if (PERSIST_PATH != null) {
                    logger.info("persisting data for center '{}", center);
                    persistDataByCenter(data, center);
                }

            } catch (Exception e) {
                System.err.println("Exception in getCenterData: " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }

        return data;
    }
}