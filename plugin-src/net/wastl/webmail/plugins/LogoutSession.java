/*
 * @(#)$Id: LogoutSession.java 97 2008-10-28 19:41:29Z unsaved $
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

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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

// XXX Do it more nicely ...
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Log out a user.
 *
 * provides: logout
 * requires: content bar
 *
 * @author Sebastian Schaffert
 */
public class LogoutSession implements Plugin, URLHandler {
    public static final String VERSION="1.3";
    public static final String URL="/logout";

    Storage store;
    private static WebMailServer parent;

    public LogoutSession() {
    }

    public void register(WebMailServer parent) {
        parent.getURLHandler().registerHandler(URL,this);
        //parent.getContentBar().registerContentItem(this);
        store=WebMailServer.getStorage();
        LogoutSession.parent=parent;
    }

    public String getName() {
        return "LogoutSession";
    }

    public String getDescription() {
        return "ContentProvider plugin that closes an active WebMail session.";
    }

    public String getVersion() {
        return VERSION;
    }

    public String getURL() {
        return URL;
    }
    public HTMLDocument handleURL(String suburl, HTTPSession session, HTTPRequestHeader header) throws WebMailException {
        if(session == null) {
                // XXX Ugly!
                 String empty="<USERMODEL><USERDATA><FULL_NAME>User</FULL_NAME></USERDATA><STATEDATA><VAR name=\"img base uri\" value=\"/webmail/lib/templates/en/bibop/\"/><VAR name=\"base uri\" value=\"/webmail/WebMail/\"/></STATEDATA></USERMODEL>";
                 DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
                Document emptyUser=null;
                try {
                        emptyUser=factory.newDocumentBuilder().parse(new InputSource(new StringReader(empty)));
                } catch(ParserConfigurationException pce) {
                } catch(SAXException saxe) {
                } catch(IOException ioe) {
                }
                HTMLDocument content=new XHTMLDocument(emptyUser,store.getStylesheet("logout.xsl",WebMailServer.getDefaultLocale(),WebMailServer.getDefaultTheme()));
                return content;
        }
        UserData user=((WebMailSession)session).getUser();
        HTMLDocument content=new XHTMLDocument(session.getModel(),store.getStylesheet("logout.xsl",user.getPreferredLocale(),user.getTheme()));
        if(!session.isLoggedOut()) {
            session.logout();
        }
        return content;
    }

    public String provides() {
        return "logout";
    }

    public String requires() {
        return "content bar";
    }
}
