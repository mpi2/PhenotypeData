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

import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.impress2.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;
import java.util.*;

@Component
public class ImpressUtils {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @NotNull
    @Value("${impress.service.url}")
    protected String impressServiceUrl;


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

        try {
            for (HashMap<String, Object> pipelineMap : pipelinesMap.values()) {

                ImpressPipeline pipeline = new ImpressPipeline();

                pipeline.setPipelineId((Integer) pipelineMap.get("pipelineId"));
                pipeline.setPipelineKey((String) pipelineMap.get("pipelineKey"));
                pipeline.setPipelineType((String) pipelineMap.get("pipelineType"));
                pipeline.setName((String) pipelineMap.get("name"));
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

        } catch (Exception e) {

            e.printStackTrace();
            logger.warn(e.getLocalizedMessage());
            return null;
        }

        return pipelines;
    }

    public Pipeline toPipeline(ImpressPipeline impressPipeline, Datasource datasource) {

        Pipeline pipeline = new Pipeline();

        pipeline.setDatasource(datasource);

        pipeline.setStableKey(impressPipeline.getPipelineId());
        pipeline.setStableId(impressPipeline.getPipelineKey());
        pipeline.setMajorVersion(impressPipeline.getMajorVersion());
        pipeline.setMinorVersion(impressPipeline.getMinorVersion());
        pipeline.setDescription(impressPipeline.getDescription());
        pipeline.setName(impressPipeline.getName());
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
        String          url             = impressServiceUrl + "/schedule/" + scheduleId;

        RestTemplate rt   = new RestTemplate();
        Object       o    = rt.getForEntity(url, Object.class);
        Object       body = ((ResponseEntity) o).getBody();

        HashMap<String, Object> impressScheduleMap = (HashMap<String, Object>) body;

        try {
            impressSchedule.setScheduleId((Integer) impressScheduleMap.get("scheduleId"));
            impressSchedule.setIsActive((Boolean) impressScheduleMap.get("isActive"));
            impressSchedule.setIsDeprecated((Boolean) impressScheduleMap.get("isDeprecated"));
            impressSchedule.setTimeLabel((String) impressScheduleMap.get("timeLabel"));
            impressSchedule.setTime((String) impressScheduleMap.get("time"));
            impressSchedule.setTimeUnit((String) impressScheduleMap.get("timeUnit"));
            impressSchedule.setStage((String) impressScheduleMap.get("stage"));
            impressSchedule.setPipelineId((Integer) impressScheduleMap.get("pipelineId"));
            impressSchedule.setProcedureCollection((List<Integer>) impressScheduleMap.get("procedureCollection"));

        } catch (Exception e) {

            e.printStackTrace();
            logger.warn(e.getLocalizedMessage());
            return null;
        }

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
        schedule.setProcedureCollection(impressSchedule.getProcedureCollection());

        return schedule;
    }


    // PROCEDURE


    public Procedure getProcedure(int procedureId, Datasource datasource) {

        ImpressProcedure impressProcedure = getImpressProcedure(procedureId);

        return toProcedure(impressProcedure, datasource);
    }

    public ImpressProcedure getImpressProcedure(int procedureId) {

        ImpressProcedure impressProcedure = new ImpressProcedure();
        String           url              = impressServiceUrl + "/procedure/" + procedureId;

        try {

            RestTemplate rt   = new RestTemplate();
            Object       o    = rt.getForEntity(url, Object.class);
            Object       body = ((ResponseEntity) o).getBody();

            HashMap<String, Object> procedureMap = (HashMap<String, Object>) body;

            impressProcedure.setProcedureId((Integer) procedureMap.get("procedureId"));
            impressProcedure.setProcedureKey((String) procedureMap.get("procedureKey"));

            o = procedureMap.get("minFemales");
            impressProcedure.setMinFemales(o == null ? null : ((Integer) o).shortValue());

            o = procedureMap.get("minMales");
            impressProcedure.setMinMales(o == null ? null : ((Integer) o).shortValue());

            o = procedureMap.get("minAnimals");
            impressProcedure.setMinAnimals(o == null ? null : ((Integer) o).shortValue());

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
            impressProcedure.setScheduleId((Integer) procedureMap.get("scheduleId"));

        } catch (Exception e) {

            e.printStackTrace();
            logger.warn(e.getLocalizedMessage());
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

        procedure.setStableKey(impressProcedure.getProcedureId());
        procedure.setStableId(impressProcedure.getProcedureKey());
        procedure.setMajorVersion(impressProcedure.getMajorVersion());
        procedure.setMinorVersion(impressProcedure.getMinorVersion());
        procedure.setDescription(impressProcedure.getDescription());
        procedure.setName(impressProcedure.getName());
        procedure.setLevel(impressProcedure.getLevel());
        procedure.setParameterCollection(impressProcedure.getParameterCollection());
        procedure.setScheduleKey(impressProcedure.getScheduleId());

        return procedure;
    }


    // PARAMETER


    public Parameter getParameter(int parameterId, Datasource datasource, Map<Integer, String> unitsById) {

        ImpressParameter impressParameter = getImpressParameter(parameterId);

        return toParameter(impressParameter, datasource, unitsById);
    }

    public ImpressParameter getImpressParameter(int parameterId) {

        ImpressParameter impressParameter = new ImpressParameter();
        String           url              = impressServiceUrl + "/parameter/" + parameterId;

        try {

            RestTemplate rt   = new RestTemplate();
            Object       o    = rt.getForEntity(url, Object.class);
            Object       body = ((ResponseEntity) o).getBody();

            HashMap<String, Object> parameterMap = (HashMap<String, Object>) body;

            impressParameter.setParameterId((Integer) parameterMap.get("parameterId"));
            impressParameter.setParameterKey((String) parameterMap.get("parameterKey"));
            impressParameter.setType((String) parameterMap.get("type"));
            impressParameter.setName((String) parameterMap.get("name"));
            impressParameter.setIsVisible((Boolean) parameterMap.get("isVisible"));
            impressParameter.setIsActive((Boolean) parameterMap.get("isActive"));
            impressParameter.setIsDeprecated((Boolean) parameterMap.get("isDeprecated"));
            impressParameter.setMajorVersion((Integer) parameterMap.get("majorVersion"));
            impressParameter.setMinorVersion((Integer) parameterMap.get("minorVersion"));
            impressParameter.setDerivation((String) parameterMap.get("derivation"));
            impressParameter.setDescription((String) parameterMap.get("description"));
            impressParameter.setIsAnnotation((Boolean) parameterMap.get("isAnnotation"));
            impressParameter.setIsDerived((Boolean) parameterMap.get("isDerived"));
            impressParameter.setIsImportant((Boolean) parameterMap.get("isImportant"));
            impressParameter.setIsIncrement((Boolean) parameterMap.get("isIncrement"));
            impressParameter.setIsMedia((Boolean) parameterMap.get("isMedia"));
            impressParameter.setIsOption((Boolean) parameterMap.get("isOption"));
            impressParameter.setIsRequired((Boolean) parameterMap.get("isRequired"));
            impressParameter.setQcCheck((Boolean) parameterMap.get("qcCheck"));

            Double d = (Double) parameterMap.get("qcMin");
            impressParameter.setQcMin(d == null ? null : d.floatValue());

            d = (Double) parameterMap.get("qcMax");
            impressParameter.setQcMax(d == null ? null : d.floatValue());

            impressParameter.setQcNotes((String) parameterMap.get("qcNotes"));
            impressParameter.setValueType((String) parameterMap.get("valueType"));
            impressParameter.setGraphType((String) parameterMap.get("graphType"));
            impressParameter.setDataAnalysisNotes((String) parameterMap.get("dataAnalysisNotes"));
            impressParameter.setIsInternal((Boolean) parameterMap.get("isInternal"));
            impressParameter.setIsDeleted((Boolean) parameterMap.get("isDeleted"));
            impressParameter.setOldParameterKey((String) parameterMap.get("oldParameterKey"));
            impressParameter.setOriginalParamId((Integer) parameterMap.get("originalParamId"));
            impressParameter.setOntologyGroupId((Integer) parameterMap.get("ontologyGroupId"));
            impressParameter.setWeight((Integer) parameterMap.get("weight"));
            impressParameter.setProcedureId((Integer) parameterMap.get("procedureId"));

            Integer      unitId       = (Integer) parameterMap.get("unit");
            ImpressUnits impressUnits = null;
            if (unitId != null) {
                impressUnits = new ImpressUnits();
                impressUnits.setId(unitId);
            }
            impressParameter.setUnit(impressUnits);

            impressParameter.setIncrementCollection((List<Integer>) parameterMap.get("incrementCollection"));
            impressParameter.setOptionCollection((List<Integer>) parameterMap.get("optionCollection"));
            impressParameter.setMptermCollection((List<Integer>) parameterMap.get("mptermCollection"));

        } catch (Exception e) {

            e.printStackTrace();
            logger.warn(e.getLocalizedMessage());
            return null;
        }

        return impressParameter;
    }

    public Parameter toParameter(ImpressParameter impressParameter, Datasource datasource, Map<Integer, String> unitsById) {

        if (impressParameter == null) {
            return null;
        }

        Parameter parameter = new Parameter();

        parameter.setDatasource(datasource);

        parameter.setStableKey(impressParameter.getParameterId());
        parameter.setStableId(impressParameter.getParameterKey());
        parameter.setType(impressParameter.getType());
        parameter.setName(impressParameter.getName());
        parameter.setMajorVersion(impressParameter.getMajorVersion());
        parameter.setMinorVersion(impressParameter.getMinorVersion());
        parameter.setDescription(impressParameter.getDescription());
        parameter.setAnnotateFlag(impressParameter.getIsAnnotation());
        parameter.setDerivedFlag(impressParameter.getIsDerived());
        parameter.setImportantFlag(impressParameter.getIsImportant());
        parameter.setIncrementFlag(impressParameter.getIsIncrement());
        parameter.setMediaFlag(impressParameter.getIsMedia());
        parameter.setOptionsFlag(impressParameter.getIsOption());
        parameter.setRequiredFlag(impressParameter.getIsRequired());
        parameter.setDatatype(impressParameter.getValueType());
        parameter.setDataAnalysisNotes(impressParameter.getDataAnalysisNotes());

        ImpressUnits unit = impressParameter.getUnit();
        parameter.setUnit(unit == null ? null : unitsById.get(unit.getId()));

        return parameter;
    }


    // INCREMENT


    public List<ParameterIncrement> getIncrements(int parameterId) {

        List<ParameterIncrement> increments = new ArrayList<>();

        List<ImpressIncrement> impressIncrements = getImpressIncrements(parameterId);

        for (ImpressIncrement impressIncrement : impressIncrements) {
            ParameterIncrement increment = toIncrement(impressIncrement);
            increments.add(increment);
        }

        return increments;
    }

    public List<ImpressIncrement> getImpressIncrements(Integer parameterId) {

        List<ImpressIncrement> increments = new ArrayList<>();

        String url = impressServiceUrl + "/increment/belongingtoparameter/full/" + parameterId;

        RestTemplate rt   = new RestTemplate();
        Object       o    = rt.getForEntity(url, Object.class);
        Object       body = ((ResponseEntity) o).getBody();

        List<Map<String, Object>> list = (List<Map<String, Object>>) body;

        try {
            for (Map<String, Object> map : list) {

                ImpressIncrement increment = new ImpressIncrement();

                increment.setIncrementId((Integer) map.get("incrementId"));

                try {
                    increment.setWeight((Integer) map.get("weight"));
                } catch (NullPointerException e) {
                    logger.warn("weight for parameterId {} is NULL! url = {}", parameterId, url);
                }

                increment.setIsActive((Boolean) map.get("isActive"));

                o = map.get("incrementString");
                increment.setIncrementString(o == null ? "" : (String) o);

                o = map.get("incrementType");
                increment.setIncrementType(o == null ? "" : (String) o);

                o = map.get("incrementUnit");
                increment.setIncrementUnit(o == null ? "" : (String) o);

                o = map.get("incrementMin");
                increment.setIncrementMin(o == null ? null : (Integer) o);

                increment.setIsDeleted((Boolean) map.get("isDeleted"));

                o = map.get("originalId");
                increment.setOriginalId(o == null ? null : (Integer) o);

                increment.setParameterId((Integer) map.get("parameterId"));

                increments.add(increment);
            }

        } catch (Exception e) {

            e.printStackTrace();
            logger.warn(e.getLocalizedMessage());
            return null;
        }

        return increments;
    }

    public ParameterIncrement toIncrement(ImpressIncrement impressIncrement) {

        if (impressIncrement == null) {
            return null;
        }

        ParameterIncrement increment = new ParameterIncrement();


        Integer i = impressIncrement.getIncrementMin();
        increment.setMinimum(i == null ? "" : i.toString());

        increment.setUnit(impressIncrement.getIncrementUnit() == null ? "" : impressIncrement.getIncrementUnit());

        increment.setDataType(impressIncrement.getIncrementType() == null ? "" : impressIncrement.getIncrementType());

        increment.setValue(impressIncrement.getIncrementString());

        return increment;
    }


    // OPTION


    public List<ParameterOption> getOptions(Parameter parameter, Set<String> normalCategory) {

        List<ParameterOption> options = new ArrayList<>();

        List<ImpressOption> impressOptions = getImpressOptions(parameter.getStableKey());

        for (ImpressOption impressOption : impressOptions) {
            ParameterOption option = toOption(impressOption, parameter.getStableId(), normalCategory);
            options.add(option);
        }

        return options;
    }

    public List<ImpressOption> getImpressOptions(Integer parameterId) {

        List<ImpressOption> options = new ArrayList<>();

        String url = impressServiceUrl + "/option/belongingtoparameter/full/" + parameterId;

        RestTemplate rt   = new RestTemplate();
        Object       o    = rt.getForEntity(url, Object.class);
        Object       body = ((ResponseEntity) o).getBody();

        List<HashMap<String, Object>> impressOptionsList = (List<HashMap<String, Object>>) body;

        try {

            for (HashMap<String, Object> map : impressOptionsList) {

                ImpressOption option = new ImpressOption();

                option.setOptionId((Integer) map.get("optionId"));
                option.setPhoWeight((Integer) map.get("phoweight"));

                o = map.get("parentId");
                option.setParentId(o == null ? null : (Integer) o);

                o = map.get("name");
                option.setName(o == null ? "" : (String) o);

                option.setIsDefault((Boolean) map.get("isDefault"));
                option.setIsActive((Boolean) map.get("isActive"));
                option.setPoWeight((Integer) map.get("poweight"));

                o = map.get("description");
                option.setDescription(o == null ? "" : (String) o);

                option.setIsDeleted((Boolean) map.get("isDeleted"));

                option.setParameterId((Integer) map.get("parameterId"));

                options.add(option);
            }

        } catch (Exception e) {

            e.printStackTrace();
            logger.warn(e.getLocalizedMessage());
            return null;
        }

        return options;
    }

    public ParameterOption toOption(ImpressOption impressOption, String parameterStableId, Set<String> normalCategory) {

        if (impressOption == null) {
            return null;
        }

        ParameterOption option = new ParameterOption();

        option.setName(impressOption.getName());
        option.setDescription(impressOption.getDescription());

        // This is the same format as the populate method, parameterStableId_NormalOption
        String candidate = parameterStableId + "_" + option.getName();
        option.setNormalCategory(normalCategory.contains(candidate));

        return option;
    }


    // UNITS


    public Map<Integer, String> getUnits() {

        Map<Integer, String> units = new HashMap<>();

        String url = impressServiceUrl + "/unit/list";

        RestTemplate rt   = new RestTemplate();
        Object       o    = rt.getForEntity(url, Object.class);
        Object       body = ((ResponseEntity) o).getBody();

        HashMap<String, String> unitsMap = (HashMap<String, String>) body;
        for (Map.Entry<String, String> entry : unitsMap.entrySet()) {
            units.put(Integer.parseInt(entry.getKey()), entry.getValue());
        }

        return units;
    }
}