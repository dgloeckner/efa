<?xml version ="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" version="4.0"/>
<xsl:template match ="/">
    <html>
    <body title="EFA Versionshistorie">
    <h1><b>EFA Versionshistorie</b></h1>
    <xsl:for-each select="efaOnlineUpdate/Version">
	<h2><b><xsl:value-of select="VersionID"/>  Release Date: <xsl:value-of select="ReleaseDate"/></b></h2>
        <h3><xsl:if test="not(count(MinimumJavaVersion) = 0)">Minimale Java Version: <xsl:value-of select="MinimumJavaVersion"/></xsl:if>
            <xsl:if test="not(count(MinimumEfaCloudVersion) = 0)"><br/>Minimale efaCloud Version: <xsl:value-of select="MinimumEfaCloudVersion"/></xsl:if></h3>
	<xsl:if test="not(count(ShowNotice) = 0)">
	<b><i><font color="#EE0000">Wichtige Hinweise:</font></i></b>
	<ul>
	    <xsl:for-each select="ShowNotice[@lang='de']">
	    <li>
		<xsl:value-of select="."/>
	    </li>
	    </xsl:for-each>
	</ul><br/>
	</xsl:if>
	<b>Änderungen</b><br/>
	<xsl:for-each select="Changes[@lang='de']">
	    <ul>
	    <xsl:for-each select="ChangeItem">
		<li>
		    <xsl:value-of select="."/>
		</li>
	    </xsl:for-each>
	    </ul>
	</xsl:for-each>
    </xsl:for-each>
    </body>
    </html>
</xsl:template>
</xsl:stylesheet>