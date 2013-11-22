/*
 * @(#)$Id: FolderSetup.java 101 2008-10-29 01:15:14Z unsaved $
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Show the folder setup form and handle changes (except deletion).
 *
 * provides: folder setup
 * requires: content bar
 *
 * Created: Tue Sep  7 18:45:11 1999
 *
 * @author Sebastian Schaffert
 */
public class FolderSetup implements Plugin, URLHandler {
    private static Log log = LogFactory.getLog(FolderSetup.class);

    public static final String VERSION="1.3";
    public static final String URL="/folder/setup";

    Storage store;

    public FolderSetup() {
    }

    public void register(WebMailServer parent) {
        parent.getURLHandler().registerHandler(URL,this);
        store=parent.getStorage();
    }

    public String getName() {
        return "FolderSetup";
    }

    public String getDescription() {
        return "This ContentProvider manages a users folder setup.";
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

        /* The user requested to change his *mailbox* settings */
        if(header.isContentSet("method") && header.getContent("method").equals("mailbox")) {
            if(header.isContentSet("remove")) {
                session.removeMailbox(header.getContent("remove"));
                session.refreshFolderInformation(true, true);
            } else if(header.isContentSet("add")) {
                try {
                    session.addMailbox(header.getContent("mbox_name"),
                                       header.getContent("mbox_proto"),
                                       header.getContent("mbox_host"),
                                       header.getContent("mbox_login"),
                                       header.getContent("mbox_password"));
                } catch(Exception ex) {
                    throw new WebMailException(ex);
                }
                session.refreshFolderInformation(true, true);
            }

            content=new XHTMLDocument(session.getModel(),
                                      store.getStylesheet("foldersetup-mailbox.xsl",
                                                          user.getPreferredLocale(),user.getTheme()));

            /* The user requested to change subfolders in a mailbox */
        } else if(header.isContentSet("method") && header.getContent("method").equals("folder")) {
            if(header.isContentSet("remove")) {
                try {
                    session.removeFolder(header.getContent("remove"),header.isContentSet("recurse"));
                } catch(Exception ex) {
                    log.error("Error while removing folders", ex);
                    throw new WebMailException("Error while removing folders");
                }
            } else if(header.isContentSet("addto")) {
                String type=header.getContent("folder_type");
                boolean holds_folders=false,holds_messages=false;
                if(type.equals("msgs")) {
                    holds_messages=true;
                } else if(type.equals("folder")) {
                    holds_folders=true;
                } else if(type.equals("msgfolder")) {
                    holds_folders=true; holds_messages=true;
                }
                try {
                    session.addFolder(header.getContent("addto"),
                                      header.getContent("folder_name"),
                                      holds_messages,
                                      holds_folders);

                } catch(Exception ex) {
                    log.error("Error while adding folders", ex);
                    throw new WebMailException("Error while adding folders");
                }
            } else if(header.isContentSet("hide")) {
                session.unsubscribeFolder(header.getContent("hide"));
            } else if(header.isContentSet("unhide")) {
                session.subscribeFolder(header.getContent("unhide"));
            }

            // We want to see all folders but no counts in the folder overview
            session.refreshFolderInformation(false, false);

            content=new XHTMLDocument(session.getModel(),
                                      store.getStylesheet("foldersetup-folders.xsl",
                                                          user.getPreferredLocale(),user.getTheme()));
            // but we want only to see some in the mailbox overview,
            // but with counts
            session.refreshFolderInformation(true, true);
        } else if(header.isContentSet("method") && header.getContent("method").equals("folderadd")) {
            session.setAddToFolder(header.getContent("addto"));
            content=new XHTMLDocument(session.getModel(),
                                      store.getStylesheet("foldersetup-folders-add.xsl",
                                                          user.getPreferredLocale(),user.getTheme()));
        } else {
            content=new XHTMLDocument(session.getModel(),
                                      store.getStylesheet("foldersetup.xsl",
                                                          user.getPreferredLocale(),user.getTheme()));
        }
        return content;
    }

    public String provides() {
        return "folder setup";
    }

    public String requires() {
        return "content bar";
    }
}
