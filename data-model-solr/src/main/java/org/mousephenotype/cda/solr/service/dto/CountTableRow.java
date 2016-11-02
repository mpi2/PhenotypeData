package org.mousephenotype.cda.solr.service.dto;

import java.util.Comparator;

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


    public static Comparator<CountTableRow> getComparatorByCount()
    {
        Comparator<CountTableRow> comp = new Comparator<CountTableRow>(){
            @Override
            public int compare(CountTableRow a, CountTableRow b)
            {
                return b.getCount().compareTo(a.getCount()); // b compare to a so we get reverse order. We want highest numbers at the top!
            }
        };
        return comp;
    }

}
