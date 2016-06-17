/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.loads.cdaloader.steps;

import org.mousephenotype.cda.db.pojo.GenomicFeature;
import org.mousephenotype.cda.db.pojo.Xref;
import org.mousephenotype.cda.enumerations.DbIdType;
import org.mousephenotype.cda.loads.cdaloader.exceptions.CdaLoaderException;
import org.mousephenotype.cda.loads.cdaloader.support.FileHeading;
import org.mousephenotype.cda.loads.cdaloader.support.SqlLoaderUtils;
import org.mousephenotype.cda.utilities.RunStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

/**
 * Created by mrelac on 09/06/16.
 */
public class MarkerProcessorXrefs implements ItemProcessor<FieldSet, GenomicFeature> {

    public final Set<String> errMessages              = new HashSet<>();
    private Map<String, GenomicFeature> genomicFeatures;
    private       int        lineNumber               = 0;
    private final Logger     logger                   = LoggerFactory.getLogger(this.getClass());

    // The following ints define the column offset of the given column in the GENE_TYPES file.
    public final static int OFFSET_MGI_ACCESSION_ID = 0;
    public final static int OFFSET_ENTREZ_GENE_ID   = 4;
    public final static int OFFSET_ENSEMBL_GENE_ID  = 9;
    public final static int OFFSET_VEGA_GENE_ID     = 14;
    public final static int OFFSET_CCDS_ID          = 19;

    // The following strings define the column names in the MARKER_LIST file.
    private final static String HEADING_MGI_ACCESSION_ID = "MGI Accession ID";
    private final static String HEADING_ENTREZ_GENE_ID   = "EntrezGene ID";
    private final static String HEADING_ENSEMBL_GENE_ID  = "Ensembl Gene ID";
    private final static String HEADING_VEGA_GENE_ID     = "VEGA Gene ID";
    private final static String HEADING_CCDS_ID          = "CCDS IDs";

    public FileHeading[] fileHeadings = new FileHeading[] {
              new FileHeading(OFFSET_MGI_ACCESSION_ID, HEADING_MGI_ACCESSION_ID)
            , new FileHeading(OFFSET_ENTREZ_GENE_ID, HEADING_ENTREZ_GENE_ID)
            , new FileHeading(OFFSET_ENSEMBL_GENE_ID, HEADING_ENSEMBL_GENE_ID)
            , new FileHeading(OFFSET_VEGA_GENE_ID, HEADING_VEGA_GENE_ID)
            , new FileHeading(OFFSET_CCDS_ID, HEADING_CCDS_ID)
    };

    @Autowired
    @Qualifier("sqlLoaderUtils")
    private SqlLoaderUtils sqlLoaderUtils;


    public MarkerProcessorXrefs(Map<String, GenomicFeature> genomicFeatures) {
        this.genomicFeatures = genomicFeatures;
    }

    public class XrefNode {
        private final int idOffset;
        private final DbIdType dbIdType;
        private int count;

        public XrefNode(int idOffset, DbIdType dbIdType) {
            this.idOffset = idOffset;
            this.dbIdType = dbIdType;
            this.count    = 0;
        }

        public int getCount() {
            return count;
        }
    }

    private final Map<Integer, XrefNode> xrefNodesMap = new HashMap<Integer, XrefNode>() {{
        put(OFFSET_ENTREZ_GENE_ID, new XrefNode(OFFSET_ENTREZ_GENE_ID, DbIdType.EntrezGene));
        put(OFFSET_ENSEMBL_GENE_ID, new XrefNode(OFFSET_ENSEMBL_GENE_ID, DbIdType.Ensembl));
        put(OFFSET_VEGA_GENE_ID, new XrefNode(OFFSET_VEGA_GENE_ID, DbIdType.VEGA));
        put(OFFSET_CCDS_ID, new XrefNode(OFFSET_CCDS_ID, DbIdType.cCDS));
    }};


    @Override
    public GenomicFeature process(FieldSet item) throws Exception {

        lineNumber++;

        // Validate the file using the heading names.
        if (lineNumber == 1) {
            RunStatus status = sqlLoaderUtils.validateHeadings(item.getValues(),fileHeadings);
            if (status.hasErrors()) {
                throw new CdaLoaderException(status.toStringErrorMessages());
            }

            return null;
        }

        GenomicFeature feature  = null;

        String mgiAccessionId = item.getValues()[OFFSET_MGI_ACCESSION_ID];
        String[] ids;

        for (XrefNode xrefNode : xrefNodesMap.values()) {
            if (item.getFieldCount() > xrefNode.idOffset) {
                ids = item.getValues()[xrefNode.idOffset].split(",");
                for (String id : ids) {
                    feature = genomicFeatures.get(mgiAccessionId);
                    if (feature != null) {
                        if (feature.getXrefs() == null) {
                            feature.setXrefs(new LinkedList<>());               // Make sure xrefs list is not null.
                        }                                                       // mgi sometimes fills null ids with the string "null".
                        if ((id != null) && ( ! id.isEmpty()) && ( ! id.toLowerCase().equals("null"))) {
                            Xref xref = new Xref();
                            xref.setAccession(mgiAccessionId);
                            xref.setDatabaseId(DbIdType.MGI.intValue());
                            xref.setXrefAccession(id);
                            xref.setXrefDatabaseId(xrefNode.dbIdType.intValue());
                            feature.addXref(xref);

                            xrefNode.count++;
                        }
                    }
                }
            }
        }

        return feature;
    }

    public Set<String> getErrMessages() {
        return errMessages;
    }

    public Map<String, GenomicFeature> getGenomicFeatures() {
        return genomicFeatures;
    }

    public Map<Integer, XrefNode> getXrefNodesMap() {
        return xrefNodesMap;
    }
}