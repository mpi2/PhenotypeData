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

package org.mousephenotype.cda.loads.legacy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.util.Arrays;

/**
 * This class encapsulates the code and data necessary to transform an IMSR input record to an IMSR output record.
 *
 * Created by mrelac on 26/06/2015.
 */
public class ImsrProcessor implements ItemProcessor<InputImsrDto, ImsrDto> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ImsrDto process(final InputImsrDto inputImsr) throws Exception {
        ImsrDto imsr = new ImsrDto();
        imsr.setAlleleId(inputImsr.getAlleleId());
        imsr.setAlleleName(inputImsr.getAlleleName());
        imsr.setAlleleSymbol(inputImsr.getAlleleSymbol());
        imsr.setGeneId(inputImsr.getGeneId());
        imsr.setGeneName(inputImsr.getGeneName());
        imsr.setGeneSymbol(inputImsr.getGeneSymbol());
        imsr.setNomenclature(inputImsr.getNomenclature());
        imsr.setRepository(inputImsr.getRepository());
        imsr.setState(inputImsr.getState());
        imsr.setStrainId(inputImsr.getStrainId());
        imsr.setStrainStock(inputImsr.getStrainStock());
        if ((inputImsr.getSynonyms() != null) && ( ! inputImsr.getSynonyms().trim().isEmpty())) {
            String[] synonymsArray = inputImsr.getSynonyms().split(",");
            imsr.setSynonyms(Arrays.asList(synonymsArray));
        }
        imsr.setType(inputImsr.getType());

//        logger.info("Converting (" + inputImsr + ") into (" + imsr + ")");

        return imsr;
    }
}
