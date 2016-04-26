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

package org.mousephenotype.cda.loads.cdaloader.step;

import org.mousephenotype.cda.db.pojo.DatasourceEntityId;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.PassThroughFieldSetMapper;
import org.springframework.batch.item.file.transform.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mrelac on 26/04/16.
 */
@Configuration
@ComponentScan("org.mousephenotype.cda.loads.cdaloader")
@PropertySource(value="file:${user.home}/configfiles/${profile}/application.properties")
@PropertySource(value="file:${user.home}/configfiles/${profile}/cdaload.properties",
                ignoreResourceNotFound=true)
public class LoadOntologiesStep implements ItemReader<OntologyTerm>, ItemWriter<OntologyTerm> {

    CommonUtils commonUtils = new CommonUtils();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${cdaload.workspace}")
    private String cdaWorkspace;

//    @Autowired
//    DataSource komp2loads;
//
//    @Value("${cdaload.dbname}")
//    private String dbname;
//
//    @Value("${cdaload.username}")
//    private String dbusername;
//
//    @Value("${cdaload.password}")
//    private String dbpassword;
//
//    @Value("${cdaload.mysql}")
//    private String mysql;
//
//    @Value("${cdaload.dbhostname}")
//    private String dbhostname;
//
//    @Value("${cdaload.dbport}")
//    private String dbport;
//
//    public final long TASKLET_TIMEOUT = 10000;                                  // Timeout in milliseconds

    @Autowired
    DelimitedLineTokenizer delimitedLineTokenizer;

    @Bean(name = "ontologyReader")
    @StepScope
    public FlatFileItemReader ontologyReader() {
        FlatFileItemReader<FieldSet> reader = new FlatFileItemReader<>();

        String sourcePath = cdaWorkspace + "/EMAP.obo";
System.out.println("sourcePath: '" + sourcePath + "'");

        reader.setResource(new FileSystemResource(sourcePath));
        reader.setLineMapper(new DefaultLineMapper<FieldSet>() {{
            setLineTokenizer(new PatternMatchingCompositeLineTokenizer() {{
                setTokenizers(
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
            }});
            setFieldSetMapper(new PassThroughFieldSetMapper());
        }});
        return reader;
    }

    @Bean(name = "ontologyWriter")
    @StepScope
    public FlatFileItemWriter ontologyWriter() {
        FlatFileItemWriter<FieldSet> writer = new FlatFileItemWriter<>();
        writer.setLineAggregator(new PassThroughLineAggregator());
        String sourcePath = cdaWorkspace + "/loadOntologies.log";
        writer.setResource(new FileSystemResource(sourcePath));

        return writer;
    }

    @Autowired
    FlatFileItemReader delegate;

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

    @Bean
    public DelimitedLineTokenizer delimitedLineTokenizer() {
        return new DelimitedLineTokenizer();
    }

    @Override
    public void write(List<? extends OntologyTerm> list) throws Exception {

    }
}