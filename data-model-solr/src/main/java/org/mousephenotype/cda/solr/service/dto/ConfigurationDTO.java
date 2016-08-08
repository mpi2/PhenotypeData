package org.mousephenotype.cda.solr.service.dto;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.SolrDocument;

/**
 * Created by jmason on 04/08/2016.
 */
@SolrDocument(solrCoreName = "configuration")
public class ConfigurationDTO {
    public static final String ID = "id";
    public static final String TYPE = "type";


    //<!-- Project document -->
    public static final String PROJECT_NAME = "project_name";
    public static final String PROJECT_SHORT_NAME = "project_short_name";
    public static final String PROJECT_REPOSITORY_NAME = "project_repository_name";
    public static final String PROJECT_FEE_LINK = "project_fee_link";
    public static final String PROJECT_LOGO_FILENAME = "project_logo_filename";


    // <!-- Metadata document -->
    public static final String METADATA_KEY = "metadata_key";
    public static final String METADATA_VALUE = "metadata_value";
    public static final String METADATA_VERSION = "metadata_version";


    @Field(ID)
    String id;

    @Field(TYPE)
    String type;

    @Field(PROJECT_NAME)
    String projectName;

    @Field(PROJECT_SHORT_NAME)
    String projectShortName;

    @Field(PROJECT_REPOSITORY_NAME)
    String projectRepositoryName;

    @Field(PROJECT_FEE_LINK)
    String projectFeeLink;

    @Field(PROJECT_LOGO_FILENAME)
    String projectLogoFilename;

    @Field(METADATA_KEY)
    String metadataKey;

    @Field(METADATA_VALUE)
    String metadataValue;

    @Field(METADATA_VERSION)
    String metadataVersion;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectShortName() {
        return projectShortName;
    }

    public void setProjectShortName(String projectShortName) {
        this.projectShortName = projectShortName;
    }

    public String getProjectRepositoryName() {
        return projectRepositoryName;
    }

    public void setProjectRepositoryName(String projectRepositoryName) {
        this.projectRepositoryName = projectRepositoryName;
    }

    public String getProjectFeeLink() {
        return projectFeeLink;
    }

    public void setProjectFeeLink(String projectFeeLink) {
        this.projectFeeLink = projectFeeLink;
    }

    public String getProjectLogoFilename() {
        return projectLogoFilename;
    }

    public void setProjectLogoFilename(String projectLogoFilename) {
        this.projectLogoFilename = projectLogoFilename;
    }

    public String getMetadataKey() {
        return metadataKey;
    }

    public void setMetadataKey(String metadataKey) {
        this.metadataKey = metadataKey;
    }

    public String getMetadataValue() {
        return metadataValue;
    }

    public void setMetadataValue(String metadataValue) {
        this.metadataValue = metadataValue;
    }

    public String getMetadataVersion() {
        return metadataVersion;
    }

    public void setMetadataVersion(String metadataVersion) {
        this.metadataVersion = metadataVersion;
    }
}
