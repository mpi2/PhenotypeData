/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/

package org.mousephenotype.cda.support;

import org.mousephenotype.cda.exception.TestException;
import org.mousephenotype.cda.utilities.RunStatus;
import org.mousephenotype.cda.web.DownloadType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mrelac
 *
 * This class encapsulates the code and data necessary to represent the important
 * components of a search page 'imagesGrid' HTML table common to all Image facet
 * views.
 */
@Deprecated
public class SearchImageTable extends SearchFacetTable {
    private SearchImageAnnotationView searchImageAnnotationView = null;
    private SearchImageImageView      searchImageImageView      = null;
    protected final TestUtils         testUtils                 = new TestUtils();

    private static final Map<SearchFacetTable.TableComponent, By> imageMap = new HashMap();
    static {
        imageMap.put(SearchFacetTable.TableComponent.BY_TABLE, By.xpath("//table[@id='imagesGrid']"));
        imageMap.put(SearchFacetTable.TableComponent.BY_TABLE_TR, By.xpath("//table[@id='imagesGrid']/tbody/tr"));
        imageMap.put(SearchFacetTable.TableComponent.BY_SELECT_GRID_LENGTH, By.xpath("//select[@name='imagesGrid_length']"));
    }

    /**
     * Creates a new <code>SearchImageTable</code> instance with the given map.
     * @param driver A <code>WebDriver</code> instance pointing to the search
     * facet table with thead and tbody definitions.
     * @param timeoutInSeconds The <code>WebDriver</code> timeout, in seconds
     * TableComponent</code>.
     *
     * @throws TestException
     *
     */
    public SearchImageTable(WebDriver driver, int timeoutInSeconds) throws TestException {
        super(driver, timeoutInSeconds, imageMap);

        switch(getCurrentView()) {
            case ANNOTATION_VIEW:
                searchImageAnnotationView = new SearchImageAnnotationView(driver, timeoutInSeconds, imageMap);
                break;

            case IMAGE_VIEW:
                searchImageImageView = new SearchImageImageView(driver, timeoutInSeconds, imageMap);
                break;
        }
    }

    public final ImagesView getCurrentView() {
        String imgViewSwitcherText = driver.findElement(By.cssSelector("span#imgViewSwitcher")).getText();
        return (imgViewSwitcherText.equals(SHOW_IMAGE_VIEW) ? ImagesView.ANNOTATION_VIEW : ImagesView.IMAGE_VIEW);
    }

    public void setCurrentView(ImagesView view) throws TestException {
//        if (getCurrentView() != view) {
//            SearchPage.WindowState toolboxState = getToolboxState();            // Save tool box state for later restore.
//            clickToolbox(SearchPage.WindowState.CLOSED);
//            WebElement imgViewSwitcherElement = driver.findElement(By.cssSelector("span#imgViewSwitcher"));
//            testUtils.scrollToTop(driver, imgViewSwitcherElement, -50);         // Scroll 'Show Image View' link into view.
//            driver.findElement(By.cssSelector("span#imgViewSwitcher")).click();
//            updateImageTableAfterChange();
//            if (toolboxState != getToolboxState())
//                clickToolbox(toolboxState);
//        }
    }

    /**
     * This method is meant to be called after any change to the image table,
     * such as changing between annotation and image view, or changing
     * pagination pages. It is required to keep the image table internals in
     * sync with what is seen on the page.
     */
    public void updateImageTableAfterChange() throws TestException {
        switch (getCurrentView()) {
            case ANNOTATION_VIEW:
                searchImageAnnotationView = new SearchImageAnnotationView(driver, timeoutInSeconds, imageMap);
                searchImageImageView = null;
                break;

            case IMAGE_VIEW:
                searchImageAnnotationView = null;
                searchImageImageView = new SearchImageImageView(driver, timeoutInSeconds, imageMap);
                break;
        }

        setTable(driver.findElement(imageMap.get(TableComponent.BY_TABLE)));
    }

    /**
     * Validates download data against this search table instance.
     *
     * @param data The download data used for comparison
     * @param downloadType Supported download type - e.g. TSV, XLS
     * @return validation status
     */
    @Override
    public RunStatus validateDownload(String[][] data, DownloadType downloadType) {
        RunStatus status = new RunStatus();

        switch (getCurrentView()) {
            case ANNOTATION_VIEW:
                status = searchImageAnnotationView.validateDownload(data, downloadType);
                break;

            case IMAGE_VIEW:
                status = searchImageImageView.validateDownload(data, downloadType);
                break;
        }

        return status;
    }
}