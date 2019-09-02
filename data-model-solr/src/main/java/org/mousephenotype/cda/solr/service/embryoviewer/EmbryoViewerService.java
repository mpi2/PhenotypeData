package org.mousephenotype.cda.solr.service.embryoviewer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class EmbryoViewerService {

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


    public class GeneEntry {
            @JsonProperty("symbol") String symbol;
            @JsonProperty("mgiAccessionId") String mgiAccessionId;
            @JsonProperty("opt9_5") Boolean opt9_5 = false;
            @JsonProperty("microct14_5_15_5") Boolean microct14_5_15_5 = false;
            @JsonProperty("microct18_5") Boolean microct18_5 = false;
    }

}