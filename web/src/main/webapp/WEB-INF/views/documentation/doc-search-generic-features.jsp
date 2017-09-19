<%--
  Created by IntelliJ IDEA.
  User: ckc
  Date: 20/01/2016
  Time: 16:23
  To change this template use File | Settings | File Templates.
--%>

<html>
<head>
    <title></title>
</head>
<body>

<p>
This page is separated into different subsections which are highlighted on the screenshot below.
<ul class="subUl">
    <li>Search box (see below for more information)</li>
    <li>Datatype subsection<br>
   Currently, the IMPC portal supports 6 main datatypes on the search page: Genes, Phenotypes, Diseases, Anatomy, Impc Images, and legacy Images.
Each main datatype has filters to allow for data filtering. The selected one is highlighted in dark blue. The ones w/o results will be grayed out.</li>
    <li>Filter subsection<br>
The user is in control of whether a filter is open or closed by ticking on it.
Filter options will change according to datatypes and will dynamically count data corresponding to user's filtering choice. The counts of non-matching filters will become zero and disabled (grayed out).
    </li>
    <li>Results subsection</li>
</ul>
    <p class="intraSectBr"></p>
      <img src='img/search-main-data-type.png' />

<p class="intraSectBr"></p>
      <h4 id="autosuggest_srch">Search box: more information on Auto-suggest support</h4>
      <p>Enter at least 3 letters to invoke a drop-down list of terms related to your search keyword. The results will be prefixed by a datatype which are described in the table below. The top ten most relevant terms will be shown, in order of relevance scores. You can select the desired term by using the UP/DOWN keys or by selecting the term with the mouse and pressing the ENTER key.
      <p class="intraSectBr"></p>

      <img src='img/search-autosuggest.png' />
      <p class="intraSectBr"></p>

      <h4>Keyword search and datatype</h4><p class="intraSectBr"></p>
      When a main datatype is chosen (Genes, Phenotypes, Diseases, Anatomy), typing a keyword in the main input box and hit ENTER w/o choosing an item from the dropdown list will show the result with the same main datatype selected.
      However, when you specifically choose an item from the dropdown list, the selected datatype will be the one associated with the selected item.
    <p class="intraSectBr"></p>

      The table shows the possible datatypes currently searchable.

      <table>
        <tr>
          <th>Datatype</th>
          <th>Example of value</th>
          <th>Source</th>
          <th>Method</th>
        </tr>
        <tr>
          <td>gene</td>
          <td>Akt2</td>
          <td><a href="http://www.informatics.jax.org">MGI</a></td>
          <td>-</td>
        </tr>
        <tr>
          <td>mp</td>
          <td>abnormal IgM level</td>
          <td><a href="http://www.obofoundry.org/ontology/mp.html">Mammalian phenotype </a></td>
          <td>Mouse phenotypes in IMPC are assayed using <a href="http://www.mousephenotype.org/impress">IMPReSS</a> phenotyping protocols</td>
        </tr>
        <tr>
          <td>ma</td>
          <td>brain</td>
          <td><a href="http://www.obofoundry.org/ontology/ma.html">Mouse adult gross anatomy </a></td>
          <td>Mouse anatomy terms in IMPC are associated with tissues/organs assessed according to <a href="http://www.mousephenotype.org/impress">IMPReSS</a> phenotyping protocols</td>
        </tr>
        <tr>
          <td>disease (human disease)</td>
          <td>Bloom Syndrome</td>
          <td>OMIM/ORPHANET/DECIPHER</a></td>
          <td>Human to mouse disease mapping is done using <a href="http://www.sanger.ac.uk/resources/databases/phenodigm/">Phenodigm</a></td>
        </tr>
        <tr>
          <td>hp</td>
          <td>Glucose intolerance &raquo; MP:0005293 - impaired glucose tolerance</td>
          <td><a href="http://www.obofoundry.org/ontology/hp.html">Human phenotype ontology</a></td>
          <td>Human (hp) to mouse (mp) phenotypes mapping is done using <a href="http://www.sanger.ac.uk/resources/databases/phenodigm/">Phenodigm</a></td>
        </tr>

      </table>
      <p class="intraSectBr"></p>
      <h4 id="export">Data Export of Search Results</h4>
      <p>Click on one of the the Export icons for data format of your choice:
        <br><img src='img/search-download.png' /><br>
        at the bottom-right corner of the results grid.
        <%--<p>To download larger dataset of your favorite gene/phenotype/disease list, click on the<br><img src='img/search-batch1.png' /><br>link on top of the search page.--%>


      </p><%-- end of content div--%>

</body>
</html>
