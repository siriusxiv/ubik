/*
 * @(#)$Id: TranscodeUtil.java 37 2008-10-24 17:49:26Z unsaved $
 *
 * Copyright 2008 by the JWebMail Development Team and Sebastian Schaffert.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.bulbul.webmail.util;

import java.util.Locale;

import javax.mail.internet.MimeUtility;


/**
 * TranscodeUtil.
 *
 * Provides transcoding utilities.
 *
 * @author              Steve Excellent Lee.
 */
public class TranscodeUtil {
    /**
     * Why we need
     * org.bulbul.util.TranscodeUtil.transcodeThenEncodeByLocale()?
     *
         * Because we specify client browser's encoding to UTF-8, IE seems
     * to send all data encoded in UTF-8. That means the byte sequences
     * we received are all UTF-8 bytes. However, strings read from HTTP
     * is ISO8859_1 encoded, that's we need to transcode them (usually
     * from ISO8859_1 to UTF-8.
     * Next we encode those strings using MimeUtility.encodeText() depending
     * on user's locale. Since MimeUtility.encodeText() is used to convert
     * the strings into its transmission format, finally we can use the
     * strings in the outgoing e-mail, then receiver's email agent is
     * responsible for decoding the strings.
     *
     * As described in JavaMail document, MimeUtility.encodeText() conforms
     * to RFC2047 and as a result, we'll get strings like "=?Big5?B......".
     * @param   sourceString                    String to be encoded
     * @param   sourceStringEncoding    The encoding to decode `sourceString'
     *                                                                  string. If `sourceStringEncoding'
     *                                                                  is null, use JVM's default enconding.
     * @param   Locale                                  prefered locale
     *
     * @return  empty string(prevent NullPointerException) if sourceString
     *                  is null or empty("");
     *                  otherwise RFC2047 conformed string, eg, "=?Iso8859-1?Q....."
     */
    public static String transcodeThenEncodeByLocale(
                                                     String sourceString,
                                                     String sourceStringEncoding,
                                                     Locale locale)
        throws java.io.UnsupportedEncodingException {
        String str;

        if ((sourceString == null) || (sourceString.equals("")))
            return "";

        // Transcode to UTF-8
        if ((sourceStringEncoding == null) ||
            (sourceStringEncoding.equals("")))
            str = new String(sourceString.getBytes(), "UTF-8");
        else
            str = new String(sourceString.getBytes(sourceStringEncoding),"UTF-8");

        // Encode text
        if (locale.getLanguage().equals("zh") && locale.getCountry().equals("TW")) {
            return MimeUtility.encodeText(str, "Big5", null);
        }
        return MimeUtility.encodeText(str);
    }
}
