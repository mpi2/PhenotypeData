package org.mousephenotype.cda.interfaces;

import java.util.ArrayList;
import java.util.List;

/**
 * Template for exporting data to various formats such as tsv, xls, xlsx, etc. Callers need only implement the abstract
 * methods getRow and getHeadings, each of which return a single row of data. The getRows default method provides an
 * easy, already implemented way for callers to convert a {@link List of the desired type {@link T} to a
 * {@link List<List<String>>}.
 */
public interface Exportable<T> {

    List<String> getRow(T t);
    List<String> getHeading();

    default List<List<String>> getRows(List<T> rawRows) {

        List<List<String>> matrix = new ArrayList<>();

        for (T t : rawRows) {
            matrix.add(getRow(t));
        }

        return matrix;
    }
}