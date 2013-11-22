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
  <xsl:variable name="ud" select="/GENERICMODEL/STATEDATA/USERDATA"/>

  <xsl:template match="/">

    <HTML>
      <HEAD>
        <xsl:choose>
          <xsl:when test="$ud != ''">
            <TITLE>JWebMail 管理介面：使用者『<xsl:value-of select="$ud/LOGIN"/>』的設定</TITLE>
          </xsl:when>
          <xsl:otherwise>
            <TITLE>JWebMail 管理介面：新增使用者</TITLE>
          </xsl:otherwise>
        </xsl:choose>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
      </HEAD>

      <BODY BGCOLOR="white">

        <FORM ACTION="{$base}/admin/user/edit?session-id={$session-id}" METHOD="POST">

          <TABLE WIDTH="100%" CELLSPACING="0" CELLPADDING="0">
            <TR bgcolor="#dddddd">
              <xsl:choose>
                <xsl:when test="$ud != ''">
                  <TD COLSPAN="4" ALIGN="center"><FONT SIZE="+1"><STRONG>修改使用者『<xsl:value-of select="$ud/LOGIN"/>』，網域：<xsl:value-of select="$ud/USER_DOMAIN"/></STRONG></FONT></TD>
                </xsl:when>
                <xsl:otherwise>
                  <TD COLSPAN="4" ALIGN="center"><FONT SIZE="+1"><STRONG>新增使用者於網域：<xsl:value-of select="$ud/USER_DOMAIN"/></STRONG></FONT></TD>
                </xsl:otherwise>
              </xsl:choose>
            </TR>

            <TR>
              <TD COLSPAN="4" BGCOLOR="lightblue" ALIGN="CENTER"><EM><FONT SIZE="+1">一般設定</FONT></EM></TD>
            </TR>

            <TR>
              <TD><STRONG>帳號：</STRONG></TD>
              <TD>
                <xsl:choose>
                  <xsl:when test="$ud != ''">
                    <xsl:value-of select="$ud/LOGIN"/>
                    <INPUT TYPE="hidden" NAME="user" VALUE="{$ud/LOGIN}"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <INPUT TYPE="text" NAME="user" SIZE="10"/>
                  </xsl:otherwise>
                </xsl:choose>
              </TD>

              <TD><STRONG>密碼：</STRONG></TD>
              <TD><INPUT NAME="user password" TYPE="password" SIZE="10"/></TD>
            </TR>

            <TR>
              <TD><STRONG>全名：</STRONG></TD>
              <TD COLSPAN="3"><INPUT NAME="user full name" TYPE="text" SIZE="40" VALUE="{$ud/FULL_NAME}"/></TD>
            </TR>

            <TR>
              <TD><STRONG>語言：</STRONG></TD>
              <TD COLSPAN="3">
                <SELECT NAME="user language">
                  <xsl:for-each select="/GENERICMODEL/STATEDATA/VAR[@name='language']">
                    <xsl:choose>
                      <xsl:when test="@value = $ud/LOCALE">
                        <OPTION selected="selected"><xsl:apply-templates select="@value"/></OPTION>
                      </xsl:when>
                      <xsl:otherwise>
                        <OPTION><xsl:apply-templates select="@value"/></OPTION>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:for-each>
                </SELECT>
              </TD>
            </TR>

            <TR>
              <TD COLSPAN="4" BGCOLOR="lightblue" ALIGN="CENTER"><EM><FONT SIZE="+1">顯示設定</FONT></EM></TD>
            </TR>

            <TR>
              <TD><STRONG>每頁顯示的信件數：</STRONG></TD>
              <TD><INPUT TYPE="TEXT" NAME="intvar%max show messages" SIZE="3" VALUE="{$ud/INTVAR[@name='max show messages']/@value}"/></TD>

              <TD><STRONG>導覽選單的小圖示大小：</STRONG></TD>
              <TD><INPUT TYPE="TEXT" NAME="intvar%icon size" SIZE="3" VALUE="{$ud/INTVAR[@name='icon size']/@value}"/></TD>
            </TR>

            <TR>
              <TD><STRONG>顯示漂漂的畫面：</STRONG></TD>
              <TD>
                <xsl:choose>
                  <xsl:when test="$ud/BOOLVAR[@name='show fancy']/@value = 'yes'">
                    <INPUT TYPE="CHECKBOX" NAME="boolvar%show fancy" checked="checked"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <INPUT TYPE="CHECKBOX" NAME="boolvar%show fancy"/>
                  </xsl:otherwise>
                </xsl:choose>
              </TD>

              <TD><STRONG>顯示附件中的圖片：</STRONG></TD>
              <TD>
                <xsl:choose>
                  <xsl:when test="$ud/BOOLVAR[@name='show images']/@value = 'yes'">
                    <INPUT TYPE="CHECKBOX" NAME="boolvar%show images" checked="checked"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <INPUT TYPE="CHECKBOX" NAME="boolvar%show images"/>
                  </xsl:otherwise>
                </xsl:choose>
              </TD>
            </TR>

            <TR>
              <TD><STRONG>過濾 JavaScript：</STRONG></TD>
              <TD>
                <xsl:choose>
                  <xsl:when test="$ud/BOOLVAR[@name='filter javascript']/@value = 'yes'">
                    <INPUT TYPE="CHECKBOX" NAME="boolvar%filter javascript" checked="checked"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <INPUT TYPE="CHECKBOX" NAME="boolvar%filter javascript"/>
                  </xsl:otherwise>
                </xsl:choose>
              </TD>

              <TD><STRONG>設定信件旗標：</STRONG></TD>
              <TD>
                <xsl:choose>
                  <xsl:when test="$ud/BOOLVAR[@name='set message flags']/@value = 'yes'">
                    <INPUT TYPE="CHECKBOX" NAME="boolvar%set message flags" checked="checked"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <INPUT TYPE="CHECKBOX" NAME="boolvar%set message flags"/>
                  </xsl:otherwise>
                </xsl:choose>
              </TD>
            </TR>

            <TR>
              <TD><STRONG>斷行（信件顯示與編輯）：</STRONG></TD>
              <TD>
                <xsl:choose>
                  <xsl:when test="$ud/BOOLVAR[@name='break lines']/@value = 'yes'">
                    <INPUT TYPE="CHECKBOX" NAME="boolvar%break lines" checked="checked"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <INPUT TYPE="CHECKBOX" NAME="boolvar%break lines"/>
                  </xsl:otherwise>
                </xsl:choose>
              </TD>

              <TD><STRONG>單行的最大長度：</STRONG></TD>
              <TD><INPUT TYPE="TEXT" NAME="intvar%max line length" SIZE="3" VALUE="{$ud/INTVAR[@name='max line length']/@value}"/></TD>
            </TR>

            <TR>
              <TD COLSPAN="4" BGCOLOR="lightblue" ALIGN="CENTER"><EM><FONT SIZE="+1">信件編輯設定</FONT></EM></TD>
            </TR>

            <TR>
              <TD><STRONG>電子郵件地址：</STRONG></TD>
              <TD COLSPAN="3"><INPUT NAME="user email" TYPE="text" SIZE="40" VALUE="{$ud/EMAIL}"/></TD>
            </TR>

            <TR>
              <TD VALIGN="TOP"><STRONG>簽名檔內容：</STRONG></TD>
              <TD COLSPAN="3">
                <TEXTAREA ROWS="5" COLS="79" NAME="user signature"><xsl:value-of select="$ud/SIGNATURE"/></TEXTAREA>
              </TD>
            </TR>

            <TR bgcolor="#dddddd">
              <TD COLSPAN="4" ALIGN="center"><FONT SIZE="+1"><STRONG>送出修改</STRONG></FONT></TD>
            </TR>

            <TR>
              <TD COLSPAN="4" ALIGN="center">
                <xsl:choose>
                  <xsl:when test="$ud != ''">
                    <INPUT TYPE="submit" name="change" value="修改"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <INPUT TYPE="submit" name="add" value="新增"/>
                  </xsl:otherwise>
                </xsl:choose>
                <INPUT type="reset" name="reset" value="重填"/>
              </TD>
            </TR>
          </TABLE>
        </FORM>
      </BODY>
    </HTML>
  </xsl:template>
</xsl:stylesheet>
