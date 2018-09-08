package uk.ac.ebi.phenotype.web.dao;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public interface ReferenceRepositoryCustom {
    TreeMap<String, Integer> getPublicationsByYear();
    TreeMap<String, Integer> getAddedPublicationsByYear();
    TreeMap<String, TreeMap<String, Integer>> getPublicationsByQuarter();
    LinkedHashMap<String, Integer> getPublicationsByAgency();
}
