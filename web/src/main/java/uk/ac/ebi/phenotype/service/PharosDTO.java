package uk.ac.ebi.phenotype.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by ilinca on 02/02/2017.
 */


@JsonIgnoreProperties(ignoreUnknown = true)
public class PharosDTO {

    String tdl;
    String  idgfam;
    Integer idg2;
    String pageLink;

    public String getDescription() {
        // TDL class descriptions. gotten Feb 2nd, 2017 from http://juniper.health.unm.edu/tcrd/
        if (tdl != null){
            String tdlLC = tdl.toLowerCase();
            switch (tdlLC) {
                case "tclin" : return "These targets have activities in DrugCentral (ie. approved drugs) with known mechanism of action.";
                case "tchem" : return "These targets have activities in ChEMBL or DrugCentral that satisfy the activity thresholds detailed below. In some cases, targets have been manually migrated to Tchem by human curation based on small molecule activities from other sources.";
                case "tbio"  : return " These targets do not have known drug or small molecule activities that satisfy the activity thresholds detailed below AND satisfy one or more of the following criteria:<br/>" +
                        "1. target is above the cutoff criteria for Tdark <br/>" +
                        "2. target is annotated with a Gene Ontology Molecular Function or Biological Process leaf term(s) with an Experimental Evidence code<br/>" +
                        "3. target has confirmed OMIM phenotype(s)";
                case "tdark" : return "hese are targets about which virtually nothing is known. They do not have known drug or small molecule activities that satisfy the activity thresholds detailed below AND satisfy two or more of the following criteria:<br/>" +
                        "        A PubMed text-mining score from Jensen Lab < 5<br/>" +
                        "                <= 3 Gene RIFs<br/>" +
                        "<= 50 Antibodies available according to http://antibodypedia.com";
                default: return "";
            }
        }  else {
            return "PHAROS has no entry on this gene.";
        }
    }

    public String getTdl() {
        return tdl;
    }

    public void setTdl(String tdl) {
        this.tdl = tdl;
    }

    public String getIdgfam() {
        return idgfam;
    }

    public void setIdgfam(String idgfam) {
        this.idgfam = idgfam;
    }

    public Integer getIdg2() {
        return idg2;
    }

    public void setIdg2(Integer idg2) {
        this.idg2 = idg2;
    }

    public String getPageLink() {
        return pageLink;
    }

    public void setPageLink(String pageLink) {
        this.pageLink = pageLink;
    }

    @Override
    public String toString() {
        return "PharosDTO{" +
                "tdl='" + tdl + '\'' +
                ", idgfam='" + idgfam + '\'' +
                ", idg2='" + idg2 + '\'' +
                '}';
    }
}
