/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.loads.cdaloader;

import org.mousephenotype.cda.db.pojo.DatasourceEntityId;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.PassThroughFieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.file.transform.PatternMatchingCompositeLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.Assert;

import java.util.HashMap;

/**
 * Created by mrelac on 28/04/16.
 */
//@Configuration
//@ComponentScan("org.mousephenotype.cda.loads.cdaloader")
//@PropertySource(value="file:${user.home}/configfiles/${profile}/application.properties")
//@PropertySource(value="file:${user.home}/configfiles/${profile}/cdaload.properties",
//                ignoreResourceNotFound=true)
public class OboItemReader implements ItemReader<OntologyTerm> {

//    @Value("${cdaload.workspace}")
//    private String cdaWorkspace;

    @Autowired
    DelimitedLineTokenizer delimitedLineTokenizer;


    @Bean(name = "oboReader")
    public FlatFileItemReader<OntologyTerm> oboReader() {

        String sourcePath = "/tmp/EMAP.obo";
        System.out.println("sourcePath: '" + sourcePath + "'");

        PatternMatchingCompositeLineTokenizer tokenizer = new PatternMatchingCompositeLineTokenizer();
        tokenizer.setTokenizers(
            new HashMap<String, LineTokenizer>() {{
                put("[Term]", delimitedLineTokenizer);
                put("[Typedef]", delimitedLineTokenizer);

                put("comment: *", delimitedLineTokenizer);
                put("def: *", delimitedLineTokenizer);
                put("domain: *", delimitedLineTokenizer);
                put("id: *", delimitedLineTokenizer);
                put("name: *", delimitedLineTokenizer);
                put("range: *", delimitedLineTokenizer);
                put("relationship: *", delimitedLineTokenizer);
                put("synonym: *", delimitedLineTokenizer);
                put("*", delimitedLineTokenizer);
            }});

        DefaultLineMapper mapper = new DefaultLineMapper();
        mapper.setFieldSetMapper(new PassThroughFieldSetMapper());
        mapper.setLineTokenizer(tokenizer);

        FlatFileItemReader<OntologyTerm> reader = new FlatFileItemReader<>();
        reader.setLineMapper(mapper);
        reader.setResource(new FileSystemResource(sourcePath));

        return reader;
    }


    private FlatFileItemReader delegate;

    @Override
    public OntologyTerm read() throws Exception {
        OntologyTerm term = null;

        for (FieldSet line; (line = (FieldSet)delegate.read()) != null;) {
            String prefix = line.readString(0);

            switch (prefix.trim()) {
                case "[Term]":
                    term = new OntologyTerm();
                    break;

                case "id:":
                    Assert.notNull(term, "Header is missing!");
                    term.setId(new DatasourceEntityId(line.readString(0), -1));
                    break;

                case "name:":
                    Assert.notNull(term, "Header is missing!");
                    term.setName(line.readString(0));
                    break;

                case "relationship:":
                    Assert.notNull(term, "Header is missing!");
                    break;

                default:
                    throw new Exception("Unknown ontology term '" + prefix.trim() + "'");
            }
        }

        return term;
    }
}