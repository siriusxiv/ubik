/*
 * @(#)$Id: HTTPRequestHeader.java 116 2008-10-30 06:12:51Z unsaved $
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


package net.wastl.webmail.server.http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Hashtable;

import net.wastl.webmail.misc.ByteStore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * HTTPHeader.java
 *
 * Created: Tue Feb  2 15:25:48 1999
 *
 * @author Sebastian Schaffert
 */
public class HTTPRequestHeader  {
    private static Log log = LogFactory.getLog(HTTPRequestHeader.class);

    private Hashtable<String, Object> content;

    private Hashtable<String, String> headers;


    public HTTPRequestHeader() {
        headers = new Hashtable<String, String>(10,.9f);
        content = new Hashtable<String, Object>(5,.9f);
    }

    public String getMethod() {
        return getHeader("Method");
    }

    public String getPath() {
        return getHeader("Path");
    }

    public String getVersion() {
        return getHeader("Version");
    }

    public void setPath(String s) {
        try {
            setHeader("PATH",URLDecoder.decode(s, "UTF-8"));
        } catch (UnsupportedEncodingException uee) {
            log.fatal("Unable to dedode UTF-8", uee);
            throw new RuntimeException("Unable to dedode UTF-8", uee);
        }
    }

    public void setMethod(String s) {
        setHeader("METHOD",s);
    }

    public void setVersion(String s) {
        setHeader("VERSION",s);
    }


    public void setHeader(String key, String value) {
        if(headers == null) {
            headers = new Hashtable<String, String>();
        }
        headers.put(key.toUpperCase(), value);
    }

    public String getHeader(String t) {
        return headers.get(t.toUpperCase());
    }

    public Hashtable getContent() {
        return content;
    }

    public Object getObjContent(String key) {
        if(content!=null) {
            return content.get(key.toUpperCase());
        } else {
            return null;
        }
    }

    public String getContent(String key) {
        if(content!=null) {
            Object o=content.get(key.toUpperCase());
            if(o == null) {
                return null;
            } else if(o instanceof String) {
                return (String)o;
            } else if(o instanceof ByteStore) {
                return new String(((ByteStore)o).getBytes());
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public boolean isContentSet(String key) {
        Object s=content.get(key.toUpperCase());
        return s != null && !(s instanceof String && ((String)s).trim().equals(""));
    }

    public void setContent(String key, Object value) {
        content.put(key.toUpperCase(),value);
    }

    public Enumeration getHeaderKeys() {
        return headers.keys();
    }

    public Enumeration getContentKeys() {
        return content.keys();
    }

    public String toString() {
        String s="Method: "+headers.get("METHOD")+", Path="+headers.get("PATH")+", HTTP-version: "+headers.get("VERSION")+"\n";
        s+="HTTP Headers:\n";
        Enumeration e=headers.keys();
        while(e.hasMoreElements()) {
            String h=(String)e.nextElement();
            s+="Header name: "+h+", value: "+headers.get(h)+"\n";
        }
        if(content != null) {
            s+="Form Content:\n";
            e=content.keys();
            while(e.hasMoreElements()) {
                String h=(String)e.nextElement();
                s+="Header name: "+h+", value: "+content.get(h)+"\n";
            }
        }
        return s;
    }
}
