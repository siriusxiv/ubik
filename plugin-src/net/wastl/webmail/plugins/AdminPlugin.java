/*
 * @(#)$Id: AdminPlugin.java 116 2008-10-30 06:12:51Z unsaved $
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


package net.wastl.webmail.plugins;

import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.UnavailableException;

import net.wastl.webmail.exceptions.DocumentNotFoundException;
import net.wastl.webmail.exceptions.WebMailException;
import net.wastl.webmail.server.AdminSession;
import net.wastl.webmail.server.HTTPSession;
import net.wastl.webmail.server.Plugin;
import net.wastl.webmail.server.URLHandler;
import net.wastl.webmail.server.WebMailServer;
import net.wastl.webmail.server.WebMailVirtualDomain;
import net.wastl.webmail.server.http.HTTPRequestHeader;
import net.wastl.webmail.ui.html.HTMLDocument;
import net.wastl.webmail.ui.xml.XHTMLDocument;
import net.wastl.webmail.xml.XMLGenericModel;
import net.wastl.webmail.xml.XMLSystemData;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Sebastian Schaffert
 */
public class AdminPlugin implements Plugin, URLHandler {
    private static Log log = LogFactory.getLog(AdminPlugin.class);

    public static final String VERSION="1.3";
    public static final String URL="/admin";

    protected Vector sessions;

    WebMailServer parent;

    public AdminPlugin() {
        sessions=new Vector();
    }

    public void register(WebMailServer parent) {
        parent.getURLHandler().registerHandler(URL,this);
        this.parent=parent;
    }

    public String getName() {
        return "Administrator";
    }

    public String getDescription() {
        return "Change WebMail settings";
    }

    public String getVersion() {
        return VERSION;
    }

    public String getURL() {
        return URL;
    }

    /**
     * @deprecated
     * Should never System.exit() in a servlet container.
     */
    @Deprecated
    protected void reqShutdown(int time, boolean reboot) {
        //new ShutdownThread(time,reboot,parent);
        log.fatal("Obsolete method reqShutdown() called");
        throw new RuntimeException("reqShutdown purposefully disabled");
    }

    public HTMLDocument handleURL(String suburl, HTTPSession session, HTTPRequestHeader header) throws WebMailException {
        HTMLDocument content=null;
        if(session != null) {
            session.setLastAccess();
            session.setEnv();
        }
        if(session == null && !(suburl.equals("/") || suburl.equals(""))) {
            throw new DocumentNotFoundException("Could not continue as there was no session id submitted");
        } else if(suburl.startsWith("/login")) {
            log.info("Admin login ... ");
            content=new XHTMLDocument(session.getModel(),
                                      parent.getStorage().getStylesheet("admin-frame.xsl",
                                                                        parent.getDefaultLocale(),
                                                                        parent.getProperty("webmail.default.theme")));

        } else if(suburl.startsWith("/system")) {
            if(suburl.startsWith("/system/set")) {
                XMLSystemData sysdata=parent.getStorage().getSystemData();
                for (String ckey : sysdata.getConfigKeys()) {
                    if(header.isContentSet(ckey)) {
//                      log.debug(ckey+" = "+header.getContent(ckey));
                        sysdata.setConfig(ckey,header.getContent(ckey));
                    }
                }
                parent.getStorage().save();
                session.setEnv();
            }
            content=new XHTMLDocument(session.getModel(),
                                      parent.getStorage().getStylesheet("admin-system.xsl",
                                                                        parent.getDefaultLocale(),
                                                                        parent.getProperty("webmail.default.theme")));

        } else if(suburl.startsWith("/navigation")) {
            content=new XHTMLDocument(session.getModel(),
                                      parent.getStorage().getStylesheet("admin-navigation.xsl",
                                                                        parent.getDefaultLocale(),
                                                                        parent.getProperty("webmail.default.theme")));
        } else if(suburl.startsWith("/control")) {
            if(suburl.startsWith("/control/kill")) {
                String sid=header.getContent("kill");

                log.info("Session "+sid+": removing on administrator request.");

                HTTPSession sess2=parent.getSession(sid);
                if(sess2 != null) {
                    parent.removeSession(sess2);
                }
                session.setEnv();
            }
            if(header.isContentSet("SHUTDOWN")) {
                int time=0;
                try {
                    time=Integer.parseInt(header.getContent("SHUTDOWN SECONDS"));
                } catch(NumberFormatException e) {}
                reqShutdown(time,false);
            } else if(header.isContentSet("REBOOT")) {
                int time=0;
                try {
                    time=Integer.parseInt(header.getContent("SHUTDOWN SECONDS"));
                } catch(NumberFormatException e) {}
                reqShutdown(time,true);
            }

            content=new XHTMLDocument(session.getModel(),
                                      parent.getStorage().getStylesheet("admin-status.xsl",
                                                                        parent.getDefaultLocale(),
                                                                        parent.getProperty("webmail.default.theme")));

        } else if(suburl.startsWith("/domain")) {
            if(suburl.startsWith("/domain/set")) {
                try {
                    Enumeration enumVar=parent.getStorage().getVirtualDomains();
                    while(enumVar.hasMoreElements()) {
                            if(header.getContent("VIRTUALS")!=null) {
                                    if(header.getContent("STATE").equals("disable")) {
                                            parent.getStorage().setVirtuals(false);
                                        } else {
                                                parent.getStorage().setVirtuals(true);
                                        }
                                    break;
                        }
                        String s1=(String)enumVar.nextElement();
                        if(header.getContent("CHANGE "+s1) != null && !header.getContent("CHANGE "+s1).equals("")) {
                            WebMailVirtualDomain vd=parent.getStorage().getVirtualDomain(s1);
                            if(!vd.getDomainName().equals(header.getContent(s1+" DOMAIN"))) {
                                vd.setDomainName(header.getContent(s1+" DOMAIN"));
                            }
                            vd.setDefaultServer(header.getContent(s1+" DEFAULT HOST"));
                            vd.setAuthenticationHost(header.getContent(s1+" AUTH HOST"));
                            vd.setHostsRestricted(header.getContent(s1+" HOST RESTRICTION")!=null);
                            vd.setAllowedHosts(header.getContent(s1+" ALLOWED HOSTS"));
                            parent.getStorage().setVirtualDomain(s1,vd);
                        } else if(header.getContent("DELETE "+s1) != null && !header.getContent("DELETE "+s1).equals("")) {
                            parent.getStorage().deleteVirtualDomain(s1);
                        }
                    }
                    if(header.getContent("ADD NEW") != null && !header.getContent("ADD NEW").equals("")) {
                        WebMailVirtualDomain vd=parent.getStorage().createVirtualDomain(header.getContent("NEW DOMAIN"));
                        vd.setDomainName(header.getContent("NEW DOMAIN"));
                        vd.setDefaultServer(header.getContent("NEW DEFAULT HOST"));
                        vd.setAuthenticationHost(header.getContent("NEW AUTH HOST"));
                        vd.setHostsRestricted(header.getContent("NEW HOST RESTRICTION")!=null);
                        vd.setAllowedHosts(header.getContent("NEW ALLOWED HOSTS"));
                        parent.getStorage().setVirtualDomain(header.getContent("NEW DOMAIN"),vd);
                    }
                } catch(Exception ex) {
                    log.error("Failed to serve /domain/set URL.  "
                        + "Shouldn't we NOT save?  Continuing anyways.", ex);
                }
                parent.getStorage().save();
                session.setEnv();
            }

            content=new XHTMLDocument(session.getModel(),
                                      parent.getStorage().getStylesheet("admin-domains.xsl",
                                                                        parent.getDefaultLocale(),
                                                                        parent.getProperty("webmail.default.theme")));

        } else if(suburl.startsWith("/user")) {
            if(header.isContentSet("domain")) {
                ((AdminSession)session).selectDomain(header.getContent("domain"));
            }

            if(suburl.startsWith("/user/edit") &&
               (header.isContentSet("edit") || header.isContentSet("change"))) {
                if(header.isContentSet("user")) {
                    ((AdminSession)session).selectUser(header.getContent("user"));
            ((AdminSession)session).setupUserEdit();

                    if(header.isContentSet("change")) {
                        ((AdminSession)session).changeUser(header);
                    }

                } else {
                    ((AdminSession)session).clearUser();
                }

                content=new XHTMLDocument(session.getModel(),
                                        parent.getStorage().getStylesheet("admin-edituser.xsl",
                                                                        parent.getDefaultLocale(),
                                                                        parent.getProperty("webmail.default.theme")));

            } else {
                if(header.isContentSet("user") && header.isContentSet("delete")) {
                    ((AdminSession)session).deleteUser(header.getContent("user"));
                }
                content=new XHTMLDocument(session.getModel(),
                                        parent.getStorage().getStylesheet("admin-users.xsl",
                                                                        parent.getDefaultLocale(),
                                                                        parent.getProperty("webmail.default.theme")));
            }
        } else {
            if(suburl.startsWith("/logout")) {
                session.logout();
            }
            //content=new HTMLDocument("WebMail Administrator Login",parent.getStorage(),"adminlogin",parent.getBasePath());
            XMLGenericModel model = parent.getStorage().createXMLGenericModel();
            if(header.isContentSet("login")) {
                model.setStateVar("invalid password","yes");
            }
            content=new XHTMLDocument(model.getRoot(),parent.getStorage().getStylesheet("admin-login.xsl",parent.getDefaultLocale(),parent.getProperty("webmail.default.theme")));
        }
        return content;
    }

    public String provides() {
        return "admin";
    }

    public String requires() {
        return "";
    }


    protected class ShutdownThread extends Thread {
        protected WebMailServer parent;

        protected int time;
        protected boolean reboot;

        ShutdownThread(int time, boolean restart, WebMailServer parent) {
            log.fatal("Aborting ShutdownThread instantiation.  Obsolete.");
            if (true) throw new RuntimeException("ShutdownThread obsoleted");
            this.parent=parent;
            this.time=time;
            this.reboot=restart;
            this.start();
        }

        public void run() {
            String action=reboot?"reboot":"shutdown";
            if(time >=0) {
                log.info("WebMail "+action+" in "+time+" seconds!");
                try {
                    Thread.sleep(time*1000);
                } catch(InterruptedException ex) {}
                log.info("WebMail "+action+" NOW!");
                if(reboot) {
                        try {
                                parent.restart();
                        } catch(UnavailableException ue) {
                                log.fatal("Unable to restart, UnavailableException caught!", ue);
                                // Is it good for us?
                                parent.shutdown();
                        }
                } else {
                    parent.shutdown();
                }
            }
        }
    }
}
