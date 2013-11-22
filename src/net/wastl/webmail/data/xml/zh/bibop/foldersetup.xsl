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

    <xsl:variable name="imgbase" select="/USERMODEL/STATEDATA/VAR[@name='img base uri']/@value"/>
    <xsl:variable name="base" select="/USERMODEL/STATEDATA/VAR[@name='base uri']/@value"/>
    <xsl:variable name="session-id" select="/USERMODEL/STATEDATA/VAR[@name='session id']/@value"/>

    <xsl:template match="/">

    <HTML>
      <HEAD>
        <TITLE> <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/> 的 JWebMail 信箱：收信匣設定</TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
        <META CONTENT="GENERATOR" VALUE="JWebMail 1.0.1 XSL"/>
        <link rel="stylesheet" href="{$base}/passthrough/webmail.css"/>
      </HEAD>

      <BODY bgcolor="#B5C1CF" topmargin="5" leftmargin="0" marginwidth="0" marginheight="5">

        <TABLE width="100%" border="0" cellspacing="2" cellpadding="4">
          <TR>
            <TD colspan="2" height="22" class="testoNero">
              <IMG SRC="{$imgbase}/images/icona_folder.gif" align="absmiddle"/>
            <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/> 的 JWebMail 收信匣設定 （<A HREF="{$base}/help?session-id={$session-id}&amp;helptopic=folder-setup">求救</A>）
                </TD>
            </TR>
           <TR>
            <TD colspan="4" bgcolor="#697791" height="22" class="testoBianco">
                登入帳號 <xsl:value-of select="normalize-space(/USERMODEL/USERDATA/LOGIN)"/><BR/>
                此帳號從 <xsl:apply-templates select="/USERMODEL/STATEDATA/VAR[@name='first login']"/> 開始使用
           </TD>
          </TR>
          <TR>
            <TD colspan="2" bgcolor="#A6B1C0" height="22" class="testoGrande">
                您可選擇以下的功能：
            </TD>
          </TR>
          <TR>
            <TD height="22" width="23%" class="testoNero" bgcolor="#E2E6F0">
              <A HREF="{$base}/folder/setup?session-id={$session-id}&amp;method=mailbox">新增/刪除信箱</A>
            </TD>
            <TD height="22" class="testoNero" width="77%" bgcolor="#E2E6F0">
              JWebMail 允許你連線到數個 IMAP 與 POP 主機。在這裡你可以新增與刪除這些連線。
            </TD>
          </TR>
          <TR>
            <TD height="22" width="23%" bgcolor="#D1D7E7" class="testoNero">
              <A HREF="{$base}/folder/setup?session-id={$session-id}&amp;method=folder">新增/刪除子收信匣</A>
            </TD>
            <TD height="22" class="testoNero" width="77%" bgcolor="#D1D7E7">
              JWebMail 會為您的每個信箱顯示出其收信匣的樹狀結構，讓您在這裡選擇新增或刪除個別的子收信匣。
            </TD>
          </TR>
        </TABLE>
      </BODY>
    </HTML>
  </xsl:template>
</xsl:stylesheet>
