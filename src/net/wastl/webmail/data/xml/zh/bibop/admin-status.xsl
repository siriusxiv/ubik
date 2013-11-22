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
        <TITLE>JWebMail 管理介面：系統狀態</TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
      </HEAD>

      <BODY BGCOLOR="white">
        <TABLE WIDTH="100%" CELLSPACING="0" CELLPADDING="0">
          <TR bgcolor="#dddddd">
            <TD COLSPAN="3" ALIGN="center"><FONT SIZE="+1"><STRONG>執行中的伺服器執行緒：</STRONG></FONT></TD>
          </TR>
          <xsl:if test='/GENERICMODEL/STATEDATA/VAR[@name="http server status"]/@value != ""'>
            <TR>
              <TD><STRONG>HTTP 伺服器</STRONG></TD>
              <TD COLSPAN="2"><PRE><xsl:value-of select='/GENERICMODEL/STATEDATA/VAR[@name="http server status"]/@value'/></PRE></TD>
            </TR>
          </xsl:if>
          <xsl:if test='/GENERICMODEL/STATEDATA/VAR[@name="ssl server status"]/@value != ""'>
            <TR>
              <TD><STRONG>SSL 伺服器</STRONG></TD>
              <TD COLSPAN="2"><PRE><xsl:value-of select='/GENERICMODEL/STATEDATA/VAR[@name="ssl server status"]/@value'/></PRE></TD>
            </TR>
          </xsl:if>
          <xsl:if test='/GENERICMODEL/STATEDATA/VAR[@name="servlet status"]/@value != ""'>
            <TR>
              <TD><STRONG>Servlet</STRONG></TD>
              <TD COLSPAN="2"><PRE><xsl:value-of select='/GENERICMODEL/STATEDATA/VAR[@name="servlet status"]/@value'/></PRE></TD>
            </TR>
          </xsl:if>
          <xsl:if test='/GENERICMODEL/STATEDATA/VAR[@name="storage status"]/@value != ""'>
            <TR>
              <TD><STRONG>Storage</STRONG></TD>
              <TD COLSPAN="2"><PRE><xsl:value-of select='/GENERICMODEL/STATEDATA/VAR[@name="storage status"]/@value'/></PRE></TD>
            </TR>
          </xsl:if>

          <TR bgcolor="#dddddd">
            <TD COLSPAN="3" ALIGN="center"><FONT SIZE="+1"><STRONG>執行中的連線</STRONG></FONT></TD>
          </TR>

          <xsl:for-each select="/GENERICMODEL/STATEDATA/SESSION">
            <xsl:sort select="@type" order="ascending"/>
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

          <TR bgcolor="#dddddd">
            <TD COLSPAN="3" ALIGN="center"><FONT SIZE="+1"><STRONG>關閉系統/重新啟動</STRONG></FONT></TD>
          </TR>
          <TR>
            <FORM ACTION="{$base}/admin/control?session-id={$session-id}" METHOD="POST">
              <TD><STRONG>關閉系統/重新啟動</STRONG></TD>
              <TD>於 <INPUT TYPE="text" SIZE="4" NAME="SHUTDOWN SECONDS" VALUE="0"/> 秒鐘之後</TD>
              <TD><INPUT TYPE="submit" name="REBOOT" value="重新啟動"/><INPUT TYPE="submit" name="SHUTDOWN" value="關閉系統"/></TD>
            </FORM>
          </TR>
        </TABLE>
      </BODY>
    </HTML>
  </xsl:template>

  <xsl:template match="SESSION">
    <FORM ACTION="{$base}/admin/control/kill?session-id={$session-id}&amp;kill={SESS_CODE}" METHOD="POST">
      <TD>
        <xsl:choose>
          <xsl:when test="@type = 'admin'">
            <STRONG>管理員的連線</STRONG>
          </xsl:when>
          <xsl:otherwise>
            <STRONG>使用者的連線</STRONG> （使用者： <xsl:value-of select="SESS_USER"/>）
          </xsl:otherwise>
        </xsl:choose>
      </TD>
      <TD>
        <STRONG>遠端位址：</STRONG> <xsl:value-of select="SESS_ADDRESS"/>, <STRONG>閒置時間： </STRONG> <xsl:value-of select="VAR[@name='idle time']/@value"/><BR/>
        <xsl:if test="count(SESS_CONN) > 0">
          <STRONG>active mail connections:</STRONG><BR/>
          <UL>
            <xsl:for-each select="SESS_CONN">
              <LI><EM><xsl:value-of select="."/></EM></LI>
            </xsl:for-each>
          </UL>
        </xsl:if>
      </TD>
      <TD>
        <INPUT TYPE="submit" NAME="submit" VALUE="踢出系統"/>
      </TD>
    </FORM>
  </xsl:template>
</xsl:stylesheet>
