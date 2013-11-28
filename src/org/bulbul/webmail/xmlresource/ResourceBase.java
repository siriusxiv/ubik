/*
 * @(#)$Id: ResourceBase.java 116 2008-10-30 06:12:51Z unsaved $
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


/**
 * @(#)ResourceBase.java  1.0 2001/09/02
 */
package org.bulbul.webmail.xmlresource;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.wastl.webmail.server.WebMailServer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A ResourceBundle implementation that uses a XML file to store the resources.
 * Modified from Sebastian Schaffert's
 * net.wastl.webmail.xml.XMLResourceBundle.java
 *
 * New scheme:
 * We separate locale resource to differenet files instead of putting all
 * different locale resources into single xml file, since the encoding
 * can't vary. (A single xml file can only use one encoding).
 *
 * Subclasses must override <code>getXmlResourceFilename</code> and
 * provide the filename which contains appropriate locale-specific resources.
 *
 * Note:
 * The resource files must resides in the directory that defined by
 * `webmail.template.path' property, hence <code>getXmlResourceFilename</code>
 * must returns only filename without pathname)
 *
 * @author        Steve Excellent Lee
 */
public abstract class ResourceBase extends ResourceBundle {
    private static Log log = LogFactory.getLog(ResourceBase.class);
    protected Document xmlRoot = null;

    protected Element elementBundle = null;     // The <BUNDLE> element of resource xml file
    protected Element elem_common = null;

    /**
     * Sole constructor.  (For invocation by subclass constructors, typically
     * implicit.)
     */
    public ResourceBase() {
    }


    public Enumeration<String> getKeys() {
        Hashtable<String, String> prop=new Hashtable<String, String>();

        if(elem_common != null) {
            getKeys(elem_common,prop);
        }
        if(elementBundle != null) {
            getKeys(elementBundle,prop);
        }
        return prop.keys();
    }

    protected Object handleGetObject(String key) throws MissingResourceException {
        String retval=null;

        // Lazily load the XML resource file
        if (xmlRoot == null) {
            loadXmlResourceFile();
        }

        if (elementBundle != null) {
            retval = getResult(elementBundle, key);
        }
        if ((retval == null) && (elem_common != null)) {
            retval = getResult(elem_common,key);
        }
        log.debug("XMLResourceBundle: "+key+" = "+retval);
        return retval;
    }

    /**
     * See class description.
     */
    abstract protected String getXmlResourceFilename();

    protected void loadXmlResourceFile() {
        try {
            DocumentBuilder parser=DocumentBuilderFactory.newInstance().newDocumentBuilder();
            log.info("file://" +
                               WebMailServer.getServer().getProperty("webmail.template.path") +
                               System.getProperty("file.separator") +
                               getXmlResourceFilename());
            xmlRoot = parser.parse("file://" +
                                   WebMailServer.getServer().getProperty("webmail.template.path") +
                                   System.getProperty("file.separator") +
                                   getXmlResourceFilename());


            NodeList nl = xmlRoot.getElementsByTagName("COMMON");
            if (nl.getLength() > 0) {
                elem_common=(Element)nl.item(0);
            }

            nl = xmlRoot.getElementsByTagName("LOCALE");
            if (nl.getLength() > 0) {
                elementBundle = (Element)nl.item(0);
            }
        } catch (IOException e) {
            log.error(e);
        } catch (SAXException e) {
            log.error(e);
        } catch (ParserConfigurationException e) {
            log.error(e);
        }  // We don't want to throw for these exceptions?
    }

    protected void getKeys(Element element, Hashtable<String, String> hash) {
        NodeList nl = element.getElementsByTagName("RESOURCE");
        for (int i=0; i < nl.getLength(); i++) {
            hash.put(((Element)nl.item(i)).getAttribute("name"), "");
        }
    }

    protected String getResult(Element element, String key) {
        NodeList nl = element.getElementsByTagName("RESOURCE");
        for(int i = 0; i < nl.getLength(); i++) {
            Element e = (Element)nl.item(i);
            if (e.getAttribute("name").equals(key)) {
                String s="";
                NodeList textl = e.getChildNodes();
                for (int j=0; j < textl.getLength(); j++) {
                    log.debug("XMLResourceBundle ("+key+"): Type "+textl.item(j).getNodeName());
                    if (textl.item(j).getNodeName().equals("#text") ||
                        textl.item(j).getNodeName().equals("#cdata-section")) {
                        s += textl.item(j).getNodeValue();
                    }
                }
                return s;
            }
        }
        return null;
    }
}
