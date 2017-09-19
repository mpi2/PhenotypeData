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
    <p>The screenshot below shows searching the IMPC Images datatype with the mouse anatomy term "eye":<br>
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
        </td>
    </tr>
    <tr>
        <td>Images</td>
        <td>Shows thumbnail image (Image View) or thumbnail images (Annotation View, maximun of 4) with linkout to images page</td>
    </tr>
</table>

<h4 id="imported">Imported Images</h4>
<p>IMPC Images are from the IMPC standardised <a href="https://www.mousephenotype.org/impress">IMPReSS procedures</a>.
    The procedures highlighted in red below have image data collected. Data has been analysed by statistical methods
    (labelled "IMPC images" in the web portal). The web portal also contains legacy data from the MGP
    project at the Wellcome Trust Sanger Institute where the terms were manually annotated by researchers
    (labelled "Images" in the web portal).
    <br /><br />
    <img src="img/pipelines-with-images.png" />
</p>

<h4 id="update_frequency">How often are images updated?</h4>
<p>IMPC images are released with the other data in the portal roughly every three to four months.
    Images from the MGP resource at the Wellcome Trust Sanger Institute are updated as needed at the same time as IMPC data is collected.
</p>

<h4 id="download">How do I get image data / download?</h4>
<p>Individual images may be downloaded by clicking on an image in the normal view where a larger popup image is then displayed together with
    the option of downloading it underneath. Individual images can be downloaded from the comparison view (Control images vs experimental).
    Download links are displayed next to other annotations underneath each image. A spreadsheet containing information and links can be
    downloaded using the "download" link on the top right corner of the search results.
</p>

<p>See the gene page <a href="gene-help#impc-expression">Expression</a> documentation for more information on gene page expression images.</p>
<p>See the gene page <a href="gene-help#impc-images">Associated Images</a> documentation for more information on gene page associated images.</p>

</body>
</html>