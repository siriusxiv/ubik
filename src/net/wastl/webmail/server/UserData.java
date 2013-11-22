/*
 * @(#)$Id: UserData.java 113 2008-10-29 23:41:26Z unsaved $
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
import java.util.Locale;

import net.wastl.webmail.authenticators.SimpleAuthenticator;
import net.wastl.webmail.exceptions.InvalidPasswordException;
import net.wastl.webmail.exceptions.WebMailException;

/**
 * @author Sebastian Schaffert
 */
public interface UserData {
    public void login();
    public void logout();

    /**
     * Add a mailbox to this user's data.
     * The new mailbox will be stored in the user configuration and used
     * for connection.
     *
     * @param name Name used to represent the mailhost in WebMail
     * @param host This will usually be the URL to the mailhost, constructed
     * from the protocol and the hostname
     * @param login The login name used for the host
     * @param password The corresponding password
     */
    public void addMailHost(String name, String host, String login,
            String password, String imapBasedir);

    /**
     * Remove the mailhost with the given name.
     *
     * @param name The WebMail internal name of this mailbox
     */
    public void removeMailHost(String name);

    /**
     * Return the mailhost data for the mailbox with the given name.
     *
     * @param name The WebMail internal name of this mailbox
     * @returns the MailHostData representation of the mailbox
     * @see MailHostData
     */
    public MailHostData getMailHost(String name);

    /**
     * Return the list of mailbox names of the mailboxes this user has.
     *
     * @returns list of mailbox names for this user
     */
    public Enumeration mailHosts();

    /**
     * Return the value that the user configured as maximum number of
     * messages to display on one page of the message list.
     */
    public int getMaxShowMessages();

    /**
     * Set the number of maximum messages to display on one page of the
     * messagelist.
     *
     * @param i maximum number of messages on one page
     */
    public void setMaxShowMessages(int i);

    /**
     * Break lines at this maximum length.Only applicable if the user configured
     * to break lines.
     *
     * @see wantsBreakLines()
     */
    public int getMaxLineLength();

    /**
     * Set the maximum column count for messages. Only applicable if the user
     * configured to break lines.
     *
     * @param i maximum column count
     * @see wantsBreakLines()
     */
    public void setMaxLineLength(int i);

    /**
     * Check whether the user wants to force line breaks.
     * If this is set to true, lines in composed and shown messages are
     * smartly broken at the configured positions.
     *
     * @see getMaxLineLength()
     */
    public boolean wantsBreakLines();

    /**
     * Set whether the user wants to force line breaks or not.
     *
     * @param b true if the user wants linebreaks
     */
    public void setBreakLines(boolean b);

    /**
     * Returns the username concatenated with '@' and the virtual domain.
     * As of WebMail 0.7.0 this is different from the username, because it
     * consists of the username and the domain.
     *
     * @see getUserName()
     */
    public String getLogin();

    /**
     * Return the full name (christian and last name) of the user.
     */
    public String getFullName();

    /**
     * Set the full name (christian and last name) of the user.
     *
     * @param s the String containing the full name of the user
     */
    public void setFullName(String s);

    /**
     * Return the signature of this user that should be appended to
     * composed messages.
     */
    public String getSignature();

    /**
     * Set the signature that should be appended to composed messages.
     *
     * @param s String containing the signature
     */
    public void setSignature(String s);

    /**
     * Get the default email address of the user.
     * This will be used for outgoing mail.
     */
    public String getDefaultEmail() throws WebMailException;

    public void removeEmail(String s) throws WebMailException;

    /**
     * Add the email address of the user.
     * This may be used for outgoing mail.
     *
     * @param s String containing the Email address
     */
    public void addEmail(String s);

    /**
     * Return the locale that this user configured.
     *
     * @returns a Locale object constructed from the locale string
     */
    public Locale getPreferredLocale();

    /**
     * Set the preferred locale for this user
     *
     * @param newloc name of the new locale (e.g. de_DE)
     */
    public void setPreferredLocale(String newloc);

    /**
     * Return the theme that the user configured.
     * (Reserved for future use, just returns "default").
     */
    public String getTheme();

    /**
     * Change the theme for this user.
     *
     * @param theme name of the theme
     */
    public void setTheme(String theme);

    /**
     * Get a localized string containing the date when the user first logged in.
     */
    public String getFirstLogin();

    /**
     * Get a localized string containing the date when the user logged in
     * the last time.
     */
    public String getLastLogin();

    /**
     * Get a string containing the total count of logins for the user.
     */
    public String getLoginCount();

    /**
     * Check the given password against the user's password.
     *
     * @param s String with the password that is to be checked
     * @see SimpleAuthenticator
     */
    public boolean checkPassword(String s);

    /**
     * Change the users password.
     *
     * @param newpasswd new password
     * @param verify new password again to verify that it was not misspelled
     */
    public void setPassword(String newpasswd, String verify) throws InvalidPasswordException;

    /**
     * Set the password data for a user.  This can contain any string which
     * will then become password data for a user.  It can be encoded
     * however the authenticator chooses.
     * Note that when switching authenticators, this password data will
     * most likely not be valid anymore, and the user will be locked out
     * of their account.
     *
     * @param data The new data to use
     */
    public void setPasswordData(String data);

    /**
     * Get the password data for this user.
     *
     * @return The saved password data
     */
    public String getPasswordData();

    /**
     * Check whether the user wants attached images to be shown inlined in
     * the messages or not.
     *
     * @return true, if the user wants images to be shown inline
     */
    public boolean wantsShowImages();

    /**
     * Set whether the user wants attached images to be shown inlined in the messages.
     *
     * @param b true if images are to be shown inline
     */
    public void setShowImages(boolean b);


    /**
     * Check whether the user wants some graphical enhancements (image smileys, etc).
     *
     * @returns true if the user wants graphical enhancements.
     */
    public boolean wantsShowFancy();

    /**
     * Set whether the user wants some graphical enhancements (image smileys, etc).
     *
     * @param b true if the user wants graphical enhancements.
     */
    public void setShowFancy(boolean b);

    /**
     * Check whether write mode is enabled on mailboxes.
     * This influences many things like setting message flags, copying/moving messages, deleting
     * messages and the like.
     *
     * @see net.wastl.webmail.server.WebMailSession.getMessage()
     * @see net.wastl.webmail.server.WebMailSession.copyMoveMessage()
     * @see net.wastl.webmail.server.WebMailSession.setFlags()
     */
    public boolean wantsSetFlags();

    /**
     * Enable/disable write mode on mailboxes.
     * This influences many things like setting message flags, copying/moving messages, deleting
     * messages and the like.
     *
     * @param b true: enable write mode
     * @see net.wastl.webmail.server.WebMailSession.getMessage()
     * @see net.wastl.webmail.server.WebMailSession.copyMoveMessage()
     * @see net.wastl.webmail.server.WebMailSession.setFlags()
     */
    public void setSetFlags(boolean b);

    /**
     * Set whether the user wants to save sent messages or not. The folder where sent messages
     * will be stored has to be configured also.
     *
     * @param b true if the user wants to save sent messages
     * @see getSentFolder()
     * @see setSentFolder()
     */
    public void setSaveSent(boolean b);

    /**
     * Check whether the user wants to save sent messages or not. The folder where sent messages
     * will be stored has to be configured also.
     *
     * @returns true if the user wants to save sent messages
     * @see getSentFolder()
     * @see setSentFolder()
     */
    public boolean wantsSaveSent();

    /**
     * Return the id of the folder that should be used to store sent messages.
     * This is only applicable if the user enabled the storage of sent messages.
     *
     * @see net.wastl.webmail.server.WebMailSession.generateFolderHash()
     * @see wantsSaveSent()
     * @see setSaveSent()
     */
    public String getSentFolder();
    /**
     * Set the id of the folder that should be used to store sent messages.
     * This is only applicable if the user enabled the storage of sent messages.
     *
     * @see net.wastl.webmail.server.WebMailSession.generateFolderHash()
     * @see wantsSaveSent()
     * @see setSaveSent()
     */
    public void setSentFolder(String s);

    /**
     * Return the name of the virtual domain this user is in.
     */
    public String getDomain();

    /**
     * Set the name of the virtual domain this user is in.
     *
     * @param s name of the virtual domain
     */
    public void setDomain(String s);

    /**
     * Return the username without the domain (in contrast to getLogin()).
     * @see getLogin()
     */
    public String getUserName();
}
