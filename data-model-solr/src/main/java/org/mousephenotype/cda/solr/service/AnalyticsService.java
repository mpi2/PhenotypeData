package org.mousephenotype.cda.solr.service;

import org.mousephenotype.cda.db.beans.AggregateCountXYBean;
import org.mousephenotype.cda.db.dao.AnalyticsDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In process of migrating away from the database for these operations.  Until complete, the analyticsDAO will
 * require the komp2Datasource
 */

@Service
public class AnalyticsService {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsService.class);

    AnalyticsDAO analyticsDAO;
    DataSource komp2Datasource;

    @Inject
    public AnalyticsService(
            AnalyticsDAO analyticsDAO,
            @Named("komp2DataSource") DataSource komp2Datasource
    ) {

        Assert.notNull(analyticsDAO, "AnalyticsDAO cannot be null");
        Assert.notNull(komp2Datasource, "komp2Datasource cannot be null");

        this.analyticsDAO = analyticsDAO;
        this.komp2Datasource = komp2Datasource;
    }

    /**
     * Returns the meta data about the data release
     *
     * Currently, the table looks like:

     +----------------+------------------+------+-----+---------+----------------+
     | Field          | Type             | Null | Key | Default | Extra          |
     +----------------+------------------+------+-----+---------+----------------+
     | id             | int(10) unsigned | NO   | PRI | NULL    | auto_increment |
     | property_key   | varchar(255)     | NO   | UNI |         |                |
     | property_value | varchar(2000)    | NO   | MUL |         |                |
     | description    | text             | YES  |     | NULL    |                |
     +----------------+------------------+------+-----+---------+----------------+

     * @return map of key value pairs representing the metadata
     */
    public Map<String, String> getMetaInfo() {

        Map<String, String> metaInfo = new HashMap<>();

        try (Connection connection = komp2Datasource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * from meta_info")) {

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                String pKey = resultSet.getString(2);
                String pValue = resultSet.getString(3);
                metaInfo.put(pKey,  pValue);

            }

        } catch (SQLException e) {
            logger.error("Unable to execute sql. Error:", e);
        }

        return metaInfo;
    }




    /**
     * Retrieves number of lines per procedure for every phenotyping center.
     * @return a list of objects containing the number of lines per procedure
     * per center
     */
    public List<AggregateCountXYBean> getAllProcedureLines() {
        return analyticsDAO.getAllProcedureLines();
    }

    /**
     * Retrieves the aggregate count of significant call per procedure per center
     * @return list of aggregate count
     */
    public List<AggregateCountXYBean> getAllProcedurePhenotypeCalls(){
        return analyticsDAO.getAllProcedurePhenotypeCalls();

    }

    /**
     * Retrieves the statistical methods used for the analysis
     */
    public Map<String, List<String>> getAllStatisticalMethods(){
        return analyticsDAO.getAllStatisticalMethods();

    }

    /**
     * Return the p-value distribution from the statistical analysis using
     * statistical method statisticalMethod on data of type dataType
     * @param dataType type of data
     * @param statisticalMethod statistical method used.
     * @return
     */
    public List<AggregateCountXYBean> getPValueDistribution(String dataType, String statisticalMethod){
        return analyticsDAO.getPValueDistribution(dataType, statisticalMethod);

    }

    /**
     * Get historical data from release to release
     * @param propertyKey which variable to look at
     * @return a list of points for this variable ordered by release date
     */
    public List<AggregateCountXYBean> getHistoricalData(String propertyKey){
        return analyticsDAO.getHistoricalData(propertyKey);

    }

    /**
     * List all releases
     * @return a list of releases
     */
    public List<String> getReleases(String excludeRelease){
        return analyticsDAO.getReleases(excludeRelease);
    }

    /**
     * Return current release number
     * @return a string representing the current release number
     */
    public String getCurrentRelease(){
        return analyticsDAO.getCurrentRelease();
    }

}
