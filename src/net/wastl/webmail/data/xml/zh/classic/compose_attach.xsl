<?xml version="1.0" encoding="UTF-8"?>
<!--
$Id: compose_attach.xsl 126 2008-10-31 03:41:09Z unsaved $

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
  <xsl:output method="html" encoding="UTF-8"/>

  <xsl:variable name="imgbase" select="/USERMODEL/STATEDATA/VAR[@name='img base uri']/@value"/>
  <xsl:variable name="base" select="/USERMODEL/STATEDATA/VAR[@name='base uri']/@value"/>
  <xsl:variable name="session-id" select="/USERMODEL/STATEDATA/VAR[@name='session id']/@value"/>

  <xsl:variable name="work" select="/USERMODEL/WORK/MESSAGE[position()=1]"/>

    <xsl:template match="/">

    <HTML>
      <HEAD>
        <TITLE>JWebMail Mailbox for <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/>: Showing message <xsl:value-of select="/USERMODEL/CURRENT[@type='message']/@id"/></TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
      </HEAD>

      <BODY bgcolor="#ffffff">
        <FORM ACTION="{$base}/compose/attach?session-id={$session-id}" METHOD="POST" ENCTYPE="multipart/form-data">

          <TABLE BGCOLOR="#dddddd" CELLSPACING="0" CELLPADDING="5" BORDER="0">
            <TR>
              <TD VALIGN="CENTER"><IMG SRC="{$imgbase}/images/btn-compose.png" BORDER="0"/></TD>
              <TD VALIGN="CENTER">
                <FONT SIZE="+2"><STRONG>Attaching files...</STRONG></FONT> (<A HREF="{$base}/help?session-id={$session-id}&amp;helptopic=compose-attach">Help</A>)<BR/>
                <EM>Date: <xsl:value-of select="/USERMODEL/STATEDATA/VAR[@name='date']/@value"/></EM>
              </TD>
            </TR>
            <TR>
              <TD>
                <SELECT NAME="ATTACHMENTS" SIZE="10" multiple="multiple" WIDTH="40%">
                  <xsl:for-each select="$work/PART[position()=1]/PART[@type='binary']">
                    <OPTION VALUE="{@filename}"><xsl:apply-templates select="."/></OPTION>
                  </xsl:for-each>
                </SELECT>
              </TD>
              <TD>
                <INPUT TYPE="FILE" NAME="FILE"/><BR/>
                <STRONG>Description:</STRONG><BR/>
                <TEXTAREA NAME="DESCRIPTION" ROWS="4" COLS="79"></TEXTAREA><BR/>
                <INPUT TYPE="SUBMIT" NAME="ADD" VALUE="Add new file"/>
                <INPUT TYPE="SUBMIT" NAME="DELETE" VALUE="Delete selected file(s)"/>
              </TD>
            </TR>
            <TR>
              <TD COLSPAN="4" ALIGN="CENTER">
                <STRONG><xsl:value-of select="/USERMODEL/STATEDATA/VAR[@name='current attach size']/@value"/> of <xsl:value-of select="/USERMODEL/STATEDATA/VAR[@name='max attach size']/@value"/> bytes</STRONG>
              </TD>
            </TR>
            <TR>
              <TD COLSPAN="4" VALIGN="center">
                <A HREF="{$base}/compose?session-id={$session-id}&amp;continue=1"><IMG SRC="{$imgbase}/images/arrow-left.png" BORDER="0"/> Back to compose dialog ...</A>
              </TD>
            </TR>
          </TABLE>
        </FORM>
      </BODY>
    </HTML>
  </xsl:template>

  <!-- All parts that are attachments (= have a file name) should be displayed with their name and
       size only -->
  <xsl:template match="PART">
    <xsl:if test="@filename != ''">
      <xsl:value-of select="@filename"/> (<xsl:value-of select="@size"/> bytes)
    </xsl:if>
  </xsl:template>


  <!-- Content of a message should be displayed plain -->
  <xsl:template match="CONTENT">
    <xsl:value-of select="."/>
  </xsl:template>
</xsl:stylesheet>
