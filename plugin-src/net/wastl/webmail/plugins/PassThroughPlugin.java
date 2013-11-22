/*
 * @(#)$Id: PassThroughPlugin.java 97 2008-10-28 19:41:29Z unsaved $
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
import net.wastl.webmail.server.AdminSession;
import net.wastl.webmail.server.HTTPSession;
import net.wastl.webmail.server.Plugin;
import net.wastl.webmail.server.Storage;
import net.wastl.webmail.server.URLHandler;
import net.wastl.webmail.server.UserData;
import net.wastl.webmail.server.WebMailServer;
import net.wastl.webmail.server.WebMailSession;
import net.wastl.webmail.server.http.HTTPRequestHeader;
import net.wastl.webmail.ui.html.HTMLDocument;
import net.wastl.webmail.ui.html.HTMLImage;

/**
 * This plugin passes through data without doing any processing. It can be used for images
 * or other binary/text data by calling http://yourhost/mountpoint/webmail/passthrough/<file>
 *
 * @author Sebastian Schaffert
 */
public class PassThroughPlugin implements Plugin, URLHandler  {
    public static final String VERSION="1.0";
    public static final String URL="/passthrough";

    Storage store;
    WebMailServer parent;

    public PassThroughPlugin() {
    }

    public void register(WebMailServer parent) {
        parent.getURLHandler().registerHandler(URL,this);
        store=parent.getStorage();
        this.parent=parent;
    }

    public String getName() {
        return "PassThroughPlugin";
    }

    public String getDescription() {
        return "Pass though any kind of data to the browser";
    }

    public String getVersion() {
        return VERSION;
    }

    public String getURL() {
        return URL;
    }


    public HTMLDocument handleURL(String suburl, HTTPSession session, HTTPRequestHeader header) throws WebMailException {
        //log.debug(header);
        if(session == null || session instanceof AdminSession) {
            //throw new WebMailException("No session was given. If you feel this is incorrect, please contact your system administrator");
            //log.debug("Sending "+suburl.substring(1)+" to unknown user ");
            /**
             * If we just use JVM's default locale, no matter what user's locale
             * is, we always send webmail.css of JVM's default locale.
             *
             * By using WebMailServer's default locale instead of JVM's,
             * when responsing the login screen, we are able to pass through
             * webmail.css depending on user's locale.
             */
            // return new HTMLImage(store,suburl.substring(1),Locale.getDefault(),"default");
            return new HTMLImage(store, suburl.substring(1), WebMailServer.getDefaultLocale(), parent.getProperty("webmail.default.theme"));
        } else {
            UserData data=((WebMailSession)session).getUser();
            //log.debug("Sending "+suburl.substring(1)+" to user "+data.getLogin());
            return new HTMLImage(store,suburl.substring(1),data.getPreferredLocale(),data.getTheme());
        }
    }

    public String provides() {
        return "passthrough";
    }

    public String requires() {
        return "";
    }
}
