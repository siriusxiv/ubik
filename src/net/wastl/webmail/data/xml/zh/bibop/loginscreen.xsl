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

  <xsl:template match="/">

    <HTML>
      <HEAD>
        <TITLE>JWebMail 系統登入視窗</TITLE>
        <script language="JavaScript">
        &lt;!--
        if(top.location!=location)
            top.location.href=document.location.href;
        --&gt;
      </script>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
        <link rel="stylesheet" href="{$base}/passthrough/webmail.css"/>
      </HEAD>

      <BODY bgcolor="#ffffff">
        <P align="center">&#160;</P>
        <P align="center">&#160;</P>
            <CENTER>
              <FORM ACTION="{$base}/login" METHOD="POST" NAME="loginForm">
                <TABLE width="402" border="0" cellspacing="0" cellpadding="1" bgcolor="#000000" height="252" align="center">
                  <TR>
                    <TD align="center" bgcolor="#000000" valign="middle">
                      <TABLE width="400" border="0" cellspacing="0" cellpadding="0" height="250">
                        <TR>
                          <TD colspan="4" width="400" height="50" bgcolor="#7B889F">&#160;
                          </TD>
                        </TR>
                        <TR>
                          <TD rowspan="4" width="85" align="center" bgcolor="#D3D8DE" height="110">
                            <IMG SRC="{$imgbase}/images/logobibop.gif" ALT="Logo BiBop"/>
                          </TD>
                          <TD colspan="2" align="center" height="40" bgcolor="#FFFFFF" width="290">
                            <IMG SRC="{$imgbase}/images/webmailtitle.gif"/>
                          </TD>
                          <TD rowspan="4" width="25" height="115" bgcolor="#FFFFFF">
                            &#160;
                          </TD>
                        </TR>
                        <TR>
                          <TD class="testo" align="right" height="25" bgcolor="#FFFFFF" width="100">
                            帳號&#160;&#160;
                          </TD>
                          <TD height="25" bgcolor="#FFFFFF" width="190" class="testo">
                            <INPUT TYPE="text" NAME="login" SIZE="15" class="testo"/>
                          </TD>
                        </TR>
                        <TR>
                          <TD class="testo" align="right" height="25" bgcolor="#FFFFFF" width="100">
                            密碼&#160;&#160;
                          </TD>
                          <TD height="25" bgcolor="#FFFFFF" width="190" class="testo">
                           <INPUT TYPE="password" NAME="password" SIZE="15" class="testo"/>
                          </TD>
                        </TR>
                        <TR>
                                <xsl:choose>
                                <xsl:when test="/GENERICMODEL/SYSDATA/VIRTUALS/@enabled='true'">
                          <TD class="testo" align="right" height="25" bgcolor="#FFFFFF" width="100">
                            區域&#160;&#160;
                          </TD>
                          <TD height="25" bgcolor="#FFFFFF" width="190" class="testo">
                            <SELECT name="vdom" class="testo">
                              <xsl:for-each select="/GENERICMODEL/SYSDATA/DOMAIN">
                                <OPTION><xsl:apply-templates select="NAME"/></OPTION>
                              </xsl:for-each>
                            </SELECT>
                          </TD>
                            </xsl:when>
                            <xsl:otherwise>
                          <TD class="testo" align="right" height="25" bgcolor="#FFFFFF" width="100">
                                &#160;
                          </TD>
                          <TD height="25" bgcolor="#FFFFFF" width="190" class="testo">
                                &#160;
                          </TD>
                            </xsl:otherwise>
                            </xsl:choose>
                        </TR>
                        <TR>
                          <TD width="85" bgcolor="#7B889F" height="50" class="testo">&#160;</TD>
                          <TD bgcolor="#7B889F" height="50" width="100" class="testo">&#160;</TD>
                          <TD bgcolor="#7B889F" height="50" width="190" class="testo">
                            <INPUT TYPE="submit" value="登入" class="testo"/>
                            <INPUT TYPE="reset" value="重填" class="testo"/>
                          </TD>
                          <TD width="25" bgcolor="#7B889F" height="50" class="testo">&#160;</TD>
                        </TR>
                        <TR>
                          <TD colspan="4" width="400" class="testoBianco" bgcolor="#394864" height="35" align="center"><SPAN class="bold">BiBop
              JWebMail </SPAN>is based on<BR/>
              JWebMail is &#169; 1999-@year@ by Sebastian Schaffert</TD>
                        </TR>
                      </TABLE>
                    </TD>
                  </TR>
                </TABLE>
              </FORM>
            </CENTER>
          <xsl:if test="/GENERICMODEL/STATEDATA/VAR[@name='invalid password']/@value = 'yes'">
            <!-- START invalid pass -->
            <P align="center" class="testo"><SPAN class="bold">Login incorrect</SPAN>. The passwords did not match or the name/password field was empty! Attempt will be logged.</P>
            <P align="center">&#160;</P>
            <P align="center">&#160;</P>
            <!-- END invalid pass -->
          </xsl:if>
      </BODY>
    </HTML>
  </xsl:template>
</xsl:stylesheet>
