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
package uk.ac.ebi.phenotype.web.controller;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.mousephenotype.cda.db.dao.GenomicFeatureDAO;
import org.mousephenotype.cda.db.dao.ReferenceDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;


@Controller
public class PaperController {

    private static final int ArrayList = 0;

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getCanonicalName());


    @Resource(name = "globalConfiguration")
    private Map<String, String> config;

    @Autowired
    @Qualifier("admintoolsDataSource")
    private DataSource admintoolsDataSource;

    @Autowired
    @Qualifier("komp2DataSource")
    private DataSource komp2DataSource;

    @Autowired
    private ReferenceDAO referenceDAO;

    @Autowired
    private GenomicFeatureDAO genesDao;


    @RequestMapping(value = "/fetchMeshMapping", method = RequestMethod.GET)
    public @ResponseBody
    String gettopmeshmapping(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws IOException, URISyntaxException, SQLException {

        String html = fetchMeshToTopMeshMapping();

        return html;
    }


    // allele ref data points: number grouped by year at start of month
    @RequestMapping(value = "/fetchPaperStats", method = RequestMethod.GET)
    public @ResponseBody
    String paperStats(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws IOException, URISyntaxException, SQLException {

        return fetchPaperStats(); // json string
    }

    public String fetchMeshToTopMeshMapping() throws SQLException {
        Connection conn = admintoolsDataSource.getConnection();

        String sql = "SELECT top_mesh, number_mapped_mesh, number_mapped_pmid FROM paperMeshTopmesh";
        PreparedStatement p = conn.prepareStatement(sql);
        ResultSet resultSet = p.executeQuery();

        String th = "<thead><th>Top level mesh term</th><th>No. mapped mesh terms</th><th>No. publications</th></thead>";
        String trs = "";
        while (resultSet.next()) {
            String top_mesh = resultSet.getString("top_mesh");
            int number_mapped_mesh = resultSet.getInt("number_mapped_mesh");
            int number_mapped_pmid = resultSet.getInt("number_mapped_pmid");

            trs += "<tr><td>" + top_mesh + "</td><td>" + number_mapped_mesh + "</td><td>" + number_mapped_pmid + "</td></tr>";
        }
        return "<table id='mesh'>" + th + trs + "</table>";

    }

    public String fetchPaperStats() throws SQLException {

        final String MOUSEMiNE = "mousemine";
        final String MANUAL = "manual";
        final String EUROPUBMED = "europubmed";

        Connection conn = admintoolsDataSource.getConnection();

        Set<String> uniqYears = new HashSet<>();
        JSONObject dataJson = new JSONObject();

        Map<String, String> quarters = new HashedMap();
        quarters.put("01", "Q1");
        quarters.put("02", "Q1");
        quarters.put("03", "Q1");

        quarters.put("04", "Q2");
        quarters.put("05", "Q2");
        quarters.put("06", "Q2");

        quarters.put("07", "Q3");
        quarters.put("08", "Q3");
        quarters.put("09", "Q3");

        quarters.put("10", "Q4");
        quarters.put("11", "Q4");
        quarters.put("12", "Q4");


        Map<String, List<Map<String, Integer>>> dm = new HashMap<>();

        try {

            //----------------------------------------------------
            // line data: yearly paper increase over the years
            //----------------------------------------------------

            String qry = "select left(date_of_publication,4) as year, count(*) as count " +
                    "from allele_ref where falsepositive = 'no' group by left(date_of_publication,4) order by year";

            PreparedStatement py = conn.prepareStatement(qry);
            ResultSet resultSety = py.executeQuery();

            int addedCount = 0;
            Map<String, Integer> yearAddedCount = new HashMap<>();

            while (resultSety.next()) {
                String year = resultSety.getString("year");
                int count = resultSety.getInt("count");
                addedCount += count;
                yearAddedCount.put(year, addedCount);
            }

            dataJson.put("yearlyIncrease", yearAddedCount);

            //-----------------------------------------------------------------------------------
            // bar chart: monthly increments (weekly drilldown) by counts (merge of datasources)
            //-----------------------------------------------------------------------------------

            String querySum2 = "select date, count(*) as count " +
                    "from datasource_by_year_weekly_increase " +
                    "where date > '2017-02-09' " +
                    "group by date";


            PreparedStatement p5 = conn.prepareStatement(querySum2);
            ResultSet resultSet5 = p5.executeQuery();

            // String: datasource, String: date, Integer: sum count
            Map<String, Map<String, Integer>> monthIncreaseWeekDrilldown = new HashMap<>();

            while (resultSet5.next()) {
                String date = resultSet5.getString("date");
                Integer count = resultSet5.getInt("count");

                String[] dateParts = StringUtils.split(date, "-");
                String yyyy = dateParts[0];
                String mm = dateParts[1];
                String dd = dateParts[2];
                String key = yyyy + "-" + mm;
                String key2 = mm + "-" + dd;

                if ( !monthIncreaseWeekDrilldown.containsKey(key)){
                    monthIncreaseWeekDrilldown.put(key, new HashMap<String, Integer>());
                }
                if ( !monthIncreaseWeekDrilldown.get(key).containsKey(key2)) {
                    monthIncreaseWeekDrilldown.get(key).put(key2, count);
                }
                else {
                    int weekcnt = monthIncreaseWeekDrilldown.get(key).get(key2);
                    monthIncreaseWeekDrilldown.get(key).put(key2, weekcnt+count);
                }
            }

            dataJson.put("paperMonthlyIncrementWeekDrilldown", monthIncreaseWeekDrilldown);

            //-----------------------------------------------------------
            // bar data: quarterly paper increase by year of publication
            //-----------------------------------------------------------

            String querySum3 = "select left(date_of_publication, 7) as yyyymm, count(*) as count " +
                    "from allele_ref where falsepositive = 'no' group by left(date_of_publication , 7);";

            PreparedStatement pQuarter = conn.prepareStatement(querySum3);
            ResultSet resultSetQ = pQuarter.executeQuery();

            Map<String, Map<String, Integer>> yearQuarterSum = new HashMap<>();


            while (resultSetQ.next()) {
                String yyyymm = resultSetQ.getString("yyyymm");
                String[] ym = yyyymm.split("-");
                String year = ym[0];
                String quarter  = quarters.get(ym[1]);
                Integer count = resultSetQ.getInt("count");

                if ( ! yearQuarterSum.containsKey(year)){
                    yearQuarterSum.put(year, new HashMap<String, Integer>());
                }

                if ( ! yearQuarterSum.get(year).containsKey(quarter)){
                    yearQuarterSum.get(year).put(quarter, count);
                }
                else {
                    int addedCount2 = yearQuarterSum.get(year).get(quarter);
                    yearQuarterSum.get(year).put(quarter, count+addedCount2);
                }
            }

            dataJson.put("yearQuarterSum", yearQuarterSum);

            //----------------------------------------------------------------------------
            // bar data: agency by number of papers and drilldown to year of that number
            //----------------------------------------------------------------------------
            String agentQry = "select left(date_of_publication, 4) as yyyy, pmid, agency from allele_ref where falsepositive = 'no'";

            PreparedStatement pAgent = conn.prepareStatement(agentQry);
            ResultSet resultSetAgent = pAgent.executeQuery();

            Map<String, Map<String, String>> agencyPmidYear = new HashMap<>();

            while (resultSetAgent.next()) {
                String year = resultSetAgent.getString("yyyy");
                String agencies = resultSetAgent.getString("agency");
                String pmid = Integer.toString(resultSetAgent.getInt("pmid"));

                for(String ag : StringUtils.split(agencies, "|||")){
                    Set<String> agencyList = new HashSet<String>(Arrays.asList(StringUtils.split(ag, ",")));
                    for(String agency : agencyList){
                        agency = agency.trim();
                        if (! agencyPmidYear.containsKey(agency)){
                            agencyPmidYear.put(agency, new HashMap<String, String>());
                        }
                        if (! agencyPmidYear.get(agency).containsKey(pmid)){
                            agencyPmidYear.get(agency).put(pmid, year);
                        }
                    }
                }
            }


            // a map with number of papers as key and list of agency as values: for sorting purpose
            Map<String, List<String>> numAgency = new HashedMap();
            Iterator itAg = agencyPmidYear.entrySet().iterator();
            while (itAg.hasNext()) {
                Map.Entry pair = (Map.Entry) itAg.next();
                String ag = pair.getKey().toString();
                Map<String, String> pmidYear = (Map<String, String>) pair.getValue();
                String pmidCount = String.valueOf(pmidYear.keySet().size());

                if (! numAgency.containsKey(pmidCount)) {
                    numAgency.put(pmidCount, new ArrayList<String>());
                }
                numAgency.get(pmidCount).add(ag);
                //itAg.remove();
            }

            dataJson.put("agencyPmidYear", agencyPmidYear);
            dataJson.put("numAgency", numAgency);

            //-----------------------------------------------------------
            // line data: monthly paper increase by year of publication
            //-----------------------------------------------------------

//			String query = "select * from paper_by_year_monthly_increase order by year";
//			PreparedStatement p = conn.prepareStatement(query);
//			ResultSet resultSet = p.executeQuery();
//
//			JSONObject dj = new JSONObject();
//
//			while (resultSet.next()) {
//				String timeTaken = resultSet.getString("date");
//				String year = resultSet.getString("year");
//				int count = resultSet.getInt("count");
//
//				uniqYears.add(year);
//
//				if (!dm.containsKey(timeTaken)) {
//					dm.put(timeTaken, new ArrayList<Map<String, Integer>>());
//				}
//				Map<String, Integer> dobj = new HashMap<>();
//				dobj.put(year, count);
//				dm.get(timeTaken).add(dobj);
//			}
//
//
//			List sorrtedYearList = new ArrayList(uniqYears);
//			Collections.sort(sorrtedYearList);
//
//			Map<String, List<Integer>> timeVallist = new HashMap<>();
//
//			//System.out.println(sorrtedYearList);
//
//			Iterator it = dm.entrySet().iterator();
//			while (it.hasNext()) {
//				Map.Entry pair = (Map.Entry) it.next();
//				String timeTaken2 = pair.getKey().toString();
//
//				List<String> thisYlist = new ArrayList<>();
//
//				List<Map<String, Integer>> yc = (ArrayList<Map<String, Integer>>) pair.getValue();
//				for (Map<String, Integer> yco : yc) {
//					thisYlist.add(yco.keySet().toArray()[0].toString());
//				}
//
//				ArrayList missingYears = (java.util.ArrayList) CollectionUtils.disjunction(sorrtedYearList, new ArrayList(thisYlist));
//
//				for (Object my : missingYears) {
//					Map<String, Integer> ob = new HashMap<>();
//					ob.put(my.toString(), 0);
//					dm.get(timeTaken2).add(ob);
//
//					//System.out.println(timeTaken2 + " ----- missing year: " + my + " ====> " + dm.get(timeTaken2));
//				}
//
//				List<Integer> counts = new ArrayList<>();
//
//				for (Object year : sorrtedYearList) {
//					//System.out.println("doing " + year.toString());
//
//					for (Map<String, Integer> ob : dm.get(timeTaken2)) {
//						String yr = ob.keySet().toArray()[0].toString();
//						if (yr.equals(year)) {
//							counts.add(ob.get(yr));
//							break;
//						}
//					}
//				}
//
//				timeVallist.put(timeTaken2, counts); // counts ordered by year
//				//System.out.println(timeTaken2 + " --> " + counts);
//
//				it.remove(); // avoids a ConcurrentModificationException
//			}
//
//			dataJson.put("years", sorrtedYearList);
//			dataJson.put("monthlyPaperIncreaseByYearOfPublication", timeVallist);

//			//--------------------------------------
//			// bar chart: datasource by year
//			//--------------------------------------
//			String query2 = "select curdate() as date, " +
//					"left(date_of_publication,4) as year, " +
//					"datasource, count(*) as count " +
//					"from allele_ref where falsepositive='no' " +
//					"group by left(date_of_publication,4), datasource";
//
//			Map<String, List<Map<String, Integer>>> dm2 = new HashMap<>();
//
//			PreparedStatement p2 = conn.prepareStatement(query2);
//			ResultSet resultSet2 = p2.executeQuery();
//
//			Map<String, Map<String, Datasource>> dby = new HashMap<>();
//
//			while (resultSet2.next()) {
//				String timeTaken = resultSet2.getString("date");
//				String year = resultSet2.getString("year");
//				String ds = resultSet2.getString("datasource");
//				Integer count = resultSet2.getInt("count");
//
//				if (!dby.containsKey(timeTaken)) {
//					dby.put(timeTaken, new HashMap<String, Datasource>());
//				}
//				if (!dby.get(timeTaken).containsKey(year)) {
//					Datasource dso = new Datasource();
//					dby.get(timeTaken).put(year, dso);
//				}
//				Datasource dso = dby.get(timeTaken).get(year);
//
//				if (ds.equals(MOUSEMiNE)) {
//					dso.setMousemine(count);
//				} else if (ds.equals(MANUAL)) {
//					dso.setManual(count);
//				} else if (ds.equals(EUROPUBMED)) {
//					dso.setEuropubmed(count);
//				}
//			}
//
//			// String: timeTaken, String: datasource, List<Integer>: [data points across the years]
//			Map<String, Map<String, List<Integer>>> dsby = new HashMap<>();
//
//			// put counts by year and datasource in order
//			Iterator it2 = dby.entrySet().iterator();
//			while (it2.hasNext()) {
//
//				Map.Entry pair = (Map.Entry) it2.next();
//				String timeTaken2 = pair.getKey().toString();
//
//				Map<String, List<Integer>> barDs = new HashMap<>();
//				barDs.put(MOUSEMiNE, new ArrayList<Integer>());
//				barDs.put(MANUAL, new ArrayList<Integer>());
//				barDs.put(EUROPUBMED, new ArrayList<Integer>());
//
//				Map<String, Datasource> yds = (Map<String, Datasource>) pair.getValue();
//
//				List<Integer> yrc = new ArrayList<>();
//				for (Object yr : sorrtedYearList) {
//					String year = yr.toString();
//					if (dby.get(timeTaken2).containsKey(year)) {
//						Datasource ds = dby.get(timeTaken2).get(year);
//
//						barDs.get(MOUSEMiNE).add(ds.getMousemine());
//						barDs.get(MANUAL).add(ds.getManual());
//						barDs.get(EUROPUBMED).add(ds.getEuropubmed());
//					} else {
//						barDs.get(MOUSEMiNE).add(0);
//						barDs.get(MANUAL).add(0);
//						barDs.get(EUROPUBMED).add(0);
//					}
//				}
//
//				dsby.put(timeTaken2, barDs);
//			}
//
//			//System.out.println("datasourceByYear" + dsby.toString());
//			dataJson.put("datasourceByYear", dsby);

            //--------------------------------------------
            // bar chart: weekly increments by datasource
            //--------------------------------------------
//			String querySum = "select date, datasource, sum(count) as sum " +
//					"from datasource_by_year_weekly_increase " +
//					"where date > '2017-02-09' " +  // where we started to collect weekly data
//					"group by date, datasource order by date desc";
//
//			PreparedStatement p4 = conn.prepareStatement(querySum);
//			ResultSet resultSet4 = p4.executeQuery();
//
//			// String: datasource, String: date, Integer: sum count
//			Map<String, Map<String, Integer>> dsSum = new HashMap<>();
//			//Map<String, List<Object>> increment = new HashMap<>();
//
//			LinkedHashSet<String> dates = new LinkedHashSet<>();
//			while (resultSet4.next()) {
//				String date = resultSet4.getString("date");
//				String ds = resultSet4.getString("datasource");
//				Integer sum = resultSet4.getInt("sum");
//
//				dates.add(date);
//
//				if ( ! dsSum.containsKey(ds)) {
//					dsSum.put(ds, new HashMap<String, Integer>());
//				}
//				dsSum.get(ds).put(date, sum);
//			}
//
//			Map<String, Map<String, List<Integer>>> ddc = new HashMap<>();
//			List<String> dsnames = new ArrayList<>(Arrays.asList(MOUSEMiNE, EUROPUBMED, MANUAL));
//			for (String date : dates){
//				for (String dsname : dsnames){
//					int sum = 0;
//					if (dsSum.containsKey(dsname)) {
//						if ( dsSum.get(dsname).containsKey(date)) {
//							sum = dsSum.get(dsname).get(date);
//						}
//					}
//					if ( !ddc.containsKey(date)){
//						ddc.put(date, new HashMap<String, List<Integer>>());
//					}
//
//					if ( ! ddc.get(date).containsKey(dsname)){
//						List<Integer> listCount = new ArrayList<>();
//						listCount.add(sum);
//						ddc.get(date).put(dsname, listCount);
//					}
//					else {
//						ddc.get(date).get(dsname).add(sum);
//					}
//				}
//			}
//
//			//System.out.println("ddc: "+ ddc);
//			dataJson.put("datasourceWeeklyIncrement", ddc);



            //-----------------------------------------------
            // pie chart: current datasource distribution
            //-----------------------------------------------
//			String query3 = "select datasource, count(*) as count from allele_ref where falsepositive = 'no'  group by datasource";
//
//			PreparedStatement p3 = conn.prepareStatement(query3);
//			ResultSet resultSet3 = p3.executeQuery();
//
//			Map<String, Integer> pie = new HashMap<>();
//
//			while (resultSet3.next()) {
//				String ds = resultSet3.getString("datasource");
//				Integer count = resultSet3.getInt("count");
//				pie.put(ds, count);
//			}
//
//			dataJson.put("pie", pie);

        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }

        return dataJson.toString();
    }

    // allele reference stuff
    @RequestMapping(value = "/alleleRefLogin", method = RequestMethod.POST)
    public @ResponseBody
    boolean checkPassCode(
            @RequestParam(value = "passcode", required = true) String passcode,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws IOException, URISyntaxException, SQLException {

        return checkPassCode(passcode);
    }

    public boolean checkPassCode(String passcode) throws SQLException {

        Connection conn = admintoolsDataSource.getConnection();

        // prevent sql injection
        String query = "select password = md5(?) as status from users where name='ebi'";
        boolean match = false;

        try (PreparedStatement p = conn.prepareStatement(query)) {
            p.setString(1, passcode);
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
                match = resultSet.getBoolean("status");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }

        return match;
    }

    @RequestMapping(value = "/addpmid", method = RequestMethod.POST)
    public @ResponseBody
    String addPaperByPmid(
            @RequestParam(value = "idStr", required = true) String idStr,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws IOException, URISyntaxException, SQLException {

        List<String> pmidQrys = new ArrayList<>();
        List<String> pmidStrs = Arrays.asList(idStr.split(","));
        int pmidCount = pmidStrs.size();

        for( String pmidStr : pmidStrs ){
            pmidQrys.add("ext_id:" + pmidStr);
        }

        String status = "";
        String failStatus = "";
        String successStatus = "";
        String notFoundStatus = "";
        String ignoreStatus = "";
        int ignoredCount = 0;

        Map<Integer, Pubmed> pubmeds = fetchEuropePubmedData(pmidQrys);

        if (pubmeds.size() == 0) {
            status = "Paper id(s) not found in pubmed database";
        }
        else {
            List<String> failedPmids = new ArrayList<>();
            List<String> failedPmidsMsg = new ArrayList<>();
            List<String> foundPmids = new ArrayList<>();

            Iterator it = pubmeds.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                String pmidStr = pair.getKey().toString();

                foundPmids.add(pmidStr);

                Pubmed pub = (Pubmed) pair.getValue();

                String msg = savePmidData(pub);

                if (msg.contains("duplicate ")){
                    ignoreStatus += pub.getPmid() + "\n";
                    ignoredCount++;
                }
                else if ( ! msg.equals("success") ){
                    //System.out.println("failed: "+ msg);
                    msg = msg.replace(" for key 'pmid'", "");
                    failedPmids.add(pmidStr);
                    failedPmidsMsg.add(msg);
                }
                else {
                    successStatus += pmidStr + " added to database\n";
                }

                it.remove(); // avoids a ConcurrentModificationException
            }

            String submitted = pmidCount + " PMID(s) submitted\n";
            status += submitted;

            if ( failedPmids.size() == 0 && foundPmids.size() == pmidCount && ignoreStatus.isEmpty() ) {
                status += pmidCount + " PMID(s) added successfully";
            }
            else if (failedPmids.size() == 0 && foundPmids.size() == pmidCount && ! ignoreStatus.isEmpty() ){
                status += ignoredCount + " PMID(s) ignored  - already in database:\n" + ignoreStatus;
            }
            else {
                failStatus += fetchNotFoundMsg(pmidStrs, failedPmids);
                if ( !successStatus.equals("")){
                    status += "Success:\n" + successStatus;
                }

                notFoundStatus = fetchNotFoundMsg(pmidStrs, foundPmids);
                if ( !notFoundStatus.equals("") ){
                    status += "Not Found:\n" + notFoundStatus;
                }

                status += "Error:\n" + StringUtils.join(failedPmidsMsg, "\n");

            }
        }

        return status;
    }

    @RequestMapping(value = "/addpmidAllele", method = RequestMethod.POST)
    public @ResponseBody
    String addPaperByPmidAllele(
            @RequestParam(value = "idAlleleStr", required = true) String idAlleleStr,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws IOException, URISyntaxException, SQLException {

        Connection connkomp2 = komp2DataSource.getConnection();

        String status = "";
        String failStatus = "";
        String successStatus = "";
        String notFoundStatus = "";
        String ignoreStatus = "";
        int ignoredCount = 0;

        int pmidSubmittedCount = 0;
        List<String> failedPmids = new ArrayList<>();
        List<String> failedPmidsMsg = new ArrayList<>();
        List<String> foundPmids = new ArrayList<>();
        List<String> allPmids = new ArrayList<>();

        List<String> pmidAlleleStrs = Arrays.asList(idAlleleStr.split("___"));

        System.out.println("param: "+idAlleleStr);
        for( String pmidAlleleStr : pmidAlleleStrs) {

            System.out.println("pmidAlleleStr: " + pmidAlleleStr);
            List<String> alleles = new ArrayList<>();
            List<String> pmidQrys = new ArrayList<>();

            String[] vals = pmidAlleleStr.split("__");
            String pmid = vals[0];
            pmidQrys.add("ext_id:" + pmid);
            allPmids.add(pmid);
            pmidSubmittedCount++;

            String alleleStr = vals[1];

            // look for multiple alleles
            if (alleleStr.contains(",")) {
                alleles.addAll(Arrays.asList(alleleStr.split(",")));
            } else {
                alleles.add(alleleStr);
            }
            // verify allele name exists in komp2 database

            List<String> geneAccs = new ArrayList<>();
            List<String> alleleAccs = new ArrayList<>();
            List<String> goodAlleleSymbols = new ArrayList<>();

            for (String allelename : alleles) {
                allelename = allelename.trim();
                System.out.println("checking allele: " + allelename);
                Map<String, String> ag = isImpcAllele(allelename, connkomp2);
                if (ag.size() > 0) {
                    // add to allele_ref database
                    geneAccs.add(ag.get("geneAcc"));
                    alleleAccs.add(ag.get("alleleAcc"));
                    goodAlleleSymbols.add(allelename);
                }
            }

            String geneAccStr = "";
            String alleleAccStr = "";
            String alleleSymbols = "";
            if (geneAccs.size() != 0) {
                geneAccStr = StringUtils.join(geneAccs, "|||");
            }
            if (alleleAccs.size() != 0) {
                alleleAccStr = StringUtils.join(alleleAccs, "|||");
            }

            if (goodAlleleSymbols.size() != 0) {
                alleleSymbols = StringUtils.join(goodAlleleSymbols, "|||");
            }
            else {
                alleleSymbols = "N/A";
            }

            Map<Integer, Pubmed> pubmeds = fetchEuropePubmedData(pmidQrys);

            if (pubmeds.size() == 0) {
                status = pmid + " not found in pubmed database";
            }
            else {

                Iterator it = pubmeds.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    String pmidStr = pair.getKey().toString();

                    foundPmids.add(pmidStr);

                    Pubmed pub = (Pubmed) pair.getValue();

					System.out.println("gene acc: " + geneAccStr);
					System.out.println("allele acc: " + alleleAccStr);
					System.out.println("allele symbol: " + alleleSymbols);

                    pub.setGeneAccs(geneAccStr);
                    pub.setAlleleAccs(alleleAccStr);
                    pub.setAlleleSymbols(alleleSymbols);
                    pub.setReviewed("yes");

                    //System.out.println("found paper: "+pmidStr);

                    String msg = savePmidData(pub);
                    //System.out.println("insert status: "+msg);

                    if (msg.contains("duplicate ")) {
                        ignoreStatus += pub.getPmid() + "\n";
                        ignoredCount++;
                    } else if (!msg.equals("success")) {
                        //System.out.println("failed: "+ msg);
                        msg = msg.replace(" for key 'pmid'", "");
                        failedPmids.add(pmidStr);
                        failedPmidsMsg.add(msg);
                    } else {
                        successStatus += pmidStr + " added to database\n";
                    }

                    it.remove(); // avoids a ConcurrentModificationException
                }
            }

        }

        String submitted = pmidSubmittedCount + " PMID(s) submitted\n";
        status += submitted;

        if (failedPmids.size() == 0 && foundPmids.size() == pmidSubmittedCount && ignoreStatus.isEmpty()) {
            status += pmidSubmittedCount + " PMID(s) added successfully";
        } else if (failedPmids.size() == 0 && foundPmids.size() == pmidSubmittedCount && !ignoreStatus.isEmpty()) {
            status += ignoredCount + " PMID(s) ignored  - already in database:\n" + ignoreStatus;
        } else {
            failStatus += fetchNotFoundMsg(allPmids, failedPmids);
            if (!successStatus.equals("")) {
                status += "Success:\n" + successStatus;
            }

            notFoundStatus = fetchNotFoundMsg(allPmids, foundPmids);
            if (!notFoundStatus.equals("")) {
                status += "Not Found:\n" + notFoundStatus;
            }

            status += "Error:\n" + StringUtils.join(failedPmidsMsg, "\n");

        }

        connkomp2.close();
        return status;

    }

    public Map<String, String> isImpcAllele(String allelename, Connection conn){

        Map<String, String> alleleGene = new HashMap<>();

        String query = "SELECT acc, gf_acc FROM allele WHERE symbol=?";
        try (PreparedStatement p = conn.prepareStatement(query)) {
            p.setString(1,allelename);

            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
                alleleGene.put("alleleAcc", resultSet.getString("acc"));
                alleleGene.put("geneAcc", resultSet.getString("gf_acc"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return alleleGene;
    }

    public String fetchNotFoundMsg(List<String> pmidStrs, List<String> failedPmids ){
        String msg = "";
        for (String pmid : pmidStrs){
            if ( !failedPmids.contains(pmid)){
                msg += pmid + " not found in pubmed\n";
            }
        }
        return msg;
    }

    public String savePmidData(Pubmed pub) throws SQLException{

        Connection conn = admintoolsDataSource.getConnection();
        Boolean autocommit = conn.getAutoCommit();
        conn.setAutoCommit(false);


//		// make sure do not insert duplicate pmid
//		PreparedStatement insertStatement = conn.prepareStatement("REPLACE INTO allele_ref "
//				+ "(gacc, acc, symbol, name, pmid, date_of_publication, reviewed, grant_id, agency, acronym, title, journal, paper_url, datasource, timestamp, falsepositive, mesh, meshtree, author) "
//				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");


        // make sure do not insert duplicate pmid
        PreparedStatement insertStatement = conn.prepareStatement("INSERT IGNORE INTO allele_ref "
                + "(gacc, acc, symbol, name, pmid, date_of_publication, reviewed, grant_id, agency, acronym, title, journal, paper_url, datasource, timestamp, falsepositive, mesh, meshtree, author) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?)");


        // (1.gacc, 2.acc, 3.symbol, 4.name, 5.pmid, 6.date_of_publication,
        // 7.reviewed, 8.grant_id, 9.agency, 10.acronym, 11.title,
        // 12.journal, 13.paper_url, 14.datasource, 15.timestamp, 16.falsepositive, 17.mesh, 18.meshtree, 19 author) VALUES (?,?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)");

        final String delimiter = "|||";
        int pmid = pub.getPmid();

        insertStatement.setString(1, pub.getGeneAccs() == null || pub.getGeneAccs().isEmpty() ? "" : pub.getGeneAccs());
        insertStatement.setString(2, pub.getAlleleAccs() == null || pub.getAlleleAccs().isEmpty() ? "" : pub.getAlleleAccs());
        insertStatement.setString(3, pub.getAlleleSymbols() == null || pub.getAlleleSymbols().isEmpty()  ? "" : pub.getAlleleSymbols());
        insertStatement.setString(4, "");
        insertStatement.setInt(5, pmid);
        insertStatement.setString(6, pub.getDateOfPublication());
        insertStatement.setString(7, pub.getReviewed() == null || pub.getReviewed().isEmpty() ? "no" : "yes"); // reviewed, default is no

        // grant info: we can have multiple grants for a paper

        List<String> grantIds = new ArrayList<>();
        List<String> grantAgencies = new ArrayList<>();
        List<String> grantAcronyms = new ArrayList<>();

        List<Grant> grants = pub.getGrants();
        for (int i = 0; i < grants.size(); i++) {
            Grant g = grants.get(i);
            if (!g.getId().equals("")) {
                grantIds.add(g.getId());
            }
            if (!g.getAgency().equals("")) {
                grantAgencies.add(g.getAgency());
            }
            if (!g.getAcronym().equals("")) {
                grantAcronyms.add(g.getAcronym());
            }
        }

        insertStatement.setString(8, grantIds.size() > 0 ? StringUtils.join(grantIds, delimiter) : "");
        insertStatement.setString(9, grantAgencies.size() > 0 ? StringUtils.join(grantAgencies, delimiter) : "");
        insertStatement.setString(10, grantAcronyms.size() > 0 ? StringUtils.join(grantAcronyms, delimiter) : "");

        insertStatement.setString(11, pub.getTitle());
        insertStatement.setString(12, pub.getJournal());

        // we can have multiple links to a paper
        List<String> paper_urls = new ArrayList<>();
        List<Paperurl> paperUrls = pub.getPaperurls();
        for (int j = 0; j < paperUrls.size(); j++) {
            Paperurl p = paperUrls.get(j);
            paper_urls.add(p.getUrl());
        }
        insertStatement.setString(13, paper_urls.size() > 0 ? StringUtils.join(paper_urls, delimiter) : "");

        insertStatement.setString(14, "manual");
        insertStatement.setTimestamp(15, new Timestamp(System.currentTimeMillis()));
        insertStatement.setString(16, "no");

        // fetch mesh terms: heading pulus mesh heading+mesh qualifier
        List<String> mterms = new ArrayList<>();
        for ( int k=0; k<pub.meshTerms.size(); k++ ) {
            MeshTerm mt = pub.meshTerms.get(k);
            mterms.add(mt.meshHeading);
            for (String mq : mt.meshQualifiers) {
                mterms.add(mt.meshHeading + " " + mq);
            }
        }

        insertStatement.setString(17, mterms.size() > 0 ? StringUtils.join(mterms, delimiter) : "");
        insertStatement.setString(18, mterms.size() > 0 ? pub.getMeshJsonStr() : "");
        insertStatement.setString(19, pub.getAuthor());
        // add new pmid do not need consotium_paper column (default "no")

        try {

            int count = insertStatement.executeUpdate();
            if (count==0){
                return "duplicate " + pmid;
            }

            updateTableDatasourceByYearWeeklyIncrease(pub.getDateOfPublication(), mterms, conn);

            conn.commit();
            conn.setAutoCommit(autocommit);

            return "success";
        }
        catch(SQLException se){
            conn.rollback();
            conn.setAutoCommit(autocommit);

            return se.getMessage();
        }
        finally {
            conn.close();
        }
    }

    public void updateTableDatasourceByYearWeeklyIncrease(String dateOfPublication, List<String> mterms, Connection conn) throws SQLException {

        String[] dateparts = StringUtils.split(dateOfPublication,"-");
        String year = dateparts[0];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String yyyymmdd =sdf.format(Calendar.getInstance().getTime());

        try {
            PreparedStatement insertStatement = conn.prepareStatement("INSERT INTO datasource_by_year_weekly_increase (date, year, datasource, count) "
                    + " VALUES (?,?,?,?)"
                    + " ON DUPLICATE KEY UPDATE "
                    + " count = count+1");
            insertStatement.setString(1, yyyymmdd);
            insertStatement.setString(2, year);
            insertStatement.setString(3, "manual");
            insertStatement.setInt(4, 1);

            insertStatement.executeUpdate();

            // update mesh stats for papers
            if (mterms.size() > 0) {
                System.out.println(mterms);

                List<String> mts = new ArrayList<>();
                for(String m : mterms){
                    mts.add("'" + m + "'");
                }

                List<String> tops = new ArrayList<>();

                String sql = "SELECT mm.top_mesh, count(mm.top_mesh) as count, pm.number_mapped_mesh, pm.number_mapped_pmid "
                        + "FROM mesh_mapping mm "
                        + "JOIN paperMeshTopmesh pm "
                        + "ON mm.top_mesh=pm.top_mesh "
                        + "WHERE mesh_heading IN (" + StringUtils.join(mts, ",") + ") "
                        + "GROUP BY mm.top_mesh";

                System.out.println("sql: " + sql);
                PreparedStatement p = conn.prepareStatement(sql);
                String whenMesh = "";
                String whenPmid = "";

                ResultSet resultSet = p.executeQuery();
                while (resultSet.next()) {
                    String top_mesh = resultSet.getString("top_mesh");
                    tops.add("'" + top_mesh + "'");
                    int count = resultSet.getInt("count");
                    int number_mapped_mesh = resultSet.getInt("number_mapped_mesh");
                    int number_mapped_pmid = resultSet.getInt("number_mapped_pmid");

                    whenMesh += " WHEN top_mesh = '" + top_mesh + "' then " + (int) (number_mapped_mesh + count);
                    whenPmid += " WHEN top_mesh = '" + top_mesh + "' then " + (int) (number_mapped_pmid + 1);  // one paper
                }

                String updateSql = "UPDATE paperMeshTopmesh SET number_mapped_mesh = (case" + whenMesh + " end), "
                    + "number_mapped_pmid = (case" + whenPmid +  " end) "
                    + "WHERE top_mesh in (" + StringUtils.join(tops, ",") + ")";

                System.out.println("update: "+ updateSql);
                PreparedStatement upt = conn.prepareStatement(updateSql);
                upt.executeUpdate();

            }

        } catch (Exception e) {
            logger.error("Failed to include manually added paper for counting");
        }
    }

    public String fetchTopLevelMesh(String mesh) throws SQLException {
        String query = "SELECT top_mesh from mesh_mapping where mesh_heading = ?";
        String topmesh = null;

        try {
            Connection connection = admintoolsDataSource.getConnection();
            PreparedStatement p = connection.prepareStatement(query);

            p.setString(1, mesh);

            ResultSet r = p.executeQuery();
            while (r.next()) {
                topmesh = r.getString("top_mesh");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return topmesh;
    }

    public Map<Integer, Pubmed> fetchEuropePubmedData(List<String> pmidQrys) throws SQLException {

        Map<Integer, Pubmed> pubmeds = new HashMap<>();

        // attach pubmed info to pmid
        for( String q : pmidQrys ){
            //System.out.println("Working on filter: "+ q);
            String dbfetchUrl = "http://www.ebi.ac.uk/europepmc/webservices/rest/search/query=" + q + "%20and%20src:MED&format=json&resulttype=core";
            //System.out.println(dbfetchUrl);

            JSONObject json = fetchHttpUrlJson(dbfetchUrl);

            JSONArray results = json.getJSONObject("resultList").getJSONArray("result");

            for (int j = 0; j < results.size(); j++) {

                JSONObject r = results.getJSONObject(j);

                int pmid = r.getInt("pmid");

                if (!pubmeds.containsKey(q)) {
                    pubmeds.put(pmid, new Pubmed());
                }
                Pubmed pub = pubmeds.get(pmid);

                pub.setPmid(pmid);

                if (r.containsKey("title")) {
                    // sometime paper title is within "[]", remove this: trouble from Europubmed
                    String title = r.getString("title");
                    title = title.replaceAll("\\[", "").replaceAll("\\]","");
                    pub.setTitle(title);
                } else {
                    pub.setTitle("");
                }

                if (r.containsKey("electronicPublicationDate")){
                    pub.setDateOfPublication(r.getString("electronicPublicationDate"));
                }
                else if (r.containsKey("firstPublicationDate")){
                    pub.setDateOfPublication(r.getString("firstPublicationDate"));
                }
                else if (r.containsKey("journalInfo") && r.getJSONObject("journalInfo").containsKey("printPublicationDate")) {
                    pub.setDateOfPublication(r.getJSONObject("journalInfo").getString("printPublicationDate"));
                }
                else if (r.containsKey("pubYear")){
                    pub.setDateOfPublication(r.getString("pubYear") + "-00-00");
                }
                else {
                    pub.setDateOfPublication("");
                }

                if (r.containsKey("journalInfo") && r.getJSONObject("journalInfo").containsKey("journal")) {
                    pub.setJournal(r.getJSONObject("journalInfo").getJSONObject("journal").getString("title"));
                } else {
                    pub.setJournal("");
                }


                List<Grant> grantList = new ArrayList<>();

                if (r.containsKey("grantsList")) {
                    JSONArray grants = r.getJSONObject("grantsList").getJSONArray("grant");
                    for (int k = 0; k < grants.size(); k++) {
                        JSONObject thisG = (JSONObject) grants.get(k);
                        Grant g = new Grant();
                        //System.out.println(thisG.toString());

                        if (thisG.containsKey("grantId")) {
                            g.setId(thisG.getString("grantId"));
                        } else {
                            g.setId("");
                        }

                        if (thisG.containsKey("agency")) {
                            g.setAgency(thisG.getString("agency"));
                        } else {
                            g.setAgency("");
                        }

                        if (thisG.containsKey("acronym")) {
                            g.setAcronym(thisG.getString("acronym"));
                        } else {
                            g.setAcronym("");
                        }

                        grantList.add(g);

                        //System.out.println(g.toString());
                    }
                }
                pub.setGrants(grantList);

                List<Paperurl> paperurls = new ArrayList<>();

                if (r.containsKey("fullTextUrlList")) {
                    JSONArray textUrl = r.getJSONObject("fullTextUrlList").getJSONArray("fullTextUrl");
                    for (int l = 0; l < textUrl.size(); l++) {
                        Paperurl p = new Paperurl();
                        JSONObject thisT = (JSONObject) textUrl.get(l);
                        if (thisT.containsKey("url")) {
                            p.setUrl(thisT.getString("url"));
                            paperurls.add(p);
                            //System.out.println("URL: "+ p.url);
                        }
                    }
                }

                pub.setPaperurls(paperurls);

                // mesh terms
                List<MeshTerm> meshTerms = new ArrayList<>();
                JSONArray meshHeadings_modified = new JSONArray();

                if ( r.containsKey("meshHeadingList") ){
                    JSONArray meshHeadings = r.getJSONObject("meshHeadingList").getJSONArray("meshHeading");
                    for ( int mh=0; mh<meshHeadings.size(); mh++ ){
                        JSONObject thisMeshHeading = (JSONObject) meshHeadings.get(mh);
                        JSONObject thisMeshHeading_modified = new JSONObject();

                        MeshTerm mt  = new MeshTerm();
                        //System.out.println(thisMeshHeading.toString());

                        // mesh heading
                        mt.setMeshHeading("");
                        if ( thisMeshHeading.containsKey("descriptorName") ){
                            mt.setMeshHeading(thisMeshHeading.getString("descriptorName"));

                            String topMeshTerm = fetchTopLevelMesh(mt.meshHeading);
                            thisMeshHeading_modified.put("text", mt.meshHeading + "(<span class='topmesh'>" + topMeshTerm + "</span>)");
                        }

                        // mesh subheading
                        mt.setMeshQualifiers(new ArrayList<>());
                        if ( thisMeshHeading.containsKey("meshQualifierList") ) {
                            JSONArray meshQualifiers= thisMeshHeading.getJSONObject("meshQualifierList").getJSONArray("meshQualifier");
                            JSONArray meshQualifiers_modified = new JSONArray();

                            for (int mq = 0; mq < meshQualifiers.size(); mq++) {
                                JSONObject thisMeshQualifier = (JSONObject) meshQualifiers.get(mq);
                                if ( thisMeshQualifier.containsKey("qualifierName") ){
                                    String qf = thisMeshQualifier.getString("qualifierName");

                                    JSONObject thisMeshQualifier_modified = new JSONObject();
                                    thisMeshQualifier_modified.put("text", qf);
                                    meshQualifiers_modified.add(thisMeshQualifier_modified);

                                    if ( ! mt.getMeshQualifiers().contains(qf)) {
                                        mt.getMeshQualifiers().add(qf);
                                    }
                                }
                            }
                            thisMeshHeading_modified.put("children", meshQualifiers_modified);
                        }

                        meshHeadings_modified.add(thisMeshHeading_modified);
                        meshTerms.add(mt);
                    }
                }
                pub.setMeshTerms(meshTerms);
                pub.setMeshJsonStr(meshHeadings_modified.toString());
                pub.setAuthor(r.getString("authorString"));
            }
        }

        System.out.println("PARSING EUROPE PUBMED:");
        logger.info("Found " + pubmeds.size() + " pmids");
        System.out.println("");

        return pubmeds;
    }

    public JSONObject fetchHttpUrlJson(String dbfetchUrl) {
        // Data obtained from service, to be returned
        String jsonStr = null;
        // Get data using HTTP GET
        try {
            URL url = new URL(dbfetchUrl);
            BufferedReader inBuf = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer strBuf = new StringBuffer();
            while(inBuf.ready()) {
                strBuf.append(inBuf.readLine() + System.getProperty("line.separator"));
            }
            jsonStr = strBuf.toString();
        }
        catch(IOException ex) {
            System.out.println(ex.getMessage());
        }
        // Return the response data as JSON object
        JSONObject json = JSONObject.fromObject(jsonStr);
        return json;
    }

    public class Datasource {
        int mousemine;
        int manual;
        int europubmed;

        public int getMousemine() {
            return mousemine;
        }

        public void setMousemine(int mousemine) {
            this.mousemine = mousemine;
        }

        public int getManual() {
            return manual;
        }

        public void setManual(int manual) {
            this.manual = manual;
        }

        public int getEuropubmed() {
            return europubmed;
        }

        public void setEuropubmed(int europubmed) {
            this.europubmed = europubmed;
        }

        @Override
        public String toString() {
            return "Datasource{" +
                    "mousemine=" + mousemine +
                    ", manual=" + manual +
                    ", europubmed=" + europubmed +
                    '}';
        }
    }

    public class Pubmed {
        Integer pmid;
        String title;
        String journal;
        String dateOfPublication;
        List<Grant> grants;
        List<Paperurl> paperurls;
        List<MeshTerm> meshTerms;
        String meshJsonStr;
        String geneAccs;
        String alleleAccs;
        String alleleSymbols;
        String reviewed;
        String author;

        public String getGeneAccs() {
            return geneAccs;
        }

        public void setGeneAccs(String geneAccs) {
            this.geneAccs = geneAccs;
        }

        public String getAlleleAccs() {
            return alleleAccs;
        }

        public void setAlleleAccs(String alleleAccs) {
            this.alleleAccs = alleleAccs;
        }

        public String getAlleleSymbols() {
            return alleleSymbols;
        }

        public void setAlleleSymbols(String alleleSymbols) {
            this.alleleSymbols = alleleSymbols;
        }

        public Integer getPmid() {
            return pmid;
        }

        public void setPmid(Integer pmid) {
            this.pmid = pmid;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getJournal() {
            return journal;
        }

        public void setJournal(String journal) {
            this.journal = journal;
        }

        public String getDateOfPublication() {
            return dateOfPublication;
        }

        public void setDateOfPublication(String dateOfPublication) {
            this.dateOfPublication = dateOfPublication;
        }

        public List<Grant> getGrants() {
            return grants;
        }

        public void setGrants(List<Grant> grants) {
            this.grants = grants;
        }

        public List<Paperurl> getPaperurls() {
            return paperurls;
        }

        public void setPaperurls(List<Paperurl> paperurls) {
            this.paperurls = paperurls;
        }

        public List<MeshTerm> getMeshTerms() {
            return meshTerms;
        }

        public void setMeshTerms(List<MeshTerm> meshTerms) {
            this.meshTerms = meshTerms;
        }

        public String getReviewed() {
            return reviewed;
        }

        public void setReviewed(String reviewed) {
            this.reviewed = reviewed;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getMeshJsonStr() {
            return meshJsonStr;
        }

        public void setMeshJsonStr(String meshJsonStr) {
            this.meshJsonStr = meshJsonStr;
        }

        @Override
        public String toString() {
            return "Pubmed{" +
                    "pmid=" + pmid +
                    ", title='" + title + '\'' +
                    ", journal='" + journal + '\'' +
                    ", dateOfPublication='" + dateOfPublication + '\'' +
                    ", grants=" + grants +
                    ", paperurls=" + paperurls +
                    ", meshTerms=" + meshTerms +
                    ", meshJsonStr='" + meshJsonStr + '\'' +
                    ", geneAccs='" + geneAccs + '\'' +
                    ", alleleAccs='" + alleleAccs + '\'' +
                    ", alleleSymbols='" + alleleSymbols + '\'' +
                    ", reviewed='" + reviewed + '\'' +
                    ", author='" + author + '\'' +
                    '}';
        }
    }
    public class Grant {
        public String id;
        public String acronym;
        public String agency;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAcronym() {
            return acronym;
        }

        public void setAcronym(String acronym) {
            this.acronym = acronym;
        }

        public String getAgency() {
            return agency;
        }

        public void setAgency(String agency) {
            this.agency = agency;
        }
    }
    public class Paperurl {
        public String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
    public class MeshTerm {
        public String meshHeading;
        public List<String> meshQualifiers; // same as mes subheading

        public String getMeshHeading() {
            return meshHeading;
        }

        public void setMeshHeading(String meshHeading) {
            this.meshHeading = meshHeading;
        }

        public List<String> getMeshQualifiers() {
            return meshQualifiers;
        }

        public void setMeshQualifiers(List<String> meshQualifiers) {
            this.meshQualifiers = meshQualifiers;
        }
    }

    private HttpHeaders createResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return responseHeaders;
    }
}
