package uk.ac.ebi.phenotype.web.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.phenotype.web.dto.Publication;
import org.bson.types.ObjectId;

import java.util.List;


@Repository
public interface ReferenceRepository extends MongoRepository<Publication, ObjectId>, ReferenceRepositoryCustom  {

    Page<Publication> findAllByReviewedIsTrueAndFalsePositiveIsFalse(Pageable pageable);

    int countAllByReviewedIsTrueAndFalsePositiveIsFalse();

    @Query("{$and: [{$or: [{title: {$regex : ?0 }}, {abstractText: {$regex : ?0 }}, {authorString: {$regex : ?0 }}]}, {reviewed: true, falsePositive: false}]}")
    Page<Publication> findAllByReviewedIsTrueAndFalsePositiveIsFalseContains(String filter, Pageable pageable);

    @CountQuery("{$and: [{$or: [{title: {$regex : ?0 }}, {abstractText: {$regex : ?0 }}, {authorString: {$regex : ?0 }}]}, {reviewed: true, falsePositive: false}]}")
    int countAllByReviewedIsTrueAndFalsePositiveIsFalseContains(String filter);


    Page<Publication> findAllByReviewedIsTrueAndFalsePositiveIsFalseAndConsortiumPaperIsTrue(Pageable pageable);

    int countAllByReviewedIsTrueAndFalsePositiveIsFalseAndConsortiumPaperIsTrue();

    @Query("{$and: [{$or: [{title: {$regex : ?0 }}, {abstractText: {$regex : ?0 }}, {authorString: {$regex : ?0 }}]}, {reviewed: true, falsePositive: false, consortiumPaper: true}]}")
    Page<Publication> findAllByReviewedIsTrueAndFalsePositiveIsFalseAndConsortiumPaperIsTrueContains(String filter, Pageable pageable);

    @CountQuery("{$and: [{$or: [{title: {$regex : ?0 }}, {abstractText: {$regex : ?0 }}, {authorString: {$regex : ?0 }}]}, {reviewed: true, falsePositive: false, consortiumPaper: true}]}")
    int countAllByReviewedIsTrueAndFalsePositiveIsFalseAndConsortiumPaperIsTrueContains(String filter);

    Page<Publication> findDistinctByReviewedIsTrueAndFalsePositiveIsFalseAndGrantsList_AgencyIs(String agency, Pageable pageable);

    @CountQuery("{ grantsList.agency: ?0, reviewed: true, falsePositive: false}")
    int countDistinctByGrantsList_AgencyIs(String agency);

    @Query("{$and: [{$or: [{title: {$regex : ?1 }}, {abstractText: {$regex : ?1 }}, {authorString: {$regex : ?1 }}]}, {reviewed: true, falsePositive: false, grantsList.agency: ?0}]}")
    Page<Publication> findByAgencyFiltered(String agency, String filter, Pageable pageable);

    @CountQuery("{$and: [{$or: [{title: {$regex : ?1 }}, {abstractText: {$regex : ?1 }}, {authorString: {$regex : ?1 }}]}, {reviewed: true, falsePositive: false, grantsList.agency: ?0}]}")
    int countByAgencyFiltered(String agency, String filter);

    @Query("{meshHeadingList: {'$regex' : ?0, '$options' : 'i'}}")
    Page<Publication> findByMeshHeadingListContains(String mesh, Pageable pageable);

    @CountQuery("{meshHeadingList: {'$regex' : ?0, '$options' : 'i'}}")
    int countDistinctByMeshHeadingListContains(String mesh);

    @Query("{$and: [{$or: [{title: {$regex : ?1 }}, {abstractText: {$regex : ?1 }}, {authorString: {$regex : ?1 }}]}, {reviewed: true, falsePositive: false, meshHeadingList: {'$regex' : ?0, '$options' : 'i'}}]}")
    Page<Publication> findByMeshtermFiltered(String meshTerm, String filter, Pageable pageable);

    @CountQuery("{$and: [{$or: [{title: {$regex : ?1 }}, {abstractText: {$regex : ?1 }}, {authorString: {$regex : ?1 }}]}, {reviewed: true, falsePositive: false, meshHeadingList: {'$regex' : ?0, '$options' : 'i'}}]}")
    int countByMeshtermFiltered(String meshTerm, String filter);

}
