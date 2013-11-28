/*
 * @(#)$Id: AttributedExpireableCache.java 116 2008-10-30 06:12:51Z unsaved $
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

import java.util.Hashtable;

/**
 * AttributedExpireableCache.java
 *
 * Created: Tue Apr 25 14:57:22 2000
 *
 * @author Sebastian Schaffert
 */
public class AttributedExpireableCache extends ExpireableCache {
    protected Hashtable<Object, Object> attributes;

    public AttributedExpireableCache(int capacity, float expire_factor) {
        super(capacity,expire_factor);
        setName("AttributedExpireableCache");
        attributes = new Hashtable<Object, Object>(capacity);
    }

    public AttributedExpireableCache(int capacity) {
        super(capacity);
        setName("AttributedExpireableCache");
        attributes = new Hashtable<Object, Object>(capacity);
    }

    public synchronized void put(Object id, Object object, Object attribs) {
        attributes.put(id,attribs);
        super.put(id,object);
    }

    public Object getAttributes(Object key) {
        return attributes.get(key);
    }

    public synchronized void remove(Object key) {
        attributes.remove(key);
        super.remove(key);
    }
}
