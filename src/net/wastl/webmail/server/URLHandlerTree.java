/*
 * @(#)$Id: URLHandlerTree.java 116 2008-10-30 06:12:51Z unsaved $
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

import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * A tree structure to improve (speed up) access to URLs
 *
 * @author Sebastian Schaffert
 */
public class URLHandlerTree implements URLHandlerTreeNode {
    URLHandler handler;

    String url;

    Hashtable<String, URLHandlerTree> nodes;

    StringTokenizer t;

    public URLHandlerTree(String url) {
        nodes = new Hashtable<String, URLHandlerTree>();
        this.url=url;
   }

    public String getURL() {
        return url;
    }

    public void addHandler(String url, URLHandler h) {
        if(url.equals("/") || url.equals("") || url==null) {
            handler=h;
        } else {
            t=new StringTokenizer(url,"/");
            String subtree_name=t.nextToken();
            URLHandlerTree subtree= nodes.get(subtree_name);
            if(subtree == null) {
                if(this.url.endsWith("/")) {
                    subtree=new URLHandlerTree(this.url+subtree_name);
                } else {
                    subtree=new URLHandlerTree(this.url+"/"+subtree_name);
                }
                nodes.put(subtree_name,subtree);
            }
            subtree.addHandler(url.substring(subtree_name.length()+1,url.length()),h);
        }
    }

    public URLHandler getHandler(String url) {
        if(url.equals("/") || url.equals("") || url==null) {
            /* We are the handler */
            return handler;
        } else {
            t=new StringTokenizer(url,"/");
            String subtree_name=t.nextToken();
            URLHandlerTree subtree = nodes.get(subtree_name);
            if(subtree == null) {
                /* If there is no subtree, we are the handler! */
                return handler;
            } else {
                /* Else there is a subtree*/
                URLHandler uh=subtree.getHandler(url.substring(subtree_name.length()+1,url.length()));
                if(uh != null) {
                    /* It has a handler */
                    return uh;
                } else {
                    /* It has no handler, we are handler */
                    return handler;
                }
            }
        }
    }

    public String toString() {
        return nodes.toString();
    }
}
