<?xml version="1.0" encoding="UTF-8"?>
<!--
$Id: admin-users.xsl 19 2008-10-20 22:49:25Z unsaved $

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
  <xsl:variable name="imgbase" select="/GENERICMODEL/STATEDATA/VAR[@name='img base uri']/@value"/>
  <xsl:variable name="base" select="/GENERICMODEL/STATEDATA/VAR[@name='base uri']/@value"/>
  <xsl:variable name="session-id" select="/GENERICMODEL/STATEDATA/VAR[@name='session id']/@value"/>
  <xsl:template match="/">
    <HTML>
      <HEAD>
        <TITLE>Interfaz de administraci&#243;n de WeMail: Configurar usuario</TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
      </HEAD>
      <BODY BGCOLOR="white">
        <TABLE WIDTH="100%" CELLSPACING="0" CELLPADDING="0">
          <TR bgcolor="#dddddd">
            <TD COLSPAN="2" ALIGN="center">
              <FONT SIZE="+1">
                <STRONG>Seleccionar dominio</STRONG>
              </FONT>
            </TD>
          </TR>
          <TR>
            <FORM ACTION="{$base}/admin/user?session-id={$session-id}" METHOD="POST">
              <TD WIDTH="70%">
                <SELECT NAME="domain">
                  <xsl:for-each select="/GENERICMODEL/SYSDATA/DOMAIN">
                    <xsl:choose>
                      <xsl:when test="NAME = /GENERICMODEL/STATEDATA/VAR[@name = 'selected domain']/@value">
                        <OPTION selected="selected">
                          <xsl:value-of select="NAME"/>
                        </OPTION>
                      </xsl:when>
                      <xsl:otherwise>
                        <OPTION>
                          <xsl:value-of select="NAME"/>
                        </OPTION>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:for-each>
                </SELECT>
              </TD>
              <TD>
                <INPUT TYPE="submit" name="submit" value="Select"/>
              </TD>
            </FORM>
          </TR>
          <xsl:if test="/GENERICMODEL/STATEDATA/VAR[@name = 'selected domain']/@value !='' and count(/GENERICMODEL/STATEDATA/VAR[@name = 'user']) &gt; 0">
            <TR bgcolor="#dddddd">
              <TD COLSPAN="2" ALIGN="center">
                <FONT SIZE="+1">
                  <STRONG>Usuarios en el dominio <xsl:value-of select="/GENERICMODEL/STATEDATA/VAR[@name = 'selected domain']/@value"/></STRONG>
                </FONT>
              </TD>
            </TR>
            <xsl:for-each select="/GENERICMODEL/STATEDATA/VAR[@name = 'user']">
              <xsl:sort select="@value" order="ascending"/>
              <xsl:choose>
                <xsl:when test="position() mod 2 = 1">
                  <TR bgcolor="#f7f3a8">
                    <xsl:apply-templates select="."/>
                  </TR>
                </xsl:when>
                <xsl:otherwise>
                  <TR>
                    <xsl:apply-templates select="."/>
                  </TR>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:for-each>
            <TR bgcolor="lightblue">
              <FORM ACTION="{$base}/admin/user/edit?session-id={$session-id}&amp;domain={/GENERICMODEL/STATEDATA/VAR[@name = 'selected domain']/@value}" METHOD="POST">
                <TD>
                  <INPUT TYPE="text" SIZE="20" NAME="user"/>
                </TD>
                <TD>
                  <INPUT TYPE="submit" name="edit" value="Create"/>
                </TD>
              </FORM>
            </TR>
          </xsl:if>
        </TABLE>
      </BODY>
    </HTML>
  </xsl:template>
  <xsl:template match="VAR">
    <FORM ACTION="{$base}/admin/user/edit?session-id={$session-id}&amp;user={@value}&amp;domain={/GENERICMODEL/STATEDATA/VAR[@name = 'selected domain']/@value}" METHOD="POST">
      <TD>
        <STRONG>
          <xsl:value-of select="@value"/>
        </STRONG>
      </TD>
      <TD>
        <INPUT TYPE="submit" name="edit" value="Edit"/>
        <INPUT TYPE="submit" name="delete" value="Delete"/>
      </TD>
    </FORM>
  </xsl:template>
</xsl:stylesheet>
