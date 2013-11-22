/*
 * @(#)$Id: JavaScriptCleaner.java 38 2008-10-24 19:23:35Z unsaved $
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


package net.wastl.webmail.misc;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLFormElement;
import org.w3c.dom.html.HTMLImageElement;
import org.w3c.dom.html.HTMLScriptElement;

/**
 * JavaScriptCleaner.java
 *
 * This class removes hopefully all of the possible malicious code from HTML messages
 * like <SCRIPT> tags, javascript: hrefs and onMouseOver, ...;
 *
 * Furthermore, we should consider removing all IMG tags as they might be used to call CGIs
 *
 * Created: Mon Jan  1 15:20:54 2001
 *
 * @author Sebastian Schaffert
 */
public class JavaScriptCleaner  {
    Document d;

    public JavaScriptCleaner(Document d) {
        this.d=d;
        //walkTree(d.getDocumentElement());
        walkTree(d);
    }

    protected void walkTree(Node node) {
        /* First we check for element types that shouldn't be sent to the user.
           For that, we add an attribute "malicious" that can be handled by the XSLT
           stylesheets that display the message.
         */
        if(node instanceof HTMLScriptElement) {
            ((Element)node).setAttribute("malicious","Marked malicious because of potential JavaScript abuse");
        }

        if(node instanceof HTMLImageElement) {
            ((Element)node).setAttribute("malicious","Marked malicious because of potential Image/CGI abuse");
        }

        /* What we also really don't like in HTML messages are FORMs! */

        if(node instanceof HTMLFormElement) {
            ((Element)node).setAttribute("malicious","Marked malicious because of potential Form abuse");
        }

        /* Now we search the attribute list for attributes that may potentially be used maliciously.
           These will be:
           - href: check for a String containing "javascript"
           - onXXX events: if they exist, the link will be marked "malicious".
         */
//      String javascript_href="javascript";
//      NamedNodeMap map=node.getAttributes();
//      for(int i=0;i<map.getLength();i++) {
//          Attr a=(Attr)map.item(i);
//          /* First case: look for hrefs containing "javascript" */
//          if(a.getName().toUpperCase().equals("HREF")) {
//              for(int j=0;j<a.getValue().length()-javascript_href.length() ;j++) {
//                  if(a.getValue().regionMatches(true,j,javascript_href,0,javascript_href.length())) {
//                      ((Element)node).setAttribute("malicious","Marked malicious because of potential JavaScript abuse (HREF attribute contains javascript code)");
//                      break;
//                  }
//              }
//              /* All elements containing "onXXX" tags get the malicious attribute immediately */
//          } else if(a.getName().toUpperCase().startsWith("ON")) {
//                      ((Element)node).setAttribute("malicious","Marked malicious because of potential JavaScript abuse (element contains script events)");
//          }
//      }

        /* Do that recursively */
        if(node.hasChildNodes()) {
            NodeList nl=node.getChildNodes();
            for(int i=0;i<nl.getLength();i++) {
                walkTree(nl.item(i));
            }
        }
    }
}
