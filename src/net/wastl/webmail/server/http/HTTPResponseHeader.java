/*
 * @(#)$Id: HTTPResponseHeader.java 116 2008-10-30 06:12:51Z unsaved $
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SimpleTimeZone;

import net.wastl.webmail.server.WebMailServer;

/**
 * HTTPHeader.java
 *
 * Created: Tue Feb  2 15:25:48 1999
 *
 * @author Sebastian Schaffert
 */
public class HTTPResponseHeader  {
    private String status;
    private int response_code;
    private String http_version="HTTP/1.1";

    private String response_line;

    private Map<String, String> headers;

    public HTTPResponseHeader(int response_code,String status) {
        headers = new Hashtable<String, String>();
        this.response_code=response_code;
        this.status=status;

        response_line=http_version+" "+response_code+" "+status;

        putHeader("Server","JWebMail/"+WebMailServer.VERSION);
        SimpleTimeZone tz=new SimpleTimeZone(0,"GMT");
        SimpleDateFormat df=new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", new Locale("en","UK"));
        df.setTimeZone(tz);
        String now=df.format(new Date());
        String now_plus_5=df.format(new Date(System.currentTimeMillis()+300000));
        putHeader("Date",now);
        putHeader("Expires",now_plus_5);
        putHeader("Pragma","no-cache");
        putHeader("Cache-Control","must-revalidate");
        putHeader("Allow","GET, POST");
    }

    public String getResponseLine() {
        return response_line;
    }

    public void putHeader(String key,String value) {
        headers.remove(key);
        headers.put(key,value);
    }

    public String getHeader(String t) {
        return (String)headers.get(t);
    }

    public void removeHeader(String key) {
        headers.remove(key);
    }

    public Set<String> getHeaderKeys() {
        return headers.keySet();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(response_line + "\r\n");
        for (Map.Entry<String, String> e : headers.entrySet())
            sb.append(e.getKey() + ": " + e.getValue() + "\r\n");
        return sb.toString() + "\r\n";
    }
}
