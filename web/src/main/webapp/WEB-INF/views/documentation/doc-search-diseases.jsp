<%--
  Created by IntelliJ IDEA.
  User: ckc
  Date: 20/01/2016
  Time: 16:23
  To change this template use File | Settings | File Templates.
--%>

<html>
<head>
  <style></style>
</head>
<body>
<%@ include file="doc-search-generic-features-note.jsp" %><br>
        <div>First, click on the "Diseases" datatype tab. It will show all the diseases that IMPC knows about if no filters are ticked on the left panel.<br>
          You could enter a human disease ID/name into the main input box and/or click on a filter on the left panel to narrow down your result to relevant disease(s).<br><br>
          <p>The screenshot below shows searching the human "cardiac" diseases:<br>
        </div><br>


        <img src='img/search-disease.png' /><p>

        The filters on the left are grayed out if they are not related to your search.<br><br>

        <b>Columns in the dataset on the right panel:</b>
        <table>
          <tr>
            <th>Column</th>
            <th>Explaination</th>
          </tr>
          <tr>
            <td>Disease</td>
            <td>This column shows the human disease name from OMIM/DECIPHER/ORPHANET with a linkout to the IMPC disease page.<br>
              In the disease page, mouse genes are mapped to this human disease which is based on the PhenoDigm phenotypic similarity tool using either Sanger-MGP or MGI data</td>
          </tr>
          <tr>
            <td>Source</td>
            <td>A human disease term is from either OMIM or DECIPHER or ORPHANET
            </td>
          </tr>
         <!-- <tr>
            <td>Curated Genes</td>
            <td>Possible values are:<br>
              (1) human: known disease-gene associations in human from OMIM/DECIPHER/ORPHANET in blue box<br>
              (2) mice: literature curated mouse models of disease from MGI in blue box.<br><br>

              Depending on the disease, it can have a mixture of information for both
            </td>
          </tr>
          <tr>
            <td>Candidate Genes by phenotype</td>
            <td>The value is MGI in blue box if applicable. It represents predicted mouse models of disease based on the PhenoDigm phenotypic similarity tool using either Sanger-MGP or MGI data</td>
          </tr>-->
        </table>

</body>
</html>