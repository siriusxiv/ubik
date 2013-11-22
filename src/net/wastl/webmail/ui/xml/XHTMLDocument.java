/*
 * @(#)$Id: XHTMLDocument.java 105 2008-10-29 06:22:32Z unsaved $
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


package net.wastl.webmail.ui.xml;

import java.io.StringWriter;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.wastl.webmail.exceptions.WebMailException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 * Constructs HTML-Documents using a Stylesheet and a XML Document.
 *
 * @author Sebastian Schaffert
 */
public class XHTMLDocument extends net.wastl.webmail.ui.html.HTMLDocument {
    private static Log log = LogFactory.getLog(XHTMLDocument.class);

    public XHTMLDocument(Document xml, String xsl) throws WebMailException {
        StringWriter writer = new StringWriter();

        long start_t=System.currentTimeMillis();
        try {
            DOMSource msg_xml=new DOMSource((Node)xml);
            StreamSource msg_xsl=new StreamSource("file://"+xsl);
            StreamResult msg_result=new StreamResult(writer);

            TransformerFactory factory = TransformerFactory.newInstance();

            Transformer processor = factory.newTransformer(msg_xsl);

            //processor.setDiagnosticsOutput(System.err);
            processor.transform(msg_xml,msg_result);
        } catch(Exception ex) {
            log.error("Error transforming XML with "+xsl+" to XHTML.");
            throw new WebMailException(ex.getMessage());
        }
        long end_t=System.currentTimeMillis();
        log.debug("Transformation XML --> XHTML took "+(end_t-start_t)+" ms.");

        content=writer.toString();
    }

    public XHTMLDocument(Document xml, Templates stylesheet) throws WebMailException {
        StringWriter writer = new StringWriter();

        long start_t=System.currentTimeMillis();
        try {
            DOMSource msg_xml=new DOMSource((Node)xml);
            StreamResult msg_result=new StreamResult(writer);

            Transformer processor = stylesheet.newTransformer();
            processor.transform(msg_xml,msg_result);
        } catch(Exception ex) {
            log.error("Error transforming XML to XHTML.", ex);
            throw new WebMailException("Error transforming XML to XHTML: "
                    + ex);
        }
        long end_t=System.currentTimeMillis();
        log.debug("Transformation (with precompiled stylesheet) XML --> XHTML took "+(end_t-start_t)+" ms.");

        content=writer.toString();
    }


    public String toString() {
        return content;
    }

    public int length() {
        return content.length();
    }
}
