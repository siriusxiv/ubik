<?xml version="1.0" encoding="ISO-8859-1"?>
<!--
$Id: messagelist.xsl 126 2008-10-31 03:41:09Z unsaved $

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
  <xsl:variable name="imgbase" select="/USERMODEL/STATEDATA/VAR[@name='img base uri']/@value"/>
  <xsl:variable name="base" select="/USERMODEL/STATEDATA/VAR[@name='base uri']/@value"/>
  <xsl:variable name="session-id" select="/USERMODEL/STATEDATA/VAR[@name='session id']/@value"/>
  <xsl:template match="/">
    <HTML>
      <HEAD>
        <TITLE>Buz&#243;n de JWebMail para <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/>: Lista de mensajes (Carpeta <xsl:value-of select="/USERMODEL/CURRENT[@type='folder']/@id"/></TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
      </HEAD>
      <BODY bgcolor="#ffffff">
        <xsl:variable name="current" select="/USERMODEL/CURRENT[@type='folder']/@id"/>
        <xsl:apply-templates select="/USERMODEL/MAILHOST_MODEL//FOLDER[@id=$current]"/>
      </BODY>
    </HTML>
  </xsl:template>
  <xsl:template match="FOLDER"><H3>
      Mostrando mensajes <xsl:value-of select="/USERMODEL/CURRENT[@type='folder']/@first_msg"/>
      a <xsl:value-of select="/USERMODEL/CURRENT[@type='folder']/@last_msg"/>
      en carpeta <xsl:value-of select="@name"/> (<A HREF="{$base}/help?session-id={$session-id}&amp;helptopic=messagelist">Ayuda</A>).
    </H3><xsl:call-template name="navigation"/><HR/><xsl:apply-templates select="MESSAGELIST"/><STRONG>Marcas del mensaje:</STRONG><IMG SRC="{$imgbase}/images/icon-attachment.gif" BORDER="0"/> mensaje con adjuntos
    <IMG SRC="{$imgbase}/images/icon-new.gif" BORDER="0"/> nuevo mensaje
    <IMG SRC="{$imgbase}/images/icon-seen.gif" BORDER="0"/> mensaje visto
    <IMG SRC="{$imgbase}/images/icon-answered.gif" BORDER="0"/> mensaje respondido
    <IMG SRC="{$imgbase}/images/icon-deleted.gif" BORDER="0"/> mensaje borrado
    <HR/><xsl:call-template name="navigation"/></xsl:template>
  <xsl:template match="MESSAGELIST">
    <FORM ACTION="{$base}/folder/list?flag=1&amp;session-id={$session-id}&amp;folder-id={../@id}&amp;part={/USERMODEL/CURRENT[@type='folder']/@list_part}" METHOD="POST">
      <TABLE WIDTH="100%" BORDER="0">
        <TR>
          <TD WIDTH="3%" BGCOLOR="#dddddd">
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
          </TD>
          <TD WIDTH="3%" BGCOLOR="#dddddd">
            <STRONG>Nr</STRONG>
          </TD>
          <TD WIDTH="5%" BGCOLOR="#dddddd">
            <STRONG>Banderas</STRONG>
          </TD>
          <TD WIDTH="35%" BGCOLOR="#dddddd">
            <STRONG>Asunto</STRONG>
          </TD>
          <TD WIDTH="24%" BGCOLOR="#dddddd">
            <STRONG>Remitente</STRONG>
          </TD>
          <TD WIDTH="24%" BGCOLOR="#dddddd">
            <STRONG>Fecha</STRONG>
          </TD>
          <TD WIDTH="6%" BGCOLOR="#dddddd">
            <STRONG>Tama&#241;o</STRONG>
          </TD>
        </TR>
        <xsl:for-each select="MESSAGE[number(@msgnr) &gt;= number(/USERMODEL/CURRENT[@type='folder']/@first_msg) and number(@msgnr) &lt;= number(/USERMODEL/CURRENT[@type='folder']/@last_msg)]">
          <xsl:sort select="@msgnr" data-type="number" order="descending"/>
          <!--
          <xsl:variable name="bgcolor" select="#ffffff"/>
          -->
          <xsl:choose>
            <xsl:when test="@msgnr mod 2 = 1">
              <TR bgcolor="#f7f3a8">
                <xsl:call-template name="headerrow"/>
              </TR>
            </xsl:when>
            <xsl:otherwise>
              <TR>
                <xsl:call-template name="headerrow"/>
              </TR>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:for-each>
      </TABLE>
      <TABLE WIDTH="100%" BGCOLOR="#dddddd" CELLSPACING="0" BORDER="0">
        <TR>
          <FONT SIZE="-1">
            <TD>
              <SELECT NAME="MARK">
                <OPTION VALUE="MARK">poner</OPTION>
                <OPTION VALUE="UNMARK">quitar</OPTION>
              </SELECT>
              <STRONG>marca de</STRONG>
              <SELECT NAME="MESSAGE FLAG">
                <OPTION VALUE="DELETED">borrado</OPTION>
                <OPTION VALUE="SEEN">visto</OPTION>
                <OPTION VALUE="ANSWERED">respondido</OPTION>
                <OPTION VALUE="RECENT">reciente</OPTION>
                <OPTION VALUE="DRAFT">borrador</OPTION>
              </SELECT>
              <INPUT TYPE="SUBMIT" NAME="flagmsgs" VALUE="Perform!"/>
            </TD>
            <TD>
              <SELECT NAME="COPYMOVE">
                <OPTION VALUE="COPY">copiar</OPTION>
                <OPTION VALUE="MOVE">mover</OPTION>
              </SELECT>
              <STRONG>mensajes a la carpeta</STRONG>
              <SELECT NAME="TO">
                <xsl:for-each select="/USERMODEL/MAILHOST_MODEL//FOLDER">
                  <OPTION value="{@id}">
                    <xsl:value-of select="@name"/>
                  </OPTION>
                </xsl:for-each>
              </SELECT>
              <INPUT TYPE="SUBMIT" NAME="copymovemsgs" VALUE="Perform!"/>
            </TD>
          </FONT>
        </TR>
      </TABLE>
    </FORM>
  </xsl:template>
  <xsl:template name="navigation">
    <P>
      <TABLE WIDTH="100%">
        <TR>
          <TD ALIGN="left" VALIGN="center">
            <EM>
              <xsl:choose>
                <xsl:when test="number(/USERMODEL/CURRENT[@type='folder']/@first_msg) &gt; number(1)">
                  <A HREF="{$base}/folder/list?session-id={$session-id}&amp;folder-id={@id}&amp;part={/USERMODEL/CURRENT[@type='folder']/@list_part + 1}"><IMG SRC="{$imgbase}/images/arrow-left.png" BORDER="0"/> Anteriores <xsl:value-of select="/USERMODEL/USERDATA/INTVAR[@name='max show messages']/@value"/> mensajes</A>
                </xsl:when>
                <xsl:otherwise><IMG SRC="{$imgbase}/images/arrow-left-disabled.png" BORDER="0"/> Anteriores <xsl:value-of select="/USERMODEL/USERDATA/INTVAR[@name='max show messages']/@value"/> mensajes
                </xsl:otherwise>
              </xsl:choose>
            </EM>
          </TD>
          <TD ALIGN="right" VALIGN="center">
            <EM>
              <xsl:choose>
                <xsl:when test="number(/USERMODEL/CURRENT[@type='folder']/@list_part) &gt; number(1)">
                  <A HREF="{$base}/folder/list?session-id={$session-id}&amp;folder-id={@id}&amp;part={/USERMODEL/CURRENT[@type='folder']/@list_part - 1}">Siguientes <xsl:value-of select="/USERMODEL/USERDATA/INTVAR[@name='max show messages']/@value"/> mensajes <IMG SRC="{$imgbase}/images/arrow-right.png" BORDER="0"/></A>
                </xsl:when>
                <xsl:otherwise>
                   Siguientes <xsl:value-of select="/USERMODEL/USERDATA/INTVAR[@name='max show messages']/@value"/> mensajes <IMG SRC="{$imgbase}/images/arrow-right-disabled.png" BORDER="0"/>
                </xsl:otherwise>
              </xsl:choose>
            </EM>
          </TD>
        </TR>
      </TABLE>
    </P>
  </xsl:template>
  <xsl:template name="headerrow">
    <TD>
      <INPUT TYPE="checkbox" NAME="CH%{@msgnr}"/>
    </TD>
    <TD>
      <xsl:value-of select="@msgnr"/>
    </TD>
    <TD>
      <xsl:if test="@attachment='true'">
        <IMG SRC="{$imgbase}/images/icon-attachment.gif" BORDER="0"/>
      </xsl:if>
      <xsl:if test="@recent='true'">
        <IMG SRC="{$imgbase}/images/icon-new.gif" BORDER="0"/>
      </xsl:if>
      <xsl:if test="@seen='true'">
        <IMG SRC="{$imgbase}/images/icon-seen.gif" BORDER="0"/>
      </xsl:if>
      <xsl:if test="@answered='true'">
        <IMG SRC="{$imgbase}/images/icon-answered.gif" BORDER="0"/>
      </xsl:if>
      <xsl:if test="@deleted='true'">
        <IMG SRC="{$imgbase}/images/icon-deleted.gif" BORDER="0"/>
      </xsl:if>
    </TD>
    <TD>
      <A HREF="{$base}/folder/showmsg?session-id={$session-id}&amp;folder-id={/USERMODEL/CURRENT[@type='folder']/@id}&amp;message-nr={@msgnr}">
        <xsl:apply-templates select="HEADER/SUBJECT"/>
      </A>
    </TD>
    <TD>
      <xsl:apply-templates select="HEADER/FROM"/>
    </TD>
    <TD>
      <xsl:apply-templates select="HEADER/DATE"/>
    </TD>
    <TD>
      <FONT SIZE="-1">
        <xsl:value-of select="@size"/>
      </FONT>
    </TD>
  </xsl:template>
  <xsl:template match="DATE">
    <FONT SIZE="-1">
      <xsl:value-of select="."/>
    </FONT>
  </xsl:template>
  <xsl:template match="SUBJECT">
    <FONT SIZE="-1">
      <xsl:value-of select="."/>
    </FONT>
  </xsl:template>
  <xsl:template match="FROM">
    <FONT SIZE="-1">
      <xsl:choose>
        <xsl:when test="contains(.,'&lt;')">
          <xsl:value-of select="substring-before(.,'&lt;')"/>
          <BR/>
          <STRONG>Correo:</STRONG>
          <xsl:value-of select="substring-before(substring-after(.,'&lt;'),'&gt;')"/>
        </xsl:when>
        <xsl:otherwise>
          <BR/>
          <STRONG>Correo:</STRONG>
          <xsl:value-of select="."/>
        </xsl:otherwise>
      </xsl:choose>
    </FONT>
  </xsl:template>
</xsl:stylesheet>
