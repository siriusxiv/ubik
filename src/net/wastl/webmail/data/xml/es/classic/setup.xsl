<?xml version="1.0" encoding="ISO-8859-1"?>
<!--
$Id: setup.xsl 126 2008-10-31 03:41:09Z unsaved $

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
  <xsl:variable name="imgbase" select="/USERMODEL/STATEDATA/VAR[@name='img base uri']/@value"/>
  <xsl:variable name="base" select="/USERMODEL/STATEDATA/VAR[@name='base uri']/@value"/>
  <xsl:variable name="session-id" select="/USERMODEL/STATEDATA/VAR[@name='session id']/@value"/>
  <xsl:template match="/">
    <HTML>
      <HEAD>
        <TITLE>Buz&#243;n de JWebMail para <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/>: Ventana principal</TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
      </HEAD>
      <BODY bgcolor="#ffffff">
        <FORM ACTION="{$base}/setup/submit?session-id={$session-id}" METHOD="POST">
          <TABLE BGCOLOR="#dddddd" CELLSPACING="0" BORDER="0">
            <TR>
              <TD COLSPAN="2" VALIGN="CENTER">
                <IMG SRC="{$imgbase}/images/btn-setup.png"/>
              </TD>
              <TD COLSPAN="2" VALIGN="CENTER"><FONT SIZE="+2"><STRONG>Configuraci&#243;n de JWebMail para <xsl:value-of select="normalize-space(/USERMODEL/USERDATA/FULL_NAME)"/></STRONG></FONT> (<A HREF="{$base}/help?session-id={$session-id}&amp;helptopic=setup">Ayuda</A>)<BR/><EM>Nombre de usuario <xsl:value-of select="normalize-space(/USERMODEL/USERDATA/LOGIN)"/></EM><BR/><EM>La cuenta existe desde <xsl:apply-templates select="/USERMODEL/STATEDATA/VAR[@name='first login']"/></EM></TD>
            </TR>
            <TR>
              <TD COLSPAN="4" BGCOLOR="#aaaaaa" ALIGN="CENTER">
                <EM>
                  <FONT SIZE="+1">Preferencias</FONT>
                </EM>
              </TD>
            </TR>
            <TR>
              <TD WIDTH="10%">
                <STRONG>Nombre completo:</STRONG>
              </TD>
              <TD WIDTH="40%">
                <INPUT TYPE="TEXT" NAME="FULLNAME" VALUE="{normalize-space(/USERMODEL/USERDATA/FULL_NAME)}" SIZE="30"/>
              </TD>
              <TD WIDTH="10%">
                <STRONG>Contrase&#241;a:</STRONG>
              </TD>
              <TD WIDTH="40%">
                <INPUT TYPE="PASSWORD" NAME="PASSWORD" SIZE="10"/>
              </TD>
            </TR>
            <TR>
              <TD>
                <STRONG>Idioma:</STRONG>
              </TD>
              <TD>
                <SELECT NAME="LANGUAGE">
                  <xsl:for-each select="/USERMODEL/STATEDATA/VAR[@name='language']">
                    <xsl:choose>
                      <xsl:when test="@value = /USERMODEL/USERDATA/LOCALE">
                        <OPTION selected="selected">
                          <xsl:apply-templates select="."/>
                        </OPTION>
                      </xsl:when>
                      <xsl:otherwise>
                        <OPTION>
                          <xsl:apply-templates select="."/>
                        </OPTION>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:for-each>
                </SELECT>
              </TD>
              <TD WIDTH="10%">
                <STRONG>Verificar:</STRONG>
              </TD>
              <TD WIDTH="40%">
                <INPUT TYPE="PASSWORD" NAME="VERIFY" SIZE="10"/>
              </TD>
            </TR>
            <TR>
              <TD COLSPAN="4" BGCOLOR="#aaaaaa" ALIGN="CENTER">
                <EM>
                  <FONT SIZE="+1">Opciones de presentaci&#243;n del buz&#243;n</FONT>
                </EM>
              </TD>
            </TR>
            <TR>
              <TD>
                <STRONG>Mensajes por p&#225;gina</STRONG>
              </TD>
              <TD>
                <INPUT TYPE="TEXT" NAME="intvar%max show messages" SIZE="3" VALUE="{/USERMODEL/USERDATA/INTVAR[@name='max show messages']/@value}"/>
              </TD>
              <TD>
                <STRONG>Tama&#241;o de los iconos de la barra de navegaci&#243;n</STRONG>
              </TD>
              <TD>
                <INPUT TYPE="TEXT" NAME="intvar%icon size" SIZE="3" VALUE="{/USERMODEL/USERDATA/INTVAR[@name='icon size']/@value}"/>
              </TD>
            </TR>
            <TR>
              <TD>
                <STRONG>Mostras informaci&#243;n adicional</STRONG>
              </TD>
              <TD>
                <xsl:choose>
                  <xsl:when test="/USERMODEL/USERDATA/BOOLVAR[@name='show fancy']/@value = 'yes'">
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
                  <xsl:when test="/USERMODEL/USERDATA/BOOLVAR[@name='show images']/@value = 'yes'">
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
                <STRONG>Purgar aut. carpetas</STRONG>
              </TD>
              <TD>
                <xsl:choose>
                  <xsl:when test="/USERMODEL/USERDATA/BOOLVAR[@name='autoexpunge']/@value = 'yes'">
                    <INPUT TYPE="CHECKBOX" NAME="boolvar%autoexpunge" checked="checked"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <INPUT TYPE="CHECKBOX" NAME="boolvar%autoexpunge"/>
                  </xsl:otherwise>
                </xsl:choose>
              </TD>
              <TD>
                <STRONG>Poner marcas de mensajes</STRONG>
              </TD>
              <TD>
                <xsl:choose>
                  <xsl:when test="/USERMODEL/USERDATA/BOOLVAR[@name='set message flags']/@value = 'yes'">
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
                <STRONG>Cortar l&#237;neas (en mostrar y redactar)</STRONG>
              </TD>
              <TD>
                <xsl:choose>
                  <xsl:when test="/USERMODEL/USERDATA/BOOLVAR[@name='break lines']/@value = 'yes'">
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
                <INPUT TYPE="TEXT" NAME="intvar%max line length" SIZE="3" VALUE="{/USERMODEL/USERDATA/INTVAR[@name='max line length']/@value}"/>
              </TD>
            </TR>
            <TR>
              <TD COLSPAN="4" BGCOLOR="#aaaaaa" ALIGN="CENTER">
                <EM>
                  <FONT SIZE="+1">Preferencias de redacci&#243;n</FONT>
                </EM>
              </TD>
            </TR>
            <TR>
              <TD>
                <STRONG>Guardar correo enviado</STRONG>
              </TD>
              <TD>
                <xsl:choose>
                  <xsl:when test="/USERMODEL/USERDATA/BOOLVAR[@name='save sent messages']/@value = 'yes'">
                    <INPUT TYPE="CHECKBOX" NAME="boolvar%save sent messages" checked="checked"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <INPUT TYPE="CHECKBOX" NAME="boolvar%save sent messages"/>
                  </xsl:otherwise>
                </xsl:choose>
              </TD>
              <TD>en la carpeta </TD>
              <TD>
                <SELECT NAME="SENTFOLDER">
                  <xsl:for-each select="/USERMODEL/MAILHOST_MODEL//FOLDER">
                    <xsl:choose>
                      <xsl:when test="normalize-space(/USERMODEL/USERDATA/SENT_FOLDER) = @id">
                        <OPTION value="{@id}" selected="selected">
                          <xsl:value-of select="@name"/>
                        </OPTION>
                      </xsl:when>
                      <xsl:otherwise>
                        <OPTION value="{@id}">
                          <xsl:value-of select="@name"/>
                        </OPTION>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:for-each>
                </SELECT>
              </TD>
            </TR>
            <TR>
              <TD>
                <STRONG>Direcci&#243;n de correo:</STRONG>
              </TD>
              <TD COLSPAN="3">
                <INPUT TYPE="TEXT" NAME="EMAIL" SIZE="40" VALUE="{normalize-space(/USERMODEL/USERDATA/EMAIL)}"/>
              </TD>
            </TR>
            <TR>
              <TD VALIGN="TOP">
                <STRONG>Firma:</STRONG>
              </TD>
              <TD COLSPAN="3">
                <TEXTAREA ROWS="5" COLS="79" NAME="SIGNATURE">
                  <xsl:value-of select="/USERMODEL/USERDATA/SIGNATURE"/>
                </TEXTAREA>
              </TD>
            </TR>
            <TR>
              <TD COLSPAN="2" BGCOLOR="#aaaaaa">
                <CENTER>
                  <INPUT TYPE="submit" VALUE="Submit"/>
                </CENTER>
              </TD>
              <TD COLSPAN="2" BGCOLOR="#aaaaaa">
                <CENTER>
                  <INPUT TYPE="reset" VALUE="Reset"/>
                </CENTER>
              </TD>
            </TR>
          </TABLE>
        </FORM>
      </BODY>
    </HTML>
  </xsl:template>
  <xsl:template match="/USERMODEL/STATEDATA/VAR">
    <xsl:value-of select="@value"/>
  </xsl:template>
</xsl:stylesheet>
