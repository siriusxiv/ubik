/*
 * @(#)$Id: XMLGenericModel.java 116 2008-10-30 06:12:51Z unsaved $
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


package net.wastl.webmail.xml;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import net.wastl.webmail.server.WebMailServer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * A generic representation of WebMail data. Contains mainly state information
 * and the system configuration
 *
 * @author Sebastian Schaffert
 */
public class XMLGenericModel extends XMLData {
    private static Log log = LogFactory.getLog(XMLGenericModel.class);
    //protected Document root;

    protected Element sysdata;

    protected Element statedata;

    protected WebMailServer parent;

    protected long current_id=0;

    protected DocumentBuilder parser;

    public XMLGenericModel(WebMailServer parent, Element rsysdata)
        throws ParserConfigurationException, org.xml.sax.SAXException, java.io.IOException {
        super();
        this.parent=parent;

        parser=DocumentBuilderFactory.newInstance().newDocumentBuilder();

        initRoot();

        statedata=root.createElement("STATEDATA");

        NodeList nl=getNodeListXPath("//STATEDATA");

        if(nl != null && nl.getLength() > 0) {
            log.warn("Webmail usermodel template contained a STATEDATA tag, although this should only be created at runtime!");
            root.getDocumentElement().replaceChild(statedata,nl.item(0));
        } else {
            root.getDocumentElement().appendChild(statedata);
        }

        this.sysdata=rsysdata;
    }

    protected void initRoot() throws org.xml.sax.SAXException, java.io.IOException {
        // Create a new usermodel from the template file
        root = parser.parse("file://"+parent.getProperty("webmail.xml.path")+
                            System.getProperty("file.separator")+"generic_template.xml");
        this.data=root.getDocumentElement();
    }

    public Document getRoot() {
        return root;
    }

    public Element getStateData() {
        return statedata;
    }

    public void init() {
        setStateVar("base uri",parent.getBasePath());
        setStateVar("img base uri",parent.getImageBasePath()+
                    "/"+parent.getDefaultLocale().getLanguage()+
                    "/"+parent.getDefaultTheme());
        setStateVar("webmail version",parent.getVersion());
        setStateVar("operating system",System.getProperty("os.name")+" "+
                    System.getProperty("os.version")+"/"+System.getProperty("os.arch"));
        setStateVar("java virtual machine",System.getProperty("java.vendor")+" "+
                    System.getProperty("java.vm.name")+" "+System.getProperty("java.version"));
    }

    /**
     * Insert the sysdata and userdata objects into the usermodel tree
     */
    public void update() {
        Node n = null;
        try {
             n = getNodeXPath("//SYSDATA");
        } catch (TransformerException te) {
            log.error("Failed to extract node for XPath '//SYSDATA'.  "
                    + "Aborting update");
            XMLCommon.dumpXML(log, "//SYSDATA", root);
            // TODO:  throw here instead of silently failing (from
            //        user's perspective).
        }
        if (n == null) return;
        log.debug("Got a '//SYSDATA' node");
        try {
            root.getDocumentElement().replaceChild(
                    root.importNode(sysdata,true), n);
        } catch(DOMException ex) {
            log.error(
                "Failed to replace //SYSDATA Element in the XML generic model",
                ex);
            XMLCommon.dumpXML(log, "//SYSDATA", root);
            // There is a real bug that manifests here.
            // Occurs when logging into Admin Plugin (or when it brings
            // up the main System Configuration page the first time).
        }
    }

    /**
     * Create a unique ID.
     * Important: This returns a new Unique ID within this session.
     * It should be used to generate IDs for Folders and messages so that they can be easily referenced
     */
    public String getNextID() {
        return Long.toHexString(++current_id).toUpperCase();
    }

    public synchronized void setException(Exception ex) {
        Element exception=root.createElement("EXCEPTION");
        Element ex_message=root.createElement("EX_MESSAGE");
        Element ex_stacktrace=root.createElement("EX_STACKTRACE");
        exception.appendChild(ex_message);
        exception.appendChild(ex_stacktrace);

        Text msg=root.createTextNode(ex.getMessage());
        ex_message.appendChild(msg);

        String my_stack="";
        CharArrayWriter cstream=new CharArrayWriter();
        ex.printStackTrace(new PrintWriter(cstream));
        my_stack=cstream.toString();
        CDATASection stack=root.createCDATASection(my_stack);
        ex_stacktrace.appendChild(stack);

        NodeList nl=getNodeListXPath("//EXCEPTION");
        if(nl.getLength() > 0) {
            statedata.replaceChild(exception,nl.item(0));
        } else {
            statedata.appendChild(exception);
        }
        invalidateCache();

        //XMLCommon.debugXML(root);
    }

    /**
     * We need to synchronize that to avoid problems, but this should be fast anyway
     */
    public synchronized void setStateVar(String name, String value) {
        Element var= null;
        String xPathString = "//STATEDATA/VAR[@name='"+name+"']";
        //XMLCommon.getElementByAttribute(statedata,"VAR","name",name);
        try {
            var = (Element) getNodeXPath(xPathString);
        } catch (TransformerException te) {
            log.warn("'" + xPathString + "' query threw, "
                    + "but it's probably an XPath library bug.  "
                    + "Continuing as if node just not found.");
            /*
            For some reason, the XML lib is throwing when it should just
            return null, according to Xalan API spec.
            Parent node is present, but child is not...
            so it should return null.  JDom returns null.
            TODO:  Do some testing directly with the XML lib.
            log.error("Failed to get extract node for XPath '"
                    + xPathString + "'", te);
            XMLCommon.dumpXML(log, xPathString, root);
            */
        }
        if(var == null) {
            var=root.createElement("VAR");
            var.setAttribute("name",name);
            statedata.appendChild(var);
            invalidateCache();
        }
        if(!var.getAttribute("value").equals(value)) {
            var.setAttribute("value",value);
            invalidateCache();
        }
    }

    public Element createStateVar(String name, String value) {
        Element var=root.createElement("VAR");
        var.setAttribute("name",name);
        var.setAttribute("value",value);
        return var;
    }

    public void addStateVar(String name, String value) {
        Element var=root.createElement("VAR");
        var.setAttribute("name",name);
        statedata.appendChild(var);
        var.setAttribute("value",value);
        invalidateCache();
    }

    /**
     * We need to synchronize that because it can cause problems with multiple threads
     */
    public synchronized void removeAllStateVars(String name) {
        NodeList nl=getNodeListXPath("//STATEDATA/VAR");

        if(nl != null) {
            /* This suxx: NodeList Object is changed when removing children !!!
               I will store all nodes that should be deleted in a Vector and delete them afterwards */
            int length=nl.getLength();
            Vector<Element> v = new Vector<Element>(nl.getLength());
            for(int i=0;i<length;i++) {
                if(((Element)nl.item(i)).getAttribute("name").equals(name)) {
                    v.addElement((Element) nl.item(i));
                }
            }
            Enumeration enumVar=v.elements();
            while(enumVar.hasMoreElements()) {
                Node n=(Node)enumVar.nextElement();
                statedata.removeChild(n);
            }
        }
        invalidateCache();
    }


    public String getStateVar(String name) {
        Element var = null;
        String xPathString = "//STATEDATA/VAR[@name='"+name+"']";
        //XMLCommon.getElementByAttribute(statedata,"VAR","name",name);
        try {
            var = (Element) getNodeXPath(xPathString);
        } catch (TransformerException te) {
            log.error("Failed to get extract node for XPath '"
                    + xPathString + "'", te);
            XMLCommon.dumpXML(log, xPathString, root);
        }
        return (var == null) ? "" : var.getAttribute("value");
    }
}
