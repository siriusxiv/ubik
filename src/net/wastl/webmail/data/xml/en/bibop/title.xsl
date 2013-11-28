<?xml version="1.0" encoding="ISO-8859-1"?>
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

    <xsl:variable name="imgbase" select="/USERMODEL/STATEDATA/VAR[@name='img base uri']/@value"/>
    <xsl:variable name="base" select="/USERMODEL/STATEDATA/VAR[@name='base uri']/@value"/>
    <xsl:variable name="session-id" select="/USERMODEL/STATEDATA/VAR[@name='session id']/@value"/>

    <xsl:template match="/">

    <HTML>
      <HEAD>
        <TITLE>JWebMail Mailbox for <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/>: Title Frame</TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
        <link rel="stylesheet" href="{$base}/passthrough/webmail.css"/>
      </HEAD>

      <BODY bgcolor="#B5C1CF" topmargin="0" leftmargin="0" marginwidth="0" marginheight="0">
        <TABLE width="100%" border="0" cellspacing="0" cellpadding="0">
          <TR>
            <TD width="5%" background="{$imgbase}/images/sfondino_grigio.gif">
                <IMG SRC="{$imgbase}/images/logo.gif" alt="Logo BiBop"/>
            </TD>
            <TD width="13%" background="{$imgbase}/images/sfondino_grigio.gif">
                <IMG SRC="{$imgbase}/images/webmail.gif" alt="Logo JWebMail BiBop"/>
            </TD>
            <TD width="2%" background="{$imgbase}/images/sfondino_scuro.gif">
                <IMG SRC="{$imgbase}/images/spacer.gif" width="15" height="1"/>
            </TD>
            <TD width="32%" background="{$imgbase}/images/sfondino_scuro.gif" align="center">
                &#160;
            </TD>
            <TD width="23%" valign="top" background="{$imgbase}/images/sfondino_scuro.gif">
                &#160;
            </TD>
            <TD width="16%" valign="top" align="right" bgcolor="#697791" background="{$imgbase}/images/curva_alto.gif" class="mailbox">
              <SPAN class="mailbold"><xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/></SPAN>'S<BR/>MAILBOX
            </TD>
            <TD align="right" width="9%" background="{$imgbase}/images/sfondino_grigio_scuro.gif">
                <IMG SRC="{$imgbase}/images/mailbox_dx.gif"/>
            </TD>
          </TR>
        </TABLE>
      </BODY>
    </HTML>
  </xsl:template>
</xsl:stylesheet>
