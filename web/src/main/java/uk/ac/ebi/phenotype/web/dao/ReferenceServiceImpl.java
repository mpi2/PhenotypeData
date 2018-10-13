package uk.ac.ebi.phenotype.web.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uk.ac.ebi.phenotype.web.dto.Publication;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

@Service
public class ReferenceServiceImpl implements ReferenceService {

    private final ReferenceRepository referenceRepository;

    @Autowired
    ReferenceServiceImpl(ReferenceRepository referenceRepository){
        this.referenceRepository = referenceRepository;
    }

    @Override
    public List<Publication> getAllReviewed(String filter, int start, int length, String orderBy, String sortOrder) {
        PageRequest pageRequest = getPageRequest(start,length, orderBy, sortOrder);
        if(filter == null || filter.isEmpty()) {
            return referenceRepository.findAllByReviewedIsTrueAndFalsePositiveIsFalse(pageRequest).getContent();
        } else {
            filter = ".*" + filter + ".*";
            return referenceRepository.findAllByReviewedIsTrueAndFalsePositiveIsFalseContains(filter, pageRequest).getContent();
        }
    }

    @Override
    public int countReviewed() {
        return referenceRepository.countAllByReviewedIsTrueAndFalsePositiveIsFalse();
    }

    @Override
    public int countFiltered(String filter) {
        filter = ".*" + filter + ".*";
        return referenceRepository.countAllByReviewedIsTrueAndFalsePositiveIsFalseContains(filter);
    }

    @Override
    public List<Publication> getAllConsortium(String filter, int start, int length, String orderBy, String sortOrder) {
        PageRequest pageRequest = getPageRequest(start,length, orderBy, sortOrder);
        if(filter == null || filter.isEmpty()) {
            return referenceRepository.findAllByReviewedIsTrueAndFalsePositiveIsFalseAndConsortiumPaperIsTrue(pageRequest).getContent();
        } else {
            filter = ".*" + filter + ".*";
            return referenceRepository.findAllByReviewedIsTrueAndFalsePositiveIsFalseAndConsortiumPaperIsTrueContains(filter, pageRequest).getContent();
        }
    }

    @Override
    public int countConsortium() {
        return referenceRepository.countAllByReviewedIsTrueAndFalsePositiveIsFalseAndConsortiumPaperIsTrue();
    }

    @Override
    public int countConsortiumFiltered(String filter) {
        filter = ".*" + filter + ".*";
        return referenceRepository.countAllByReviewedIsTrueAndFalsePositiveIsFalseAndConsortiumPaperIsTrueContains(filter);
    }

    @Override
    public List<Publication> getAllAgency(String agency, String filter, int start, int length, String orderBy, String sortOrder) {
        PageRequest pageRequest = getPageRequest(start,length, orderBy, sortOrder);
        if(filter == null || filter.isEmpty()) {
            return referenceRepository.findDistinctByReviewedIsTrueAndFalsePositiveIsFalseAndGrantsList_AgencyIs(agency, pageRequest).getContent();
        } else {
            filter = ".*" + filter + ".*";
            return referenceRepository.findByAgencyFiltered(agency, filter, pageRequest).getContent();
        }
    }

    @Override
    public int countAgency(String agency) {
        return referenceRepository.countDistinctByGrantsList_AgencyIs(agency);
    }

    @Override
    public int countAgencyFiltered(String agency, String filter) {
        filter = ".*" + filter + ".*";
        return referenceRepository.countByAgencyFiltered(agency, filter);
    }

    @Override
    public List<Publication> getAllMeshTerm(String meshTerm, String filter, int start, int length, String orderBy, String sortOrder) {
        return null;
    }

    @Override
    public int countMeshTerm(String meshTerm) {
        return 0;
    }

    @Override
    public int countMeshTermFiltered(String filter) {
        return 0;
    }

    @Override
    public TreeMap<String, Integer> getCountByYear() {
        return referenceRepository.getPublicationsByYear();
    }

    @Override
    public TreeMap<String, Integer> getAddedCountByYear() {
        return referenceRepository.getAddedPublicationsByYear();
    }

    @Override
    public TreeMap<String, TreeMap<String, Integer>> getCountByQuarter() {
        return referenceRepository.getPublicationsByQuarter();
    }

    @Override
    public LinkedHashMap<String, Integer> getCountByAgency() {
        return referenceRepository.getPublicationsByAgency();
    }

    @Override
    public List<Publication> getAllBiosystem(String meshTerm, String filter, int start, int length, String orderBy, String sortOrder) {
        PageRequest pageRequest = getPageRequest(start,length, orderBy, sortOrder);
        if(filter == null || filter.isEmpty()) {
            return referenceRepository.findByMeshHeadingListContains(meshTerm, pageRequest).getContent();
        } else {
            filter = ".*" + filter + ".*";
            return referenceRepository.findByMeshtermFiltered(meshTerm, filter, pageRequest).getContent();
        }

    }

    @Override
    public int countBiosystem(String sSearch) {
        return referenceRepository.countDistinctByMeshHeadingListContains(sSearch);
    }

    @Override
    public int countBiosystemFiltered(String sSearch, String filter) {
        return referenceRepository.countByMeshtermFiltered(sSearch, filter);
    }


    private PageRequest getPageRequest(int start, int length, String orderBy, String sortOrder) {
        int page = Math.floorDiv(start, length);
        Sort.Direction direction = Sort.Direction.DESC;
        if(sortOrder.equals("ASC"))
            direction = Sort.Direction.ASC;
        return new PageRequest(page, length, direction, orderBy);
    }
}