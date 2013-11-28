<?xml version="1.0" encoding="UTF-8"?>
<!--
$Id: help.xsl 126 2008-10-31 03:41:09Z unsaved $

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

    <xsl:template match="/">

    <HTML>
      <HEAD>
        <TITLE>JWebMail Mailbox for <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/>: Main Frame/Help</TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
      </HEAD>

      <BODY bgcolor="#dddddd">
        <H1><CENTER>JWebMail Help</CENTER></H1>
        <P>
          <TABLE WIDTH="100%" BORDER="0">

            <!-- Test whether the user has choosen a specific help topic or wants to display the whole
                 help file -->
            <xsl:choose>
              <xsl:when test="/USERMODEL/STATEDATA/VAR[@name='helptopic']">
                <xsl:apply-templates select="/USERMODEL/help/helptopic[@id = /USERMODEL/STATEDATA/VAR[@name='helptopic']/@value]"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates select="/USERMODEL/help/helptopic"/>
              </xsl:otherwise>
            </xsl:choose>

            <TR>
              <TD COLSPAN="2" ALIGN="CENTER">
                <STRONG>
                  <EMPH>This system uses <xsl:apply-templates select="/USERMODEL/STATEDATA/VAR[@name='java virtual machine']"/> on <xsl:apply-templates select="/USERMODEL/STATEDATA/VAR[@name='operating system']"/></EMPH>
                </STRONG>
              </TD>
            </TR>

            <TR>
              <TD COLSPAN="2" ALIGN="CENTER">
                <STRONG><EMPH>JWebMail <xsl:apply-templates select="/USERMODEL/STATEDATA/VAR[@name='webmail version']"/> (c)1999-2001 by Sebastian Schaffert, schaffer@informatik.uni-muenchen.de</EMPH></STRONG>
              </TD>
            </TR>
          </TABLE>
        </P>
      </BODY>
    </HTML>
  </xsl:template>

  <xsl:template match="/USERMODEL/STATEDATA/VAR">
    <xsl:value-of select="@value"/>
  </xsl:template>

  <xsl:template match="helptopic">
    <TR>
      <A NAME="{@id}"/>
      <TD VALIGN="top">
        <xsl:if test="icon">
          <IMG SRC="{$imgbase}/images/{icon/@href}" BORDER="0"/>
        </xsl:if>
      </TD>
      <TD>
        <P>
          <STRONG><xsl:value-of select="@title"/></STRONG>
        </P>
        <xsl:apply-templates select="helpdata"/>
        <P>
          <STRONG>further references:</STRONG> <xsl:apply-templates select="ref"/>
        </P>
      </TD>
    </TR>
    <TR>
      <TD COLSPAN="2"><HR/></TD>
    </TR>
  </xsl:template>

  <xsl:template match="helpdata">
    <P>
      <xsl:apply-templates/>
    </P>
  </xsl:template>

  <xsl:template match="p">
    <P>
      <xsl:apply-templates/>
    </P>
  </xsl:template>

  <xsl:template match="strong">
    <STRONG><xsl:apply-templates/></STRONG>
  </xsl:template>

  <xsl:template match="em">
    <EM><xsl:apply-templates/></EM>
  </xsl:template>

  <xsl:template match="br">
    <BR/>
  </xsl:template>

  <xsl:template match="ul">
    <UL>
      <xsl:apply-templates/>
    </UL>
  </xsl:template>

  <xsl:template match="li">
    <LI><xsl:apply-templates/></LI>
  </xsl:template>

  <xsl:template match="note">
    <TABLE BGCOLOR="yellow" BORDER="1" WIDTH="100%">
      <TR>
        <TD>
          <STRONG>Note: </STRONG> <xsl:apply-templates/>
        </TD>
      </TR>
    </TABLE>
  </xsl:template>

  <xsl:template match="warning">
    <TABLE BGCOLOR="red" BORDER="1" WIDTH="100%">
      <TR>
        <TD>
          <STRONG>Warning: </STRONG> <xsl:apply-templates/>
        </TD>
      </TR>
    </TABLE>
  </xsl:template>

  <xsl:template match="ref">
    <xsl:variable name="href" select="@ref-id"/>
    <xsl:choose>
      <xsl:when test="/USERMODEL/STATEDATA/VAR[@name='helptopic']">
        <A HREF="{$base}/help?session-id={$session-id}&amp;helptopic={$href}"><xsl:value-of select="/USERMODEL/help/helptopic[@id = $href]/@title"/></A>,
      </xsl:when>
      <xsl:otherwise>
        <A HREF="#{$href}"><xsl:value-of select="/USERMODEL/help/helptopic[@id = $href]/@title"/></A>,
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
