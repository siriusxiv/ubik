/*
 * @(#)$Id: SimpleAuthenticator.java 97 2008-10-28 19:41:29Z unsaved $
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

import net.wastl.webmail.config.ConfigScheme;
import net.wastl.webmail.exceptions.InvalidPasswordException;
import net.wastl.webmail.server.Authenticator;
import net.wastl.webmail.server.Storage;
import net.wastl.webmail.server.UserData;

/**
 * SimpleAuthenticator.java
 *
 * Does simple authentication just based on the UserData checkPasswd()
 *
 * Created: Mon Apr 19 11:17:03 1999
 *
 * @author Sebastian Schaffert
 * @see webmail.server.UserData
 */
public class SimpleAuthenticator extends Authenticator {
    public final String VERSION="1.0";

    public SimpleAuthenticator() {
        super();
    }

    public String getVersion() {
        return VERSION;
    }


    public void init(Storage store) {
    }

    public void register(ConfigScheme store) {
        key="SIMPLE";
        store.configAddChoice("AUTH",key,"Very simple style authentication. First login sets password. Password may be changed.");
    }

    public void authenticatePostUserData(UserData udata, String domain,String password) throws InvalidPasswordException {
        if(!udata.checkPassword(password) || password.equals("")) {
            throw new InvalidPasswordException();
        }
    }

    public void changePassword(UserData udata, String passwd, String verify) throws InvalidPasswordException {
        udata.setPassword(passwd,verify);
    }
}
