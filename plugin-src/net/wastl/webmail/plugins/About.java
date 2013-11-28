/*
 * @(#)$Id: About.java 113 2008-10-29 23:41:26Z unsaved $
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

import net.wastl.webmail.exceptions.DocumentNotFoundException;
import net.wastl.webmail.server.HTTPSession;
import net.wastl.webmail.server.Plugin;
import net.wastl.webmail.server.URLHandler;
import net.wastl.webmail.server.WebMailServer;
import net.wastl.webmail.server.http.HTTPRequestHeader;
import net.wastl.webmail.ui.html.HTMLDocument;

/**
 * @author Sebastian Schaffert
 */
public class About implements Plugin, URLHandler {
    public static final String VERSION="1.00";
    public static final String URL="/about";

    WebMailServer parent;

    public About() {
    }

    public void register(WebMailServer parent) {
        parent.getURLHandler().registerHandler(URL,this);
        this.parent=parent;
    }

    public String getName() {
        return "About";
    }

    public String getDescription() {
        return "About WebMail";
    }

    public String getVersion() {
        return VERSION;
    }

    public String getURL() {
        return URL;
    }


    public HTMLDocument handleURL(String suburl, HTTPSession session, HTTPRequestHeader header) throws DocumentNotFoundException {
        //String content="<BODY BGCOLOR=WHITE><CENTER><H1>About WebMail</H1></CENTER><BR>";
        StringBuffer contentBuffer = new StringBuffer("<BODY BGCOLOR=WHITE><CENTER><H1>About WebMail</H1></CENTER><BR>");
        contentBuffer=contentBuffer.append("<H3>Copyright</H3><BR>JWebMail is (c)2008 by the JWebMail Development Team and Sebastian Schaffert,<BR/>").append("and is distributed under the terms of the <A HREF=\"http://www.apache.org/licenses/LICENSE-2.0\">Apache 2.0 License</A> ").append(".<BR><P><HR><P>").append("<H3>Registered Plugins</H3><BR><UL>");
        Enumeration<Plugin> e=parent.getPluginHandler().getPlugins();
	Plugin p;
        while(e.hasMoreElements()) {
            p=(Plugin)e.nextElement();
            contentBuffer=contentBuffer.append("<LI><B>").append(p.getName()).append("</B> (v").append(p.getVersion()).append("): ").append(p.getDescription()).append( "</LI>");         
        }
        //System.gc();
        contentBuffer=contentBuffer.append("</UL><P><HR><P><H3>System Information</H3><BR><UL><LI><B>Operating System:</B> ").append(System.getProperty("os.name")).append("/").append(System.getProperty("os.arch")).append(" ").append(System.getProperty("os.version")).append("</LI><LI><B>Java Virtual Machine:</B> ").append(System.getProperty("java.vm.name")).append(" version ").append(System.getProperty("java.version")).append(" from ").append(System.getProperty("java.vendor")).append("</LI><LI><B>Free memory for this JVM:</B> ").append(Runtime.getRuntime().freeMemory()).append(" bytes</LI></UL></BODY>");
      
        return new HTMLDocument("About WebMail",contentBuffer.toString());
    }

    public String provides() {
        return "about";
    }

    public String requires() {
        return "";
    }
}
