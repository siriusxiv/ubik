<?xml version="1.0" encoding="UTF-8"?>
<!--
$Id: admin-domains.xsl 126 2008-10-31 03:41:09Z unsaved $

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
  <xsl:template match="/">
    <HTML>
      <HEAD>
        <TITLE>Interfaz de administraci&#243;n de JWebMail: Configuraci&#243;n de dominios virtuales</TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
      </HEAD>
      <BODY BGCOLOR="#ffffff">
        <CENTER>
          <H1>Configuraci&#243;n de dominios virtuales de JWebMail</H1>
        </CENTER>
        <FORM ACTION="{$base}/admin/domain/set?session-id={$session-id}" METHOD="POST">
          <TABLE WIDTH="100%" CELLSPACING="0" CELLPADDING="0">
            <TR BGCOLOR="lightblue">
              <TD>
                <STRONG>Nombre del dominio</STRONG>
              </TD>
              <TD>
                <STRONG>Host por defecto</STRONG>
              </TD>
              <TD>
                <STRONG>Authentication Host</STRONG>
              </TD>
              <TD>
                <STRONG>Restricci&#243;n</STRONG>
              </TD>
              <TD><STRONG>Hosts permitidos</STRONG> (para la restricci&#243;n)</TD>
              <TD/>
            </TR>
            <xsl:for-each select="/GENERICMODEL/SYSDATA/DOMAIN">
              <TR>
                <TD>
                  <INPUT type="TEXT" name="{NAME} DOMAIN" value="{NAME}" SIZE="20"/>
                </TD>
                <TD>
                  <INPUT type="TEXT" name="{NAME} DEFAULT HOST" value="{DEFAULT_HOST}" SIZE="20"/>
                </TD>
                <TD>
                  <INPUT type="TEXT" name="{NAME} AUTH HOST" value="{AUTHENTICATION_HOST}" SIZE="20"/>
                </TD>
                <TD ALIGN="center">
                  <xsl:choose>
                    <xsl:when test="count(RESTRICTED) &gt; 0">
                      <INPUT type="CHECKBOX" checked="checked" name="{NAME} HOST RESTRICTION"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <INPUT type="CHECKBOX" name="{NAME} HOST RESTRICTION"/>
                    </xsl:otherwise>
                  </xsl:choose>
                </TD>
                <TD>
                  <INPUT type="TEXT" name="{NAME} ALLOWED HOSTS" value="{ALLOWED_HOST}" SIZE="40"/>
                </TD>
                <TD>
                  <INPUT type="submit" name="CHANGE {NAME}" value="Change"/>
                  <INPUT type="submit" name="DELETE {NAME}" value="Delete"/>
                </TD>
              </TR>
            </xsl:for-each>
            <TR>
              <TD>
                <INPUT type="TEXT" name="NEW DOMAIN" value="new" SIZE="20"/>
              </TD>
              <TD>
                <INPUT type="TEXT" name="NEW DEFAULT HOST" SIZE="20"/>
              </TD>
              <TD>
                <INPUT type="TEXT" name="NEW AUTH HOST" SIZE="20"/>
              </TD>
              <TD align="center">
                <INPUT type="CHECKBOX" name="NEW HOST RESTRICTION"/>
              </TD>
              <TD>
                <INPUT type="TEXT" name="NEW ALLOWED HOSTS" SIZE="40"/>
              </TD>
              <TD>
                <INPUT type="submit" name="ADD NEW" value="Add"/>
              </TD>
            </TR>
          </TABLE>
        </FORM>
      </BODY>
    </HTML>
  </xsl:template>
</xsl:stylesheet>
