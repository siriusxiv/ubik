/*
 * @(#)$Id: WebMailVirtualDomain.java 84 2008-10-27 01:29:13Z unsaved $
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

/**
 * Represents a virtual domain in WebMail.
 * A virtual domain in WebMail allows the following things
 * - users can belong to a certain domain
 * - each domain has it's own default host, authentication host, and default email suffix
 * - each domain can have specific security features, i.e. IMAP/POP hosts users of that domain
 *   are allowed to connect to.
 *
 * @author Sebastian Schaffert
 */
public interface WebMailVirtualDomain  {
    /**
     * Return the name of this domain. This will be appended to a new users email address
     * and will be used in the login screen
     */
    public String getDomainName();

    public void setDomainName(String name) throws Exception;

    /**
     * This returns the name of the default server that will be used.
     * The default server is where a user gets his first folder (the one named "Default").
     */
    public String getDefaultServer();

    public void setDefaultServer(String name);

    /**
     * If the authentication type for this domain is IMAP or POP, this host will be used
     * to authenticate users.
     */
    public String getAuthenticationHost();

    public void setAuthenticationHost(String name);

    /**
     * Check if a hostname a user tried to connect to is within the allowed range of
     * hosts. Depending on implementation, this could simply check the name or do an
     * DNS lookup to check for IP ranges.
     * The default behaviour should be to only allow connections to the default host and
     * reject all others. This behaviour should be configurable by the administrator, however.
     */
    public boolean isAllowedHost(String host);

    /**
     * Set the hosts a user may connect to if host restriction is enabled.
     * Excpects a comma-separated list of hostnames.
     * The default host will be added to this list in any case
     */
    public void setAllowedHosts(String hosts);

    public Enumeration getAllowedHosts();

    /**
     * Enable/Disable restriction on the hosts that a user may connect to.
     * If "disabled", a user may connect to any host on the internet
     * If "enabled", a user may only connect to hosts in the configured list
     * @see isAllowedHost
     */
    public void setHostsRestricted(boolean b);

    public boolean getHostsRestricted();

    public String getImapBasedir();
}
