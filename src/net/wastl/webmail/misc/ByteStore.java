/*
 * @(#)$Id: ByteStore.java 117 2008-10-30 06:32:49Z unsaved $
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

import java.io.EOFException;
import java.io.InputStream;
import java.io.Serializable;

import net.wastl.webmail.server.WebMailServer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * ByteStore.java
 *
 * Created: Sun Sep 19 17:22:13 1999
 *
 * @author Sebastian Schaffert
 */
public class ByteStore implements Serializable {
    static final long serialVersionUID = 4689743335269719513L;
    private static Log log = LogFactory.getLog(ByteStore.class);

    byte[] bytes;

    String content_type=null;
    String content_encoding=null;
    String name;
    String description="";

    public ByteStore(byte[] b) {
        bytes=b;
    }

    public void setDescription(String s) {
        description=s;
    }

    public String getDescription() {
        return description;
    }

    public void setContentType(String s) {
        content_type=s;
    }

    public String getContentType() {
        if(content_type != null) {
            return content_type;
        } else {
            content_type=WebMailServer.getStorage().getMimeType(name);
            return content_type;
        }
    }

    public void setContentEncoding(String s) {
        content_encoding=s;
    }

    public String getContentEncoding() {
        return content_encoding;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setName(String s) {
        name=s;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return bytes.length;
    }


    /**
     * Create a ByteStore out of an InputStream
     */
    public static ByteStore getBinaryFromIS(InputStream in, int nr_bytes_to_read) {
        byte[] s=new byte[nr_bytes_to_read+100];
        int count=0;
        int lastread=0;
        // log.debug("Reading ... ");
        if(in != null) {
            synchronized(in) {
                while(count < s.length) {
                    try {
                        lastread=in.read(s,count,nr_bytes_to_read-count);
                    } catch(EOFException ex) {
                        log.error(ex);
                        lastread=0;
                    } catch(Exception z) {
                        log.error(z.getMessage());
                        lastread=0;
                    }
                    count+=lastread;
                    // log.debug(lastread+" ");
                    if(lastread < 1) break;
                }
            }
            byte[] s2=new byte[count+1];
            for(int i=0; i<count+1;i++) {
                s2[i]=s[i];
            }
            // log.debug("new byte-array, size "+s2.length);
            ByteStore d=new ByteStore(s2);
            return d;
        } else {
            return null;
        }
    }
}
