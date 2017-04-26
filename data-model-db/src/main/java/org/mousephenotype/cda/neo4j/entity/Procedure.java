package org.mousephenotype.cda.neo4j.entity;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

/**
 * Created by ckchen on 14/03/2017.
 */
@NodeEntity
public class Procedure {

    @GraphId
    Long id;

    private String stableId;
    private String stableKey;
    private String name;
    private String stage;

    @Relationship(type = "PARAMETER", direction = Relationship.OUTGOING)
    private Set<Parameter> parameters;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStableId() {
        return stableId;
    }

    public void setStableId(String stableId) {
        this.stableId = stableId;
    }

    public String getStableKey() {
        return stableKey;
    }

    public void setStableKey(String stableKey) {
        this.stableKey = stableKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public Set<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(Set<Parameter> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "Procedure{" +
                "id=" + id +
                ", stableId='" + stableId + '\'' +
                ", stableKey='" + stableKey + '\'' +
                ", name='" + name + '\'' +
                ", stage='" + stage + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}
