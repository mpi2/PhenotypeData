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

        Currently, there are two major types of images. The "IMPC images" (the IMPC Images datatype) and the legacy ones (the Images datatype).<br>
        The Images datatype is legacy data from previous Europhenome/Eumodic phenotyping projects.<br><br>

        As both datatypes can be searched by similar interfaces, the document here refers to the "IMPC Images" datatype.<br><br>

        <div>First, click on the "IMPC Images" datatype tab. It will show all images that IMPC knows about if no filters are ticked on the left panel.<br>
          You could enter a mouse anatomy ID/name or procedure name from IMPReSS into the main input box and/or click on a filter on the left panel to narrow down your result to relevant IMPC images.<br>
          IMPReSS contains standardized phenotyping protocols essential for the characterization of mouse phenotypes.<br><br>
          <p>The screenshot below shows searching the IMPC Images datatype with the mouse anatomy term "trunk":<br>
        </div><br>

        There are two ways to view images data: Annotation View and Image View.<br>
        The former groups images by annotations which can be genes, anatomy terms (MA) or procedures. The latter lists annotations (genes, MA and procedure) to an image.<br><br>


        <h6 id="quick-image-search">Annotation View (default)</h6>
        <img src='img/search-image-annotview.png' /><br>


        <h6 id="quick-image-search-image-view">Image View</h6>
        <p>To list annotations to an image, simply click on the "Show Image View" link in the top-right corner of the results grid. The label of the same link will then change to "Show Annotation View" so that you can toggle the views.</p><br>
        <img src='img/search-image-imgview.png' /><br>

        <b>Columns in the dataset on the right panel:</b>

        <table>
          <tr>
            <th>Column</th>
            <th>Explaination</th>
          </tr>
          <tr>
            <td>Name</td>
            <td><b>Annotation View</b>:<br>This column shows name of an annotation and the number of annotation-associated images.<br>A gene annotation is a linkout to IMPC gene page; a procedure annotation does not have a link for now and a MA annotation is a linkout to anatomy page<br><br>
              <b>Image View</b>:<br>This column shows a group of different annotations (gene, procedure, MA)<br>
            </td>>
          </tr>
          <tr>
            <td>Images</td>
            <td>Shows thumbnail image (Image View) or thumbnail images (Annotation View, maximun of 4) with linkout to images page</td>
          </tr>
        </table>

</body>
</html>