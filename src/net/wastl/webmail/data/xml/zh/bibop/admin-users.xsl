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
  <xsl:output method="html" indent="yes" xalan:content-handler="org.apache.xml.serializer.ToHTMLStream"/>

    <xsl:variable name="imgbase" select="/GENERICMODEL/STATEDATA/VAR[@name='img base uri']/@value"/>
    <xsl:variable name="base" select="/GENERICMODEL/STATEDATA/VAR[@name='base uri']/@value"/>
    <xsl:variable name="session-id" select="/GENERICMODEL/STATEDATA/VAR[@name='session id']/@value"/>

    <xsl:template match="/">

    <HTML>
      <HEAD>
        <TITLE>JWebMail 管理介面：使用者設定</TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
      </HEAD>

      <BODY BGCOLOR="white">
        <TABLE WIDTH="100%" CELLSPACING="0" CELLPADDING="0">
          <TR bgcolor="#dddddd">
            <TD COLSPAN="2" ALIGN="center"><FONT SIZE="+1"><STRONG>選擇網域</STRONG></FONT></TD>
          </TR>

          <TR>
            <FORM ACTION="{$base}/admin/user?session-id={$session-id}" METHOD="POST">
              <TD WIDTH="70%">
                <SELECT NAME="domain">
                  <xsl:for-each select="/GENERICMODEL/SYSDATA/DOMAIN">
                    <xsl:choose>
                      <xsl:when test="NAME = /GENERICMODEL/STATEDATA/VAR[@name = 'selected domain']/@value">
                        <OPTION selected="selected"><xsl:value-of select="NAME"/></OPTION>
                      </xsl:when>
                      <xsl:otherwise>
                        <OPTION><xsl:value-of select="NAME"/></OPTION>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:for-each>
                </SELECT>
              </TD>
              <TD>
                <INPUT TYPE="submit" name="submit" value="選擇"/>
              </TD>
            </FORM>
          </TR>

          <xsl:if test="/GENERICMODEL/STATEDATA/VAR[@name = 'selected domain']/@value !='' and count(/GENERICMODEL/STATEDATA/VAR[@name = 'user']) > 0">
            <TR bgcolor="#dddddd">
              <TD COLSPAN="2" ALIGN="center">
                <FONT SIZE="+1"><STRONG>網域： <xsl:value-of select="/GENERICMODEL/STATEDATA/VAR[@name = 'selected domain']/@value"/> 內的使用者</STRONG></FONT>
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
                <TD><INPUT TYPE="text" SIZE="20" NAME="user"/></TD>
                <TD><INPUT TYPE="submit" name="edit" value="新增"/></TD>
              </FORM>
            </TR>
          </xsl:if>
        </TABLE>
      </BODY>
    </HTML>
  </xsl:template>

  <xsl:template match="VAR">
    <FORM ACTION="{$base}/admin/user/edit?session-id={$session-id}&amp;user={@value}&amp;domain={/GENERICMODEL/STATEDATA/VAR[@name = 'selected domain']/@value}" METHOD="POST">
      <TD><STRONG><xsl:value-of select="@value"/></STRONG></TD>
      <TD>
        <INPUT TYPE="submit" name="edit" value="編輯"/>
        <INPUT TYPE="submit" name="delete" value="刪除"/>
      </TD>
    </FORM>
  </xsl:template>
</xsl:stylesheet>
