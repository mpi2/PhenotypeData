/*******************************************************************************
 * Copyright © 2019 EMBL - European Bioinformatics Institute
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package uk.ac.ebi.phenotype.util;

import org.mousephenotype.cda.utilities.DisplayPager;
import org.mousephenotype.cda.utilities.DisplaySorter;
import uk.ac.ebi.phenotype.web.dao.ReferenceService;
import uk.ac.ebi.phenotype.web.dto.Publication;

import javax.validation.constraints.NotNull;
import java.util.List;


public class PublicationFetcher {

    private String           agency;
    private DisplayPager     displayPager  = new DisplayPager(0, 10);
    private DisplaySorter    displaySorter = new DisplaySorter("firstPublicationDate", DisplaySorter.SortByDirection.DESC);
    private String           filter;
    private PublicationType  publicationType;
    private ReferenceService referenceService;


    /**
     * Supported publication types
     */
    public enum PublicationType {
        IMPC_CONSORTIUM,
        FUNDING_AGENCY,
        ACCEPTED_IMPC_PUBLICATION,
        BIOSYSTEM
        ;
    }


    // CUSTOM GETTERS


    /**
     * return the publication count for all documents specified by {@code publicationType}, filtered by {@code filter}
     * and {@code agency} if specified.
     * @return
     */
    public int getAllPublicationsCount() {

        switch(publicationType) {
            case IMPC_CONSORTIUM:
                return referenceService.countConsortium();

            case FUNDING_AGENCY:
                return referenceService.countAgency(agency);

            case BIOSYSTEM:
                return referenceService.countMeshTerm(agency);

            default:
                return referenceService.countReviewed();
        }
    }


    /**
     * @return The display publication count for all documents specified by {@code publicationType}, filtered by
     * {@code filter} and {@code agency} if specified.
     */
    public int getDisplayPublicationsCount() {

        switch(publicationType) {
            case IMPC_CONSORTIUM:
                return ((filter == null) || (filter.isEmpty())
                        ? referenceService.countConsortium()
                        : referenceService.countConsortiumFiltered(filter));

            case FUNDING_AGENCY:
                return ((filter == null) || filter.isEmpty())
                        ? referenceService.countAgency(agency)
                        : referenceService.countAgencyFiltered(agency, filter);

            case BIOSYSTEM:
                return ((filter == null) || filter.isEmpty())
                        ? referenceService.countMeshTerm(agency)
                        : referenceService.countMeshTermFiltered(agency, filter);

            default:
                return ((filter == null) || (filter.isEmpty()))
                        ? referenceService.countReviewed()
                        : referenceService.countFiltered(filter);
        }
    }

    /**
     * @return All display publication documents specified by {@code publicationType}, filtered by {@code filter}
     * and {@code agency} if specified.
     */
    public List<Publication> getDisplayPublications() {

        switch(publicationType) {
            case IMPC_CONSORTIUM:
                return referenceService.getAllConsortium(filter,
                                                         displayPager.getStartingDocumentOffset(),
                                                         displayPager.getNumDocumentsToDisplay(),
                                                         displaySorter.getSortByFieldName(),
                                                         displaySorter.getSortByDirection().toString());

            case FUNDING_AGENCY:
                return referenceService.getAllAgency(agency,
                                                     filter,
                                                     displayPager.getStartingDocumentOffset(),
                                                     displayPager.getNumDocumentsToDisplay(),
                                                     displaySorter.getSortByFieldName(),
                                                     displaySorter.getSortByDirection().toString());
            case BIOSYSTEM:
                return referenceService.getAllMeshTerm(agency,
                        filter,
                        displayPager.getStartingDocumentOffset(),
                        displayPager.getNumDocumentsToDisplay(),
                        displaySorter.getSortByFieldName(),
                        displaySorter.getSortByDirection().toString());

            default:
                return referenceService.getAllReviewed(filter,
                                                       displayPager.getStartingDocumentOffset(),
                                                       displayPager.getNumDocumentsToDisplay(),
                                                       displaySorter.getSortByFieldName(),
                                                       displaySorter.getSortByDirection().toString());
        }
    }

    /**
     * @return All publication documents specified by {@code publicationType}, filtered by {@code filter}
     * and {@code agency} if specified.
     */
    public List<Publication> getAllPublications() {

        switch(publicationType) {
            case IMPC_CONSORTIUM:

                return referenceService.getAllConsortium(filter,
                                                         0,
                                                         filter == null ? referenceService.countConsortium() : referenceService.countConsortiumFiltered(filter),
                                                         displaySorter.getSortByFieldName(),
                                                         displaySorter.getSortByDirection().toString());

            case FUNDING_AGENCY:
                return referenceService.getAllAgency(agency,
                                                     filter,
                                                     0,
                                                     filter == null ? referenceService.countAgency(agency) : referenceService.countAgencyFiltered(agency, filter),
                                                     displaySorter.getSortByFieldName(),
                                                     displaySorter.getSortByDirection().toString());
            case BIOSYSTEM:
                return referenceService.getAllMeshTerm(agency,
                        filter,
                        0,
                        filter == null ? referenceService.countAgency(agency) : referenceService.countAgencyFiltered(agency, filter),
                        displaySorter.getSortByFieldName(),
                        displaySorter.getSortByDirection().toString());

            default:
                return referenceService.getAllReviewed(filter,
                                                       0,
                                                       filter == null ? referenceService.countReviewed() : referenceService.countFiltered(filter),
                                                       displaySorter.getSortByFieldName(),
                                                       displaySorter.getSortByDirection().toString());
        }
    }


    // AUTOMATICALLY GENERATED GETTERS AND SETTERS


    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public PublicationType getPublicationType() {
        return publicationType;
    }

    public void setPublicationType(PublicationType publicationType) {
        this.publicationType = publicationType;
    }

    public DisplaySorter getDisplaySorter() {
        return displaySorter;
    }

    public void setDisplaySorter(DisplaySorter displaySorter) {
        this.displaySorter = displaySorter;
    }

    public DisplayPager getDisplayPager() {
        return displayPager;
    }

    public void setDisplayPager(DisplayPager displayPager) {
        this.displayPager = displayPager;
    }

    public PublicationFetcher(ReferenceService referenceService, @NotNull PublicationType publicationType) {
        this.referenceService = referenceService;
        this.publicationType = publicationType;
    }
}