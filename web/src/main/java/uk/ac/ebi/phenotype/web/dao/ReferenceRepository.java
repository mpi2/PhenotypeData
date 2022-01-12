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

    final String reviewedContainsQuery =
      " {$and: "
      +  "["
      +    "{$or: "
      +      "["
      +        "{title:                  {$regex : ?0, '$options' : 'i' }},"
      +        "{abstractText:           {$regex : ?0, '$options' : 'i' }},"
      +        "{authorString:           {$regex : ?0, '$options' : 'i' }},"
      +        "{'alleles.alleleSymbol': {$regex : ?0, '$options' : 'i' }},"
      +        "{'alleles.geneSymbol':   {$regex : ?0, '$options' : 'i' }}"
      +      "]"
      +    "},"
      +   "{status: 'reviewed'}"
      +  "]"
    + " }";

    final String reviewedContainsGeneQuery =
            " {$and: "
                    +  "["
                    +    "{$or: "
                    +      "["
                    +        "{'alleles.alleleSymbol': {$regex : ?0, '$options' : 'i' }},"
                    +        "{'alleles.geneSymbol':   {$regex : ?0, '$options' : 'i' }}"
                    +      "]"
                    +    "},"
                    +   "{status: 'reviewed'}"
                    +  "]"
                    + " }";

    @Query(reviewedContainsQuery)
    Page<Publication> findReviewedContains(String filter, Pageable pageable);

    @Query(reviewedContainsGeneQuery)
    Page<Publication> findReviewedContainsGene(String genes, Pageable pageable);

    @CountQuery(reviewedContainsQuery)
    int countReviewedContains(String filter);

    Page<Publication> findAllByStatusIsAndConsortiumPaperIsTrue(String reviewed, Pageable pageable);

    int countAllByStatusIsAndConsortiumPaperIsTrue(String status);

    final String reviewedConsortiumPaperQuery =
        " {$and: "
          +  "["
          +    "{$or: "
          +      "["
          +        "{title:                  {$regex : ?0, '$options' : 'i' }},"
          +        "{abstractText:           {$regex : ?0, '$options' : 'i' }},"
          +        "{authorString:           {$regex : ?0, '$options' : 'i' }},"
          +        "{'alleles.alleleSymbol': {$regex : ?0, '$options' : 'i' }},"
          +        "{'alleles.geneSymbol':   {$regex : ?0, '$options' : 'i' }}"
          +      "]"
          +    "},"
          +   "{status: 'reviewed', consortiumPaper: true}"
          +  "]"
      + " }";
    @Query(reviewedConsortiumPaperQuery)
    Page<Publication> findReviewedConsortiumPaperIsTrueContains(String filter, Pageable pageable);

    @CountQuery(reviewedConsortiumPaperQuery)
    int countReviewedAndConsortiumPaperIsTrueContains(String filter);

    Page<Publication> findDistinctByStatusEqualsAndGrantsList_AgencyIs(String status, String agency, Pageable pageable);

    @CountQuery("{ 'grantsList.agency': ?0, status: 'reviewed'}")
    int countDistinctByGrantsList_AgencyIs(String agency);

    final String agencyFilteredQuery =
        " {$and: "
            +  "["
            +    "{$or: "
            +      "["
            +        "{title:                  {$regex : ?1, '$options' : 'i' }},"
            +        "{abstractText:           {$regex : ?1, '$options' : 'i' }},"
            +        "{authorString:           {$regex : ?1, '$options' : 'i' }},"
            +        "{'alleles.alleleSymbol': {$regex : ?1, '$options' : 'i' }},"
            +        "{'alleles.geneSymbol':   {$regex : ?1, '$options' : 'i' }}"
            +      "]"
            +    "},"
            +   "{status: 'reviewed', 'grantsList.agency': ?0}"
            +  "]"
      + " }";
    @Query(agencyFilteredQuery)
    Page<Publication> findByAgencyFiltered(String agency, String filter, Pageable pageable);

    @CountQuery(agencyFilteredQuery)
    int countByAgencyFiltered(String agency, String filter);

    @Query("{$and: [{meshHeadingList: {'$regex' : ?0, '$options' : 'i'}}, {status: 'reviewed'}]}")
    Page<Publication> findByMeshHeadingListContains(String mesh, Pageable pageable);

    @CountQuery("{$and: [{meshHeadingList: {'$regex' : ?0, '$options' : 'i'}}, {status: 'reviewed'}]}")
    int countDistinctByMeshHeadingListContains(String mesh);

    final String meshtermFilteredQuery =
        " {$and: "
           +  "["
           +    "{$or: "
           +      "["
           +        "{title:                  {$regex : ?1, '$options' : 'i' }},"
           +        "{abstractText:           {$regex : ?1, '$options' : 'i' }},"
           +        "{authorString:           {$regex : ?1, '$options' : 'i' }},"
           +        "{'alleles.alleleSymbol': {$regex : ?1, '$options' : 'i' }},"
           +        "{'alleles.geneSymbol':   {$regex : ?1, '$options' : 'i' }}"
           +      "]"
           +    "},"
           +   "{status: 'reviewed', meshHeadingList: {'$regex' : ?0, '$options' : 'i'}}"
           +  "]"
      + " }";
    @Query(meshtermFilteredQuery)
    Page<Publication> findByMeshtermFiltered(String meshTerm, String filter, Pageable pageable);

    @CountQuery(meshtermFilteredQuery)
    int countByMeshtermFiltered(String meshTerm, String filter);
}
