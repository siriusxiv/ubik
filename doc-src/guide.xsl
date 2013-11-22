<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
$Id: guide.xsl 140 2008-10-31 21:49:50Z unsaved $

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

  <xsl:template match="GUIDE">

    <HTML>
      <HEAD>
        <TITLE><xsl:apply-templates select="TITLE"/></TITLE>
        <META CONTENT="AUTHOR" VALUE="{AUTHOR}"/>
      </HEAD>
      <BODY BGCOLOR="#ffffff">
        <CENTER><H1>User's Guide for <xsl:value-of select="TITLE"/></H1></CENTER>
        <CENTER><H4><xsl:value-of select="REVISION"/></H4></CENTER>
        <xsl:apply-templates select="SECTION"/>
      </BODY>
    </HTML>
  </xsl:template>

  <xsl:template match="SECTION">
    <A NAME="{@id}"/>
    <xsl:variable name="cur_section" select="position()"/>
     <TABLE WIDTH="100%" BGCOLOR="#a0d895" CELLSPACING="0" BORDER="0">
        <TR>
          <TD>
            <H1><CENTER><xsl:number value="position()" format="I"/>. <xsl:value-of select="@name"/></CENTER></H1>
          </TD>
        </TR>
     </TABLE>
    <xsl:apply-templates select="*[self::NOTE or self::P or self::CONTENTS]"/>
    <xsl:apply-templates select="SUBSECTION">
      <xsl:with-param name="cur_section" select="$cur_section"/>
    </xsl:apply-templates>
     <A HREF="#{@id}">Top of section "<xsl:value-of select="@name"/>"</A>
   </xsl:template>

  <xsl:template match="SUBSECTION">
    <xsl:param name="cur_section"/>
    <A NAME="{@id}"/>
    <xsl:variable name="cur_subsection" select="position()"/>
    <H2>
      <xsl:number value="$cur_section"/>.<xsl:number value="position()"/>. <xsl:value-of select="@name"/>
    </H2>
    <xsl:apply-templates select="*[self::NOTE or self::P]"/>
    <xsl:apply-templates select="SUBSUBSECTION">
      <xsl:with-param name="cur_section" select="$cur_section"/>
      <xsl:with-param name="cur_subsection" select="$cur_subsection"/>
    </xsl:apply-templates>
    <A HREF="#{@id}">Top of section "<xsl:value-of select="@name"/>"</A>
    <HR/>
  </xsl:template>

  <xsl:template match="SUBSUBSECTION">
    <xsl:param name="cur_section"/>
    <xsl:param name="cur_subsection"/>
    <A NAME="{@id}"/>
    <H3><xsl:number value="$cur_section"/>.<xsl:number value="$cur_subsection"/>.<xsl:number value="position()"/>. <xsl:value-of select="@name"/></H3>
    <xsl:apply-templates/>
    <A HREF="#{@id}">Top of section "<xsl:value-of select="@name"/>"</A>
  </xsl:template>

   <xsl:template match="CONTENTS">
     <OL>
       <xsl:for-each select="../../SECTION">
         <LI><A HREF="#{@id}"><xsl:value-of select="@name"/></A></LI>
       </xsl:for-each>
     </OL>
   </xsl:template>

   <xsl:template match="P">
     <P>
       <xsl:apply-templates/>
     </P>
   </xsl:template>

   <xsl:template match="STRONG">
     <STRONG>
       <xsl:apply-templates/>
     </STRONG>
   </xsl:template>

   <xsl:template match="EM">
     <I>
       <xsl:apply-templates/>
     </I>
   </xsl:template>

   <xsl:template match="CODE">
     <CODE>
       <xsl:apply-templates/>
     </CODE>
   </xsl:template>

   <xsl:template match="NOTE">
     <CENTER>
       <TABLE WIDTH="80%" BGCOLOR="#eae723" BORDER="1">
         <TR>
           <TD><B>Note:</B> <xsl:apply-templates/></TD>
         </TR>
       </TABLE>
     </CENTER>
   </xsl:template>

   <xsl:template match="LIST">
     <UL>
       <xsl:apply-templates/>
     </UL>
   </xsl:template>

   <xsl:template match="NLIST">
     <OL>
       <xsl:apply-templates/>
     </OL>
   </xsl:template>

   <xsl:template match="ITEM">
     <LI><xsl:apply-templates/></LI>
   </xsl:template>

   <xsl:template match="A">
     <A HREF="{@href}"><xsl:apply-templates/></A>
   </xsl:template>

   <xsl:template match="BR">
     <BR/>
   </xsl:template>

   <xsl:template match="TABLE">
     <TABLE>
       <xsl:apply-templates/>
     </TABLE>
   </xsl:template>

   <xsl:template match="TR">
     <TR>
       <xsl:apply-templates/>
     </TR>
   </xsl:template>

   <xsl:template match="TD">
     <TD>
       <xsl:apply-templates/>
     </TD>
   </xsl:template>
</xsl:stylesheet>
