package org.mousephenotype.impress2;


import java.io.Serializable;
import java.util.Collection;

public class ImpressParameter implements Serializable {

    private Long                parameterId;
    private String              parameterKey;
    private String              type         = "simpleParameter";
    private String              name;
    private boolean             isVisible;
    private boolean             isActive     = true;
    private boolean             isDeprecated;
    private int                 majorVersion = 1;
    private int                 minorVersion = 0;
    private String              derivation;
    private String              description;
    private boolean             isAnnotation;
    private boolean             isDerived;
    private boolean             isImportant;
    private boolean             isIncrement;
    private boolean             isMedia;
    private boolean             isOption;
    private boolean             isRequired;
    private boolean             qcCheck;
    private Float               qcMin;
    private Float               qcMax;
    private String              qcNotes;
    private String              valueType    = "TEXT";
    private String              graphType;
    private String              dataAnalysisNotes;
    private boolean             isInternal;
    private boolean             isDeleted;
    private String              oldParameterKey;
    private Long                originalParamId;
    private Long                ontologyGroupId;
    private int                 weight;
    private Long                procedureId;
    private ImpressUnits        unit;
    private Collection<Integer> incrementCollection;
    private Collection<Integer> optionCollection;
    private Collection<Integer> mptermCollection;

    public ImpressParameter() {
    }

    public ImpressParameter(Long parameterId) {
        this.parameterId = parameterId;
    }

    public ImpressParameter(Long parameterId, boolean isVisible, boolean isActive, boolean isDeprecated, int majorVersion, int minorVersion, boolean isAnnotation, boolean isDerived, boolean isImportant, boolean isIncrement, boolean isMedia, boolean isOption, boolean isRequired, boolean qcCheck, String valueType, boolean isInternal, boolean isDeleted, int weight) {
        this.parameterId = parameterId;
        this.isVisible = isVisible;
        this.isActive = isActive;
        this.isDeprecated = isDeprecated;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.isAnnotation = isAnnotation;
        this.isDerived = isDerived;
        this.isImportant = isImportant;
        this.isIncrement = isIncrement;
        this.isMedia = isMedia;
        this.isOption = isOption;
        this.isRequired = isRequired;
        this.qcCheck = qcCheck;
        this.valueType = valueType;
        this.isInternal = isInternal;
        this.isDeleted = isDeleted;
        this.weight = weight;
    }

    public Long getParameterId() {
        return parameterId;
    }

    public void setParameterId(Long parameterId) {
        this.parameterId = parameterId;
    }

    public String getParameterKey() {
        return parameterKey;
    }

    public void setParameterKey(String parameterKey) {
        this.parameterKey = parameterKey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean getIsDeprecated() {
        return isDeprecated;
    }

    public void setIsDeprecated(boolean isDeprecated) {
        this.isDeprecated = isDeprecated;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }

    public String getDerivation() {
        return derivation;
    }

    public void setDerivation(String derivation) {
        this.derivation = derivation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getIsAnnotation() {
        return isAnnotation;
    }

    public void setIsAnnotation(boolean isAnnotation) {
        this.isAnnotation = isAnnotation;
    }

    public boolean getIsDerived() {
        return isDerived;
    }

    public void setIsDerived(boolean isDerived) {
        this.isDerived = isDerived;
    }

    public boolean getIsImportant() {
        return isImportant;
    }

    public void setIsImportant(boolean isImportant) {
        this.isImportant = isImportant;
    }

    public boolean getIsIncrement() {
        return isIncrement;
    }

    public void setIsIncrement(boolean isIncrement) {
        this.isIncrement = isIncrement;
    }

    public boolean getIsMedia() {
        return isMedia;
    }

    public void setIsMedia(boolean isMedia) {
        this.isMedia = isMedia;
    }

    public boolean getIsOption() {
        return isOption;
    }

    public void setIsOption(boolean isOption) {
        this.isOption = isOption;
    }

    public boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    public boolean getQcCheck() {
        return qcCheck;
    }

    public void setQcCheck(boolean qcCheck) {
        this.qcCheck = qcCheck;
    }

    public Float getQcMin() {
        return qcMin;
    }

    public void setQcMin(Float qcMin) {
        this.qcMin = qcMin;
    }

    public Float getQcMax() {
        return qcMax;
    }

    public void setQcMax(Float qcMax) {
        this.qcMax = qcMax;
    }

    public String getQcNotes() {
        return qcNotes;
    }

    public void setQcNotes(String qcNotes) {
        this.qcNotes = qcNotes;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getGraphType() {
        return graphType;
    }

    public void setGraphType(String graphType) {
        this.graphType = graphType;
    }

    public String getDataAnalysisNotes() {
        return dataAnalysisNotes;
    }

    public void setDataAnalysisNotes(String dataAnalysisNotes) {
        this.dataAnalysisNotes = dataAnalysisNotes;
    }

    public boolean getIsInternal() {
        return isInternal;
    }

    public void setIsInternal(boolean isInternal) {
        this.isInternal = isInternal;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getOldParameterKey() {
        return oldParameterKey;
    }

    public void setOldParameterKey(String oldParameterKey) {
        this.oldParameterKey = oldParameterKey;
    }

    public Long getProcedureId() {
        return procedureId;
    }

    public void setProcedureId(Long procedureId) {
        this.procedureId = procedureId;
    }

    

    public ImpressUnits getUnit() {
        return unit;
    }
    
    public void setUnit(ImpressUnits unit) {
        this.unit = unit;
    }

    public Collection<Integer> getIncrementCollection() {
        return incrementCollection;
    }

    public void setIncrementCollection(Collection<Integer> incrementCollection) {
        this.incrementCollection = incrementCollection;
    }

    public Collection<Integer> getOptionCollection() {
        return optionCollection;
    }

    public void setOptionCollection(Collection<Integer> optionCollection) {
        this.optionCollection = optionCollection;
    }

    public Collection<Integer> getMptermCollection() {
        return mptermCollection;
    }

    public void setMptermCollection(Collection<Integer> mptermCollection) {
        this.mptermCollection = mptermCollection;
    }
    


    
    public Long getOriginalParamId() {
        return originalParamId;
    }

    public void setOriginalParamId(Long originalParamId) {
        this.originalParamId = originalParamId;
    }

    public Long getOntologyGroupId() {
        return ontologyGroupId;
    }

    public void setOntologyGroupId(Long ontologyGroupId) {
        this.ontologyGroupId = ontologyGroupId;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (parameterId != null ? parameterId.hashCode() : 0);
        return hash;
    }
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ImpressParameter)) {
            return false;
        }
        ImpressParameter other = (ImpressParameter) object;
        return !((this.parameterId == null && other.parameterId != null) || (this.parameterId != null && !this.parameterId.equals(other.parameterId)));
    }
}