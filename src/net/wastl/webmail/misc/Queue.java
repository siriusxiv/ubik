/*
 * @(#)$Id: Queue.java 116 2008-10-30 06:12:51Z unsaved $
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

import java.util.Vector;

/**
 * @author Sebastian Schaffert
 */
public class Queue  {
    Vector<Object> contents;

    public Queue() {
        contents = new Vector<Object>();
    }


    public void queue(Object o) {
        // Would be very intuitive if this method were named "push".
        contents.addElement(o);
    }

    public boolean isEmpty() {
        return contents.isEmpty();
    }

    public Object next() {
        // Would be very intuitive if this method were named "pop".
        // "next" gives no indication that caller is changing the set contents.
        Object o=contents.firstElement();
        contents.removeElementAt(0);
        return o;
    }
}
