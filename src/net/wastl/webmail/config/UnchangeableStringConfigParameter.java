/*
 * @(#)$Id: UnchangeableStringConfigParameter.java 38 2008-10-24 19:23:35Z unsaved $
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
 * This is a parameter that cannot be changed.
 *
 * Created: Tue Oct 19 16:16:18 1999
 *
 * @author Sebastian Schaffert
 */
public class UnchangeableStringConfigParameter extends StringConfigParameter {
    public UnchangeableStringConfigParameter(String name, String def, String desc) {
        super(name,def,desc);
    }

    public boolean isPossibleValue(Object value) {
        return false;
    }

    public String getType() {
        return "unchangeable";
    }
}
