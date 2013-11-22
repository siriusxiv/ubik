/*
 * @(#)$Id: UnixAuthenticator.java 97 2008-10-28 19:41:29Z unsaved $
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import net.wastl.webmail.config.ConfigScheme;
import net.wastl.webmail.exceptions.InvalidPasswordException;
import net.wastl.webmail.misc.Helper;
import net.wastl.webmail.server.Authenticator;
import net.wastl.webmail.server.Storage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * UnixAuthenticator.java
 *
 * Created: Mon Apr 19 13:43:48 1999
 *
 * @author Sebastian Schaffert
 */
public class UnixAuthenticator extends Authenticator {
    private static Log log = LogFactory.getLog(UnixAuthenticator.class);

    public final String VERSION="1.2";

    public static final String passwd="/etc/passwd";
    public static final String shadow="/etc/shadow";

    public UnixAuthenticator() {
        super();
    }

    public String getVersion() {
        return VERSION;
    }

    public void init(Storage store) {
    }

    public void register(ConfigScheme store) {
        key="UNIX";
        store.configAddChoice("AUTH",key,"Authenticate against the local Unix server's passwd/shadow files. Password change not possible.");
    }

    public void authenticatePreUserData(String user, String domain,
     String given_passwd) throws InvalidPasswordException
    {
        super.authenticatePreUserData(user,domain,given_passwd);
        String login=user;
        try {
            File f_passwd=new File(passwd);
            File f_shadow=new File(shadow);
            BufferedReader in;
            if(f_shadow.exists()) {
                in=new BufferedReader(new InputStreamReader(new FileInputStream(f_shadow)));
            } else {
                in=new BufferedReader(new InputStreamReader(new FileInputStream(f_passwd)));
            }
            String line;
            line=in.readLine();
            while(line != null) {
                if(line.startsWith(login)) break;
                line=in.readLine();
            }

            if(line == null) throw new InvalidPasswordException("Invalid user: "+login);


            StringTokenizer tok=new StringTokenizer(line,":");
            String my_login=tok.nextToken();
            String password=tok.nextToken();

            if(!password.equals(Helper.crypt(password,given_passwd))) {
                log.warn("UnixAuthentication: user "+login+
                                               " authentication failed.");
                throw new InvalidPasswordException("Unix authentication failed");
            }
            log.info("UnixAuthentication: user "+login+
                                           " authenticated successfully.");
        } catch(IOException ex) {
            log.error("Cannot use UnixAuthentication and shadow passwords if WebMail is not executed as user 'root'!");
            throw new InvalidPasswordException("User login denied due to configuration error (contact system administrator)");
        }
    }

    /**
     * Don't allow to change Unix-Passwords as this could mess things up.
     */
    public boolean canChangePassword() {
        return false;
    }
}
