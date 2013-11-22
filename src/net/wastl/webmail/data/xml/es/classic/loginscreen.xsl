<?xml version="1.0" encoding="ISO-8859-1"?>
<!--
$Id: loginscreen.xsl 126 2008-10-31 03:41:09Z unsaved $

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
  <xsl:template match="/">
    <HTML>
      <HEAD>
        <TITLE>Pantalla de conexi&#243;n de JWebMail </TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
      </HEAD>
      <BODY bgcolor="#ffffff">
        <TABLE WIDTH="100%">
          <TR>
            <TD COLSPAN="3" ALIGN="CENTER" HEIGHT="70">
            </TD>
          </TR>
          <TR>
            <TD WIDTH="20%" VALIGN="TOP">
              <IMG SRC="{$imgbase}/images/java_powered.png" ALT="Java powered"/>
            </TD>
            <TD WIDTH="60%" ALIGN="CENTER">
              <FORM ACTION="{$base}/login" METHOD="POST" NAME="loginForm">
                <TABLE CELLSPACING="0" CELLPADDING="20" BORDER="4" bgcolor="#ff0000">
                  <TR>
                    <TD ALIGN="CENTER">
                      <TABLE CELLSPACING="0" CELLPADDING="10" BORDER="0" bgcolor="#ff0000">
                        <TR>
                          <TD COLSPAN="2" ALIGN="CENTER">
                            <IMG SRC="{$imgbase}/images/login_title.png" ALT="JWebMail login"/>
                          </TD>
                        </TR>
                        <TR>
                          <TD WIDTH="50%" ALIGN="RIGHT">
                            <STRONG>Usuario:</STRONG>
                          </TD>
                          <TD WIDTH="50%">
                            <INPUT TYPE="text" NAME="login" SIZE="15"/>
                          </TD>
                        </TR>
                        <TR>
                          <TD WIDTH="50%" ALIGN="RIGHT">
                            <STRONG>Contrase&#241;a:</STRONG>
                          </TD>
                          <TD WIDTH="50%">
                            <INPUT TYPE="password" NAME="password" SIZE="15"/>
                          </TD>
                        </TR>
                        <TR>
                          <TD WIDTH="50%" ALIGN="RIGHT">
                            <B>Dominio:</B>
                          </TD>
                          <TD WIDTH="50%">
                            <SELECT name="vdom">
                              <xsl:for-each select="/GENERICMODEL/SYSDATA/DOMAIN">
                                <OPTION>
                                  <xsl:apply-templates select="NAME"/>
                                </OPTION>
                              </xsl:for-each>
                            </SELECT>
                          </TD>
                        </TR>
                        <TR>
                          <TD ALIGN="CENTER">
                            <INPUT TYPE="submit" value="Login"/>
                          </TD>
                          <TD ALIGN="CENTER">
                            <INPUT TYPE="reset" value="Reset"/>
                          </TD>
                        </TR>
                      </TABLE>
                    </TD>
                  </TR>
                </TABLE>
              </FORM>
            </TD>
            <TD WIDTH="20%">
            </TD>
          </TR>
          <xsl:if test="/GENERICMODEL/STATEDATA/VAR[@name='invalid password']/@value = 'yes'">
<!-- START invalid pass -->
            <TR>
              <TD COLSPAN="3" ALIGN="CENTER">
                <FONT COLOR="red" SIZE="+1">
                  Conexi&#243;n fallida. &#161;Las contrease&#241;as no coincidieron o el campo del nombre/contrase&#241;a estaba vac&#237;o! El intento ser&#225; registrado.
                </FONT>
              </TD>
            </TR>
<!-- END invalid pass -->
          </xsl:if>
          <TR>
            <TD COLSPAN="3" ALIGN="CENTER">
              <FONT SIZE="-1">
                <EM>JWebMail es (c)1999/2000 por <A HREF="mailto:schaffer@informatik.uni-muenchen.de">Sebastian Schaffert</A>. Est&#225; distribu&#237;do bajo los t&#233;rminos de la GNU Public License (GPL).</EM>
              </FONT>
            </TD>
          </TR>
          <TR>
            <TD COLSPAN="3" ALIGN="CENTER">
              <FONT SIZE="-1">
                <EM><STRONG>Versi&#243;n</STRONG>: JWebMail <xsl:value-of select="/GENERICMODEL/STATEDATA/VAR[@name = 'webmail version']/@value"/> on &quot;<xsl:value-of select="/GENERICMODEL/STATEDATA/VAR[@name = 'java virtual machine']/@value"/>&quot;, <xsl:value-of select="/GENERICMODEL/STATEDATA/VAR[@name = 'operating system']/@value"/></EM>
              </FONT>
            </TD>
          </TR>
          <TR>
            <TD COLSPAN="3" ALIGN="CENTER">
              <FONT SIZE="-1">
                <EM>La taza de caf&#233; de Java y &quot;Java&quot; son marcas registradas de <A HREF="http://java.sun.com">Sun Microsystems, Inc.</A></EM>
              </FONT>
            </TD>
          </TR>
        </TABLE>
      </BODY>
    </HTML>
  </xsl:template>
</xsl:stylesheet>
