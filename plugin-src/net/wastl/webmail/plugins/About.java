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
        String content="<BODY BGCOLOR=WHITE><CENTER><H1>About WebMail</H1></CENTER><BR>";
        content+="<H3>Copyright</H3><BR>JWebMail is (c)2008 by the JWebMail Development Team and Sebastian Schaffert,<BR/>"
            +"and is distributed under the terms of the <A HREF=\"http://www.apache.org/licenses/LICENSE-2.0\">Apache 2.0 License</A> "
            +".<BR><P><HR><P>"
            +"<H3>Registered Plugins</H3><BR><UL>";
        Enumeration e=parent.getPluginHandler().getPlugins();
        while(e.hasMoreElements()) {
            Plugin p=(Plugin)e.nextElement();
            content+="<LI><B>"+p.getName()+"</B> (v"+p.getVersion()+"): "+p.getDescription()+"</LI>";
        }
        //System.gc();
        content+="</UL><P><HR><P><H3>System Information</H3><BR><UL><LI><B>Operating System:</B> "+System.getProperty("os.name")
            +"/"+System.getProperty("os.arch")+" "+System.getProperty("os.version")+"</LI><LI><B>Java Virtual Machine:</B> "
            +System.getProperty("java.vm.name")+" version "+System.getProperty("java.version")+" from "
            +System.getProperty("java.vendor")+"</LI><LI><B>Free memory for this JVM:</B> "
            +Runtime.getRuntime().freeMemory()+" bytes</LI></UL></BODY>";
        return new HTMLDocument("About WebMail",content);
    }

    public String provides() {
        return "about";
    }

    public String requires() {
        return "";
    }
}
