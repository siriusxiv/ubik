/*
 * @(#)$Id: DocumentNotFoundException.java 117 2008-10-30 06:32:49Z unsaved $
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


package net.wastl.webmail.exceptions;


/**
 * DocumentNotFoundException.java
 *
 * Created: Sun Feb  7 12:53:14 1999
 *
 * @author Sebastian Schaffert
 */
public class DocumentNotFoundException extends WebMailException {
    static final long serialVersionUID = 1601183573411460007L;

    public DocumentNotFoundException() {
        super();
    }

    public DocumentNotFoundException(String s) {
        super(s);
    }
}
