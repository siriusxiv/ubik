/*
 * @(#)$Id: POPSAuthenticator.java 131 2008-10-31 17:09:46Z unsaved $
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


package net.wastl.webmail.authenticators;

import javax.mail.NoSuchProviderException;
import javax.mail.Session;

import net.wastl.webmail.config.ConfigScheme;
import net.wastl.webmail.server.Storage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author blaine.simpson@admc.com
 */
public class POPSAuthenticator extends POPAuthenticator {
    private static Log log = LogFactory.getLog(POPSAuthenticator.class);

    @Override
    public void init(Storage store) {
        storage = store;
        final Session session =
                Session.getDefaultInstance(System.getProperties(), null);
        try {
            st = session.getStore("pop3s");
        } catch (final NoSuchProviderException e) {
            log.error("Initialization for 'pop3s' failed", e);
        }
    }

    @Override
    public void register(ConfigScheme store) {
        key = "POP3S";
        store.configAddChoice("AUTH", key,
                "Authenticate against a POP3 SSL server on the net.  "
                + "Does not allow password change.");
    }
}
