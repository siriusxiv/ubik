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
        <TITLE>Buz&#243;n de JWebMail para <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/>: Configuraci&#243;n de carpetas</TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
        <META CONTENT="GENERATOR" VALUE="JWebMail 1.0.1 XSL"/>
        <link rel="stylesheet" href="{$base}/passthrough/webmail.css"/>
      </HEAD>

      <BODY bgcolor="#B5C1CF" topmargin="5" leftmargin="0" marginwidth="0" marginheight="5">

        <TABLE width="100%" border="0" cellspacing="2" cellpadding="4">
          <TR>
            <TD colspan="2" height="22" class="testoNero">
              <IMG SRC="{$imgbase}/images/icona_folder.gif" align="absmiddle"/>
            Configuraci&#243;n de carpetas de JWebMail para <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/> (<A HREF="{$base}/help?session-id={$session-id}&amp;helptopic=folder-setup">Ayuda</A>)
                </TD>
            </TR>
           <TR>
            <TD colspan="4" bgcolor="#697791" height="22" class="testoBianco">
                Nombre de usuario <xsl:value-of select="normalize-space(/USERMODEL/USERDATA/LOGIN)"/><BR/>
                La cuenta existe desde <xsl:apply-templates select="/USERMODEL/STATEDATA/VAR[@name='first login']"/>
           </TD>
          </TR>
          <TR>
            <TD colspan="2" bgcolor="#A6B1C0" height="22" class="testoGrande">
                Tienes las siguientes opciones:
            </TD>
          </TR>
          <TR>
            <TD height="22" width="23%" class="testoNero" bgcolor="#E2E6F0">
              <A HREF="{$base}/folder/setup?session-id={$session-id}&amp;method=mailbox">A&#241;adir/Borrar buz&#243;n</A>
            </TD>
            <TD height="22" class="testoNero" width="77%" bgcolor="#E2E6F0">
              JWebMail te permite tener varios buzones, en servidores IMAP y POP. Puedes a&#241;adir y eliminar esos buzones aqu&#237;.
            </TD>
          </TR>
          <TR>
            <TD height="22" width="23%" bgcolor="#D1D7E7" class="testoNero">
              <A HREF="{$base}/folder/setup?session-id={$session-id}&amp;method=folder">A&#241;adir/Borrar carpetas</A>
            </TD>
            <TD height="22" class="testoNero" width="77%" bgcolor="#D1D7E7">
              JWebMail te mostrar&#225; un &#225;rbol de carpetas para cada buz&#243;n, donde podr&#225;s a&#241;adir o
              borrar carpetas.
            </TD>
          </TR>
        </TABLE>
      </BODY>
    </HTML>
  </xsl:template>
</xsl:stylesheet>
