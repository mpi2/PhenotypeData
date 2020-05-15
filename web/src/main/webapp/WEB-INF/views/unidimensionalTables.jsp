<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<c:if test="${unidimensionalChartDataSet!=null}">
    <c:if test="${fn:length(unidimensionalChartDataSet.statsObjects)>1}">

        <c:set var="data" value="${unidimensionalChartDataSet.statsObjects[1]}"></c:set>

        <c:if test="${data.result.status ne 'Success'}">
            <div class="alert">
                <strong>Statistics ${data.result.status}</strong>
            </div>
        </c:if>

        <%-- Display result of a mixed model calculation --%>
        <!-- Statistical Result docId: ${data.result.dbId} -->

        <div class="row">
            <div class="col-md-12">

                <div class="row">
                    <div class="col-md-6">

                <c:if test="${data.result.statisticalMethod!=null and data.result.statisticalMethod!='Wilcoxon rank sum test with continuity correction' and data.result.statisticalMethod!='Reference Ranges Plus framework'}">

                    <c:if test="${data.result.blupsTest!=null or data.result.interceptEstimate!=null or data.result.varianceSignificance!=null}">


                            <h4> Results of statistical analysis  </h4>

                            <dl class="alert alert-success">
                                <dt>P value</dt>
                                <dd><t:formatScientific>${data.result.nullTestPValue}</t:formatScientific></dd>

                                <dt>Classification</dt>
                                <dd>${data.result.significantType.text}</dd>

                            </dl>

                            <table class="table table-striped small">
                                <c:choose>
                                <c:when
                                        test="${data.result.significantType.text == 'If phenotype is significant it is for the one sex tested' || data.result.significantType.text == 'Both genders equally' || data.result.significantType.text == 'No significant change'  || data.result.significantType.text == 'Can not differentiate genders' }">
                                <thead>
                                <tr>
                                    <th>Genotype effect P Value</th>
                                    <th>Effect size</th>
                                    <th>Standard Error</th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr>
                                    <td class="pvalue"><t:formatScientific>${data.result.genotypeEffectPValue }</t:formatScientific></td>
                                    <td class="effect"><t:formatScientific>${data.result.genotypeParameterEstimate}</t:formatScientific></td>
                                    <td>
                                        <c:if test="${data.result.genotypeStandardErrorEstimate!=null}">
                                            &#177;
                                        </c:if>
                                        <t:formatScientific>${data.result.genotypeStandardErrorEstimate}</t:formatScientific></td>
                                </tr>
                                </c:when>
                                <c:when
                                        test="${data.result.significantType.text == 'Female only' || data.result.significantType.text == 'Male only'  || data.result.significantType.text == 'Different effect size, females greater' || data.result.significantType.text == 'Different effect size, males greater' || data.result.significantType.text == 'Female and male different directions'}">
                                <thead>
                                <tr>
                                    <th>Sex</th>
                                    <th>Sex*Genotype P Value</th>
                                    <th>Effect size</th>
                                    <th>Standard Error</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:if test="${data.result.genderFemaleKoPValue!=null || data.result.genderFemaleKoEstimate!=null || data.result.genderFemaleKoStandardErrorEstimate!=null}">
                                    <tr>
                                        <td>Female</td>
                                        <c:if test="${data.result.genderFemaleKoPValue!=null}">
                                            <td class="pvalue"><t:formatScientific>${data.result.genderFemaleKoPValue }</t:formatScientific></td>
                                        </c:if>
                                        <td class="effect"><t:formatScientific>${data.result.genderFemaleKoEstimate}</t:formatScientific></td>
                                        <c:if
                                                test="${data.result.genderFemaleKoStandardErrorEstimate!=null}">
                                            <td>&#177;<t:formatScientific>${data.result.genderFemaleKoStandardErrorEstimate }</t:formatScientific></td>
                                        </c:if>
                                    </tr>
                                </c:if>
                                <c:if test="${data.result.genderMaleKoPValue!=null || data.result.genderMaleKoEstimate != null || data.result.genderMaleKoStandardErrorEstimate!=null}">
                                    <tr>
                                        <td>Male</td>
                                        <c:if test="${data.result.genderMaleKoPValue!=null}">
                                            <td class="pvalue"><t:formatScientific>${data.result.genderMaleKoPValue }</t:formatScientific></td>
                                        </c:if>
                                        <td class="effect"><t:formatScientific>${data.result.genderMaleKoEstimate}</t:formatScientific></td>
                                        <c:if
                                                test="${data.result.genderMaleKoStandardErrorEstimate!=null}">
                                            <td>&#177;<t:formatScientific>${data.result.genderMaleKoStandardErrorEstimate }</t:formatScientific></td>
                                        </c:if>
                                    </tr>
                                </c:if>
                                </c:when>
                                </c:choose>
                                </tbody>
                            </table>

                    </c:if>

                </c:if>

                <%-- Display result of a wilcoxon calculation --%>

                <c:if test="${data.result.statisticalMethod!=null and data.result.statisticalMethod=='Wilcoxon rank sum test with continuity correction'}">
                    <h4> Results of statistical analysis  </h4>
                    <table class="table table-striped small">
                        <thead>
                            <tr>
                                <th>Sex</th>
                                <th>P Value</th>
                                <th>Effect size</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:if test="${data.result.genderFemaleKoPValue != null}">
                                <tr class="toggle_table_covariate_details">
                                    <td>Female</td>
                                    <c:if test="${data.result.genderFemaleKoPValue!=null}">
                                        <td class="pvalue"><t:formatScientific>${data.result.genderFemaleKoPValue }</t:formatScientific></td>
                                    </c:if>
                                    <td class="effect"><t:formatScientific>${data.result.genderFemaleKoEstimate}</t:formatScientific></td>
                                </tr>
                            </c:if>
                            <c:if test="${data.result.genderMaleKoPValue!=null}">
                                <tr>
                                    <td>Male</td>
                                    <c:if test="${data.result.genderMaleKoPValue!=null}">
                                        <td class="pvalue"><t:formatScientific>${data.result.genderMaleKoPValue }</t:formatScientific></td>
                                    </c:if>
                                    <td class="effect"><t:formatScientific>${data.result.genderMaleKoEstimate}</t:formatScientific></td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </c:if>

                <%-- Display result of a reference range plus calculation --%>
                <c:if test="${data.result.statisticalMethod!=null and data.result.statisticalMethod=='Reference Ranges Plus framework'}">

                    <h4> Results of statistical analysis  </h4>
                    <table class="table table-striped small">
                        <thead>
                        <tr>
                            <th>Sex</th>
                            <th>Decreased P Value</th>
                            <th>Decreased Effect Size</th>
                            <th>Increased P Value</th>
                            <th>Increased Effect Size</th>
                        </tr>
                        </thead>

                        <tbody>
                        <c:if test="${data.result.femalePvalueLowVsNormalHigh!=null or data.result.femalePvalueLowNormalVsHigh!=null}">

                        <tr>
                            <td>Females</td>
                            <td><t:formatScientific>${data.result.femalePvalueLowVsNormalHigh }</t:formatScientific></td>
                            <td>${data.result.femaleEffectSizeLowVsNormalHigh}<c:if test="${data.result.femaleEffectSizeLowVsNormalHigh!=null}">%</c:if></td>
                            <td><t:formatScientific>${data.result.femalePvalueLowNormalVsHigh }</t:formatScientific></td>
                            <td>${data.result.femaleEffectSizeLowNormalVsHigh}<c:if test="${data.result.femaleEffectSizeLowNormalVsHigh!=null}">%</c:if></td>
                        </tr>

                        </c:if>
                        <c:if test="${data.result.malePvalueLowVsNormalHigh!=null or data.result.malePvalueLowNormalVsHigh!=null}">

                        <tr>
                            <td>Males</td>
                            <td><t:formatScientific>${data.result.malePvalueLowVsNormalHigh }</t:formatScientific></td>
                            <td>${data.result.maleEffectSizeLowVsNormalHigh}<c:if test="${data.result.maleEffectSizeLowVsNormalHigh!=null}">%</c:if></td>
                            <td><t:formatScientific>${data.result.malePvalueLowNormalVsHigh }</t:formatScientific></td>
                            <td>${data.result.maleEffectSizeLowNormalVsHigh}<c:if test="${data.result.maleEffectSizeLowNormalVsHigh!=null}">%</c:if></td>
                        </tr>
                        </c:if>

                        <tr>
                            <td>Both</td>
                            <td><t:formatScientific>${data.result.genotypePvalueLowVsNormalHigh }</t:formatScientific></td>
                            <td>${data.result.genotypeEffectSizeLowVsNormalHigh}%</td>
                            <td><t:formatScientific>${data.result.genotypePvalueLowNormalVsHigh }</t:formatScientific></td>
                            <td>${data.result.genotypeEffectSizeLowNormalVsHigh}%</td>
                        </tr>

                        </tbody>
                    </table>
                </c:if>

                </div>

                    <div class="col-md-6">

                    <h4> Summary statistics of the dataset  </h4>
                <%-- always print the summary statistics table --%>
                <table class="table table-striped small">
                    <thead>
                    <tr>
                        <th></th>
                        <th>Mean</th>
                        <th>Stddev</th>
                        <th># Samples</th>
                    </tr>
                    </thead>
                    <tbody>


                    <c:forEach var="statsObject"
                               items="${unidimensionalChartDataSet.statsObjects}">
                        <tr>
                            <td><c:choose>
                                <c:when test="${statsObject.sexType eq 'female'}">
                                    Female
                                </c:when>
                                <c:when test="${statsObject.sexType eq 'male'}">
                                    Male
                                </c:when>
                            </c:choose> <c:choose>
                                <c:when test="${statsObject.line =='Control' }">
                                    Control
                                </c:when>
                                <c:when test="${statsObject.line !='Control' }">
                                    ${statsObject.zygosity}
                                </c:when>
                            </c:choose></td>
                            <td>${statsObject.mean}</td>
                            <td>${statsObject.sd}</td>
                            <c:if test="${statsObject.sexType eq 'female'}">
                                <td>${statsObject.sampleSize}</td>
                            </c:if>
                            <c:if test="${statsObject.sexType eq 'male'}">
                                <td>${statsObject.sampleSize}</td>
                            </c:if>

                        </tr>
                    </c:forEach>
                    </tbody>
                </table>

                </div>
                </div>

                <div class="row mt-5">
                    <div class="col-md-6">
                        <c:if test="${fn:length(unidimensionalChartDataSet.statsObjects)>1}">

                            <c:set var="data" value="${unidimensionalChartDataSet.statsObjects[1]}"></c:set>

                            <c:if test="${data.result.blupsTest!=null or data.result.interceptEstimate!=null or data.result.varianceSignificance!=null}">

                                <c:if test="${data.result.statisticalMethod!=null}">
                                    <h4> Statistical method  </h4>
                                    <p>${data.result.statisticalMethod}</p>
                                </c:if>

                                <c:if test="${data.result.colonyId!=null}"><!-- Colony Id: ${data.result.colonyId } --></c:if>
                                <table class="table table-striped small">
                                    <tr>
                                        <th>Model attribute</th>
                                        <th>Value</th>
                                    </tr>
                                        <%-- <c:if test="${data.result.experimentalZygosity!=null}"><tr><td>Experimental Zygosity</td><td>${data.result.experimentalZygosity}</td></tr></c:if> --%>
                                        <%-- <td>${data.result.mixedModel}</td> --%>
                                        <%-- <c:if test="${data.result.colonyId!=null}"><tr><td>Colony Id</td><td>${data.result.colonyId }</td></tr></c:if> --%>
                                        <%-- <c:if test="${data.result.dependantVariable!=null}"><tr><td>Dependant Variable</td><td>${data.result.dependantVariable}</td></tr></c:if> --%>
                                    <c:if test="${data.result.batchSignificant != null}">
                                        <tr>
                                            <td>Batch effect significant</td>
                                            <td>${data.result.batchSignificant }</td>
                                        </tr>
                                    </c:if>
                                    <c:if test="${data.result.varianceSignificant != null}">
                                        <tr>
                                            <td>Variance significant</td>
                                            <td>${data.result.varianceSignificant }</td>
                                        </tr>
                                    </c:if>
                                    <c:if test="${data.result.interactionEffectPValue!=null}">
                                        <tr>
                                            <td>Genotype*Sex interaction effect p value</td>
                                            <td><t:formatScientific>${data.result.interactionEffectPValue }</t:formatScientific></td>
                                        </tr>
                                    </c:if>
                                        <%-- <c:if test="${data.result.nullTestSignificance !=null}"><tr><td>Null Test Significance</td><td>${data.result.nullTestSignificance }</td></tr></c:if> --%>
                                    <c:if test="${data.result.genotypeEffectParameterEstimate!=null}">
                                        <tr>
                                            <td>Genotype parameter estimate</td>
                                            <td><t:formatScientific>${data.result.genotypeEffectParameterEstimate }</t:formatScientific></td>
                                        </tr>
                                    </c:if>
                                    <c:if test="${data.result.genotypeEffectStderrEstimate!=null}">
                                        <tr>
                                            <td>Genotype standard error estimate</td>
                                            <td><t:formatScientific>${data.result.genotypeEffectStderrEstimate }</t:formatScientific></td>
                                        </tr>
                                    </c:if>
                                    <c:if test="${data.result.genotypeEffectPValue!=null}">
                                        <tr>
                                            <td>Genotype Effect P Value</td>
                                            <td><t:formatScientific>${data.result.genotypeEffectPValue}</t:formatScientific></td>
                                        </tr>
                                    </c:if>

                                    <c:if test="${data.result.sexEffectParameterEstimate!=null}">
                                        <tr>
                                            <td>Sex Parameter Estimate</td>
                                            <td><t:formatScientific>${data.result.sexEffectParameterEstimate }</t:formatScientific></td>
                                        </tr>
                                    </c:if>
                                    <c:if test="${data.result.sexEffectStderrEstimate!=null}">
                                        <tr>
                                            <td>Sex Standard Error Estimate</td>
                                            <td><t:formatScientific>${data.result.sexEffectStderrEstimate }</t:formatScientific></td>
                                        </tr>
                                    </c:if>
                                    <c:if test="${data.result.sexEffectPValue!=null}">
                                        <tr>
                                            <td>Sex Effect P Value</td>
                                            <td><t:formatScientific>${data.result.sexEffectPValue}</t:formatScientific></td>
                                        </tr>
                                    </c:if>
                                    <c:if test="${data.result.interceptEstimate!=null}">
                                        <tr>
                                            <td>Intercept Estimate</td>
                                            <td><t:formatScientific>${data.result.interceptEstimate }</t:formatScientific></td>
                                        </tr>
                                    </c:if>
                                    <c:if test="${data.result.interceptEstimateStderrEstimate!=null}">
                                        <tr>
                                            <td>Intercept Estimate Standard Error</td>
                                            <td><t:formatScientific>${data.result.interceptEstimateStderrEstimate }</t:formatScientific></td>
                                        </tr>
                                    </c:if>
                                    <c:if test="${data.result.maleKoEffectPValue!=null}">
                                        <tr>
                                            <td>Sex Male KO P Value</td>
                                            <td><t:formatScientific>${data.result.maleKoEffectPValue }</t:formatScientific></td>
                                        </tr>
                                    </c:if>
                                    <c:if test="${data.result.femaleKoEffectPValue!=null}">
                                        <tr>
                                            <td>Sex Female KO P Value</td>
                                            <td><t:formatScientific>${data.result.femaleKoEffectPValue }</t:formatScientific></td>
                                        </tr>
                                    </c:if>
                                    <!-- 10-15 -->
                                    <c:if test="${data.result.weightEffectParameterEstimate!=null}">
                                        <tr>
                                            <td>Weight Parameter Estimate</td>
                                            <td><t:formatScientific>${data.result.weightEffectParameterEstimate }</t:formatScientific></td>
                                        </tr>
                                    </c:if>
                                    <c:if test="${data.result.weightEffectStderrEstimate!=null}">
                                        <tr>
                                            <td>Weight Standard Error Estimate</td>
                                            <td><t:formatScientific>${data.result.weightEffectStderrEstimate }</t:formatScientific></td>
                                        </tr>
                                    </c:if>
                                    <c:if test="${data.result.weightEffectPValue!=null}">
                                        <tr>
                                            <td>Weight Effect P Value</td>
                                            <td><t:formatScientific>${data.result.weightEffectPValue }</t:formatScientific></td>
                                        </tr>
                                    </c:if>
                                    <c:if test="${data.result.group1ResidualsNormalityTest!=null}">
                                        <tr>
                                            <td>WT Residuals Normality Tests</td>
                                            <td><t:formatScientific>${data.result.group1ResidualsNormalityTest }</t:formatScientific></td>
                                        </tr>
                                    </c:if>
                                    <c:if test="${data.result.group2ResidualsNormalityTest!=null}">
                                        <tr>
                                            <td>KO Residuals Normality Tests</td>
                                            <td><t:formatScientific>${data.result.group2ResidualsNormalityTest }</t:formatScientific></td>
                                        </tr>
                                    </c:if>
                                    <!-- relabel as KO residuals normality tests -->
                                    <c:if test="${data.result.blupsTest!=null}">
                                        <tr>
                                            <td>Blups Test</td>
                                            <td><t:formatScientific>${data.result.blupsTest }</t:formatScientific></td>
                                        </tr>
                                    </c:if>
                                    <c:if test="${data.result.rotatedResidualsTest !=null}">
                                        <tr>
                                            <td>Rotated Residuals Normality Test</td>
                                            <td><t:formatScientific>${data.result.rotatedResidualsTest }</t:formatScientific></td>
                                        </tr>
                                    </c:if>
                                </table>
                            </c:if>
                        </c:if>
                    </div>
                    <div class="col-md-6">
                        <h4> Access the results programmatically </h4>
                        <hr />
                        <p>
                            <a target="_blank" class="btn btn-outline-primary btn-sm" href='${srUrl}'> Statistical result raw XML </a>
                            <a target="_blank" class="btn btn-outline-primary btn-sm" href='${gpUrl}'> Genotype phenotype raw XML </a>
                            <a target="_blank" class="btn btn-outline-primary btn-sm" href='${baseUrl}${phenStatDataUrl}'> PhenStat-ready raw experiment data</a>
                        </p>
                    </div>
                </div>
            </div>


        </div>

    </c:if>

</c:if>
