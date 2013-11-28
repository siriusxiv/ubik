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
        <TITLE>JWebMail 管理介面：虛擬網域設定</TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
      </HEAD>

      <BODY BGCOLOR="#ffffff">
        <CENTER><H1>JWebMail 虛擬網域設定</H1></CENTER>
        <FORM ACTION="{$base}/admin/domain/set?session-id={$session-id}" METHOD="POST">
          <TABLE WIDTH="100%">
               <tr>
                 <td colspan="2">
                   <xsl:choose>
                       <xsl:when test="/GENERICMODEL/SYSDATA/VIRTUALS/@enabled='true'">
                       Virtual hosts are enabled.
                   <input name="STATE" type="hidden" value="disable"/>
                   Click to disable them.
                   </xsl:when>
                   <xsl:otherwise>
                       Virtual hosts are disabled.
                   <input name="STATE" type="hidden" value="enable"/>
                   Click to enable them.
                   </xsl:otherwise>
                   </xsl:choose>
                 </td>
                 <td>
                   <input name="VIRTUALS" type="submit" value="Change"/>
                 </td>
               </tr>

            <TR BGCOLOR="lightblue">
              <TD><STRONG>網域名稱</STRONG></TD>
              <TD><STRONG>預設主機</STRONG></TD>
              <TD><STRONG>認證主機</STRONG></TD>
              <TD><STRONG>主機
                  存取限制</STRONG></TD>
              <TD><STRONG>被允許的主機</STRONG>
                  （設定『主機存取限制』用）</TD>
              <TD></TD>
            </TR>
            <xsl:for-each select="/GENERICMODEL/SYSDATA/DOMAIN">
              <TR>
                <TD><INPUT type="TEXT" name="{NAME} DOMAIN" value="{NAME}" SIZE="20"/></TD>
                <TD><INPUT type="TEXT" name="{NAME} DEFAULT HOST" value="{DEFAULT_HOST}" SIZE="20"/></TD>
                <TD><INPUT type="TEXT" name="{NAME} AUTH HOST" value="{AUTHENTICATION_HOST}" SIZE="20"/></TD>
                <TD ALIGN="center">
                  <xsl:choose>
                    <xsl:when test="count(RESTRICTED) > 0">
                      <INPUT type="CHECKBOX" checked="checked" name="{NAME} HOST RESTRICTION"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <INPUT type="CHECKBOX" name="{NAME} HOST RESTRICTION"/>
                    </xsl:otherwise>
                  </xsl:choose>
                </TD>
                <TD><INPUT type="TEXT" name="{NAME} ALLOWED HOSTS" value="{ALLOWED_HOST}" SIZE="40"/></TD>
                <TD><INPUT type="submit" name="CHANGE {NAME}" value="修改"/><INPUT type="submit" name="DELETE {NAME}" value="刪除"/></TD>
              </TR>
            </xsl:for-each>

            <TR>
              <TD><INPUT type="TEXT" name="NEW DOMAIN" value="new" SIZE="20"/></TD>
              <TD><INPUT type="TEXT" name="NEW DEFAULT HOST" SIZE="20"/></TD>
              <TD><INPUT type="TEXT" name="NEW AUTH HOST" SIZE="20"/></TD>
              <TD align="center"><INPUT type="CHECKBOX" name="NEW HOST RESTRICTION"/></TD>
              <TD><INPUT type="TEXT" name="NEW ALLOWED HOSTS" SIZE="40"/></TD>
              <TD><INPUT type="submit" name="ADD NEW" value="新增"/></TD>
            </TR>
          </TABLE>
        </FORM>
      </BODY>
    </HTML>
  </xsl:template>
</xsl:stylesheet>
