package org.mousephenotype.cda.neo4j.entity;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by ckchen on 14/03/2017.
 */
@NodeEntity
public class Procedure {

    @GraphId
    Long id;

    private String procedureId;
    private String procedureStableId;
    private String procedureStableKey;
    private String procedureName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProcedureId() {
        return procedureId;
    }

    public void setProcedureId(String procedureId) {
        this.procedureId = procedureId;
    }

    public String getProcedureStableId() {
        return procedureStableId;
    }

    public void setProcedureStableId(String procedureStableId) {
        this.procedureStableId = procedureStableId;
    }

    public String getProcedureStableKey() {
        return procedureStableKey;
    }

    public void setProcedureStableKey(String procedureStableKey) {
        this.procedureStableKey = procedureStableKey;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    @Override
    public String toString() {
        return "Procedure{" +
                "id=" + id +
                ", procedureId='" + procedureId + '\'' +
                ", procedureStableId='" + procedureStableId + '\'' +
                ", procedureStableKey='" + procedureStableKey + '\'' +
                ", procedureName='" + procedureName + '\'' +
                '}';
    }
}
