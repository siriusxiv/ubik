/*
 * @(#)$Id: WebMailHelp.java 101 2008-10-29 01:15:14Z unsaved $
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.wastl.webmail.exceptions.WebMailException;
import net.wastl.webmail.misc.ExpireableCache;
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
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Show WebMail help file
 *
 * provides: help
 * requires: content bar
 *
 * @author Sebastian Schaffert
 */
public class WebMailHelp implements Plugin, URLHandler {
    private static Log log = LogFactory.getLog(WebMailHelp.class);

    public static final String VERSION="2.0";
    public static final String URL="/help";

    ExpireableCache cache;

    Storage store;

    public WebMailHelp() {
    }

    public void register(WebMailServer parent) {
        parent.getURLHandler().registerHandler(URL,this);
        cache=new ExpireableCache(20,(float).9);
        store=parent.getStorage();
    }

    public String getName() {
        return "WebMailHelp";
    }

    public String getDescription() {
        return "This is the WebMail help content-provider.";
    }

    public String getVersion() {
        return VERSION;
    }

    public String getURL() {
        return URL;
    }

    public HTMLDocument handleURL(String suburl, HTTPSession session, HTTPRequestHeader header) throws WebMailException {
        UserData user=((WebMailSession)session).getUser();

        Document helpdoc=(Document)cache.get(user.getPreferredLocale().getLanguage()+"/"+user.getTheme());

        if(helpdoc == null) {
            String helpdocpath="file://"+store.getBasePath(user.getPreferredLocale(),user.getTheme())+"help.xml";

            try {
                DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                helpdoc=parser.parse(helpdocpath);
            } catch(Exception ex) {
                log.error("Could not parse "+helpdocpath, ex);
                throw new WebMailException("Could not parse "+helpdocpath);
            }

            cache.put(user.getPreferredLocale().getLanguage()+"/"+user.getTheme(),helpdoc);
        }

        /* Unfortunately we can't use two input documents, so we will temporarily insert the help document
           into the user's model */
        Node n=session.getModel().importNode(helpdoc.getDocumentElement(),true);
        session.getModel().getDocumentElement().appendChild(n);

        if(header.isContentSet("helptopic") && session instanceof WebMailSession) {
            ((WebMailSession)session).getUserModel().setStateVar("helptopic",header.getContent("helptopic"));
        }


        HTMLDocument retdoc=new XHTMLDocument(session.getModel(),store.getStylesheet("help.xsl",user.getPreferredLocale(),user.getTheme()));

        /* Here we remove the help document from the model */
        session.getModel().getDocumentElement().removeChild(n);
        /* Remove the indicator for a specific help topic */
        if(header.isContentSet("helptopic") && session instanceof WebMailSession) {
            ((WebMailSession)session).getUserModel().removeAllStateVars("helptopic");
        }


        return retdoc;
    }

    public String provides() {
        return "help";
    }

    public String requires() {
        return "content bar";
    }
}
