package uk.ac.ebi.phenotype.service;

import com.fasterxml.jackson.annotation.*;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ilinca on 02/02/2017.
 */
@EnableRetry
@Service
public class PharosService {

private static final String PHAROS_URL = "https://pharos.nih.gov/idg/api/v1/targets";
    private RestTemplate restTemplate = new RestTemplate();

    @Retryable
    public PharosDTO getPharosInfo(String humanGeneSymbol){

        String url = PHAROS_URL + "/" + humanGeneSymbol;
        PharosRestDTO result = restTemplate.getForObject(url, PharosRestDTO.class);

        System.out.println(result);

        if(result != null) {

            PharosDTO pharos = getPharosDtoFromResult(result, humanGeneSymbol);

            return pharos;

        } else {
            return null;
        }
    }


    PharosDTO getPharosDtoFromResult(PharosRestDTO result, String humanGeneSymbol) {

        PharosDTO pharosDTO = new PharosDTO();

        pharosDTO.setIdgfam(result.getIdgFamily());
        pharosDTO.setPageLink("https://pharos.nih.gov/idg/targets/" + humanGeneSymbol);
        pharosDTO.setTdl(result.getIdgTDL());

        return pharosDTO;
    }



    // *** START GENERATED FROM http://www.jsonschema2pojo.org/ ****************************************

    // **
    // ** NOTE: If the classes need to be regenerated, DO NOT FORGET that the static modifier must be
    // **       added to all the constructors or this will not work.  For more infromation see
    // **       http://www.cowtowncoder.com/blog/archives/2010/08/entry_411.html
    // **


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "id",
            "version",
            "created",
            "modified",
            "deprecated",
            "name",
            "accession",
            "gene",
            "description",
            "idgFamily",
            "idgTDL",
            "novelty",
            "antibodyCount",
            "monoclonalCount",
            "pubmedCount",
            "jensenScore",
            "patentCount",
            "grantCount",
            "grantTotalCost",
            "r01Count",
            "ppiCount",
            "knowledgeAvailability",
            "pubTatorScore",
            "kind",
            "self",
            "_organism",
            "_synonyms",
            "_publications",
            "_properties",
            "_links",
            "_namespace"
    })
    public static class PharosRestDTO {

        @JsonProperty("id")
        private Integer id;
        @JsonProperty("version")
        private Integer version;
        @JsonProperty("created")
        private Long created;
        @JsonProperty("modified")
        private Long modified;
        @JsonProperty("deprecated")
        private Boolean deprecated;
        @JsonProperty("name")
        private String name;
        @JsonProperty("accession")
        private String accession;
        @JsonProperty("gene")
        private String gene;
        @JsonProperty("description")
        private String description;
        @JsonProperty("idgFamily")
        private String idgFamily;
        @JsonProperty("idgTDL")
        private String idgTDL;
        @JsonProperty("novelty")
        private Double novelty;
        @JsonProperty("antibodyCount")
        private Integer antibodyCount;
        @JsonProperty("monoclonalCount")
        private Integer monoclonalCount;
        @JsonProperty("pubmedCount")
        private Object pubmedCount;
        @JsonProperty("jensenScore")
        private Double jensenScore;
        @JsonProperty("patentCount")
        private Integer patentCount;
        @JsonProperty("grantCount")
        private Object grantCount;
        @JsonProperty("grantTotalCost")
        private Object grantTotalCost;
        @JsonProperty("r01Count")
        private Object r01Count;
        @JsonProperty("ppiCount")
        private Integer ppiCount;
        @JsonProperty("knowledgeAvailability")
        private Double knowledgeAvailability;
        @JsonProperty("pubTatorScore")
        private Double pubTatorScore;
        @JsonProperty("kind")
        private String kind;
        @JsonProperty("self")
        private String self;
        @JsonProperty("_organism")
        private Object organism;
        @JsonProperty("_synonyms")
        private Synonyms synonyms;
        @JsonProperty("_publications")
        private Publications publications;
        @JsonProperty("_properties")
        private Properties properties;
        @JsonProperty("_links")
        private Links links;
        @JsonProperty("_namespace")
        private Object namespace;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        @JsonProperty("id")
        public Integer getId() {
            return id;
        }

        @JsonProperty("id")
        public void setId(Integer id) {
            this.id = id;
        }

        public PharosRestDTO withId(Integer id) {
            this.id = id;
            return this;
        }

        @JsonProperty("version")
        public Integer getVersion() {
            return version;
        }

        @JsonProperty("version")
        public void setVersion(Integer version) {
            this.version = version;
        }

        public PharosRestDTO withVersion(Integer version) {
            this.version = version;
            return this;
        }

        @JsonProperty("created")
        public Long getCreated() {
            return created;
        }

        @JsonProperty("created")
        public void setCreated(Long created) {
            this.created = created;
        }

        public PharosRestDTO withCreated(Long created) {
            this.created = created;
            return this;
        }

        @JsonProperty("modified")
        public Long getModified() {
            return modified;
        }

        @JsonProperty("modified")
        public void setModified(Long modified) {
            this.modified = modified;
        }

        public PharosRestDTO withModified(Long modified) {
            this.modified = modified;
            return this;
        }

        @JsonProperty("deprecated")
        public Boolean getDeprecated() {
            return deprecated;
        }

        @JsonProperty("deprecated")
        public void setDeprecated(Boolean deprecated) {
            this.deprecated = deprecated;
        }

        public PharosRestDTO withDeprecated(Boolean deprecated) {
            this.deprecated = deprecated;
            return this;
        }

        @JsonProperty("name")
        public String getName() {
            return name;
        }

        @JsonProperty("name")
        public void setName(String name) {
            this.name = name;
        }

        public PharosRestDTO withName(String name) {
            this.name = name;
            return this;
        }

        @JsonProperty("accession")
        public String getAccession() {
            return accession;
        }

        @JsonProperty("accession")
        public void setAccession(String accession) {
            this.accession = accession;
        }

        public PharosRestDTO withAccession(String accession) {
            this.accession = accession;
            return this;
        }

        @JsonProperty("gene")
        public String getGene() {
            return gene;
        }

        @JsonProperty("gene")
        public void setGene(String gene) {
            this.gene = gene;
        }

        public PharosRestDTO withGene(String gene) {
            this.gene = gene;
            return this;
        }

        @JsonProperty("description")
        public String getDescription() {
            return description;
        }

        @JsonProperty("description")
        public void setDescription(String description) {
            this.description = description;
        }

        public PharosRestDTO withDescription(String description) {
            this.description = description;
            return this;
        }

        @JsonProperty("idgFamily")
        public String getIdgFamily() {
            return idgFamily;
        }

        @JsonProperty("idgFamily")
        public void setIdgFamily(String idgFamily) {
            this.idgFamily = idgFamily;
        }

        public PharosRestDTO withIdgFamily(String idgFamily) {
            this.idgFamily = idgFamily;
            return this;
        }

        @JsonProperty("idgTDL")
        public String getIdgTDL() {
            return idgTDL;
        }

        @JsonProperty("idgTDL")
        public void setIdgTDL(String idgTDL) {
            this.idgTDL = idgTDL;
        }

        public PharosRestDTO withIdgTDL(String idgTDL) {
            this.idgTDL = idgTDL;
            return this;
        }

        @JsonProperty("novelty")
        public Double getNovelty() {
            return novelty;
        }

        @JsonProperty("novelty")
        public void setNovelty(Double novelty) {
            this.novelty = novelty;
        }

        public PharosRestDTO withNovelty(Double novelty) {
            this.novelty = novelty;
            return this;
        }

        @JsonProperty("antibodyCount")
        public Integer getAntibodyCount() {
            return antibodyCount;
        }

        @JsonProperty("antibodyCount")
        public void setAntibodyCount(Integer antibodyCount) {
            this.antibodyCount = antibodyCount;
        }

        public PharosRestDTO withAntibodyCount(Integer antibodyCount) {
            this.antibodyCount = antibodyCount;
            return this;
        }

        @JsonProperty("monoclonalCount")
        public Integer getMonoclonalCount() {
            return monoclonalCount;
        }

        @JsonProperty("monoclonalCount")
        public void setMonoclonalCount(Integer monoclonalCount) {
            this.monoclonalCount = monoclonalCount;
        }

        public PharosRestDTO withMonoclonalCount(Integer monoclonalCount) {
            this.monoclonalCount = monoclonalCount;
            return this;
        }

        @JsonProperty("pubmedCount")
        public Object getPubmedCount() {
            return pubmedCount;
        }

        @JsonProperty("pubmedCount")
        public void setPubmedCount(Object pubmedCount) {
            this.pubmedCount = pubmedCount;
        }

        public PharosRestDTO withPubmedCount(Object pubmedCount) {
            this.pubmedCount = pubmedCount;
            return this;
        }

        @JsonProperty("jensenScore")
        public Double getJensenScore() {
            return jensenScore;
        }

        @JsonProperty("jensenScore")
        public void setJensenScore(Double jensenScore) {
            this.jensenScore = jensenScore;
        }

        public PharosRestDTO withJensenScore(Double jensenScore) {
            this.jensenScore = jensenScore;
            return this;
        }

        @JsonProperty("patentCount")
        public Integer getPatentCount() {
            return patentCount;
        }

        @JsonProperty("patentCount")
        public void setPatentCount(Integer patentCount) {
            this.patentCount = patentCount;
        }

        public PharosRestDTO withPatentCount(Integer patentCount) {
            this.patentCount = patentCount;
            return this;
        }

        @JsonProperty("grantCount")
        public Object getGrantCount() {
            return grantCount;
        }

        @JsonProperty("grantCount")
        public void setGrantCount(Object grantCount) {
            this.grantCount = grantCount;
        }

        public PharosRestDTO withGrantCount(Object grantCount) {
            this.grantCount = grantCount;
            return this;
        }

        @JsonProperty("grantTotalCost")
        public Object getGrantTotalCost() {
            return grantTotalCost;
        }

        @JsonProperty("grantTotalCost")
        public void setGrantTotalCost(Object grantTotalCost) {
            this.grantTotalCost = grantTotalCost;
        }

        public PharosRestDTO withGrantTotalCost(Object grantTotalCost) {
            this.grantTotalCost = grantTotalCost;
            return this;
        }

        @JsonProperty("r01Count")
        public Object getR01Count() {
            return r01Count;
        }

        @JsonProperty("r01Count")
        public void setR01Count(Object r01Count) {
            this.r01Count = r01Count;
        }

        public PharosRestDTO withR01Count(Object r01Count) {
            this.r01Count = r01Count;
            return this;
        }

        @JsonProperty("ppiCount")
        public Integer getPpiCount() {
            return ppiCount;
        }

        @JsonProperty("ppiCount")
        public void setPpiCount(Integer ppiCount) {
            this.ppiCount = ppiCount;
        }

        public PharosRestDTO withPpiCount(Integer ppiCount) {
            this.ppiCount = ppiCount;
            return this;
        }

        @JsonProperty("knowledgeAvailability")
        public Double getKnowledgeAvailability() {
            return knowledgeAvailability;
        }

        @JsonProperty("knowledgeAvailability")
        public void setKnowledgeAvailability(Double knowledgeAvailability) {
            this.knowledgeAvailability = knowledgeAvailability;
        }

        public PharosRestDTO withKnowledgeAvailability(Double knowledgeAvailability) {
            this.knowledgeAvailability = knowledgeAvailability;
            return this;
        }

        @JsonProperty("pubTatorScore")
        public Double getPubTatorScore() {
            return pubTatorScore;
        }

        @JsonProperty("pubTatorScore")
        public void setPubTatorScore(Double pubTatorScore) {
            this.pubTatorScore = pubTatorScore;
        }

        public PharosRestDTO withPubTatorScore(Double pubTatorScore) {
            this.pubTatorScore = pubTatorScore;
            return this;
        }

        @JsonProperty("kind")
        public String getKind() {
            return kind;
        }

        @JsonProperty("kind")
        public void setKind(String kind) {
            this.kind = kind;
        }

        public PharosRestDTO withKind(String kind) {
            this.kind = kind;
            return this;
        }

        @JsonProperty("self")
        public String getSelf() {
            return self;
        }

        @JsonProperty("self")
        public void setSelf(String self) {
            this.self = self;
        }

        public PharosRestDTO withSelf(String self) {
            this.self = self;
            return this;
        }

        @JsonProperty("_organism")
        public Object getOrganism() {
            return organism;
        }

        @JsonProperty("_organism")
        public void setOrganism(Object organism) {
            this.organism = organism;
        }

        public PharosRestDTO withOrganism(Object organism) {
            this.organism = organism;
            return this;
        }

        @JsonProperty("_synonyms")
        public Synonyms getSynonyms() {
            return synonyms;
        }

        @JsonProperty("_synonyms")
        public void setSynonyms(Synonyms synonyms) {
            this.synonyms = synonyms;
        }

        public PharosRestDTO withSynonyms(Synonyms synonyms) {
            this.synonyms = synonyms;
            return this;
        }

        @JsonProperty("_publications")
        public Publications getPublications() {
            return publications;
        }

        @JsonProperty("_publications")
        public void setPublications(Publications publications) {
            this.publications = publications;
        }

        public PharosRestDTO withPublications(Publications publications) {
            this.publications = publications;
            return this;
        }

        @JsonProperty("_properties")
        public Properties getProperties() {
            return properties;
        }

        @JsonProperty("_properties")
        public void setProperties(Properties properties) {
            this.properties = properties;
        }

        public PharosRestDTO withProperties(Properties properties) {
            this.properties = properties;
            return this;
        }

        @JsonProperty("_links")
        public Links getLinks() {
            return links;
        }

        @JsonProperty("_links")
        public void setLinks(Links links) {
            this.links = links;
        }

        public PharosRestDTO withLinks(Links links) {
            this.links = links;
            return this;
        }

        @JsonProperty("_namespace")
        public Object getNamespace() {
            return namespace;
        }

        @JsonProperty("_namespace")
        public void setNamespace(Object namespace) {
            this.namespace = namespace;
        }

        public PharosRestDTO withNamespace(Object namespace) {
            this.namespace = namespace;
            return this;
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

        public PharosRestDTO withAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
            return this;
        }

    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "count",
            "href"
    })
    public static class Links {

        @JsonProperty("count")
        private Integer count;
        @JsonProperty("href")
        private String href;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        @JsonProperty("count")
        public Integer getCount() {
            return count;
        }

        @JsonProperty("count")
        public void setCount(Integer count) {
            this.count = count;
        }

        public Links withCount(Integer count) {
            this.count = count;
            return this;
        }

        @JsonProperty("href")
        public String getHref() {
            return href;
        }

        @JsonProperty("href")
        public void setHref(String href) {
            this.href = href;
        }

        public Links withHref(String href) {
            this.href = href;
            return this;
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

        public Links withAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
            return this;
        }

    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "count",
            "href"
    })
    public static class Properties {

        @JsonProperty("count")
        private Integer count;
        @JsonProperty("href")
        private String href;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        @JsonProperty("count")
        public Integer getCount() {
            return count;
        }

        @JsonProperty("count")
        public void setCount(Integer count) {
            this.count = count;
        }

        public Properties withCount(Integer count) {
            this.count = count;
            return this;
        }

        @JsonProperty("href")
        public String getHref() {
            return href;
        }

        @JsonProperty("href")
        public void setHref(String href) {
            this.href = href;
        }

        public Properties withHref(String href) {
            this.href = href;
            return this;
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

        public Properties withAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
            return this;
        }

    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "count",
            "href"
    })
    public static class Publications {

        @JsonProperty("count")
        private Integer count;
        @JsonProperty("href")
        private String href;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        @JsonProperty("count")
        public Integer getCount() {
            return count;
        }

        @JsonProperty("count")
        public void setCount(Integer count) {
            this.count = count;
        }

        public Publications withCount(Integer count) {
            this.count = count;
            return this;
        }

        @JsonProperty("href")
        public String getHref() {
            return href;
        }

        @JsonProperty("href")
        public void setHref(String href) {
            this.href = href;
        }

        public Publications withHref(String href) {
            this.href = href;
            return this;
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

        public Publications withAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
            return this;
        }

    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "count",
            "href"
    })
    public static class Synonyms {

        @JsonProperty("count")
        private Integer             count;
        @JsonProperty("href")
        private String              href;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        @JsonProperty("count")
        public Integer getCount() {
            return count;
        }

        @JsonProperty("count")
        public void setCount(Integer count) {
            this.count = count;
        }

        public Synonyms withCount(Integer count) {
            this.count = count;
            return this;
        }

        @JsonProperty("href")
        public String getHref() {
            return href;
        }

        @JsonProperty("href")
        public void setHref(String href) {
            this.href = href;
        }

        public Synonyms withHref(String href) {
            this.href = href;
            return this;
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

        public Synonyms withAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
            return this;
        }
    }

    // *** END GENERATED FROM http://www.jsonschema2pojo.org/ ****************************************
}