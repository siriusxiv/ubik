/*
 * @(#)$Id: MailHostData.java 42 2008-10-24 21:19:58Z unsaved $
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

/**
 * MailHostData.java
 *
 * Created: Sun Feb  7 15:56:39 1999
 *
 * @author Sebastian Schaffert
 */
public interface MailHostData {
    /**
     * The password for this mailbox
     * @return Value of password.
     */
    public String getPassword();

    /**
     * Set the value of password.
     * @param v  Value to assign to password.
     */
    public void setPassword(String  v);

    /**
     * The name of this mailbox
     * @return Value of name.
     */
    public String getName();

    /**
     * Set the value of name.
     * @param v  Value to assign to name.
     */
    public void setName(String  v);

    /**
     * The login for this mailbox
     */
    public String getLogin();

    public void setLogin(String s);

    /**
     * The Hostname for this mailbox
     * @return Value of host.
     */
    public String getHostURL();

    /**
     * Set the value of host.
     * @param v  Value to assign to host.
     */
    public void setHostURL(String  v);

    /**
     * The unique ID of this mailbox
     */
    public String getID();
}
