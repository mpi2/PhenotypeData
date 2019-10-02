package uk.ac.ebi.phenotype.web.dao;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.phenotype.web.dto.Publication;


@Repository
public interface ReferenceRepository extends MongoRepository<Publication, ObjectId>, ReferenceRepositoryCustom  {

    Page<Publication> findAllByStatusIs(Pageable pageable, String status);

    int countAllByStatusIs(String status);

    @Query("{$and: [{$or: [{title: {$regex : ?0 }}, {abstractText: {$regex : ?0 }}, {authorString: {$regex : ?0 }}]}, {status: 'reviewed'}]}")
    Page<Publication> findReviewedContains(String filter, Pageable pageable);

    @CountQuery("{$and: [{$or: [{title: {$regex : ?0 }}, {abstractText: {$regex : ?0 }}, {authorString: {$regex : ?0 }}]}, {status: 'reviewed'}]}")
    int countReviewedContains(String filter);


    Page<Publication> findAllByStatusIsAndConsortiumPaperIsTrue(String reviewed, Pageable pageable);

    int countAllByStatusIsAndConsortiumPaperIsTrue(String status);

    @Query("{$and: [{$or: [{title: {$regex : ?0 }}, {abstractText: {$regex : ?0 }}, {authorString: {$regex : ?0 }}]}, {status: 'reviewed', consortiumPaper: true}]}")
    Page<Publication> findReviewedConsortiumPaperIsTrueContains(String filter, Pageable pageable);

    @CountQuery("{$and: [{$or: [{title: {$regex : ?0 }}, {abstractText: {$regex : ?0 }}, {authorString: {$regex : ?0 }}]}, {status: 'reviewed', consortiumPaper: true}]}")
    int countReviewedAndConsortiumPaperIsTrueContains(String filter);

    Page<Publication> findDistinctByStatusEqualsAndGrantsList_AgencyIs(String status, String agency, Pageable pageable);

    @CountQuery("{ grantsList.agency: ?0, status: 'reviewed'}")
    int countDistinctByGrantsList_AgencyIs(String agency);

    @Query("{$and: [{$or: [{title: {$regex : ?1 }}, {abstractText: {$regex : ?1 }}, {authorString: {$regex : ?1 }}]}, {status: 'reviewed', grantsList.agency: ?0}]}")
    Page<Publication> findByAgencyFiltered(String agency, String filter, Pageable pageable);

    @CountQuery("{$and: [{$or: [{title: {$regex : ?1 }}, {abstractText: {$regex : ?1 }}, {authorString: {$regex : ?1 }}]}, {status: 'reviewed', grantsList.agency: ?0}]}")
    int countByAgencyFiltered(String agency, String filter);

    @Query("{meshHeadingList: {'$regex' : ?0, '$options' : 'i'}}")
    Page<Publication> findByMeshHeadingListContains(String mesh, Pageable pageable);

    @CountQuery("{meshHeadingList: {'$regex' : ?0, '$options' : 'i'}}")
    int countDistinctByMeshHeadingListContains(String mesh);

    @Query("{$and: [{$or: [{title: {$regex : ?1 }}, {abstractText: {$regex : ?1 }}, {authorString: {$regex : ?1 }}]}, {status: 'reviewed', meshHeadingList: {'$regex' : ?0, '$options' : 'i'}}]}")
    Page<Publication> findByMeshtermFiltered(String meshTerm, String filter, Pageable pageable);

    @CountQuery("{$and: [{$or: [{title: {$regex : ?1 }}, {abstractText: {$regex : ?1 }}, {authorString: {$regex : ?1 }}]}, {status: 'reviewed', meshHeadingList: {'$regex' : ?0, '$options' : 'i'}}]}")
    int countByMeshtermFiltered(String meshTerm, String filter);
}
