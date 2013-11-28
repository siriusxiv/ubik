/*
 * @(#)$Id: StreamConnector.java 67 2008-10-26 03:35:18Z unsaved $
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

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Used to write to a OutputStream in a separate Thread to avoid blocking.
 *
 * @author Sebastian Schaffert
 */
public class StreamConnector extends Thread {
    private static Log threadLog = LogFactory.getLog("THREAD.StreamConnector");

    InputStream in;
    ByteStore b;
    int size;
    boolean ready=false;
    boolean verbose = false;

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public StreamConnector(InputStream sin, int size) {
        super("StreamConnector");
        in=sin;
        this.size=size;
        b=null;
        this.start();
    }

    public void run() {
        threadLog.info("Starting " + getName());
        try {
            b=ByteStore.getBinaryFromIS(in,size);
            ready=true;
        } finally {
            threadLog.info("Exiting " + getName());
        }
    }

    public ByteStore getResult() {
        while(!ready) {
            try {
                sleep(500);
                if (verbose) System.err.print(".");
            } catch(InterruptedException ex) {
            }
        }
        if (verbose) System.err.println();
        return b;
    }
}
