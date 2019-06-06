package org.mousephenotype.impress2;

import java.io.Serializable;

public class ImpressOption implements Serializable {

    private Integer optionId;
    private int phoWeight;
    private Integer parentId;
    private String name;
    private boolean isDefault;
    private boolean isActive = true;
    private int poWeight;
    private String description;
    private boolean isDeleted;
    private Integer parameterId;

    public ImpressOption() {
    }

    public ImpressOption(Integer id) {
        this.optionId = id;
    }

    public ImpressOption(Integer id, int phoWeight, boolean isDefault, boolean isActive, int poWeight, boolean deleted) {
        this.optionId = id;
        this.phoWeight = phoWeight;
        this.isDefault = isDefault;
        this.isActive = isActive;
        this.poWeight = poWeight;
        this.isDeleted = deleted;
    }

    public Integer getOptionId() {
        return optionId;
    }

    public void setOptionId(Integer optionId) {
        this.optionId = optionId;
    }

    public int getPhoWeight() {
        return phoWeight;
    }

    public void setPhoWeight(int phoWeight) {
        this.phoWeight = phoWeight;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }

    public int getPoWeight() {
        return poWeight;
    }

    public void setPoWeight(int poWeight) {
        this.poWeight = poWeight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean deleted) {
        isDeleted = deleted;
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
        hash += (optionId != null ? optionId.hashCode() : 0);
        return hash;
    }
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ImpressOption)) {
            return false;
        }
        ImpressOption other = (ImpressOption) object;
        return !((this.optionId == null && other.optionId != null) || (this.optionId != null && !this.optionId.equals(other.optionId)));
    }
}
