<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<c:set var="omeroStaticUrl" value="${fn:replace(impcMediaBaseUrl,'/omero/webgateway', '/static/')}"/>

<t:genericpage>

	<jsp:attribute name="title">${gene.markerSymbol} Image Picker</jsp:attribute>
	<jsp:attribute name="bodyTag"><body class="chartpage no-sidebars small-header"></jsp:attribute>

	<jsp:attribute name="header">
		<link href="${omeroStaticUrl}webgateway/css/ome.viewport.css" type="text/css" rel="stylesheet"/>
		<link href="${baseUrl}/css/comparator/comparator.css" rel="stylesheet" type="text/css"/><!-- put after default omero css so we can override -->
		<link href="${omeroStaticUrl}3rdparty/panojs-2.0.0/panojs.css" type="text/css" rel="stylesheet"/>
	</jsp:attribute>


	<jsp:attribute name="addToFooter">
		<script src="${omeroStaticUrl}/omeroweb.viewer.min.js" type="text/javascript"></script>
		<script type='text/javascript' src="${baseUrl}/js/comparator/comparatorFrames.js?v=${version}"></script>
	</jsp:attribute>

	<jsp:body>
		<div class="container row">
			<div class="col-12 no-gutters">
				<div class="node">

					<c:set var="jpegUrlThumbWithoutId" value="${impcMediaBaseUrl}/render_birds_eye_view"/>
					<c:set var="jpegUrlDetailWithoutId" value="${impcMediaBaseUrl}/img_detail"/>
					<c:set var="pdfWithoutId" value="http:${fn:replace(impcMediaBaseUrl,'webgateway','webclient/annotation')}"/>

					<!-- Orange header layout -->
					<div class="container data-heading">
						<div class="row">
							<div class="col-12 no-gutters">
								<h1 class="h1 m-0 d-inline-block">
									<b>${procedure}: ${parameter.name}</b>
								</h1>
							</div>
						</div>
					</div>

					<div class="container row"
						 style="background-color: white;">
						<div class="col-12 d-none d-lg-block px-3 py-3">
							<aside>
								<a href="/">Home</a> <span class="fal fa-angle-right"></span>
								<a href="${baseUrl}/search">Genes</a> <span class="fal fa-angle-right"></span>
								<a href="${baseUrl}/genes/${gene.mgiAccessionId}">${gene.markerSymbol}</a> <span
									class="fal fa-angle-right"></span>
								Image comparator
							</aside>
						</div>
					</div>

					<div class="container row" style="background-color:  white;">

						<div class="row text-center" style="margin-right: 0 !important;">
							<div id="control_box" class="col col-6 m-0 px-2">
								<h3>WT Images</h3>

								<c:choose>
									<c:when test="${not empty controls}">
										<c:choose>
											<c:when test="${mediaType eq 'pdf' }">
												<iframe id="control_frame" src="//docs.google.com/gview?url=${pdfWithoutId}/${controls[0].omeroId}&embedded=true"></iframe>
											</c:when>
											<c:otherwise>
												<iframe id="control_frame" src="${srcForControl}"></iframe>
											</c:otherwise>
										</c:choose>

									</c:when>
									<c:otherwise>
										No Image for Controls Selected
									</c:otherwise>
								</c:choose>

								<div id="control_annotation" class="annotation">
								</div>

								<div class="thumbList pt-2">

									<div class="row text-left">

										<c:forEach var="img" items="${controls}" varStatus="controlLoop">

											<c:forEach items="${img.parameterAssociationName}" var="currentItem"
													   varStatus="stat">
												<c:set var="controlParamAssValues"
													   value="${stat.first ? '' : paramAssValues} ${currentItem}:${img.parameterAssociationValue[stat.index]}"/>
											</c:forEach>

											<c:choose>
												<c:when test="${img.sex eq 'male' }">
													<c:set var="imgSex" value="fal fa-mars"/>
												</c:when>
												<c:when test="${img.sex eq 'female' }">
													<c:set var="imgSex" value="fal fa-venus"/>
												</c:when>
												<c:otherwise>
													<c:set var="imgSex" value="nosex"/>
												</c:otherwise>
											</c:choose>

											<div class="col mb-1 col-4">
												<div class="card image <c:if test='${controlLoop.index eq 0}'>img_selected</c:if>">
													<div class="card-img-top img-fluid text-center">
														<c:choose>
															<c:when test="${mediaType eq 'pdf' }">
																<a id="${img.omeroId}" onclick="return false;" class="text-dark clickable_image_control " data-type="pdf">
																	<svg class="bd-placeholder-img card-img-top" width="100%" height="180" xmlns="http://www.w3.org/2000/svg" role="img" aria-label="Placeholder: Image cap" preserveAspectRatio="xMidYMid slice" focusable="false">
																		<title>PDF thumbnail</title>
																		<rect width="100%" height="100%" fill="#6c757d"></rect>
																		<text dominant-baseline="middle" text-anchor="middle" x="50%" y="50%" fill="#dee2e6" dy=".3em">PDF</text>
																	</svg>
																</a>															</c:when>
															<c:otherwise>
																<img id="${img.omeroId}" src="${jpegUrlThumbWithoutId}/${img.omeroId}/" data-imageLink="${img.imageLink}" class="clickable_image_control <c:if test='${controlLoop.index eq 0}'>img_selected</c:if>" />
															</c:otherwise>
														</c:choose>
													</div>
													<div class="card-body">
														<i class="${imgSex}"></i>
														<p><small class="text-muted">Specimen ID: ${img.externalSampleId}</small></p>
														<c:if test="${img.parameterAssociationName.size() > 0}">
															<p><small class="text-muted">${img.parameterAssociationName}</small>
															</p>
														</c:if>
													</div>

												</div>
											</div>
										</c:forEach>
									</div>
								</div>
							</div>
							<div id="mutant_box" class="col col-6 m-0 px-2">
								<h3>Mutant Images</h3>
								<c:choose>
									<c:when test="${not empty mutants}">
										<c:choose>
											<c:when test="${mediaType eq 'pdf' }">
												<iframe id="mutant_frame" src="//docs.google.com/gview?url=${pdfWithoutId}/${mutants[0].omeroId}&embedded=true"></iframe>
											</c:when>
											<c:otherwise>
												<iframe id="mutant_frame" src="${srcForMutant}"></iframe>
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										No Image for Mutants Selected
									</c:otherwise>
								</c:choose>

								<div class="thumbList pt-2">

									<div class="row text-left">

										<c:forEach var="img" items="${mutants}" varStatus="mutantLoop">

											<c:forEach items="${img.parameterAssociationName}" var="currentItem" varStatus="stat">
												<c:set var="paramAssValues" value="${stat.first ? '' : paramAssValues} ${currentItem}:${img.parameterAssociationValue[stat.index]}"/>
											</c:forEach>

											<c:choose>
												<c:when test="${img.sex eq 'male' }">
													<c:set var="imgSex" value="fal fa-mars"/>
												</c:when>
												<c:when test="${img.sex eq 'female' }">
													<c:set var="imgSex" value="fal fa-venus"/>
												</c:when>
												<c:otherwise>
													<c:set var="imgSex" value="nosex"/>
												</c:otherwise>
											</c:choose>

											<div class="col mb-1 col-4">
												<div class="card image <c:if test='${mutantLoop.index eq 0}'>img_selected</c:if>">
													<div class="card-img-top img-fluid text-center">
														<c:choose>
															<c:when test="${mediaType eq 'pdf' }">
																<a id="${img.omeroId}" onclick="return false;" class="text-dark clickable_image_mutant" data-type="pdf">
																	<svg class="bd-placeholder-img card-img-top" width="100%" height="180" xmlns="http://www.w3.org/2000/svg" role="img" aria-label="Placeholder: Image cap" preserveAspectRatio="xMidYMid slice" focusable="false">
																		<title>PDF thumbnail</title>
																		<rect width="100%" height="100%" fill="#6c757d"></rect>
																		<text dominant-baseline="middle" text-anchor="middle" x="50%" y="50%" fill="#dee2e6" dy=".3em">PDF</text>
																	</svg>
																</a>
															</c:when>

															<c:otherwise>
																<img id="${img.omeroId}" src="${jpegUrlThumbWithoutId}/${img.omeroId}/" data-imageLink="${img.imageLink}" class="clickable_image_mutant <c:if test='${mutantLoop.index eq 0}'>img_selected</c:if>" title="${mutantText}" />
															</c:otherwise>
														</c:choose>
													</div>
													<div class="card-body">
														<i class="${imgSex}"></i>
														<p><small class="text-muted">Specimen ID: ${img.externalSampleId}</small></p>
														<p><small class="text-muted"><t:formatAllele>${img.alleleSymbol}</t:formatAllele> ${img.zygosity}</small>
														</p>
														<c:if test="${img.parameterAssociationName.size() > 0}">
															<p><small class="text-muted">${img.parameterAssociationName}: ${img.parameterAssociationValue}</small></p>
														</c:if>
													</div>
												</div>
											</div>
										</c:forEach>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<script type='text/javascript'>
			var jpegUrlDetailWithoutId = "${jpegUrlDetailWithoutId}";
			var pdfWithoutId = "${pdfWithoutId}";
			var googlePdf = "//docs.google.com/gview?url=replace&embedded=true";
			var omeroStaticUrl = "${omeroStaticUrl}";
			var acc = "${gene.mgiAccessionId}";
		</script>

	</jsp:body>

</t:genericpage>
