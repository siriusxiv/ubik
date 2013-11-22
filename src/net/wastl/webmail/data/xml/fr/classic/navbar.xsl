<?xml version="1.0" encoding="ISO-8859-1"?>
<!--
$Id: navbar.xsl 126 2008-10-31 03:41:09Z unsaved $

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
    <xsl:variable name="iconsize" select="/USERMODEL/USERDATA/INTVAR[@name='icon size']/@value"/>

    <xsl:template match="/">

    <HTML>
      <HEAD>
        <TITLE>Boite aux Lettres JWebMail de <xsl:value-of select="/USERMODEL/USERDATA/FULL_NAME"/>: Page de Titre</TITLE>
        <META CONTENT="AUTHOR" VALUE="Sebastian Schaffert"/>
      </HEAD>

      <BODY bgcolor="#dddddd">

        <A HREF="{$base}/mailbox?session-id={$session-id}" TARGET="Main" onMouseOver="self.status='affiche la liste de tous les dossiers et liens.';"><IMG SRC="{$imgbase}/images/btn-mailbox.png" BORDER="0" ALT="Liste des Boites" WIDTH="{$iconsize}" HEIGTH="{$iconsize}"/></A>
        <BR/>
        <A HREF="{$base}/compose?session-id={$session-id}" TARGET="Main" onMouseOver="self.status='gere la composition des messages.';"><IMG SRC="{$imgbase}/images/btn-compose.png" BORDER="0" ALT="Composer Message" WIDTH="{$iconsize}" HEIGTH="{$iconsize}"/></A>
        <BR/>
        <A HREF="{$base}/folder/setup?session-id={$session-id}" TARGET="Main" onMouseOver="self.status='gere le parametrage dossier de l utilisateur.';"><IMG SRC="{$imgbase}/images/btn-folders.png" BORDER="0" ALT="Setup Dossier" WIDTH="{$iconsize}" HEIGTH="{$iconsize}"/></A>
        <BR/>
        <A HREF="{$base}/setup?session-id={$session-id}" TARGET="Main" onMouseOver="self.status='change les parametres utilisateurs.';"><IMG SRC="{$imgbase}/images/btn-setup.png" BORDER="0" ALT="Setup Utilisateur" WIDTH="{$iconsize}" HEIGTH="{$iconsize}"/></A>
        <BR/>
        <A HREF="{$base}/help?session-id={$session-id}" TARGET="Main" onMouseOver="self.status='fournit l Aide JWebMail.';"><IMG SRC="{$imgbase}/images/btn-help.png" BORDER="0" ALT="Aide JWebMail" WIDTH="{$iconsize}" HEIGTH="{$iconsize}"/></A>
        <BR/><A HREF="{$base}/logout?session-id={$session-id}" TARGET="_top" onMouseOver="self.status='ferme une session JWebMail active.';"><IMG SRC="{$imgbase}/images/btn-logout.png" BORDER="0" ALT="Logout Session" WIDTH="{$iconsize}" HEIGTH="{$iconsize}"/></A>
        <BR/>
      </BODY>
    </HTML>
  </xsl:template>
</xsl:stylesheet>
