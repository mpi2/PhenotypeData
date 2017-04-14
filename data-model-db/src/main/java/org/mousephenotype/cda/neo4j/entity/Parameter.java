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

    private String parameterId;
    private String parameterStableId;
    private String parameterStableKey;
    private String parameterName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParameterId() {
        return parameterId;
    }

    public void setParameterId(String parameterId) {
        this.parameterId = parameterId;
    }

    public String getParameterStableId() {
        return parameterStableId;
    }

    public void setParameterStableId(String parameterStableId) {
        this.parameterStableId = parameterStableId;
    }

    public String getParameterStableKey() {
        return parameterStableKey;
    }

    public void setParameterStableKey(String parameterStableKey) {
        this.parameterStableKey = parameterStableKey;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "id=" + id +
                ", parameterId='" + parameterId + '\'' +
                ", parameterStableId='" + parameterStableId + '\'' +
                ", parameterStableKey='" + parameterStableKey + '\'' +
                ", parameterName='" + parameterName + '\'' +
                '}';
    }
}
