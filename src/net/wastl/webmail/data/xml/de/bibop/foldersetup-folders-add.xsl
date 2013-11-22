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
      </HEAD>

      <BODY bgcolor="#ffffff">

        <TABLE BGCOLOR="#dddddd" CELLSPACING="0" BORDER="0" WIDTH="100%">
          <TR>
            <TD VALIGN="CENTER">
              <IMG SRC="{$imgbase}/images/btn-folders.png"/>
            </TD>
            <TD VALIGN="CENTER" COLSPAN="2">
              <FONT SIZE="+2"><STRONG>JWebMail Ordnereinstellungen für <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/></STRONG></FONT><BR/>
              <EM>Benutzer <xsl:value-of select="normalize-space(/USERMODEL/USERDATA/LOGIN)"/></EM><BR/>
              <EM>Kennung existiert seit <xsl:apply-templates select="/USERMODEL/STATEDATA/VAR[@name='first login']"/></EM>
            </TD>
          </TR>
          <TR>
            <TD COLSPAN="3" bgcolor="#aaaaaa"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></TD>
          </TR>
        </TABLE>
        <FORM ACTION="{$base}/folder/setup?session-id={$session-id}&amp;method=folder&amp;addto={/USERMODEL/STATEDATA/VAR[@name='add to folder']/@value}" METHOD="POST">
          <TABLE WIDTH="100%">
            <TR>
              <TD><STRONG>Ordnername</STRONG></TD>
              <TD><INPUT TYPE="text" NAME="folder_name" SIZE="20"/></TD>
              <TD><STRONG>Ordnertyp</STRONG></TD>
              <TD>
                <SELECT NAME="folder_type">
                  <OPTION value="msgs">enthält Nachrichten</OPTION>
                  <OPTION value="folder">enthält Ordner</OPTION>
                  <OPTION value="msgfolder">enthält Nachrichten und Ordner</OPTION>
                </SELECT>
              </TD>
            </TR>
            <TR>
              <TD COLSPAN="4"><INPUT TYPE="submit" name="submit" value="Ordner hinzufügen"/></TD>
            </TR>
          </TABLE>
        </FORM>
      </BODY>
    </HTML>
  </xsl:template>

  <xsl:template match="/USERMODEL/STATEDATA/VAR">
    <xsl:value-of select="@value"/>
  </xsl:template>
</xsl:stylesheet>
