/*
 * @(#)$Id: ConfigYesNoParameter.java 42 2008-10-24 21:19:58Z unsaved $
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
 * ConfigYesNoParameter.java
 *
 * Created: Wed Sep  8 16:49:22 1999
 *
 * @author Sebastian Schaffert
 */
public class ConfigYesNoParameter extends ChoiceConfigParameter {
    public ConfigYesNoParameter(String name, String desc) {
        super(name,desc);
        addChoice("YES","Enabled.");
        addChoice("NO","Disabled.");
    }

    public String getType() {
        return "bool";
    }
}
