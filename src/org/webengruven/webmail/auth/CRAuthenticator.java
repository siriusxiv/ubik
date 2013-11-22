/*
 * @(#)$Id: CRAuthenticator.java 38 2008-10-24 19:23:35Z unsaved $
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


package org.webengruven.webmail.auth;

import net.wastl.webmail.exceptions.WebMailException;
import net.wastl.webmail.server.Authenticator;
import net.wastl.webmail.server.UserData;

/**
 * CRAuthenticator.java
 *
 * This is the base class for all Authenticators which implement challenge
 * response authentication.  As a general rule most parts of Webmail won't
 * care if if an authenticator is a child of the class Authenticator, or a
 * child of CRAuthenticator, however, there are a few extra methods needed
 * for challenge response.
 *
 * Also worth noting, this used to be the name of concrete authenticator.  I
 * decided that it was okay to reuse the name here because:
 *  1) it was only a test authenticator
 *  2) I really couldn't think of a better name.
 *
 * Created: Mon Jul 15 20:25
 * Recreated: Sun Sep 24 2000
 *
 * @author Devin Kowatch
 * @see webmail.server.UserData
 */
public abstract class CRAuthenticator extends Authenticator {
    /* dummy c'tor */
    public CRAuthenticator() { }

    /** Return an AuthDisplayMngr to use for display*/
    public AuthDisplayMngr getAuthDisplayMngr() {
        return new CRAuthDisplayMngr(this);
    }

    /** Get the challenge for this authentication.  This will get passed some
     * user data and should return the approriate challenge string for that
     * user.
     */
    public abstract String getChallenge(UserData ud) throws WebMailException;
}
