package org.mousephenotype.cda.reports.ml_report;


import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.solr.service.StatisticalResultService;
import org.mousephenotype.cda.solr.service.dto.StatisticalResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ReportGenerator {

    @Autowired
    StatisticalResultService sr;

    public static final String FILENAME = "ml_report.csv";

    public void run() throws IOException, SolrServerException {

//        long start = System.currentTimeMillis();
//
//        SolrClient solr = new HttpSolrClient(urlString);
//
//        SolrQuery q = new SolrQuery("*:*")
//                .addFilterQuery(StatisticalResultDTO.STATUS + ":Success")
//                .addFilterQuery(StatisticalResultDTO.RESOURCE_NAME + ":(IMPC OR 3i)")
//                .addField(StatisticalResultDTO.ALLELE_SYMBOL)
//                .addField(StatisticalResultDTO.COLONY_ID)
//                .addField(StatisticalResultDTO.MARKER_SYMBOL)
//                .addField(StatisticalResultDTO.ZYGOSITY)
//                .addField(StatisticalResultDTO.PHENOTYPING_CENTER)
//                .addField(StatisticalResultDTO.PARAMETER_STABLE_ID)
//                .addField(StatisticalResultDTO.PARAMETER_NAME)
//                .addField(StatisticalResultDTO.P_VALUE);
//
//        QueryResponse response = solr.query(q);
//        Long rows = response.getResults().getNumFound();
//        q.setRows(Integer.parseInt(rows.toString()));
//        System.out.println(String.format(" Getting %s rows [%s]", rows, System.currentTimeMillis()-start));
//
//        Map<RowKey, Map<String, Double>> matrixValues = new HashMap<>();
//        Set<String> allParameters = new HashSet<>();
//
//        Integer i =0 ;
//        for (StatisticalResultDTO result : solr.query(q).getBeans(StatisticalResultDTO.class)) {
//
//            if (i%50000==0) {
//                System.out.println(String.format(" %s records processed [%s]", i, System.currentTimeMillis()-start));
//            }
//            i++;
//            String parameter = result.getParameterName() + "(" + result.getParameterStableId() + ")";
//            Double pvalue = result.getpValue();
//            RowKey rowKey = new RowKey(result);
//
//            if ( ! matrixValues.containsKey(rowKey)) {
//                matrixValues.put(rowKey, new HashMap<String, Double>());
//            }
//
//            if ( ! matrixValues.get(rowKey).containsKey(parameter)) {
//
//                matrixValues.get(rowKey).put(parameter, pvalue);
//
//            } else if (pvalue < matrixValues.get(rowKey).get(parameter)) {
//                matrixValues.get(rowKey).put(parameter, pvalue);
//
//            }
//            allParameters.add(parameter.replace("\r\n", " ").replace("\n", " "));
//        }
//
//        System.out.println(String.format( " Found %s rows", matrixValues.keySet().size()));
//
//        List<String> sortedParameters = new ArrayList<>(allParameters);
//        Collections.sort(sortedParameters);
//        sortedParameters = Collections.unmodifiableList(sortedParameters);
//
//        System.out.println(" Parameter: " + StringUtils.join(sortedParameters, "\n Paramter: "));
//
//        File file = new File(FILENAME);
//        file.delete();
//        PrintWriter writer = new PrintWriter(FILENAME, "UTF-8");
//
//        List<String> header = new ArrayList<>();
//        header.addAll(Arrays.asList("Genotype", "ColonyId", "Gene", "Center"));
//        header.addAll(sortedParameters);
//        writer.println(StringUtils.join(header, "\t") + "\n");
//
//        i=0;
//        for (RowKey rowKey : matrixValues.keySet()) {
//
//            if (i%100==0) {
//                System.out.println(String.format(" %s rowKey records processed [%s]", i, System.currentTimeMillis()-start));
//            }
//            i++;
//
//            List<String> row = new ArrayList<>();
//            row.add(rowKey.genotype);
//            row.add(rowKey.colonyId);
//            row.add(rowKey.markerSymbol);
//            row.add(rowKey.center);
//
//            for (String param : sortedParameters) {
//                if (matrixValues.get(rowKey).containsKey(param)) {
//                    row.add(matrixValues.get(rowKey).get(param).toString());
//                } else {
//                    row.add("");
//                }
//            }
//
//            writer.println(StringUtils.join(row, "\t"));
//
//        }
//
//        writer.close();

    }


    private class RowKey {
        String genotype;
        String colonyId;
        String markerSymbol;
        String center;

        public RowKey(StatisticalResultDTO result) {
            this.genotype = result.getAlleleSymbol() + "-" + result.getZygosity();
            this.colonyId = result.getColonyId();
            this.markerSymbol = result.getMarkerSymbol();
            this.center = result.getPhenotypingCenter();
        }

        @Override
        public String toString() {
            return "RowKey{" +
                    "genotype='" + genotype + '\'' +
                    ", colonyId='" + colonyId + '\'' +
                    ", markerSymbol='" + markerSymbol + '\'' +
                    ", center='" + center + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RowKey rowKey = (RowKey) o;

            if (genotype != null ? !genotype.equals(rowKey.genotype) : rowKey.genotype != null) return false;
            if (colonyId != null ? !colonyId.equals(rowKey.colonyId) : rowKey.colonyId != null) return false;
            if (markerSymbol != null ? !markerSymbol.equals(rowKey.markerSymbol) : rowKey.markerSymbol != null)
                return false;
            return !(center != null ? !center.equals(rowKey.center) : rowKey.center != null);

        }

        @Override
        public int hashCode() {
            int result = genotype != null ? genotype.hashCode() : 0;
            result = 31 * result + (colonyId != null ? colonyId.hashCode() : 0);
            result = 31 * result + (markerSymbol != null ? markerSymbol.hashCode() : 0);
            result = 31 * result + (center != null ? center.hashCode() : 0);
            return result;
        }
    }
}
