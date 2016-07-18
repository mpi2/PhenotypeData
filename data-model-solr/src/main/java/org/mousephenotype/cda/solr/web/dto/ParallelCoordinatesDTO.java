/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/

package org.mousephenotype.cda.solr.web.dto;

import net.sf.json.JSONObject;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;

import java.util.*;

/**
 * @since 2015/07
 * @author tudose
 *
 */
public class ParallelCoordinatesDTO {

	public static final String DEFAULT = "Default value"; 
	public static final String MEAN = "Mean value"; 
	public static final String GROUP_WT = "WT";
	public static final String GROUP_MUTANT = "Mutant";

	String group;
	String geneSymbol;
	String geneAccession;
	HashMap<String, MeanBean> values;
	List<ParameterDTO> allColumns;


	public ParallelCoordinatesDTO(String geneSymbol, String geneAccession, String group, List<ParameterDTO> allColumns){

		this.geneAccession = geneAccession;
		this.geneSymbol = geneSymbol;
		this.group = group;
		values = new HashMap<>();
		this.allColumns = allColumns;

		for (ParameterDTO parameter: allColumns){
			if (getSortValue(parameter.getStableId()) % 100 != 0){
				values.put(parameter.getName(), new MeanBean( null, parameter.getStableId(), parameter.getName(), parameter.getStableKey(), null));
			}
		}
	}


	public void addValue( String unit, String parameterStableId, String parameterName, Integer parameterStableKey, Double mean){

		if (getSortValue(parameterStableId) % 100 != 0){
			values.put(parameterName, new MeanBean( unit, parameterStableId, parameterName, parameterStableKey, mean));
		}

	}


	public String toString(boolean onlyComplete){

		String res = "";

		if (onlyComplete && isComplete() || !onlyComplete){
			res += "\"name\": \"" + geneSymbol + "(" + geneAccession + ")\",";
			res += "\"group\": \"" + group + "\",";
			int i = 0;

			if (this.values.values().size() > 0){

                List <MeanBean> values = new ArrayList<MeanBean>(this.values.values());
				Collections.sort(values, this.values.values().iterator().next().getComparatorByTerry());

				for (MeanBean mean : values){
					res += "\"" + mean.parameterName + "\": ";
					res += mean.mean;
					i++;
					if (i < this.values.size()){
						res +=", ";
					}
				}
			}
		}
		return res;
	}


	public boolean isComplete(){

		boolean complete = true;
		for (MeanBean row: values.values()){
			if (row.mean == null){
				complete = false;
				System.out.println(this.geneSymbol + " not complete");
				break;
			}
		}
		return complete;
	}

	public JSONObject getJson(){

		JSONObject obj = new JSONObject();
		obj.accumulate("name", this.geneSymbol);
		obj.accumulate("group", "default gene group");
		for (MeanBean mean: this.values.values()){
			obj.accumulate(mean.parameterName, mean.mean);
		}
		return obj;
	}

	public HashMap<String, MeanBean> getValues(){
		return values;
	}

	public class MeanBean{

		String unit;
		String parameterStableId;
		String parameterName;
		Integer parameterStableKey;
		Double mean;

		public MeanBean(String unit, String parameterStableId,
		String parameterName, Integer parameterStableKey, Double mean){
			this.unit = unit;
			this.parameterName = parameterName;
			this.parameterStableId = parameterStableId;
			this.parameterStableKey = parameterStableKey;
			this.mean = mean;
		}
		public Double getMean(){
			return mean;
		}
		public String getUnit() {
			return unit;
		}
		public void setUnit(String unit) {
			this.unit = unit;
		}
		public String getParameterStableId() {
			return parameterStableId;
		}
		public void setParameterStableId(String parameterStableId) {
			this.parameterStableId = parameterStableId;
		}
		public String getParameterName() {
			return parameterName;
		}
		public void setParameterName(String parameterName) {
			this.parameterName = parameterName;
		}
		public Integer getParameterStableKey() {
			return parameterStableKey;
		}
		public void setParameterStableKey(Integer parameterStableKey) {
			this.parameterStableKey = parameterStableKey;
		}
		public void setMean(Double mean) {
			this.mean = mean;
		}

		/**
		 * @author tudose
		 * @return
		 */
		public Comparator<MeanBean> getComparatorByParameterName()
		{
			Comparator<MeanBean> comp = new Comparator<MeanBean>(){
		    @Override
		    public int compare(MeanBean s1, MeanBean s2)
		    {
		        return s1.parameterName.compareTo(s2.parameterName);
		    }
			};
			return comp;
		}

		/**
		 * @author tudose
		 * @since 2015/08/04
		 * @return
		 */
		public Comparator<MeanBean> getComparatorByTerry()
		{
			Comparator<MeanBean> comp = new Comparator<MeanBean>(){
			    @Override
			    public int compare(MeanBean a, MeanBean b)
			    {
			    	Integer valA = getSortValue(a.getParameterStableId());
			    	Integer valB = getSortValue(b.getParameterStableId());
	 		        return valA.compareTo(valB);
			    }
			};
			return comp;
		}

	}

	private Integer getHash(String str){
		Integer hash=7;
		for (int i=0; i < str.length(); i++) {
		    hash = hash * 31 + str.charAt(i);
		}
		return hash;
	}

	/**
	 * @author ilinca
	 * @since 2015/10/29
	 * @param parameterStableId
	 * @return
	 */
	private int getSortValue(String parameterStableId){
		if (sortMap.containsKey(parameterStableId)){
			return sortMap.get(parameterStableId);
		} else {
			System.out.println("WARNING: Parameter unsorted " + parameterStableId + ". Aks Terry or Nat to fing a place for it in the order we have for each procedure." ); 
			int hash = 100000 + getHash(parameterStableId);
			if (hash % 100 == 0){
				hash += 1;
			}
			sortMap.put(parameterStableId, hash);
			return hash;
		}
	}

	
	/**	
	 * @author ilinca
	 * Sorting done by Terry, based on how parameters make most biological sense.
	 * See e-mail from 2015/08/03.
	 * 0 means no display. Since I added the procedure prefix 0 from his list translates to x%100 = 0.
	 * Anything >0 is the sorting order.
	 * Nathalie also made some changes to it.
	 */
	private static Map<String, Integer> sortMap;
	static {
			sortMap = new HashMap<String, Integer>();
			sortMap.put("IMPC_DXA_001_001",1002); //Body weight,2,1000
			sortMap.put("IMPC_DXA_002_001",1003); //Fat mass,3,1000
			sortMap.put("IMPC_DXA_003_001",1005); //Lean mass,5,1000
			sortMap.put("IMPC_DXA_004_001",1007); //Bone Mineral Density (excluding skull),7,1000
			sortMap.put("IMPC_DXA_005_001",1008); //Bone Mineral Content (excluding skull),8,1000
			sortMap.put("IMPC_DXA_006_001",1001); //Body length,1,1000
			sortMap.put("IMPC_DXA_007_001",1009); //BMC/Body weight,9,1000
			sortMap.put("IMPC_DXA_008_001",1006); //Lean/Body weight,6,1000
			sortMap.put("IMPC_DXA_009_001",1004); //Fat/Body weight,4,1000
			sortMap.put("IMPC_DXA_010_001",1010); //Bone Area (BMC/BMD),10,1000
			sortMap.put("IMPC_HEM_004_001",1103); //Hematocrit,3,1100
			sortMap.put("IMPC_HEM_005_001",1104); //Mean cell volume,4,1100
			sortMap.put("IMPC_HEM_006_001",1105); //Mean corpuscular hemoglobin,5,1100
			sortMap.put("IMPC_HEM_007_001",1106); //Mean cell hemoglobin concentration,6,1100
			sortMap.put("IMPC_HEM_008_001",1108); //Platelet count,8,1100
			sortMap.put("IMPC_HEM_001_001",1110); //White blood cell count,10,1100
			sortMap.put("IMPC_HEM_002_001",1101); //Red blood cell count,1,1100
			sortMap.put("IMPC_HEM_003_001",1102); //Hemoglobin,2,1100
			sortMap.put("IMPC_HEM_032_001",1113); //Lymphocyte cell count,13,1100
			sortMap.put("IMPC_HEM_034_001",1114); //Monocyte cell count,14,1100
			sortMap.put("IMPC_HEM_036_001",1115); //Eosinophil cell count,15,1100
			sortMap.put("IMPC_HEM_038_001",1122); //Basophil differential count,22,1100
			sortMap.put("IMPC_HEM_027_001",1107); //Red blood cell distribution width,7,1100
			sortMap.put("IMPC_HEM_030_001",1112); //Neutrophil cell count,12,1100
			sortMap.put("IMPC_HEM_037_001",1116); //Basophil cell count,16,1100
			sortMap.put("IMPC_HEM_019_001",1109); //Mean platelet volume,9,1100
			sortMap.put("IMPC_HEM_035_001",1121); //Eosinophil differential count,21,1100
			sortMap.put("IMPC_HEM_029_001",1118); //Neutrophil differential count,18,1100
			sortMap.put("IMPC_HEM_031_001",1119); //Lymphocyte differential count,19,1100
			sortMap.put("IMPC_HEM_033_001",1120); //Monocyte differential count,20,1100
			sortMap.put("IMPC_HEM_040_001",1123); //Large Unstained Cell (LUC) differential count,23,1100
			sortMap.put("IMPC_HEM_039_001",1117); //Large Unstained Cell (LUC) count,17,1100
			sortMap.put("IMPC_GRS_009_001",1202); //Forelimb and hindlimb grip strength measurement mean,2,1200
			sortMap.put("IMPC_GRS_003_001",1203); //Body weight,3,1200
			sortMap.put("IMPC_GRS_008_001",1201); //Forelimb grip strength measurement mean,1,1200
			sortMap.put("IMPC_GRS_011_001",1205); //Forelimb and hindlimb grip strength normalised against body weight,5,1200
			sortMap.put("IMPC_GRS_010_001",1204); //Forelimb grip strength normalised against body weight,4,1200
			sortMap.put("JAX_LDT_009_001",1305); //Percent time in light,5,1300
			sortMap.put("JAX_LDT_001_001",1307); //Side changes,7,1300
			sortMap.put("JAX_LDT_003_001",1301); //Time mobile light side,1,1300
			sortMap.put("JAX_LDT_005_001",1302); //Time mobile dark side,2,1300
			sortMap.put("JAX_LDT_008_001",1306); //Percent time in dark,6,1300
			sortMap.put("JAX_LDT_002_001",1303); //Light side time spent,3,1300
			sortMap.put("JAX_LDT_004_001",1304); //Dark side time spent,4,1300
			sortMap.put("JAX_LDT_006_001",1310); //Fecal boli,10,1300
			sortMap.put("JAX_LDT_007_001",1309); //Latency to first transition into dark,9,1300
			sortMap.put("JAX_PLC_003_001",1404); //Triglycerides,4,1400
			sortMap.put("JAX_PLC_004_001",1401); //Glucose,1,1400
			sortMap.put("JAX_PLC_005_001",1405); //Free fatty acids,5,1400
			sortMap.put("JAX_PLC_001_001",1402); //Total cholesterol,2,1400
			sortMap.put("JAX_PLC_002_001",1403); //HDL cholesterol,3,1400
			sortMap.put("JAX_SLW_009_001",1504); //Dark sleep bout lengths mean,4,1500
			sortMap.put("JAX_SLW_010_001",1505); //Dark sleep bout lengths standard deviation,5,1500
			sortMap.put("JAX_SLW_001_001",1500); //Data confidence level,0,1500
			sortMap.put("JAX_SLW_007_001",1501); //Light sleep bout lengths mean,1,1500
			sortMap.put("JAX_SLW_008_001",1502); //Light sleep bout lengths standard deviation,2,1500
			sortMap.put("JAX_SLW_012_001",1513); //Peak wake with respect to dark onset median,13,1500
			sortMap.put("JAX_SLW_005_001",1508); //Sleep bout lengths mean,8,1500
			sortMap.put("JAX_SLW_006_001",1509); //Sleep bout lengths standard deviation,9,1500
			sortMap.put("JAX_SLW_002_001",1507); //Sleep daily percent,7,1500
			sortMap.put("JAX_SLW_004_001",1506); //Sleep dark phase percent,6,1500
			sortMap.put("JAX_SLW_003_001",1503); //Sleep light phase percent,3,1500
			sortMap.put("JAX_SLW_015_001",1500); //Test duration,0,1500
			sortMap.put("JAX_SLW_013_001",1510); //Breath rate during sleep mean,10,1500
			sortMap.put("JAX_SLW_014_001",1511); //Breath rate during sleep standard deviation,11,1500
			sortMap.put("JAX_SLW_011_001",1512); //Activity onset with respect to dark onset median,12,1500
			sortMap.put("JAX_ROT_003_001",1605); //Learning difference,5,1600
			sortMap.put("JAX_ROT_004_001",1604); //Learning slope,4,1600
			sortMap.put("JAX_ROT_002_001",1603); //Average duration,3,1600
			sortMap.put("HMGU_ROT_002_001",1602); //Latency to fall_Mean,2,1600
			sortMap.put("HMGU_ROT_004_001",1601); //Body weight,1,1600
			sortMap.put("JAX_TLS_002_001",1702); //Latency to immobility,2,1700
			sortMap.put("JAX_TLS_001_001",1701); //Time immobile,1,1700
			sortMap.put("IMPC_ABR_006_001",1803); //12kHz-evoked ABR Threshold,3,1800
			sortMap.put("IMPC_ABR_010_001",1805); //24kHz-evoked ABR Threshold,5,1800
			sortMap.put("IMPC_ABR_004_001",1802); //6kHz-evoked ABR Threshold,2,1800
			sortMap.put("IMPC_ABR_008_001",1804); //18kHz-evoked ABR Threshold,4,1800
			sortMap.put("IMPC_ABR_012_001",1806); //30kHz-evoked ABR Threshold,6,1800
			sortMap.put("IMPC_ABR_002_001",1801); //Click-evoked ABR threshold,1,1800
			sortMap.put("IMPC_ABR_001_001",1800); //Body weight,0,1800
			sortMap.put("IMPC_ECH_006_001",1906); //Cardiac Output,6,1900
			sortMap.put("IMPC_ECH_007_001",1907); //LVAWd,7,1900
			sortMap.put("IMPC_ECH_008_001",1908); //LVIDd,8,1900
			sortMap.put("IMPC_ECH_009_001",1909); //LVPWd,9,1900
			sortMap.put("IMPC_ECH_010_001",1910); //LVAWs,10,1900
			sortMap.put("IMPC_ECH_011_001",1911); //LVIDs,11,1900
			sortMap.put("IMPC_ECH_012_001",1912); //LVPWs,12,1900
			sortMap.put("IMPC_ECH_018_001",1913); //Respiration Rate,13,1900
			sortMap.put("IMPC_ECH_014_001",1914); //Body Temp,14,1900
			sortMap.put("IMPC_ECH_013_001",1915); //HR,15,1900
			sortMap.put("IMPC_ECH_001_001",1901); //End-Systolic Diameter,1,1900
			sortMap.put("IMPC_ECH_002_001",1902); //End-Diastolic Diameter,2,1900
			sortMap.put("IMPC_ECH_003_001",1903); //Stroke Volume,3,1900
			sortMap.put("IMPC_ECH_004_001",1904); //Ejection Fraction,4,1900
			sortMap.put("IMPC_ECH_005_001",1905); //Fractional Shortening,5,1900
			sortMap.put("IMPC_ECH_016_001",1916); //Aortic diameter (Dao),16,1900
			sortMap.put("IMPC_ECG_014_001",2014); //rMSSD,14,2000
			sortMap.put("IMPC_ECG_001_001",2000); //Number of signals,0,2000
			sortMap.put("IMPC_ECG_002_001",2001); //HR,1,2000
			sortMap.put("IMPC_ECG_003_001",2002); //CV,2,2000
			sortMap.put("IMPC_ECG_004_001",2003); //RR,3,2000
			sortMap.put("IMPC_ECG_005_001",2004); //PQ,4,2000
			sortMap.put("IMPC_ECG_006_001",2005); //PR,5,2000
			sortMap.put("IMPC_ECG_007_001",2006); //QRS,6,2000
			sortMap.put("IMPC_ECG_008_001",2007); //ST,7,2000
			sortMap.put("IMPC_ECG_009_001",2008); //QTc,8,2000
			sortMap.put("IMPC_ECG_010_001",2009); //HRV,9,2000
			sortMap.put("IMPC_ECG_011_001",2010); //QTc Dispersion,10,2000
			sortMap.put("IMPC_ECG_012_001",2011); //Mean SR amplitude,11,2000
			sortMap.put("IMPC_ECG_013_001",2012); //Mean R amplitude,12,2000
			sortMap.put("IMPC_ECG_015_001",2013); //pNN5(6>ms),13,2000
			sortMap.put("IMPC_CBC_001_001",2105); //Sodium,5,2100
			sortMap.put("IMPC_CBC_002_001",2104); //Potassium,4,2100
			sortMap.put("IMPC_CBC_003_001",2106); //Chloride,6,2100
			sortMap.put("IMPC_CBC_004_001",2127); //Urea (Blood Urea Nitrogen - BUN),27,2100
			sortMap.put("IMPC_CBC_005_001",2128); //Creatinine (enzymatic method preferred),28,2100
			sortMap.put("IMPC_CBC_006_001",2109); //Total protein,9,2100
			sortMap.put("IMPC_CBC_007_001",2110); //Albumin,10,2100
			sortMap.put("IMPC_CBC_008_001",2111); //Total bilirubin,11,2100
			sortMap.put("IMPC_CBC_009_001",2101); //Calcium,1,2100
			sortMap.put("IMPC_CBC_010_001",2103); //Phosphorus,3,2100
			sortMap.put("IMPC_CBC_011_001",2107); //Iron,7,2100
			sortMap.put("IMPC_CBC_012_001",2112); //Aspartate aminotransferase,12,2100
			sortMap.put("IMPC_CBC_013_001",2113); //Alanine aminotransferase,13,2100
			sortMap.put("IMPC_CBC_014_001",2114); //Alkaline phosphatase,14,2100
			sortMap.put("IMPC_CBC_015_001",2120); //Total cholesterol,20,2100
			sortMap.put("IMPC_CBC_016_001",2121); //HDL-cholesterol,21,2100
			sortMap.put("IMPC_CBC_017_001",2123); //Triglycerides,23,2100
			sortMap.put("IMPC_CBC_018_001",2116); //Glucose,16,2100
			sortMap.put("IMPC_CBC_020_001",2117); //Fructosamine,17,2100
			sortMap.put("IMPC_CBC_021_001",2125); //Lipase,25,2100
			sortMap.put("IMPC_CBC_022_001",2126); //Lactate dehydrogenase,26,2100
			sortMap.put("IMPC_CBC_023_001",2115); //Alpha-amylase,15,2100
			sortMap.put("IMPC_CBC_024_001",2108); //UIBC (unsaturated iron binding capacity),8,2100
			sortMap.put("IMPC_CBC_025_001",2122); //LDL-cholesterol,22,2100
			sortMap.put("IMPC_CBC_026_001",2124); //Free fatty acids,24,2100
			sortMap.put("IMPC_CBC_027_001",2118); //Glycerol,18,2100
			sortMap.put("IMPC_CBC_028_001",2130); //Creatine kinase,30,2100
			sortMap.put("IMPC_CBC_029_001",2129); //Uric acid,29,2100
			sortMap.put("IMPC_CBC_052_001",2119); //Glycosilated hemoglobin A1c (HbA1c),19,2100
			sortMap.put("IMPC_CBC_054_001",2104); //Magnesium,4,2100
			sortMap.put("IMPC_CAL_017_001",2203); //Respiratory Exchange Ratio,3,2200
			sortMap.put("IMPC_CAL_002_001",2202); //Body weight after experiment,2,2200
			sortMap.put("IMPC_CAL_001_001",2201); //Body weight before experiment,1,2200
			sortMap.put("IMPC_CAL_008_001",2204); //Total food intake,4,2200
			sortMap.put("IMPC_CAL_021_001",2205); //Total water intake,5,2200
			sortMap.put("IMPC_IPG_001_001",2301); //Body Weight,1,2300
			sortMap.put("IMPC_IPG_012_001",2304); //Area under glucose response curve,4,2300
			sortMap.put("IMPC_IPG_011_001",2303); //Initial response to glucose challenge,3,2300
			sortMap.put("IMPC_IPG_010_001",2302); //Fasted blood glucose concentration,2,2300
			sortMap.put("IMPC_XRY_028_001",2401); //Number of digits,1,2400
			sortMap.put("IMPC_XRY_033_001",2404); //Tibia length,4,2400
			sortMap.put("IMPC_XRY_009_001",2402); //Number of ribs left,2,2400
			sortMap.put("IMPC_XRY_008_001",2403); //Number of ribs right,3,2400
			sortMap.put("IMPC_XRY_013_001",2405); //Number of cervical vertebrae,5,2400
			sortMap.put("IMPC_XRY_015_001",2407); //Number of lumbar vertebrae,7,2400
			sortMap.put("IMPC_XRY_017_001",2409); //Number of caudal vertebrae,9,2400
			sortMap.put("IMPC_XRY_014_001",2406); //Number of thoracic vertebrae,6,2400
			sortMap.put("IMPC_XRY_016_001",2408); //Number of pelvic vertebrae,8,2400
			sortMap.put("IMPC_HWT_008_001",2501); //Heart weight,1,2500
			sortMap.put("HRWL_OWT_003_001",2503); //Right kidney,3,2500
			sortMap.put("IMPC_HWT_007_001",2505); //Body weight,5,2500
			sortMap.put("HRWL_OWT_001_001",2504); //Spleen weight,4,2500
			sortMap.put("HRWL_OWT_002_001",2502); //Left kidney,2,2500
			sortMap.put("IMPC_ACS_001_001",2601); //Response amplitude - BN,1,2600
			sortMap.put("IMPC_ACS_002_001",2602); //Response amplitude - PP1,2,2600
			sortMap.put("IMPC_ACS_003_001",2603); //Response amplitude - PP2,3,2600
			sortMap.put("IMPC_ACS_004_001",2604); //Response amplitude - PP3,4,2600
			sortMap.put("IMPC_ACS_005_001",2605); //Response amplitude - PP4,5,2600
			sortMap.put("IMPC_ACS_006_001",2606); //Response amplitude - S,6,2600
			sortMap.put("IMPC_ACS_007_001",2607); //Response amplitude - PP1_S,7,2600
			sortMap.put("IMPC_ACS_008_001",2608); //Response amplitude - PP2_S,8,2600
			sortMap.put("IMPC_ACS_009_001",2609); //Response amplitude - PP3_S,9,2600
			sortMap.put("IMPC_ACS_010_001",2610); //Response amplitude - PP4_S,10,2600
			sortMap.put("IMPC_ACS_033_001",2611); //% Pre-pulse inhibition - PPI1,11,2600
			sortMap.put("IMPC_ACS_034_001",2612); //% Pre-pulse inhibition - PPI2,12,2600
			sortMap.put("IMPC_ACS_035_001",2613); //% Pre-pulse inhibition - PPI3,13,2600
			sortMap.put("IMPC_ACS_036_001",2614); //% Pre-pulse inhibition - PPI4,14,2600
			sortMap.put("IMPC_ACS_037_001",2615); //% Pre-pulse inhibition - Global,15,2600
			sortMap.put("IMPC_HWT_002_001",2701); //Tibia length,1,2700
			sortMap.put("IMPC_BWT_001_001",2800); //Body weight,0,2800
			sortMap.put("IMPC_VIA_008_001",2900); //Total male heterozygous,0,2900
			sortMap.put("IMPC_VIA_009_001",2900); //Total male homozygous,0,2900
			sortMap.put("IMPC_VIA_010_001",2900); //Total male pups,0,2900
			sortMap.put("IMPC_VIA_011_001",2900); //Total female WT,0,2900
			sortMap.put("IMPC_VIA_012_001",2900); //Total female heterozygous,0,2900
			sortMap.put("IMPC_VIA_013_001",2900); //Total female homozygous,0,2900
			sortMap.put("IMPC_VIA_014_001",2900); //Total female pups,0,2900
			sortMap.put("IMPC_VIA_003_001",2900); //Total pups,0,2900
			sortMap.put("IMPC_VIA_004_001",2900); //Total pups WT,0,2900
			sortMap.put("IMPC_VIA_005_001",2900); //Total pups heterozygous,0,2900
			sortMap.put("IMPC_VIA_006_001",2900); //Total pups homozygous,0,2900
			sortMap.put("IMPC_VIA_007_001",2900); //Total male WT,0,2900
			sortMap.put("IMPC_VIA_017_001",2901); //Average litter size,1,2900
			sortMap.put("IMPC_VIA_015_001",2902); //% pups WT,2,2900
			sortMap.put("IMPC_VIA_018_001",2903); //% pups heterozygous,3,2900
			sortMap.put("IMPC_VIA_019_001",2904); //% pups homozygous,4,2900
			sortMap.put("IMPC_VIA_020_001",2905); //% male WT,5,2900
			sortMap.put("IMPC_VIA_021_001",2906); //% male heterozygous,6,2900
			sortMap.put("IMPC_VIA_023_001",2908); //% female WT,8,2900
			sortMap.put("IMPC_VIA_025_001",2910); //% female homozygous,10,2900
			sortMap.put("IMPC_VIA_022_001",2907); //% male homozygous,7,2900
			sortMap.put("IMPC_VIA_024_001",2909); //% female heterozygous,9,2900
			sortMap.put("IMPC_VIA_032_001",2900); //P-value for outcome call,0,2900
			sortMap.put("IMPC_EYE_054_001",3001); //Min left eye lens density,1,3000
			sortMap.put("IMPC_EYE_055_001",3002); //Max left eye lens density,2,3000
			sortMap.put("IMPC_EYE_056_001",3003); //Mean left eye lens density,3,3000
			sortMap.put("IMPC_EYE_057_001",3004); //Min right eye lens density,4,3000
			sortMap.put("IMPC_EYE_058_001",3005); //Max right eye lens density,5,3000
			sortMap.put("IMPC_EYE_059_001",3006); //Mean right eye lens density,6,3000
			sortMap.put("IMPC_EYE_060_001",3008); //Right corneal thickness,8,3000
			sortMap.put("IMPC_EYE_061_001",3010); //Right anterior chamber depth,10,3000
			sortMap.put("IMPC_EYE_062_001",3012); //Right total retinal thickness,12,3000
			sortMap.put("IMPC_EYE_063_001",3014); //Right inner nuclear layer,14,3000
			sortMap.put("IMPC_EYE_064_001",3016); //Right outer nuclear layer,16,3000
			sortMap.put("IMPC_EYE_065_001",3018); //Right posterior chamber depth,18,3000
			sortMap.put("IMPC_EYE_066_001",3007); //Left corneal thickness,7,3000
			sortMap.put("IMPC_EYE_067_001",3009); //Left anterior chamber depth,9,3000
			sortMap.put("IMPC_EYE_068_001",3011); //Left total retinal thickness,11,3000
			sortMap.put("IMPC_EYE_069_001",3013); //Left inner nuclear layer,13,3000
			sortMap.put("IMPC_EYE_070_001",3015); //Left outer nuclear layer,15,3000
			sortMap.put("IMPC_EYE_071_001",3017); //Left posterior chamber depth,17,3000
			sortMap.put("IMPC_EYE_087_001",3020); //Right vitreous humor thickness,20,3000
			sortMap.put("IMPC_EYE_088_001",3019); //Left vitreous humour thickness,19,3000
			sortMap.put("IMPC_IMM_001_001",3101); //Spleen weight,1,3100
			sortMap.put("IMPC_IMM_002_001",3100); //Percentage of live gated events in Panel A,0,3100
			sortMap.put("IMPC_IMM_003_001",3101); //T cells (panel A),1,3100
			sortMap.put("IMPC_IMM_004_001",3102); //NKT cells (panel A),2,3100
			sortMap.put("IMPC_IMM_005_001",3103); //NK cells (panel A),3,3100
			sortMap.put("IMPC_IMM_006_001",3104); //Others,4,3100
			sortMap.put("IMPC_IMM_007_001",3105); //CD4 T cells,5,3100
			sortMap.put("IMPC_IMM_008_001",3106); //CD8 T cells,6,3100
			sortMap.put("IMPC_IMM_009_001",3107); //DN T cells,7,3100
			sortMap.put("IMPC_IMM_011_001",3108); //CD4 NKT cells,8,3100
			sortMap.put("IMPC_IMM_012_001",3109); //CD8 NKT cells,9,3100
			sortMap.put("IMPC_IMM_013_001",3110); //DN NKT cells,10,3100
			sortMap.put("IMPC_IMM_014_001",3111); //CD4 CD25+ T cells,11,3100
			sortMap.put("IMPC_IMM_015_001",3112); //CD4 CD25- T cells,12,3100
			sortMap.put("IMPC_IMM_016_001",3113); //CD8 CD25+ T cells,13,3100
			sortMap.put("IMPC_IMM_017_001",3114); //CD8 CD25- T cells,14,3100
			sortMap.put("IMPC_IMM_018_001",3115); //DN CD25+ T cells,15,3100
			sortMap.put("IMPC_IMM_019_001",3116); //DN CD25- T cells,16,3100
			sortMap.put("IMPC_IMM_020_001",3117); //CD4 CD25+ NKT cells,17,3100
			sortMap.put("IMPC_IMM_021_001",3118); //CD4 CD25- NKT cells,18,3100
			sortMap.put("IMPC_IMM_022_001",3119); //CD8 CD25+ NKT cells,19,3100
			sortMap.put("IMPC_IMM_023_001",3120); //CD8 CD25- NKT cells,20,3100
			sortMap.put("IMPC_IMM_024_001",3121); //DN CD25+ NKT cells,21,3100
			sortMap.put("IMPC_IMM_025_001",3122); //DN CD25- NKT cells,22,3100
			sortMap.put("IMPC_IMM_026_001",3100); //Total number of acquired events in Panel A,0,3100
			sortMap.put("IMPC_IMM_027_001",3100); //Total number of acquired events in Panel B,0,3100
			sortMap.put("IMPC_IMM_028_001",3123); //CD4 CD44+CD62L- T cells,23,3100
			sortMap.put("IMPC_IMM_029_001",3124); //CD4 CD44+CD62L+ T cells,24,3100
			sortMap.put("IMPC_IMM_030_001",3125); //CD4 CD44-CD62L+ T cells,25,3100
			sortMap.put("IMPC_IMM_032_001",3126); //CD8 CD44+CD62L- T cells,26,3100
			sortMap.put("IMPC_IMM_033_001",3127); //CD8 CD44+CD62L+ T cells,27,3100
			sortMap.put("IMPC_IMM_034_001",3128); //CD8 CD44-CD62L+ T cells,28,3100
			sortMap.put("IMPC_IMM_036_001",3129); //DN CD44+CD62L- T cells,29,3100
			sortMap.put("IMPC_IMM_037_001",3130); //DN CD44+CD62L+ T cells,30,3100
			sortMap.put("IMPC_IMM_038_001",3132); //DN CD44-CD62L+ T cells,32,3100
			sortMap.put("IMPC_IMM_040_001",3133); //CD4 CD44+CD62L- NKT cells,33,3100
			sortMap.put("IMPC_IMM_041_001",3134); //CD4 CD44+CD62L+ NKT cells,34,3100
			sortMap.put("IMPC_IMM_042_001",3135); //CD4 CD44-CD62L+ NKT cells,35,3100
			sortMap.put("IMPC_IMM_043_001",3136); //CD8 CD44+CD62L- NKT cells,36,3100
			sortMap.put("IMPC_IMM_044_001",3137); //CD8 CD44+CD62L+ NKT cells,37,3100
			sortMap.put("IMPC_IMM_045_001",3138); //CD8 CD44-CD62L+ NKT cells,38,3100
			sortMap.put("IMPC_IMM_046_001",3139); //DN CD44+CD62L- NKT cells,39,3100
			sortMap.put("IMPC_IMM_047_001",3140); //DN CD44+CD62L+ NKT cells,40,3100
			sortMap.put("IMPC_IMM_048_001",3141); //DN CD44-CD62L+ NKT cells,41,3100
			sortMap.put("IMPC_IMM_049_001",3100); //Percentage of live gated events in Panel B,0,3100
			sortMap.put("IMPC_IMM_050_001",3142); //Neutrophils,42,3100
			sortMap.put("IMPC_IMM_051_001",3143); //Monocytes,43,3100
			sortMap.put("IMPC_IMM_052_001",3144); //Eosinophils,44,3100
			sortMap.put("IMPC_IMM_053_001",3145); //NK Cells (panel B),45,3100
			sortMap.put("IMPC_IMM_054_001",3146); //NK Subsets (Q1),46,3100
			sortMap.put("IMPC_IMM_055_001",3147); //NK Subsets (Q2),47,3100
			sortMap.put("IMPC_IMM_056_001",3148); //NK Subsets (Q3),48,3100
			sortMap.put("IMPC_IMM_057_001",3149); //NK Subsets (Q4),49,3100
			sortMap.put("IMPC_IMM_058_001",3150); //NKT Cells (panel B),50,3100
			sortMap.put("IMPC_IMM_059_001",3151); //NKT Subsets (Q1),51,3100
			sortMap.put("IMPC_IMM_060_001",3152); //NKT Subsets (Q3),52,3100
			sortMap.put("IMPC_IMM_061_001",3153); //T Cells (panel B),53,3100
			sortMap.put("IMPC_IMM_062_001",3154); //T Subset,54,3100
			sortMap.put("IMPC_IMM_063_001",3155); //B Cells,55,3100
			sortMap.put("IMPC_IMM_064_001",3156); //B1B Cells,56,3100
			sortMap.put("IMPC_IMM_065_001",3157); //B2B Cells,57,3100
			sortMap.put("IMPC_IMM_067_001",3158); //Follicular B Cells (CD21/35+),58,3100
			sortMap.put("IMPC_IMM_069_001",3159); //pre-B Cells (CD21/35 low),59,3100
			sortMap.put("IMPC_IMM_071_001",3160); //MZB (CD21/35 high),60,3100
			sortMap.put("IMPC_IMM_072_001",3161); //cDCs,61,3100
			sortMap.put("IMPC_IMM_073_001",3162); //cDCs CD11b Type,62,3100
			sortMap.put("IMPC_IMM_076_001",3163); //RP Macrophage (CD19-  CD11c-),63,3100
			sortMap.put("JAX_ERG_003_001",3207); //Cone b-wave amplitude,7,3200
			sortMap.put("JAX_ERG_004_001",3203); //Rod a-wave amplitude,3,3200
			sortMap.put("JAX_ERG_005_001",3205); //Rod b-wave amplitude,5,3200
			sortMap.put("JAX_ERG_007_001",3201); //Eye size,1,3200
			sortMap.put("JAX_ERG_008_001",3202); //Interpupillary distance,2,3200
			sortMap.put("JAX_ERG_024_001",3208); //Cone b-wave implicit time,8,3200
			sortMap.put("JAX_ERG_025_001",3204); //Rod a-wave implicit time,4,3200
			sortMap.put("JAX_ERG_026_001",3206); //Rod b-wave implicit time,6,3200
			sortMap.put("IMPC_FER_002_001",3300); //Pups born (primary),0,3300
			sortMap.put("IMPC_FER_003_001",3300); //Total matings (primary),0,3300
			sortMap.put("IMPC_FER_004_001",3300); //Total litters (primary),0,3300
			sortMap.put("IMPC_FER_005_001",3300); //Total pups with dissection (primary),0,3300
			sortMap.put("IMPC_FER_006_001",3300); //Pups born (Male screen),0,3300
			sortMap.put("IMPC_FER_007_001",3300); //Total matings (Male screen),0,3300
			sortMap.put("IMPC_FER_008_001",3300); //Total litters (Male screen),0,3300
			sortMap.put("IMPC_FER_009_001",3300); //Total pups/embryos (Male Screen),0,3300
			sortMap.put("IMPC_FER_010_001",3300); //Pups born (Female Screen),0,3300
			sortMap.put("IMPC_FER_011_001",3300); //Total matings (Female Screen),0,3300
			sortMap.put("IMPC_FER_012_001",3300); //Total litters (Female Screen),0,3300
			sortMap.put("IMPC_FER_013_001",3300); //Total pups/embryos (Female Screen),0,3300
			sortMap.put("ICS_HOT_003_001",3400); //Hot plate latency,0,3400
			sortMap.put("ICS_HOT_002_001",3400); //Body weight,0,3400
			sortMap.put("ICS_SHO_002_001",3501); //Shock Intensity Flinch,1,3500
			sortMap.put("ICS_SHO_003_001",3503); //Shock Intensity Vocalization,3,3500
			sortMap.put("ICS_SHO_004_001",3502); //Shock Intensity Jump,2,3500
			sortMap.put("HRWL_IMM_001_001",3601); //Spleen weight,1,3600
			sortMap.put("HRWL_IMM_002_001",3600); //Number of live cells acquired Panel 1,0,3600
			sortMap.put("HRWL_IMM_003_001",3606); //CD4 Effector,6,3600
			sortMap.put("HRWL_IMM_004_001",3605); //CD4 Resting/Naive,5,3600
			sortMap.put("HRWL_IMM_005_001",3604); //CD4 T cells total,4,3600
			sortMap.put("HRWL_IMM_006_001",3610); //CD8 Effector,10,3600
			sortMap.put("HRWL_IMM_007_001",3609); //CD8 Naive,9,3600
			sortMap.put("HRWL_IMM_008_001",3608); //CD8 Resting,8,3600
			sortMap.put("HRWL_IMM_009_001",3607); //CD8 T cells total,7,3600
			sortMap.put("HRWL_IMM_010_001",3622); //gd + B1,22,3600
			sortMap.put("HRWL_IMM_011_001",3616); //iNKT,16,3600
			sortMap.put("HRWL_IMM_012_001",3611); //NK Total,11,3600
			sortMap.put("HRWL_IMM_013_001",3615); //NKT Effector,15,3600
			sortMap.put("HRWL_IMM_014_001",3614); //NKT Resting,14,3600
			sortMap.put("HRWL_IMM_015_001",3613); //NKT Total,13,3600
			sortMap.put("HRWL_IMM_016_001",3612); //T/NKT/B1,12,3600
			sortMap.put("HRWL_IMM_017_001",3617); //Tregs,17,3600
			sortMap.put("HRWL_IMM_018_001",3619); //Tregs Effector,19,3600
			sortMap.put("HRWL_IMM_019_001",3618); //Tregs Resting,18,3600
			sortMap.put("HRWL_IMM_020_001",3600); //Number of live cells acquired Panel 2,0,3600
			sortMap.put("HRWL_IMM_021_001",3620); //B cell total,20,3600
			sortMap.put("HRWL_IMM_022_001",3621); //B1 Total,21,3600
			sortMap.put("HRWL_IMM_023_001",3623); //B2 Total,23,3600
			sortMap.put("HRWL_IMM_024_001",3624); //B2 Immature + MZB,24,3600
			sortMap.put("HRWL_IMM_025_001",3625); //B2 Mature,25,3600
			sortMap.put("HRWL_IMM_026_001",3627); //cDC CD8a type,27,3600
			sortMap.put("HRWL_IMM_027_001",3628); //cDC CD11b type,28,3600
			sortMap.put("HRWL_IMM_028_001",3626); //DC Total,26,3600
			sortMap.put("HRWL_IMM_029_001",3631); //Eosinophils,31,3600
			sortMap.put("HRWL_IMM_030_001",3630); //Neutrophils,30,3600
			sortMap.put("HRWL_IMM_031_001",3633); //Macrophages,33,3600
			sortMap.put("HRWL_IMM_032_001",3632); //Monocytes,32,3600
			sortMap.put("HRWL_IMM_033_001",3629); //pDCs,29,3600
			sortMap.put("HRWL_IMM_034_001",3600); //Total cell count in spleen,0,3600
			sortMap.put("JAX_ERG_046_001",3703); //Interpupillary distance,3,3700
			sortMap.put("JAX_ERG_032_001",3712); //Cone b-wave amplitude-left,12,3700
			sortMap.put("JAX_ERG_031_001",3713); //Cone b-wave amplitude-right,13,3700
			sortMap.put("JAX_ERG_034_001",3714); //Cone b-wave implicit time-left,14,3700
			sortMap.put("JAX_ERG_033_001",3715); //Cone b-wave implicit time-right,15,3700
			sortMap.put("JAX_ERG_045_001",3701); //Eye size-left,1,3700
			sortMap.put("JAX_ERG_044_001",3702); //Eye size-right,2,3700
			sortMap.put("JAX_ERG_036_001",3704); //Rod a-wave amplitude-left,4,3700
			sortMap.put("JAX_ERG_035_001",3705); //Rod a-wave amplitude-right,5,3700
			sortMap.put("JAX_ERG_038_001",3706); //Rod a-wave implicit time-left,6,3700
			sortMap.put("JAX_ERG_037_001",3707); //Rod a-wave implicit time-right,7,3700
			sortMap.put("JAX_ERG_040_001",3708); //Rod b-wave amplitude-left,8,3700
			sortMap.put("JAX_ERG_039_001",3709); //Rod b-wave amplitude-right,9,3700
			sortMap.put("JAX_ERG_042_001",3710); //Rod b-wave implicit time-left,10,3700
			sortMap.put("JAX_ERG_041_001",3711); //Rod b-wave implicit time-right,11,3700
		}

	/**
	 * List of procedures not to be displayed as options for the parallel coordinates.
	 * List came from Terry, see email 2015/08/03.
	 */
	public static final List<String> procedureNoDisplay;
	static {
			procedureNoDisplay = new ArrayList<>();
			procedureNoDisplay.add("IMPC_BWT_001");
			procedureNoDisplay.add("IMPC_FER_001");
			procedureNoDisplay.add("ICS_HOT_001");
	}

}
