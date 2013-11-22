/*
 * @(#)$Id: AdminSession.java 116 2008-10-30 06:12:51Z unsaved $
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
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.mail.Folder;
import javax.mail.Provider;
import javax.servlet.http.HttpSession;

import net.wastl.webmail.exceptions.InvalidDataException;
import net.wastl.webmail.exceptions.InvalidPasswordException;
import net.wastl.webmail.exceptions.UserDataException;
import net.wastl.webmail.exceptions.WebMailException;
import net.wastl.webmail.misc.Helper;
import net.wastl.webmail.server.http.HTTPRequestHeader;
import net.wastl.webmail.xml.XMLAdminModel;
import net.wastl.webmail.xml.XMLCommon;
import net.wastl.webmail.xml.XMLSystemData;
import net.wastl.webmail.xml.XMLUserData;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.webengruven.webmail.auth.AuthDisplayMngr;

/**
 * @author Sebastian Schaffert
 */
public class AdminSession implements HTTPSession {
    private static Log log = LogFactory.getLog(AdminSession.class);

    /** When has the session been last accessed? */
    private long last_access;
    /** The session-ID for this session */
    private String session_code;
    /** Parent WebMailServer */
    protected WebMailServer parent;

    protected InetAddress remote;
    private String remote_agent;
    private String remote_accepts;

    protected XMLAdminModel model;

    protected HttpSession sess=null;

    protected boolean running_as_servlet=false;

    protected String selected_domain="";
    protected String selected_user="";

    protected boolean is_logged_out=false;

    public AdminSession(WebMailServer parent, Object parm, HTTPRequestHeader h) throws InvalidPasswordException, WebMailException {
        try {
            Class srvltreq=Class.forName("javax.servlet.http.HttpServletRequest");
            if(srvltreq.isInstance(parm)) {
                running_as_servlet=true;
                javax.servlet.http.HttpServletRequest req=(javax.servlet.http.HttpServletRequest)parm;
                this.sess=req.getSession(false);
                session_code=((javax.servlet.http.HttpSession)sess).getId();
                try {
                    remote=InetAddress.getByName(req.getRemoteHost());
                } catch(UnknownHostException e) {
                    try {
                        remote=InetAddress.getByName(req.getRemoteAddr());
                    } catch(Exception ex) {
                        try {
                            remote=InetAddress.getByName("localhost");
                        } catch(Exception ex2) {}
                    }
                }
            } else {
                throw new Exception("Running as Servlet but not a valid ServletRequest");
            }
        } catch(Throwable t) {
            this.remote=(InetAddress)parm;
            session_code=Helper.calcSessionCode(remote,h);
        }
        doInit(parent,h);
    }

    protected void doInit(WebMailServer parent, HTTPRequestHeader h)
        throws InvalidPasswordException, WebMailException {
        this.parent=parent;
        last_access=System.currentTimeMillis();
        remote_agent=h.getHeader("User-Agent").replace('\n',' ');
        remote_accepts=h.getHeader("Accept").replace('\n',' ');
        //env=new Hashtable();
        model=parent.getStorage().createXMLAdminModel();
        login(h);
        log.info("WebMail: New Session ("+session_code+")");


        setEnv();
    }

    public void login(HTTPRequestHeader h) throws InvalidPasswordException {
        String passwd=parent.getStorage().getConfig("ADMIN PASSWORD");
        if(!Helper.crypt(passwd,h.getContent("password")).equals(passwd)) {
            throw new InvalidPasswordException();
        }
        login();
        log.info("Ok");
    }

    public void login() {
        setLastAccess();
        setEnv();
    }

    public void logout() {
        if(!is_logged_out) {
            if(sess!=null) {
                try {
                  sess.invalidate();
                } catch(Exception ex) {}
            }
            if(parent.getSession(getSessionCode()) != null) {
                parent.removeSession(this);
            }
        }
        is_logged_out=true;
    }

    public boolean isLoggedOut() {
        return is_logged_out;
    }

    public String getSessionCode() {
        return session_code;
    }

    public Locale getLocale() {
        return Locale.getDefault();
    }

    public long getLastAccess() {
        return last_access;
    }

    public void setLastAccess() {
        last_access=System.currentTimeMillis();
    }

    public String getEnv(String key) {
        return model.getStateVar(key);
    }


    public void selectUser(String user) {
      try {
            selected_user=user;
            System.err.println("Selecting user "+user);
            XMLUserData ud=parent.getStorage().getUserData(user,selected_domain,"");
            System.err.println("Done.");
            model.importUserData(ud.getUserData());
      }
      catch (InvalidPasswordException e) { }
      catch (UserDataException e) { }
    }

    public void clearUser() {
        selected_user="";
        model.clearUserData();
    }

    public void deleteUser(String user) {
        parent.getStorage().deleteUserData(user,selected_domain);
        // Refresh information
        selectDomain(selected_domain);
    }

    /** This does all the necessary setup to edit the currently selected
     * user.
     */
    public void setupUserEdit() throws WebMailException {
        XMLUserData ud;
        AuthDisplayMngr adm;

        ud=parent.getStorage().getUserData(selected_user, selected_domain, "");
        adm=parent.getStorage().getAuthenticator().getAuthDisplayMngr();

        adm.setPassChangeVars(ud, model);
        model.setStateVar("pass change tmpl", adm.getPassChangeTmpl());
    }


    public void setException(Exception ex) {
        model.setException(ex);
    }

    /**
     * Change the settings for a specific user.
     * This method will check for changes to a user's configuration and save the new user configuration.
     * Note that this should not be done when a user session is still active!
     * @param h Header parsed from AdministratorPlugin
     */
    public void changeUser(HTTPRequestHeader head) throws WebMailException {
        XMLUserData user=parent.getStorage().getUserData(selected_user,selected_domain,"",false);

        Enumeration contentkeys=head.getContentKeys();
        user.resetBoolVars();
        while(contentkeys.hasMoreElements()) {
            String key=((String)contentkeys.nextElement()).toLowerCase();
            if(key.startsWith("intvar")) {
                try {
                    long value=Long.parseLong(head.getContent(key));
                    user.setIntVar(key.substring(7),value);
                } catch(NumberFormatException ex) {
                    System.err.println("Warning: Remote provided illegal intvar in request header: \n("+key+","+head.getContent(key)+")");
                }
            } else if(key.startsWith("boolvar")) {
                boolean value=head.getContent(key).toUpperCase().equals("ON");
                user.setBoolVar(key.substring(8),value);
            }
        }

        user.setSignature(head.getContent("user signature"));
        user.setFullName(head.getContent("user full name"));
        user.addEmail(head.getContent("user email"));
        user.setDefaultEmail(head.getContent("user email"));
        if(!head.getContent("user password").equals("")) {
            net.wastl.webmail.server.Authenticator auth=parent.getStorage().getAuthenticator();
            if(auth.canChangePassword()) {
            try {
                        auth.changePassword(user,head.getContent("user password"),head.getContent("user password"));
            }
            catch (InvalidPasswordException e) {
                /* XXX Not sure this is the right exception */
                            /**
                throw new InvalidDataException(parent.getStorage().getStringResource("EX NO CHANGE PASSWORD", Locale.getDefault()));
                                **/
                                throw new InvalidDataException(parent.getStorage().getStringResource("EX NO CHANGE PASSWORD", parent.getDefaultLocale()));
            }
            } else {
                throw new InvalidDataException(parent.getStorage().getStringResource("EX NO CHANGE PASSWORD",Locale.getDefault()));
            }
        }
        user.setPreferredLocale(head.getContent("user language"));

        parent.getStorage().saveUserData(selected_user,selected_domain);

        selectUser(selected_user);
        selectDomain(selected_domain);
    }

    public void selectDomain(String domain) {
        model.setStateVar("selected domain",domain);

        selected_domain=domain;

        Enumeration enumVar=parent.getStorage().getUsers(domain);
        model.removeAllStateVars("user");
        while(enumVar.hasMoreElements()) {
            model.addStateVar("user",(String)enumVar.nextElement());
        }
    }


    public void setEnv(String key, String value) {
        //env.put(key,value);
        model.setStateVar(key,value);
    }

    public void setEnv() {
        model.setStateVar("session id",session_code);
        model.setStateVar("base uri",parent.getBasePath());
        model.setStateVar("img base uri",parent.getBasePath());
        model.setStateVar("uptime",parent.getUptime()/1000+"");
        model.update();

        // Here we must initialize which choices are available for ChoiceConfigParameters!
        XMLSystemData sysdata=parent.getStorage().getSystemData();
        sysdata.initChoices();

        if(running_as_servlet) {
            model.setStateVar("servlet status",parent.toString());
        } else {
            model.setStateVar("http server status",((StatusServer)parent.getServer("HTTP")).getStatus());
            model.setStateVar("ssl server status",((StatusServer)parent.getServer("SSL")).getStatus());
        }
        model.setStateVar("storage status",parent.getStorage().toString());

        /*
          Generate a list of active sessions with some additional information
          (idle time, session code, active mail connections, ...)
        */
        XMLCommon.genericRemoveAll(model.getStateData(),"SESSION");
        Enumeration e=parent.getSessions();
        if(e != null && e.hasMoreElements()) {
            while(e.hasMoreElements()) {
                String name=(String)e.nextElement();
                HTTPSession h=parent.getSession(name);
                if(h instanceof WebMailSession) {
                    WebMailSession w=(WebMailSession)h;

                    Element sess_elem=model.addStateElement("SESSION");
                    sess_elem.setAttribute("type","user");

                    sess_elem.appendChild(model.createTextElement("SESS_USER",w.getUserName()));
                    sess_elem.appendChild(model.createTextElement("SESS_CODE",w.getSessionCode()));
                    sess_elem.appendChild(model.createTextElement("SESS_ADDRESS",w.getRemoteAddress().toString()));
                    sess_elem.appendChild(model.createStateVar("idle time",(System.currentTimeMillis()-w.getLastAccess())/1000+""));

                    for (Map.Entry<String, Folder> connEntry :
                            w.getActiveConnections().entrySet()) try {
                        sess_elem.appendChild(model.createTextElement("SESS_CONN",connEntry.getValue().getURLName()+""));
                    } catch(Exception ex) {
                        sess_elem.appendChild(model.createTextElement("SESS_CONN","Error while fetching connection "+connEntry.getKey()));
                    }
                    /* If the remote is admin and we are not the remote! */
                    // && !h.getSessionCode().equals(session_code)
                } else if(h instanceof AdminSession) {
                    Element sess_elem=model.addStateElement("SESSION");
                    sess_elem.setAttribute("type","admin");

                    sess_elem.appendChild(model.createTextElement("SESS_USER","Administrator"));
                    sess_elem.appendChild(model.createTextElement("SESS_ADDRESS",h.getRemoteAddress().toString()));
                    sess_elem.appendChild(model.createTextElement("SESS_CODE",h.getSessionCode()));
                    sess_elem.appendChild(model.createStateVar("idle time",(System.currentTimeMillis()-h.getLastAccess())/1000+""));
                }
            }
        }

        // Add all languages to the state
        model.removeAllStateVars("language");
        String lang=parent.getConfig("languages");
        StringTokenizer tok=new StringTokenizer(lang," ");
        while(tok.hasMoreTokens()) {
            String t=tok.nextToken();
            model.addStateVar("language",t);
        }

        model.removeAllStateVars("protocol");
        Provider[] stores=parent.getStoreProviders();
        for(int i=0; i<stores.length; i++) {
            model.addStateVar("protocol",stores[i].getProtocol());
        }
    }


    public InetAddress getRemoteAddress() {
        return remote;
    }


    public long getTimeout() {
        return 600000;
    }

    public void timeoutOccured() {
    }


    public void saveData() {
    }

    public Document getModel() {
        return model.getRoot();
    }
}
