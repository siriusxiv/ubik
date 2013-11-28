/*
 * @(#)$Id: Plugin.java 113 2008-10-29 23:41:26Z unsaved $
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

/**
 * This provides a generic interface for WebMail Plugins
 *
 * @author Sebastian Schaffert
*/
public interface Plugin {
    /**
     * Register this plugin with a WebMailServer
     * The plugin thus has access to most WebMail objects.
     */
    public void register(WebMailServer parent);

    /**
     * Return the name for this plugin.
     */
    public String getName();

    /**
     * Return a short description for this plugin to be shown in the
     * plugin list and perhaps in configuration
     */
    public String getDescription();

    /**
     * Get a version information for this plugin.
     * This is used for informational purposes only.
     */
    public String getVersion();

    /**
     * Return a stringlist (comma seperated) of features this plugin provides.
     * @see requires
     */
    public String provides();

    /**
     * Return a stringlist (comma seperated) of features this plugin requires.
     * @see provides
     */
    public String requires();
}
