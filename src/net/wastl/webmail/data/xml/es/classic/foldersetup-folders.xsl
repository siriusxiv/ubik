<?xml version="1.0" encoding="ISO-8859-1"?>
<!--
$Id: foldersetup-folders.xsl 126 2008-10-31 03:41:09Z unsaved $

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
        <TITLE>Buz&#243;n de JWebMail para <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/>: Configuraci&#243;n de carpetas</TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
        <META CONTENT="GENERATOR" VALUE="JWebMail 1.0.1 XSL"/>
      </HEAD>
      <BODY bgcolor="#ffffff">
        <TABLE BGCOLOR="#dddddd" CELLSPACING="0" BORDER="0" WIDTH="100%">
          <TR>
            <TD VALIGN="CENTER">
              <IMG SRC="{$imgbase}/images/btn-folders.png"/>
            </TD>
            <TD VALIGN="CENTER" COLSPAN="2"><FONT SIZE="+2"><STRONG>Configuraci&#243;n de carpetas de JWebMail para <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/></STRONG></FONT> (<A HREF="{$base}/help?session-id={$session-id}&amp;helptopic=folder-setup-folders">Ayuda</A>)<BR/><EM>Nombre de usuario <xsl:value-of select="normalize-space(/USERMODEL/USERDATA/LOGIN)"/></EM><BR/><EM>La cuenta existe desde <xsl:apply-templates select="/USERMODEL/STATEDATA/VAR[@name='first login']"/></EM></TD>
          </TR>
        </TABLE>
        <TABLE WIDTH="100%">
          <xsl:for-each select="/USERMODEL/MAILHOST_MODEL">
            <xsl:apply-templates select="."/>
          </xsl:for-each>
        </TABLE>
        <P>
          Las carpetas mostradas en  <STRONG>negrita</STRONG> pueden contener a su vez otras carpetas, las carpetas mostradas en texto normal no. Las carpetas mostradas en <EM>cursiva</EM> est&#225;n ocultas (en la vista principal del buz&#243;n), las otras no.
        </P>
        <P><FONT color="red"><STRONG>&#161;Advertencia!</STRONG></FONT> &#161;Si borras una carpeta, todos los mensajes (y subcarpetas) en ella ser&#225;n <FONT color="red">borrados</FONT>, no s&#243;lo de JWebMail sino tambi&#233;n <FONT color="red">f&#237;sicamente del servidor de correo</FONT>! &#161;Esto es peligroso y no puede deshacerse!
        </P>
      </BODY>
    </HTML>
  </xsl:template>
  <xsl:template match="/USERMODEL/USERDATA/INTVAR">
    <xsl:value-of select="@value"/>
  </xsl:template>
  <xsl:template match="/USERMODEL/STATEDATA/VAR">
    <xsl:value-of select="@value"/>
  </xsl:template>
  <xsl:template match="/USERMODEL/MAILHOST_MODEL">
    <TR BGCOLOR="#dddddd">
      <TD COLSPAN="{/USERMODEL/STATEDATA/VAR[@name='max folder depth']/@value}" WIDTH="50%">
        <STRONG>
          <FONT COLOR="green">
            <xsl:value-of select="@name"/>
          </FONT>
        </STRONG>
      </TD>
      <TD WIDTH="50%"><STRONG>Host</STRONG>: <xsl:value-of select="@url"/></TD>
    </TR>
    <xsl:for-each select="FOLDER">
      <xsl:apply-templates select="."/>
    </xsl:for-each>
  </xsl:template>
  <xsl:template match="FOLDER">
    <xsl:variable name="level" select="count(ancestor::FOLDER)"/>
    <TR>
      <xsl:call-template name="recurse folder">
        <xsl:with-param name="level" select="$level"/>
      </xsl:call-template>
      <TD>
        <IMG SRC="{$imgbase}/images/icon-folder.png"/>
      </TD>
      <xsl:choose>
        <xsl:when test="@holds_folders = 'true'">
          <TD COLSPAN="{/USERMODEL/STATEDATA/VAR[@name='max folder depth']/@value - $level - 1}">
            <xsl:choose>
              <xsl:when test="@subscribed = 'true'">
                <STRONG>
                  <xsl:value-of select="@name"/>
                </STRONG>
              </xsl:when>
              <xsl:otherwise>
                <EM>
                  <STRONG>
                    <xsl:value-of select="@name"/>
                  </STRONG>
                </EM>
              </xsl:otherwise>
            </xsl:choose>
          </TD>
          <TD><A HREF="{$base}/folder/setup?session-id={$session-id}&amp;method=folderadd&amp;addto={@id}">A&#241;adir subcarpeta</A> - <A HREF="{$base}/folder/setup?session-id={$session-id}&amp;method=folder&amp;remove={@id}&amp;recurse=1">Borrar esta carpeta (y todas las subcarpetas)</A> -
            <xsl:choose><xsl:when test="@subscribed = 'true'"><A HREF="{$base}/folder/setup?session-id={$session-id}&amp;method=folder&amp;hide={@id}&amp;">Ocultar</A></xsl:when><xsl:otherwise><A HREF="{$base}/folder/setup?session-id={$session-id}&amp;method=folder&amp;unhide={@id}&amp;">Mostrar</A></xsl:otherwise></xsl:choose></TD>
        </xsl:when>
        <xsl:otherwise>
          <TD COLSPAN="{/USERMODEL/STATEDATA/VAR[@name='max folder depth']/@value - $level - 1}">
            <xsl:choose>
              <xsl:when test="@subscribed = 'true'">
                <xsl:value-of select="@name"/>
              </xsl:when>
              <xsl:otherwise>
                <EM>
                  <xsl:value-of select="@name"/>
                </EM>
              </xsl:otherwise>
            </xsl:choose>
          </TD>
          <TD><A HREF="{$base}/folder/setup?session-id={$session-id}&amp;method=folder&amp;remove={@id}">Borrar esta carpeta</A> -
            <xsl:choose><xsl:when test="@subscribed = 'true'"><A HREF="{$base}/folder/setup?session-id={$session-id}&amp;method=folder&amp;hide={@id}&amp;">Ocultar</A></xsl:when><xsl:otherwise><A HREF="{$base}/folder/setup?session-id={$session-id}&amp;method=folder&amp;unhide={@id}&amp;">Mostrar</A></xsl:otherwise></xsl:choose></TD>
        </xsl:otherwise>
      </xsl:choose>
    </TR>
    <xsl:for-each select="FOLDER">
      <xsl:apply-templates select="."/>
    </xsl:for-each>
  </xsl:template>
<!-- Create an appropriate number of <TD></TD> before a folder, depending on the level -->
  <xsl:template name="recurse folder">
    <xsl:if test="$level&gt;0">
      <TD/>
      <xsl:variable name="levelneu" select="$level - 1"/>
      <xsl:call-template name="recurse folder">
        <xsl:with-param name="level" select="$levelneu"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
