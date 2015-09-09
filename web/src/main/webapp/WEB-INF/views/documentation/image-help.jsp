<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	<jsp:attribute name="title">International Mouse Phenotyping Consortium Documentation</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/documentation/index">Documentation</a></jsp:attribute>
	<jsp:attribute name="bodyTag"><body class="page-node searchpage one-sidebar sidebar-first small-header"></body></jsp:attribute>
	<jsp:attribute name="addToFooter"></jsp:attribute>
	<jsp:attribute name="header"></jsp:attribute>

	<jsp:body>
		
        <div id="wrapper">

            <div id="main">
                <!-- Sidebar First -->
                <jsp:include page="doc-menu.jsp"></jsp:include>

                <!-- Maincontent -->

                <div class="region region-content">              

                    <div class="block block-system">

                        <div id="top" class="content node">


                            <h3>More information about the way IMPC uses images.</h3>


                            <h4 id="explore">Explore Image Data</h4>
                            <p>The IMPC portal offers images that are annotated with gene associations, Mouse Anatomy (MA) and procedure terms.
                               To search for images associated with the gene symbol "Akt2", type Akt2 into the search box at the top of the page
                               and then click on the "IMPC images" link at the side. A list with categories of images associated to Akt2 is
                               then displayed on the left. Click on these categories to see sub-categories and their respective counts of images
                               associated with that category. You can also search for genes using MGI identifiers. To search for images
                               associated to an anotomy term you can use MA identifiers or terms such as  blood vessel (MA:0000060) in the search box.
                               <br />
                               <img src="img/image_facet.png" />
                            </p>

                            <h4 id="imported">Imported Images</h4>
                            <p>IMPC Images are from the IMPC standardised <a href="https://www.mousephenotype.org/impress">IMPReSS procedures</a>.
                               The procedures highlighted in red below have image data collected. Data has been analysed by statistical methods
                               (labelled "IMPC images" in the web portal). The web portal also contains legacy data from the MGP
                               project at the Wellcome Trust Sanger Institute where the terms were manually annotated by researchers
                               (labelled "Images" in the web portal).
                               <br /><br />
                               <img src="img/pipelines_with_images.png" />
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
                       </div>
                    </div>
                </div>
            </div>
        </div>
   
    </jsp:body>
  
</t:genericpage>