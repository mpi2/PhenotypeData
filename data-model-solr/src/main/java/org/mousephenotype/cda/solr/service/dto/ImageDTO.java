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
package org.mousephenotype.cda.solr.service.dto;

import lombok.*;
import org.apache.solr.client.solrj.beans.Field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Data
public class ImageDTO extends ObservationDTO {


    public final static String ID = ObservationDTO.ID;
    public final static String DATASOURCE_ID = ObservationDTO.DATASOURCE_ID;
    public final static String DATASOURCE_NAME = ObservationDTO.DATASOURCE_NAME;
    public final static String PROJECT_ID = ObservationDTO.PROJECT_ID;
    public final static String PROJECT_NAME = ObservationDTO.PROJECT_NAME;
    public final static String PHENOTYPING_CENTER = ObservationDTO.PHENOTYPING_CENTER;
    public final static String PHENOTYPING_CENTER_ID = ObservationDTO.PHENOTYPING_CENTER_ID;
    public final static String GENE_ACCESSION_ID = ObservationDTO.GENE_ACCESSION_ID;
    public final static String GENE_SYMBOL = ObservationDTO.GENE_SYMBOL;
    public final static String ALLELE_ACCESSION_ID = ObservationDTO.ALLELE_ACCESSION_ID;
    public final static String ALLELE_SYMBOL = ObservationDTO.ALLELE_SYMBOL;
    public final static String ZYGOSITY = ObservationDTO.ZYGOSITY;
    public final static String SEX = ObservationDTO.SEX;
    public final static String BIOLOGICAL_SAMPLE_GROUP = ObservationDTO.BIOLOGICAL_SAMPLE_GROUP;
    public final static String STRAIN_ACCESSION_ID = ObservationDTO.STRAIN_ACCESSION_ID;
    public final static String STRAIN_NAME = ObservationDTO.STRAIN_NAME;
    public final static String GENETIC_BACKGROUND = ObservationDTO.GENETIC_BACKGROUND;
    public final static String PIPELINE_NAME = ObservationDTO.PIPELINE_NAME;
    public final static String PIPELINE_ID = ObservationDTO.PIPELINE_ID;
    public final static String PIPELINE_STABLE_ID = ObservationDTO.PIPELINE_STABLE_ID;
    public final static String PROCEDURE_ID = ObservationDTO.PROCEDURE_ID;
    public final static String PROCEDURE_NAME = ObservationDTO.PROCEDURE_NAME;
    public final static String PROCEDURE_STABLE_ID = ObservationDTO.PROCEDURE_STABLE_ID;
    public final static String PROCEDURE_GROUP = ObservationDTO.PROCEDURE_GROUP;
    public final static String PARAMETER_ID = ObservationDTO.PARAMETER_ID;
    public final static String PARAMETER_NAME = ObservationDTO.PARAMETER_NAME;
    public final static String PARAMETER_STABLE_ID = ObservationDTO.PARAMETER_STABLE_ID;
    public final static String EXPERIMENT_ID = ObservationDTO.EXPERIMENT_ID;
    public final static String EXPERIMENT_SOURCE_ID = ObservationDTO.EXPERIMENT_SOURCE_ID;
    public final static String OBSERVATION_TYPE = ObservationDTO.OBSERVATION_TYPE;
    public final static String COLONY_ID = ObservationDTO.COLONY_ID;
    public final static String DATE_OF_BIRTH = ObservationDTO.DATE_OF_BIRTH;
    public final static String DATE_OF_EXPERIMENT = ObservationDTO.DATE_OF_EXPERIMENT;
    public final static String POPULATION_ID = ObservationDTO.POPULATION_ID;
    public final static String EXTERNAL_SAMPLE_ID = ObservationDTO.EXTERNAL_SAMPLE_ID;
    public final static String DATA_POINT = ObservationDTO.DATA_POINT;
    public final static String ORDER_INDEX = ObservationDTO.ORDER_INDEX;
    public final static String DIMENSION = ObservationDTO.DIMENSION;
    public final static String TIME_POINT = ObservationDTO.TIME_POINT;
    public final static String DISCRETE_POINT = ObservationDTO.DISCRETE_POINT;
    public final static String CATEGORY = ObservationDTO.CATEGORY;
    public final static String VALUE = ObservationDTO.VALUE;
    public final static String METADATA = ObservationDTO.METADATA;
    public final static String METADATA_GROUP = ObservationDTO.METADATA_GROUP;
    public final static String DOWNLOAD_FILE_PATH = ObservationDTO.DOWNLOAD_FILE_PATH;
    public final static String FILE_TYPE = ObservationDTO.FILE_TYPE;
    public final static String PARAMETER_ASSOCIATION_STABLE_ID = ObservationDTO.PARAMETER_ASSOCIATION_STABLE_ID;
    public final static String PARAMETER_ASSOCIATION_SEQUENCE_ID = ObservationDTO.PARAMETER_ASSOCIATION_SEQUENCE_ID;
    public final static String PARAMETER_ASSOCIATION_DIM_ID = ObservationDTO.PARAMETER_ASSOCIATION_DIM_ID;
    public final static String PARAMETER_ASSOCIATION_NAME = ObservationDTO.PARAMETER_ASSOCIATION_NAME;
    public final static String PARAMETER_ASSOCIATION_VALUE = ObservationDTO.PARAMETER_ASSOCIATION_VALUE;
    public final static String WEIGHT_PARAMETER_STABLE_ID = ObservationDTO.WEIGHT_PARAMETER_STABLE_ID;
    public final static String WEIGHT_DATE = ObservationDTO.WEIGHT_DATE;
    public final static String WEIGHT_DAYS_OLD = ObservationDTO.WEIGHT_DAYS_OLD;
    public final static String WEIGHT = ObservationDTO.WEIGHT;
    public static final String AGE_IN_DAYS = ObservationDTO.AGE_IN_DAYS;

    public static final String FULL_RESOLUTION_FILE_PATH = "full_resolution_file_path";

    public static final String OMERO_ID = "omero_id";
    public static final String DOWNLOAD_URL = "download_url";
    public static final String JPEG_URL = "jpeg_url";
    public static final String IMAGE_LINK = "image_link";

    public static final String EFO_ID = "efo_id";
    public static final String UBERON_ID = "uberon_id";

    public static final String STAGE = "stage";

    public static final String SYMBOL_GENE = "symbol_gene";
    public static final String SYMBOL = "symbol";
    public static final String SUBTYPE = "subtype";
    public static final String STATUS = "status";
    public static final String IMITS_PHENOTYPE_STARTED = SangerImageDTO.IMITS_PHENOTYPE_STARTED;
    public static final String IMITS_PHENOTYPE_COMPLETE = SangerImageDTO.IMITS_PHENOTYPE_COMPLETE;
    public static final String IMITS_PHENOTYPE_STATUS = SangerImageDTO.IMITS_PHENOTYPE_STATUS;
    public static final String LEGACY_PHENOTYPE_STATUS = AlleleDTO.LEGACY_PHENOTYPE_STATUS;
    public static final String LATEST_PRODUCTION_CENTRE = SangerImageDTO.LATEST_PRODUCTION_CENTRE;
    public static final String LATEST_PHENOTYPING_CENTRE = SangerImageDTO.LATEST_PHENOTYPING_CENTRE;
    public static final String ALLELE_NAME = SangerImageDTO.ALLELE_NAME;
    public static final String MARKER_SYMBOL = SangerImageDTO.MARKER_SYMBOL;
    public static final String MARKER_NAME = SangerImageDTO.MARKER_NAME;
    public static final String MARKER_SYNONYM = SangerImageDTO.MARKER_SYNONYM;
    public static final String MARKER_TYPE = SangerImageDTO.MARKER_TYPE;
    public static final String HUMAN_GENE_SYMBOL = SangerImageDTO.HUMAN_GENE_SYMBOL;
    public static final String LATEST_PHENOTYPE_STATUS = AlleleDTO.LATEST_PHENOTYPE_STATUS;

    public static final String MP_ID = MpDTO.MP_ID;
    public static final String MP_TERM = MpDTO.MP_TERM;
    public static final String MP_TERM_SYNONYM = MpDTO.MP_TERM_SYNONYM;
    public static final String MP_NARROW_SYNONYM = MpDTO.MP_NARROW_SYNONYM;

    public static final String INTERMEDIATE_MP_ID = MpDTO.INTERMEDIATE_MP_ID;
    public static final String INTERMEDIATE_MP_TERM = MpDTO.INTERMEDIATE_MP_TERM;

    public static final String TOP_LEVEL_MP_TERM = MpDTO.TOP_LEVEL_MP_TERM;
    public static final String TOP_LEVEL_MP_ID = MpDTO.TOP_LEVEL_MP_ID;

    public static final String MP_ID_TERM = "mp_id_term";

    public static final String INCREMENT_VALUE = "increment_value";

    //	used for lexical match on search page
    public static final String MARKER_SYNONYM_SYMBOL_GENE = "marker_synonym_symbol_gene";
    public static final String PARAMETER_ASSOCIATION_NAME_PROCEDURE_NAME = "parameter_association_name_procedure_name";
    public static final String SELECTED_TOP_LEVEL_ANATOMY_ID_ANATOMY_ID_TERM = "selected_top_level_anatomy_id_anatomy_id_term";
    public static final String SELECTED_TOP_LEVEL_ANATOMY_TERM_ANATOMY_ID_TERM = "selected_top_level_anatomy_term_anatomy_id_term";
    public static final String SELECTED_TOP_LEVEL_ANATOMY_TERM_SYNONYM_ANATOMY_ID_TERM = "selected_top_level_anatomy_term_synonym_anatomy_id_term";
    public static final String INTERMEDIATE_ANATOMY_ID_ANATOMY_ID_TERM = "intermediate_anatomy_id_anatomy_id_term";
    public static final String INTERMEDIATE_ANATOMY_TERM_ANATOMY_ID_TERM = "intermediate_anatomy_term_anatomy_id_term";
    public static final String INTERMEDIATE_ANATOMY_TERM_SYNONYM_ANATOMY_ID_TERM = "intermediate_anatomy_term_synonym_anatomy_id_term";
    public static final String ANATOMY_TERM_SYNONYM_ANATOMY_ID_TERM = "anatomy_term_synonym_anatomy_id_term";
    public static final String THUMBNAIL_URL = "thumbnail_url";

    @Field(MP_ID)
    private List<String> mpId;

    @Field(MP_TERM)
    private List<String> mpTerm;

    @Field(FULL_RESOLUTION_FILE_PATH)
    private String fullResolutionFilePath;

    @Field(OMERO_ID)
    private int omeroId;

    @Field(DOWNLOAD_URL)
    private String downloadUrl;

    @Field(IMAGE_LINK)
    private String imageLink;

    @Field(JPEG_URL)
    private String jpegUrl;

    @Field(THUMBNAIL_URL)
    private String thumbnailUrl;


    public ImageDTO() {
        super();
    }


    protected List<String> add(List<String> to, String what, Boolean uniqueOnly) {
        if (to == null) {
            to = new ArrayList<>();
        }
        if (!uniqueOnly || !to.contains(what)) {
            to.add(what);
        }
        return to;
    }


}
