package org.mousephenotype.cda.db.pojo;

import java.io.Serializable;
import java.util.Collection;

public class Schedule implements Serializable {

    private Integer             scheduleId;
    private boolean             isActive = true;
    private boolean             isDeprecated;
    private String              timeLabel;
    private String              time;
    private String              timeUnit;
    private String              stage;
    private Integer             pipelineId;
    private Collection<Integer> procedureCollection;

    public Schedule() {
    }

    public Schedule(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Schedule(Integer scheduleId, boolean isActive, boolean isDeprecated, String timeLabel, String stage) {
        this.scheduleId = scheduleId;
        this.isActive = isActive;
        this.isDeprecated = isDeprecated;
        this.timeLabel = timeLabel;
        this.stage = stage;
    }

    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
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

    public String getTimeLabel() {
        return timeLabel;
    }

    public void setTimeLabel(String timeLabel) {
        this.timeLabel = timeLabel;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public Integer getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(Integer pipelineId) {
        this.pipelineId = pipelineId;
    }

    

    public Collection<Integer> getProcedureCollection() {
        return procedureCollection;
    }

    public void setProcedureCollection(Collection<Integer> procedureCollection) {
        this.procedureCollection = procedureCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (scheduleId != null ? scheduleId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Schedule)) {
            return false;
        }
        Schedule other = (Schedule) object;
        return !((this.scheduleId == null && other.scheduleId != null) || (this.scheduleId != null && !this.scheduleId.equals(other.scheduleId)));
    }

}
