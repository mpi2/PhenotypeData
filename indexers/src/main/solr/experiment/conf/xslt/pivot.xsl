<?xml version="1.0" encoding="UTF-8" ?>
<!-- Recursive method to produce CSV file from solr pivot facet results -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="text" encoding="iso-8859-1"/>

  <xsl:template match="/">
      <xsl:apply-templates select="response//lst[@name = 'facet_pivot']/arr" />
      <xsl:apply-templates select="response//lst[@name = 'facet_pivot']/arr/lst" />
  </xsl:template>
  
  
  <xsl:template match="lst[@name = 'facet_pivot']/arr"><xsl:apply-templates select="@name"/>,count<xsl:text>&#xD;</xsl:text></xsl:template> <!-- header -->
  
  <xsl:template match="lst[@name = 'facet_pivot']/arr/lst">
  	<xsl:variable name="v1" select="concat(concat('&quot;',response//lst[@name = 'facet_pivot']/arr/lst/str[@name='value']), '&quot;,')"/>
  	<xsl:if test="arr/lst/arr/lst">
  		<xsl:call-template name="inner">
  			<xsl:with-param name="line" select="$v1" />
  		</xsl:call-template>
	</xsl:if>
	<xsl:if test="not(arr/lst/arr/lst)">
  		<xsl:call-template name="last">
  			<xsl:with-param name="wholeLine" select="$v1" />  			
  		</xsl:call-template>
	</xsl:if>
  </xsl:template>
  
  
  <xsl:template name="last">
  	<xsl:param name="wholeLine" />
  	<xsl:for-each select="arr/lst">
  		<xsl:if test="str[@name='value']">
			<xsl:value-of select="$wholeLine"/>"<xsl:value-of select="str[@name='value']"/>",<xsl:value-of select="normalize-space(int)"/><xsl:text>&#xD;</xsl:text>
		</xsl:if>
  	</xsl:for-each>
  </xsl:template>
  
  
  <xsl:template name="inner">
    <xsl:param name="line" />  
  	<xsl:for-each select="arr/lst">
		<xsl:variable name="currentLine" select="concat($line, concat(concat('&quot;',str[@name='value']), '&quot;,'))"/>
		<xsl:if test="arr/lst/arr/lst">
  			<xsl:call-template name="inner">
  				<xsl:with-param name="line" select="$currentLine" />  			
  			</xsl:call-template>
		</xsl:if>
		<xsl:if test="not(arr/lst/arr/lst)">
  			<xsl:call-template name="last">
  				<xsl:with-param name="wholeLine" select="$currentLine" />  			
  			</xsl:call-template>
		</xsl:if>
  	</xsl:for-each>
  </xsl:template>
</xsl:stylesheet>