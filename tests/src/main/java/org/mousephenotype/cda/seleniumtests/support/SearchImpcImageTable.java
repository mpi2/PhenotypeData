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

package org.mousephenotype.cda.seleniumtests.support;

import org.mousephenotype.cda.seleniumtests.exception.TestException;
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
public class SearchImpcImageTable extends SearchFacetTable {
    private SearchImpcImageAnnotationView searchImpcImageAnnotationView = null;
    private SearchImpcImageImageView searchImpcImageImageView = null;
    protected final TestUtils testUtils = new TestUtils();

    private static final Map<SearchFacetTable.TableComponent, By> impcImageMap = new HashMap();
    static {
        impcImageMap.put(SearchFacetTable.TableComponent.BY_TABLE, By.xpath("//table[@id='impc_imagesGrid']"));
        impcImageMap.put(SearchFacetTable.TableComponent.BY_TABLE_TR, By.xpath("//table[@id='impc_imagesGrid']/tbody/tr"));
        impcImageMap.put(SearchFacetTable.TableComponent.BY_SELECT_GRID_LENGTH, By.xpath("//select[@name='impc_imagesGrid_length']"));
    }


    /**
     * Creates a new <code>SearchImpcImageTable</code> instance with the given map.
     * @param driver A <code>WebDriver</code> instance pointing to the search
     * facet table with thead and tbody definitions.
     * @param timeoutInSeconds The <code>WebDriver</code> timeout, in seconds
     * TableComponent</code>.
     *
     * @throws TestException
     *
     */
    public SearchImpcImageTable(WebDriver driver, int timeoutInSeconds) throws TestException {
        super(driver, timeoutInSeconds, impcImageMap);

        switch(getCurrentView()) {
            case ANNOTATION_VIEW:
                searchImpcImageAnnotationView = new SearchImpcImageAnnotationView(driver, timeoutInSeconds, impcImageMap);
                break;

            case IMAGE_VIEW:
                searchImpcImageImageView = new SearchImpcImageImageView(driver, timeoutInSeconds, impcImageMap);
                break;
        }
    }

    public final ImagesView getCurrentView() {
        String imgViewSwitcherText = driver.findElement(By.cssSelector("span#imgViewSwitcher")).getText();
        return (imgViewSwitcherText.equals(SHOW_IMAGE_VIEW) ? ImagesView.ANNOTATION_VIEW : ImagesView.IMAGE_VIEW);
    }

//    public void setCurrentView(ImagesView view) throws TestException {
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
//    }

    /**
     * This method is meant to be called after any change to the image table,
     * such as changing between annotation and image view, or changing
     * pagination pages. It is required to keep the image table internals in
     * sync with what is seen on the page.
     */
    public void updateImageTableAfterChange() throws TestException {
        switch (getCurrentView()) {
            case ANNOTATION_VIEW:
                searchImpcImageAnnotationView = new SearchImpcImageAnnotationView(driver, timeoutInSeconds, impcImageMap);
                searchImpcImageImageView = null;
                break;

            case IMAGE_VIEW:
                searchImpcImageAnnotationView = null;
                searchImpcImageImageView = new SearchImpcImageImageView(driver, timeoutInSeconds, impcImageMap);
                break;
        }

        setTable(driver.findElement(impcImageMap.get(TableComponent.BY_TABLE)));
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
                status = searchImpcImageAnnotationView.validateDownload(data, downloadType);
                break;

            case IMAGE_VIEW:
                status = searchImpcImageImageView.validateDownload(data, downloadType);
                break;
        }

        return status;
    }
}