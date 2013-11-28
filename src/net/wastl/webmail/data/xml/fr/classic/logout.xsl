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
<!-- This is part of the French translation of JWebMail - Christian SENET - senet@lpm.u-nancy.fr - 2002 -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html" encoding="UTF-8"/>

    <xsl:template match="/">

    <HTML>
      <HEAD>
        <TITLE>Boite aux Lettres de <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/>: Page de Titre</TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
        <META HTTP-EQUIV="REFRESH" CONTENT="5;URL={/USERMODEL/STATEDATA/VAR[@name='base uri']/@value}/"/>
      </HEAD>

      <BODY bgcolor="#ffffff">
        <H1><CENTER>Merci d'utiliser JWebMail!</CENTER></H1>
        <H3><CENTER>Préparation du logout de <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/>.</CENTER></H3>
        <P>
          <CENTER>
            Merci de patienter le temps que votre session soit fermée et que votre configuration
            soit écrite sur disque.<BR/>
            Si vous ne voyez pas la <STRONG>fenêtre de login</STRONG> dans quelques secondes, alors
            <A HREF="{/USERMODEL/STATEDATA/VAR[@name='base uri']/@value}/">cliquez ici</A>.
          </CENTER>
        </P>
        <P>
          <CENTER>
            <FONT SIZE="-1">
              <EMPH>
                JWebMail is (c)2008 by the JWebMail Development Team and Sebastian Schaffert.
                It is distributed under the terms of the Apache 2.0 License.
              </EMPH>
            </FONT>
          </CENTER>
        </P>
      </BODY>
    </HTML>
  </xsl:template>
</xsl:stylesheet>
