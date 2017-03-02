/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
 * <p>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.cda.selenium.support;

import org.mousephenotype.cda.utilities.CommonUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * This class encapsulates the code and data required to represent access to the HTML pagination widget found at the
 * bottom of paginated HTML tables.
 *
 * Created by mrelac on 16/09/2015.
 *
 * Assumptions:
 *  id is a valid div containing the class 'pagination'.
 *
 */
public class Paginator {
    private final String id;            // paginator HTML id
    protected WebDriver driver;
    private CommonUtils commonUtils = new CommonUtils();
    private WebElement paginatorElement;

    /**
     * Creates a new <code>Paginator</code> instance using <code>id</code> and <code>driver</code>.
     *
     * @param id HTML id of the div containing the 'pagination' class
     * @param driver this page's <code>WebDriver</code> instance
     */
    public Paginator(String id, WebDriver driver) {
        this.id = id;
        this.driver = driver;
        this.paginatorElement = getPaginatorElement();
    }


    // PRIVATE METHODS


    private void dumpElement(WebElement element) {
        System.out.println("\n      id = " + element.getAttribute("id"));
        System.out.println("tag name = " + element.getTagName());
        System.out.println("   class = " + element.getAttribute("class"));
        System.out.println("    text = " + element.getText());
    }

    private WebElement getPaginatorElement() {
        WebElement element = null;

        List<WebElement> elements = driver.findElements(By.xpath(("//div[@id = '" + id + "']")));
        if ( ! elements.isEmpty()) {
            element = elements.get(0);
        }

        return element;
    }

    /**
     * @return the currently active page element.
     */
    public WebElement getActivePageElement() {
        WebElement element = getPaginatorElement();

        List<WebElement> elements = element.findElements(By.xpath("./ul/li[@class='active']"));

        return (elements.isEmpty() ? null : elements.get(0));
    }

    /**
     *
     * @return true if the page after the active page is enabled; false otherwise.
     */
    public boolean hasNext() {
        boolean retVal = false;

        WebElement activeElement = getActivePageElement();
        List<WebElement> elements = activeElement.findElements(By.xpath("./following-sibling::li[1]"));
        if ( ! elements.isEmpty()) {
            String s = elements.get(0).getAttribute("class");
            if ( ! s.contains("disabled")) {
                retVal = true;
            }
        }

        return retVal;
    }

    /**
     * Returns true if <code>element</code> is the active element.
     *
     * @param element The element to interrogate
     *
     * @return true if <code>element</code> is the active element.
     */
    public boolean isActive(WebElement element) {
        return  ! element.getAttribute("class").contains("disabled");
    }

    /**
     * Returns the next web element if there is one; null otherwise.
     *
     * @return the next web element if there is one; null otherwise.
     */
    public WebElement next() {
        WebElement activeElement = getActivePageElement();
        List<WebElement> elements = activeElement.findElements(By.xpath("./following-sibling::li[1]"));
        return (elements.isEmpty() ? null : elements.get(0));
    }

    /**
     * If the next page is active, clicks that page; otherwise, does nothing.
     */
    public void clickNext() {
        WebElement next = next();

        if (isActive(next)) {
            next.click();
        }
    }
}