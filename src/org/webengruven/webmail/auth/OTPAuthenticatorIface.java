/*
 * @(#)$Id: OTPAuthenticatorIface.java 38 2008-10-24 19:23:35Z unsaved $
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


/** OTPAuthenticatorIface is the interface for the OTPAuthenticator class.
 * It is used by the OTPAuthDisplayMngr class as a means of accessing the
 * OTPAuthenticator methods that are not also part of the CRAuthenticator
 * base class.  It primarilly exists because OTPAuthenticator is not a
 * member of any package (due to being a plugin) and as such,
 * OTPAuthDisplayMngr has no access to it, or it's specific methods.
 *
 * @author Devin Kowatch
 * @see OTPAuthenticator
 * @see OTPAuthDisplayMngr
 */
package org.webengruven.webmail.auth;

import net.wastl.webmail.exceptions.WebMailException;
import net.wastl.webmail.server.UserData;

public abstract class OTPAuthenticatorIface extends CRAuthenticator {
    public OTPAuthenticatorIface() { super(); }

    abstract public String getNewChallenge(UserData ud) throws WebMailException;
};
