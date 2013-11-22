<?xml version="1.0" encoding="UTF-8"?>
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

<!-- This template is part of the German translation -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html" encoding="UTF-8"/>

    <xsl:template match="/">

    <HTML>
      <HEAD>
        <TITLE>JWebMail Mailbox für <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/>: Abmeldung</TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
        <META HTTP-EQUIV="REFRESH" CONTENT="5;URL={/USERMODEL/STATEDATA/VAR[@name='base uri']/@value}/"/>
      </HEAD>

      <BODY bgcolor="#ffffff">
        <H1><CENTER>Danke dass Du JWebMail benutzt!</CENTER></H1>
        <H3><CENTER>Bereite Abmeldung vor für <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/>.</CENTER></H3>
        <P>
          <CENTER>
            Die Benutzereinstellungen wurden gespeichert.
        <BR/>
            Wenn Du den <STRONG>Anmeldeschirm</STRONG> nicht in ein paar Sekunden siehst,
            <A HREF="{/USERMODEL/STATEDATA/VAR[@name='base uri']/@value}/">hier klicken</A>.
          </CENTER>
        </P>
        <P>
          <CENTER>
            <FONT SIZE="-">
              <EMPH>
                JWebMail ist (c)2008 von JWebMail Development Team und Sebastian Schaffert.
                Es steht unter den Bedingungen der Apache 2.0 License.
              </EMPH>
            </FONT>
          </CENTER>
        </P>
      </BODY>
    </HTML>
  </xsl:template>
</xsl:stylesheet>
