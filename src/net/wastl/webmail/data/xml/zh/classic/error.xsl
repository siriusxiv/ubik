<?xml version="1.0" encoding="UTF-8"?>
<!--
$Id: error.xsl 126 2008-10-31 03:41:09Z unsaved $

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
        <TITLE>JWebMail Error Reporting</TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
        <META CONTENT="GENERATOR" VALUE="JWebMail 1.0.1 XSL"/>
      </HEAD>

      <BODY bgcolor="#ffffff">
        <TABLE WIDTH="100%">
          <TR BGCOLOR="red">
            <TD COLSPAN="2" align="center"><H1>An error ocurred</H1></TD>
          </TR>
          <TR>
            <TD><STRONG>Error message</STRONG></TD>
            <TD>
              <xsl:apply-templates select="//STATEDATA/EXCEPTION/EX_MESSAGE"/>
            </TD>
          </TR>
          <TR>
            <TD COLSPAN="2" ALIGN="center">
              <TABLE BGCOLOR="yellow" WIDTH="80%" BORDER="1">
                <TR>
                  <TD><STRONG>Stack Trace</STRONG><BR/>
                    <PRE>
                      <xsl:apply-templates select="//STATEDATA/EXCEPTION/EX_STACKTRACE"/>
                    </PRE>
                  </TD>
                </TR>
              </TABLE>
            </TD>
          </TR>
          <TR BGCOLOR="red">
            <TD COLSPAN="2" align="center">!</TD>
          </TR>
        </TABLE>
      </BODY>
    </HTML>
  </xsl:template>
</xsl:stylesheet>
