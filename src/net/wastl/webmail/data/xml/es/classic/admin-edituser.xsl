<?xml version="1.0" encoding="UTF-8"?>
<!--
$Id: admin-edituser.xsl 126 2008-10-31 03:41:09Z unsaved $

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
  <xsl:variable name="session-id" select="/GENERICMODEL/STATEDATA/VAR[@name='session id']/@value"/>
  <xsl:variable name="ud" select="/GENERICMODEL/STATEDATA/USERDATA"/>
  <xsl:template match="/">
    <HTML>
      <HEAD>
        <xsl:choose>
          <xsl:when test="$ud != ''">
            <TITLE>Interfaz de administraci&#243;n de JWebMail: Configuraci&#243;n de usuario para usuario <xsl:value-of select="$ud/LOGIN"/></TITLE>
          </xsl:when>
          <xsl:otherwise>
            <TITLE>Interfaz de Administraci&#243;n de JWebMail: Creaci&#243;n de un nuevo usuario</TITLE>
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
                  <TD COLSPAN="4" ALIGN="center">
                    <FONT SIZE="+1">
                      <STRONG>Modificaci&#243;n de un usuario <xsl:value-of select="$ud/LOGIN"/>, Dominio <xsl:value-of select="$ud/USER_DOMAIN"/></STRONG>
                    </FONT>
                  </TD>
                </xsl:when>
                <xsl:otherwise>
                  <TD COLSPAN="4" ALIGN="center">
                    <FONT SIZE="+1">
                      <STRONG>Creaci&#243;n de un nuevo usuario en el dominio <xsl:value-of select="$ud/USER_DOMAIN"/></STRONG>
                    </FONT>
                  </TD>
                </xsl:otherwise>
              </xsl:choose>
            </TR>
            <TR>
              <TD COLSPAN="4" BGCOLOR="lightblue" ALIGN="CENTER">
                <EM>
                  <FONT SIZE="+1">Preferencias generales</FONT>
                </EM>
              </TD>
            </TR>
            <TR>
              <TD>
                <STRONG>Usuario:</STRONG>
              </TD>
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
              <TD>
                <STRONG>Contrase&#241;a:</STRONG>
              </TD>
              <TD>
                <INPUT NAME="user password" TYPE="password" SIZE="10"/>
              </TD>
            </TR>
            <TR>
              <TD>
                <STRONG>Nombre completo:</STRONG>
              </TD>
              <TD COLSPAN="3">
                <INPUT NAME="user full name" TYPE="text" SIZE="40" VALUE="{$ud/FULL_NAME}"/>
              </TD>
            </TR>
            <TR>
              <TD>
                <STRONG>Idioma:</STRONG>
              </TD>
              <TD COLSPAN="3">
                <SELECT NAME="user language">
                  <xsl:for-each select="/GENERICMODEL/STATEDATA/VAR[@name='language']">
                    <xsl:choose>
                      <xsl:when test="@value = $ud/LOCALE">
                        <OPTION selected="selected">
                          <xsl:apply-templates select="@value"/>
                        </OPTION>
                      </xsl:when>
                      <xsl:otherwise>
                        <OPTION>
                          <xsl:apply-templates select="@value"/>
                        </OPTION>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:for-each>
                </SELECT>
              </TD>
            </TR>
            <TR>
              <TD COLSPAN="4" BGCOLOR="lightblue" ALIGN="CENTER">
                <EM>
                  <FONT SIZE="+1">Preferencias de pantalla</FONT>
                </EM>
              </TD>
            </TR>
            <TR>
              <TD>
                <STRONG>Mensajes por p&#225;gina</STRONG>
              </TD>
              <TD>
                <INPUT TYPE="TEXT" NAME="intvar%max show messages" SIZE="3" VALUE="{$ud/INTVAR[@name='max show messages']/@value}"/>
              </TD>
              <TD>
                <STRONG>Tama&#241;o de los iconos de la barra de navegaci&#243;n</STRONG>
              </TD>
              <TD>
                <INPUT TYPE="TEXT" NAME="intvar%icon size" SIZE="3" VALUE="{$ud/INTVAR[@name='icon size']/@value}"/>
              </TD>
            </TR>
            <TR>
              <TD>
                <STRONG>Mostrar informaci&#243;n adicional</STRONG>
              </TD>
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
              <TD>
                <STRONG>Mostrar atributos de im&#225;genes</STRONG>
              </TD>
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
              <TD>
                <STRONG>Filtrar JavaScript</STRONG>
              </TD>
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
              <TD>
                <STRONG>Poner marcas a los mensajes</STRONG>
              </TD>
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
              <TD>
                <STRONG>Cortar l&#237;neas (al mostrar y redactar)</STRONG>
              </TD>
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
              <TD>
                <STRONG>Longitud m&#225;x. de l&#237;nea</STRONG>
              </TD>
              <TD>
                <INPUT TYPE="TEXT" NAME="intvar%max line length" SIZE="3" VALUE="{$ud/INTVAR[@name='max line length']/@value}"/>
              </TD>
            </TR>
            <TR>
              <TD COLSPAN="4" BGCOLOR="lightblue" ALIGN="CENTER">
                <EM>
                  <FONT SIZE="+1">Preferencias de redacci&#243;n</FONT>
                </EM>
              </TD>
            </TR>
            <TR>
              <TD>
                <STRONG>Direcci&#243;n de correo:</STRONG>
              </TD>
              <TD COLSPAN="3">
                <INPUT NAME="user email" TYPE="text" SIZE="40" VALUE="{$ud/EMAIL}"/>
              </TD>
            </TR>
            <TR>
              <TD VALIGN="TOP">
                <STRONG>Firma:</STRONG>
              </TD>
              <TD COLSPAN="3">
                <TEXTAREA ROWS="5" COLS="79" NAME="user signature">
                  <xsl:value-of select="$ud/SIGNATURE"/>
                </TEXTAREA>
              </TD>
            </TR>
            <TR bgcolor="#dddddd">
              <TD COLSPAN="4" ALIGN="center">
                <FONT SIZE="+1">
                  <STRONG>Aceptar cambios</STRONG>
                </FONT>
              </TD>
            </TR>
            <TR>
              <TD COLSPAN="4" ALIGN="center">
                <xsl:choose>
                  <xsl:when test="$ud != ''">
                    <INPUT TYPE="submit" name="change" value="Change"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <INPUT TYPE="submit" name="add" value="Add"/>
                  </xsl:otherwise>
                </xsl:choose>
                <INPUT type="reset" name="reset" value="Reset"/>
              </TD>
            </TR>
          </TABLE>
        </FORM>
      </BODY>
    </HTML>
  </xsl:template>
</xsl:stylesheet>
