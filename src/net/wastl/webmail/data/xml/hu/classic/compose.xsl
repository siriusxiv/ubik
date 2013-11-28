<?xml version="1.0" encoding="UTF-8"?>
<!--
$Id: compose.xsl 126 2008-10-31 03:41:09Z unsaved $

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
  <xsl:output method="html" encoding="UTF-8"/>

  <xsl:variable name="imgbase" select="/USERMODEL/STATEDATA/VAR[@name='img base uri']/@value"/>
  <xsl:variable name="base" select="/USERMODEL/STATEDATA/VAR[@name='base uri']/@value"/>
  <xsl:variable name="session-id" select="/USERMODEL/STATEDATA/VAR[@name='session id']/@value"/>

  <xsl:variable name="work" select="/USERMODEL/WORK/MESSAGE[position()=1]"/>

    <xsl:template match="/">

    <HTML>
      <HEAD>
        <TITLE>JWebMail Mailbox for <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/>: Showing message <xsl:value-of select="/USERMODEL/CURRENT[@type='message']/@id"/></TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
      </HEAD>

      <BODY bgcolor="#ffffff">
        <FORM ACTION="{$base}/send?session-id={$session-id}" METHOD="POST">
          <TABLE BGCOLOR="#dddddd" CELLSPACING="0" CELLPADDING="5" BORDER="0">
            <TR>
              <TD COLSPAN="3" VALIGN="CENTER"><IMG SRC="{$imgbase}/images/btn-compose.png" BORDER="0"/></TD>
              <TD VALIGN="CENTER">
                <FONT SIZE="+2"><STRONG>Composing a message...</STRONG></FONT> (<A HREF="{$base}/help?session-id={$session-id}&amp;helptopic=compose">Help</A>)<BR/>
                <EM>Date: <xsl:value-of select="/USERMODEL/STATEDATA/VAR[@name='date']/@value"/></EM>
              </TD>
            </TR>
            <TR>
             <TD><STRONG>From:</STRONG></TD>
             <TD>
               <select name="FROM" size="1">
                 <xsl:for-each select="/USERMODEL/USERDATA/EMAIL/ADDY">
                <xsl:choose>
                  <xsl:when test="$work/HEADER/FROM!=''">
                   <xsl:choose>
                         <xsl:when test="$work/HEADER/FROM=current()">
                           <option selected="selected">
                                  <xsl:value-of select="."/>
                            </option>
                          </xsl:when>
                          <xsl:otherwise>
                           <option>
                                <xsl:value-of select="."/>
                                  </option>
                          </xsl:otherwise>
                        </xsl:choose>
                  </xsl:when>
                <xsl:otherwise>
                   <xsl:choose>
                     <xsl:when test="@default='yes'">
                       <option selected="selected">
                              <xsl:value-of select="."/>
                            </option>
                          </xsl:when>
                          <xsl:otherwise>
                       <option>
                            <xsl:value-of select="."/>
                          </option>
                          </xsl:otherwise>
                        </xsl:choose>
              </xsl:otherwise>
            </xsl:choose>
             </xsl:for-each>
           </select>
           </TD>
           </TR>
           <TR>
              <TD><STRONG>To:</STRONG></TD>
              <TD><INPUT TYPE="TEXT" NAME="TO" SIZE="40" VALUE="{$work/HEADER/TO}"/></TD>
              <TD><STRONG>CarbonCopy:</STRONG></TD>
              <TD><INPUT TYPE="TEXT" NAME="CC" SIZE="40" VALUE="{$work/HEADER/CC}"/></TD>
            </TR>
            <TR>
              <TD><STRONG>Reply-To:</STRONG></TD>
              <TD><INPUT TYPE="TEXT" NAME="REPLY-TO" SIZE="40" VALUE="{$work/HEADER/REPLY_TO}"/></TD>
              <TD><STRONG>Blind CC:</STRONG></TD>
              <TD><INPUT TYPE="TEXT" NAME="BCC" SIZE="40" VALUE="{$work/HEADER/BCC}"/></TD>
            </TR>
            <TR>
              <TD><STRONG>Subject:</STRONG></TD>
              <TD COLSPAN="3"><INPUT TYPE="TEXT" NAME="SUBJECT" SIZE="80" VALUE="{$work/HEADER/SUBJECT}"/></TD>
            </TR>
            <TR>
              <TD><STRONG>Attachments:</STRONG></TD>
              <TD COLSPAN="3">
                <xsl:for-each select="$work/PART[position()=1]//PART[@type='binary']">
                  <xsl:apply-templates select="."/>
                </xsl:for-each>
                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
              </TD>
            </TR>
            <TR>
              <TD COLSPAN="2" ALIGN="LEFT"><INPUT TYPE="SUBMIT" VALUE="Send message..." NAME="SEND"/></TD>
              <TD COLSPAN="2" ALIGN="RIGHT"><INPUT TYPE="SUBMIT" VALUE="Attach files..." NAME="ATTACH"/></TD>
            </TR>
            <TR>
              <TD COLSPAN="4" BGCOLOR="#ffffff" ALIGN="CENTER">
                <TEXTAREA NAME="BODY" COLS="79" ROWS="50">
                  <xsl:for-each select="$work/PART[position()=1]/PART[position()=1]/CONTENT">
                    <xsl:apply-templates select="."/>
                  </xsl:for-each>
                </TEXTAREA>
              </TD>
            </TR>
            <TR>
              <TD COLSPAN="2" ALIGN="LEFT"><INPUT TYPE="SUBMIT" VALUE="Send message..." NAME="SEND"/></TD>
              <TD COLSPAN="2" ALIGN="RIGHT"><INPUT TYPE="SUBMIT" VALUE="Attach files..." NAME="ATTACH"/></TD>
            </TR>
          </TABLE>
        </FORM>
      </BODY>
    </HTML>
  </xsl:template>


  <!-- All parts that are attachments (= have a file name) should be displayed with their name and
       size only -->
  <xsl:template match="PART">
    <xsl:if test="@filename != ''">
      <xsl:value-of select="@filename"/> (<xsl:value-of select="@size"/> bytes),
    </xsl:if>
  </xsl:template>


  <!-- Content of a message should be displayed plain -->
  <xsl:template match="CONTENT">
    <xsl:value-of select="."/>
  </xsl:template>
</xsl:stylesheet>
