/*
 * @(#)$Id: ShowMIME.java 97 2008-10-28 19:41:29Z unsaved $
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


package net.wastl.webmail.plugins;

import net.wastl.webmail.exceptions.WebMailException;
import net.wastl.webmail.misc.ByteStore;
import net.wastl.webmail.server.HTTPSession;
import net.wastl.webmail.server.Plugin;
import net.wastl.webmail.server.Storage;
import net.wastl.webmail.server.URLHandler;
import net.wastl.webmail.server.WebMailServer;
import net.wastl.webmail.server.WebMailSession;
import net.wastl.webmail.server.http.HTTPRequestHeader;
import net.wastl.webmail.ui.html.HTMLDocument;
import net.wastl.webmail.ui.html.HTMLImage;

/**
 * Show a MIME part of a message.
 *
 * provides: message mime
 * requires: message show
 *
 * Created: Thu Sep  2 18:52:40 1999
 *
 * @author Sebastian Schaffert
 */
public class ShowMIME implements Plugin, URLHandler {
    public static final String VERSION="1.1";
    public static final String URL="/showmime";

    Storage store;

    public ShowMIME() {
    }

    public void register(WebMailServer parent) {
        parent.getURLHandler().registerHandler(URL,this);
        this.store=parent.getStorage();
    }

    public String getName() {
        return "ShowMIME";
    }

    public String getDescription() {
        return "Show a MIME part";
    }

    public String getVersion() {
        return VERSION;
    }

    public String getURL() {
        return URL;
    }


    public HTMLDocument handleURL(String suburl, HTTPSession sess, HTTPRequestHeader header) throws WebMailException {
        if(sess == null) {
            throw new WebMailException("No session was given. If you feel this is incorrect, please contact your system administrator");
        }
        WebMailSession session=(WebMailSession)sess;
        //log.debug("Fetching MIME part: "+suburl);
        ByteStore b=session.getMIMEPart(header.getContent("msgid"),suburl.substring(1));
        int count=0;
        while(b == null && count <= 10) {
            //log.debugcount+" ");
            try {
                Thread.sleep(250);
            } catch(InterruptedException e) {}
            b=session.getMIMEPart(header.getContent("msgid"),suburl.substring(1));
            count++;
        }
        //if(count != 0) { System.err.println(); }
        HTMLImage content=new HTMLImage(b);
        //log.debug(content.size());
        return content;
    }

    public String provides() {
        return "message mime";
    }

    public String requires() {
        return "message show";
    }
}
