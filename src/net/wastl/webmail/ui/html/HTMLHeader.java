/*
 * @(#)$Id: HTMLHeader.java 38 2008-10-24 19:23:35Z unsaved $
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


package net.wastl.webmail.ui.html;


/**
 * A HTML header used by HTMLDocument.
 *
 * @author Sebastian Schaffert
 */
public class HTMLHeader  {
    private String title;

    public HTMLHeader(String title) {
        this.title=title;
    }

    public String toString() {
        String s="<!-- HTML-Header created by webmail.ui.html package -->\n";
        s += "<HTML>\n";
        s += "  <HEAD>\n";
        s += "    <TITLE>"+title+"</TITLE>\n";
        s += "  </HEAD>\n";
        return s;
    }
}
