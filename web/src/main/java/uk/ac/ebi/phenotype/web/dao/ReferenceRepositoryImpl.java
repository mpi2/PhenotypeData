package uk.ac.ebi.phenotype.web.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AccumulatorOperators;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import uk.ac.ebi.phenotype.web.dto.Publication;
import uk.ac.ebi.phenotype.web.dto.YearCount;
import uk.ac.ebi.phenotype.web.dto.YearQuarterCount;

import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.DateOperators.Month.monthOf;
import static org.springframework.data.mongodb.core.aggregation.DateOperators.Year.yearOf;
import static org.springframework.data.mongodb.core.query.Criteria.where;

public class ReferenceRepositoryImpl implements ReferenceRepositoryCustom {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public TreeMap<String, Integer> getPublicationsByYear() {
        AggregationExpression yearOperation = yearOf("firstPublicationDate");
        Aggregation aggregation = Aggregation.newAggregation(
                match(where("reviewed").is(true).and("falsePositive").is(false)),
                project().and(yearOperation).as("publicationYear"),
                group("publicationYear").count().as("count")
        );

        List<YearCount> results = this.mongoTemplate.aggregate(aggregation, Publication.class, YearCount.class).getMappedResults();
        TreeMap<String, Integer> countMap = new TreeMap<>();
        for (YearCount yearCount: results) {
            countMap.put(yearCount.getYear(), yearCount.getCount());
        }
        return countMap;
    }

    @Override
    public TreeMap<String, Integer> getAddedPublicationsByYear() {
        TreeMap<String, Integer> byYearMap = this.getPublicationsByYear();
        TreeMap<String, Integer> addedMap = new TreeMap<>();
        int addedCount = 0;

        for (Map.Entry<String, Integer> entry : byYearMap.entrySet()) {
            String key = entry.getKey();
            Integer count = entry.getValue();
            addedCount += count;
            addedMap.put(key, addedCount);
        }
        return addedMap;
    }

    @Override
    public TreeMap<String, TreeMap<String, Integer>> getPublicationsByQuarter() {
        AggregationExpression yearOperation = yearOf("firstPublicationDate");
        AggregationExpression monthOperation = monthOf("firstPublicationDate");
        ConditionalOperators.Cond condQ1Operation = ConditionalOperators.when(Criteria.where("publicationMonth").lte(3))
                .then(1)
                .otherwise(0);
        ConditionalOperators.Cond condQ2Operation = ConditionalOperators.when(new Criteria().andOperator(
                where("publicationMonth").gt(3),
                where("publicationMonth").lte(6)
                ))
                .then(1)
                .otherwise(0);
        ConditionalOperators.Cond condQ3Operation = ConditionalOperators.when(new Criteria().andOperator(
                where("publicationMonth").gt(6),
                where("publicationMonth").lte(9)
        ))
                .then(1)
                .otherwise(0);
        ConditionalOperators.Cond condQ4Operation = ConditionalOperators.when(Criteria.where("publicationMonth").gt(9))
                .then(1)
                .otherwise(0);
        Aggregation aggregation = Aggregation.newAggregation(
                match(where("reviewed").is(true).and("falsePositive").is(false)),
                project().and(yearOperation).as("publicationYear").and(monthOperation).as("publicationMonth"),
                project("publicationYear")
                        .and(condQ1Operation).as("q1")
                        .and(condQ2Operation).as("q2")
                        .and(condQ3Operation).as("q3")
                        .and(condQ4Operation).as("q4")
                ,
                group("publicationYear")
                        .sum("q1").as("q1")
                        .sum("q2").as("q2")
                        .sum("q3").as("q3")
                        .sum("q4").as("q4")
        );

        List<YearQuarterCount> results = this.mongoTemplate.aggregate(aggregation, Publication.class, YearQuarterCount.class).getMappedResults();
        TreeMap<String, TreeMap<String, Integer>> countMap = new TreeMap<>();
        for (YearQuarterCount yearQuarterCount: results) {
            TreeMap<String, Integer> quarterCounts = new TreeMap<>();
            if(yearQuarterCount.getQ1() > 0)
                quarterCounts.put("Q1", yearQuarterCount.getQ1());
            if(yearQuarterCount.getQ2() > 0)
                quarterCounts.put("Q2", yearQuarterCount.getQ2());
            if(yearQuarterCount.getQ3() > 0)
                quarterCounts.put("Q3", yearQuarterCount.getQ3());
            if(yearQuarterCount.getQ4() > 0)
                quarterCounts.put("Q4", yearQuarterCount.getQ4());
            countMap.put(yearQuarterCount.getYear(), quarterCounts);
        }
        return countMap;
    }

    @Override
    public LinkedHashMap<String, Integer> getPublicationsByAgency() {
        Aggregation aggregation = Aggregation.newAggregation(
                match(where("reviewed").is(true).and("falsePositive").is(false)),
                project("pmid", "grantsList"),
                unwind("grantsList"),
                group("pmid").addToSet("grantsList.agency").as("agencies"),
                unwind("agencies"),
                group("agencies").count().as("count"),
                sort(Sort.Direction.DESC, "count")
        );

        System.out.println(aggregation.toString());

        List<YearCount> results = this.mongoTemplate.aggregate(aggregation, Publication.class, YearCount.class).getMappedResults();
        LinkedHashMap<String, Integer> countMap = new LinkedHashMap<>();
        for (YearCount yearCount: results) {
            countMap.put(yearCount.getYear(), yearCount.getCount());
        }
        return countMap;
    }
}
