/*
 * @(#)$Id: UserSetup.java 97 2008-10-28 19:41:29Z unsaved $
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

import net.wastl.webmail.exceptions.DocumentNotFoundException;
import net.wastl.webmail.exceptions.InvalidPasswordException;
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

import org.webengruven.webmail.auth.AuthDisplayMngr;

/**
 * Show a form to change user settings and actually perform them.
 *
 * provides: user setup
 * requires: content bar
 *
 * @author Sebastian Schaffert
 */
public class UserSetup implements Plugin, URLHandler {
    public static final String VERSION="1.3";
    public static final String URL="/setup";

    Storage store;

    public UserSetup() {
    }

    public void register(WebMailServer parent) {
        parent.getURLHandler().registerHandler(URL,this);
        store=parent.getStorage();
    }

    public String getName() {
        return "UserSetup";
    }

    public String getDescription() {
        return "Change a users settings.";
    }

    public String getVersion() {
        return VERSION;
    }

    public String getURL() {
        return URL;
    }

    public HTMLDocument handleURL(String suburl,HTTPSession sess,
     HTTPRequestHeader header) throws WebMailException
    {
        if(sess == null) {
            throw new WebMailException("No session was given. If you feel this is incorrect, please contact your system administrator");
        }
        WebMailSession session=(WebMailSession)sess;
        UserData user=session.getUser();
        HTMLDocument content;
    AuthDisplayMngr adm=store.getAuthenticator().getAuthDisplayMngr();

    adm.setPassChangeVars(user, session.getUserModel());
    session.getUserModel().setStateVar(
        "pass change tmpl", adm.getPassChangeTmpl());

        if(suburl.startsWith("/submit")) {
            session.refreshFolderInformation(true, true);
            try {
                session.changeSetup(header);
                content=new XHTMLDocument(session.getModel(),store.getStylesheet("setup.xsl",user.getPreferredLocale(),user.getTheme()));
            } catch(InvalidPasswordException e) {
                throw new DocumentNotFoundException("The two passwords did not match");
            }
        } else {
            content=new XHTMLDocument(session.getModel(),store.getStylesheet("setup.xsl",user.getPreferredLocale(),user.getTheme()));
        }
        return content;
    }

    public String provides() {
        return "user setup";
    }

    public String requires() {
        return "content bar";
    }
}
