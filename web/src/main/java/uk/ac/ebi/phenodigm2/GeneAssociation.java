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

import java.util.List;

/**
 * Object describing an association to a gene
 */
public class GeneAssociation extends AssociationType implements IdUrl {

    private String id;
    private String symbol;
    private List<String> symbolsWithdrawn;

    // used internally to construct URLs
    private static String baseUrlMGI = "http://www.informatics.jax.org/accession/MGI:";
    private static String baseUrlHGNC = "http://www.genenames.org/data/hgnc_data.php?hgnc_id=";

    public GeneAssociation(String id, String symbol) {
        this.id = id;
        this.symbol = symbol;
    }

    public String getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public List<String> getSymbolsWithdrawn() {
        return symbolsWithdrawn;
    }

    public void setSymbolsWithdrawn(List<String> symbolsWithdrawn) {
        this.symbolsWithdrawn = symbolsWithdrawn;
    }

    @Override
    public String toString() {
        return "GeneAssociation{" + "id=" + id + ", symbol=" + symbol + ", symbolsWithdrawn=" + symbolsWithdrawn + '}';
    }

    /**
     *
     * @return
     *
     * string with a url for a page with a gene definition or extra information
     *
     */
    @Override
    public String getExternalUrl() {
        String[] tokens = id.split(":");
        switch (tokens[0]) {
            case "MGI":
                return baseUrlMGI + tokens[1];
            case "HGNC":
                return baseUrlHGNC + tokens[1];
            default:
                return "[unknown]";
        }
    }

}
