/*
 * @(#)$Id: XMLMessagePart.java 116 2008-10-30 06:12:51Z unsaved $
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

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A message part object for an XML message
 */
public class XMLMessagePart  {
    private static Log log = LogFactory.getLog(XMLMessagePart.class);
    protected Document root;
    protected Element part;

    /**
     * Create a new part for the given root document.
     * Creates the necessary Element.
     */
    public XMLMessagePart(Document root) {
        this.part=root.createElement("PART");
        this.root=root;
    }

    /**
     * Return a new part for a given part element
     */
    public XMLMessagePart(Element part) {
        this.part=part;
        this.root=part.getOwnerDocument();
    }

    public Element getPartElement() {
        return part;
    }

    public void setAttribute(String key, String value) {
        part.setAttribute(key,value);
    }

    public String getAttribute(String key) {
        return part.getAttribute(key);
    }

    public void quoteContent() {
        NodeList nl=part.getChildNodes();
        StringBuilder text=new StringBuilder();
        for(int i=0;i<nl.getLength();i++) {
            Element elem=(Element)nl.item(i);
            if(elem.getNodeName().equals("CONTENT")) {
                String value=XMLCommon.getElementTextValue(elem);
                StringTokenizer tok=new StringTokenizer(value,"\n");
                while(tok.hasMoreTokens()) {
                    text.append("> ").append(tok.nextToken()).append("\n");
                }
            }
        }
        removeAllContent();

        addContent(text.toString(),0);
    }

    /**
     * This method is designed for content that already is in DOM format, like HTML
     * messages.
     */
    public void addContent(Document content) {
        Element content_elem=root.createElement("CONTENT");
        content_elem.setAttribute("quotelevel","0");

        /* Find all <BODY> elements and add the child nodes to the content */
        for(int count=0; count < 2; count++) {
            NodeList nl=content.getDocumentElement().getElementsByTagName(count==0?"BODY":"body");
            log.debug("While parsing HTML content: Found "+nl.getLength()
                    +" body elements");
            for(int i=0; i<nl.getLength();i++) {
                NodeList nl2=nl.item(i).getChildNodes();
                log.debug("While parsing HTML content: Found "
                        +nl2.getLength()+" child elements");
                for(int j=0;j<nl2.getLength();j++) {
                    log.debug("Element: "+j);
                    content_elem.appendChild(XMLCommon.importNode(root,nl2.item(j),true));
                }
            }
        }


        part.appendChild(content_elem);

        //XMLCommon.debugXML(root);
    }

    public void addContent(String content, int quotelevel) {
        Element content_elem=root.createElement("CONTENT");
        content_elem.setAttribute("quotelevel",quotelevel+"");
        XMLCommon.setElementTextValue(content_elem,content,true);
        part.appendChild(content_elem);
    }

    public void insertContent(String content, int quotelevel) {
        Element content_elem=root.createElement("CONTENT");
        content_elem.setAttribute("quotelevel",quotelevel+"");
        XMLCommon.setElementTextValue(content_elem,content,true);
        Node first=part.getFirstChild();
        part.insertBefore(content_elem,first);
    }


    public void addJavaScript(String content) {
        Element javascript_elem=root.createElement("JAVASCRIPT");
        XMLCommon.setElementTextValue(javascript_elem,content,true);
        part.appendChild(javascript_elem);
    }

    public void removeAllContent() {
        XMLCommon.genericRemoveAll(part,"CONTENT");
    }

    public XMLMessagePart createPart(String type) {
        XMLMessagePart newpart=new XMLMessagePart(root);
        newpart.setAttribute("type",type);
        appendPart(newpart);
        return newpart;
    }

    public void insertPart(XMLMessagePart childpart) {
        Node first=part.getFirstChild();
        part.insertBefore(childpart.getPartElement(),first);
    }

    public void appendPart(XMLMessagePart childpart) {
        part.appendChild(childpart.getPartElement());
    }

    public Enumeration<XMLMessagePart> getParts() {
        // Sucking NodeList needs a Vector to store Elements that will be removed!
        Vector<XMLMessagePart> v = new Vector<XMLMessagePart>();
        NodeList parts=part.getChildNodes();
        for(int j=0;j<parts.getLength();j++) {
            Element elem=(Element)parts.item(j);
            if(elem.getTagName().equals("PART"))
                v.addElement(new XMLMessagePart(elem));
        }
        return v.elements();
    }

    public void removePart(XMLMessagePart childpart) {
        part.removeChild(childpart.getPartElement());
    }

    public void removeAllParts() {
        XMLCommon.genericRemoveAll(part,"PART");
    }
}
