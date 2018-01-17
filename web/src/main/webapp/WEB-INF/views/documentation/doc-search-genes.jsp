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
        <div>First, click on the "Genes" datatype tab. It will show all the genes that IMPC knows about if no filters are ticked on the left panel.<br>
          You could enter a gene symbol/ID/name or human orthologue into the main input box and/or click on a filter on the left panel to narrow down your result to relevant gene(s).
          <p>The screenshot below shows searching the gene symbol "nxn":<br>
        </div><br>
        <img src='img/search-gene.png' /><p>

        The filters on the left are grayed out if they are not related to your search.<br><br>
        The <b>IMPC Phenotyping Status</b> facet is open by default and has three sub-facets and are progressive.<br>
        <b>(1) Approved</b> means completed status. Ie, phenotyping procedures agreed among the IMPC phenotyping centers have been completed.<br>
        <b>(2) Attempt Registered</b> means phenotyping centers have plans to carry out the phenotyping procedures.<br>
        <b>(3) Started</b> means a phenotyping work has been started.<br><br>


        <b>Columns in the dataset on the right panel:</b>
        <table>
          <tr>
            <th>Column</th>
            <th>Explaination</th>
          </tr>
          <tr>
            <td>Gene</td>
            <td>This column shows the symbol of a gene with a linkout to the IMPC gene page. When mouseover this column, its gene name, human ortholog(s) and synonym will be displayed whichever is available</td>
          </tr>
          <tr>
            <td>Production</td>
            <td>Possible information in this column are one of the four values:<br>
              (1) "mice produced" labelled as "Mice" in blue box<br>
              (2) "assigned for mouse production and phenotyping" labelled as "Mice" in orange box<br>
              (3) "ES cells produced" labelled as "ES Cells" in blue box<br>
              (4) "not assigned for ES cell production" which is not shown<br><br>
              Depending on the status of a gene, it can have a mixture of information for both mice and ES cells
            </td>
          </tr>
          <tr>
            <td>Phenotype</td>
            <td>Possible information in this column are:<br>
              (1) "phenotype data available" in blue box: this indicates a gene with phenotyping data already being approved or a center has started producing phenotyping data of a gene<br>
              (2) "legacy data available" in gray box: this indicates a gene has old phenotyping data from the Eumodic / Europhenome projects<br><br>
              Depending on the status of a gene, it can have a mixture of information for both
            </td>
          </tr>
          <tr>
            <td>Register</td>
            <td>The "Interest" link takes you to login page if you have not. Once you have logged in you will be taken back to this column and the label will be changed to "Register interest".<br>
              Click on it will register you to the interest alert system of IMPC and you will be alerted once the gene status data is available/updated
            </td>
          </tr>
        </table>

</body>
</html>