/*
 * @(#)$Id: ConfigParameter.java 114 2008-10-30 00:49:42Z unsaved $
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


package net.wastl.webmail.config;

import java.util.Enumeration;
import java.util.Vector;

/**
 * An abstraction for a configuration parameter.
 * Subclasses must implement a method that checks whether a specific value is correct for this
 * parameter.
 *
 * ConfigParameters may have ConfigurationListeners that work much like the Listeners in the
 * Java AWT. All listeners get informed if the value of the parameter has changed.
 *
 * Each ConfigParameter has a corresponding (unique) key, a default value (if not yet changed
 * by the user) and a short description for the administrator about what the parameter means.
 *
 * This is a scheme only, however, ConfigParameters just describe the behaviour of certain
 * keys in the WebMail configuration, they don't actually store the value itself.
 */
public abstract class ConfigParameter {
    protected String key;
    protected Object def_value;
    protected String desc;
    protected Vector<ConfigurationListener> listeners;

    protected String group;

    /**
     * Create a new parameter.
     * @param name Unique key of this parameter
     * @param def Default value for this parameter
     * @param desc Description for this parameter
     */
    public ConfigParameter(String name, Object def, String desc) {
        key=name;
        this.def_value=def;
        this.desc=desc;
        group="default";
        listeners = new Vector<ConfigurationListener>();
    }

    public void setGroup(String g) {
        group=g;
    }

    /**
     * Return the key of this parameter.
     */
    public String getKey() {
        return key;
    }

    /**
     * Return the default value of this parameter.
     */
    public Object getDefault() {
        return def_value;
    }

    public void setDefault(Object value) {
        def_value=value;
    }

    /**
     * Return the description for this parameter.
     */
    public String getDescription() {
        return desc;
    }

    /**
     * Add a ConfigurationListener for this object that will be informed if the parameter's
     * value changes.
     */
    public void addConfigurationListener(ConfigurationListener l) {
        listeners.addElement(l);
    }

    /**
     * Get a list of all configuration listeners.
     */
    public Enumeration getConfigurationListeners() {
        return listeners.elements();
    }

    /**
     * Put through some sort of filter.
     * This method is called when a String value for this parameter is set.
     * Subclasses should implement this, if they want to change the behaviour
     * @see CryptedStringConfigParameter
     */
    public String filter(String s) {
        return s;
    }

    /**
     * Check whether the value that is passed as the parameter is a valid value for this
     * ConfigParameter
     * @see ChoiceConfigParameter
     */
    public abstract boolean isPossibleValue(Object value);

    public String getType() {
        return "undefined";
    }

    public String getGroup() {
        return group;
    }
}
