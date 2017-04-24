package org.mousephenotype.cda.neo4j.entity;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by ckchen on 14/03/2017.
 */
@NodeEntity
public class Parameter {

    @GraphId
    Long id;

    private String stableId;
    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "id=" + id +
                ", stableId='" + stableId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
