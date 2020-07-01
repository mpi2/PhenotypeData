package org.mousephenotype.cda.solr.service.embryoviewer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.HeatmapData;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class EmbryoViewerService {
    private static int NO_DATA=0;
    private static int IMAGES_AVAILABLE=2;
    private static int IMAGES_AND_AUTOMATED_ANAYLISIS_AVAILABLE=4;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String EMBRYO_VIEWER_URL = "https://www.mousephenotype.org/embryoviewer/rest/ready";
    private static final String PROCEDURE_URL = "https://api.mousephenotype.org/impress/procedure/%s";

    private static final ObjectMapper mapper = new ObjectMapper();
    private final GeneService geneService;

    public EmbryoViewerService(GeneService geneService) {
        this.geneService = geneService;
    }

    public List<GeneEntry> getGenesEmbryoStatus() {

        Map<Integer, Procedure> procedures = new HashMap<>();

        List<GeneEntry> genes = new ArrayList<>();

        try {

            Embryo embryo = getEmbryos();

            for (Colony colony : embryo.colonies) {

                GeneEntry gene = new GeneEntry();
                gene.mgiAccessionId = colony.mgi;

                for (ProceduresParameter proceduresParameter : colony.proceduresParameters) {

                    try {

                        Integer procedureId = Integer.parseInt(proceduresParameter.procedureId);

                        if (!procedures.containsKey(procedureId)) {
                            procedures.put(procedureId, getProcedure(procedureId));
                        }

                        Procedure p = procedures.get(procedureId);
                        switch (p.name) {
                            case "OPT E9.5":
                                gene.opt9_5 = true;
                                break;
                            case "MicroCT E14.5-E15.5":
                                gene.microct14_5_15_5 = true;
                                break;
                            case "MicroCT E18.5":
                                gene.microct18_5 = true;
                                break;
                            default:
                                logger.info("Embryo REST service returned unmapped procedure {}", p.name);
                                break;
                        }

                    } catch (JAXBException | IOException e) {
                        logger.warn("Could not retreive entry for procedure ID {}", proceduresParameter.procedureId, e);
                    }
                }
                //add analysis information here for the heat map
                if(colony.hasAutomatedAnalysis){
                    gene.hasAutomatedAnalysis=true;
                    gene.analysisDownloadUrl=colony.analysisDownloadUrl;
                    gene.analysisViewUrl=colony.analysisViewUrl;
                }
                genes.add(gene);

            }

        } catch (IOException e) {
            logger.warn("Could not retrieve embryo entries", e);
        }

        // Decorate the records with gene symbol
        List<String> mgiIds = genes.stream().map(x -> x.mgiAccessionId).collect(Collectors.toList());
        try {
            final Map<String, String> genesByMgiIds = geneService
                    .getGenesByMgiIds(mgiIds, GeneDTO.MGI_ACCESSION_ID, GeneDTO.MARKER_SYMBOL)
                    .stream()
                    .collect(Collectors.toMap(GeneDTO::getMgiAccessionId, GeneDTO::getMarkerSymbol));

            for (GeneEntry entry : genes) {
                entry.symbol = genesByMgiIds.get(entry.mgiAccessionId);
            }
        } catch (SolrServerException | IOException e) {
            logger.warn("Error occurred getting gene information");
        }

        return genes;

    }

    Embryo getEmbryos() throws IOException {
        URL url = new URL(EMBRYO_VIEWER_URL);
        return mapper.readValue(url, Embryo.class);
    }

    Procedure getProcedure(Integer id) throws JAXBException, IOException {
        URL url = new URL(String.format(PROCEDURE_URL, id));
        return mapper.readValue(url, Procedure.class);
    }

    public HeatmapData getEmbryoHeatmap(){
        List<EmbryoViewerService.GeneEntry> genes = this.getGenesEmbryoStatus();
        List<String> columnList=new ArrayList<>();
        columnList.add("OPT E9.5");
        columnList.add("MicroCT E14.5-E15.5");
        columnList.add("MicroCT E18.5");
        //UMASS data column and hacks for Washington meeting
        columnList.add("UMASS Pre E9.5");
        //add rows for UMASS - hack here as code not very open for extension
        Map<String,String> umassSymbolAccessions=this.populateUmassData();
        List<String> geneList=new ArrayList<>();
        List<String> mgiAccessionsList=new ArrayList<>();
        List<List<Integer>> rows=new ArrayList<>();
        Map<String,EmbryoViewerService.GeneEntry>geneSymbolToEntryMap=new HashMap<>();
        for(EmbryoViewerService.GeneEntry gene: genes){
            geneList.add(gene.symbol);
            geneSymbolToEntryMap.put(gene.symbol, gene);
        }
        geneList.addAll(umassSymbolAccessions.keySet());//add all the genes from UMass to this set

        int numberOfImpcColumns=3;
        for(String  geneSymbol: geneList){
            ArrayList<Integer> row = new ArrayList<>();
            String mgiAccessionId="";
            if(geneSymbolToEntryMap.containsKey(geneSymbol)) {
                GeneEntry gene=geneSymbolToEntryMap.get(geneSymbol);
                mgiAccessionId=gene.mgiAccessionId;


                int typeOfData = IMAGES_AVAILABLE;//if stage is mentioned in rest service means we at least have images

                if (gene.opt9_5) {
                    row.add(IMAGES_AVAILABLE);
                } else {
                    row.add(NO_DATA);
                }
                if (gene.microct14_5_15_5) {
                    if (gene.hasAutomatedAnalysis) {
                        typeOfData = IMAGES_AND_AUTOMATED_ANAYLISIS_AVAILABLE;//used to show has automated analysis but only automated analysis for E15 data currently and need more info in rest from dcc if otherwise
                    }
                    row.add(typeOfData);
                } else {
                    row.add(NO_DATA);
                }
                if (gene.microct18_5) {
                    row.add(IMAGES_AVAILABLE);
                } else {
                    row.add(NO_DATA);
                }
            }else{
                //add dummy columns for impc data if non for this gene
                for(int i=0; i<numberOfImpcColumns;i++){
                    row.add(NO_DATA);
                }

            }
            if(umassSymbolAccessions.containsKey(geneSymbol)){

                row.add(IMAGES_AVAILABLE);
            }else{
                row.add(NO_DATA);
            }

            if(mgiAccessionId.equals("")){//if emtpy assume we need to get it from UMASS data map
               mgiAccessionId=umassSymbolAccessions.get(geneSymbol);
            }
            mgiAccessionsList.add(mgiAccessionId);
            rows.add(row);
        }



        HeatmapData heatmapData=new HeatmapData(columnList,geneList,rows);
        heatmapData.setMgiAccessions(mgiAccessionsList);
        return heatmapData;
    }


    public class GeneEntry {
            @JsonProperty("symbol") String symbol;
            @JsonProperty("mgiAccessionId") String mgiAccessionId;
            @JsonProperty("opt9_5") Boolean opt9_5 = false;
            @JsonProperty("microct14_5_15_5") Boolean microct14_5_15_5 = false;
            @JsonProperty("microct18_5") Boolean microct18_5 = false;
            @JsonProperty("has_automated_analysis") Boolean hasAutomatedAnalysis = false;
            @JsonProperty("analysis_download_url") String analysisDownloadUrl;
            @JsonProperty("analysis_view_url") String analysisViewUrl;
    }

    private Map<String, String> populateUmassData(){
        List<String> originalUmassGeneSymbols = Arrays.asList("4933427D14Rik","Actr8","Alg14","Ap2s1","Atp2b1","B4gat1","Bc052040","Bcs1l","Borcs6","Casc3","Ccdc59","Cenpo","Clpx","Dbr1","Dctn6","Ddx59","Dnaaf2","Dolk","Elof1","Exoc2","Fastkd6","Glrx3","Hlcs","Ipo11","Isca1","mars2","Mcrs1","med20","Mepce","Mrm3","Mrpl22","Mrpl3","Mrpl44","Mrps18c","Mrps22","Mrps25","mtpap","Nars2","Ndufa9","Ndufs8","Orc6","Pmpcb","Pold2","Polr1a","Polr1d","Ppp1r35","Prim1","Prpf4b","Rab11a","Ranbp2","Rbbp4","Riok1","Rpain","Sars","Sdhaf2","Ska2","Snapc2","Sptssa","Strn3","Timm22","tmx2","Tpk1","Trit1","Tubgcp4","Ube2m","Washc4","Ylpm1","Zc3h4","Zfp407","Zwint");
        System.out.println("calling decorate Gene info");
        Map<String, String> symbolAndAccession = this.getLatestGeneSymbolAndAccession(originalUmassGeneSymbols);
        System.out.println("symbolAndAccession size="+symbolAndAccession.size());
        return symbolAndAccession;
    }



    public Map<String, String> getLatestGeneSymbolAndAccession(List<String> geneSymbols){
        Map<String, String> genesByMgiIdsUmass=null;
        try {
             genesByMgiIdsUmass = geneService
                    .getGeneByGeneSymbolsOrGeneSynonyms(geneSymbols)
                    .stream()
                    .collect(Collectors.toMap(GeneDTO::getMarkerSymbol,GeneDTO::getMgiAccessionId));
//            System.out.println(genesByMgiIdsUmass);
//            for (String key : genesByMgiIdsUmass.keySet()) {
//               String newGene = genesByMgiIdsUmass.get(key);
//            }
        } catch (SolrServerException | IOException e) {
            System.out.print(e);
        }
        return genesByMgiIdsUmass;
    }

}
