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
        <div>First, click on the "Anatomy" datatype tab. It will show all the anatomy terms that IMPC knows about if no filters are ticked on the left panel.<br>
          You could enter a mouse anatomy ID/name into the main input box and/or click on a filter (all top level mouse anatomy ontology terms) on the left panel to narrow down your result to relevant anatomy term(s).<br><br>
          <p>The screenshot below shows searching the mouse anatomy terms containing "eye":<br>
        </div><br>
        <img src='img/search-anatomy.png' /><p>

        The filters on the left are grayed out if they are not related to your search.<br><br>

        <b>Columns in the dataset on the right panel:</b>
        <table>
          <tr>
            <th>Column</th>
            <th>Explaination</th>
          </tr>
          <tr>
            <td>Anatomy</td>
            <td>This column shows the ontological term of a mouse anatomical tissue/organ with a linkout to the IMPC anatomy page</td>
          </tr>
          <tr>
             <td>Stage</td>
             <td>Either embry or adult</td>
          </tr>
          <tr>
            <td>LacZ Expression Data</td>
            <td>This column indicates existence of expression images associated with this anatomy term</td>
          </tr>
          <tr>
            <td>Ontology Tree</td>
            <td>Shows the tissue/organ in the hierarchy of the Mouse Adult Gross Anatomy Ontology (MA)</td>
          </tr>
        </table>
   </body>
</html>