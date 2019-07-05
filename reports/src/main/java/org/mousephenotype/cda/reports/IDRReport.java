package org.mousephenotype.cda.reports;

import org.apache.commons.lang3.ClassUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.mousephenotype.cda.db.dao.SecondaryProjectDAO;
import org.mousephenotype.cda.reports.support.ReportException;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.MpService;
import org.mousephenotype.cda.solr.service.StatisticalResultService;
import org.mousephenotype.cda.solr.service.dto.AnatomyDTO;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.web.dto.GeneRowForHeatMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.Introspector;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by ilinca on 15/02/2017.
 */

@Component
public class IDRReport extends AbstractReport {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private StatisticalResultService statisticalResultService;

    @Autowired
    private GeneService geneService;

    @Autowired
    private MpService mpService;

    @Autowired
    private SecondaryProjectDAO secondaryProjectDAO;

    private Map<String, AnatomyDTO> anatomyMap = new HashMap<>();

    public IDRReport() {
        super();
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public void run(String[] args)
            throws ReportException, SolrServerException, IOException {

        List<GeneRowForHeatMap> geneRows = new ArrayList<>();
        List<IdrRow> data = getDataFromCsv("/Users/ilinca/Documents/temp/pharos-tcrd3.1.4.csv");
        Pattern pattern = Pattern.compile("<span>(.*?)</span>");
//        Map<String, List<IdrRow>> mapByHumanGene = data.stream().collect(Collectors.groupingBy(IdrRow::getGene));
        Map<String, List<IdrRow>> mapByHumanGene = new HashMap<>();
        for (IdrRow row : data){
            if (!mapByHumanGene.containsKey(row.getGene())){
                mapByHumanGene.put(row.getGene(), new ArrayList<>());
            }
            List<IdrRow> current = mapByHumanGene.get(row.getGene());
            current.add(row);
            mapByHumanGene.put(row.getGene(), current);
        }

        BufferedWriter out = new BufferedWriter(new FileWriter(new File("/Users/ilinca/Documents/temp/pharos-tcrd3.1.4.tsv")));
        out.write("human gene" + "\t" + "uniProt" + "\t" + "tdl" + "\t" + "targetFamily" + "\t" + "pharosUrl" + "\t" + "mouse symbol" + "\t" + "production status" + "\t" + "phenotyping status" + "\t" + "significant top level MP associations" + "\t" +"IMPC link" + "\n");

        int i = 0;
        while (i < data.size()) {
            Set<String> humanGenes = data.subList(i, Math.min(data.size(), i + 1000)).stream().map(row -> {return row.gene;}).filter(item -> item != null && !item.equals("")).collect(Collectors.toSet());
            i += 1001;

            List<SolrDocument> geneToMouseStatus = geneService.getProductionStatusForGeneSet(null, humanGenes);
            for (SolrDocument doc : geneToMouseStatus) {

                // -- mouse symbol
                String tabbed = doc.get(GeneDTO.MARKER_SYMBOL).toString() + "\t";

                // -- production status
                // Production  and phenotyping status
                Map<String, String> prod = GeneService.getStatusFromDoc(doc, "");
                String prodStatusIcons = prod.get("productionIcons");
                prodStatusIcons = prodStatusIcons.equals("") ? "<span>No</span>" : prodStatusIcons;
                // Get rid of the HTML code returned by getStatus method
                Matcher matcher = pattern.matcher(prodStatusIcons);
                while (matcher.find()) {
                    String group = matcher.group();
                    tabbed += group.replaceAll("<span>|</span>", "").replaceAll("<br>", " ") + " ";
                }
                // -- phenotyping status
                tabbed += "\t";
                String phenStatus = prod.get("phenotypingIcons");
                matcher = pattern.matcher(phenStatus);
                while (matcher.find()) {
                    String group = matcher.group();
                    tabbed += group.replaceAll("<span>|</span>|<br>", "") + " ";
                }
                tabbed += "\t";

                tabbed += doc.get(GeneDTO.TOP_LEVEL_MP_TERM);
                tabbed = tabbed.replaceAll("\\[|\\]", "");

                Set<String> currentHumanSymbols = Arrays.stream(doc.get(GeneDTO.HUMAN_GENE_SYMBOL).toString().replaceAll("\\[|\\]", "").split(",")).collect(Collectors.toSet());
                for(String gene : currentHumanSymbols){
                    gene = gene.trim();
                    if (mapByHumanGene.get(gene) == null) {
                        System.out.println("Null for " + gene + ". Don't worry, this is probably because we have 2 human genes mapped to this MGI acc but only one of them is in the list. ");
                    } else {
                        for (IdrRow row : mapByHumanGene.get(gene)) {
                            out.write( row.gene + "\t" + row.uniProt + "\t" + row.tdl + "\t" + row.targetFamily + "\t" + row.pharosUrl + "\t" + tabbed + "\n");
                        }
                    }
                }
                tabbed += "\t" + "http://www.mousephenotype.org/data/genes/" + doc.get(GeneDTO.MGI_ACCESSION_ID).toString();
            }
        }

        out.close();

    }

    private List<IdrRow> getDataFromCsv(String pathToCsvFile) throws IOException {

        List<IdrRow> data = new ArrayList<>();
        BufferedReader in = new BufferedReader(new FileReader(new File(pathToCsvFile)));
        String s = in.readLine();
        while (s != null){
            String[] current = s.split(",");
            data.add(new IdrRow(current[0], current[1], current[2], current[3], current[4]));
            s = in.readLine();
        }
        return data;

    }

    private class IdrRow {

        String uniProt;
        String gene;
        String tdl;
        String targetFamily;
        String pharosUrl;

        public IdrRow(String uniProt, String gene, String tdl, String targetFamily, String pharosUrl){

            this.uniProt = uniProt;
            this.tdl = tdl;
            this.targetFamily = targetFamily;
            this.pharosUrl = pharosUrl;
            this.gene = gene;

        }

        public String getGene(){
            return gene;
        }
    }
}