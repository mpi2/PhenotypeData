package org.mousephenotype.cda.indexers.utils;

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
//@Service
public class EmbryoRestGetter {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private String embryoViewerFilename;


	@Inject
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
            embryoStrain.setMgi(jsonObject.getString("mgi"));
            embryoStrain.setCentre(jsonObject.getString("centre"));
            embryoStrain.setUrl(jsonObject.getString("url"));
            if(jsonObject.has("analysis_view_url")){
            	embryoStrain.setAnalysisViewUrl(jsonObject.getString("analysis_view_url"));
            }
            

            List<String> procedureStableKeys = new ArrayList<String>();
            List<String> parameterStableKeys = new ArrayList<String>();
            List<String> modalities          = new ArrayList<String>();

            JSONArray jProcParam = jsonObject.getJSONArray("procedures_parameters");
            for (int j = 0; j < jProcParam.length(); j++) {

                JSONObject jo = (JSONObject) jProcParam.get(j);

                // the procedure_id on the harwell RESTful interface is actually a stable_key
                String procedure_stable_key = jo.getString("procedure_id");
                String parameter_stable_key = jo.getString("parameter_id");
                String modality             = jo.getString("modality");
                procedureStableKeys.add(procedure_stable_key);
                parameterStableKeys.add(parameter_stable_key);

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