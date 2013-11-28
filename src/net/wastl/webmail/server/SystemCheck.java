/*
 * @(#)$Id: SystemCheck.java 113 2008-10-29 23:41:26Z unsaved $
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

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.wastl.webmail.exceptions.WebMailException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Sebastian Schaffert
 */
public class SystemCheck  {
    private static Log log = LogFactory.getLog(SystemCheck.class);
    public SystemCheck(WebMailServer parent) throws WebMailException {
        log.info("Checking Java Virtual Machine ... ");
        log.info("Version: "+System.getProperty("java.version")+" ... ");

        /* Test if the Java version might cause trouble */
        if(System.getProperty("java.version").compareTo("1.5")>=0) {
            log.info("JDK version ok.");
        } else {
            log.warn("At least Java 1.5 is required for WebMail.");
        }

        /* Test if the operating system is supported */
        log.info("Operating System: "+System.getProperty("os.name")+"/"+System.getProperty("os.arch")+" "+System.getProperty("os.version")+" ... ");
        if(System.getProperty("os.name").equals("SunOS") ||
            System.getProperty("os.name").equals("Solaris") ||
            System.getProperty("os.name").equals("Linux")) {
            log.info("OS variant Ok");
        } else {
            log.warn("WebMail was only tested\n   on Solaris, HP-UX and Linux and may cause problems on your platform.");
        }

        /* Check if we are running as root and issue a warning */
        try {
            log.info("User name: "+System.getProperty("user.name")+" ... ");
            if(!System.getProperty("user.name").equals("root")) {
                log.info("User ok.");
            } else {
                log.warn("warning. You are running WebMail as root. This may be a potential security problem.");
            }
        } catch(Exception ex) {
            // Security restriction prohibit reading the username, then we do not need to
            // check for root anyway
        }

        /* Check whether all WebMail system properties are defined */
        log.info("WebMail System Properties: ");
        //checkPathProperty(parent,"webmail.plugin.path");
        //checkPathProperty(parent,"webmail.auth.path");
        checkPathProperty(parent,"webmail.lib.path");
        checkPathProperty(parent,"webmail.template.path");
        checkPathProperty(parent,"webmail.data.path");
        checkPathProperty(parent,"webmail.xml.path");
        log.info("WebMail System Properties ok!");

        log.info("Setting DTD-path in webmail.xml ... ");
        File f1=new File(parent.getProperty("webmail.data.path")+System.getProperty("file.separator")+"webmail.xml");
        File f2=new File(parent.getProperty("webmail.data.path")+System.getProperty("file.separator")+"webmail.xml."+Long.toHexString(System.currentTimeMillis()));

        try {
            Pattern regexp=Pattern.compile("<!DOCTYPE SYSDATA SYSTEM \".*\">");
            BufferedReader file1=new BufferedReader(new FileReader(f1));
            PrintWriter file2=new PrintWriter(new FileWriter(f2));
            try {
                String line=file1.readLine();
                while(line != null) {
                    Matcher m = regexp.matcher(line);
                    String s = m.replaceAll("<!DOCTYPE SYSDATA SYSTEM \"file://"+
                                            parent.getProperty("webmail.xml.path")+
                                            System.getProperty("file.separator")+
                                            "sysdata.dtd"+"\">");
//                  String s=regexp.substituteAll(line,"<!DOCTYPE SYSDATA SYSTEM \"file://"+
//                                                parent.getProperty("webmail.xml.path")+
//                                                System.getProperty("file.separator")+
//                                                "sysdata.dtd"+"\">");
                    //log.debug(s);
                    file2.println(s);
                    line=file1.readLine();
                }
            } catch(EOFException ex) {
            }
            file2.close();
            file1.close();
        } catch(Exception ex) {
            throw new WebMailException(ex);
        }
        f2.renameTo(f1);
        log.info("Done checking system!");
    }

    protected static void checkPathProperty(WebMailServer parent,String property) throws WebMailException {
        if(parent.getProperty(property) == null ||
           parent.getProperty(property).equals("")) {
            throw new WebMailException("fatal error. "+property+" not defined.");
        } else {
            File f=new File(parent.getProperty(property));
            parent.setProperty(property,f.getAbsolutePath());
        }
    }
}
