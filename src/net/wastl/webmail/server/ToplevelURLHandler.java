/*
 * @(#)$Id: ToplevelURLHandler.java 42 2008-10-24 21:19:58Z unsaved $
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


package net.wastl.webmail.server;

import java.util.Locale;

import javax.servlet.ServletException;

import net.wastl.webmail.exceptions.DocumentNotFoundException;
import net.wastl.webmail.exceptions.WebMailException;
import net.wastl.webmail.server.http.HTTPRequestHeader;
import net.wastl.webmail.ui.html.HTMLDocument;
import net.wastl.webmail.ui.xml.XHTMLDocument;
import net.wastl.webmail.xml.XMLGenericModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webengruven.webmail.auth.AuthDisplayMngr;


/**
 * Handle URLs. Give them to the appropriate Plugins/Program parts
 *
 * Created: Tue Aug 31 17:20:29 1999
 *
 * @author Sebastian Schaffert
 */
public class ToplevelURLHandler implements URLHandler {
    private static Log log = LogFactory.getLog(ToplevelURLHandler.class);

    WebMailServer parent;
    //Hashtable urlhandlers;
    URLHandlerTree urlhandlers;

    public ToplevelURLHandler(WebMailServer parent) {
        log.info("Initializing WebMail URL Handler ... done.");
        urlhandlers=new URLHandlerTree("/");
        urlhandlers.addHandler("/",this);
        this.parent=parent;
    }

    public void registerHandler(String url, URLHandler handler) {
        //urlhandlers.put(url,handler);
        urlhandlers.addHandler(url,handler);
        //log.debug("Tree changed: "+urlhandlers.toString());
    }

    public String getURL() {
        return "/";
    }

    public String getName() {
        return "TopLevelURLHandler";
    }

    public String getDescription() {
        return "";
    }

    public HTMLDocument handleException(Exception ex, HTTPSession session, HTTPRequestHeader header) throws ServletException {
        try {
            session.setException(ex);
            String theme=parent.getDefaultTheme();
            Locale locale=Locale.getDefault();
            if(session instanceof WebMailSession) {
                WebMailSession sess=(WebMailSession)session;
                theme=sess.getUser().getTheme();
                locale=sess.getUser().getPreferredLocale();
            }
            return new XHTMLDocument(session.getModel(),parent.getStorage().getStylesheet("error.xsl",locale,theme));
        } catch(Exception myex) {
            log.error("Error while handling exception:", myex);
            log.error("The handled exception was:", ex);
            throw new ServletException(ex);
        }
    }

    public HTMLDocument handleURL(String url, HTTPSession session, HTTPRequestHeader header) throws WebMailException, ServletException {
        HTMLDocument content;

        if(url.equals("/")) {
            //content=new HTMLLoginScreen(parent,parent.getStorage(),false);
            XMLGenericModel model=parent.getStorage().createXMLGenericModel();

            AuthDisplayMngr adm = parent.getStorage().getAuthenticator().getAuthDisplayMngr();

            if(header.isContentSet("login")) {
                model.setStateVar("invalid password","yes");
            }

            // Let the authenticator setup the loginscreen
            adm.setLoginScreenVars(model);

                /**
                 * Show login screen depending on WebMailServer's default locale.
                 */
                /*
            content = new XHTMLDocument(model.getRoot(),
                                        parent.getStorage().getStylesheet(adm.getLoginScreenFile(),
                                                                          Locale.getDefault(),"default"));
                */
            content = new XHTMLDocument(model.getRoot(),
                                        parent.getStorage().getStylesheet(adm.getLoginScreenFile(),
                                                                                parent.getDefaultLocale(),parent.getProperty("webmail.default.theme")));
        } else if(url.equals("/login")) {
            WebMailSession sess=(WebMailSession)session;
            UserData user=sess.getUser();
            content=new XHTMLDocument(session.getModel(),parent.getStorage().getStylesheet("login.xsl",user.getPreferredLocale(),user.getTheme()));
        } else {
            /* Let the plugins handle it */

            URLHandler uh=urlhandlers.getHandler(url);

            if(uh != null && uh != this) {
                // log.debug("Handler: "+uh.getName()+" ("+uh.getURL()+")");
                String suburl=url.substring(uh.getURL().length(),url.length());
                content=uh.handleURL(suburl,session,header);
            } else {
                throw new DocumentNotFoundException(url + " was not found on this server");
            }
        }
        return content;
    }
}
