/*
 * @(#)$Id: ConnectionTimer.java 116 2008-10-30 06:12:51Z unsaved $
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

import java.util.Enumeration;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Sebastian Schaffert
 */
public class ConnectionTimer extends Thread {
    /* TODO:  One of these thread remains running after a webapp shutdown.
     * Fix that! */
    private static Log log = LogFactory.getLog(ConnectionTimer.class);
    private static Log threadLog = LogFactory.getLog("THREAD.ConnectionTimer");
    private Vector<TimeableConnection> connections;
    private static final long sleep_interval=1000;

    public ConnectionTimer() {
        super("ConnectionTimer");
        connections = new Vector<TimeableConnection>();
        this.start();
    }

    public void printStatus() {
        log.info("Vulture: "+connections.size()+" connections in queue");
    }

    public void addTimeableConnection(TimeableConnection c) {
        synchronized(connections) {
            connections.addElement(c);
        }
    }

    public void removeTimeableConnection(TimeableConnection c) {
        synchronized(connections) {
            connections.removeElement(c);
        }
    }

    public void removeAll() {
        Enumeration e;
        synchronized(connections) {
            e=connections.elements();
        }
        while(e.hasMoreElements()) {
            TimeableConnection t=(TimeableConnection)e.nextElement();
            t.timeoutOccured();
        }
    }

    public void run() {
        /** See TODO impl. note at top of this file. */
        Enumeration e;
        threadLog.info("Starting " + getName());
        try { while(true) {
            synchronized(connections) {
                e=connections.elements();
            }
            while(e.hasMoreElements()) {
                TimeableConnection t=(TimeableConnection)e.nextElement();
                if(System.currentTimeMillis() - t.getLastAccess() > t.getTimeout()) {
                    t.timeoutOccured();
                }
            }
            try { this.sleep(sleep_interval); } catch(InterruptedException ex) {
                log.error(ex);
            }
        } } finally { threadLog.info("Exiting " + getName()); }
    }
}
