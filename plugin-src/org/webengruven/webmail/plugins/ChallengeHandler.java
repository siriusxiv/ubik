/*
 * @(#)$Id: ChallengeHandler.java 97 2008-10-28 19:41:29Z unsaved $
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


package org.webengruven.webmail.plugins;

import java.util.Locale;

import net.wastl.webmail.exceptions.WebMailException;
import net.wastl.webmail.server.HTTPSession;
import net.wastl.webmail.server.Plugin;
import net.wastl.webmail.server.Storage;
import net.wastl.webmail.server.URLHandler;
import net.wastl.webmail.server.WebMailServer;
import net.wastl.webmail.server.http.HTTPRequestHeader;
import net.wastl.webmail.ui.html.HTMLDocument;
import net.wastl.webmail.ui.xml.XHTMLDocument;
import net.wastl.webmail.xml.XMLGenericModel;
import net.wastl.webmail.xml.XMLUserData;

import org.webengruven.webmail.auth.CRAuthDisplayMngr;

/** This is the URLHandler for "/challenge" It shows the user a challenge and
 *  gets their new password.
 *
 * @author Devin Kowatch
 * @see net.wastl.webmail.URLHandler
 */
public class ChallengeHandler implements Plugin, URLHandler {
    public final String VERSION="2.0";
    public final String URL="/challenge";

    private Storage store;

        public void register(WebMailServer parent) {
        parent.getURLHandler().registerHandler(URL, this);
        storage = parent.getStorage();
        }

        public String getVersion() {
        return VERSION;
        }

        public String provides() {
        return "Authentication Challenge";
        }

    /* XXX Not sure what, if anything, this should return */
        public String requires() {
        return "";
        }

        public String getURL() {
        return URL;
        }

        public String getName() {
                return "ChallengeHandler";
        }

        public String getDescription() {
                return "This URLHandler will show the user a challenge and allow them"
         + "to respond to it";
        }

        public HTMLDocument handleURL(String subURL, HTTPSession sess,
        HTTPRequestHeader h) throws WebMailException
        {
        XMLGenericModel model = storage.createXMLGenericModel();
        HTMLDocument content;
        XMLUserData ud;
        CRAuthDisplayMngr adm;
        String chal_file;

        ud = storage.getUserData(h.getContent("login"), h.getContent("vdom"),"",false);

        try {
            adm=(CRAuthDisplayMngr)storage.getAuthenticator().getAuthDisplayMngr();
            adm.setChallengeScreenVars(ud, model);
            chal_file = adm.getChallengeScreenFile();
        } catch (ClassCastException e) {
            throw new WebMailException(
             "Trying to handle /challenge for a non CRAuthenticator");
        } catch (Exception e) {
            throw new WebMailException(e.toString());
        }

        model.setStateVar("login", h.getContent("login"));
        model.setStateVar("vdom", h.getContent("vdom"));

        content = new XHTMLDocument(model.getRoot(), storage.getStylesheet(
         chal_file, Locale.getDefault(), "default"));

        return content;
    }

    private Storage storage;


} /* END class ChallengeHandler */
