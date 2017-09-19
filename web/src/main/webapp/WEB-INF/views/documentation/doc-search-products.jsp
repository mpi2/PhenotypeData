<%--
  Created by IntelliJ IDEA.
  User: ckc
  Date: 20/01/2016
  Time: 16:23
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <style></style>
</head>
<body>
<%@ include file="doc-search-generic-features-note.jsp" %><br>
    <div>Firts, click on the "Products" datatype tab. It will show all the alleles that IMPC knows about if no filters are ticked on the left panel.<br>
            You could enter a mouse gene name (either by text or by ID) into the main input box and/or click on a filter on the left panel to narrow down your result to relevant allele(s).
            <p>The screenshot below shows searching mouse allele without a keyword:<br>

          </div><br>
          <img src='img/search-products.png' /><p>
          The filters on the left are grayed out if they are not related to your search.<br><br>

          <b>Columns in the dataset on the right panel:</b>
          <table>
            <tr>
              <th>Column</th>
              <th>Explaination</th>
            </tr>
            <tr>
              <td>Allele Name</td>
              <td>Nameing of the allele according to the MGI nomenclature
            <tr>
              <td>Mutation</td>
              <td>The type of mutation the allele has introduced to the animal
              </td>
            </tr>
            <tr>
              <td>Order</td>
              <td>Linkouts where you could order targeting vector/ES cell/mice for the allele
              </td>
            </tr>
            <tr>
              <td>Map</td>
              <td>Vector map depicting how the allele is produced
              </td>
            </tr>
            <tr>
                <td>Seq</td>
                <td>The genbank file of the vector cassette
            </td>
    </tr>
          </table>
    </body>
</html>