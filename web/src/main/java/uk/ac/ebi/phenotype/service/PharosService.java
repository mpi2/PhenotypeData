package uk.ac.ebi.phenotype.service;

import com.fasterxml.jackson.annotation.*;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ilinca on 02/02/2017.
 */
@EnableRetry
@Service
public class PharosService {

    private static final String PHAROS_URL = "http://juniper.health.unm.edu/tcrd/api/target?q={q}";
    private RestTemplate restTemplate = new RestTemplate();

    @Retryable
    public PharosDTO getPharosInfo(String humanGeneSymbol){

        Map<String, String> params = new HashMap<>();
        params.put("q", "{\"genesymb\":\"" + humanGeneSymbol + "\"}");

        PharosRestDTO result = restTemplate.getForObject(PHAROS_URL, PharosRestDTO.class, params);

        System.out.println(result);

        if(result != null && result.getResults().size() > 0){

            List<Result> res = result.getResults();

            if (res.size() > 1){
                System.out.println("More than 1 PHAROS result for this gene!! " + PHAROS_URL.replace("{q}", params.get("q")) );
            }

            PharosDTO pharos = getPharosDtoFromResult(res.get(0), humanGeneSymbol);

            return pharos;

        } else {
            return null;
        }
    }


    PharosDTO getPharosDtoFromResult(Result res, String humanGeneSymbol) {

        PharosDTO pharosDTO = new PharosDTO();

        pharosDTO.setIdg2(res.getIdg2());
        pharosDTO.setIdgfam(res.getFam());
        pharosDTO.setPageLink("https://pharos.nih.gov/idg/targets/" + humanGeneSymbol);
        pharosDTO.setTdl(res.getTdl());

        return pharosDTO;
    }



    // *** START GENERATED FROM http://www.jsonschema2pojo.org/ ****************************************

    // **
    // ** NOTE: If the classes need to be regenerated, DO NOT FORGET that the static modifier must be
    // **       must be added to all the constructors or this will not work.  For more infromation see
    // **       http://www.cowtowncoder.com/blog/archives/2010/08/entry_411.html
    // **


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "protein"
    })
    public static class Components {

        @JsonProperty("protein")
        @Valid
        private List<Protein> protein = null;
        @JsonIgnore
        @Valid
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        /**
         * No args constructor for use in serialization
         *
         */
        public Components() {
        }

        /**
         *
         * @param protein
         */
        public Components(List<Protein> protein) {
            super();
            this.protein = protein;
        }

        @JsonProperty("protein")
        public List<Protein> getProtein() {
            return protein;
        }

        @JsonProperty("protein")
        public void setProtein(List<Protein> protein) {
            this.protein = protein;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

    }



    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "data_ver",
            "dbname",
            "dump_file",
            "is_copy",
            "owner",
            "query",
            "schema_ver"
    })
    public static class Metadata {

        @JsonProperty("data_ver")
        private String dataVer;
        @JsonProperty("dbname")
        private String dbname;
        @JsonProperty("dump_file")
        private Object dumpFile;
        @JsonProperty("is_copy")
        private Integer isCopy;
        @JsonProperty("owner")
        private String owner;
        @JsonProperty("query")
        @Valid
        private Query query;
        @JsonProperty("schema_ver")
        private String schemaVer;
        @JsonIgnore
        @Valid
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        /**
         * No args constructor for use in serialization
         *
         */
        public Metadata() {
        }

        /**
         *
         * @param dumpFile
         * @param dataVer
         * @param query
         * @param dbname
         * @param owner
         * @param isCopy
         * @param schemaVer
         */
        public Metadata(String dataVer, String dbname, Object dumpFile, Integer isCopy, String owner, Query query, String schemaVer) {
            super();
            this.dataVer = dataVer;
            this.dbname = dbname;
            this.dumpFile = dumpFile;
            this.isCopy = isCopy;
            this.owner = owner;
            this.query = query;
            this.schemaVer = schemaVer;
        }

        @JsonProperty("data_ver")
        public String getDataVer() {
            return dataVer;
        }

        @JsonProperty("data_ver")
        public void setDataVer(String dataVer) {
            this.dataVer = dataVer;
        }

        @JsonProperty("dbname")
        public String getDbname() {
            return dbname;
        }

        @JsonProperty("dbname")
        public void setDbname(String dbname) {
            this.dbname = dbname;
        }

        @JsonProperty("dump_file")
        public Object getDumpFile() {
            return dumpFile;
        }

        @JsonProperty("dump_file")
        public void setDumpFile(Object dumpFile) {
            this.dumpFile = dumpFile;
        }

        @JsonProperty("is_copy")
        public Integer getIsCopy() {
            return isCopy;
        }

        @JsonProperty("is_copy")
        public void setIsCopy(Integer isCopy) {
            this.isCopy = isCopy;
        }

        @JsonProperty("owner")
        public String getOwner() {
            return owner;
        }

        @JsonProperty("owner")
        public void setOwner(String owner) {
            this.owner = owner;
        }

        @JsonProperty("query")
        public Query getQuery() {
            return query;
        }

        @JsonProperty("query")
        public void setQuery(Query query) {
            this.query = query;
        }

        @JsonProperty("schema_ver")
        public String getSchemaVer() {
            return schemaVer;
        }

        @JsonProperty("schema_ver")
        public void setSchemaVer(String schemaVer) {
            this.schemaVer = schemaVer;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

    }



    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "metadata",
            "results"
    })
    public static class PharosRestDTO {

        @JsonProperty("metadata")
        @Valid
        private Metadata metadata;
        @JsonProperty("results")
        @Valid
        private List<Result> results = null;
        @JsonIgnore
        @Valid
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        /**
         * No args constructor for use in serialization
         *
         */
        public PharosRestDTO() {
        }

        /**
         *
         * @param results
         * @param metadata
         */
        public PharosRestDTO(Metadata metadata, List<Result> results) {
            super();
            this.metadata = metadata;
            this.results = results;
        }

        @JsonProperty("metadata")
        public Metadata getMetadata() {
            return metadata;
        }

        @JsonProperty("metadata")
        public void setMetadata(Metadata metadata) {
            this.metadata = metadata;
        }

        @JsonProperty("results")
        public List<Result> getResults() {
            return results;
        }

        @JsonProperty("results")
        public void setResults(List<Result> results) {
            this.results = results;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "chr",
            "description",
            "dtoid",
            "family",
            "geneid",
            "id",
            "name",
            "seq",
            "stringid",
            "sym",
            "uniprot",
            "up_version"
    })
    public static class Protein {

        @JsonProperty("chr")
        private String chr;
        @JsonProperty("description")
        private String description;
        @JsonProperty("dtoid")
        private String dtoid;
        @JsonProperty("family")
        private String family;
        @JsonProperty("geneid")
        private Integer geneid;
        @JsonProperty("id")
        private Integer id;
        @JsonProperty("name")
        private String name;
        @JsonProperty("seq")
        private String seq;
        @JsonProperty("stringid")
        private String stringid;
        @JsonProperty("sym")
        private String sym;
        @JsonProperty("uniprot")
        private String uniprot;
        @JsonProperty("up_version")
        private Integer upVersion;
        @JsonIgnore
        @Valid
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        /**
         * No args constructor for use in serialization
         *
         */
        public Protein() {
        }

        /**
         *
         * @param id
         * @param dtoid
         * @param geneid
         * @param upVersion
         * @param family
         * @param description
         * @param name
         * @param seq
         * @param stringid
         * @param sym
         * @param uniprot
         * @param chr
         */
        public Protein(String chr, String description, String dtoid, String family, Integer geneid, Integer id, String name, String seq, String stringid, String sym, String uniprot, Integer upVersion) {
            super();
            this.chr = chr;
            this.description = description;
            this.dtoid = dtoid;
            this.family = family;
            this.geneid = geneid;
            this.id = id;
            this.name = name;
            this.seq = seq;
            this.stringid = stringid;
            this.sym = sym;
            this.uniprot = uniprot;
            this.upVersion = upVersion;
        }

        @JsonProperty("chr")
        public String getChr() {
            return chr;
        }

        @JsonProperty("chr")
        public void setChr(String chr) {
            this.chr = chr;
        }

        @JsonProperty("description")
        public String getDescription() {
            return description;
        }

        @JsonProperty("description")
        public void setDescription(String description) {
            this.description = description;
        }

        @JsonProperty("dtoid")
        public String getDtoid() {
            return dtoid;
        }

        @JsonProperty("dtoid")
        public void setDtoid(String dtoid) {
            this.dtoid = dtoid;
        }

        @JsonProperty("family")
        public String getFamily() {
            return family;
        }

        @JsonProperty("family")
        public void setFamily(String family) {
            this.family = family;
        }

        @JsonProperty("geneid")
        public Integer getGeneid() {
            return geneid;
        }

        @JsonProperty("geneid")
        public void setGeneid(Integer geneid) {
            this.geneid = geneid;
        }

        @JsonProperty("id")
        public Integer getId() {
            return id;
        }

        @JsonProperty("id")
        public void setId(Integer id) {
            this.id = id;
        }

        @JsonProperty("name")
        public String getName() {
            return name;
        }

        @JsonProperty("name")
        public void setName(String name) {
            this.name = name;
        }

        @JsonProperty("seq")
        public String getSeq() {
            return seq;
        }

        @JsonProperty("seq")
        public void setSeq(String seq) {
            this.seq = seq;
        }

        @JsonProperty("stringid")
        public String getStringid() {
            return stringid;
        }

        @JsonProperty("stringid")
        public void setStringid(String stringid) {
            this.stringid = stringid;
        }

        @JsonProperty("sym")
        public String getSym() {
            return sym;
        }

        @JsonProperty("sym")
        public void setSym(String sym) {
            this.sym = sym;
        }

        @JsonProperty("uniprot")
        public String getUniprot() {
            return uniprot;
        }

        @JsonProperty("uniprot")
        public void setUniprot(String uniprot) {
            this.uniprot = uniprot;
        }

        @JsonProperty("up_version")
        public Integer getUpVersion() {
            return upVersion;
        }

        @JsonProperty("up_version")
        public void setUpVersion(Integer upVersion) {
            this.upVersion = upVersion;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

    }



    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "genesymb"
    })
    public static class Query {

        @JsonProperty("genesymb")
        private String genesymb;
        @JsonIgnore
        @Valid
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        /**
         * No args constructor for use in serialization
         *
         */
        public Query() {
        }

        /**
         *
         * @param genesymb
         */
        public Query(String genesymb) {
            super();
            this.genesymb = genesymb;
        }

        @JsonProperty("genesymb")
        public String getGenesymb() {
            return genesymb;
        }

        @JsonProperty("genesymb")
        public void setGenesymb(String genesymb) {
            this.genesymb = genesymb;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "comment",
            "components",
            "description",
            "fam",
            "famext",
            "id",
            "idg2",
            "name",
            "tdl",
            "ttype"
    })
    public static class Result {

        @JsonProperty("comment")
        private Object comment;
        @JsonProperty("components")
        @Valid
        private Components components;
        @JsonProperty("description")
        private Object description;
        @JsonProperty("fam")
        private String fam;
        @JsonProperty("famext")
        private String famext;
        @JsonProperty("id")
        private Integer id;
        @JsonProperty("idg2")
        private Integer idg2;
        @JsonProperty("name")
        private String name;
        @JsonProperty("tdl")
        private String tdl;
        @JsonProperty("ttype")
        private String ttype;
        @JsonIgnore
        @Valid
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        /**
         * No args constructor for use in serialization
         *
         */
        public Result() {
        }

        /**
         *
         * @param id
         * @param tdl
         * @param description
         * @param name
         * @param ttype
         * @param famext
         * @param components
         * @param fam
         * @param comment
         * @param idg2
         */
        public Result(Object comment, Components components, Object description, String fam, String famext, Integer id, Integer idg2, String name, String tdl, String ttype) {
            super();
            this.comment = comment;
            this.components = components;
            this.description = description;
            this.fam = fam;
            this.famext = famext;
            this.id = id;
            this.idg2 = idg2;
            this.name = name;
            this.tdl = tdl;
            this.ttype = ttype;
        }

        @JsonProperty("comment")
        public Object getComment() {
            return comment;
        }

        @JsonProperty("comment")
        public void setComment(Object comment) {
            this.comment = comment;
        }

        @JsonProperty("components")
        public Components getComponents() {
            return components;
        }

        @JsonProperty("components")
        public void setComponents(Components components) {
            this.components = components;
        }

        @JsonProperty("description")
        public Object getDescription() {
            return description;
        }

        @JsonProperty("description")
        public void setDescription(Object description) {
            this.description = description;
        }

        @JsonProperty("fam")
        public String getFam() {
            return fam;
        }

        @JsonProperty("fam")
        public void setFam(String fam) {
            this.fam = fam;
        }

        @JsonProperty("famext")
        public String getFamext() {
            return famext;
        }

        @JsonProperty("famext")
        public void setFamext(String famext) {
            this.famext = famext;
        }

        @JsonProperty("id")
        public Integer getId() {
            return id;
        }

        @JsonProperty("id")
        public void setId(Integer id) {
            this.id = id;
        }

        @JsonProperty("idg2")
        public Integer getIdg2() {
            return idg2;
        }

        @JsonProperty("idg2")
        public void setIdg2(Integer idg2) {
            this.idg2 = idg2;
        }

        @JsonProperty("name")
        public String getName() {
            return name;
        }

        @JsonProperty("name")
        public void setName(String name) {
            this.name = name;
        }

        @JsonProperty("tdl")
        public String getTdl() {
            return tdl;
        }

        @JsonProperty("tdl")
        public void setTdl(String tdl) {
            this.tdl = tdl;
        }

        @JsonProperty("ttype")
        public String getTtype() {
            return ttype;
        }

        @JsonProperty("ttype")
        public void setTtype(String ttype) {
            this.ttype = ttype;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

    }

    // *** END GENERATED FROM http://www.jsonschema2pojo.org/ ****************************************


}
