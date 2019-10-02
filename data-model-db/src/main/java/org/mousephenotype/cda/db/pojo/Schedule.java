package org.mousephenotype.cda.db.pojo;

import java.io.Serializable;
import java.util.Collection;

public class Schedule implements Serializable {

    private Long             scheduleId;
    private boolean          isActive = true;
    private boolean          isDeprecated;
    private String           timeLabel;
    private String           time;
    private String           timeUnit;
    private String           stage;
    private Long             pipelineId;
    private Collection<Long> procedureCollection;

    public Schedule() {
    }

    public Schedule(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Schedule(Long scheduleId, boolean isActive, boolean isDeprecated, String timeLabel, String stage) {
        this.scheduleId = scheduleId;
        this.isActive = isActive;
        this.isDeprecated = isDeprecated;
        this.timeLabel = timeLabel;
        this.stage = stage;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
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

    public Long getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(Long pipelineId) {
        this.pipelineId = pipelineId;
    }

    

    public Collection<Long> getProcedureCollection() {
        return procedureCollection;
    }

    public void setProcedureCollection(Collection<Long> procedureCollection) {
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