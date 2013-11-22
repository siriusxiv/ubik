/*
 * @(#)$Id: PluginHandler.java 116 2008-10-30 06:12:51Z unsaved $
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

import java.io.File;
import java.io.FilenameFilter;
import java.util.Enumeration;
import java.util.Vector;

import net.wastl.webmail.exceptions.WebMailException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * PluginHandler.java
 *
 * Handle WebMail Plugins
 *
 * Created: Tue Aug 31 15:28:45 1999
 *
 * @author Sebastian Schaffert
 */
public class PluginHandler  {
    private static Log log = LogFactory.getLog(PluginHandler.class);

    WebMailServer parent;
    String plugin_list = null;
    Vector<Plugin> plugins;

    public PluginHandler(WebMailServer parent) throws WebMailException {
        this.parent=parent;
        plugin_list=parent.getProperty("webmail.plugins");
        if(plugin_list == null) {
            throw new WebMailException("Error: No Plugins defined (Property webmail.plugins).");
        }
        plugins = new Vector<Plugin>();
        registerPlugins();
    }


     /**
     * Initialize and register WebMail Plugins.
     */
    @SuppressWarnings("unchecked")
    public void registerPlugins() throws WebMailException {
        String[] pluginStrings = plugin_list.trim().split("\\s*,\\s*", -1);
        log.info("Initializing " + pluginStrings.length
                + " WebMail Plugins ...");
        //      System.setProperty("java.class.path",System.getProperty("java.class.path")+System.getProperty("path.separator")+pluginpath);

        Class plugin_class=null;
        try {
            plugin_class=Class.forName("net.wastl.webmail.server.Plugin");
        } catch(ClassNotFoundException ex) {
            log.fatal("Could not find interface 'Plugin'", ex);
            throw new WebMailException("Could not find inteface 'Plugin'");
            // Used to System.exit() here.
        }

        PluginDependencyTree pt=new PluginDependencyTree("");
        net.wastl.webmail.misc.Queue q=new net.wastl.webmail.misc.Queue();

        for (String pluginString : pluginStrings) try {
            Class c = Class.forName(pluginString);
            if (!plugin_class.isAssignableFrom(c)) {
                log.warn("Requested plugin '" + pluginString
                        + "' not a plugin_class.getName().  Skipping.");
                continue;
            }
            Plugin p=(Plugin) c.newInstance();
            q.queue(p);
            plugins.addElement(p);
            log.debug("Registered plugin '"+c.getName()+"'");
        } catch(Exception ex) {
            log.error("Failed to register plugin '" + pluginString + "'",
                    ex);
        }
        log.info(Integer.toString(plugins.size()) + " plugins loaded");

        while(!q.isEmpty()) {
            Plugin p=(Plugin)q.next();
            if(!pt.addPlugin(p)) q.queue(p);
        }
        pt.register(parent);
        log.info(Integer.toString(plugins.size()) + " plugins initialized");
    };

    public Enumeration getPlugins() {
        return plugins.elements();
    }

    /**
     * A filter to find WebMail Plugins.
     */
    class FFilter implements FilenameFilter {
        FFilter() {
        }

        public boolean accept(File f, String s) {
            if(s.endsWith(".class")) {
                return true;
            } else {
                return false;
            }
        }
    }
}
