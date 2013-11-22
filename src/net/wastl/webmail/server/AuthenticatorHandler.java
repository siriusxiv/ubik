/*
 * @(#)$Id: AuthenticatorHandler.java 116 2008-10-30 06:12:51Z unsaved $
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

import java.util.Hashtable;
import java.util.Map;

import net.wastl.webmail.exceptions.WebMailException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Sebastian Schaffert
 */
public class AuthenticatorHandler  {
    private static Log log = LogFactory.getLog(AuthenticatorHandler.class);
    WebMailServer parent;

    Map<String, Authenticator> authenticators;

    String authenticator_list = null;

    public AuthenticatorHandler(WebMailServer parent) throws WebMailException {
        this.parent=parent;

        authenticator_list=parent.getProperty("webmail.authenticators");
        if(authenticator_list == null) {
            throw new WebMailException("No Authenticators defined (parameter: webmail.authenticators)");
        }

        parent.getConfigScheme().configRegisterChoiceKey("AUTH","Authentication method to use.");
        //parent.getConfigScheme().configRegisterStringKey("AUTHHOST","localhost","Host used for remote authentication (e.g. for IMAP,POP3)");
        registerAuthenticators();
        parent.getConfigScheme().setDefaultValue("AUTH","IMAP");
    }


    /**
     * Initialize and register WebMail Authenticators.
     */
    public void registerAuthenticators() {
        String[] authenticatorStrings =
                authenticator_list.trim().split("\\s*,\\s*", -1);
        log.info("Initializing " + authenticatorStrings.length
                + " WebMail Authenticator Plugins ...");

        authenticators = new Hashtable<String, Authenticator>();
        for (String authString : authenticatorStrings) try {
            Class c=Class.forName(authString);
            Authenticator a=(Authenticator) c.newInstance();
            a.register(parent.getConfigScheme());
            authenticators.put(a.getKey(),a);
            log.debug("Registered authenticator plugin '"+c.getName()+"'");
        } catch(Exception ex) {
            log.error("Failed to register Auth. plugin '" + authString + "'",
                    ex);
        }
        log.info("Initialized " + authenticators.size()
                + " Authenticator Plugins");
    }

    public Authenticator getAuthenticator(String key) {
        return authenticators.get(key);
    }
}
