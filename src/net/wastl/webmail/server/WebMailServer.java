/*
 * @(#)$Id: WebMailServer.java 135 2008-10-31 19:54:26Z unsaved $
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.mail.Provider;
import javax.mail.Session;
import javax.servlet.UnavailableException;

import net.wastl.webmail.config.ConfigScheme;
import net.wastl.webmail.exceptions.WebMailException;
import net.wastl.webmail.misc.Helper;
import net.wastl.webmail.server.http.HTTPRequestHeader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This is WebMails main server. From here most parts will be administered.
 *
 * @author Sebastian Schaffert
 */
public abstract class WebMailServer  {
    private static Log log = LogFactory.getLog(WebMailServer.class);

    protected ConnectionTimer timer;

    protected AuthenticatorHandler ahandler;
    protected PluginHandler phandler;
    protected ToplevelURLHandler uhandler;

    protected Hashtable<String, HTTPSession> sessions;

    public static final String VERSION="@version@";

    protected static Provider[] possible_providers;
    protected static Provider[] store_providers;
    protected static Provider[] transport_providers;

    private long start_time;

    protected static Storage storage;
    protected ConfigScheme config_scheme;

    protected static WebMailServer server;

    protected Properties config;

    /**
     * Webmail default locale setting.
     */
    protected static Locale defaultLocale = null;

    protected static String defaultTheme = null;

    public WebMailServer() {
    }

    protected void doInit() throws UnavailableException, WebMailException {
        server=this;
        log.info("WebMail/Java Server v"+VERSION+" going up...");
        log.info("Initalizing...");

        new SystemCheck(this);

        initConfig();
        /**
         * Initialize the default locale for webmail.
         */
        if ((config.getProperty("webmail.default.locale.language") == null) ||
            (config.getProperty("webmail.default.locale.country") == null))
            defaultLocale = Locale.getDefault();
        else
            defaultLocale = new Locale(
                                       config.getProperty("webmail.default.locale.language"),
                                       config.getProperty("webmail.default.locale.country")
                                       );
        log.info("Default Locale: " + defaultLocale.getDisplayName());

        /*
         * Set the default theme to the parameter given in webmail.default.theme
         * or to "bibop" if unset.
         */
        if(config.getProperty("webmail.default.theme")==null) {
            defaultTheme="bibop";
        } else {
            defaultTheme=config.getProperty("webmail.default.theme");
        }
        log.info("Default Theme: " + defaultTheme);

        ahandler=new AuthenticatorHandler(this);

        log.info("Storage API ("+System.getProperty("webmail.storage")+
                           ") and Configuration ... ");

        initStorage();
        log.info("Storage initialized.");

        timer=new ConnectionTimer();
        sessions = new Hashtable<String, HTTPSession>();

        log.info("Storage initialization done!");

        uhandler=new ToplevelURLHandler(this);

        log.info("URLHandler initialized.");

        phandler=new PluginHandler(this);

        log.info("Plugins initialized.");

        initProviders();

        initServers();

        storage.initConfigKeys();

        log.info("WebMail/Java Server "+VERSION+" initialization completed.");
        log.info("Initalization complete.");
        start_time=System.currentTimeMillis();
    }

    protected void initStorage()
        throws UnavailableException {
        /* Storage API */
        try {
            Class storage_api=Class.forName(config.getProperty("webmail.storage"));

            Class[] tmp=new Class[1];
            tmp[0]=Class.forName("net.wastl.webmail.server.WebMailServer");
            Constructor cons=storage_api.getConstructor(tmp);

            Object[] sargs=new Object[1];
            sargs[0]=this;

            storage=(Storage)cons.newInstance(sargs);

        } catch(InvocationTargetException e) {
            log.fatal("Could not initialize. Exiting now!  Nested exc:",
                    e.getTargetException());
            throw new UnavailableException(e.getMessage());
        } catch(Exception e) {
            log.fatal("Could not initialize. Exiting now!", e);
            throw new UnavailableException(e.getMessage());
        }
    }

    protected void initConfig() {
        config_scheme=new ConfigScheme();

        config_scheme.configRegisterIntegerKey("SESSION TIMEOUT","3600000",
                                               "Timeout in milliseconds after which a WebMailSession is closed automatically.");
        config_scheme.configRegisterCryptedStringKey("ADMIN PASSWORD","Secret",
                                                     "Password for administrator connections. Shown encrypted, but enter"+
                                                     " plain password to change.");
    }


    protected void initProviders() {
        possible_providers=Session.getDefaultInstance(System.getProperties(),null).getProviders();
        log.info("Mail providers:");
        config_scheme.configRegisterChoiceKey("DEFAULT PROTOCOL","Protocol to be used as default");
        int p_transport=0;
        int p_store=0;
        for(int i=0; i<possible_providers.length;i++) {
            log.info(possible_providers[i].getProtocol()+" from "+possible_providers[i].getVendor());
            if(possible_providers[i].getType() == Provider.Type.STORE) {
                p_store++;
                config_scheme.configAddChoice("DEFAULT PROTOCOL",possible_providers[i].getProtocol(),"Use "+
                                              possible_providers[i].getProtocol()+" from "+possible_providers[i].getVendor());
                config_scheme.configRegisterYesNoKey("ENABLE "+possible_providers[i].getProtocol().toUpperCase(),"Enable "+
                                              possible_providers[i].getProtocol()+" from "+possible_providers[i].getVendor());
            } else {
                p_transport++;
            }
        }
        store_providers=new Provider[p_store];
        transport_providers=new Provider[p_transport];
        p_store=0;
        p_transport=0;
        for(int i=0; i<possible_providers.length;i++) {
            if(possible_providers[i].getType() == Provider.Type.STORE) {
                store_providers[p_store]=possible_providers[i];
                p_store++;
            } else {
                transport_providers[p_transport]=possible_providers[i];
                p_transport++;
            }
        }
        /* We want to use IMAP as default, since this is the most useful protocol for WebMail */
        config_scheme.setDefaultValue("DEFAULT PROTOCOL","imap");
    }


    /**
     * Init possible servers of this main class
     */
    protected abstract void initServers();

    protected abstract void shutdownServers();

    public abstract Object getServer(String ID);

    public abstract Enumeration getServers();

    public String getBasePath() {
        return "";
    }

    public String getImageBasePath() {
        return "";
    }


    public abstract void reinitServer(String ID);


    public String getBaseURI(HTTPRequestHeader header) {
        String host=header.getHeader("Host");
        StringTokenizer tok=new StringTokenizer(host,":");
        String hostname=tok.nextToken();
        int port=80;
        if(tok.hasMoreElements()) {
            try {
                port=Integer.parseInt(tok.nextToken());
            } catch(NumberFormatException e) {}
        }
        int ssl_port=443;
        try {
            ssl_port=Integer.parseInt(storage.getConfig("ssl port"));
        } catch(NumberFormatException e) {}
        int http_port=80;
        try {
            http_port=Integer.parseInt(storage.getConfig("http port"));
        } catch(NumberFormatException e) {}
        String protocol="http";
        if(port==ssl_port) protocol="https"; else
            if(port==http_port) protocol="http";
        return protocol+"://"+host;
    }

    public Provider[] getStoreProviders() {
        Vector<Provider> v = new Vector<Provider>();
        for(int i=0;i<store_providers.length;i++) {
            if(storage.getConfig("ENABLE "+store_providers[i].getProtocol().toUpperCase()).equals("YES")) {
                v.addElement(store_providers[i]);
            }
        }
        Provider[] retval=new Provider[v.size()];
        v.copyInto(retval);
        return retval;
    }

    public Provider[] getTransportProviders() {
        return transport_providers;
    }

    public ConnectionTimer getConnectionTimer() {
        return timer;
    }


    public static Storage getStorage() {
        return storage;
    }

    public PluginHandler getPluginHandler() {
        return phandler;
    }

    public AuthenticatorHandler getAuthenticatorHandler() {
        return ahandler;
    }

    public ToplevelURLHandler getURLHandler() {
        return uhandler;
    }

    public ConfigScheme getConfigScheme() {
        return config_scheme;
    }


    public String getProperty(String name) {
        return config.getProperty(name);
    }

    public static String getDefaultTheme() {
        return defaultTheme;
    }

    /**
     * Return default locale.
     *
     * Related code:
     * 1. login screen:
     *    server/TopLevelHandler.java line #110.
     * 2. webmail.css:
     *    plugins/PassThroughPlugin.java line #77.
     * 3. user's default locale setting:
     *    xml/XMLUserData.java line #82.
     *
     * @return default locale.
     */
    public static Locale getDefaultLocale() {
        return defaultLocale;
    }

    public void setProperty(String name, String value) {
        config.put(name,value);
    }

    /**
       @deprecated Use StorageAPI instead
    */
    @Deprecated
    public static String getConfig(String key) {
        return storage.getConfig(key);
    }

    public void restart() throws UnavailableException {
        log.info("Initiating shutdown for child processes:");
        Enumeration e=sessions.keys();
        log.info("Removing active WebMail sessions ... ");
        while(e.hasMoreElements()) {
            HTTPSession w = sessions.get(e.nextElement());
            removeSession(w);
        }
        log.info("Done initializing shutdown for child precesses!");
        shutdownServers();
        try {
            Thread.sleep(5000);
        } catch(Exception ex) {}
        log.info("Shutdown completed successfully. Restarting.");
        storage.shutdown();
        log.info("Garbage collecting ...");
        System.gc();
        try {
            doInit();
        } catch(WebMailException ex) {
            log.error("Server initialization failed", ex);
        }
    }

    public void shutdown() {
        /* This method gets invoked upon webap "stop" and "undeploy",
         * only after the WebMail servlet runs. */
        log.info("Initiating shutdown for child processes:");
        Enumeration e=sessions.keys();
        log.info("Removing active WebMail sessions ... ");
        while(e.hasMoreElements()) {
            HTTPSession w = sessions.get(e.nextElement());
            removeSession(w);
        }
        log.info("Done removing active WebMail sessions!");
        shutdownServers();
        log.info("Shutdown completed successfully. Terminating.");
        storage.shutdown();
        log.info("Shutdown complete!  JWebMail threads should all be stopped.");
        Helper.logThreads("Bottom of WebMailServer.shutdown()");
        //Used to System.exit() here!
    }

    public long getUptime() {
        return System.currentTimeMillis()-start_time;
    }

    public static String getVersion() {
        return "WebMail/Java v"+VERSION+", built with JDK @java-version@";
    }

    public static String getCopyright() {
        return "(c)1999-@year@ Sebastian Schaffert and others";
    }

    public static WebMailServer getServer() {
        return server;
    }

    public static String generateMessageID(String user) {
        long time=System.currentTimeMillis();
        String msgid=Long.toHexString(time)+".JavaWebMail."+VERSION+"."+user;
        try {
            msgid+="@"+InetAddress.getLocalHost().getHostName();
        } catch(Exception ex){}
        return msgid;
    }

    public void removeSession(HTTPSession w) { log.info("Removing session: "+w.getSessionCode());
        timer.removeTimeableConnection(w);
        sessions.remove(w.getSessionCode());
        if(!w.isLoggedOut()) {
            w.logout();
        }
    }

    public HTTPSession getSession(String key) {
        return sessions.get(key);
    }


    public Enumeration getSessions() {
        return sessions.keys();
    }
}
