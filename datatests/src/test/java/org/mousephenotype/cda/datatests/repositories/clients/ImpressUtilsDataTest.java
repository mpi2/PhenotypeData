/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
/**
 * Copyright © 2014 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This test class is intended to run healthchecks against the observation table.
 */

package org.mousephenotype.cda.datatests.repositories.clients;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.pojo.Parameter;
import org.mousephenotype.cda.db.repositories.ParameterRepository;
import org.mousephenotype.cda.db.utilities.ImpressUtils;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.constraints.NotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ImpressUtilsDataTestConfig.class})
public class ImpressUtilsDataTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @NotNull @Autowired
    private ParameterRepository parameterRepository;

    @NotNull @Autowired
    private ImpressUtils impressUtils;


    @Test
    public void testCheckTypeParameterString() {
        Parameter       p     = parameterRepository.getFirstByStableId("ESLIM_003_001_006");
        String          value = "2.092";
        ObservationType oType = ImpressUtils.checkType(p, value);

        logger.debug("oType = {}", oType.toString());
        assert(oType.equals(ObservationType.time_series));
    }
}