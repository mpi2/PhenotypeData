/*
 * Copyright © 2015 EMBL - European Bioinformatics Institute
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
 */

package org.mousephenotype.cda.dao;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.TestConfig;
import org.mousephenotype.cda.pojo.BiologicalModel;
import org.mousephenotype.cda.pojo.Datasource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.constraints.NotNull;


/**
 * Created by jmason on 16/06/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestConfig.class})
public class BiologicalModelDAOTest extends TestCase {

	@NotNull
	@Autowired
	BiologicalModelDAO bmDao;

	@Before
	public void before() {
		BiologicalModel bm = new BiologicalModel();
		bm.setGeneticBackground("involves: C57BL/6");
		bm.setAllelicComposition("");
		bm.setZygosity("homozygote");
		bm.setDatasource(new Datasource());

	}

	@Test
	public void testGetAllBiologicalModels() throws Exception {
		BiologicalModel bm=null;

		bm = bmDao.findByDbidAndAllelicCompositionAndGeneticBackgroundAndZygosity(12, "", "involves: C57BL/6", "homozygote");
		assert(bm!=null);


	}
}
