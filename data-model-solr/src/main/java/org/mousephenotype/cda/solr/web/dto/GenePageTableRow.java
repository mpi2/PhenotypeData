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


import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.solr.service.ImageService;


/**
 *
 * @author mrelac
 * This class encapsulates the code and data necessary to represent a row in the
 * Gene page's 'phenotypes' HTML table.
 */
public class GenePageTableRow extends DataTableRow {

    public GenePageTableRow() {
        super();
    }

    public GenePageTableRow(PhenotypeCallSummaryDTO pcs, String baseUrl, Map<String, String> config) 
    throws UnsupportedEncodingException, SolrServerException {
        super(pcs, baseUrl, config);
    }

    /**
     * Sort by:
     * <ul>
     * <li>sex</li>
     * <li>p-value</li>
     * <li>phenotype</li>
     * <li>procedure</li>
     * <li>parameter</li>
     * <li>phenotyping center</li>
     * <li>source</li>
     * </ul>
     *
     * @param o operand to compare against
     * @return
     */
    @Override
    public int compareTo(DataTableRow o) {
    	
        if (o.phenotypeTerm == null || this.phenotypeTerm == null) {
            return -1;
        }

        // Gene Page sorting
        int pvalueOp = this.pValue.compareTo(o.pValue);
        if (pvalueOp == 0) {
            int phenotypeOp = this.phenotypeTerm.getName().compareTo(o.phenotypeTerm.getName());
            if (phenotypeOp == 0) {
             	int procedureOp = this.procedure.getName().compareTo(o.procedure.getName());
                if (procedureOp == 0) {
                    int parameterOp = this.parameter.getName().compareTo(o.parameter.getName());
                    if (parameterOp == 0) {
                        int phenotypingCenterOp = this.phenotypingCenter.compareTo(o.phenotypingCenter);
                        if (phenotypingCenterOp == 0) {
                            return this.dataSourceName.compareTo(o.dataSourceName);
                        } else {
                            return phenotypingCenterOp;
                        }
                    } else {
                        return parameterOp;
                    }
                } else {
                    return procedureOp;
                }
            } else {
                return phenotypeOp;
            }
        } else {
            return pvalueOp;
        }
    }
    
    /**
     * @since 2016/05/05
     * @return Tabbed header to be used in table export. 
     * ! Keep in synch with order in getTabbedToString !
     */
    public static String getTabbedHeader(){
    	return "Phenotype\tAllele\tZygosity\tSex\tLife Stage\tProcedure\tParameter\tPhenotyping Center\tSource\tP Value\tGraph";
    }
    
    /**
     * @since 2016/05/05
     * @return Tabbed row for data export
     */
    public String toTabbedString(){
    	return getPhenotypeTerm().getName() + "\t"
                + getAllele().getSymbol() + "\t"
                + getZygosity() + "\t"
                + getSexes().get(0) + "\t"
                + getLifeStageName() + "\t"
                + getProcedure().getName() + "\t"
                + getParameter().getName() + "\t"
                + getPhenotypingCenter() + "\t"
                + getDataSourceName() + "\t"
                + getPrValueAsString() + "\t"
                + getEvidenceLink().getUrl();
    }
    
}
