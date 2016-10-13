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

package org.mousephenotype.cda.db.pojo;

import java.util.List;
import java.util.Objects;


/**
 *
 * @author mrelac
 *
 * This class encapsulates the code and data necessary to represent a single
 * pubmed paper. Each such paper may have multiple:
 * <ul><li>allele symbols</li>
 * <li>allele ids</li>
 * <li>IMPC gene links</li>
 * <li>MGI allele names</li>
 * <li>grant ids</li>
 * <li>grant agencies</li>
 * <li>references to other papers</li></ul>
 */
public class ReferenceDTO {
    private List<String> alleleSymbols;
    private List<String> alleleAccessionIds;
    private List<String> geneAccessionIds;
    private List<String> impcGeneLinks;
    private List<String> mgiAlleleNames;
    private String title;
    private String journal;
    private Integer pmid;
    private String dateOfPublication;
    private String timestamp;
    private List<String> grantIds;
    private List<String> grantAgencies;
    private List<String> paperUrls;

    public List<String> getAlleleSymbols() {
        return alleleSymbols;
    }

    public void setAlleleSymbols(List<String> alleleSymbols) {
        this.alleleSymbols = alleleSymbols;
    }

    public List<String> getAlleleAccessionIds() {
        return alleleAccessionIds;
    }

    public void setAlleleAccessionIds(List<String> alleleAccessionIds) {
        this.alleleAccessionIds = alleleAccessionIds;
    }

    public List<String> getGeneAccessionIds() {
        return geneAccessionIds;
    }

    public void setGeneAccessionIds(List<String> geneAccessionIds) {
        this.geneAccessionIds = geneAccessionIds;
    }

    public List<String> getImpcGeneLinks() {
        return impcGeneLinks;
    }

    public void setImpcGeneLinks(List<String> impcGeneLinks) {
        this.impcGeneLinks = impcGeneLinks;
    }

    public List<String> getMgiAlleleNames() {
        return mgiAlleleNames;
    }

    public void setMgiAlleleNames(List<String> mgiAlleleNames) {
        this.mgiAlleleNames = mgiAlleleNames;
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

    public Integer getPmid() { return pmid; }

    public void setPmid(Integer pmid) {
        this.pmid = pmid;
    }

    public String getDateOfPublication() {
        return dateOfPublication;
    }

    public void setDateOfPublication(String dateOfPublication) {
        this.dateOfPublication = dateOfPublication;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getGrantIds() {
        return grantIds;
    }

    public void setGrantIds(List<String> grantIds) {
        this.grantIds = grantIds;
    }

    public List<String> getGrantAgencies() {
        return grantAgencies;
    }

    public void setGrantAgencies(List<String> grantAgencies) {
        this.grantAgencies = grantAgencies;
    }

    public List<String> getPaperUrls() {
        return paperUrls;
    }

    public void setPaperUrls(List<String> paperUrls) {
        this.paperUrls = paperUrls;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReferenceDTO that = (ReferenceDTO) o;

        if (alleleSymbols != null ? !alleleSymbols.equals(that.alleleSymbols) : that.alleleSymbols != null)
            return false;
        if (alleleAccessionIds != null ? !alleleAccessionIds.equals(that.alleleAccessionIds) : that.alleleAccessionIds != null)
            return false;
        if (geneAccessionIds != null ? !geneAccessionIds.equals(that.geneAccessionIds) : that.geneAccessionIds != null)
            return false;
        if (impcGeneLinks != null ? !impcGeneLinks.equals(that.impcGeneLinks) : that.impcGeneLinks != null)
            return false;
        if (mgiAlleleNames != null ? !mgiAlleleNames.equals(that.mgiAlleleNames) : that.mgiAlleleNames != null)
            return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (journal != null ? !journal.equals(that.journal) : that.journal != null) return false;
        if (pmid != null ? !pmid.equals(that.pmid) : that.pmid != null) return false;
        if (dateOfPublication != null ? !dateOfPublication.equals(that.dateOfPublication) : that.dateOfPublication != null)
            return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;
        if (grantIds != null ? !grantIds.equals(that.grantIds) : that.grantIds != null) return false;
        if (grantAgencies != null ? !grantAgencies.equals(that.grantAgencies) : that.grantAgencies != null)
            return false;
        return !(paperUrls != null ? !paperUrls.equals(that.paperUrls) : that.paperUrls != null);

    }

    @Override
    public int hashCode() {
        int result = alleleSymbols != null ? alleleSymbols.hashCode() : 0;
        result = 31 * result + (alleleAccessionIds != null ? alleleAccessionIds.hashCode() : 0);
        result = 31 * result + (geneAccessionIds != null ? geneAccessionIds.hashCode() : 0);
        result = 31 * result + (impcGeneLinks != null ? impcGeneLinks.hashCode() : 0);
        result = 31 * result + (mgiAlleleNames != null ? mgiAlleleNames.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (journal != null ? journal.hashCode() : 0);
        result = 31 * result + (pmid != null ? pmid.hashCode() : 0);
        result = 31 * result + (dateOfPublication != null ? dateOfPublication.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (grantIds != null ? grantIds.hashCode() : 0);
        result = 31 * result + (grantAgencies != null ? grantAgencies.hashCode() : 0);
        result = 31 * result + (paperUrls != null ? paperUrls.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ReferenceDTO{" +
                "alleleSymbols=" + alleleSymbols +
                ", alleleAccessionIds=" + alleleAccessionIds +
                ", geneAccessionIds=" + geneAccessionIds +
                ", impcGeneLinks=" + impcGeneLinks +
                ", mgiAlleleNames=" + mgiAlleleNames +
                ", title='" + title + '\'' +
                ", journal='" + journal + '\'' +
                ", pmid='" + pmid + '\'' +
                ", dateOfPublication='" + dateOfPublication + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", grantIds=" + grantIds +
                ", grantAgencies=" + grantAgencies +
                ", paperUrls=" + paperUrls +
                '}';
    }
}
