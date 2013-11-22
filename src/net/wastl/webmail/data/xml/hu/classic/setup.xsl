<?xml version="1.0" encoding="UTF-8"?>
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
  <xsl:output method="html" encoding="UTF-8"/>

    <xsl:variable name="imgbase" select="/USERMODEL/STATEDATA/VAR[@name='img base uri']/@value"/>
    <xsl:variable name="base" select="/USERMODEL/STATEDATA/VAR[@name='base uri']/@value"/>
    <xsl:variable name="session-id" select="/USERMODEL/STATEDATA/VAR[@name='session id']/@value"/>
    <xsl:variable name="themeset" select="/USERMODEL/STATEDATA/VAR[@name='themeset']/@value"/>
    <xsl:variable name="pass_change_file" select="/USERMODEL/STATEDATA/VAR[@name='pass change file']/@value"/>

    <xsl:template match="/">

    <HTML>
      <HEAD>
        <TITLE>JWebMail Mailbox for <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/>: Title Frame</TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
      </HEAD>

      <BODY bgcolor="#ffffff">

        <FORM ACTION="{$base}/setup/submit?session-id={$session-id}" METHOD="POST">
          <TABLE BGCOLOR="#dddddd" CELLSPACING="0" BORDER="0">
            <TR>
              <TD COLSPAN="2" VALIGN="CENTER">
                <IMG SRC="{$imgbase}/images/btn-setup.png"/>
              </TD>
              <TD COLSPAN="2" VALIGN="CENTER">
                <FONT SIZE="+2"><STRONG>JWebMail Setup for <xsl:value-of select="normalize-space(/USERMODEL/USERDATA/FULL_NAME)"/></STRONG></FONT> (<A HREF="{$base}/help?session-id={$session-id}&amp;helptopic=setup">Help</A>)<BR/>
                <EM>Login name <xsl:value-of select="normalize-space(/USERMODEL/USERDATA/LOGIN)"/></EM><BR/>
                <EM>Account exists since <xsl:apply-templates select="/USERMODEL/STATEDATA/VAR[@name='first login']"/></EM>
              </TD>
            </TR>
            <TR>
              <TD COLSPAN="4" BGCOLOR="#aaaaaa" ALIGN="CENTER"><EM><FONT SIZE="+1">General settings</FONT></EM></TD>
            </TR>
            <TR>
              <TD WIDTH="10%"><STRONG>Full Name:</STRONG></TD>
              <TD WIDTH="40%"><INPUT TYPE="TEXT" NAME="FULLNAME" VALUE="{normalize-space(/USERMODEL/USERDATA/FULL_NAME)}" SIZE="30"/></TD>
              <TD><STRONG>Language:</STRONG></TD>
              <TD>
                <SELECT NAME="LANGUAGE">
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
            </TR>
            <TR>
              <TD><STRONG>Theme:</STRONG></TD>
              <TD COLSPAN="3">
                <SELECT NAME="THEME">
                  <xsl:for-each select="/USERMODEL/STATEDATA/VAR[@name = $themeset]">
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
              <TD COLSPAN="4" BGCOLOR="#aaaaaa" ALIGN="CENTER"><EM><FONT SIZE="+1">Password settings</FONT></EM></TD>
        </TR>
        <xsl:variable name="cptmpl" select="/USERMODEL/STATEDATA/VAR[@name='pass change tmpl']/@value"/>
        <TR>
          <TD WIDTH="100%" COLSPAN="4" ALIGN="CENTER">
            <xsl:call-template name="changepass_header">
              <xsl:with-param name="cptmpl" select="$cptmpl"/>
              <xsl:with-param name="sd" select="/USERMODEL/STATEDATA/VAR"/>
              <xsl:with-param name="verbose">yes</xsl:with-param>
            </xsl:call-template>
          </TD>
        </TR>
        <TR>
          <TD WIDTH="10%"><STRONG>Password:</STRONG></TD>
          <TD WIDTH="40%">
            <xsl:call-template name="changepass_input">
              <xsl:with-param name="cptmpl" select="$cptmpl"/>
              <xsl:with-param name="sd" select="/USERMODEL/STATEDATA/VAR"/>
              <xsl:with-param name="tag">PASSWORD</xsl:with-param>
            </xsl:call-template>
          </TD>
          <TD WIDTH="10%"><STRONG>Verify:</STRONG></TD>
          <TD WIDTH="40%">
            <xsl:call-template name="changepass_input">
              <xsl:with-param name="cptmpl" select="$cptmpl"/>
              <xsl:with-param name="sd" select="/USERMODEL/STATEDATA/VAR"/>
              <xsl:with-param name="tag">VERIFY</xsl:with-param>
            </xsl:call-template>
          </TD>
        </TR>
            <TR>
              <TD COLSPAN="4" BGCOLOR="#aaaaaa" ALIGN="CENTER"><EM><FONT SIZE="+1">Mailbox display settings</FONT></EM></TD>
            </TR>
            <TR>
              <TD><STRONG>Messages per page</STRONG></TD>
              <TD><INPUT TYPE="TEXT" NAME="intvar%max show messages" SIZE="3" VALUE="{/USERMODEL/USERDATA/INTVAR[@name='max show messages']/@value}"/></TD>
              <TD><STRONG>Navigation bar icon size</STRONG></TD>
              <TD><INPUT TYPE="TEXT" NAME="intvar%icon size" SIZE="3" VALUE="{/USERMODEL/USERDATA/INTVAR[@name='icon size']/@value}"/></TD>
            </TR>
            <TR>
              <TD><STRONG>Show fancy stuff</STRONG></TD>
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
              <TD><STRONG>Show att. images</STRONG></TD>
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
              <TD><STRONG>Autoexpunge folders</STRONG></TD>
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
              <TD><STRONG>Set message flags</STRONG></TD>
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
              <TD><STRONG>Break lines (show and compose)</STRONG></TD>
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
              <TD><STRONG>max line length</STRONG></TD>
              <TD><INPUT TYPE="TEXT" NAME="intvar%max line length" SIZE="3" VALUE="{/USERMODEL/USERDATA/INTVAR[@name='max line length']/@value}"/></TD>
            </TR>
            <TR>
              <TD COLSPAN="4" BGCOLOR="#aaaaaa" ALIGN="CENTER"><EM><FONT SIZE="+1">Compose settings</FONT></EM></TD>
            </TR>
            <TR>
              <TD><STRONG>Save sent mail</STRONG></TD>
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
              <TD><STRONG>in folder</STRONG></TD>
              <TD>
                <SELECT NAME="SENTFOLDER">
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
              <TD><STRONG>Email Address:</STRONG></TD>
              <TD>
                <select name="EMAIL" size="1">
                  <xsl:for-each select="/USERMODEL/USERDATA/EMAIL/ADDY">
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
                   </xsl:for-each>
                 </select>
              </TD>
                <TD><input name="SETDEFAULT" type="submit" value="Set default"/></TD>
                <TD><input name="DELETEEMAIL" type="submit" value="Delete"/></TD>
            </TR>
            <TR>
              <TD><STRONG>Add new email:</STRONG></TD>
              <TD><input name="NEWEMAIL" type="text"/></TD>
              <TD><input name="ADDNEW" type="submit" value="Add"/></TD>
              <TD>&#160;</TD>
            </TR>
            <TR>
              <TD VALIGN="TOP"><STRONG>Signature:</STRONG></TD>
              <TD COLSPAN="3">
                <TEXTAREA ROWS="5" COLS="79" NAME="SIGNATURE"><xsl:value-of select="/USERMODEL/USERDATA/SIGNATURE"/></TEXTAREA>
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

  <xsl:include href="changepass.xsl"/>
</xsl:stylesheet>
