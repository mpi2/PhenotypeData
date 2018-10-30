/*******************************************************************************
 * Copyright © 2018 EMBL - European Bioinformatics Institute
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.cda.loads.create.extract.cdabase.support;

import org.mousephenotype.cda.db.pojo.Datasource;
import org.mousephenotype.cda.db.pojo.Procedure;
import org.mousephenotype.impress2.ImpressProcedure;
import org.mousephenotype.impress2.ImpressSchedule;
import org.mousephenotype.cda.db.pojo.Pipeline;
import org.mousephenotype.cda.db.pojo.Schedule;
import org.mousephenotype.impress2.ImpressPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class ImpressUtils {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @NotNull
    @Value("${impress.service.url}")
    protected String impressServiceUrl;

//    public List<String> getPipelineKeys() {
//
//        String url = impressServiceUrl + "/pipeline/list/keys";
//
//        RestTemplate rt   = new RestTemplate();
//        Object       o    = rt.getForEntity(url, Object.class);
//        Object       body = ((ResponseEntity) o).getBody();
//
//        HashMap<String, String> hm           = ((HashMap<String, String>) body);
//        List<String>            pipelineKeys = new ArrayList(hm.values());
//
//        return pipelineKeys;
//    }

    public List<Pipeline> getPipelines(Datasource datasource) {

        List<Pipeline>        pipelines        = new ArrayList<>();
        List<ImpressPipeline> impressPipelines = getImpressPipelines();

        for (ImpressPipeline impressPipeline : impressPipelines) {

            Pipeline pipeline = toPipeline(impressPipeline, datasource);
            pipelines.add(pipeline);

        }

        return pipelines;
    }

    public List<ImpressPipeline> getImpressPipelines() {

        List<ImpressPipeline> pipelines = new ArrayList<>();

        String url = impressServiceUrl + "/pipeline/list/full";

        RestTemplate rt   = new RestTemplate();
        Object       o    = rt.getForEntity(url, Object.class);
        Object       body = ((ResponseEntity) o).getBody();

        HashMap<String, HashMap<String, Object>> pipelinesMap = (HashMap<String, HashMap<String, Object>>) body;
        for (HashMap<String, Object> pipelineMap : pipelinesMap.values()) {

            ImpressPipeline pipeline = new ImpressPipeline();

            pipeline.setPipelineId((Integer) pipelineMap.get("pipelineId"));
            pipeline.setPipelineKey((String) pipelineMap.get("pipelineKey"));
            pipeline.setPipelineType((String) pipelineMap.get("pipelineType"));
            pipeline.setName((String) pipelineMap.get("name"));
            pipeline.setWeight((Integer) pipelineMap.get("weight"));
            pipeline.setIsVisible((Boolean) pipelineMap.get("isVisible"));
            pipeline.setIsActive((Boolean) pipelineMap.get("isActive"));
            pipeline.setIsDeprecated((Boolean) pipelineMap.get("isDeprecated"));
            pipeline.setMajorVersion((Integer) pipelineMap.get("majorVersion"));
            pipeline.setMinorVersion((Integer) pipelineMap.get("minorVersion"));
            pipeline.setDescription((String) pipelineMap.get("description"));
            pipeline.setIsInternal((Boolean) pipelineMap.get("isInternal"));
            pipeline.setIsDeleted((Boolean) pipelineMap.get("isDeleted"));
            pipeline.setCentreName((String) pipelineMap.get("centreName"));
            pipeline.setImpc(((Integer) pipelineMap.get("impc")).shortValue());
            pipeline.setScheduleCollection((List<Integer>) pipelineMap.get("scheduleCollection"));

            pipelines.add(pipeline);
        }

        return pipelines;
    }

    public Pipeline toPipeline(ImpressPipeline impressPipeline, Datasource datasource) {

        Pipeline pipeline = new Pipeline();

        pipeline.setDatasource(datasource);

        pipeline.setPipelineId(impressPipeline.getPipelineId());
        pipeline.setPipelineKey(impressPipeline.getPipelineKey());
        pipeline.setPipelineType(impressPipeline.getPipelineType());
        pipeline.setName(impressPipeline.getName());
        pipeline.setWeight(impressPipeline.getWeight());
        pipeline.setIsVisible(impressPipeline.getIsVisible());
        pipeline.setIsActive(impressPipeline.getIsActive());
        pipeline.setIsDeprecated(impressPipeline.getIsDeprecated());
        pipeline.setMajorVersion(impressPipeline.getMajorVersion());
        pipeline.setMinorVersion(impressPipeline.getMinorVersion());
        pipeline.setDescription(impressPipeline.getDescription());
        pipeline.setIsInternal(impressPipeline.getIsInternal());
        pipeline.setIsDeleted(impressPipeline.getIsDeleted());
        pipeline.setCentreName(impressPipeline.getCentreName());
        pipeline.setImpc(impressPipeline.getImpc());
        pipeline.setScheduleCollection(impressPipeline.getScheduleCollection());

        return pipeline;
    }


    // SCHEDULE


    public Schedule getSchedule(int scheduleId, Datasource datasource) {

        ImpressSchedule impressSchedule = getImpressSchedule(scheduleId);

        return toSchedule(impressSchedule, datasource);
    }

    public ImpressSchedule getImpressSchedule(int scheduleId) {

        ImpressSchedule impressSchedule = new ImpressSchedule();
        String url = impressServiceUrl + "/schedule/" + scheduleId;

        RestTemplate rt   = new RestTemplate();
        Object       o    = rt.getForEntity(url, Object.class);
        Object       body = ((ResponseEntity) o).getBody();

        HashMap<String, Object> impressScheduleMap = (HashMap<String, Object>) body;

        impressSchedule.setScheduleId((Integer) impressScheduleMap.get("scheduleId"));
        impressSchedule.setIsActive((Boolean) impressScheduleMap.get("isActive"));
        impressSchedule.setIsDeprecated((Boolean) impressScheduleMap.get("isDeprecated"));
        impressSchedule.setTimeLabel((String) impressScheduleMap.get("timeLabel"));
        impressSchedule.setTime((String) impressScheduleMap.get("time"));
        impressSchedule.setTimeUnit((String) impressScheduleMap.get("timeUnit"));
        impressSchedule.setStage((String) impressScheduleMap.get("stage"));
        impressSchedule.setPipelineId((Integer) impressScheduleMap.get("pipelineId"));
        impressSchedule.setProcedureCollection((List<Integer>) impressScheduleMap.get("procedureCollection"));

        return impressSchedule;
    }
    
    public Schedule toSchedule(ImpressSchedule impressSchedule, Datasource datasource) {

        Schedule schedule = new Schedule();

        schedule.setIsActive(impressSchedule.getIsActive());
        schedule.setIsDeprecated(impressSchedule.getIsDeprecated());
        schedule.setPipelineId(impressSchedule.getPipelineId());
        schedule.setProcedureCollection(impressSchedule.getProcedureCollection());
        schedule.setScheduleId(impressSchedule.getScheduleId());
        schedule.setStage(impressSchedule.getStage());
        schedule.setTime(impressSchedule.getTime());
        schedule.setTimeLabel(impressSchedule.getTimeLabel());
        schedule.setTimeUnit(impressSchedule.getTimeUnit());

        return schedule;
    }


    // PROCEDURE


    public Procedure getProcedure(int procedureId, Datasource datasource) {

        ImpressProcedure impressProcedure = getImpressProcedure(procedureId);

        return toProcedure(impressProcedure, datasource);
    }

    public ImpressProcedure getImpressProcedure(int procedureId) {

        ImpressProcedure impressProcedure = new ImpressProcedure();
        String url = impressServiceUrl + "/procedure/" + procedureId;

        try {

            RestTemplate rt   = new RestTemplate();
            Object       o    = rt.getForEntity(url, Object.class);
            Object       body = ((ResponseEntity) o).getBody();

            HashMap<String, Object> procedureMap = (HashMap<String, Object>) body;

            impressProcedure.setProcedureId((Integer) procedureMap.get("procedureId"));
            impressProcedure.setProcedureKey((String) procedureMap.get("procedureKey"));
//            impressProcedure.setMinFemales(((Integer) procedureMap.get("minFemales")).shortValue());
//            impressProcedure.setMinMales(((Integer) procedureMap.get("minMales")).shortValue());

            o = procedureMap.get("minFemales");
            if (o == null) { logger.info("procedureId {}: minFemales is null. Setting to 0.", procedureId); }
            short s = (o == null ? 0 : ((Integer) o).shortValue());
            impressProcedure.setMinFemales(s);

            o = procedureMap.get("minMales");
            if (o == null) { logger.info("procedureId {}: minMales is null. Setting to 0.", procedureId); }
            s = (o == null ? 0 : ((Integer) o).shortValue());
            impressProcedure.setMinMales(s);

            o = procedureMap.get("minAnimals");
            if (o == null) { logger.info("procedureId {}: minAnimals is null. Setting to 0.", procedureId); }
            s = (o == null ? 0 : ((Integer) o).shortValue());
            impressProcedure.setMinAnimals(s);

//            impressProcedure.setMinAnimals(((Integer) procedureMap.get("minAnimals")).shortValue());
            impressProcedure.setIsVisible((Boolean) procedureMap.get("isVisible"));
            impressProcedure.setIsMandatory((Boolean) procedureMap.get("isMandatory"));
            impressProcedure.setIsInternal((Boolean) procedureMap.get("isInternal"));
            impressProcedure.setName((String) procedureMap.get("name"));
            impressProcedure.setType((Integer) procedureMap.get("type"));
            impressProcedure.setLevel((String) procedureMap.get("level"));
            impressProcedure.setMajorVersion((Integer) procedureMap.get("majorVersion"));
            impressProcedure.setMinorVersion((Integer) procedureMap.get("minorVersion"));
            impressProcedure.setDescription((String) procedureMap.get("description"));
            impressProcedure.setOldProcedureKey((String) procedureMap.get("oldProcedureKey"));
            impressProcedure.setParameterCollection((List<Integer>) procedureMap.get("parameterCollection"));
        } catch (Exception e) {
            return null;
        }

        return impressProcedure;
    }

    public Procedure toProcedure(ImpressProcedure impressProcedure, Datasource datasource) {

        if (impressProcedure == null) {
            return null;
        }

        Procedure procedure = new Procedure();

        procedure.setDatasource(datasource);

        procedure.setProcedureId(impressProcedure.getProcedureId());
        procedure.setProcedureKey(impressProcedure.getProcedureKey());
        procedure.setMinFemales(impressProcedure.getMinFemales());
        procedure.setMinMales(impressProcedure.getMinMales());
        procedure.setMinAnimals(impressProcedure.getMinAnimals());
        procedure.setIsVisible(impressProcedure.getIsVisible());
        procedure.setIsMandatory(impressProcedure.getIsMandatory());
        procedure.setIsInternal(impressProcedure.getIsInternal());
        procedure.setName(impressProcedure.getName());
        procedure.setType(impressProcedure.getType());
        procedure.setLevel(impressProcedure.getLevel());
        procedure.setMajorVersion(impressProcedure.getMajorVersion());
        procedure.setMinorVersion(impressProcedure.getMinorVersion());
        procedure.setDescription(impressProcedure.getDescription());
        procedure.setOldProcedureKey(impressProcedure.getOldProcedureKey());
        procedure.setParameterCollection(impressProcedure.getParameterCollection());

        return procedure;
    }
}