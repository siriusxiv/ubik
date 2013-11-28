/*
 * @(#)$Id: AuthDisplayMngr.java 38 2008-10-24 19:23:35Z unsaved $
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


/** AuthDisplayMngr is a class that knows how to display various parts of
 * the authentication system.  It's currently used to generate the content
 * of the login screen and setup the password changing fields.
 *
 * Each Authenticator will have an object that is either an instace of, or
 * an instance of a child of AuthDisplayMngr.  URLHandlers can use this
 * objects of this class to setup displays that may be partially
 * Authenticator specific.  The purpose of putting all the information here
 * rather than in the actual Authenticator is that many Authenticators may
 * use the exact same display setup. And only the odd ones will be left
 * out.
 *
 * This class will show a display that is suitable for normal
 * password authentication.  To accomadate other types of authentication
 * this class can be inherited from.  When inherited form, each child class
 * should assure it's self access to any additional information that might
 * be needed.  This may mean including a pointer to the Authenticator which
 * is using that AuthDisplayMngr, or it may include access to other
 * classes.
 *
 * $Id: AuthDisplayMngr.java 38 2008-10-24 19:23:35Z unsaved $
 *
 * @author Devin Kowatch
 * @see net.wastl.webmail.server.Authenticator
 * @see net.wastl.webmail.server.AuthenticatorHandler
 */

package org.webengruven.webmail.auth;

import net.wastl.webmail.exceptions.WebMailException;
import net.wastl.webmail.server.UserData;
import net.wastl.webmail.xml.XMLGenericModel;

public class AuthDisplayMngr {
    /** Default C'tor */
    public AuthDisplayMngr() { }

    /** Setup state vars for the login screen.  If the login screen
     * requires any special state variables, this function should set them
     * up.
     * @param model The model to set vars in.
     */
    public void setLoginScreenVars(XMLGenericModel model)
        throws WebMailException
    {
        model.setStateVar("action uri", "login");
        model.setStateVar("pass prompt", "1");
    }

    /** Get the filename of the loginscreen.
     * @return The filename of the login screen .xsl template
     */
    public String getLoginScreenFile() {
        return "loginscreen.xsl";
    }

    /** Setup state vars for the password change prompt.
     * @param ud UserData for the user who will have their password changed
     * @param model The model to set state vars in
     */
    public void setPassChangeVars(UserData ud, XMLGenericModel model)
        throws WebMailException
    {
        model.setStateVar("pass len", "15");
    }

    /** Get the name of the template that can display the password change
     * screen.
     * @return The name of the template.
     */
    public String getPassChangeTmpl() {
        return "normchangepass";
    }
}
