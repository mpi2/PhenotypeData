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
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.solr.service.dto.StatisticalResultDTO;
import org.mousephenotype.cda.utilities.HttpProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
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
public class PhenotypeCenterProcedureCompletenessAllService {

	private final Logger                    logger = LoggerFactory.getLogger(this.getClass());
	private       PhenotypeCenterAllService phenotypeCenterAllService;
	private       SolrClient                statisticalResultCore;

	public PhenotypeCenterProcedureCompletenessAllService(
			PhenotypeCenterAllService phenotypeCenterAllService,
			@Qualifier("statisticalResultCore")
			SolrClient statisticalResultCore)
	{
		this.phenotypeCenterAllService = phenotypeCenterAllService;
		this.statisticalResultCore = statisticalResultCore;
	}

    private Map<String, String> procedureNamesById;
    private Map<String, String> parameterNamesById;
    private Map<String, String> topLevelMpNamesById;
    private Map<String, String> mpNamesById;

    final boolean USE_PERSIST = true;


	/**
	 * @author mrelac
	 * @return a {@List} of {@String[]} of each center's progress by Strain, suitable for display in a report
	 * @throws SolrServerException, IOException
	 */
	public List<String[]> getCentersProgressByStrainCsv() throws SolrServerException, IOException, URISyntaxException {

        procedureNamesById = phenotypeCenterAllService.getProcedureNamesById();
        parameterNamesById = phenotypeCenterAllService.getParameterNamesById();
        topLevelMpNamesById = phenotypeCenterAllService.getTopLevelMpNamesById();
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

		header.add("Procedure Ids with Parameter Status Fail");
		header.add("Procedure Ids with Parameter Status Fail Count");
		header.add("Parameter Ids with Status Fail Count");

		header.add("Procedure Ids with Parameter Status Other");
		header.add("Procedure Ids with Parameter Status Other Count");
		header.add("Parameter Ids with Status Other Count");

		results.add(header.toArray(temp));


        List<String> centers = phenotypeCenterAllService.getPhenotypeCenters();

        for (String center : centers) {
            final Set<ProcedureCompletenessDTO>      dtos         = getProcedureCompletenessDTOs(center);
            final Set<PhenotypeCenterAllServiceBean> dataByCenter = getCenterData(center);
            int centerRowCount = 0;

            logger.info("Processing center '{} - Start'", center);
            System.out.println(df.format(new Date()) + ": Processing center '" + center + "' - End.");

            for (ProcedureCompletenessDTO dto : dtos) {

                // Filter data by lifeStageName, zygosity, and colonyId.
                List<PhenotypeCenterAllServiceBean> rowData =
                        dataByCenter
                                .stream()
                                .filter(x -> x.getLifeStageName().equalsIgnoreCase(dto.getLifeStageName()))
                                .filter(x -> x.getZygosity().equalsIgnoreCase(dto.getZygosity()))
                                .filter(x -> x.getColonyId().equalsIgnoreCase(dto.getColonyId()))
                                .collect(Collectors.toList());

                // Get set of bean rows for each status type
                Set<PhenotypeCenterAllServiceBean> rowDataSuccess = rowData.stream().filter(x -> x.getStatus().equals(STATUSES.SUCCESS)).collect(Collectors.toSet());
                Set<PhenotypeCenterAllServiceBean> rowDataFailed  = rowData.stream().filter(x -> x.getStatus().equals(STATUSES.FAILED)).collect(Collectors.toSet());
                Set<PhenotypeCenterAllServiceBean> rowDataOther   = rowData.stream().filter(x -> x.getStatus().equals(STATUSES.OTHER)).collect(Collectors.toSet());


                // Aggregate procedure, parameter, topLevelMp, and mp for status = SUCCESS
                final Set<String> procedureIdsSuccess = rowDataSuccess
                        .stream()
                        .map(x -> x.getProcedureStableId())
                        .collect(Collectors.toSet());

                final Set<String> parameterIdsSuccess = rowDataSuccess
                        .stream()
                        .map(x -> x.getParameterStableId())
                        .collect(Collectors.toSet());

                // Remove null topLevelMpTermId list elements first that cause NPE when flatMap is called.
                final Set<String> topLevelMpIdsSuccess = rowDataSuccess
                        .stream()
                        .filter(x -> x.getTopLevelMpTermId() != null)
                        .collect(Collectors.toSet())
                        .stream()
                        .flatMap(x -> x.getTopLevelMpTermId()
                        .stream())
                        .collect(Collectors.toSet());

                // Remove null mpTermId elements first that cause NPE when flatMap is called.
                final Set<String> mpIdsSuccess = rowDataSuccess
                        .stream()
                        .map(x -> x.getMpTermId())
                        .filter(x -> x != null)
                        .collect(Collectors.toSet());

                // NOTE: Use List instead of Set when extracting names, as sometimes the name already exists and needs to be duplicated.
                final List<String> procedureNamesSuccess = procedureIdsSuccess
                        .stream()
                        .map(x -> {
                            return procedureNamesById.get(x);
                        }).collect(Collectors.toList());

                final List<String> parameterNamesSuccess = parameterIdsSuccess
                        .stream()
                        .map(x -> {
                            return parameterNamesById.get(x);
                        })
                        .collect(Collectors.toList());

                final List<String> topLevelMpNamesSuccess = topLevelMpIdsSuccess
                        .stream()
                        .map(x -> {
                            return topLevelMpNamesById.get(x);
                        })
                        .collect(Collectors.toList());

                final List<String> mpNamesSuccess = mpIdsSuccess
                        .stream()
                        .map(x -> {
                            return mpNamesById.get(x);
                        }).collect(Collectors.toList());


                // Aggregate procedure for status = FAILED
                final Set<String> procedureIdsFailed = rowDataFailed
                        .stream()
                        .map(x -> x.getProcedureStableId())
                        .collect(Collectors.toSet());

                // Aggregate procedure for status = OTHER
                final Set<String> procedureIdsOther = rowDataOther
                        .stream()
                        .map(x -> x.getProcedureStableId())
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
                row.add(Integer.toString(rowDataSuccess.stream().map(x -> x.getParameterStableId()).distinct().toArray().length));


                // Write FAILED aggregates
                row.add(procedureIdsFailed.stream().map(String::toString).collect(Collectors.joining(", ")));
                row.add(Integer.toString(procedureIdsFailed.size()));
                row.add(Integer.toString(rowDataFailed.stream().map(x -> x.getParameterStableId()).distinct().toArray().length));

                // Write OTHER aggregates
                row.add(procedureIdsOther.stream().map(String::toString).collect(Collectors.joining(", ")));
                row.add(Integer.toString(procedureIdsOther.size()));
                row.add(Integer.toString(rowDataOther.stream().map(x -> x.getParameterStableId()).distinct().toArray().length));

                results.add(row.toArray(temp));
                centerRowCount++;
                if (centerRowCount % 1000 == 0) {
                    System.out.println("  Wrote " + centerRowCount + " rows.");
                }
            }

            System.out.println(df.format(new Date()) + ": Processing center '" + center + "' - End. " + centerRowCount + " rows added.");
        }

		return results;
	}
SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
            .set("wt", "xslt")
            .set("tr", "pivot.xsl");



//        try {
//            QueryResponse response = statisticalResultCore.query(query);
//
//            for( PivotField pivot : response.getFacetPivot().get(facetPivotFields)) {
//                if (pivot.getPivot() != null){
//                    System.out.println();
//                    for (PivotField parameter : pivot.getPivot()) {
//                        Object[] row = {pivot.getValue().toString(), parameter.getValue().toString()};
//                        System.out.println();
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//            System.err.println("Exception: " + e.getLocalizedMessage());
//            e.printStackTrace();
//        }











//		query
//			.set("wt", "xslt")
//			.set("tr", "pivot.xsl");

		HttpProxy proxy = new HttpProxy();

		// FIXME FIXME FIXME FIXME FIXME
		// FIXME FIXME FIXME FIXME FIXME
		// FIXME FIXME FIXME FIXME FIXME
		// FIXME FIXME FIXME FIXME FIXME
		// FIXME FIXME FIXME FIXME FIXME
		// FIXME FIXME FIXME FIXME FIXME
		// FIXME FIXME FIXME FIXME FIXME
		// FIXME FIXME FIXME FIXME FIXME
		// FIXME FIXME FIXME FIXME FIXME
		URL url = new URL(SolrUtils.getBaseURL(statisticalResultCore) + "/select?" + query);
//url = new URL("http://ves-ebi-d0:8986/solr/statistical-result/select?q=*:*&rows=0&facet=true&facet.mincount=1&facet.limit=-1&facet.pivot=phenotyping_center,life_stage_name,zygosity,status,colony_id&wt=xslt&tr=pivot.xsl");
		String content = proxy.getContent(url);

//		String content = proxy.getContent(new URL(SolrUtils.getBaseURL(statisticalResultCore) + "/select?" + query));

//		return Arrays.stream(content.split("\r"))
//				.skip(1)
//				.map(ProcedureStatusDTO::new)
//				.collect(Collectors.toSet());

		Set<ProcedureCompletenessDTO> procedureParameterStatuses;
		procedureParameterStatuses = Arrays.stream(content.split("\r"))
				.skip(1)
				.map(ProcedureCompletenessDTO::new)
				.collect(Collectors.toSet());

		return procedureParameterStatuses;
	}


	public class ProcedureCompletenessDTO {
        private String lifeStageName;
        private String zygosity;
        private String colonyId;

		public ProcedureCompletenessDTO(String data) {
			List<String> fields = Arrays.asList((data.split("\","))).stream().map(x -> x.replaceAll("\"", "")).collect(Collectors.toList());

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

            FileOutputStream   fout = new FileOutputStream("/Users/mrelac/persisted_" + center.replaceAll(" ", "_"));
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

            FileInputStream   fin = new FileInputStream("/Users/mrelac/persisted_" + center.replaceAll(" ", "_"));
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

	    String filename = "/Users/mrelac/persisted_" + center.replaceAll(" ", "_");

        return Files.exists(Paths.get(filename));
    }

    private Set<PhenotypeCenterAllServiceBean> getCenterData(String center) {
        Set<PhenotypeCenterAllServiceBean> data = new HashSet<>();

        if (USE_PERSIST && isPersistedDataByCenter(center)) {

            logger.info("loading persisted data for center '{}'", center);
            data = loadDataByCenter(center);

        } else {

            try {

                data = phenotypeCenterAllService.getDataByCenter(center);
                if (USE_PERSIST) {
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