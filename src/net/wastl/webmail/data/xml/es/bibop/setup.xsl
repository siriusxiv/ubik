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

    <xsl:variable name="imgbase" select="/USERMODEL/STATEDATA/VAR[@name='img base uri']/@value"/>
    <xsl:variable name="base" select="/USERMODEL/STATEDATA/VAR[@name='base uri']/@value"/>
    <xsl:variable name="session-id" select="/USERMODEL/STATEDATA/VAR[@name='session id']/@value"/>
    <xsl:variable name="themeset" select="/USERMODEL/STATEDATA/VAR[@name='themeset']/@value"/>

    <xsl:template match="/">

    <HTML>
      <HEAD>
        <TITLE>Buz&#243;n de JWebMail para <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/>: Ventana principal</TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
        <link rel="stylesheet" href="{$base}/passthrough/webmail.css"/>
      </HEAD>

      <BODY bgcolor="#B5C1CF" topmargin="5" leftmargin="0" marginwidth="0" marginheight="5">

        <FORM ACTION="{$base}/setup/submit?session-id={$session-id}" METHOD="POST">
          <TABLE width="100%" border="0" cellspacing="2" cellpadding="4">
            <TR>
              <TD width="100%" colspan="4" height="22" class="testoNero">
                <IMG SRC="{$imgbase}/images/icona_user.gif" align="absmiddle"/>
              Configuraci&#243;n de JWebMail para <xsl:value-of select="normalize-space(/USERMODEL/USERDATA/FULL_NAME)"/> (<A HREF="{$base}/help?session-id={$session-id}&amp;helptopic=setup">Ayuda</A>)
                </TD>
            </TR>
            <TR>
                <TD width="100%" colspan="4" bgcolor="#697791" height="22" class="testoBianco">
                Nombre de usuario <xsl:value-of select="normalize-space(/USERMODEL/USERDATA/LOGIN)"/><BR/>
                La cuenta existe desde <xsl:apply-templates select="/USERMODEL/STATEDATA/VAR[@name='first login']"/>
              </TD>
            </TR>
            <TR>
              <TD colspan="4" bgcolor="#A6B1C0" height="22" align="center" class="testoGrande">Preferencias</TD>
            </TR>
            <TR>
              <TD bgcolor="#E2E6F0" class="testoNero">Nombre completo:</TD>
              <TD class="testoNero" bgcolor="#D1D7E7"><INPUT TYPE="TEXT" NAME="FULLNAME" VALUE="{normalize-space(/USERMODEL/USERDATA/FULL_NAME)}" class="testoNero" size="15"/></TD>
              <TD class="testoNero" bgcolor="#E2E6F0">Contrase&#241;a:</TD>
              <TD class="testoNero" bgcolor="#D1D7E7"><INPUT TYPE="PASSWORD" NAME="PASSWORD" class="testoNero" size="15"/></TD>
            </TR>
            <TR>
              <TD bgcolor="#E2E6F0" class="testoNero">Idioma:</TD>
              <TD class="testoNero" bgcolor="#D1D7E7">
                <SELECT NAME="LANGUAGE" class="testoNero">
                  <xsl:for-each select="/USERMODEL/STATEDATA/VAR[@name='language']">
                    <xsl:choose>
                      <xsl:when test="@value = /USERMODEL/USERDATA/LOCALE">
                        <OPTION selected="selected"><xsl:apply-templates select="."/></OPTION>
                      </xsl:when>
                      <xsl:otherwise>
                        <OPTION><xsl:apply-templates select="."/></OPTION>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:for-each>
                </SELECT>
              </TD>
              <TD class="testoNero" bgcolor="#E2E6F0">Verificar:</TD>
              <TD class="testoNero" bgcolor="#D1D7E7"><INPUT TYPE="PASSWORD" NAME="VERIFY" class="testoNero" size="15"/></TD>
            </TR>
            <TR>
              <TD class="testoNero">Tema:</TD>
              <TD class="testoNero" COLSPAN="3" bgcolor="#D1D7E7">
                <SELECT NAME="THEME" class="testoNero">
                  <xsl:for-each select="/USERMODEL/STATEDATA/VAR[@name=$themeset]">
                    <xsl:choose>
                      <xsl:when test="@value = /USERMODEL/USERDATA/THEME">
                        <OPTION selected="selected"><xsl:apply-templates select="."/></OPTION>
                      </xsl:when>
                      <xsl:otherwise>
                        <OPTION><xsl:apply-templates select="."/></OPTION>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:for-each>
                </SELECT>
              </TD>
            </TR>
            <TR>
              <TD width="100%" bgcolor="#A6B1C0" height="22" align="center" colspan="4" class="testoGrande">Opciones de presentaci&#243;n del buz&#243;n</TD>
            </TR>
            <TR>
              <TD bgcolor="#E2E6F0" class="testoNero">Mensajes por p&#225;gina</TD>
              <TD class="testoNero" bgcolor="#D1D7E7"><INPUT TYPE="TEXT" NAME="intvar%max show messages" size="5" class="testoNero" VALUE="{/USERMODEL/USERDATA/INTVAR[@name='max show messages']/@value}"/></TD>
              <TD class="testoNero" bgcolor="#E2E6F0">Tama&#241;o de los iconos de la barra de navegaci&#243;n</TD>
              <TD class="testoNero" bgcolor="#D1D7E7"><INPUT TYPE="TEXT" NAME="intvar%icon size" size="5" class="testoNero" VALUE="{/USERMODEL/USERDATA/INTVAR[@name='icon size']/@value}"/></TD>
            </TR>
            <TR>
              <TD bgcolor="#E2E6F0" class="testoNero">Mostrar informaci&#243;n adicional</TD>
              <TD class="testoNero" bgcolor="#D1D7E7">
                <xsl:choose>
                  <xsl:when test="/USERMODEL/USERDATA/BOOLVAR[@name='show fancy']/@value = 'yes'">
                    <INPUT TYPE="CHECKBOX" NAME="boolvar%show fancy" checked="checked" class="testoNero"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <INPUT TYPE="CHECKBOX" NAME="boolvar%show fancy"/>
                  </xsl:otherwise>
                </xsl:choose>
              </TD>
              <TD class="testoNero" bgcolor="#E2E6F0">Mostrar atributos de im&#225;genes</TD>
              <TD class="testoNero" bgcolor="#D1D7E7">
                <xsl:choose>
                  <xsl:when test="/USERMODEL/USERDATA/BOOLVAR[@name='show images']/@value = 'yes'">
                    <INPUT TYPE="CHECKBOX" NAME="boolvar%show images" checked="checked"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <INPUT TYPE="CHECKBOX" NAME="boolvar%show images" class="testoNero"/>
                  </xsl:otherwise>
                </xsl:choose>
              </TD>
            </TR>
            <TR>
              <TD bgcolor="#E2E6F0" class="testoNero">Purgar aut. carpetas</TD>
              <TD class="testoNero" bgcolor="#D1D7E7">
                <xsl:choose>
                  <xsl:when test="/USERMODEL/USERDATA/BOOLVAR[@name='autoexpunge']/@value = 'yes'">
                    <INPUT TYPE="CHECKBOX" NAME="boolvar%autoexpunge" checked="checked"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <INPUT TYPE="CHECKBOX" NAME="boolvar%autoexpunge" class="testoNero"/>
                  </xsl:otherwise>
                </xsl:choose>
              </TD>
              <TD class="testoNero" bgcolor="#E2E6F0">Poner marcas de mensajes</TD>
              <TD class="testoNero" bgcolor="#D1D7E7">
                <xsl:choose>
                  <xsl:when test="/USERMODEL/USERDATA/BOOLVAR[@name='set message flags']/@value = 'yes'">
                    <INPUT TYPE="CHECKBOX" NAME="boolvar%set message flags" checked="checked" class="testoNero"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <INPUT TYPE="CHECKBOX" NAME="boolvar%set message flags"/>
                  </xsl:otherwise>
                </xsl:choose>
              </TD>
            </TR>
            <TR>
              <TD bgcolor="#E2E6F0" class="testoNero">Cortar l&#237;neas (en mostrar y redactar)</TD>
              <TD class="testoNero" bgcolor="#D1D7E7">
                <xsl:choose>
                  <xsl:when test="/USERMODEL/USERDATA/BOOLVAR[@name='break lines']/@value = 'yes'">
                    <INPUT TYPE="CHECKBOX" NAME="boolvar%break lines" checked="checked" class="testoNero"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <INPUT TYPE="CHECKBOX" NAME="boolvar%break lines" class="testoNero"/>
                  </xsl:otherwise>
                </xsl:choose>
              </TD>
              <TD class="testoNero" bgcolor="#E2E6F0">Longitud m&#225;x. de l&#237;nea</TD>
              <TD class="testoNero" bgcolor="#D1D7E7"><INPUT TYPE="TEXT" NAME="intvar%max line length" size="5" class="testoNero" VALUE="{/USERMODEL/USERDATA/INTVAR[@name='max line length']/@value}"/></TD>
            </TR>
            <TR>
              <TD height="22" colspan="4" bgcolor="#A6B1C0" align="center" class="testoGrande">Preferencias de redacci&#243;n</TD>
            </TR>
            <TR>
              <TD bgcolor="#E2E6F0" class="testoNero">Guardar correo enviado</TD>
              <TD class="testoNero" bgcolor="#D1D7E7">
                <xsl:choose>
                  <xsl:when test="/USERMODEL/USERDATA/BOOLVAR[@name='save sent messages']/@value = 'yes'">
                    <INPUT TYPE="CHECKBOX" NAME="boolvar%save sent messages" checked="checked" class="testoNero"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <INPUT TYPE="CHECKBOX" NAME="boolvar%save sent messages"/>
                  </xsl:otherwise>
                </xsl:choose>
              </TD>
              <TD class="testoNero" bgcolor="#E2E6F0"> en la carpeta </TD>
              <TD class="testoNero" bgcolor="#D1D7E7">
                <SELECT NAME="SENTFOLDER" class="testoNero">
                  <xsl:for-each select="/USERMODEL/MAILHOST_MODEL//FOLDER">
                    <xsl:choose>
                      <xsl:when test="normalize-space(/USERMODEL/USERDATA/SENT_FOLDER) = @id">
                        <OPTION value="{@id}" selected="selected"><xsl:value-of select="@name"/></OPTION>
                      </xsl:when>
                      <xsl:otherwise>
                        <OPTION value="{@id}"><xsl:value-of select="@name"/></OPTION>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:for-each>
                </SELECT>
              </TD>
            </TR>
            <TR>
              <TD bgcolor="#E2E6F0" class="testoNero">Direcci&#243;n de correo:</TD>
              <TD class="testoNero" bgcolor="#D1D7E7"><INPUT TYPE="TEXT" NAME="EMAIL" SIZE="40" VALUE="{normalize-space(/USERMODEL/USERDATA/EMAIL)}" class="testoNero" size="15"/></TD>
                <TD class="testoNero" bgcolor="#E2E6F0">&#160;</TD>
                <TD class="testoNero" bgcolor="#D1D7E7">&#160;</TD>
            </TR>
            <TR>
              <TD bgcolor="#E2E6F0" class="testoNero">Firma:</TD>
              <TD class="testoNero" bgcolor="#D1D7E7" colspan="3">
                <TEXTAREA ROWS="4" COLS="40" NAME="SIGNATURE" class="testoNero"><xsl:value-of select="/USERMODEL/USERDATA/SIGNATURE"/></TEXTAREA>
              </TD>
            </TR>
            <TR>
              <TD bgcolor="#E2E6F0" class="testoNero" align="center">
                  <INPUT TYPE="reset" VALUE="Reset" class="testoNero"/>
              </TD>
              <TD class="testoNero" bgcolor="#D1D7E7" colspan="3" align="center">
                  <INPUT TYPE="submit" VALUE="Submit" class="testoNero"/>
              </TD>
            </TR>
            <TR>
                <TD width="100%" height="22" colspan="4" bgcolor="#A6B1C0" align="center" class="testoGrande">&#160;
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
