<?xml version="1.0" encoding="ISO-8859-1"?>
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
        <TITLE>JWebMail Administrator Login Screen</TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
      </HEAD>

      <BODY bgcolor="#ffffff">
        <TABLE CELLSPACING="0" STYLE="border:0; padding:0; margin:50px;">
          <TR>
            <!-- The java_powerd.png icon is completely incongruous with
                 the login widget, and with the Bibop theme
                 (not that these other two items are complementary
                 with each other). -->
            <TD STYLE="padding:20px; padding-top:0; border:0; margin:0; text-align:center;">
              <A href="{$base}" title="Home Page link">
                <IMG STYLE="margin:20px; border:0; padding:0;"
                     SRC="{$imgbase}/images/homefolders-icon.png"
                     ALT="JWebMail Home Page"/>
              </A>
              <DIV style="font:10px sans-serif; padding:0; border:0; margin:0; text-align:center;">
                <A href="http://admc.com/oss-support" title="Axis support link"
                   style="text-decoration:none; color:black;">
                <IMG STYLE="margin:0; border:0; padding:0;"
                     SRC="{$imgbase}/images/axislogo-x90.png"
                     ALT="Axis Logo"/><BR/>
                   <SPAN style="font-variant:small-caps">Professional support available from</SPAN><BR/>
                   <SPAN style="font-weight:bold;">Axis Data Management Corp</SPAN>
                </A>
              </DIV>
            </TD>
            <TD STYLE="padding:20px; border:0; margin:0; text-align:center;">
              <FORM ACTION="{$base}/admin/login" METHOD="POST" NAME="loginForm">
                <TABLE CELLSPACING="0" CELLPADDING="20" BORDER="4" bgcolor="#ff0000">
                  <TR>
                    <TD ALIGN="CENTER">
                      <TABLE CELLSPACING="0" CELLPADDING="10" BORDER="0" bgcolor="#ff0000">
                        <TR>
                          <TD COLSPAN="2" ALIGN="CENTER">
                            <IMG SRC="{$imgbase}/images/login_title.png" ALT="JWebMail login"/></TD>
                        </TR>
                        <TR>
                          <TD WIDTH="50%" ALIGN="RIGHT"><STRONG>Login:</STRONG></TD>
                          <TD WIDTH="50%">Administrator</TD>
                        </TR>
                        <TR>
                          <TD WIDTH="50%" ALIGN="RIGHT"><STRONG>Password:</STRONG></TD>
                          <TD WIDTH="50%"><INPUT ID="passwordId" TYPE="password" NAME="password" SIZE="15"/></TD>
                        </TR>
                        <TR>
                          <TD ALIGN="CENTER"><INPUT TYPE="submit" value="Login"/></TD>
                          <TD ALIGN="CENTER"><INPUT TYPE="reset" value="Reset"/></TD>
                        </TR>
                      </TABLE>
                    </TD>
                  </TR>
                </TABLE>
              </FORM>
            </TD>
          </TR>
          <xsl:if test="/GENERICMODEL/STATEDATA/VAR[@name='invalid password']/@value = 'yes'">
            <!-- START invalid pass -->
            <TR>
              <TD COLSPAN="2" ALIGN="CENTER">
                <FONT COLOR="red" SIZE="+1">
                  Login incorrect. The passwords did not match or the name/password field was empty! Attempt will be logged.
                </FONT>
              </TD>
            </TR>
            <!-- END invalid pass -->
          </xsl:if>
        </TABLE>
      </BODY>
    </HTML>
    <SCRIPT type="text/javascript">
      document.getElementById("passwordId").focus();
    </SCRIPT>
  </xsl:template>
</xsl:stylesheet>
