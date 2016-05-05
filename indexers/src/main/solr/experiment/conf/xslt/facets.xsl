<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<!-- Simple to CSV transform for plain facets. Takes all value for first facet field only. -->
<xsl:output method="text" encoding="iso-8859-1"/>

  <xsl:template match="/">
      <xsl:apply-templates select="response//lst[@name = 'facet_fields']/lst[1]" />
      <xsl:apply-templates select="response//lst[@name = 'facet_fields']/lst/int" />
  </xsl:template>
  
  <xsl:template match="lst[@name = 'facet_fields']/lst[1]"><xsl:apply-templates select="@name"/>,count<xsl:text>&#xD;</xsl:text></xsl:template>
  <xsl:template match="lst[@name = 'facet_fields']/lst/int">"<xsl:apply-templates select="@name" />",<xsl:value-of select="normalize-space(.)"/><xsl:text>&#xD;</xsl:text></xsl:template>

</xsl:stylesheet>