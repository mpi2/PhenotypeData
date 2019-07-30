package org.mousephenotype.cda.indexers.utils;

import org.mousephenotype.cda.utilities.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Class for getting the embryo data from the phenoDCC on embryo data available
 *
 * @author jwarren
 *
 */
public class EmbryoRestGetter {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private String embryoViewerFilename;


	public EmbryoRestGetter(String embryoViewerFilename) {
	    this.embryoViewerFilename = embryoViewerFilename;
    }

	public EmbryoRestData getEmbryoRestData() throws JSONException {

        EmbryoRestData     data          = new EmbryoRestData();
        String             content       = readEmbryoViewerFile();
        List<EmbryoStrain> strains       = new ArrayList<>();
        EmbryoStrain       embryoStrain;
        JSONObject         json          = new JSONObject(content);
        JSONArray          coloniesArray = json.getJSONArray("colonies");

        for (int i = 0; i < coloniesArray.length(); i++) {
            JSONObject jsonObject = coloniesArray.getJSONObject(i);
            embryoStrain = new EmbryoStrain();
            embryoStrain.setColonyId(jsonObject.getString("colony_id"));
            embryoStrain.setMgiGeneAccessionId(jsonObject.getString("mgi"));
            embryoStrain.setCentre(jsonObject.getString("centre"));
            embryoStrain.setUrl(jsonObject.getString("url"));
            if(jsonObject.has("analysis_view_url")){
            	embryoStrain.setAnalysisViewUrl(jsonObject.getString("analysis_view_url"));
            }


            List<Long> procedureStableKeys = new ArrayList<>();
            List<Long> parameterStableKeys = new ArrayList<>();
            List<String>  modalities          = new ArrayList<>();

            JSONArray jProcParam = jsonObject.getJSONArray("procedures_parameters");
            for (int j = 0; j < jProcParam.length(); j++) {

                JSONObject jo = (JSONObject) jProcParam.get(j);

                // Harwell's supplied procedure_id and parameter_id fields are cda procedure_stable_key and parameter_stable_key.
                Long procedureStableKey = CommonUtils.tryParseLong(jo.getString("procedure_id"));
                Long parameterStableKey = CommonUtils.tryParseLong(jo.getString("parameter_id"));
                if ((procedureStableKey == null) || (parameterStableKey == null)) {
                    continue;
                }
                String modality             = jo.getString("modality");
                procedureStableKeys.add(procedureStableKey);
                parameterStableKeys.add(parameterStableKey);

                modalities.add(modality.replace("&#956", "micro"));
            }
            embryoStrain.setProcedureStableKeys(procedureStableKeys);
            embryoStrain.setParameterStableKeys(parameterStableKeys);
            embryoStrain.setModalities(modalities);
            strains.add(embryoStrain);
        }

        data.setStrains(strains);

        return data;
    }

	private String readEmbryoViewerFile() {
		StringBuilder retVal = new StringBuilder();

		try (Stream<String> stream = Files.lines(Paths.get(embryoViewerFilename))) {
			stream.forEach(retVal::append);
		} catch (IOException e) {
			logger.warn("Unable to read Embryo Viewer file {}. Reason: {}", embryoViewerFilename, e.getLocalizedMessage());
			e.printStackTrace();
            return null;
		}

		return retVal.toString();
	}
}