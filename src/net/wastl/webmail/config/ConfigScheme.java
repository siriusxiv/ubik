/*
 * @(#)$Id: ConfigScheme.java 116 2008-10-30 06:12:51Z unsaved $
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
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class contains a scheme for WebMail configuration data.
 *
 * It is mainly a container for the ConfigParameter objects and a wrapper to access
 * the main functions in them to ease access to the scheme.
 *
 * Created: 31.08.99
 *
 * @author Sebastian Schaffert
 */
public class ConfigScheme {
    private static Log log = LogFactory.getLog(ConfigScheme.class);
    protected Map<String, ConfigParameter> config_scheme;

    public ConfigScheme() {
        log.info("Configuration Scheme Handler ... ");
        config_scheme=new Hashtable<String, ConfigParameter>();
        log.info("Done setting up Config Scheme Handler!");
    }

    /**
     * Check whether a key/value pair is valid in this configuration scheme
     * @param key Name of the parameter
     * @param value value to check for
     */
    public boolean isValid(String key, Object value) {
        ConfigParameter scheme=getConfigParameter(key);
        if(scheme==null) {
            return false;
        } else {
            return scheme.isPossibleValue(value);
        }
    }

    public String filter(String key, String value) {
        ConfigParameter c = config_scheme.get(key);
        if(c!=null) {
            return c.filter(value);
        } else {
            return value;
        }
    }

    /**
     * Register a configuration key that can take String values
     * @param key Name of the configuration key
     * @param default Default value for this key
     * @param desc Description for this key
     */
    public void configRegisterStringKey(String key, String def, String desc) {
        StringConfigParameter parm=new StringConfigParameter(key,def,desc);
        registerConfig(parm);
    }

    public void configRegisterStringKey(ConfigurationListener l,String key, String def, String desc) {
        StringConfigParameter parm=new StringConfigParameter(key,def,desc);
        registerConfig(parm);
        parm.addConfigurationListener(l);
    }

    /**
     * Register a configuration key that can take String values
     * @param key Name of the configuration key
     * @param default Default value for this key
     * @param desc Description for this key
     */
    public void configRegisterIntegerKey(String key, String def, String desc) {
        IntegerConfigParameter parm=new IntegerConfigParameter(key,def,desc);
        registerConfig(parm);
    }

    public void configRegisterIntegerKey(ConfigurationListener l,String key, String def, String desc) {
        IntegerConfigParameter parm=new IntegerConfigParameter(key,def,desc);
        registerConfig(parm);
        parm.addConfigurationListener(l);
    }

    /**
     * Register a configuration key that can take String values and crypts them
     * @param key Name of the configuration key
     * @param default Default value for this key
     * @param desc Description for this key
     */
    public void configRegisterCryptedStringKey(String key, String def, String desc) {
        CryptedStringConfigParameter parm=new CryptedStringConfigParameter(key,def,desc);
        registerConfig(parm);
    }

    public void configRegisterCryptedStringKey(ConfigurationListener l,String key, String def, String desc) {
        CryptedStringConfigParameter parm=new CryptedStringConfigParameter(key,def,desc);
        registerConfig(parm);
        parm.addConfigurationListener(l);
    }

    /**
     * Register a configuration key that can take one of a choice of possible values
     * @param key Name of the configuration key
     * @param desc Description for this key
     * @see configAddChoice
     */
    public void configRegisterChoiceKey(String key, String desc) {
        ChoiceConfigParameter parm=new ChoiceConfigParameter(key,desc);
        registerConfig(parm);
    }

    public void configRegisterChoiceKey(ConfigurationListener l,String key, String desc) {
        ChoiceConfigParameter parm=new ChoiceConfigParameter(key,desc);
        registerConfig(parm);
        parm.addConfigurationListener(l);
    }

    public void configRegisterYesNoKey(String key, String desc) {
        ChoiceConfigParameter parm=new ConfigYesNoParameter(key,desc);
        registerConfig(parm);
    }

    public void configRegisterYesNoKey(ConfigurationListener l,String key, String desc) {
        ChoiceConfigParameter parm=new ConfigYesNoParameter(key,desc);
        registerConfig(parm);
        parm.addConfigurationListener(l);
    }

    /**
     * Add a choice to an already-existing Choice-key
     * @param key Name of the configuration key where a choice is to be added
     * @param choice Name of the new choice
     * @param desc Description for this choice
     */
    public void configAddChoice(String key, String choice, String desc) {
        if(config_scheme!=null) {
            ConfigParameter parm = config_scheme.get(key);
            if(parm instanceof ChoiceConfigParameter) {
                ((ChoiceConfigParameter)parm).addChoice(choice,desc);
            }
        }
    }

    /**
     * Add a configuration listener for a key.
     * There may be any amount of Listeners for a parameter.
     */
    public void addConfigurationListener(String key,ConfigurationListener l) {
        ConfigParameter parm=getConfigParameter(key);
        parm.addConfigurationListener(l);
    }

    public ConfigParameter getConfigParameter(String key) {
        return config_scheme.get(key);
    }

    public String getConfigParameterType(String key) {
        return getConfigParameter(key).getType();
    }

    public String getConfigParameterGroup(String key) {
        return getConfigParameter(key).getGroup();
    }

    public Object getDefaultValue(String key) {
        ConfigParameter cp = config_scheme.get(key);
        if(cp!=null) {
            return cp.getDefault();
        } else {
            return null;
        }
    }

    public void setDefaultValue(String key, Object default_value) {
        ConfigParameter cp = config_scheme.get(key);
        if(cp!=null) {
            cp.setDefault(default_value);
        }
    }


    public String getDescription(String key) {
        ConfigParameter cp = config_scheme.get(key);
        if(cp!=null) {
            return cp.getDescription();
        } else {
            return null;
        }
    }

    public Set<String> getPossibleKeys() {
        return config_scheme.keySet();
    }

    public void notifyConfigurationChange(String key) {
        log.debug("NOTIFY: "+key);
        ConfigParameter parm=getConfigParameter(key);
        if(parm!=null) {
            Enumeration enumVar=parm.getConfigurationListeners();

            while(enumVar.hasMoreElements()) {
                ConfigurationListener l=(ConfigurationListener)enumVar.nextElement();
                // log.debug(l);
                try {
                    l.notifyConfigurationChange(key);
                } catch(Exception e) {
                    log.error("Failed to notify of cofnig change", e);
                }
            }
        }
    }

    public void registerConfig(ConfigParameter parm) {
        if(config_scheme == null)
            config_scheme=new Hashtable<String, ConfigParameter>();
        boolean flag=false;
        for (String schemeKey : config_scheme.keySet()) {
            if(schemeKey.equals(parm.getKey())) {
                flag=true;
                break;
            }
        }
        if(!flag) config_scheme.put(parm.getKey(),parm);
    }
}
