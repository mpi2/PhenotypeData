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

package org.mousephenotype.cda.datatests.repositories;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.pojo.BiologicalModel;
import org.mousephenotype.cda.db.repositories.BiologicalModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.constraints.NotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RepositoryTestConfig.class})
public class BiologicalModelRepositoryTest {

	@NotNull @Autowired
	private BiologicalModelRepository biologicalModelRepository;

	@Test
	public void testGetAllBiologicalModels() {
		BiologicalModel bm;

		bm = biologicalModelRepository.getBiologicalModelByDatasource_IdAndAllelicCompositionAndGeneticBackgroundAndZygosity(12L, "", "involves: C57BL/6", "homozygote");
		assert(bm != null);
	}
}