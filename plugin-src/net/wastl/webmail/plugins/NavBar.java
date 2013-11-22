/*
 * @(#)$Id: NavBar.java 97 2008-10-28 19:41:29Z unsaved $
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
import net.wastl.webmail.server.HTTPSession;
import net.wastl.webmail.server.Plugin;
import net.wastl.webmail.server.Storage;
import net.wastl.webmail.server.URLHandler;
import net.wastl.webmail.server.UserData;
import net.wastl.webmail.server.WebMailServer;
import net.wastl.webmail.server.WebMailSession;
import net.wastl.webmail.server.http.HTTPRequestHeader;
import net.wastl.webmail.ui.html.HTMLDocument;
import net.wastl.webmail.ui.xml.XHTMLDocument;

/**
 * The content bar on the left.
 *
 * provides: content bar
 * requires:
 *
 * @author Sebastian Schaffert
 */
public class NavBar implements Plugin, URLHandler {
    public static final String VERSION="2.0";
    public static final String URL="/content";

    String template;
    String bar;
    Storage store;

    public NavBar() {
    }


    public void register(WebMailServer parent) {
        this.store=parent.getStorage();
        parent.getURLHandler().registerHandler(URL,this);
    }

    public String getVersion() {
        return VERSION;
    }

    public String getName() {
        return "ContentBar";
    }

    public String getDescription() {
        return "This is the content-bar on the left frame in the mailbox window. "+
            "ContentProviders register with this content-bar to have a link and an icon added.";
    }

    public String getURL() {
        return URL;
    }


    public HTMLDocument handleURL(String suburl, HTTPSession session, HTTPRequestHeader header) throws WebMailException {
        if(session == null) {
            throw new WebMailException("No session was given. If you feel this is incorrect, please contact your system administrator");
        }
        WebMailSession sess=(WebMailSession)session;
        UserData user=sess.getUser();
        return new XHTMLDocument(session.getModel(),store.getStylesheet("navbar.xsl",user.getPreferredLocale(),user.getTheme()));
    }

    public String provides() {
        return "content bar";
    }

    public String requires() {
        return "";
    }
}
