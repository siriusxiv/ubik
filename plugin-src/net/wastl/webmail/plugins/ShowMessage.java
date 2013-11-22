/*
 * @(#)$Id: ShowMessage.java 97 2008-10-28 19:41:29Z unsaved $
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
import net.wastl.webmail.exceptions.NoSuchFolderException;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Show a message.
 *
 * provides: message show
 * requires: message list
 *
 * @author Sebastian Schaffert
 */
public class ShowMessage implements Plugin, URLHandler {
    private static Log log = LogFactory.getLog(ShowMessage.class);
    public static final String VERSION="1.3";
    public static final String URL="/folder/showmsg";

    Storage store;

    public ShowMessage() {
    }

    public void register(WebMailServer parent) {
        parent.getURLHandler().registerHandler(URL,this);
        this.store=parent.getStorage();
    }

    public String getName() {
        return "ShowMessage";
    }

    public String getDescription() {
        return "Display a message";
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
        UserData user=session.getUser();
        String folderhash=header.getContent("folder-id");

        if(header.isContentSet("flag")) {
            try {
                session.setFlags(folderhash,header);
            } catch(Exception ex) {
                log.error(ex);
                throw new WebMailException(ex.getMessage());
            }
        }

        int nr=1;
        try {
            nr=Integer.parseInt(header.getContent("message-nr"));
        } catch(Exception e) {}
        try {
            session.getMessage(folderhash,nr);
        } catch(NoSuchFolderException e) {
            throw new DocumentNotFoundException("Could not find folder "+folderhash+"!");
        }
        return new XHTMLDocument(session.getModel(),
                                 store.getStylesheet("showmessage.xsl",
                                                     user.getPreferredLocale(),user.getTheme()));
        //return new HTMLParsedDocument(store,session,"showmessage");
    }

    public String provides() {
        return "message show";
    }

    public String requires() {
        return "message list";
    }
}
