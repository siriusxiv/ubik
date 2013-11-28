<?xml version="1.0" encoding="UTF-8"?>
<!--
$Id: admin-status.xsl 126 2008-10-31 03:41:09Z unsaved $

Copyright 2008 by the JWebMail Development Team and Sebastian Schaffert.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html" indent="yes"/>
  <xsl:variable name="imgbase" select="/GENERICMODEL/STATEDATA/VAR[@name='img base uri']/@value"/>
  <xsl:variable name="base" select="/GENERICMODEL/STATEDATA/VAR[@name='base uri']/@value"/>
  <xsl:variable name="session-id" select="/GENERICMODEL/STATEDATA/VAR[@name='session id']/@value"/>
  <xsl:template match="/">
    <HTML>
      <HEAD>
        <TITLE>Interfaz de administraci&#243;n de JWebMail: Estado del sistema</TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
      </HEAD>
      <BODY BGCOLOR="white">
        <TABLE WIDTH="100%" CELLSPACING="0" CELLPADDING="0">
          <TR bgcolor="#dddddd">
            <TD COLSPAN="3" ALIGN="center">
              <FONT SIZE="+1">
                <STRONG>Threads activos del servidor</STRONG>
              </FONT>
            </TD>
          </TR>
          <xsl:if test="/GENERICMODEL/STATEDATA/VAR[@name=&quot;http server status&quot;]/@value != &quot;&quot;">
            <TR>
              <TD>
                <STRONG>Servidor HTTP</STRONG>
              </TD>
              <TD COLSPAN="2">
                <PRE>
                  <xsl:value-of select="/GENERICMODEL/STATEDATA/VAR[@name=&quot;http server status&quot;]/@value"/>
                </PRE>
              </TD>
            </TR>
          </xsl:if>
          <xsl:if test="/GENERICMODEL/STATEDATA/VAR[@name=&quot;ssl server status&quot;]/@value != &quot;&quot;">
            <TR>
              <TD>
                <STRONG>Servidor SSL</STRONG>
              </TD>
              <TD COLSPAN="2">
                <PRE>
                  <xsl:value-of select="/GENERICMODEL/STATEDATA/VAR[@name=&quot;ssl server status&quot;]/@value"/>
                </PRE>
              </TD>
            </TR>
          </xsl:if>
          <xsl:if test="/GENERICMODEL/STATEDATA/VAR[@name=&quot;servlet status&quot;]/@value != &quot;&quot;">
            <TR>
              <TD>
                <STRONG>Servlet</STRONG>
              </TD>
              <TD COLSPAN="2">
                <PRE>
                  <xsl:value-of select="/GENERICMODEL/STATEDATA/VAR[@name=&quot;servlet status&quot;]/@value"/>
                </PRE>
              </TD>
            </TR>
          </xsl:if>
          <xsl:if test="/GENERICMODEL/STATEDATA/VAR[@name=&quot;storage status&quot;]/@value != &quot;&quot;">
            <TR>
              <TD>
                <STRONG>Almacenamiento</STRONG>
              </TD>
              <TD COLSPAN="2">
                <PRE>
                  <xsl:value-of select="/GENERICMODEL/STATEDATA/VAR[@name=&quot;storage status&quot;]/@value"/>
                </PRE>
              </TD>
            </TR>
          </xsl:if>
          <TR bgcolor="#dddddd">
            <TD COLSPAN="3" ALIGN="center">
              <FONT SIZE="+1">
                <STRONG>Sesiones activas</STRONG>
              </FONT>
            </TD>
          </TR>
          <xsl:for-each select="/GENERICMODEL/STATEDATA/SESSION">
            <xsl:sort select="@type" order="ascending"/>
            <xsl:choose>
              <xsl:when test="position() mod 2 = 1">
                <TR bgcolor="#f7f3a8">
                  <xsl:apply-templates select="."/>
                </TR>
              </xsl:when>
              <xsl:otherwise>
                <TR>
                  <xsl:apply-templates select="."/>
                </TR>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
          <TR bgcolor="#dddddd">
            <TD COLSPAN="3" ALIGN="center">
              <FONT SIZE="+1">
                <STRONG>Apagar/Reiniciar</STRONG>
              </FONT>
            </TD>
          </TR>
          <TR>
            <FORM ACTION="{$base}/admin/control?session-id={$session-id}" METHOD="POST">
              <TD>
                <STRONG>Apagar/Reiniciar el sistema</STRONG>
              </TD>
              <TD>despu&#233;s de <INPUT TYPE="text" SIZE="4" NAME="SHUTDOWN SECONDS" VALUE="0"/> segundos</TD>
              <TD>
                <INPUT TYPE="submit" name="REBOOT" value="Restart"/>
                <INPUT TYPE="submit" name="SHUTDOWN" value="Shutdown"/>
              </TD>
            </FORM>
          </TR>
        </TABLE>
      </BODY>
    </HTML>
  </xsl:template>
  <xsl:template match="SESSION">
    <FORM ACTION="{$base}/admin/control/kill?session-id={$session-id}&amp;kill={SESS_CODE}" METHOD="POST">
      <TD>
        <xsl:choose>
          <xsl:when test="@type = 'admin'">
            <STRONG>Sesi&#243;n de admin.</STRONG>
          </xsl:when>
          <xsl:otherwise><STRONG>Sesi&#243;n de usuario </STRONG> (usuario <xsl:value-of select="SESS_USER"/>)
          </xsl:otherwise>
        </xsl:choose>
      </TD>
      <TD><STRONG>direcci&#243;n remota: </STRONG><xsl:value-of select="SESS_ADDRESS"/>, <STRONG>tiempo sin uso: </STRONG><xsl:value-of select="VAR[@name='idle time']/@value"/><BR/><xsl:if test="count(SESS_CONN) &gt; 0"><STRONG>conexiones de correo activas:</STRONG><BR/><UL><xsl:for-each select="SESS_CONN"><LI><EM><xsl:value-of select="."/></EM></LI></xsl:for-each></UL></xsl:if></TD>
      <TD>
        <INPUT TYPE="submit" NAME="submit" VALUE="Kill"/>
      </TD>
    </FORM>
  </xsl:template>
</xsl:stylesheet>
