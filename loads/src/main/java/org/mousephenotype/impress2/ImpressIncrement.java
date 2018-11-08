package org.mousephenotype.impress2;

import java.io.Serializable;

public class ImpressIncrement implements Serializable {


    private Integer incrementId;
    private int weight;
    private boolean isActive = true;
    private String incrementString;
    private String incrementType;
    private String incrementUnit;
    private Integer incrementMin;
    private boolean isDeleted;
    private Integer originalId;
    private Integer parameterId;


    public ImpressIncrement() {
    }

    public ImpressIncrement(Integer incrementId) {
        this.incrementId = incrementId;
    }

    public ImpressIncrement(Integer incrementId, int weight, boolean isActive, boolean deleted) {
        this.incrementId = incrementId;
        this.weight = weight;
        this.isActive = isActive;
        this.isDeleted = deleted;
    }

    public Integer getIncrementId() {
        return incrementId;
    }

    public void setIncrementId(Integer incrementId) {
        this.incrementId = incrementId;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getIncrementString() {
        return incrementString;
    }

    public void setIncrementString(String incrementString) {
        this.incrementString = incrementString;
    }

    public String getIncrementType() {
        return incrementType;
    }

    public void setIncrementType(String incrementType) {
        this.incrementType = incrementType;
    }

    public String getIncrementUnit() {
        return incrementUnit;
    }

    public void setIncrementUnit(String incrementUnit) {
        this.incrementUnit = incrementUnit;
    }

    public Integer getIncrementMin() {
        return incrementMin;
    }

    public void setIncrementMin(Integer incrementMin) {
        this.incrementMin = incrementMin;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getOriginalId() {
        return originalId;
    }

    public void setOriginalId(Integer originalId) {
        this.originalId = originalId;
    }

    public Integer getParameterId() {
        return parameterId;
    }

    public void setParameterId(Integer parameterId) {
        this.parameterId = parameterId;
    }

   
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (incrementId != null ? incrementId.hashCode() : 0);
        return hash;
    }
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ImpressIncrement)) {
            return false;
        }
        ImpressIncrement other = (ImpressIncrement) object;
        return !((this.incrementId == null && other.incrementId != null) || (this.incrementId != null && !this.incrementId.equals(other.incrementId)));
    }
    
}
