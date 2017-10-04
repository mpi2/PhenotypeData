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
    <div>Firts, click on the "Phenotypes" datatype tab. It will show all the mouse phenotypes that IMPC knows about if no filters are ticked on the left panel.<br>
            You could enter a human or mouse phenotype (either by text or by ID) into the main input box and/or click on a filter (all top level mouse phenotype ontology terms) on the left panel to narrow down your result to relevant phenotype(s).
            <p>The screenshot below shows searching phenotypes containing the word "glucose":<br>

          </div><br>
          <img src='img/search-phenotype.png' /><p>
          The filters on the left are grayed out if they are not related to your search.<br><br>

          <b>Columns in the dataset on the right panel:</b>
          <table>
            <tr>
              <th>Column</th>
              <th>Explaination</th>
            </tr>
            <tr>
              <td>Phenotype</td>
              <td>This column shows the ontological term of a phenotype with a linkout to the IMPC phenotype page. When mouseover this column, its computationally mapped HP term(s) and/or term synonym(s) will be displayed if available</td>
            </tr>
            <tr>
              <td>Definition</td>
              <td>Ontological definition of this mouse phenotype term
              </td>
            </tr>
            <tr>
              <td>Ontology Tree</td>
              <td>Shows the phenotype in the hierarchy of the Mammalian Phenotype Ontology (MP)
              </td>
            </tr>
            <tr>
              <td>Register</td>
              <td>The "Interest" link takes you to login page if you have not. Once you have logged in you will be taken back to this column and the label will be changed to "Register interest".<br>
                Click on it will register you to the interest alert system of IMPC and you will be alerted once the phenotyping data is available/updated
              </td>
            </tr>
          </table>
    </body>
</html>