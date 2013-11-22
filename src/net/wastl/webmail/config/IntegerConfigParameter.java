/*
 * @(#)$Id: IntegerConfigParameter.java 113 2008-10-29 23:41:26Z unsaved $
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

/**
 * @author Sebastian Schaffert
 */
public class IntegerConfigParameter extends ConfigParameter {
    public IntegerConfigParameter(String name, String def, String desc) {
        super(name,def,desc);
    }

    public boolean isPossibleValue(Object value) {
        try {
            int i=Integer.parseInt((String)value);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }

    public String getType() {
        return "integer";
    }
}
