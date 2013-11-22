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
<!-- This is part of the French translation of JWebMail - Christian SENET - senet@lpm.u-nancy.fr - 2002 -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xslt" version="1.0">
  <xsl:output method="html" indent="yes" xalan:content-handler="org.apache.xml.serializer.ToHTMLStream"/>

    <xsl:variable name="imgbase" select="/USERMODEL/STATEDATA/VAR[@name='img base uri']/@value"/>
    <xsl:variable name="base" select="/USERMODEL/STATEDATA/VAR[@name='base uri']/@value"/>
    <xsl:variable name="session-id" select="/USERMODEL/STATEDATA/VAR[@name='session id']/@value"/>
    <xsl:variable name="iconsize" select="/USERMODEL/USERDATA/INTVAR[@name='icon size']/@value"/>

    <xsl:template match="/">

    <HTML>
      <HEAD>
        <TITLE>Boite aux Lettres JWebMail de <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/>: Page de Titre</TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
        <link rel="stylesheet" href="{$base}/passthrough/webmail.css"/>
      </HEAD>

      <BODY bgcolor="#818A9E" background="{$imgbase}/images/sfondo_sx.gif" leftmargin="5" topmargin="5" marginwidth="5" marginheight="5">
<TABLE width="50" border="0" cellspacing="0" cellpadding="0">
  <TR>
    <TD align="center">
        <A HREF="{$base}/mailbox?session-id={$session-id}" TARGET="Main" onMouseOver="self.status='affiche la liste de tous les dossiers et liens.';"><IMG SRC="{$imgbase}/images/mailbox.gif" BORDER="0" ALT="Liste des Boites" WIDTH="{$iconsize}" HEIGTH="{$iconsize}"/></A>
    </TD>
  </TR>
  <TR>
    <TD height="30" align="center" valign="top">
        <A HREF="{$base}/mailbox?session-id={$session-id}" TARGET="Main" onMouseOver="self.status='affiche la liste de tous les dossiers et liens.';">
          <P class="testoScuroSx">Liste<BR/>des Boîtes</P>
        </A>
    </TD>
  </TR>
  <TR>
    <TD align="center">
        <A HREF="{$base}/compose?session-id={$session-id}" TARGET="Main" onMouseOver="self.status='gere la composition des messages.';"><IMG SRC="{$imgbase}/images/composer.gif" BORDER="0" ALT="Composer Message" WIDTH="{$iconsize}" HEIGTH="{$iconsize}"/></A>
    </TD>
  </TR>
  <TR>
    <TD height="30" align="center" valign="top">
        <A HREF="{$base}/compose?session-id={$session-id}" TARGET="Main" onMouseOver="self.status='gere la composition des messages.';">
          <P class="testoScuroSx">Composer<BR/>
        Message</P>
        </A>
    </TD>
  </TR>
  <TR>
    <TD align="center">
        <A HREF="{$base}/folder/setup?session-id={$session-id}" TARGET="Main" onMouseOver="self.status='gere le parametrage dossier de l utilisateur.';"><IMG SRC="{$imgbase}/images/folder_setup.gif" BORDER="0" ALT="Setup Dossier" WIDTH="{$iconsize}" HEIGTH="{$iconsize}"/></A>
    </TD>
  </TR>
  <TR>
    <TD height="30" align="center" valign="top">
        <A HREF="{$base}/folder/setup?session-id={$session-id}" TARGET="Main" onMouseOver="self.status='gere le parametrage dossier de l utilisateur.';">
          <P class="testoScuroSx">Setup<BR/>Dossier</P>
        </A>
    </TD>
  </TR>
  <TR>
    <TD align="center">
        <A HREF="{$base}/setup?session-id={$session-id}" TARGET="Main" onMouseOver="self.status='change les parametres utilisateurs.';"><IMG SRC="{$imgbase}/images/other_setup.gif" BORDER="0" ALT="Setup Utilisateur" WIDTH="{$iconsize}" HEIGTH="{$iconsize}"/></A>
    </TD>
  </TR>
  <TR>
    <TD height="30" align="center" valign="top">
        <A HREF="{$base}/setup?session-id={$session-id}" TARGET="Main" onMouseOver="self.status='change les parametres utilisateurs.';">
          <P class="testoScuroSx">Setup<BR/>
        Utilisateur</P>
        </A>
    </TD>
  </TR>
  <TR>
    <TD align="center">
        <A HREF="{$base}/help?session-id={$session-id}" TARGET="Main" onMouseOver="self.status='fournit l Aide JWebMail.';"><IMG SRC="{$imgbase}/images/help.gif" BORDER="0" ALT="Aide JWebMail" WIDTH="{$iconsize}" HEIGTH="{$iconsize}"/></A>
    </TD>
  </TR>
  <TR>
    <TD height="30" align="center" valign="top">
<A HREF="{$base}/help?session-id={$session-id}" TARGET="Main" onMouseOver="self.status='fournit l Aide JWebMail.';">
          <P class="testoScuroSx">Aide<BR/>
        JWebMail</P>
        </A>
    </TD>
  </TR>
  <TR>
    <TD align="center">
        <A HREF="{$base}/logout?session-id={$session-id}" TARGET="_top" onMouseOver="self.status='ferme une session JWebMail active.';"><IMG SRC="{$imgbase}/images/logout.gif" BORDER="0" ALT="Logout Session" WIDTH="{$iconsize}" HEIGTH="{$iconsize}"/></A>
    </TD>
  </TR>
  <TR>
    <TD height="30" align="center" valign="top">
        <A HREF="{$base}/logout?session-id={$session-id}" TARGET="_top" onMouseOver="self.status='ferme une session JWebMail active.';">
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
