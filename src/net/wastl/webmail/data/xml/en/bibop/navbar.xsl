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
  <xsl:output method="html" indent="yes" xalan:content-handler="org.apache.xml.serializer.ToHTMLStream"/>

    <xsl:variable name="imgbase" select="/USERMODEL/STATEDATA/VAR[@name='img base uri']/@value"/>
    <xsl:variable name="base" select="/USERMODEL/STATEDATA/VAR[@name='base uri']/@value"/>
    <xsl:variable name="session-id" select="/USERMODEL/STATEDATA/VAR[@name='session id']/@value"/>
    <xsl:variable name="iconsize" select="/USERMODEL/USERDATA/INTVAR[@name='icon size']/@value"/>

    <xsl:template match="/">

    <HTML>
      <HEAD>
        <TITLE>JWebMail Mailbox for <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/>: Title Frame</TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
        <link rel="stylesheet" href="{$base}/passthrough/webmail.css"/>
      </HEAD>

      <BODY bgcolor="#818A9E" background="{$imgbase}/images/sfondo_sx.gif" leftmargin="5" topmargin="5" marginwidth="5" marginheight="5">
<TABLE width="50" border="0" cellspacing="0" cellpadding="0">
  <TR>
    <TD align="center">
        <A HREF="{$base}/mailbox?session-id={$session-id}" TARGET="Main" onMouseOver="self.status='This ContentProvider shows a list of all folders and links to the FolderList URLHandler.';"><IMG SRC="{$imgbase}/images/mailbox.gif" BORDER="0" ALT="MailboxList" WIDTH="{$iconsize}" HEIGTH="{$iconsize}"/></A>
    </TD>
  </TR>
  <TR>
    <TD height="30" align="center" valign="top">
        <A HREF="{$base}/mailbox?session-id={$session-id}" TARGET="Main" onMouseOver="self.status='This ContentProvider shows a list of all folders and links to the FolderList URLHandler.';">
          <P class="testoScuroSx">Mailbox<BR/>List</P>
        </A>
    </TD>
  </TR>
  <TR>
    <TD align="center">
        <A HREF="{$base}/compose?session-id={$session-id}" TARGET="Main" onMouseOver="self.status='This ContentProvider handles the composition of messages.';"><IMG SRC="{$imgbase}/images/composer.gif" BORDER="0" ALT="Composer" WIDTH="{$iconsize}" HEIGTH="{$iconsize}"/></A>
    </TD>
  </TR>
  <TR>
    <TD height="30" align="center" valign="top">
        <A HREF="{$base}/compose?session-id={$session-id}" TARGET="Main" onMouseOver="self.status='This ContentProvider handles the composition of messages.';">
          <P class="testoScuroSx">Message<BR/>
        Composer</P>
        </A>
    </TD>
  </TR>
  <TR>
    <TD align="center">
        <A HREF="{$base}/folder/setup?session-id={$session-id}" TARGET="Main" onMouseOver="self.status='This ContentProvider manages a users folder setup.';"><IMG SRC="{$imgbase}/images/folder_setup.gif" BORDER="0" ALT="FolderSetup" WIDTH="{$iconsize}" HEIGTH="{$iconsize}"/></A>
    </TD>
  </TR>
  <TR>
    <TD height="30" align="center" valign="top">
        <A HREF="{$base}/folder/setup?session-id={$session-id}" TARGET="Main" onMouseOver="self.status='This ContentProvider manages a users folder setup.';">
          <P class="testoScuroSx">Folder<BR/>Setup</P>
        </A>
    </TD>
  </TR>
  <TR>
    <TD align="center">
        <A HREF="{$base}/setup?session-id={$session-id}" TARGET="Main" onMouseOver="self.status='Change a users settings.';"><IMG SRC="{$imgbase}/images/other_setup.gif" BORDER="0" ALT="UserSetup" WIDTH="{$iconsize}" HEIGTH="{$iconsize}"/></A>
    </TD>
  </TR>
  <TR>
    <TD height="30" align="center" valign="top">
        <A HREF="{$base}/setup?session-id={$session-id}" TARGET="Main" onMouseOver="self.status='Change a users settings.';">
          <P class="testoScuroSx">User<BR/>
        Setup</P>
        </A>
    </TD>
  </TR>
  <TR>
    <TD align="center">
        <A HREF="{$base}/help?session-id={$session-id}" TARGET="Main" onMouseOver="self.status='This is the JWebMail help content-provider.';"><IMG SRC="{$imgbase}/images/help.gif" BORDER="0" ALT="WebMailHelp" WIDTH="{$iconsize}" HEIGTH="{$iconsize}"/></A>
    </TD>
  </TR>
  <TR>
    <TD height="30" align="center" valign="top">
<A HREF="{$base}/help?session-id={$session-id}" TARGET="Main" onMouseOver="self.status='This is the JWebMail help content-provider.';">
          <P class="testoScuroSx">JWebMail<BR/>
        Help</P>
        </A>
    </TD>
  </TR>
  <TR>
    <TD align="center">
        <A HREF="{$base}/logout?session-id={$session-id}" TARGET="_top" onMouseOver="self.status='ContentProvider plugin that closes an active JWebMail session.';"><IMG SRC="{$imgbase}/images/logout.gif" BORDER="0" ALT="LogoutSession" WIDTH="{$iconsize}" HEIGTH="{$iconsize}"/></A>
    </TD>
  </TR>
  <TR>
    <TD height="30" align="center" valign="top">
        <A HREF="{$base}/logout?session-id={$session-id}" TARGET="_top" onMouseOver="self.status='ContentProvider plugin that closes an active JWebMail session.';">
          <P class="testoScuroSx">Logout<BR/>
        Session</P>
        </A>
    </TD>
  </TR>
</TABLE>
        </BODY>
    </HTML>
  </xsl:template>
</xsl:stylesheet>
