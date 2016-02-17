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

  <h3>Explore the diverse entry points to mouse phenotype data.</h3><br>


      Currently, the IMPC portal supports 6 main datatypes on the search page:
      Genes, Phenotypes, Diseases, Anatomy, Impc Images, and legacy Images.<br>
      Each main datatype has filters to allow for data filtering.<p>
      <img src='img/search-main-data-type.png' /><p>

      <div>Clicking on a main datatype tab under the main input box on top of the search page (eg, Genes, Phenotypes, Diseases) without ticking its filter(s) (in left side panel) will display all records of that datatype.
        The screenshot above shows the total number of genes in the portal.<p><p></p>

        Depending on the datatype, the filters will be changed dynamically.<br><br>

        <h6>Filter control and behavior</h6>
        <div>You are in control of whether a filter is open or closed by ticking on it.<p>
          When a filter is checked (ie, ticking on the checkbox), the result counts will change dynamically. The counts of non-matching filters will become zero and disabled (grayed out) and when you hover over them, a no-entry sign will appear indicating that that filter is now unclickable until it matches other filter combinations.
        </div>

      </div><br>

      <h6 id="autosuggest_srch">Auto-suggest Support for Keyword Search</h6>
      <div>Enter at least 3 letters to invoke a drop-down list of terms (prefixed by datatype) related to your search keyword.
        The top ten most relevant terms will be shown, in order of relevance scores.
        You can select the desired term by using the UP/DOWN keys or by selecting the term with the mouse and pressing the ENTER key.<p></p><p></p>

        <img src='img/search-autosuggest.png' /><p>

          <b>Keyword search and datatype</b><br>
          When a datatype is chosen, typing a keyword in the main input box and hit ENTER w/o choosing an item from the dropdown list will show the result with the same main datatype selected.<br>
          However, when you specifically choose an item from the dropdown list, the selected datatype will be the one associated with the selected item.<br>

      </div><br>
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
          <td>MPs in IMPC are associated with phenotypes assessed using <a href="http://www.mousephenotype.org/impress">IMPReSS</a> phenotyping protocols</td>
        </tr>
        <tr>
          <td>ma</td>
          <td>brain</td>
          <td><a href="http://www.obofoundry.org/ontology/ma.html">Mouse adult gross anatomy </a></td>
          <td>MAs in IMPC are associated with tissues/organs assessed using <a href="http://www.mousephenotype.org/impress">IMPReSS</a> phenotyping protocols</td>
        </tr>
        <tr>
          <td>disease (human disease)</td>
          <td>Bloom Syndrome</td>
          <td>OMIM/ORPHANET/DECIPHER</a></td>
          <td>Human to mouse disease mapping using <a href="http://www.sanger.ac.uk/resources/databases/phenodigm/">Phenodigm</a></td>
        </tr>
        <tr>
          <td>hp</td>
          <td>Glucose intolerance » MP:0005293 - impaired glucose tolerance</td>
          <td><a href="http://www.obofoundry.org/ontology/hp.html">Human phenotype ontology</a></td>
          <td>Hp to Mp mapping using <a href="http://www.sanger.ac.uk/resources/databases/phenodigm/">Phenodigm</a></td>
        </tr>

      </table>

      <h6 id="export">Data Export of Search Results</h6>
      <div>Click on the download icon <p>
        <img src='img/search-download.png' /><br>
        in the top-right corner of the results grid to expand or hide it.
        When expanded, it looks like this:<p> <img src='img/search-export-expanded.png' /><p><p>
        Click on either the TSV (tab separated) or the XLS (MS Excel) link for the desired report format.
        <p>To download data for the currently displayed page only, choose the set of links under the label "Current paginated entries in table".
          To download larger dataset, click on the "Batch query" link.</p>

      </div><%-- end of content div--%>

</body>
</html>
