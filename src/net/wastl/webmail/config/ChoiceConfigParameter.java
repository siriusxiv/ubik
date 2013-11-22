/*
 * @(#)$Id: ChoiceConfigParameter.java 114 2008-10-30 00:49:42Z unsaved $
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

/**
 * Scheme of a parameter that can take one of several choices as value
 */
public class ChoiceConfigParameter extends ConfigParameter {
    Hashtable<Object, String> possible_values;

    public ChoiceConfigParameter(String name, String desc) {
        super(name, null, desc);
        possible_values = new Hashtable<Object, String>();
    }

    public void addChoice(Object choice, String desc) {
        /* First is default */
        if(possible_values.isEmpty()) {
            def_value=choice;
        }
        possible_values.put(choice,desc);
    }

    public void removeChoice(Object choice) {
        possible_values.remove(choice);
    }

    public Enumeration choices() {
        return possible_values.keys();
    }

    public String getDescription(String choice) {
        return (String)possible_values.get(choice);
    }

    public boolean isPossibleValue(Object value) {
        Enumeration e=possible_values.keys();
        boolean flag=false;
        while(e.hasMoreElements()) {
            Object o=e.nextElement();
            if(value.equals(o)) {
                flag=true;
                break;
            }
            //log.debug(String)value + " <> " + (String)o);
        }
        return flag;
    }

    public String getType() {
        return "choice";
    }
}
