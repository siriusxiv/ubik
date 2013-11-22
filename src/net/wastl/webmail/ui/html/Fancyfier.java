/*
 * @(#)$Id: Fancyfier.java 101 2008-10-29 01:15:14Z unsaved $
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
 * Do some fancifying with the messages. Also filters JavaScript.
 *
 * Created: Mon Feb 22 14:55:36 1999
 *
 * @author Sebastian Schaffert
 */
public class Fancyfier  {
    private static Log log = LogFactory.getLog(Fancyfier.class);

    public Fancyfier() {
    }
    private static Pattern[] regs=null;
    private static Pattern uri=null;

    private static String[] repls={
        "<IMG SRC=\"/images/emoticon11.gif\">",
        "<IMG SRC=\"/images/emoticon12.gif\">",
        "<IMG SRC=\"/images/emoticon13.gif\">",
        "<IMG SRC=\"/images/emoticon14.gif\">",
        "<IMG SRC=\"/images/emoticon11.gif\">",
        "<IMG SRC=\"/images/emoticon12.gif\">",
        "<IMG SRC=\"/images/emoticon13.gif\">",
        "<IMG SRC=\"/images/emoticon14.gif\">",
        "<IMG SRC=\"/images/emoticon21.gif\">",
        "<IMG SRC=\"/images/emoticon22.gif\">",
        "<IMG SRC=\"/images/emoticon23.gif\">",
        "<IMG SRC=\"/images/emoticon24.gif\">",
        "<IMG SRC=\"/images/emoticon31.gif\">",
        "<IMG SRC=\"/images/emoticon32.gif\">",
        "<IMG SRC=\"/images/emoticon33.gif\">",
        "<IMG SRC=\"/images/emoticon34.gif\">",
        "<IMG SRC=\"/images/emoticon41.gif\">",
        "<IMG SRC=\"/images/emoticon42.gif\">",
        "<IMG SRC=\"/images/emoticon43.gif\">",
        "<IMG SRC=\"/images/emoticon44.gif\">",
        "<IMG SRC=\"/images/emoticon51.gif\">",
        "<IMG SRC=\"/images/emoticon52.gif\">",
    };

    public static void init() {
        try {
            // Smiley substitution
            String[] temp={
                ":-\\)",
                ":-\\(",
                ":-O",
                ":\\)",
                ":\\(",
                ":O",
                ":\\|",
                ";-\\)",
                ";-\\(",
                ";-O",
                ";-\\|",
                "B-\\)",
                "B-\\(",
                "B-O",
                "B-\\|",
                "%-\\)",
                "%-\\(",
                "%-O",
                "%-\\|",
                ":-X",
                "\\}:->"
                    };
            regs=new Pattern[temp.length];
            for(int i=0;i<temp.length;i++) {
                regs[i]=Pattern.compile(temp[i]);
            }
            // Link highlighting
            //uri=new RE("http\\:\\/\\/(.+)(html|\\/)(\\S|\\-|\\+|\\.|\\\|\\:)");

        } catch(Exception e) {
            log.fatal("Failed to clinit static objects", e);
        }
    }

    public static String apply(String s) {
        if(regs==null) {
            init();
        }
        String retval=s;
        for(int i=0;i<regs.length;i++) {
            Matcher m = regs[i].matcher(retval);
            retval = m.replaceAll(repls[i]);
            //retval=regs[i].substituteAll(retval,repls[i]);
        }
        return retval;
    }
}
