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
  <xsl:output method="html" indent="yes" encoding="UTF-8" xalan:content-handler="org.apache.xml.serializer.ToHTMLStream"/>

    <xsl:variable name="imgbase" select="/USERMODEL/STATEDATA/VAR[@name='img base uri']/@value"/>
    <xsl:variable name="base" select="/USERMODEL/STATEDATA/VAR[@name='base uri']/@value"/>
    <xsl:variable name="session-id" select="/USERMODEL/STATEDATA/VAR[@name='session id']/@value"/>

    <xsl:template match="/">

    <HTML>
      <HEAD>
        <TITLE>JWebMail Mailbox für <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/>: Ordnereinstellungen</TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
        <META CONTENT="GENERATOR" VALUE="JWebMail 1.0.1 XSL"/>
        <link rel="stylesheet" href="{$base}/passthrough/webmail.css"/>
      </HEAD>

      <BODY bgcolor="#B5C1CF" topmargin="5" leftmargin="0" marginwidth="0" marginheight="5">

        <TABLE width="100%" border="0" cellspacing="2" cellpadding="4">
         <FORM ACTION="{$base}/folder/setup?session-id={$session-id}&amp;method=mailbox&amp;add=1" METHOD="POST">
          <TR>
            <TD colspan="4" height="22" class="testoNero">
              <IMG SRC="{$imgbase}/images/icona_folder.gif" align="absmiddle"/>
               JWebMail Ordnereinstellungen für <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/> (<A HREF="{$base}/help?session-id={$session-id}&amp;helptopic=folder-setup-mailboxes">Hilfe</A>)
            </TD>
           </TR>
           <TR>
            <TD colspan="4" bgcolor="#697791" height="22" class="testoBianco">
                Benutzer <xsl:value-of select="normalize-space(/USERMODEL/USERDATA/LOGIN)"/><BR/>
                Kennung existiert seit <xsl:apply-templates select="/USERMODEL/STATEDATA/VAR[@name='first login']"/>
           </TD>
          </TR>
          <TR>
            <TD bgcolor="#A6B1C0" colspan="4" height="22" align="center" class="testoGrande">Briefkästen entfernen</TD>
          </TR>
          <xsl:for-each select="/USERMODEL/USERDATA/MAILHOST">
            <TR>
              <TD width="25%" height="35" class="testoNero" bgcolor="#E2E6F0">
                <SPAN class="bold">
                  <xsl:value-of select="@name"/>
                </SPAN>
              </TD>
              <TD width="50%" colspan="2" bgcolor="#D1D7E7" class="testoNero">
                <xsl:apply-templates select="MH_URI"/>
              </TD>
              <TD width="25%" class="testoNero" bgcolor="#E2E6F0"><A HREF="{$base}/folder/setup?session-id={$session-id}&amp;method=mailbox&amp;remove={@id}">Entfernen</A></TD>
            </TR>
          </xsl:for-each>
          <TR>
            <TD bgcolor="#A6B1C0" colspan="4" height="22" align="center" class="testoGrande">Briefkasten hinzufügen</TD>
          </TR>
          <TR>
            <TD width="25%" bgcolor="#E2E6F0" class="testoNero">Name des Briefkasten:</TD>
            <TD width="75%" colspan="3" class="testoNero" bgcolor="#D1D7E7">
              <INPUT TYPE="text" SIZE="25" NAME="mbox_name" class="testoNero"/>
            </TD>
          </TR>
          <TR>
            <TD width="25%" class="testoNero" bgcolor="#E2E6F0">Rechner:</TD>
            <TD width="25%" class="testoNero" bgcolor="#D1D7E7"><INPUT TYPE="text" SIZE="10" NAME="mbox_host" class="testoNero"/>
            </TD>
            <TD width="25%" class="testoNero" bgcolor="#E2E6F0">Protokoll:</TD>
            <TD width="25%" class="testoNero" bgcolor="#D1D7E7">
              <SELECT NAME="mbox_proto" class="testoNero">
                <xsl:for-each select="/USERMODEL/STATEDATA/VAR[@name='protocol']">
                  <xsl:choose>
                    <xsl:when test="@value = /USERMODEL/SYSDATA//CONFIG[KEY='DEFAULT PROTOCOL']/VALUE">
                      <OPTION selected="selected"><xsl:value-of select="@value"/></OPTION>
                    </xsl:when>
                    <xsl:otherwise>
                      <OPTION><xsl:value-of select="@value"/></OPTION>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:for-each>
              </SELECT>
            </TD>
          </TR>
          <TR>
            <TD width="25%" class="testoNero" bgcolor="#E2E6F0">Kennung:
            </TD>
            <TD width="25%" class="testoNero" bgcolor="#D1D7E7">
              <INPUT TYPE="text" SIZE="10" NAME="mbox_login" class="testoNero"/>
            </TD>
            <TD width="25%" class="testoNero" bgcolor="#E2E6F0">Passwort:
            </TD>
            <TD width="25%" class="testoNero" bgcolor="#D1D7E7">
              <INPUT TYPE="PASSWORD" SIZE="10" NAME="mbox_password" class="testoNero"/>
            </TD>
          </TR>
          <TR>
            <TD colspan="4" align="center" class="testoNero" bgcolor="#A6B1C0">
              <INPUT TYPE="submit" NAME="submit" VALUE="Add Mailbox" class="testoNero"/>
            </TD>
          </TR>
        </FORM>
      </TABLE>
    </BODY>
    </HTML>
  </xsl:template>

  <xsl:template match="/USERMODEL/STATEDATA/VAR">
    <xsl:value-of select="@value"/>
  </xsl:template>
</xsl:stylesheet>
