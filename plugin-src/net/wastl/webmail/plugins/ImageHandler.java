/*
 * @(#)$Id: ImageHandler.java 113 2008-10-29 23:41:26Z unsaved $
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

import java.util.Locale;

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
 * @author Sebastian Schaffert
 */
public class ImageHandler implements Plugin, URLHandler  {
    public static final String VERSION="1.0";
    public static final String URL="/images";

    Storage store;
    WebMailServer parent;

    public ImageHandler() {
    }

    public void register(WebMailServer parent) {
        parent.getURLHandler().registerHandler(URL,this);
        store=parent.getStorage();
        this.parent=parent;
    }

    public String getName() {
        return "ImageHandler";
    }

    public String getDescription() {
        return "Return WebMail images";
    }

    public String getVersion() {
        return VERSION;
    }

    public String getURL() {
        return URL;
    }


    public HTMLDocument handleURL(String suburl, HTTPSession session, HTTPRequestHeader header) throws WebMailException {
        if(session == null || session instanceof AdminSession) {
            //throw new WebMailException("No session was given. If you feel this is incorrect, please contact your system administrator");
            return new HTMLImage(store,"images"+suburl,Locale.getDefault(),parent.getProperty("webmail.default.theme"));
        } else {
            UserData data=((WebMailSession)session).getUser();
            return new HTMLImage(store,"images"+suburl,data.getPreferredLocale(),data.getTheme());
        }
    }

    public String provides() {
        return "imagehandler";
    }

    public String requires() {
        return "";
    }
}
