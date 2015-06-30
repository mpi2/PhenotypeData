/*******************************************************************************
 *  Copyright (c) 2013 - 2015 EMBL - European Bioinformatics Institute
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *  either express or implied. See the License for the specific
 *  language governing permissions and limitations under the
 *  License.
 ******************************************************************************/

package org.mousephenotype.cda.seleniumtests.tests;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class encapsulates the code and data necessary for selenium functionality used in most, if not all, tests.
 *
 * Created by mrelac on 26/06/2015.
 */
@Component
public class SeleniumWrapper {

    @NotNull
    @Value("${seleniumUrl}")
    private String seleniumUrl;

    @NotNull
    @Value("${browserName}")
    private String browserName;

    public String getSeleniumUrl() {
        return seleniumUrl;
    }

    public String getBrowserName() {
        return browserName;
    }

    public RemoteWebDriver getDriver() throws MalformedURLException {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();

        desiredCapabilities.setBrowserName(browserName);
        return new RemoteWebDriver(new URL(seleniumUrl), desiredCapabilities);
    }
}