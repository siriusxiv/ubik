/*
 * @(#)$Id: OTPCacheNode.java 38 2008-10-24 19:23:35Z unsaved $
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


/** OTPCacheNode is a class that exists exclusivly for OTPAuthenticator.
 * It was placed in this package because including it in either in the
 * unamed package (as OTPAuthenticator is) or as a private memeber class of
 * OTPAuthenticator means that it would need to be placed in the webmail
 * authenticators directory, and that just creates problems.
 *
 * @author Devin Kowatch
 * @see OTPAuthenticator
 */
package org.webengruven.webmail.auth;
import org.webengruven.javaOTP.OTPState;

public class OTPCacheNode {
    public OTPState active_st;
    public OTPState new_st;

    public OTPCacheNode() {
        active_st = new_st = null;
    }

    public OTPCacheNode(OTPState act) {
        active_st = act;
        new_st = null;
    }

    public OTPCacheNode(OTPState act, OTPState nw) {
        active_st = act;
        new_st = nw;
    }
};
