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
        <TITLE>JWebMail Mailbox for <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/>: Message List (Folder <xsl:value-of select="/USERMODEL/CURRENT[@type='folder']/@id"/></TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
        <link rel="stylesheet" href="{$base}/passthrough/webmail.css"/>
      </HEAD>

      <BODY bgcolor="#B5C1CF" topmargin="5" leftmargin="0" marginwidth="0" marginheight="5">
        <TABLE width="100%" border="0" cellspacing="2" cellpadding="2">
        <xsl:variable name="current" select="/USERMODEL/CURRENT[@type='folder']/@id"/>
            <FORM ACTION="{$base}/folder/list?flag=1&amp;session-id={$session-id}&amp;folder-id={$current}&amp;part={/USERMODEL/CURRENT[@type='folder']/@list_part}" METHOD="POST">
        <xsl:apply-templates select="/USERMODEL/MAILHOST_MODEL//FOLDER[@id=$current]"/>
                   </FORM>
                </TABLE>
      </BODY>
    </HTML>
  </xsl:template>


  <xsl:template match="FOLDER">
    <TR>
      <TD width="100%" colspan="7" height="22" class="testoNero"><IMG SRC="{$imgbase}/images/icona_mail.gif" BORDER="0" align="absmiddle"/>
      Üzenetek megjelnítése <xsl:value-of select="/USERMODEL/CURRENT[@type='folder']/@first_msg"/>
      tól/től <xsl:value-of select="/USERMODEL/CURRENT[@type='folder']/@last_msg"/> -ig
      a <xsl:value-of select="@name"/> mappában (<A HREF="{$base}/help?session-id={$session-id}&amp;helptopic=messagelist">Súgó</A>).
     </TD>
    </TR>
    <xsl:call-template name="navigation"/>

    <xsl:apply-templates select="MESSAGELIST"/>

    <TR>
      <TD width="100%" colspan="7" align="center" class="testoNero" bgcolor="#A6B1C0">
        <TABLE width="100%" border="0" cellspacing="0" cellpadding="0">
          <TR>
            <TD class="testo"><SPAN class="bold">Üzenet jelölők:</SPAN></TD>
            <TD class="testo"><IMG SRC="{$imgbase}/images/attach.gif" BORDER="0" hspace="2"/></TD>
            <TD class="testo">üzenet csatolással</TD>
            <TD class="testo"><IMG SRC="{$imgbase}/images/new.gif" BORDER="0" hspace="2"/></TD>
            <TD class="testo">új üzenet</TD>
            <TD class="testo"><IMG SRC="{$imgbase}/images/seen.gif" BORDER="0" hspace="2"/></TD>
            <TD class="testo">megtekintett üzenet</TD>
            <TD class="testo"><IMG SRC="{$imgbase}/images/reply.gif" BORDER="0" hspace="2"/></TD>
            <TD class="testo">megválaszolt üzenet</TD>
            <TD class="testo"><IMG SRC="{$imgbase}/images/delete.gif" BORDER="0" hspace="2"/></TD>
            <TD class="testo">törölt üzenet</TD>
          </TR>
         </TABLE>
           </TD>
          </TR>
    <xsl:call-template name="navigation"/>
  </xsl:template>


  <xsl:template match="MESSAGELIST">
        <TR>
          <TD width="5%" height="22" class="testoNero" align="center" bgcolor="#A6B1C0"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></TD>
          <TD width="5%" class="testoGrande" bgcolor="#A6B1C0">Szám</TD>
          <TD  width="7%" class="testoGrande" bgcolor="#A6B1C0">Jelölő</TD>
          <TD width="28%" class="testoGrande" bgcolor="#A6B1C0">Tárgy</TD>
          <TD width="25%" class="testoGrande" bgcolor="#A6B1C0">Küldő</TD>
          <TD width="25%" class="testoGrande" bgcolor="#A6B1C0">Dátum</TD>
          <TD width="5%" class="testoGrande" bgcolor="#A6B1C0">Méret</TD>
        </TR>
        <xsl:for-each select="MESSAGE[number(@msgnr) >= number(/USERMODEL/CURRENT[@type='folder']/@first_msg) and number(@msgnr) &lt;= number(/USERMODEL/CURRENT[@type='folder']/@last_msg)]">
          <xsl:sort select="@msgnr" data-type="number" order="descending"/>
          <!--
          <xsl:variable name="bgcolor" select="##E2E6F0"/>
          -->
          <xsl:choose>
            <xsl:when test="@msgnr mod 2 = 1">
              <TR bgcolor="#E2E6F0">
                <xsl:call-template name="headerrow"/>
              </TR>
            </xsl:when>
            <xsl:otherwise>
              <TR bgcolor="#D1D7E7">
                <xsl:call-template name="headerrow"/>
              </TR>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:for-each>

        <TR>
            <TD colspan="4" width="45%" align="center" class="testoNero" bgcolor="#D1D7E7">
              <SELECT NAME="MARK" class="testoNero">
                <OPTION VALUE="MARK">beállít</OPTION>
                <OPTION VALUE="UNMARK">eltávolít</OPTION>
              </SELECT>
              üzenet jelölő
              <SELECT NAME="MESSAGE FLAG" class="testoNero">
                <OPTION VALUE="DELETED">törölt</OPTION>
                <OPTION VALUE="SEEN">megtekintett</OPTION>
                <OPTION VALUE="ANSWERED">megválaszolt</OPTION>
                <OPTION VALUE="RECENT">új</OPTION>
                <OPTION VALUE="DRAFT">piszkozat</OPTION>
              </SELECT>
              <INPUT TYPE="SUBMIT" NAME="flagmsgs" VALUE="Végrehajt!" class="testoNero"/>
            </TD>
            <TD colspan="3" width="55%" align="center" class="testoNero" bgcolor="#D1D7E7">
              <SELECT NAME="COPYMOVE" class="testoNero">
                <OPTION VALUE="COPY">másold</OPTION>
                <OPTION VALUE="MOVE">mozgasd</OPTION>
              </SELECT>
              az üzeneteket a következő mappába
              <SELECT NAME="TO" class="testoNero">
                <xsl:for-each select="/USERMODEL/MAILHOST_MODEL//FOLDER">
                  <OPTION value="{@id}"><xsl:value-of select="@name"/></OPTION>
                </xsl:for-each>
              </SELECT>
              <INPUT TYPE="SUBMIT" NAME="copymovemsgs" VALUE="Végrehajt!" class="testoNero"/>
            </TD>
        </TR>
  </xsl:template>

  <xsl:template name="navigation">

        <TR>
          <TD colspan="7" width="100%" height="22" class="testoNero" align="center">
            <TABLE width="100%" border="0" cellspacing="0" cellpadding="0">
              <TR>
               <xsl:choose>
                <xsl:when test="number(/USERMODEL/CURRENT[@type='folder']/@first_msg) > number(1)">
                  <TD class="testoBianco" width="3%" bgcolor="#697791" valign="middle" align="center">
                    <A HREF="{$base}/folder/list?session-id={$session-id}&amp;folder-id={@id}&amp;part={/USERMODEL/CURRENT[@type='folder']/@list_part + 1}">
                    <IMG SRC="{$imgbase}/images/back.gif" BORDER="0"/></A>
                  </TD>
                  <TD class="testoBianco" width="47%" bgcolor="#697791" valign="middle"><A HREF="{$base}/folder/list?session-id={$session-id}&amp;folder-id={@id}&amp;part={/USERMODEL/CURRENT[@type='folder']/@list_part + 1}"><SPAN class="testoBianco"> Previous <xsl:value-of select="/USERMODEL/USERDATA/INTVAR[@name='max show messages']/@value"/> üzenetek</SPAN></A>
                  </TD>
                </xsl:when>
                <xsl:otherwise>
                  <TD class="testoBianco" width="3%" bgcolor="#697791" valign="middle" align="center" height="22">
                  &#160;
                </TD>
                <TD class="testoBianco" width="47%" bgcolor="#697791" valign="middle" height="22">
                  &#160;
                </TD>
               </xsl:otherwise>
              </xsl:choose>

              <xsl:choose>
                <xsl:when test="number(/USERMODEL/CURRENT[@type='folder']/@list_part) > number(1)">
                  <TD class="testoBianco" width="47%" bgcolor="#697791" valign="middle" align="right">
                    <A HREF="{$base}/folder/list?session-id={$session-id}&amp;folder-id={@id}&amp;part={/USERMODEL/CURRENT[@type='folder']/@list_part - 1}"><SPAN class="testoBianco"> Next <xsl:value-of select="/USERMODEL/USERDATA/INTVAR[@name='max show messages']/@value"/> üzenetek</SPAN></A>
                   </TD>
                   <TD class="testoBianco" width="3%" bgcolor="#697791" valign="middle" align="center">
                   <A HREF="{$base}/folder/list?session-id={$session-id}&amp;folder-id={@id}&amp;part={/USERMODEL/CURRENT[@type='folder']/@list_part - 1}">
                    <IMG SRC="{$imgbase}/images/next.gif" BORDER="0"/></A></TD>
                </xsl:when>
                <xsl:otherwise>
                <TD class="testoBianco" width="47%" bgcolor="#697791" valign="middle" align="right" height="22">
                   &#160;
                </TD>
                <TD class="testoBianco" width="3%" bgcolor="#697791" valign="middle" align="center">
                   &#160;
                </TD>
                </xsl:otherwise>
              </xsl:choose>
             </TR>
           </TABLE>
          </TD>
        </TR>
  </xsl:template>

  <xsl:template name="headerrow">
    <TD width="5%" align="center" class="testoNero"><INPUT TYPE="checkbox" NAME="CH%{@msgnr}" class="testoNero"/></TD>
    <TD width="5%" class="testoNero"><xsl:value-of select="@msgnr"/></TD>
    <TD width="7%">
      <xsl:if test="@attachment='true'">
        <IMG SRC="{$imgbase}/images/attach.gif" BORDER="0" hspace="2"/>
      </xsl:if>
      <xsl:if test="@recent='true'">
        <IMG SRC="{$imgbase}/images/new.gif" BORDER="0" hspace="2"/>
      </xsl:if>
      <xsl:if test="@seen='true'">
        <IMG SRC="{$imgbase}/images/seen.gif" BORDER="0" hspace="2"/>
      </xsl:if>
      <xsl:if test="@answered='true'">
        <IMG SRC="{$imgbase}/images/reply.gif" BORDER="0" hspace="2"/>
      </xsl:if>
      <xsl:if test="@deleted='true'">
        <IMG SRC="{$imgbase}/images/delete.gif" BORDER="0" hspace="2"/>
      </xsl:if>
    </TD>
    <TD width="28%" class="testoNero">
      <xsl:choose>
        <xsl:when test="@seen='true'">
          <A HREF="{$base}/folder/showmsg?session-id={$session-id}&amp;folder-id={/USERMODEL/CURRENT[@type='folder']/@id}&amp;message-nr={@msgnr}" class="seen"><xsl:apply-templates select="HEADER/SUBJECT"/></A>
        </xsl:when>
        <xsl:otherwise>
          <A HREF="{$base}/folder/showmsg?session-id={$session-id}&amp;folder-id={/USERMODEL/CURRENT[@type='folder']/@id}&amp;message-nr={@msgnr}" class="unseen">
            <xsl:apply-templates select="HEADER/SUBJECT"/>
          </A>
        </xsl:otherwise>
      </xsl:choose>
    </TD>
    <TD width="25%" class="testoNero">
      <xsl:apply-templates select="HEADER/FROM"/>
    </TD>
    <TD width="25%" class="testoNero">
      <xsl:apply-templates select="HEADER/DATE"/>
    </TD>
    <TD width="5%" class="testoNero">
        <xsl:value-of select="@size"/>
    </TD>
  </xsl:template>

  <xsl:template match="DATE">
    <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="SUBJECT">
    <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="FROM">
      <xsl:choose>
        <xsl:when test="contains(.,'&lt;')">
          <xsl:value-of select="substring-before(.,'&lt;')"/><BR/>
          <SPAN class="bold">EMail:</SPAN> <xsl:value-of select="substring-before(substring-after(.,'&lt;'),'>')"/>
        </xsl:when>
        <xsl:otherwise>
          <BR/>
          <SPAN class="bold">EMail:</SPAN> <xsl:value-of select="."/>
        </xsl:otherwise>
      </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
