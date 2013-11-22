/*
 * @(#)$Id: CRAuthDisplayMngr.java 38 2008-10-24 19:23:35Z unsaved $
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


/** CRAuthDisplayMngr is a class that knows how to display various parts of
 * the authentication system.  This is a customized version of
 * AuthDisplaMngr that works with the CRAuthenticator.  It knows how to
 * handle the challenge screen.
 *
 * $Id: CRAuthDisplayMngr.java 38 2008-10-24 19:23:35Z unsaved $
 *
 * @author Devin Kowatch
 * @see org.webengruven.webmail.auth.AuthDisplayMngr
 */

package org.webengruven.webmail.auth;

import net.wastl.webmail.exceptions.WebMailException;
import net.wastl.webmail.server.UserData;
import net.wastl.webmail.xml.XMLGenericModel;

public class CRAuthDisplayMngr extends AuthDisplayMngr {
    /** Default C'tor. This c'tor isn't very useful, so don't use it.  */
    public CRAuthDisplayMngr() { }

    /** Construct with a ref to the constructing authenticator.  This class
     * needs to get info from the authenticator which created it, so a
     * refrence to that authenticator is needed.
     * @param a The CRAuthenticator which this is tied to.
     */
    public CRAuthDisplayMngr(CRAuthenticator a) {
        auth = a;
    }

    /** Set up some variables for the challenge screen.
     * @param ud User data for the user trying to authenticate.
     * @param model The model to set vars in.
     */
    public void setChallengeScreenVars(UserData ud, XMLGenericModel model)
        throws WebMailException
    {
        String chal = auth.getChallenge(ud);
        model.setStateVar("challenge", chal);
    }

    /** Get the filename of the challenge screen.
     * @return The filename of the challenge screen
     */
    public String getChallengeScreenFile() {
        return "challenge.xsl";
    }

    /** Tell the loginscreen not to use a password prompt.  */
    public void setLoginScreenVars(XMLGenericModel model)
        throws WebMailException
    {
        model.setStateVar("pass prompt", "0");
        model.setStateVar("action uri", "challenge");
    }

    protected CRAuthenticator auth;
}
