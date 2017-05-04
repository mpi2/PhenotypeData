package org.mousephenotype.cda.neo4j.entity;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

/**
 * Created by ckchen on 14/03/2017.
 */
@NodeEntity
public class Pipeline {

    @GraphId
    Long id;

    private String pipelineId;
    private String pipelineStableId;
    private String pipelineStableKey;
    private String pipelineName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(String pipelineId) {
        this.pipelineId = pipelineId;
    }

    public String getPipelineStableId() {
        return pipelineStableId;
    }

    public void setPipelineStableId(String pipelineStableId) {
        this.pipelineStableId = pipelineStableId;
    }

    public String getPipelineStableKey() {
        return pipelineStableKey;
    }

    public void setPipelineStableKey(String pipelineStableKey) {
        this.pipelineStableKey = pipelineStableKey;
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public void setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
    }

    @Override
    public String toString() {
        return "Pipeline{" +
                "id=" + id +
                ", pipelineId='" + pipelineId + '\'' +
                ", pipelineStableId='" + pipelineStableId + '\'' +
                ", pipelineStableKey='" + pipelineStableKey + '\'' +
                ", pipelineName='" + pipelineName + '\'' +
                '}';
    }
}
