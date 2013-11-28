<?xml version="1.0" encoding="UTF-8"?>
<!--
$Id: changepass.xsl 115 2008-10-30 00:57:01Z unsaved $

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

  <xsl:template name="changepass_header">
    <xsl:param name="cptmpl">normchangepass</xsl:param>
    <xsl:param name="verbose">yes</xsl:param>
    <xsl:param name="sd"/>

    <xsl:choose>

      <!-- a normal password change -->
      <xsl:when test="$cptmpl = 'normchangepass'">
      <!-- do nothing special -->
      </xsl:when>

      <!-- an OTP password change -->
      <xsl:when test="$cptmpl = 'otpchangepass'">
        <xsl:if test="$verbose = 'yes'">
          Enter A new challenge and you password into your OTP generator.
          Then input the result into the Password box below. <BR/>
        </xsl:if>
        New Challenge:
        <xsl:value-of select='$sd[@name="new challenge"]/@value'/>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="changepass_input">
    <xsl:param name="cptmpl">normchangepass</xsl:param>
    <xsl:param name="tag">password</xsl:param>
    <xsl:param name="sd"/>
    <xsl:variable name="plen" select="$sd[@name='pass len']/@value"/>

    <xsl:choose>
      <!-- a normal password change box -->
      <xsl:when test="$cptmpl = 'normchangepass'">
        <INPUT TYPE="PASSWORD" NAME="{$tag}" SIZE="{$plen}"/>
      </xsl:when>

      <!-- an OTP password change box -->
      <xsl:when test="$cptmpl = 'otpchangepass'">
        <INPUT TYPE="TEXT" NAME="{$tag}" SIZE="{$plen}"/>
      </xsl:when>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
