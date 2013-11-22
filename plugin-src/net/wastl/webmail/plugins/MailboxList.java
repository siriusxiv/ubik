/*
 * @(#)$Id: MailboxList.java 97 2008-10-28 19:41:29Z unsaved $
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
 * Show a list of user mailboxes.
 *
 * provides: mailbox list
 * requires: content bar
 *
 * Created: Thu Sep  2 12:00:38 1999
 *
 * @author Sebastian Schaffert
 */
public class MailboxList implements Plugin, URLHandler {
    public static final String VERSION="1.3";
    public static final String URL="/mailbox";

    Storage store;

    public MailboxList() {
    }

    public void register(WebMailServer parent) {
        parent.getURLHandler().registerHandler(URL,this);
        //parent.getContentBar().registerContentItem(this);
        store=parent.getStorage();
    }

    public String getName() {
        return "MailboxList";
    }

    public String getDescription() {
        return "This ContentProvider shows a list of all folders and links to the FolderList URLHandler.";
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
        HTMLDocument content;
        /* If the user requests the folder overview, try to fetch new information */
        /* Do so only, if this is forced, to save the time! */
        if(header.isContentSet("force-refresh")) {
            session.refreshFolderInformation(true, true);
        }
        content=new XHTMLDocument(session.getModel(),store.getStylesheet("mailbox.xsl",user.getPreferredLocale(),user.getTheme()));
        return content;
    }

    public String provides() {
        return "mailbox list";
    }

    public String requires() {
        return "content bar";
    }
}
