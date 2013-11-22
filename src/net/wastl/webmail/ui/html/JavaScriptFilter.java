/*
 * @(#)$Id: JavaScriptFilter.java 101 2008-10-29 01:15:14Z unsaved $
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


package net.wastl.webmail.ui.html;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Filter JavaScript content from HTML messages to avoid security problems.
 *
 * @author Sebastian Schaffert
 */
public class JavaScriptFilter  {
    private static Log log = LogFactory.getLog(JavaScriptFilter.class);

    private static Pattern[] filter = new Pattern[3];
    private static String[] substitution = new String[3];

    private static boolean initialized=false;

    public JavaScriptFilter() {
    }

    public static void init() {
        try {
            filter[0]=Pattern.compile("<\\s*SCRIPT[^>]*>",Pattern.CASE_INSENSITIVE);
            filter[1]=Pattern.compile("<\\s*\\/SCRIPT[^>]*>",Pattern.CASE_INSENSITIVE);
            filter[2]=Pattern.compile("<\\s*A +HREF *=.*\"(javascript:[^\"]*)\"[^>]*>([^<]+)</A>",Pattern.CASE_INSENSITIVE);

            substitution[0]="<P><FONT color=\"red\">WebMail security: JavaScript filtered</FONT>:<BR>\n<HR>\n<FONT COLOR=\"orange\"><PRE>";
            substitution[1]="</PRE></FONT><HR><FONT color=\"red\">JavaScript end</FONT><P>";
            substitution[2]="<FONT COLOR=\"red\">WebMail security: JavaScript link filtered:</FONT> <FONT COLOR=\"orange\">$1</FONT> $2 ";
            // Link highlighting
            //uri=new RE("http\\:\\/\\/(.+)(html|\\/)(\\S|\\-|\\+|\\.|\\\|\\:)");
            initialized=true;
        } catch(Exception e) {
            log.fatal("Failed to clinit static objects", e);
        }
    }

    public static String apply(String s) {
        if(!initialized) {
            init();
        }
        String retval=s;
        for(int i=0;i<filter.length;i++) {
            Matcher m = filter[i].matcher(retval);
            retval=m.replaceAll(substitution[i]);
        }
        return retval;
    }
}
