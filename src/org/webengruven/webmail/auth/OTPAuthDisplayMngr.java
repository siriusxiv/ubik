/*
 * @(#)$Id: OTPAuthDisplayMngr.java 113 2008-10-29 23:41:26Z unsaved $
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


/** OTPAuthDisplayMngr provides info/setup for user interaction with the
 * OTPAuthenticator class.  There are a few special details to using
 * RFC1938 OTPs when it comes to changing them.  Namely, rather than having
 * the server compute the OTP, the client computes it and gives it to the
 * serevr.  Thus there needs to be a challenge used in changing the
 * password.
 *
 * $Id: OTPAuthDisplayMngr.java 113 2008-10-29 23:41:26Z unsaved $
 *
 * @author Devin Kowatch
 * @see org.webengruven.auth.CRAuthenticator
 * @see OTPAuthenticator
 */

package org.webengruven.webmail.auth;

import net.wastl.webmail.exceptions.WebMailException;
import net.wastl.webmail.server.UserData;
import net.wastl.webmail.xml.XMLGenericModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OTPAuthDisplayMngr extends CRAuthDisplayMngr {
    private static Log log = LogFactory.getLog(OTPAuthDisplayMngr.class);

    /* the length of the passwords */
    private int     PASS_LEN = 30;

    /** Default C'tor */
    public OTPAuthDisplayMngr() { super(null);}

    /** Construct with a ref to the Authenticator using this object.  It
     * will be used later.
     */
    public OTPAuthDisplayMngr(OTPAuthenticatorIface a) {
        auth = a;
    }

    /** Setup state vars for the password change prompt.
     * @param ud UserData for the user who will have their password changed
     * @param model The model to set state vars in
     */
    public void setPassChangeVars(UserData ud, XMLGenericModel model)
        throws WebMailException
    {
        try {
            OTPAuthenticatorIface otp_auth = (OTPAuthenticatorIface)auth;

            model.setStateVar("new challenge", otp_auth.getNewChallenge(ud));
            model.setStateVar("pass len", String.valueOf(PASS_LEN));
        }
        catch (ClassCastException e) {
            log.error( "tried to use OTPAuthDisplayMngr"
             + " with an Authenticator other than OTPAuthenticator", e);
            throw new WebMailException( "tried to use OTPAuthDisplayMngr"
             + " with an Authenticator other than OTPAuthenticator");
        }
    }

    /** Get the name of the template that can display an OTP password
     * change screen.
     * @return The name of the template
     */
    public String getPassChangeTmpl() {
        return "otpchangepass";
    }
}
