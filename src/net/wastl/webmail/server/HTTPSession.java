/*
 * @(#)$Id: HTTPSession.java 42 2008-10-24 21:19:58Z unsaved $
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


import java.net.InetAddress;
import java.util.Locale;

import net.wastl.webmail.exceptions.InvalidPasswordException;
import net.wastl.webmail.server.http.HTTPRequestHeader;

import org.w3c.dom.Document;

/*
 * HTTPSession.java
 *
 * Created: Thu Sep  9 17:20:37 1999
 *
 * @author Sebastian Schaffert
 */
public interface HTTPSession extends TimeableConnection {
    public void login(HTTPRequestHeader h) throws InvalidPasswordException;

    public void login();

    public void logout();

    public String getSessionCode();

    public Locale getLocale();

    public long getLastAccess();

    public void setLastAccess();

    public String getEnv(String key);

    public void setEnv(String key, String value);

    public void setEnv();

    public InetAddress getRemoteAddress();

    public void saveData();

    public Document getModel();

    public boolean isLoggedOut();

    public void setException(Exception ex);
}
