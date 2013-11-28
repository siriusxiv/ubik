<?xml version="1.0" encoding="UTF-8"?>
<!--
 * Copyright (C) 2000 Sebastian Schaffert
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
-->


<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xslt" version="1.0">
  <xsl:output method="html" indent="yes" xalan:content-handler="org.apache.xml.serializer.ToHTMLStream"/>

    <xsl:variable name="imgbase" select="/GENERICMODEL/STATEDATA/VAR[@name='img base uri']/@value"/>
    <xsl:variable name="base" select="/GENERICMODEL/STATEDATA/VAR[@name='base uri']/@value"/>
    <xsl:variable name="session-id" select="/GENERICMODEL/STATEDATA/VAR[@name='session id']/@value"/>

    <xsl:template match="/">

    <HTML>
      <HEAD>
        <TITLE>Interfaz de administraci&#243;n de JWebMail: Navegaci&#243;n</TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
      </HEAD>

      <BODY BGCOLOR="lightblue">
        <TABLE WIDTH="100%">
          <TR>
            <TD>
              <A HREF="{$base}/admin/system?session-id={$session-id}" TARGET="Main"><FONT SIZE="-1" COLOR="red">Configuraci&#243;n del sistema</FONT></A>
            </TD>
          </TR>
          <TR>
            <TD>
              <A HREF="{$base}/admin/control?session-id={$session-id}" TARGET="Main"><FONT SIZE="-1" COLOR="red">Control del sistema</FONT></A>
            </TD>
          </TR>
          <TR>
            <TD>
              <A HREF="{$base}/admin/domain?session-id={$session-id}" TARGET="Main"><FONT SIZE="-1" COLOR="red">Configuraci&#243;n de dominios virtuales</FONT></A>
            </TD>
          </TR>
          <TR>
            <TD>
              <A HREF="{$base}/admin/user?session-id={$session-id}" TARGET="Main"><FONT SIZE="-1" COLOR="red">Configuraci&#243;n de usuario</FONT></A>
            </TD>
          </TR>
          <TR>
            <TD>
<!--          <A HREF="{$base}/admin/help?session-id={$session-id}" TARGET="Main"><FONT SIZE="-1" COLOR="red">Help</FONT></A>-->
              <FONT SIZE="-1" COLOR="red">Ayuda (a&#250;n no implementada)</FONT>
            </TD>
          </TR>
          <TR>
            <TD>
              <A HREF="{$base}/admin/logout?session-id={$session-id}" TARGET="_top"><FONT SIZE="-1" COLOR="red">Salir</FONT></A>
            </TD>
          </TR>
        </TABLE>
      </BODY>
    </HTML>
  </xsl:template>
</xsl:stylesheet>
