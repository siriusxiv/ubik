<?xml version="1.0" encoding="ISO-8859-1"?>
<!--
$Id: logout.xsl 126 2008-10-31 03:41:09Z unsaved $

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
  <xsl:template match="/">
    <HTML>
      <HEAD>
        <TITLE>Buz&#243;n de JWebMail para <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/>: Marco de t&#237;tulo</TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
        <META HTTP-EQUIV="REFRESH" CONTENT="5;URL={/USERMODEL/STATEDATA/VAR[@name='base uri']/@value}/"/>
      </HEAD>
      <BODY bgcolor="#ffffff">
        <H1>
          <CENTER>&#161;Gracias por usar Webmail!</CENTER>
        </H1>
        <H3>
          <CENTER>Preparando desconexi&#243;n para <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/>.</CENTER>
        </H3>
        <P>
          <CENTER>
            Por favor, espera mientras se cierra la sesi&#243;n y se guarda
            la configuración en el disco.<BR/>
            Si no ves la <STRONG>pantalla de conexi&#243;n</STRONG> en unos pocos segundos, por favor
            <A HREF="{/USERMODEL/STATEDATA/VAR[@name='base uri']/@value}/">haz click aqu&#237;</A>.
          </CENTER>
        </P>
        <P>
          <CENTER>
            <FONT SIZE="-1">
              <EMPH>
                JWebMail es &#169; 2008 de JWebMail Development Team y Sebastian Schaffert.
                Est&#225; distribu&#237;do bajo los t&#233;rminos de la Apache 2.0 License.
              </EMPH>
            </FONT>
          </CENTER>
        </P>
      </BODY>
    </HTML>
  </xsl:template>
</xsl:stylesheet>
