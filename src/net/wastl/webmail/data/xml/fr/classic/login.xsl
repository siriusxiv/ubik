<?xml version="1.0" encoding="ISO-8859-1"?>
<!--
$Id: login.xsl 126 2008-10-31 03:41:09Z unsaved $

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

    <xsl:variable name="imgbase" select="/USERMODEL/STATEDATA/VAR[@name='img base uri']/@value"/>
    <xsl:variable name="base" select="/USERMODEL/STATEDATA/VAR[@name='base uri']/@value"/>
    <xsl:variable name="session-id" select="/USERMODEL/STATEDATA/VAR[@name='session id']/@value"/>

    <xsl:template match="/">

    <HTML>
      <HEAD>
        <TITLE>Boite aux Lettres JWebMail de <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/></TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
      </HEAD>

      <FRAMESET ROWS="85,*" border="0">
        <FRAME NAME="Title" SRC="{$base}/title?session-id={$session-id}" scrolling="no"/>
        <FRAMESET COLS="{/USERMODEL/USERDATA/INTVAR[@name='icon size']/@value + 15},*" border="0">
          <FRAME NAME="Content"  SRC="{$base}/content?session-id={$session-id}" scrolling="auto"/>
          <FRAME NAME="Main"   SRC="{$base}/mailbox?session-id={$session-id}&amp;force-refresh=1" scrolling="auto"/>
        </FRAMESET>
      </FRAMESET>
    </HTML>
  </xsl:template>
</xsl:stylesheet>
