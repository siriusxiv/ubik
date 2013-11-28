/*
 * @(#)$Id: IMAPAuthenticator.java 104 2008-10-29 02:09:55Z unsaved $
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

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import net.wastl.webmail.config.ConfigScheme;
import net.wastl.webmail.exceptions.InvalidPasswordException;
import net.wastl.webmail.server.Storage;
import net.wastl.webmail.server.WebMailVirtualDomain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * IMAPAuthenticator.java
 *
 * Created: Mon Apr 19 12:03:53 1999
 *
 * @author Sebastian Schaffert
 */
public class IMAPAuthenticator extends net.wastl.webmail.server.Authenticator {
    private static Log log = LogFactory.getLog(IMAPAuthenticator.class);

    public final String VERSION="1.2";

    protected Store st;

    protected Storage storage;

    public IMAPAuthenticator() {
        super();
    }

    public String getVersion() {
        return VERSION;
    }

    public void init(Storage store) {
        storage=store;
        Session session=Session.getDefaultInstance(System.getProperties(),null);
        try {
            st=session.getStore("imap");
        } catch(NoSuchProviderException e) {
            log.error("Initialization failed", e);
        }
    }

    public void register(ConfigScheme store) {
        key="IMAP";
        store.configAddChoice("AUTH",key,"Authenticate against an IMAP server on the net. Does not allow password change.");
    }

    public void authenticatePreUserData(String user,String domain,String passwd)
     throws InvalidPasswordException {
        super.authenticatePreUserData(user,domain,passwd);

        WebMailVirtualDomain vd=storage.getVirtualDomain(domain);
        String authhost=vd.getAuthenticationHost();

        try {
            st.connect(authhost,user,passwd);
            st.close();
            log.info("IMAPAuthentication: user "+user+
                        " authenticated successfully (imap host: "+authhost+").");
        } catch(MessagingException e) {
            log.warn("IMAPAuthentication: user "+user+
                        " authentication failed (imap host: "+authhost+").");
            throw new InvalidPasswordException("IMAP authentication failed!");
        }
    }

    public boolean canChangePassword() {
        return false;
    }
}
