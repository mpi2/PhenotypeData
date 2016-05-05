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

package org.mousephenotype.cda.loads.cdaloader.steps.itemwriters;

import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.core.io.FileSystemResource;

import java.util.List;

/**
 * Created by mrelac on 26/04/16.
 */
public class ResourceFileItemWriter implements ItemWriter<OntologyTerm> {

//    CommonUtils commonUtils = new CommonUtils();
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    @Value("${cdaload.workspace}")
//    private String cdaWorkspace;

//    @Autowired
//    @Qualifier("oboReaderBean")
//    public ItemReader<OntologyTerm> oboReader;

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

    public FlatFileItemWriter ontologyWriter() {
        FlatFileItemWriter<FieldSet> writer = new FlatFileItemWriter<>();
        writer.setLineAggregator(new PassThroughLineAggregator());
        String sourcePath = "/tmp/loadOntologies.log";
        writer.setResource(new FileSystemResource(sourcePath));

        return writer;
    }

    public DelimitedLineTokenizer delimitedLineTokenizer() {
        return new DelimitedLineTokenizer();
    }

    @Override
    public void write(List<? extends OntologyTerm> list) throws Exception {

    }
}