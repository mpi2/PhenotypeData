/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/

package org.mousephenotype.cda.web;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * This class encapsulates the time series parameters used by the web and tests modules.
 *
 * Created by mrelac on 14/07/2015.
 */
@Component
public class TimeSeriesConstants {
    public static final List<String> ESLIM_702 = Arrays.asList("ESLIM_009_001_003", "ESLIM_010_001_003", "ESLIM_011_001_011", "ESLIM_012_001_005", "ESLIM_013_001_018", "ESLIM_022_001_001");
    public static final List<String> ESLIM_701 = Arrays.asList("ESLIM_001_001_001", "ESLIM_002_001_001", "ESLIM_003_001_001", "ESLIM_004_001_001", "ESLIM_005_001_001", "ESLIM_020_001_001", "ESLIM_022_001_001");
    public static final List<String> IMPC_BWT = Arrays.asList("IMPC_GRS_003_001", "IMPC_CAL_001_001", "IMPC_DXA_001_001", "IMPC_HWT_007_001", "IMPC_PAT_049_001", "IMPC_BWT_001_001", "IMPC_ABR_001_001", "IMPC_CHL_001_001", "TCP_CHL_001_001", "HMGU_ROT_004_001");
    public static final List<String> IMPC_IPG_002_001 = Arrays.asList("IMPC_IPG_012_001", "IMPC_IPG_011_001", "IMPC_IPG_010_001");
}
