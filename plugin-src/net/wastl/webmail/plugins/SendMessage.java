/*
 * @(#)$Id: SendMessage.java 116 2008-10-30 06:12:51Z unsaved $
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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletException;

import net.wastl.webmail.config.ConfigurationListener;
import net.wastl.webmail.exceptions.DocumentNotFoundException;
import net.wastl.webmail.exceptions.WebMailException;
import net.wastl.webmail.misc.ByteStore;
import net.wastl.webmail.misc.StreamConnector;
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
import org.bulbul.webmail.util.TranscodeUtil;

/**
 * Send a message and show a result page.
 *
 * provides: message send
 * requires: composer
 *
 * @author Sebastian Schaffert
 */
public class SendMessage implements Plugin, URLHandler, ConfigurationListener {
    private static Log log = LogFactory.getLog(SendMessage.class);

    public static final String VERSION="1.8";
    public static final String URL="/send";

    Storage store;

    WebMailServer parent;

    Session mailsession;

    public SendMessage() {
    }

    public void register(WebMailServer parent) {
        parent.getURLHandler().registerHandler(URL,this);
        parent.getConfigScheme().configRegisterStringKey(this,"SMTP HOST","localhost","Host used to send messages via SMTP. Should be localhost or your SMTP smarthost");
        parent.getConfigScheme().configRegisterYesNoKey(this,"ADVERTISEMENT ATTACH", "Attach advertisement from ADVERTISEMENT SIGNATURE PATH to each outgoing message");
        parent.getConfigScheme().setDefaultValue("ADVERTISEMENT ATTACH","NO");
        parent.getConfigScheme().configRegisterStringKey(this,"ADVERTISEMENT SIGNATURE PATH","advertisement.sig","Path to advertisement to attach to all outgoing messages (either absolute or relative to data directory)");
        this.store=parent.getStorage();
        this.parent=parent;

        init();
    }

    protected void init() {
        Properties props=new Properties();
        props.put("mail.host",store.getConfig("SMTP HOST"));
        props.put("mail.smtp.host",store.getConfig("SMTP HOST"));
        mailsession=Session.getInstance(props,null);
    }

    public String getName() {
        return "SendMessage";
    }

    public String getDescription() {
        return "This URL-Handler sends a submitted message.";
    }

    public String getVersion() {
        return VERSION;
    }

    public String getURL() {
        return URL;
    }

    public void notifyConfigurationChange(String key) {
        init();
    }

    public HTMLDocument handleURL(String suburl, HTTPSession sess1, HTTPRequestHeader head) throws WebMailException, ServletException {
        if(sess1 == null) {
            throw new WebMailException("No session was given. If you feel this is incorrect, please contact your system administrator");
        }
        WebMailSession session=(WebMailSession)sess1;
        UserData user=session.getUser();
        HTMLDocument content;

        Locale locale = user.getPreferredLocale();

        /* Save message in case there is an error */
        session.storeMessage(head);

        if(head.isContentSet("SEND")) {
            /* The form was submitted, now we will send it ... */
            try {
                MimeMessage msg=new MimeMessage(mailsession);

                Address from[]=new Address[1];
                try {
                    /**
                     * Why we need
                     * org.bulbul.util.TranscodeUtil.transcodeThenEncodeByLocale()?
                     *
                     * Because we specify client browser's encoding to UTF-8, IE seems
                     * to send all data encoded in UTF-8. We have to transcode all byte
                     * sequences we received to UTF-8, and next we encode those strings
                     * using MimeUtility.encodeText() depending on user's locale. Since
                     * MimeUtility.encodeText() is used to convert the strings into its
                     * transmission format, finally we can use the strings in the
                     * outgoing e-mail which relies on receiver's email agent to decode
                     * the strings.
                     *
                     * As described in JavaMail document, MimeUtility.encodeText() conforms
                     * to RFC2047 and as a result, we'll get strings like "=?Big5?B......".
                     */
                    /**
                     * Since data in session.getUser() is read from file, the encoding
                     * should be default encoding.
                     */
                    // from[0]=new InternetAddress(MimeUtility.encodeText(session.getUser().getEmail()),
                    //                  MimeUtility.encodeText(session.getUser().getFullName()));
                    from[0]=
                        new InternetAddress(
                                            TranscodeUtil.transcodeThenEncodeByLocale(head.getContent("FROM"), null, locale),
                                            TranscodeUtil.transcodeThenEncodeByLocale(session.getUser().getFullName(), null, locale)
                                            );
                } catch(UnsupportedEncodingException e) {
                    log.warn(
                              "Unsupported Encoding while trying to send message: "+e.getMessage());
                    from[0]=
                        new InternetAddress(head.getContent("FROM"),
                                            session.getUser().getFullName());
                }

                StringTokenizer t;
                try {
                    /**
                     * Since data in session.getUser() is read from file, the encoding
                     * should be default encoding.
                     */
                    // t=new StringTokenizer(MimeUtility.encodeText(head.getContent("TO")).trim(),",");
                    t = new StringTokenizer(TranscodeUtil.transcodeThenEncodeByLocale(head.getContent("TO"), null, locale).trim(), ",");
                } catch(UnsupportedEncodingException e) {
                    log.warn(
                              "Unsupported Encoding while trying to send message: "+e.getMessage());
                    t=new StringTokenizer(head.getContent("TO").trim(),",;");
                }

                /* Check To: field, when empty, throw an exception */
                if(t.countTokens()<1) {
                    throw new MessagingException("The recipient field must not be empty!");
                }
                Address to[]=new Address[t.countTokens()];
                int i=0;
                while(t.hasMoreTokens()) {
                    to[i]=new InternetAddress(t.nextToken().trim());
                    i++;
                }

                try {
                    /**
                     * Since data in session.getUser() is read from file, the encoding
                     * should be default encoding.
                     */
                    // t=new StringTokenizer(MimeUtility.encodeText(head.getContent("CC")).trim(),",");
                    t = new StringTokenizer(TranscodeUtil.transcodeThenEncodeByLocale(head.getContent("CC"), null, locale).trim(), ",");
                } catch(UnsupportedEncodingException e) {
                    log.warn(
                              "Unsupported Encoding while trying to send message: "+e.getMessage());
                    t=new StringTokenizer(head.getContent("CC").trim(),",;");
                }
                Address cc[]=new Address[t.countTokens()];
                i=0;
                while(t.hasMoreTokens()) {
                    cc[i]=new InternetAddress(t.nextToken().trim());
                    i++;
                }

                try {
                    /**
                     * Since data in session.getUser() is read from file, the encoding
                     * should be default encoding.
                     */
                    // t=new StringTokenizer(MimeUtility.encodeText(head.getContent("BCC")).trim(),",");
                    t = new StringTokenizer(TranscodeUtil.transcodeThenEncodeByLocale(head.getContent("BCC"), null, locale).trim(), ",");
                } catch(UnsupportedEncodingException e) {
                    log.warn(
                              "Unsupported Encoding while trying to send message: "+e.getMessage());
                    t=new StringTokenizer(head.getContent("BCC").trim(),",;");
                }
                Address bcc[]=new Address[t.countTokens()];
                i=0;
                while(t.hasMoreTokens()) {
                    bcc[i]=new InternetAddress(t.nextToken().trim());
                    i++;
                }

                session.setSent(false);

                msg.addFrom(from);
                if(to.length > 0) {
                    msg.addRecipients(Message.RecipientType.TO,to);
                }
                if(cc.length > 0) {
                    msg.addRecipients(Message.RecipientType.CC,cc);
                }
                if(bcc.length > 0) {
                    msg.addRecipients(Message.RecipientType.BCC,bcc);
                }
                msg.addHeader("X-Mailer",WebMailServer.getVersion()+", "+getName()+" plugin v"+getVersion());

                String subject = null;

                if(!head.isContentSet("SUBJECT")) {
                    subject="no subject";
                } else {
                    try {
                        // subject=MimeUtility.encodeText(head.getContent("SUBJECT"));
                        subject = TranscodeUtil.transcodeThenEncodeByLocale(head.getContent("SUBJECT"), "ISO8859_1", locale);
                    } catch(UnsupportedEncodingException e) {
                        log.warn("Unsupported Encoding while trying to send message: "+e.getMessage());
                        subject=head.getContent("SUBJECT");
                    }
                }

                msg.addHeader("Subject",subject);

                if(head.isContentSet("REPLY-TO")) {
                    // msg.addHeader("Reply-To",head.getContent("REPLY-TO"));
                    msg.addHeader("Reply-To", TranscodeUtil.transcodeThenEncodeByLocale(head.getContent("REPLY-TO"), "ISO8859_1", locale));
                }

                msg.setSentDate(new Date(System.currentTimeMillis()));

                String contnt=head.getContent("BODY");

                //String charset=MimeUtility.mimeCharset(MimeUtility.getDefaultJavaCharset());
                String charset="utf-8";

                MimeMultipart cont=new MimeMultipart();
                MimeBodyPart txt=new MimeBodyPart();

                // Transcode to UTF-8
                contnt = new String(contnt.getBytes("ISO8859_1"), "UTF-8");
                // Encode text
                if (locale.getLanguage().equals("zh") && locale.getCountry().equals("TW")) {
                    txt.setText(contnt, "Big5");
                    txt.setHeader("Content-Type","text/plain; charset=\"Big5\"");
                    txt.setHeader("Content-Transfer-Encoding", "quoted-printable"); // JavaMail defaults to QP?
                } else {
                    txt.setText(contnt,"utf-8");
                    txt.setHeader("Content-Type","text/plain; charset=\"utf-8\"");
                    txt.setHeader("Content-Transfer-Encoding", "quoted-printable"); // JavaMail defaults to QP?
                }


                /* Add an advertisement if the administrator requested to do so */
                cont.addBodyPart(txt);
                if(store.getConfig("ADVERTISEMENT ATTACH").equals("YES")) {
                    MimeBodyPart adv=new MimeBodyPart();
                    String file="";
                    if(store.getConfig("ADVERTISEMENT SIGNATURE PATH").startsWith("/")) {
                        file=store.getConfig("ADVERTISEMENT SIGNATURE PATH");
                    } else {
                        file=parent.getProperty("webmail.data.path")+System.getProperty("file.separator")+
                            store.getConfig("ADVERTISEMENT SIGNATURE PATH");
                    }
                    String advcont="";
                    try {
                        BufferedReader fin=new BufferedReader(new FileReader(file));
                        String line=fin.readLine();
                        while(line!= null && !line.equals("")) {
                            advcont+=line+"\n";
                            line=fin.readLine();
                        }
                        fin.close();
                    } catch(IOException ex) {}

                    /**
                     * Transcode to UTF-8; Since advcont comes from file, we transcode
                     * it from default encoding.
                     */
                    // Encode text
                    if (locale.getLanguage().equals("zh") &&
                        locale.getCountry().equals("TW")) {
                        advcont = new String(advcont.getBytes(), "Big5");
                        adv.setText(advcont,"Big5");
                        adv.setHeader("Content-Type","text/plain; charset=\"Big5\"");
                        adv.setHeader("Content-Transfer-Encoding", "quoted-printable");
                    } else {
                        advcont = new String(advcont.getBytes(), "UTF-8");
                        adv.setText(advcont,"utf-8");
                        adv.setHeader("Content-Type","text/plain; charset=\"utf-8\"");
                        adv.setHeader("Content-Transfer-Encoding", "quoted-printable");
                    }

                    cont.addBodyPart(adv);
                }
                for (String attachmentKey : session.getAttachments().keySet()) {
                    ByteStore bs=session.getAttachment(attachmentKey);
                    InternetHeaders ih=new InternetHeaders();
                    ih.addHeader("Content-Transfer-Encoding","BASE64");

                    PipedInputStream pin=new PipedInputStream();
                    PipedOutputStream pout=new PipedOutputStream(pin);

                    /* This is used to write to the Pipe asynchronously to avoid blocking */
                    StreamConnector sconn=new StreamConnector(pin,(int)(bs.getSize()*1.6)+1000);
                    BufferedOutputStream encoder=new BufferedOutputStream(MimeUtility.encode(pout,"BASE64"));
                    encoder.write(bs.getBytes());
                    encoder.flush();
                    encoder.close();
                    //MimeBodyPart att1=sconn.getResult();
                    MimeBodyPart att1=new MimeBodyPart(ih,sconn.getResult().getBytes());

                        if(bs.getDescription()!="") {
                        att1.setDescription(bs.getDescription(),"utf-8");
                        }
                    /**
                     * As described in FileAttacher.java line #95, now we need to
                     * encode the attachment file name.
                     */
                    // att1.setFileName(bs.getName());
                    String fileName = bs.getName();
                        String localeCharset=getLocaleCharset(locale.getLanguage(),locale.getCountry());
                        String encodedFileName=MimeUtility.encodeText(fileName, localeCharset, null);
                        if(encodedFileName.equals(fileName)) {
                                att1.addHeader("Content-Type",bs.getContentType());
                                att1.setFileName(fileName);
                        } else {
                                att1.addHeader("Content-Type",bs.getContentType()+"; charset="+localeCharset);
                                encodedFileName=encodedFileName.substring(localeCharset.length()+5, encodedFileName.length()-2);
                                encodedFileName=encodedFileName.replace('=','%');
                                att1.addHeaderLine("Content-Disposition: attachment; filename*="+localeCharset+"''"+encodedFileName);
                        }
                    cont.addBodyPart(att1);
                }
                msg.setContent(cont);
                //              }

                msg.saveChanges();

                boolean savesuccess=true;

                msg.setHeader("Message-ID",session.getUserModel().getWorkMessage().getAttribute("msgid"));
                if(session.getUser().wantsSaveSent()) {
                    String folderhash=session.getUser().getSentFolder();
                    try {
                        Folder folder=session.getFolder(folderhash);
                        Message[] m=new Message[1];
                        m[0]=msg;
                        folder.appendMessages(m);
                    } catch(MessagingException e) {
                        savesuccess=false;
                    } catch(NullPointerException e) {
                        // Invalid folder:
                        savesuccess=false;
                    }
                }

                boolean sendsuccess=false;

                try {
                    Transport.send(msg);
                    Address sent[]=new Address[to.length+cc.length+bcc.length];
                    int c1=0;int c2=0;
                    for(c1=0;c1<to.length;c1++) {
                        sent[c1]=to[c1];
                    }
                    for(c2=0;c2<cc.length;c2++) {
                        sent[c1+c2]=cc[c2];
                    }
                    for(int c3=0;c3<bcc.length;c3++) {
                        sent[c1+c2+c3]=bcc[c3];
                    }
                    sendsuccess=true;
                    throw new SendFailedException("success",new Exception("success"),sent,null,null);
                } catch(SendFailedException e) {
                    session.handleTransportException(e);
                }

                //session.clearMessage();

                content=new XHTMLDocument(session.getModel(),
                                          store.getStylesheet("sendresult.xsl",
                                                              user.getPreferredLocale(),user.getTheme()));
//              if(sendsuccess) session.clearWork();
            } catch(Exception e) {
                log.error("Could not send messsage", e);
                throw new DocumentNotFoundException("Could not send message. (Reason: "+e.getMessage()+")");
            }

        } else if(head.isContentSet("ATTACH")) {
            /* Redirect request for attachment (unfortunately HTML forms are not flexible enough to
               have two targets without Javascript) */
            content=parent.getURLHandler().handleURL("/compose/attach",session,head);
        } else {
            throw new DocumentNotFoundException("Could not send message. (Reason: No content given)");
        }
        return content;
    }

        private String getLocaleCharset(String language, String country) {
                if (language.equals("zh") && country.equals("TW")) {
                        return "Big5";
                }
                if(language.equals("hu")) {
                        return "iso-8859-2";
                }
                return "UTF-8";
        }

    public String provides() {
        return "message send";
    }

    public String requires() {
        return "composer";
    }
}
