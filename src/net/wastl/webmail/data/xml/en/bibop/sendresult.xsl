<?xml version="1.0" encoding="ISO-8859-1"?>
<!-- $Id: sendresult.xsl 126 2008-10-31 03:41:09Z unsaved $ -->

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

  <xsl:variable name="work" select="/USERMODEL/WORK/MESSAGE[position()=1]"/>

    <xsl:template match="/">

    <HTML>
      <HEAD>
        <TITLE>JWebMail Mailbox for <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/>: Send message result</TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
        <META CONTENT="GENERATOR" VALUE="JWebMail 1.0.1 XSL"/>
        <link rel="stylesheet" href="{$base}/passthrough/webmail.css"/>
      </HEAD>

      <BODY bgcolor="#B5C1CF" topmargin="5" leftmargin="0" marginwidth="0" marginheight="5">
        <TABLE width="100%" border="0" cellspacing="2" cellpadding="4">
          <TR>
            <TD height="22" class="testoNero">
              <IMG SRC="{$imgbase}/images/icona_composer.gif" BORDER="0" align="absmiddle"/>Mail delivery results
            </TD>
          </TR>
          <TR>
            <TD bgcolor="#697791" height="22" class="testoBianco">Date: <xsl:value-of select="/USERMODEL/STATEDATA/VAR[@name='date']/@value"/></TD>
          </TR>
          <TR>
            <TD class="testoGrande" bgcolor="#A6B1C0">Sending message<BR/>
             From: <SPAN class="testoNero"><xsl:value-of select="$work/HEADER/FROM"/></SPAN><BR/>
              Subject: <SPAN class="testoNero"><xsl:value-of select="$work/HEADER/SUBJECT"/></SPAN><BR/>
              To: <SPAN class="testoNero"><xsl:value-of select="$work/HEADER/TO"/></SPAN><BR/>
              Date: <SPAN class="testoNero"><xsl:value-of select="/USERMODEL/STATEDATA/VAR[@name='date']/@value"/></SPAN>
            </TD>
          </TR>
          <TR>
            <TD class="testoGrande" bgcolor="#D1D7E7">Send status:
                <SPAN class="testoNero"><xsl:value-of select="/USERMODEL/STATEDATA/VAR[@name='send status']/@value"/>
                </SPAN>
            </TD>
        </TR>
        <TR>
         <TD class="testoGrande" bgcolor="#E2E6F0">
          <!-- Only show the section for valid addresses if there actually were any -->
          <xsl:if test="/USERMODEL/STATEDATA/VAR[@name='valid sent addresses']/@value != ''">
               Delivered to addresses: <SPAN class="testoNero">valid&#160;&#160;<xsl:value-of select="/USERMODEL/STATEDATA/VAR[@name='valid sent addresses']/@value"/></SPAN>
          </xsl:if>
         </TD>
        </TR>
        <TR>
          <TD class="testoGrande" bgcolor="#E2E6F0">
          <!-- Only show the section for invalid addresses if there actually were any -->
          <xsl:if test="/USERMODEL/STATEDATA/VAR[@name='valid unsent addresses']/@value != '' or /USERMODEL/STATEDATA/VAR[@name='invalid unsent addresses']/@value != ''">
            Not delivered to addresses:
              <SPAN class="testoNero">
                <xsl:if test="/USERMODEL/STATEDATA/VAR[@name='valid unsent addresses']/@value != ''">valid&#160;&#160;
<xsl:value-of select="/USERMODEL/STATEDATA/VAR[@name='valid unsent addresses']/@value"/>
                </xsl:if></SPAN>
            <SPAN class="testoNero"><xsl:if test="/USERMODEL/STATEDATA/VAR[@name='invalid unsent addresses']/@value != ''">
              invalid&#160;&#160;<xsl:value-of select="/USERMODEL/STATEDATA/VAR[@name='invalid unsent addresses']/@value"/>
            </xsl:if></SPAN>
          </xsl:if>
          </TD>
        </TR>
        <TR>
          <TD bgcolor="#697791">
        <TABLE border="0" cellpadding="0" cellspacing="0"><TR><TD>
        <A HREF="{$base}/compose?session-id={$session-id}&amp;continue=1"><IMG SRC="{$imgbase}/images/back.gif" BORDER="0"/></A></TD><TD><A HREF="{$base}/compose?session-id={$session-id}&amp;continue=1"><SPAN class="testoBianco"> Back to compose dialog ...</SPAN></A></TD></TR></TABLE>
          </TD>
        </TR>
      </TABLE>
      </BODY>
    </HTML>
  </xsl:template>
</xsl:stylesheet>
