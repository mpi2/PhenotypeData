/*******************************************************************************
 * Copyright © 2017 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.selenium.support;

import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Facet {

    public WebDriver driver;
    public WebElement element;
    public String coreName;
    public String tabName;
    public String tabId;
    public Integer count;

    public CommonUtils commonUtils = new CommonUtils();

    public Facet() {
    }

    public Facet(WebDriver driver, String coreName, String tabName, String tabId) {
        this.driver = driver;
        this.coreName = coreName;
        this.tabName = tabName;
        this.tabId = tabId;
    }

    public WebDriver getDriver() {
        return driver;
    }

    // NOTE: element is guaranteed to be not null. Upon first invocation the value is captured and saved for future requests.
    public WebElement getElement() {
        if (element == null) {
            element = driver.findElement(By.xpath("//*/li[@id='" + tabId + "']"));
        }
        return element;
    }

    public String getCoreName() {
        return coreName;
    }

    public void setCoreName(String coreName) {
        this.coreName = coreName;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public int getCount() {
        if (count == null) {
            String label = getElement().getText();
            String[] labelParts = StringUtils.split(label, " (");
            count = commonUtils.tryParseInt(labelParts[1].replace(")", ""));
        }

        return count.intValue();
    }

    @Override
    public String toString() {
        return "Facet{" +
                "element=" + getElement() +
                ", coreName='" + coreName + '\'' +
                ", tabName='" + tabName + '\'' +
                ", tabId='" + tabId + '\'' +
                ", count=" + getCount() +
                '}';
    }
}
