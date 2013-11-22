<?xml version="1.0" encoding="ISO-8859-1"?>
<!--
$Id: admin-login.xsl 126 2008-10-31 03:41:09Z unsaved $

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
        <TITLE>Pantalla de entrada del administrador de JWebMail</TITLE>
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
              <FORM ACTION="{$base}/admin/login" METHOD="POST" NAME="loginForm">
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
                          <TD WIDTH="50%">Administrador</TD>
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
                  Conexi&#243;n fallida. &#161;Las contrase&#241;as no coinciden o el campo de usuario/contrase&#241;a estaba vacio! El intento ser&#225; registrado.
                </FONT>
              </TD>
            </TR>
<!-- END invalid pass -->
          </xsl:if>
        </TABLE>
      </BODY>
    </HTML>
  </xsl:template>
</xsl:stylesheet>
