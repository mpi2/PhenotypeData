package org.mousephenotype.cda.solr.service.dto;

/**
 * Created by ilinca on 25/10/2016.
 */
public class CountTableRow {

    String category;
    String mpId;
    Integer count;

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getMpId() {
        return mpId;
    }
    public void setMpId(String mpId) {
        this.mpId = mpId;
    }
    public Integer getCount() {
        return count;
    }
    public void setCount(Integer geneNo) {
        this.count = geneNo;
    }

    public CountTableRow(String category, String mpId, Integer count ){

        this.setCategory(category);
        this.setCount(count);
        this.setMpId(mpId);

    }

    public CountTableRow() {   }


    @Override
    public String toString() {
        return "CountTableRow [category=" + category + ", mpId=" + mpId + ", count=" + count + "]";
    }

}
