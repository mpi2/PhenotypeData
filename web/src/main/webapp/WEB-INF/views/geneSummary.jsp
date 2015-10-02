<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

    <jsp:attribute name="title">Gene details for ${gene.markerName}</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search#q=*:*&facet=gene">Genes</a> &raquo; ${gene.markerSymbol}</jsp:attribute>
	<jsp:attribute name="bodyTag">
		<body class="gene-node no-sidebars small-header">
	
	</jsp:attribute>
	

</t:genericpage>