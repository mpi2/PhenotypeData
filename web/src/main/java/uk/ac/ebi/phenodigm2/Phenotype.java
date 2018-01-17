/*
 * Copyright 2017 QMUL - Queen Mary University of London
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.phenodigm2;

import java.util.Arrays;

/**
 * Phenotype bean.
 *
 * Only holds an id and description.
 *
 */
public class Phenotype implements Comparable<Phenotype> {

    private final String id;
    private final String term;

    /**
     * Convenient constructor that tries to parse a long string into an id and
     * term.
     *
     * @param idterm String, when space separated, first element is treated as
     * an id and the rest as the term.
     *
     */
    public Phenotype(String idterm) {
        String[] tokens = idterm.split(" ");
        this.id = tokens[0];
        if (tokens.length >= 2) {
            this.term = String.join(" ", Arrays.copyOfRange(tokens, 1, tokens.length));
        } else {
            this.term = "";
        }
    }

    /**
     * Standard constructor that sets a phenotype id and term separately
     *
     * @param id
     * @param term
     */
    public Phenotype(String id, String term) {
        this.id = id;
        this.term = term;
    }

    public String getId() {
        return id;
    }

    public String getTerm() {
        return term;
    }

    @Override
    public String toString() {
        return id + " " + term;
    }          

    @Override
    public int compareTo(Phenotype o) {
        return term.compareTo(o.term);
    }
    
}
