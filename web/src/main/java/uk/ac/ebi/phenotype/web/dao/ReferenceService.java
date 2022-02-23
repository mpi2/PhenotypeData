package uk.ac.ebi.phenotype.web.dao;

import uk.ac.ebi.phenotype.web.dto.Publication;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

public interface ReferenceService {

    List<Publication> getAllReviewed(String filter, int start, int length, String orderBy, String sortOrder);
    List<Publication> getReviewedByPmidList(List<String> pmidList);
    int countReviewed();
    int countFiltered(String filter);

    List<Publication> getAllConsortium(String filter, int start, int length, String orderBy, String sortOrder);
    int countConsortium();
    int countConsortiumFiltered(String filter);

    List<Publication> getAllAgency(String agency, String filter, int start, int length, String orderBy, String sortOrder);
    int countAgency(String agency);
    int countAgencyFiltered(String agency, String filter);

    List<Publication> getAllMeshTerm(String meshTerm, String filter, int start, int length, String orderBy, String sortOrder);
    int countMeshTerm(String meshTerm);
    int countMeshTermFiltered(String meshTerm, String filter);

    TreeMap<String, Integer> getCountByYear();

    TreeMap<String, Integer> getAddedCountByYear();

    TreeMap<String, TreeMap<String, Integer>> getCountByQuarter();

    LinkedHashMap<String, Integer> getCountByAgency();

    List<Publication> getAllBiosystem(String sSearch, String filter, int start, int length, String orderByField, String orderByDirection);

    int countBiosystem(String sSearch);

    int countBiosystemFiltered(String sSearch, String filter);
}
