package org.mousephenotype.impress2;

import java.io.Serializable;
import java.util.Collection;

public class ImpressUnits implements Serializable {

    private Integer id;
    private String unit;

    private Collection<Integer> parameterCollection;

    public ImpressUnits() {
    }

    public ImpressUnits(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Collection<Integer> getParameterCollection() {
        return parameterCollection;
    }

    public void setParameterCollection(Collection<Integer> parameterCollection) {
        this.parameterCollection = parameterCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if ( ! (object instanceof ImpressUnits)) {
            return false;
        }
        ImpressUnits other = (ImpressUnits) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }
}