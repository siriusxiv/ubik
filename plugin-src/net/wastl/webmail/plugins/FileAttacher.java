/*
 * @(#)$Id: FileAttacher.java 101 2008-10-29 01:15:14Z unsaved $
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


package net.wastl.webmail.plugins;

import net.wastl.webmail.exceptions.DocumentNotFoundException;
import net.wastl.webmail.exceptions.WebMailException;
import net.wastl.webmail.misc.ByteStore;
import net.wastl.webmail.server.HTTPSession;
import net.wastl.webmail.server.Plugin;
import net.wastl.webmail.server.Storage;
import net.wastl.webmail.server.URLHandler;
import net.wastl.webmail.server.UserData;
import net.wastl.webmail.server.WebMailServer;
import net.wastl.webmail.server.WebMailSession;
import net.wastl.webmail.server.http.HTTPRequestHeader;
import net.wastl.webmail.ui.html.HTMLDocument;
import net.wastl.webmail.ui.xml.XHTMLDocument;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This plugin shows the Form for attaching files to a message as well as does
 * the actual attaching to a WebMailSession
 *
 * provides: attach
 * requires: composer
 *
 * @author Sebastian Schaffert
 */
public class FileAttacher implements URLHandler, Plugin {
    private static Log log = LogFactory.getLog(FileAttacher.class);
    public static final String VERSION="1.00";
    public static final String URL="/compose/attach";

    Storage store;

    public FileAttacher() {
    }

    public void register(WebMailServer parent) {
        parent.getURLHandler().registerHandler(URL,this);
        this.store=parent.getStorage();
        parent.getConfigScheme().configRegisterStringKey("MAX ATTACH SIZE","1000000","Maximum size of attachments in bytes");
    }

    public String getName() {
        return "FileAttacher";
    }

    public String getDescription() {
        return "This URL-Handler handles file attachments for the Composer.";
    }

    public String getVersion() {
        return VERSION;
    }

    public String getURL() {
        return URL;
    }

    public HTMLDocument handleURL(String suburl, HTTPSession sess, HTTPRequestHeader head) throws WebMailException {
        if(sess == null) {
            throw new WebMailException("No session was given. If you feel this is incorrect, please contact your system administrator");
        }
        WebMailSession session=(WebMailSession)sess;
        UserData user=session.getUser();
        if(head.isContentSet("ADD")) {
            try {
                /* Read the file from the HTTP Header and store it in the user's session */
                ByteStore bs=(ByteStore)head.getObjContent("FILE");
                String description="";
                if(head.isContentSet("DESCRIPTION")) {
                    description=new String(((ByteStore)head.getObjContent("DESCRIPTION")).getBytes());
                }
                //log.debug("Description: "+description);
                /**
                 * It seems that IE will use its browser encoding setting to
                 * encode the file name that sent to us. Hence we have to
                 * transcode this carefully.
                 *
                 * Since we set browser's encoding to UTF-8, the attachment
                 * fliename, ie, p.getFileName(), should be UTF-8 encoded.
                 * However the filename retrived from JavaMail MimeBodyPart
                 * is ISO8859_1 encoded, we have to decode its raw bytes and
                 * construct a new string with UTF-8 encoding.
                 *
                 * But where should we write this code?
                 * WebMailSession.java, FileAttacher.java, or HTTPRequestHeader.java?
                 *
                 * I guess the transocde operation should be done here. We retain the
                 * original bytes in HTTP header processing classes.
                 *
                 * Note that the after we called bs.setName() to set name to fileName,
                 * the client browser will display the file name correctly. However,
                 * to safely transfer mail, the file name must be encoded -- eg, by
                 * MimeUtility.encodeText() which is also adopted by M$-OutLook.
                 * We delay such operation until the mail is being sent, that is,
                 * SendMessage.java line #390.
                 */
                String fileName = bs.getName();

                // Transcode file name
                if (!((fileName == null) || fileName.equals(""))) {
                        int offset = fileName.lastIndexOf("\\");                // This is no effect. It seems that MimeBodyPart.getFileName() filters '\' character.
                        fileName = fileName.substring(offset + 1);
                        fileName = new String(fileName.getBytes("ISO8859_1"), "UTF-8");
                        bs.setName(fileName);
                }

                // Transcode decription
                if ((description != null) && (!description.equals("")))
                        description = new String(description.getBytes("ISO8859_1"), "UTF-8");
                if(bs!=null && bs.getSize() >0 ) {
                    session.addWorkAttachment(bs.getName(),bs,description);
                }
            } catch(Exception e) {
                log.error("Could not attach file", e);
                throw new DocumentNotFoundException("Could not attach file. (Reason: "+e.getMessage()+")");
            }
        } else if(head.isContentSet("DELETE") && head.isContentSet("ATTACHMENTS")) {
                try {
                        /**
                         * Since attachmentName comes from HTTPRequestHeader, we have to
                         * transcode it.
                         */
                    // log.debug("Removing "+head.getContent("ATTACHMENTS"));
                    // session.removeWorkAttachment(head.getContent("ATTACHMENTS"));
                        String attachmentName = head.getContent("ATTACHMENTS");
                        attachmentName = new String(attachmentName.getBytes("ISO8859_1"), "UTF-8");
                    log.info("Removing " + attachmentName);
                    session.removeWorkAttachment(attachmentName);
                } catch (Exception e) {
                    log.error("Could not remove attachment", e);
                    throw new DocumentNotFoundException("Could not remove attachment. (Reason: "+e.getMessage()+")");
                }
        }

        return new XHTMLDocument(session.getModel(),
                                 store.getStylesheet("compose_attach.xsl",
                                                     user.getPreferredLocale(),user.getTheme()));
    }

    public String provides() {
        return "attach";
    }

    public String requires() {
        return "composer";
    }
}
