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
  <xsl:output method="html" indent="no" xalan:content-handler="org.apache.xml.serializer.ToHTMLStream"/>

    <xsl:variable name="imgbase" select="/USERMODEL/STATEDATA/VAR[@name='img base uri']/@value"/>
    <xsl:variable name="base" select="/USERMODEL/STATEDATA/VAR[@name='base uri']/@value"/>
    <xsl:variable name="session-id" select="/USERMODEL/STATEDATA/VAR[@name='session id']/@value"/>

    <xsl:template match="/">

    <HTML>
      <HEAD>
        <TITLE>JWebMail Mailbox for <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/>: Showing message <xsl:value-of select="/USERMODEL/CURRENT[@type='message']/@id"/></TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
        <link rel="stylesheet" href="{$base}/passthrough/webmail.css"/>
      </HEAD>

      <BODY bgcolor="#B5C1CF" topmargin="5" leftmargin="0" marginwidth="0" marginheight="5">
<IMG SRC="{$imgbase}/images/icona_mail.gif" BORDER="0" hspace="5" align="absmiddle"/><SPAN class="testoNero">Showing message (<A HREF="{$base}/help?session-id={$session-id}&amp;helptopic=messagelist">Help</A>)</SPAN>
        <xsl:variable name="current_msg" select="/USERMODEL/CURRENT[@type='message']/@id"/>
        <xsl:variable name="current_folder" select="/USERMODEL/CURRENT[@type='folder']/@id"/>
        <xsl:apply-templates select="/USERMODEL/MAILHOST_MODEL//FOLDER[@id=$current_folder]//MESSAGE[@msgid=$current_msg]"/>
      </BODY>
    </HTML>
  </xsl:template>

  <xsl:template match="MESSAGE">

    <xsl:call-template name="navigation"/>

    <TABLE width="100%" border="0" cellspacing="2" cellpadding="4">
      <TR>
        <TD colspan="2" bgcolor="A6B1C0" height="22">
          <TABLE width="100%" border="0" cellpadding="0" cellspacing="0">
            <TR>
              <TD class="testoGrande">
                <xsl:apply-templates select="HEADER/SUBJECT"/>
              </TD>
            </TR>
          </TABLE>
        </TD>
      </TR>
      <TR>
        <TD height="22" class="testoGrande" width="13%" bgcolor="#E2E6F0">From:
        </TD>
        <TD height="22" class="testoNero" width="87%" bgcolor="#D1D7E7">
          <xsl:apply-templates select="HEADER/FROM"/>
        </TD>
      </TR>
      <TR>
        <TD height="22" class="testoGrande" width="13%" bgcolor="#E2E6F0">To:
        </TD>
        <TD height="22" class="testoNero" width="87%" bgcolor="#D1D7E7">
          <xsl:for-each select="HEADER/TO">
            <xsl:apply-templates select="."/>
          </xsl:for-each>
        </TD>
      </TR>
      <TR>
        <TD height="22" class="testoGrande" width="13%" bgcolor="#E2E6F0">Reply-To:
        </TD>
        <TD height="22" class="testoNero" width="87%" bgcolor="#D1D7E7">
          <xsl:apply-templates select="HEADER/REPLY_TO"/>
          <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        </TD>
      </TR>
      <TR>
        <TD height="22" class="testoGrande" width="13%" bgcolor="#E2E6F0">CC:
        </TD>
        <TD height="22" class="testoNero" width="87%" bgcolor="#D1D7E7">
          <xsl:for-each select="HEADER/CC">
            <xsl:apply-templates select="."/>
          </xsl:for-each>
          <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        </TD>
      </TR>
      <TR>
        <TD height="22" class="testoGrande" width="13%" bgcolor="#E2E6F0">Date:
        </TD>
        <TD height="22" class="testoNero" width="87%" bgcolor="#D1D7E7">
          <xsl:apply-templates select="HEADER/DATE"/>
        </TD>
      </TR>
      <TR>
        <TD COLSPAN="4" bgcolor="A6B1C0" ALIGN="right" class="testoNero">
          <A HREF="{$base}/compose?session-id={$session-id}&amp;folder-id={/USERMODEL/CURRENT[@type='folder']/@id}&amp;message-nr={@msgnr}&amp;reply=1">reply to this message...</A> -
          <A HREF="{$base}/compose?session-id={$session-id}&amp;folder-id={/USERMODEL/CURRENT[@type='folder']/@id}&amp;message-nr={@msgnr}&amp;forward=1">forward this message...</A>
        </TD>
      </TR>
    </TABLE>

    <xsl:for-each select="PART">

      <P class="testoMesg">
        <xsl:apply-templates select="."/>
      </P>
    </xsl:for-each>

    <TABLE WIDTH="100%" border="0" cellpadding="0" cellspacing="0" bgcolor="A6B1C0">
      <TR>
        <FORM ACTION="{$base}/folder/list?flag=1&amp;session-id={$session-id}&amp;folder-id={/USERMODEL/CURRENT[@type='folder']/@id}" METHOD="POST">
            <TD align="center" class="testoNero">
              <INPUT TYPE="HIDDEN" NAME="CH%{@msgnr}" VALUE="on"/>
              <SELECT NAME="MARK" class="testoNero">
                <OPTION VALUE="MARK">set</OPTION>
                <OPTION VALUE="UNMARK">remove</OPTION>
              </SELECT>
              <STRONG>message flag</STRONG>
              <SELECT NAME="MESSAGE FLAG" class="testoNero">
                <OPTION VALUE="DELETED">deleted</OPTION>
                <OPTION VALUE="SEEN">seen</OPTION>
                <OPTION VALUE="ANSWERED">answered</OPTION>
                <OPTION VALUE="RECENT">recent</OPTION>
                <OPTION VALUE="DRAFT">draft</OPTION>
              </SELECT>
              <INPUT TYPE="SUBMIT" NAME="flagmsgs" VALUE="Perform!" class="testoNero"/>
            </TD>
          </FORM>
          <FORM ACTION="{$base}/folder/showmsg?flag=1&amp;session-id={$session-id}&amp;folder-id={/USERMODEL/CURRENT[@type='folder']/@id}" METHOD="POST">
            <TD align="center" class="testoNero">
              <INPUT TYPE="HIDDEN" NAME="CH%{@msgnr}" VALUE="on"/>
              <SELECT NAME="COPYMOVE" class="testoNero">
                <OPTION VALUE="COPY">copy</OPTION>
                <OPTION VALUE="MOVE">move</OPTION>
              </SELECT>
              message to folder
              <SELECT NAME="TO" class="testoNero">
                <xsl:for-each select="/USERMODEL/MAILHOST_MODEL//FOLDER">
                  <OPTION value="{@id}"><xsl:value-of select="@name"/></OPTION>
                </xsl:for-each>
              </SELECT>
              <INPUT TYPE="SUBMIT" NAME="copymovemsgs" VALUE="Perform!" class="testoNero"/>
            </TD>
         </FORM>
       </TR>
    </TABLE>


    <xsl:call-template name="navigation"/>
  </xsl:template>

  <xsl:template match="PART">
    <xsl:choose>
      <xsl:when test="@type='text' and @hidden='true'" />
      <xsl:when test="@type='text' and not(@hidden='true')">
        <PRE>
          <xsl:for-each select="*">
            <xsl:apply-templates select="."/>
          </xsl:for-each>
        </PRE>
      </xsl:when>
      <xsl:when test="@type='html'">
        <xsl:for-each select="*">
          <xsl:apply-templates select="."/>
        </xsl:for-each>
      </xsl:when>
      <xsl:when test="@type='multi'">
        <xsl:for-each select="PART">
          <CENTER><P class="testoGrande">MIME part</P></CENTER><BR/>
          <xsl:apply-templates select="."/>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
        <TABLE width="100%" border="0" cellspacing="2" cellpadding="4">
          <TR>
            <TD height="22" class="testoGrande" width="13%" bgcolor="#E2E6F0">Attached file
            </TD>
            <TD height="22" class="testoNero" width="87%" bgcolor="#D1D7E7">
              <A HREF="{$base}/showmime/{@filename}?session-id={$session-id}&amp;msgid={ancestor::MESSAGE/@msgid}"><xsl:value-of select="@filename"/><xsl:value-of select="@name"/></A>
            </TD>
          </TR>
          <xsl:if test="@description != ''">
          <TR>
            <TD height="22" class="testoGrande" width="13%" bgcolor="#E2E6F0">Description:</TD>
            <TD height="22" class="testoNero" width="87%" bgcolor="#D1D7E7">
              <xsl:value-of select="@description"/>
              <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
            </TD>
          </TR>
          </xsl:if>
          <TR>
            <TD height="22" class="testoGrande" width="13%" bgcolor="#E2E6F0">Type:</TD>
            <TD height="22" class="testoNero" width="87%" bgcolor="#D1D7E7"><xsl:value-of select="@content-type"/></TD>
          </TR>
          <TR>
            <TD height="22" class="testoGrande" width="13%" bgcolor="#E2E6F0">Size:</TD>
            <TD height="22" class="testoNero" width="87%" bgcolor="#D1D7E7"><xsl:value-of select="@size"/> bytes</TD>
          </TR>
          <xsl:if test="@type='image' and /USERMODEL/USERDATA/BOOLVAR[@name='show images']/@value = 'yes'">
           <TR>
             <TD COLSPAN="2" bgcolor="#A6B1C0">
               <IMG SRC="{$base}/showmime/{@filename}?session-id={$session-id}&amp;msgid={ancestor::MESSAGE/@msgid}"/>
             </TD>
           </TR>
         </xsl:if>
       </TABLE>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="CONTENT">
    <xsl:choose>
      <xsl:when test="../@type = 'html'">
        <xsl:apply-templates select="*"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="." disable-output-escaping="yes" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="navigation">
      <TABLE width="100%" border="0" cellspacing="0" cellpadding="0">
        <TR>
          <xsl:choose>
             <xsl:when test="@msgnr > 1">
                <TD width="5%" bgcolor="#697791" valign="middle" align="center">
                  <A HREF="{$base}/folder/showmsg?session-id={$session-id}&amp;folder-id={/USERMODEL/CURRENT[@type='folder']/@id}&amp;message-nr={@msgnr - 1}">
                     <IMG SRC="{$imgbase}/images/back.gif" BORDER="0"/>
                  </A>
                </TD>
                <TD width="30%" bgcolor="#697791" valign="middle">
                  <A HREF="{$base}/folder/showmsg?session-id={$session-id}&amp;folder-id={/USERMODEL/CURRENT[@type='folder']/@id}&amp;message-nr={@msgnr - 1}"> <SPAN class="testoBianco">Previous message</SPAN>
                  </A>
                </TD>
                </xsl:when>
                <xsl:otherwise>
                <TD width="5%" bgcolor="#697791" valign="middle" align="center">
                  &#160;
                </TD>
                <TD class="testoBianco" width="30%" bgcolor="#697791" valign="middle">
                  &#160;
                </TD>
                </xsl:otherwise>
              </xsl:choose>
                <TD width="30%" bgcolor="#697791" valign="middle" align="center">
                  <A HREF="{$base}/folder/list?session-id={$session-id}&amp;folder-id={/USERMODEL/CURRENT[@type='folder']/@id}&amp;part={/USERMODEL/CURRENT[@type='folder']/@list_part}">
                    <IMG SRC="{$imgbase}/images/up.gif" BORDER="0"/>
                    <BR/><SPAN class="testoBianco">message list</SPAN>
                 </A>
                </TD>
            <xsl:variable name="current_folder" select="/USERMODEL/CURRENT[@type='folder']/@id"/>
              <xsl:choose>
                <xsl:when test="@msgnr &lt; /USERMODEL/MAILHOST_MODEL//FOLDER[@id=$current_folder]/MESSAGELIST/@total">
                <TD width="30%" bgcolor="#697791" valign="middle" align="right">
                 <A HREF="{$base}/folder/showmsg?session-id={$session-id}&amp;folder-id={/USERMODEL/CURRENT[@type='folder']/@id}&amp;message-nr={@msgnr + 1}">
                   <SPAN class="testoBianco">Next message</SPAN>
                 </A>
                </TD>
                <TD width="5%" bgcolor="#697791" valign="middle" align="center">
                  <A HREF="{$base}/folder/showmsg?session-id={$session-id}&amp;folder-id={/USERMODEL/CURRENT[@type='folder']/@id}&amp;message-nr={@msgnr + 1}">
                  <IMG SRC="{$imgbase}/images/next.gif" BORDER="0"/>
                  </A>
                </TD>
                </xsl:when>
                <xsl:otherwise>
                <TD width="30%" bgcolor="#697791" valign="middle" align="right">
                   &#160;
                </TD>
                <TD width="5%" bgcolor="#697791" valign="middle" align="center">
                  &#160;
                </TD>
              </xsl:otherwise>
            </xsl:choose>
        </TR>
      </TABLE>
  </xsl:template>

  <xsl:template match="DATE">
    <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="SUBJECT">
    <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="TO">
    <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="CC">
    <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="REPLY-TO">
    <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="FROM">
    <xsl:choose>
      <xsl:when test="contains(.,'&lt;')">
        <xsl:value-of select="substring-before(.,'&lt;')"/>,
        <xsl:value-of select="substring-before(substring-after(.,'&lt;'),'>')"/>
       </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="."/>
        </xsl:otherwise>
      </xsl:choose>
  </xsl:template>


  <!-- The following three sections deal with displaying HTML code from HTML attachments.
  Elements marked "malicious" will get a special treatment -->
  <xsl:template match="@*|node()">
    <xsl:choose>
      <xsl:when test="not(@malicious)">
        <xsl:copy>
          <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
      </xsl:when>
      <xsl:otherwise>
        <TABLE  BORDER="1" WIDTH="100%" CELLSPACING="0" CELLPADDING="0">
          <TR>
            <TD BGCOLOR="#FF9933">
              Untrusted HTML element removed: <xsl:value-of select="@malicious"/>
            </TD>
          </TR>
          <TR>
            <TD bgcolor="#3399FF" >
              <xsl:apply-templates select="." mode="quote"/>
            </TD>
          </TR>
        </TABLE>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="node()" mode="quote">
    <xsl:choose>
      <xsl:when test="name(.)">
        &lt;<xsl:value-of select="name(.)"/>
        <xsl:text> </xsl:text><xsl:apply-templates select="@*" mode="quote"/>
        <xsl:if test="./node()">
          &gt;
          <xsl:apply-templates select="node()" mode="quote"/>
          &lt;/<xsl:value-of select="name(.)"/>
        </xsl:if>
        &gt;
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="."/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="@*" mode="quote">
    <xsl:if test="name(.) != 'malicious'">
      <xsl:text> </xsl:text><xsl:value-of select="name(.)"/>="<xsl:value-of select="."/>"
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
