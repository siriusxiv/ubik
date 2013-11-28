<?xml version="1.0" encoding="ISO-8859-2"?>
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

    <xsl:template match="/">

    <HTML>
      <HEAD>
        <TITLE>JWebMail levélfiók <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/> számára</TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
        <link rel="stylesheet" href="{$base}/passthrough/webmail.css"/>
      </HEAD>

      <FRAMESET ROWS="65,*" border="0">
        <FRAME NAME="Title" SRC="{$base}/title?session-id={$session-id}" scrolling="no"/>
        <FRAMESET COLS="{/USERMODEL/USERDATA/INTVAR[@name='icon size']/@value + 46},*" border="0">
          <FRAME NAME="Content" SRC="{$base}/content?session-id={$session-id}" scrolling="auto"/>
          <FRAME NAME="Main" SRC="{$base}/mailbox?session-id={$session-id}&amp;force-refresh=1" scrolling="auto"/>
        </FRAMESET>
      </FRAMESET>
    </HTML>
  </xsl:template>
</xsl:stylesheet>
