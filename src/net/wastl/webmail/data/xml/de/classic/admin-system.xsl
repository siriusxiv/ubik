<?xml version="1.0" encoding="UTF-8"?>
<!--
$Id: admin-system.xsl 126 2008-10-31 03:41:09Z unsaved $

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

    <xsl:variable name="imgbase" select="/GENERICMODEL/STATEDATA/VAR[@name='img base uri']/@value"/>
    <xsl:variable name="base" select="/GENERICMODEL/STATEDATA/VAR[@name='base uri']/@value"/>
    <xsl:variable name="session-id" select="/GENERICMODEL/STATEDATA/VAR[@name='session id']/@value"/>

    <xsl:template match="/">

    <HTML>
      <HEAD>
        <TITLE>JWebMail Administration Interface: System configuration</TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
      </HEAD>

      <BODY BGCOLOR="white">
        <FORM ACTION="{$base}/admin/system/set?session-id={$session-id}" METHOD="POST">
          <TABLE WIDTH="100%" CELLSPACING="0" CELLPADDING="0">
            <xsl:for-each select="/GENERICMODEL/SYSDATA/GROUP">
              <TR bgcolor="#dddddd">
                <TD COLSPAN="2" ALIGN="center"><FONT SIZE="+1"><STRONG>Group: <xsl:value-of select="@name"/></STRONG></FONT></TD>
              </TR>
              <xsl:for-each select="CONFIG">
                <xsl:sort select="KEY" order="ascending"/>
                <xsl:choose>
                  <xsl:when test="position() mod 2 = 1">
                    <TR bgcolor="#f7f3a8">
                      <xsl:call-template name="row"/>
                    </TR>
                  </xsl:when>
                  <xsl:otherwise>
                    <TR>
                      <xsl:call-template name="row"/>
                    </TR>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:for-each>
            </xsl:for-each>
            <TR bgcolor="#dddddd">
              <TD COLSPAN="2" ALIGN="center"><FONT SIZE="+1"><STRONG>Form</STRONG></FONT></TD>
            </TR>
            <TR>
              <TD COLSPAN="2" ALIGN="center">
                <INPUT TYPE="submit" name="submit" value="Activate Changes"/>
                <INPUT TYPE="reset" name="reset" value="Undo Changes"/>
              </TD>
            </TR>
          </TABLE>
        </FORM>
      </BODY>
    </HTML>
  </xsl:template>

  <xsl:template name="row">
    <TD>
      <STRONG><xsl:value-of select="KEY"/></STRONG><BR/>
      <EM><xsl:apply-templates select="DESCRIPTION"/></EM>
    </TD>
    <TD>
      <xsl:choose>
        <xsl:when test="@type = 'choice'">
          <SELECT name="{KEY}">
            <xsl:for-each select="CHOICE">
              <xsl:choose>
                <xsl:when test=". = ../VALUE">
                  <OPTION selected="selected"><xsl:value-of select="."/></OPTION>
                </xsl:when>
                <xsl:otherwise>
                  <OPTION><xsl:value-of select="."/></OPTION>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:for-each>
          </SELECT>
        </xsl:when>
        <xsl:when test="@type = 'bool'">
          <SELECT name="{KEY}">
            <xsl:for-each select="CHOICE">
              <xsl:choose>
                <xsl:when test=". = ../VALUE">
                  <OPTION selected="selected"><xsl:value-of select="."/></OPTION>
                </xsl:when>
                <xsl:otherwise>
                  <OPTION><xsl:value-of select="."/></OPTION>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:for-each>
          </SELECT>
        </xsl:when>
        <xsl:when test="@type = 'int'">
          <INPUT type="text" name="{KEY}" SIZE="6" value="{VALUE}"/>
        </xsl:when>
        <xsl:otherwise>
          <INPUT type="text" name="{KEY}" SIZE="40" value="{VALUE}"/>
        </xsl:otherwise>
      </xsl:choose>
    </TD>
  </xsl:template>
</xsl:stylesheet>
