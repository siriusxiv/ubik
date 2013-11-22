/*
 * @(#)$Id: OTPAuthenticator.java 116 2008-10-30 06:12:51Z unsaved $
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


package org.webengruven.webmail.authenticators;

import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import java.util.Random;

import net.wastl.webmail.config.ConfigScheme;
import net.wastl.webmail.exceptions.InvalidPasswordException;
import net.wastl.webmail.exceptions.WebMailException;
import net.wastl.webmail.server.Storage;
import net.wastl.webmail.server.UserData;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webengruven.javaOTP.OTPFormatException;
import org.webengruven.javaOTP.OTPServer;
import org.webengruven.javaOTP.OTPState;
import org.webengruven.webmail.auth.AuthDisplayMngr;
import org.webengruven.webmail.auth.OTPAuthDisplayMngr;
import org.webengruven.webmail.auth.OTPAuthenticatorIface;
import org.webengruven.webmail.auth.OTPCacheNode;

/**
 * OTPAuthenticator.java -- this class is an Authenticator for
 * Webmail/Java that uses One Time Passwords for authentication.  Using the
 * new challenge/response authentication framework, this class will provide
 * authentication based on RFC1938 one time passes.
 *
 * @author Devin Kowatch
 */
public class OTPAuthenticator extends OTPAuthenticatorIface {
    private static Log log = LogFactory.getLog(OTPAuthenticator.class);

    /** The version of OTPAuthenticator */
    public final String     VERSION = "1.2";
    /** The default starting sequence for an OTP */
    public final int        START_SEQ = 499;
    /** The default hash algorithm for an OTP */
    public final String     DFLT_HASH = "MD5";

    private final int       CACHE_ACTIVE_ST = 0x01;
    private final int       CACHE_NEW_ST    = 0x02;

    /** Default Constructor */
    public OTPAuthenticator() {
        super();
        key = "OTP";
        cache = new Hashtable<String, OTPCacheNode>();
    }

    /** Get the AuthDisplayMngr for this class */
    public AuthDisplayMngr getAuthDisplayMngr() {
        if (disp_mngr == null) {
            disp_mngr = new OTPAuthDisplayMngr(this);
        }

        return disp_mngr;
    }

    /** Get this class' version
     * @return A version string for this class
     */
    public String getVersion() {
        return VERSION;
    }

    /**
     * (Re-)Initilize this authenticator.
     */
    public void init(Storage store) {
    }

    /** Register this authenticator with the system.
     */
    public void register(ConfigScheme store) {
        store.configAddChoice("AUTH", key, "Simple OTP Authentication. The"
         + " server admin must create the account,"
         + " users can change passwords");
    }

    /**
     * Get a new challenge for changing the password. This will be
     * displayed on the screen when the user tries to change their
     * password.
     * @param udata The users data, this may be needed when re-keying the
     * password.
     */
    public String getNewChallenge(UserData udata) throws WebMailException {
        OTPState        [] states = new OTPState[2];
        OTPState        st, new_st;
        String          chal = null;
        Random          rand = new Random();
        OTPServer       server = null;
        String          pData = null;
        boolean         newAccount = false;

        getFromCache(udata.getLogin(), CACHE_ACTIVE_ST | CACHE_NEW_ST, states);
        // for convience
        st = states[0];
        new_st = states[1];

        if (st == null) {
            // Not in the cache, lets see if they have logged in before
            pData = udata.getPasswordData();
            if (pData != null && pData.length() > 0) {
                st = new OTPState(pData);
            }
            else {
                // later code expects st to be set
                st = new OTPState("","","",DFLT_HASH);
                newAccount = true;
            }
        }

        try {
            // setup the new seed and seq.
            if (new_st == null) {
                // Get a random value between (0, 10000], and pad to 4 chars.
                int             randVal = rand.nextInt() % 9999;
                StringBuilder    newSeed = new StringBuilder(10);

                newSeed.append(udata.getDomain().substring(0, 2));

                if (randVal < 0) randVal = -randVal;
                if (randVal > 999) {       // 4 digits already, no pad
                }
                else if (randVal > 99) {   // 3 digits only, pad 1
                    newSeed.append("0");
                }
                    else if (randVal > 9) {    // 2 digits only, pad 2
                    newSeed.append("00");
                }
                else {                     // 1 digit only, pad 3
                    newSeed.append("000");
                }
                newSeed.append(String.valueOf(randVal));

                new_st = new OTPState(st);
                new_st.seed = newSeed.toString();
                new_st.sequence = String.valueOf(START_SEQ);
            }

            // if this is a new account don't use an instance of OTPServer
            if (newAccount) {
                chal=OTPServer.getNewChallenge(new_st.seed,START_SEQ,DFLT_HASH);
            }
            else {
                server = new OTPServer(st);
                chal = server.getNewChallenge(new_st.seed, START_SEQ);
            }
        } catch (NoSuchAlgorithmException e) {
            log.error("bad hash algorithm for new OTP.", e);
            throw new WebMailException("bad hash algorithm for new OTP.");
        }

        // for new accounts, st is a dummy.  As such it should not be
        // stored in the cache where it will cause problems by being used
        if (newAccount) {
            st = null;
        }

        // keep track of the new OTPState for changePassword()
        putIntoCache(udata.getLogin(), st, new_st);

        return chal;
    }

    /** Authenticate the user. */
    public void authenticatePostUserData(UserData ud,String dom,String pass)
     throws InvalidPasswordException
    {
        String          login = ud.getLogin();
        OTPCacheNode    n = cache.get(login);
        OTPState        st;
        OTPServer       server;
        String          pData;

        try {
            st = getFromCache(login, CACHE_ACTIVE_ST);
            if (st == null) {
                pData = ud.getPasswordData();
                if (pData == null || pData.length() == 0) {
                    throw new InvalidPasswordException("no password data");
                }
                st = new OTPState(pData);
            }

            server = new OTPServer(st);

            if (! server.checkOTP(pass))
                throw new InvalidPasswordException("bad password");

            /* Update the password data so next time we need a new pass */
            st = server.getState();
            ud.setPasswordData(st.getInfoString());
        }
        catch (OTPFormatException e) {
            throw new InvalidPasswordException("bad OTP format");
        }
        catch (NoSuchAlgorithmException e) {
            throw new InvalidPasswordException("bad hash");
        }
        /*
        catch (Exception e) {
            throw new InvalidPasswordException("WTF?!?!?");
        }
        */

        // Done with n.active_st
        removeFromCache(login, CACHE_ACTIVE_ST);
    }

    /** Change the OTP Stream */
    public void changePassword(UserData ud, String newpass, String vrfy)
     throws InvalidPasswordException
    {
        String          login = ud.getLogin();
        OTPState        st = null;
        OTPServer       server = null;
        String          pData = null;;

        st = getFromCache(login, CACHE_NEW_ST);
        if (st == null)
            throw new InvalidPasswordException(
                "Don't know what challenge the new password is for.");

        if (! newpass.equalsIgnoreCase(vrfy))
            throw new InvalidPasswordException(
                "the password and verify don't match");

        st.otp = newpass;


        try {
            pData = ud.getPasswordData();

            if (pData != null && pData.length() > 0) {
                // Get the old OTP first, so that we can compare.
                server = new OTPServer(new OTPState(pData) );

                if (! server.reinitOTP(st, true))
                    throw new InvalidPasswordException(
                        "The new OTP stream is the same as the old one.");
            }
            else {
                // We need to verify the format of the OTP
                server = new OTPServer(st);
            }
        }
        catch (OTPFormatException e) {
            throw new InvalidPasswordException("bad format in OTP");
        }
        catch (NoSuchAlgorithmException e) {
            throw new InvalidPasswordException("Bad hash name");
        }

        // We do this, so that the Hex rep. is stored.
        ud.setPasswordData(server.getState().getInfoString());

        // remove this from the cache. (kill both so that the active state
        // is not stale
        removeFromCache(login, CACHE_NEW_ST | CACHE_ACTIVE_ST);
    }


    /** Get the challenge for this authentication.  This will get passed some
     * user data and should return the approriate challenge string for that
     * user.
     */
    public String getChallenge(UserData ud) throws WebMailException {
        OTPServer       server;
        OTPState        st = null;
        String          login = ud.getLogin();
        String          chal = null;
        String          pData = null;

        st = getFromCache(login, CACHE_ACTIVE_ST);

        if (st == null) {
            pData = ud.getPasswordData();
            if (pData == null || pData.length() == 0) {
                // No password data has been set.
                throw new WebMailException("No password data available");
            }
            st = new OTPState(pData);
        }

        try {
            server = new OTPServer(st);
            chal = server.getChallenge();
        }
        catch (OTPFormatException e) {
            log.fatal(e);
            // this should never happen.
        }
        catch (NoSuchAlgorithmException e) {
            log.fatal(e);
            // this either
        }

        // final sanity check
        if (chal == null) {
            throw new WebMailException("failed to get the next challenge");
        }

        // I'm expecting that we will be using this again soon.
        putIntoCache(login, st, null);

        return chal;
    }

    /*----------------------- Private Functions -------------------------*/

    private void removeFromCache(String key, int type) {
        OTPCacheNode    node;

        node = cache.get(key);
        if (node == null) {
            node = new OTPCacheNode();
        }

        if ( (type & CACHE_ACTIVE_ST) != 0) {
            node.active_st = null;
        }

        if ( (type & CACHE_NEW_ST) != 0) {
            node.new_st = null;
        }

        if (node.new_st == null && node.active_st == null) {
            cache.remove(key);
        }
        else {
            cache.put(key, node);
        }
    }

    private void putIntoCache(String key, OTPState ast, OTPState nst) {
        OTPCacheNode    node;

        node = cache.get(key);
        if (node == null) {
            node = new OTPCacheNode(ast, nst);
        }
        else {
            if (ast != null) {
                node.active_st = ast;
            }

            if (nst != null) {
                node.new_st = nst;
            }
        }

        cache.put(key, node);
    }

    private void getFromCache(String key, int type, OTPState []sts) {
        OTPCacheNode    node;
        int             i = 0;

        node = cache.get(key);

        if (node != null) {
            if ( (type & CACHE_ACTIVE_ST) != 0) {
                sts[i++] = node.active_st;
            }

            if ( (type & CACHE_NEW_ST) != 0) {
                sts[i] = node.new_st;
            }
        }
    }

    private OTPState getFromCache(String key, int type) {
        OTPCacheNode    node;
        OTPState        rtn;

        node = cache.get(key);
        rtn = null;

        if (node != null) {
            if ( (type & CACHE_ACTIVE_ST) != 0) {
                rtn = node.active_st;
            }
            else if ( (type & CACHE_NEW_ST) != 0) {
                rtn = node.new_st;
            }
        }

        return rtn;
    }

    private Hashtable<String, OTPCacheNode> cache;
    private OTPAuthDisplayMngr  disp_mngr;
}
