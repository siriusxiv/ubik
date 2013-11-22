/*
 * @(#)$Id: WebMailSession.java 136 2008-10-31 21:14:28Z unsaved $
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;

import javax.mail.AuthenticationFailedException;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Provider;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import net.wastl.webmail.exceptions.InvalidDataException;
import net.wastl.webmail.exceptions.InvalidPasswordException;
import net.wastl.webmail.exceptions.NoSuchFolderException;
import net.wastl.webmail.exceptions.UserDataException;
import net.wastl.webmail.exceptions.WebMailException;
import net.wastl.webmail.misc.ByteStore;
import net.wastl.webmail.misc.Helper;
import net.wastl.webmail.misc.JavaScriptCleaner;
import net.wastl.webmail.misc.MD5;
import net.wastl.webmail.server.http.HTTPRequestHeader;
import net.wastl.webmail.ui.html.Fancyfier;
import net.wastl.webmail.xml.XMLMessage;
import net.wastl.webmail.xml.XMLMessagePart;
import net.wastl.webmail.xml.XMLUserData;
import net.wastl.webmail.xml.XMLUserModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * A user session for WebMail.
 * Contains the state of the actual user (loads it from disk).
 * Has a unique session-ID.
 *
 * @author Sebastian Schaffert
 */
public class WebMailSession implements HTTPSession {
    private static Log log = LogFactory.getLog(WebMailSession.class);

    /** When has the session been last accessed? */
    private long last_access;
    /** The session-ID for this session */
    private String session_code;
    /** Parent WebMailServer */
    private WebMailServer parent;
    /** State of the current users configuration */
    private XMLUserData user;

    private XMLUserModel model;

    /** Connections to Mailboxes */
    private Map<String, Folder> connections;

    /** Connections to hosts */
    private Map<String, Store> stores;

    /** javax.mail Mailsession */
    private Session mailsession;

    private InetAddress remote;

    /* Files attached to messages will be stored here. We will have to take care of
       possible memory problems! */
    private Map<String, ByteStore> mime_parts_decoded;

    private boolean sent;

    private String remote_agent;
    private String remote_accepts;

    private int attachments_size=0;

    private String last_login;

    /** Save the login password. It will be used for the second try password if
     * opening a folder fails.
     */
    private String login_password;

    private Object sess=null;

    private Map<String, Folder> folders;

    protected Vector<String> need_expunge_folders;

    protected String imapBasedir = null;

    protected boolean is_logged_out=false;

    public WebMailSession(WebMailServer parent,Object parm,HTTPRequestHeader h)
         throws UserDataException, InvalidPasswordException, WebMailException {
        try {
            Class srvltreq=Class.forName("javax.servlet.http.HttpServletRequest");
            if(srvltreq.isInstance(parm)) {
                javax.servlet.http.HttpServletRequest req=(javax.servlet.http.HttpServletRequest)parm;
                this.sess=req.getSession(false);
                session_code=((javax.servlet.http.HttpSession)sess).getId();

                try {
                    remote=InetAddress.getByName(req.getRemoteHost());
                } catch(UnknownHostException e) {
                    try {
                        remote=InetAddress.getByName(req.getRemoteAddr());
                    } catch(Exception ex) {
                        try {
                            remote=InetAddress.getByName("localhost");
                        } catch(Exception ex2) {}
                    }
                }
            } else {
                throw new Exception("Servlet class found but not running as servlet");
            }
        } catch(Throwable t) {
            this.remote=(InetAddress)parm;
            session_code=Helper.calcSessionCode(remote,h);
        }
        doInit(parent,h);
    }

    /**
     * This method does the actual initialisation
     */
    protected void doInit(WebMailServer parent, HTTPRequestHeader h)
         throws UserDataException, InvalidPasswordException, WebMailException {
        String domain;
        setLastAccess();
        this.parent=parent;
        remote_agent=h.getHeader("User-Agent").replace('\n',' ');
        remote_accepts=h.getHeader("Accept").replace('\n',' ');
        log.info("WebMail: New Session ("+session_code+")");
        if(parent.getStorage().getVirtuals()==true && h.getContent("vdom") != null) {
            domain=h.getContent("vdom");
        } else {
            domain="localnet";
        }
        user=WebMailServer.getStorage().getUserData(h.getContent("login"),domain,h.getContent("password"),true);
        last_login=user.getLastLogin();
        user.login();
        login_password=h.getContent("password");
        model=parent.getStorage().createXMLUserModel(user);
        connections = new Hashtable<String, Folder>();
        stores = new Hashtable<String, Store>();
        folders = new Hashtable<String, Folder>();
        mailsession=Session.getDefaultInstance(System.getProperties(),null);

        /* If the user logs in for the first time we want all folders subscribed */
        /* // Nope, we don't want that!
        if(user.getLoginCount().equals("1")) {
            Enumeration enumVar=user.mailHosts();
            while(enumVar.hasMoreElements()) {
                String id=(String)enumVar.nextElement();
                if(user.getMailHost(id).getName().equals("Default")) {
                    try {
                        setSubscribedAll(id,true);
                    } catch(MessagingException ex) {
                        ex.printStackTrace();
                    }
                    break;
                }
            }
        }
        */
        setEnv();
    }

    public XMLUserModel getUserModel() {
        return model;
    }

    public Document getModel() {
        return model.getRoot();
    }

    /**
     * Calculate session-ID for a session.
     *
     * @param a Adress of the remote host
     * @param h Requestheader of the remote user agent
     * @returns Session-ID
     */
    public String calcCode(InetAddress a, HTTPRequestHeader h) {
        if(sess==null) {
            return Helper.calcSessionCode(a,h);
        } else {
            try {
                Class srvltreq=Class.forName("javax.servlet.http.HttpSession");
                if(srvltreq.isInstance(sess)) {
                    return ((javax.servlet.http.HttpSession)sess).getId();
                } else {
                    return "error";
                }
            } catch(Throwable t) {
                return "error";
            }
        }
    }

    /**
     * Login to this session.
     * Establishes connections to a user´s Mailhosts
     *
     * @param h RequestHeader with content from Login-POST operation.
     * @deprecated Use login() instead, no need for parameters and exception handling
     */
    @Deprecated
    public void login(HTTPRequestHeader h) throws InvalidPasswordException {
        //user.login(h.getContent("password"));
        login();
    }

    /**
     * Login this session.
     *
     * Updates access time, sets initial environment and connects all configured mailboxes.
     */
    public void login() {
        setLastAccess();
        setEnv();
        refreshFolderInformation(false, false);
    }

    /**
     * Return a locale-specific string resource
     */
    public String getStringResource(String key) {
        return parent.getStorage().getStringResource(key,user.getPreferredLocale());
    }

    /**
     * Create a Message List.
     * Fetches a list of headers in folder foldername for part list_part.
     * The messagelist will be stored in the "MESSAGES" environment.
     *
     * @param foldername folder for which a message list should be built
     * @param list_part part of list to display (1 = last xx messages, 2 = total-2*xx - total-xx messages)
     */
    public void createMessageList(String folderhash,int list_part)
        throws NoSuchFolderException {
        long time_start=System.currentTimeMillis();
        TimeZone tz=TimeZone.getDefault();
        DateFormat df=DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT, user.getPreferredLocale());
        df.setTimeZone(tz);

        try {
            Folder folder=getFolder(folderhash);
            Element xml_folder=model.getFolder(folderhash);
            Element xml_current=model.setCurrentFolder(folderhash);
            Element xml_messagelist=model.getMessageList(xml_folder);

            if(folder == null) {
                throw new NoSuchFolderException(folderhash);
            }

            long fetch_start=System.currentTimeMillis();

            if(!folder.isOpen()) {
                folder.open(Folder.READ_ONLY);
            } else {
                folder.close(false);
                folder.open(Folder.READ_ONLY);
            }

            /* Calculate first and last message to show */
            int total_messages=folder.getMessageCount();
            int new_messages=folder.getNewMessageCount();
            int show_msgs=user.getMaxShowMessages();

            xml_messagelist.setAttribute("total",total_messages+"");
            xml_messagelist.setAttribute("new",new_messages+"");

            log.debug("Total: "+total_messages);

            /* Handle small messagelists correctly */
            if(total_messages < show_msgs) {
                show_msgs = total_messages;
            }
            /* Don't accept list-parts smaller than 1 */
            if(list_part < 1) {
                list_part=1;
            }
            for(int k=0;k<list_part;k++) {
                total_messages-=show_msgs;
            }
            /* Handle beginning of message list */
            if(total_messages<0) {
                total_messages=0;
            }
            int first=total_messages+1;
            int last=total_messages+show_msgs;
            /* Set environment variable */
            setEnv();
            xml_current.setAttribute("first_msg",first+"");
            xml_current.setAttribute("last_msg",last+"");
            xml_current.setAttribute("list_part",list_part+"");

            /* Fetch headers */
            FetchProfile fp=new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            fp.add(FetchProfile.Item.FLAGS);
            fp.add(FetchProfile.Item.CONTENT_INFO);
            log.debug("Last: "+last+", first: "+first);
            Message[] msgs=folder.getMessages(first,last);
            log.debug(msgs.length + " messages fetching...");
            folder.fetch(msgs,fp);
            long fetch_stop=System.currentTimeMillis();

            Map header=new Hashtable(15);

            Flags.Flag[] sf;
            String from,to,cc,bcc,replyto,subject;
            String messageid;

            for(int i=msgs.length-1; i>=0; i--) {
//              if(((MimeMessage)msgs[i]).getMessageID() == null) {
//                  folder.close(false);
//                  folder.open(Folder.READ_WRITE);
//                  ((MimeMessage)msgs[i]).setHeader("Message-ID","<"+user.getLogin()+"."+System.currentTimeMillis()+".jwebmail@"+user.getDomain()+">");
//                  ((MimeMessage)msgs[i]).saveChanges();
//                  folder.close(false);
//                  folder.open(Folder.READ_ONLY);
//              }

                try {
                    StringTokenizer tok=new StringTokenizer(((MimeMessage)msgs[i]).getMessageID(),"<>");
                    messageid=tok.nextToken();
                } catch(NullPointerException ex) {
                    // For mail servers that don't generate a Message-ID (Outlook et al)
                    messageid=user.getLogin()+"."+i+".jwebmail@"+user.getDomain();
                }

                XMLMessage xml_message=model.getMessage(xml_folder,msgs[i].getMessageNumber()+"",
                                                     messageid);

                /* Addresses */
                from="";replyto="";to="";cc="";bcc="";
                try {
                    from=MimeUtility.decodeText(Helper.joinAddress(msgs[i].getFrom()));
                } catch(UnsupportedEncodingException e) {
                        from=Helper.joinAddress(msgs[i].getFrom());
                }
                try {
                    replyto=MimeUtility.decodeText(Helper.joinAddress(msgs[i].getReplyTo()));
                } catch(UnsupportedEncodingException e) {
                        replyto=Helper.joinAddress(msgs[i].getReplyTo());
                }
                try {
                    to=MimeUtility.decodeText(Helper.joinAddress(msgs[i].getRecipients(Message.RecipientType.TO)));
                } catch(UnsupportedEncodingException e) {
                        to=Helper.joinAddress(msgs[i].getRecipients(Message.RecipientType.TO));
                }
                try {
                        cc=MimeUtility.decodeText(Helper.joinAddress(msgs[i].getRecipients(Message.RecipientType.CC)));
                } catch(UnsupportedEncodingException e) {
                        cc=Helper.joinAddress(msgs[i].getRecipients(Message.RecipientType.CC));
                }
                try {
                        bcc=MimeUtility.decodeText(Helper.joinAddress(msgs[i].getRecipients(Message.RecipientType.BCC)));
                } catch(UnsupportedEncodingException e) {
                        bcc=Helper.joinAddress(msgs[i].getRecipients(Message.RecipientType.BCC));
                }
                if(from=="") from=getStringResource("unknown sender");
                if(to == "") to = getStringResource("unknown recipient");

                                /* Flags */
                sf = msgs[i].getFlags().getSystemFlags();
                String basepath=parent.getBasePath();

                for(int j=0;j<sf.length;j++) {
                    if(sf[j]==Flags.Flag.RECENT) xml_message.setAttribute("recent","true");
                    if(sf[j]==Flags.Flag.SEEN) xml_message.setAttribute("seen","true");
                    if(sf[j]==Flags.Flag.DELETED) xml_message.setAttribute("deleted","true");
                    if(sf[j]==Flags.Flag.ANSWERED) xml_message.setAttribute("answered","true");
                    if(sf[j]==Flags.Flag.DRAFT) xml_message.setAttribute("draft","true");
                    if(sf[j]==Flags.Flag.FLAGGED) xml_message.setAttribute("flagged","true");
                    if(sf[j]==Flags.Flag.USER) xml_message.setAttribute("user","true");
                }
                if(msgs[i] instanceof MimeMessage &&
                   ((MimeMessage) msgs[i]).getContentType().toUpperCase().startsWith("MULTIPART/")) {
                    xml_message.setAttribute("attachment","true");
                }

                if(msgs[i] instanceof MimeMessage) {
                    int size=((MimeMessage) msgs[i]).getSize();
                    size/=1024;
                    xml_message.setAttribute("size",(size>0?size+"":"<1")+" kB");
                }

                /* Subject */
                subject="";
                if(msgs[i].getSubject() != null) {
                    try {
                        subject=MimeUtility.decodeText(msgs[i].getSubject());
                    } catch(UnsupportedEncodingException ex) {
                                subject=msgs[i].getSubject();
                                log.warn("Unsupported Encoding: "+ex.getMessage());
                    }
                }
                if(subject == null || subject.equals("")) {
                    subject=getStringResource("no subject");
                }

                /* Set all of what we found into the DOM */
                xml_message.setHeader("FROM",from);
                try {
                        // hmm, why decode subject twice? Though it doesn't matter..
                    xml_message.setHeader("SUBJECT",MimeUtility.decodeText(subject));
                } catch(UnsupportedEncodingException e) {
                        xml_message.setHeader("SUBJECT",subject);
                    log.warn("Unsupported Encoding: "+e.getMessage());
                }
                xml_message.setHeader("TO",to);
                xml_message.setHeader("CC",cc);
                xml_message.setHeader("BCC",bcc);
                xml_message.setHeader("REPLY-TO",replyto);

                /* Date */
                Date d=msgs[i].getSentDate();
                String ds="";
                if(d!=null) {
                    ds=df.format(d);
                }
                xml_message.setHeader("DATE",ds);
            }
            long time_stop=System.currentTimeMillis();
            // try {
                // XMLCommon.writeXML(model.getRoot(),new FileOutputStream("/tmp/wmdebug"),"");
            // } catch(IOException ex) {}

            log.debug("Construction of message list took "+(time_stop-time_start)+" ms. Time for IMAP transfer was "+(fetch_stop-fetch_start)+" ms.");
            folder.close(false);
        } catch(NullPointerException e) {
            log.error("Failed to construct message list", e);
            throw new NoSuchFolderException(folderhash);
        } catch(MessagingException ex) {
            log.error("Failed to construct message list.  "
                    + "For some reason, contuing anyways.", ex);
        }
    }

    /**
     * This indicates standard getMessage behaviour: Fetch the message from the IMAP server and store it in the
     * current UserModel.
     * @see getMessage(String,int,int)
     */
    public static final int GETMESSAGE_MODE_STANDARD=0;

    /**
     * Set this mode in getMessage to indicate that the message is requested to generate a reply message and
     * should therefore be set as the current "work" message.
     * @see getMessage(String,int,int)
     */
    public static final int GETMESSAGE_MODE_REPLY=1;

    /**
     * Set this mode in getMessage to indicate that the message is to be forwarded to someone else and a "work"
     * message should be generated.
     * @see getMessage(String,int,int)
     */
    public static final int GETMESSAGE_MODE_FORWARD=2;

    /**
     * This is a wrapper to call getMessage with standard mode.
     * @see getMessage(String,int,int)
     */
    public void getMessage(String folderhash, int msgnum) throws NoSuchFolderException, WebMailException {
        getMessage(folderhash,msgnum,GETMESSAGE_MODE_STANDARD);
    }

    /**
     * Fetch a message from a folder.
     * Will put the messages parameters in the sessions environment
     *
     * @param foldername Name of the folder were the message should be fetched from
     * @param msgnum Number of the message to fetch
     * @param mode there are three different modes: standard, reply and forward. reply and forward will enter the message
     *             into the current work element of the user and set some additional flags on the message if the user
     *             has enabled this option.
     * @see net.wastl.webmail.server.WebMailSession.GETMESSAGE_MODE_STANDARD
     * @see net.wastl.webmail.server.WebMailSession.GETMESSAGE_MODE_REPLY
     * @see net.wastl.webmail.server.WebMailSession.GETMESSAGE_MODE_FORWARD
     */
    public void getMessage(String folderhash, int msgnum, int mode) throws NoSuchFolderException, WebMailException {
        // security reasons:
        // attachments=null;

        try {
            TimeZone tz=TimeZone.getDefault();
            DateFormat df=DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT, user.getPreferredLocale());
            df.setTimeZone(tz);
            Folder folder=getFolder(folderhash);
            Element xml_folder=model.getFolder(folderhash);

            if(folder==null) {
                throw new NoSuchFolderException("No such folder: "+folderhash);
            }

            if(folder.isOpen() && folder.getMode()==Folder.READ_WRITE) {
                folder.close(false);
                folder.open(Folder.READ_ONLY);
            } else if(!folder.isOpen()) {
                folder.open(Folder.READ_ONLY);
            }

            MimeMessage m=(MimeMessage)folder.getMessage(msgnum);

            String messageid;
            try {
                StringTokenizer tok=new StringTokenizer(m.getMessageID(),"<>");
                messageid=tok.nextToken();
            } catch(NullPointerException ex) {
                // For mail servers that don't generate a Message-ID (Outlook et al)
                messageid=user.getLogin()+"."+msgnum+".jwebmail@"+user.getDomain();
            }

            Element xml_current=model.setCurrentMessage(messageid);
            XMLMessage xml_message=model.getMessage(xml_folder,m.getMessageNumber()+"",
                                                    messageid);

            /* Check whether we already cached this message (not only headers but complete)*/
            boolean cached=xml_message.messageCompletelyCached();
            /* If we cached the message, we don't need to fetch it again */
            if(!cached) {
                //Element xml_header=model.getHeader(xml_message);

                try {
                    String from=MimeUtility.decodeText(Helper.joinAddress(m.getFrom()));
                    String replyto=MimeUtility.decodeText(Helper.joinAddress(m.getReplyTo()));
                    String to=MimeUtility.decodeText(Helper.joinAddress(m.getRecipients(Message.RecipientType.TO)));
                    String cc=MimeUtility.decodeText(Helper.joinAddress(m.getRecipients(Message.RecipientType.CC)));
                    String bcc=MimeUtility.decodeText(Helper.joinAddress(m.getRecipients(Message.RecipientType.BCC)));
                    Date date_orig=m.getSentDate();
                    String date=getStringResource("no date");
                    if(date_orig!=null) {
                        date=df.format(date_orig);
                    }
                    String subject="";
                    if(m.getSubject() != null) {
                        subject=MimeUtility.decodeText(m.getSubject());
                    }
                    if(subject == null || subject.equals("")) {
                        subject=getStringResource("no subject");
                    }

                    try {
                        Flags.Flag[] sf = m.getFlags().getSystemFlags();
                        for(int j=0;j<sf.length;j++) {
                            if(sf[j]==Flags.Flag.RECENT) xml_message.setAttribute("recent","true");
                            if(sf[j]==Flags.Flag.SEEN) xml_message.setAttribute("seen","true");
                            if(sf[j]==Flags.Flag.DELETED) xml_message.setAttribute("deleted","true");
                            if(sf[j]==Flags.Flag.ANSWERED) xml_message.setAttribute("answered","true");
                            if(sf[j]==Flags.Flag.DRAFT) xml_message.setAttribute("draft","true");
                            if(sf[j]==Flags.Flag.FLAGGED) xml_message.setAttribute("flagged","true");
                            if(sf[j]==Flags.Flag.USER) xml_message.setAttribute("user","true");
                        }
                    } catch(NullPointerException ex) {}
                    if(m.getContentType().toUpperCase().startsWith("MULTIPART/")) {
                        xml_message.setAttribute("attachment","true");
                    }

                    int size=m.getSize();
                    size/=1024;
                    xml_message.setAttribute("size",(size>0?size+"":"<1")+" kB");

                    /* Set all of what we found into the DOM */
                    xml_message.setHeader("FROM",from);
                    xml_message.setHeader("SUBJECT",Fancyfier.apply(subject));
                    xml_message.setHeader("TO",to);
                    xml_message.setHeader("CC",cc);
                    xml_message.setHeader("BCC",bcc);
                    xml_message.setHeader("REPLY-TO",replyto);
                    xml_message.setHeader("DATE",date);

                    /* Decode MIME contents recursively */
                    xml_message.removeAllParts();
                    parseMIMEContent(m,xml_message,messageid);

                } catch(UnsupportedEncodingException e) {
                    log.warn("Unsupported Encoding in parseMIMEContent: "+e.getMessage());
                }
            }
            /* Set seen flag (Maybe make that threaded to improve performance) */
            if(user.wantsSetFlags()) {
                if(folder.isOpen() && folder.getMode()==Folder.READ_ONLY) {
                    folder.close(false);
                    folder.open(Folder.READ_WRITE);
                } else if(!folder.isOpen()) {
                    folder.open(Folder.READ_WRITE);
                }
                folder.setFlags(msgnum,msgnum,new Flags(Flags.Flag.SEEN),true);
                folder.setFlags(msgnum,msgnum,new Flags(Flags.Flag.RECENT), false);
                if((mode & GETMESSAGE_MODE_REPLY) == GETMESSAGE_MODE_REPLY) {
                    folder.setFlags(msgnum,msgnum,new Flags(Flags.Flag.ANSWERED),true);
                }
            }
            folder.close(false);

            /* In this part we determine whether the message was requested so that it may be used for
               further editing (replying or forwarding). In this case we set the current "work" message to the
               message we just fetched and then modifiy it a little (quote, add a "Re" to the subject, etc). */
            XMLMessage work=null;
            if((mode & GETMESSAGE_MODE_REPLY) == GETMESSAGE_MODE_REPLY ||
               (mode & GETMESSAGE_MODE_FORWARD) == GETMESSAGE_MODE_FORWARD) {
                log.debug("Setting work message!");
                work=model.setWorkMessage(xml_message);

String newmsgid=WebMailServer.generateMessageID(user.getUserName());

                if(work != null && (mode & GETMESSAGE_MODE_REPLY) == GETMESSAGE_MODE_REPLY) {
                    String from=work.getHeader("FROM");
                    work.setHeader("FROM",user.getDefaultEmail());
                    work.setHeader("TO",from);
                    work.prepareReply(getStringResource("reply subject prefix"),
                                      getStringResource("reply subject postfix"),
                                      getStringResource("reply message prefix"),
                                      getStringResource("reply message postfix"));

                } else if(work != null && (mode & GETMESSAGE_MODE_FORWARD) == GETMESSAGE_MODE_FORWARD) {
                    String from=work.getHeader("FROM");
                    work.setHeader("FROM",user.getDefaultEmail());
                    work.setHeader("TO","");
                    work.setHeader("CC","");
                    work.prepareForward(getStringResource("forward subject prefix"),
                                        getStringResource("forward subject postfix"),
                                        getStringResource("forward message prefix"),
                                        getStringResource("forward message postfix"));

                    /* Copy all references to MIME parts to the new message id */
                    for (String key : getMimeParts(work.getAttribute("msgid"))){
                        StringTokenizer tok2=new StringTokenizer(key,"/");
                        tok2.nextToken();
                        String newkey=tok2.nextToken();
                        mime_parts_decoded.put(newmsgid+"/"+newkey,mime_parts_decoded.get(key));
                    }
                }

                /* Clear the msgnr and msgid fields at last */
                work.setAttribute("msgnr","0");
                work.setAttribute("msgid",newmsgid);
                prepareCompose();
            }
        } catch(MessagingException ex) {
            log.error("Failed to get message.  Doing nothing instead.", ex);
        }
    }

    /**
       Use depth-first search to go through MIME-Parts recursively.
       @param p Part to begin with
    */
    protected void parseMIMEContent(Part p, XMLMessagePart parent_part, String msgid) throws MessagingException {
        StringBuilder content=new StringBuilder(1000);
        XMLMessagePart xml_part;
        try {
            if(p.getContentType().toUpperCase().startsWith("TEXT/HTML")) {
                /* The part is a text in HTML format. We will try to use "Tidy" to create a well-formatted
                   XHTML DOM from it and then remove JavaScript and other "evil" stuff.
                   For replying to such a message, it will be useful to just remove all of the tags and display
                   only the text.
                */

                xml_part=parent_part.createPart("html");

                /****************************************************
                 * LEAVING THESE OLD XML PARSERS COMMENTED OUT
                 * until know that the new Parser tactic parses HTML properly
                 * (or adequately).  See the ** comment below.
                /* Here we create a DOM tree.
                //Tidy tidy=new Tidy();
                //tidy.setUpperCaseTags(true);
                //Document htmldoc=tidy.parseDOM(p.getInputStream(),null);
//              org.cyberneko.html.parsers.DOMParser parser =
//                  new org.cyberneko.html.parsers.DOMParser();
//              DOMParser parser = new DOMParser(new HTMLConfiguration());
//              parser.parse(new InputSource(p.getInputStream()));
//              Document htmldoc = parser.getDocument();

                // instantiate a DOM implementation
                DOM dom = new com.docuverse.dom.DOM();
                // install the SAX driver for Swing HTML parser
                dom.setProperty("sax.driver", "com.docuverse.html.swing.SAXDriver");

                // install HTML element factory
                dom.setFactory(new com.docuverse.dom.html.HTMLFactory());
                // ** N.B.  WITH docuverse AND NekoHTML, THE PARSER WAS
                // HTML-Specific.  We are now using generic XML parser.
                // Is that adequate?

                // now just open the document
                Document htmldoc = (Document)dom.readDocument(p.getInputStream());
*/
                javax.xml.parsers.DocumentBuilder parser =
                        javax.xml.parsers.DocumentBuilderFactory.newInstance().
                                newDocumentBuilder();
                Document htmldoc = parser.parse(p.getInputStream());

                if(htmldoc == null) {
                    log.error("Document was null!");
                    // Why not throwing?
                }

                //dom.writeDocument(htmldoc,"/tmp/test.xml");

                /* Now let's look for all the malicious JavaScript and other <SCRIPT> tags,
                   URLS containing the "javascript:" and tags containing "onMouseOver" and such
                   stuff. */
                //              if(user.getBoolVar("filter javascript")) new JavaScriptCleaner(htmldoc);
                new JavaScriptCleaner(htmldoc);

                //dom.writeDocument(htmldoc,"/tmp/test2.xml");

                //XMLCommon.debugXML(htmldoc);
                /* HTML doesn't allow us to do such fancy stuff like different quote colors,
                   perhaps this will be implemented in the future */

                /* So we just add this HTML document to the message part, which will deal with
                   removing headers and tags that we don't need */
                xml_part.addContent(htmldoc);

            } else if(p.getContentType().toUpperCase().startsWith("TEXT") ||
               p.getContentType().toUpperCase().startsWith("MESSAGE")) {
                /* The part is a standard message part in some incarnation of
                   text (html or plain).  We should decode it and take care of
                   some extra issues like recognize quoted parts, filter
                   JavaScript parts and replace smileys with smiley-icons if the
                   user has set wantsFancy() */

                xml_part=parent_part.createPart("text");
                // TODO:
                log.debug("text hit");

                BufferedReader in;
                if(p instanceof MimeBodyPart) {
                    int size=p.getSize();
                    MimeBodyPart mpb=(MimeBodyPart)p;
                    InputStream is=mpb.getInputStream();

                    /* Workaround for Java or Javamail Bug */
                    is=new BufferedInputStream(is);
                    ByteStore ba=ByteStore.getBinaryFromIS(is,size);
                    in=new BufferedReader(new InputStreamReader(new ByteArrayInputStream(ba.getBytes())));
                    /* End of workaround */
                    size=is.available();

                } else {
                    in=new BufferedReader(new InputStreamReader(p.getInputStream()));
                }


                log.debug("Content-Type: "+p.getContentType());

                String token="";
                int quote_level=0, old_quotelevel=0;
                boolean javascript_mode=false;
                /* Read in the message part line by line */
                while((token=in.readLine()) != null) {
                    /* First decode all language and MIME dependant stuff */
                    // Default to ISO-8859-1 (Western Latin 1)
                    String charset="ISO-8859-1";

                    // Check whether the part contained a charset in the content-type header
                    StringTokenizer tok2=new StringTokenizer(p.getContentType(),";=");
                    String blah=tok2.nextToken();
                    if(tok2.hasMoreTokens()) {
                        blah=tok2.nextToken().trim();
                        if(blah.toLowerCase().equals("charset") && tok2.hasMoreTokens()) {
                            charset=tok2.nextToken().trim();
                        }
                    }

                    try {
                      token=new String(token.getBytes(),charset);
                    } catch(UnsupportedEncodingException ex1) {
                      log.info("Java Engine does not support charset "+charset+". Trying to convert from MIME ...");

                      try {
                        charset=MimeUtility.javaCharset(charset);
                        token=new String(token.getBytes(),charset);

                      } catch(UnsupportedEncodingException ex) {
                      log.warn("Converted charset ("+charset+") does not work. Using default charset (ouput may contain errors)");
                        token=new String(token.getBytes());
                      }
                    }

                    /* Here we figure out which quote level this line has, simply by counting how many
                       ">" are in front of the line, ignoring all whitespaces. */
                    int current_quotelevel=Helper.getQuoteLevel(token);

                    /* When we are in a different quote level than the last line, we append all we got
                       so far to the part with the old quotelevel and begin with a clean String buffer */
                    if(current_quotelevel != old_quotelevel) {
                        xml_part.addContent(content.toString(),old_quotelevel);
                        old_quotelevel = current_quotelevel;
                        content=new StringBuilder(1000);
                    }

                    if(user.wantsBreakLines()) {
                        Enumeration enumVar=Helper.breakLine(token,user.getMaxLineLength(),current_quotelevel);

                        while(enumVar.hasMoreElements()) {
                            String s=(String)enumVar.nextElement();
                            if(user.wantsShowFancy()) {
                                content.append(Fancyfier.apply(s)).append("\n");
                            } else {
                                content.append(s).append("\n");
                            }
                        }
                    } else {
                        if(user.wantsShowFancy()) {
                            content.append(Fancyfier.apply(token)).append("\n");
                        } else {
                            content.append(token).append("\n");
                        }
                    }
                }
                xml_part.addContent(content.toString(),old_quotelevel);
                // Why the following code???
                content=new StringBuilder(1000);
            } else if(p.getContentType().toUpperCase().startsWith("MULTIPART/ALTERNATIVE")) {
                /* This is a multipart/alternative part. That means that we should pick one of
                   the formats and display it for this part. Our current precedence list is
                   to choose HTML first and then to choose plain text. */
                MimeMultipart m=(MimeMultipart)p.getContent();
                String[] preferred={"TEXT/HTML","TEXT"};
                boolean found=false;
                int alt=0;
                // Walk though our preferred list of encodings. If we have found a fitting part,
                // decode it and replace it for the parent (this is what we really want with an
                // alternative!)
                /**
            findalt: while(!found && alt < preferred.length) {
                for(int i=0;i<m.getCount();i++) {
                    Part p2=m.getBodyPart(i);
                    if(p2.getContentType().toUpperCase().startsWith(preferred[alt])) {
                        parseMIMEContent(p2,parent_part,msgid);
                        found=true;
                        break findalt;
                    }
                }
                alt++;
            }
            **/
                /**
                 * When user try to reply a mail, there may be 3 conditions:
                 * 1. only TEXT exists.
                 * 2. both HTML and TEXT exist.
                 * 3. only HTML exists.
                 *
                 * We have to choose which part should we quote, that is, we must
                 * decide the prority of parts to quote. Since quoting HTML is not
                 * easy and precise (consider a html: <body><div><b>some text..</b>
                 * </div></body>. Even we try to get text node under <body>, we'll
                 * just get nothing, because "some text..." is marked up by
                 * <div><b>. There is no easy way to retrieve text from html
                 * unless we parse the html to analyse its semantics.
                 *
                 * Here is our policy for alternative part:
                 * 1. Displays HTML but hides TEXT.
                 * 2. When replying this mail, try to quote TEXT part. If no TEXT
                 *    part exists, quote HTML in best effort(use
                 *    XMLMessagePart.quoteContent() by Sebastian Schaffert.)
                 */
                while (alt < preferred.length) {
                    for(int i=0;i<m.getCount();i++) {
                        Part p2=m.getBodyPart(i);
                        if(p2.getContentType().toUpperCase().startsWith(preferred[alt])) {
                            log.debug("Processing: " + p2.getContentType());
                            parseMIMEContent(p2,parent_part,msgid);
                            found=true;
                            break;
                        }
                    }
                    /**
                     * If we've selected HTML part from alternative part, the TEXT
                     * part should be hidden from display but keeping in XML for
                     * later quoting operation.
                     *
                     * Of course, this requires some modification on showmessage.xsl.
                     */
                    if (found && (alt == 1)) {
                        Node textPartNode = parent_part.getPartElement().getLastChild();
                        NamedNodeMap attributes = textPartNode.getAttributes();
                        boolean hit = false;

                        for (int i = 0; i < attributes.getLength(); ++i) {
                            Node attr = attributes.item(i);
                            // If type=="TEXT", add a hidden attribute.
                            if (attr.getNodeName().toUpperCase().equals("TYPE") &&
                                attr.getNodeValue().toUpperCase().equals("TEXT")) {
                                ((Element)textPartNode).setAttribute("hidden", "true");
                            }
                        }
                    }
                    alt++;
                }
                if(!found) {
                    // If we didn't find one of our preferred encodings, choose the first one
                    // simply pass the parent part because replacement is what we really want with
                    // an alternative.
                    parseMIMEContent(m.getBodyPart(0),parent_part,msgid);
                }

            } else if(p.getContentType().toUpperCase().startsWith("MULTIPART/")) {
                /* This is a standard multipart message. We should recursively walk thorugh all of
                   the parts and decode them, appending as children to the current part */

                xml_part=parent_part.createPart("multi");

                MimeMultipart m=(MimeMultipart)p.getContent();
                for(int i=0;i<m.getCount();i++) {
                    parseMIMEContent(m.getBodyPart(i),xml_part,msgid);
                }
            } else {
                /* Else treat the part as a binary part that the user should either download or
                   get displayed immediately in case of an image */
                InputStream in=null;
                String type="";
                if(p.getContentType().toUpperCase().startsWith("IMAGE/JPG") ||
                   p.getContentType().toUpperCase().startsWith("IMAGE/JPEG")) {
                    type="jpg";
                    xml_part=parent_part.createPart("image");
                } else if(p.getContentType().toUpperCase().startsWith("IMAGE/GIF")) {
                    type="gif";
                    xml_part=parent_part.createPart("image");
                } else if(p.getContentType().toUpperCase().startsWith("IMAGE/PNG")) {
                    type="png";
                    xml_part=parent_part.createPart("image");
                } else {
                    xml_part=parent_part.createPart("binary");
                }
                int size=p.getSize();
                if(p instanceof MimeBodyPart) {
                    MimeBodyPart mpb=(MimeBodyPart)p;
                    log.debug("MIME Body part (image), Encoding: "+mpb.getEncoding());
                    InputStream is=mpb.getInputStream();

                    /* Workaround for Java or Javamail Bug */
                    in=new BufferedInputStream(is);
                    ByteStore ba=ByteStore.getBinaryFromIS(in,size);
                    in=new ByteArrayInputStream(ba.getBytes());
                    /* End of workaround */
                    size=in.available();

                } else {
                    log.warn("No MIME Body part!!");
                    // Is this unexpected?  Consider changing log level.
                    in=p.getInputStream();
                }

                ByteStore data=ByteStore.getBinaryFromIS(in,size);
                if(mime_parts_decoded==null) {
                    mime_parts_decoded = new HashMap<String, ByteStore>();
                }
                String name=p.getFileName();
                if(name == null || name.equals("")) {
                        // Try an other way
                        String headers[]=p.getHeader("Content-Disposition");
                        int pos=-1;
                        if(headers.length==1) {
                                pos=headers[0].indexOf("filename*=")+10;
                        }
                        if(pos!=-1) {
                                int charsetEnds=headers[0].indexOf("''", pos);
                                String charset=headers[0].substring(pos, charsetEnds);
                                String encodedFileName=headers[0].substring(charsetEnds+2);
                                encodedFileName="=?"+charset+"?Q?"+encodedFileName.replace('%','=')+"?=";
                                name=MimeUtility.decodeText(encodedFileName);
                        } else {
                                name="unknown."+type;
                        }
                }
                // Eliminate space characters. Should do some more things in the future
                name=name.replace(' ','_');
                data.setContentType(p.getContentType());
                data.setContentEncoding("BINARY");
                mime_parts_decoded.put(msgid+"/"+name,data);

                /**
                 * For multibytes language system, we have to separate filename into
                 * 2 format: one for display (UTF-8 encoded), another for encode the
                 * url of hyperlink.
                 * `filename' is for display, while `hrefFileName' is for hyperlink.
                 * To make use of these two attributes, `showmessage.xsl' is slightly
                 * modified.
                 */
                data.setName(name);
                xml_part.setAttribute("filename",name);
                // Transcode name into UTF-8 bytes then make a new ISO8859_1 string to encode URL.
                xml_part.setAttribute("hrefFileName", name);
                xml_part.setAttribute("size",size+"");
                String description=p.getDescription()==null?"":p.getDescription();
                xml_part.setAttribute("description",description);
                StringTokenizer tok=new StringTokenizer(p.getContentType(),";");
                xml_part.setAttribute("content-type",tok.nextToken().toLowerCase());
            }
        } catch(java.io.IOException ex) {
            log.error("Failed to parse mime content", ex);
        } catch(MessagingException ex) {
            log.error("Failed to parse mime content", ex);
        } catch(Exception ex) {
            log.error("Failed to parse mime content", ex);
        }
    }

    public ByteStore getMIMEPart(String msgid,String name) {
        if(mime_parts_decoded != null) {
            return mime_parts_decoded.get(msgid+"/"+name);
        } else {
            return null;
        }
    }

    public List<String> getMimeParts(String msgid) {
        List<String> parts = new ArrayList<String>();
        if(mime_parts_decoded == null) {
            mime_parts_decoded = new HashMap<String, ByteStore>();
        }
        for (String partKey : mime_parts_decoded.keySet())
            if (partKey.startsWith(msgid)) parts.add(partKey);
        return parts;
    }

    public void clearWork() {
        clearAttachments();
        model.clearWork();
    }

    public void prepareCompose() {
        model.getWorkMessage().getFirstMessageTextPart().addContent("\n--\n",0);
        model.getWorkMessage().getFirstMessageTextPart().addContent(user.getSignature(),0);
    }

    /**
     * This method removes all of the attachments of the current "work" message
     */
    public void clearAttachments() {
        attachments_size=0;

        XMLMessage xml_message=model.getWorkMessage();

        String msgid=xml_message.getAttribute("msgid");

        attachments_size=0;
        for (String partKey : getMimeParts(msgid))
            mime_parts_decoded.remove(partKey);
    }

    /**
     * This method returns a table of attachments for the current "work" message
     */
    public Map<String, ByteStore> getAttachments() {
        Map<String, ByteStore> hash = new Hashtable<String, ByteStore>();
        XMLMessage xml_message=model.getWorkMessage();

        String msgid=xml_message.getAttribute("msgid");

        for (String partKey : getMimeParts(msgid)) {
            String filename=partKey.substring(msgid.length()+1);
            hash.put(filename,mime_parts_decoded.get(partKey));
        }

        return hash;
    }

    /**
     * This method returns the attachment with the given name of the current "work" message
     */
    public ByteStore getAttachment(String key) {
        XMLMessage xml_message=model.getWorkMessage();
        String msgid=xml_message.getAttribute("msgid");

        return getMIMEPart(msgid,key);
    }

    /**
     * Add an attachment to the current work message.
     * @param name Name of the attachment (e.g. filename)
     * @param bs The contents of the attachment, as a ByteStore object
     * @param description A short description of the contents (will be used as the "Description:" header
     */
    public void addWorkAttachment(String name, ByteStore bs, String description) throws WebMailException {
        XMLMessage xml_message=model.getWorkMessage();
        XMLMessagePart xml_multipart=xml_message.getFirstMessageMultiPart();

        String msgid=xml_message.getAttribute("msgid");

        bs.setDescription(description);

        attachments_size=0;
        for (String partKey : getMimeParts(msgid)) {
            ByteStore b = mime_parts_decoded.get(partKey);
            attachments_size+=b.getSize();
        }

        int max_size=0;
        try {
            max_size=Integer.parseInt( parent.getStorage().getConfig("MAX ATTACH SIZE"));
        } catch(NumberFormatException e) {
            log.warn("Invalid setting for parameter \"MAX ATTACH SIZE\". Must be a number!");
        }

        if(attachments_size+bs.getSize() > max_size) {
            throw new WebMailException("Attachments are too big. The sum of the sizes may not exceed "+max_size+" bytes.");
        } else {
            mime_parts_decoded.put(msgid+"/"+name,bs);
            attachments_size+=bs.getSize();
            XMLMessagePart xml_part=xml_multipart.createPart("binary");

            xml_part.setAttribute("filename",name);
            xml_part.setAttribute("size",bs.getSize()+"");
            xml_part.setAttribute("description",description);
            xml_part.setAttribute("content-type",bs.getContentType().toLowerCase());
        }
        setEnv();
        //XMLCommon.debugXML(model.getRoot());
    }

    /**
     * Remove the attachment with the given name from the current work message.
     */
    public void removeWorkAttachment(String name) {
        XMLMessage xml_message=model.getWorkMessage();
        XMLMessagePart xml_multipart=xml_message.getFirstMessageMultiPart();

        String msgid=xml_message.getAttribute("msgid");

        mime_parts_decoded.remove(msgid+"/"+name);

        attachments_size=0;
        for (String partKey : getMimeParts(msgid)) {
            ByteStore b = mime_parts_decoded.get(partKey);
            attachments_size+=b.getSize();
        }

        Enumeration<XMLMessagePart> partEnum = xml_multipart.getParts();
        XMLMessagePart oldpart=null;
        while(partEnum.hasMoreElements()) {
            XMLMessagePart tmp = partEnum.nextElement();
            if(tmp.getAttribute("filename") != null &&
               tmp.getAttribute("filename").equals(name)) {
                oldpart=tmp;
                break;
            }
        }
        if(oldpart != null) {
            xml_multipart.removePart(oldpart);
        }
        setEnv();
        //XMLCommon.debugXML(model.getRoot());
    }

    /**
     * Store a message in the environment for further processing.
     */
    public void storeMessage(HTTPRequestHeader head) {
        XMLMessage xml_message=model.getWorkMessage();
        XMLMessagePart xml_textpart=xml_message.getFirstMessageTextPart();

        /* Store the already typed message if necessary/possible */
        if(head.isContentSet("BODY")) {
            StringBuilder content=new StringBuilder();
                /**
                 * Because the data transfered through HTTP should be ISO8859_1,
                 * HTTPRequestHeader is also ISO8859_1 encoded. Furthermore, the
                 * string we used in brwoser is UTF-8 encoded, hence we have to
                 * transcode the stored variables from ISO8859_1 to UTF-8 so that
                 * the client browser displays correctly.
                 */
                String bodyString;
                try {
                    bodyString = new String(head.getContent("BODY").getBytes("ISO8859_1"), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    log.fatal("Failed to encode to UTF-8?  Continuing", e);
                    bodyString = head.getContent("BODY");
                }

            // If the user enabled "break line", then do it!
            if(user.wantsBreakLines()) {
                // StringTokenizer tok=new StringTokenizer(head.getContent("BODY"),"\n");
                StringTokenizer tok=new StringTokenizer(bodyString,"\n");
                while(tok.hasMoreTokens()) {
                    String line=tok.nextToken();
                    Enumeration enumVar=Helper.breakLine(line,user.getMaxLineLength(),
                                                      Helper.getQuoteLevel(line));
                    while(enumVar.hasMoreElements()) {
                        content.append((String)enumVar.nextElement()).append('\n');
                    }
                }
            } else {
                // content.append(head.getContent("BODY"));
                content.append(bodyString);
            }
            xml_textpart.removeAllContent();
            xml_textpart.addContent(content.toString(),0);
        }
        if(head.isContentSet("FROM")) {
                try {
                        xml_message.setHeader("FROM", new String(head.getContent("FROM").getBytes("ISO8859_1"), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    log.fatal("Failed to encode to UTF-8?  Continuing", e);
                    xml_message.setHeader("FROM",head.getContent("FROM"));
                }
        }
        if(head.isContentSet("TO")) {
            // xml_message.setHeader("TO",head.getContent("TO"));
            try {
                        xml_message.setHeader("TO", new String(head.getContent("TO").getBytes("ISO8859_1"), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    log.fatal("Failed to encode to UTF-8?  Continuing", e);
                    xml_message.setHeader("TO",head.getContent("TO"));
                }
        }
        if(head.isContentSet("CC")) {
            // xml_message.setHeader("CC",head.getContent("CC"));
            try {
                xml_message.setHeader("CC", new String(head.getContent("CC").getBytes("ISO8859_1"), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                log.fatal("Failed to encode to UTF-8?  Continuing", e);
                xml_message.setHeader("CC",head.getContent("CC"));
            }
        }
        if(head.isContentSet("BCC")) {
            // xml_message.setHeader("BCC",head.getContent("BCC"));
            try {
                xml_message.setHeader("BCC", new String(head.getContent("BCC").getBytes("ISO8859_1"), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                log.fatal("Failed to encode to UTF-8?  Continuing", e);
                xml_message.setHeader("BCC",head.getContent("BCC"));
            }
        }
        if(head.isContentSet("REPLY-TO")) {
            // xml_message.setHeader("REPLY-TO",head.getContent("REPLY-TO"));
            try {
                xml_message.setHeader("REPLY-TO", new String(head.getContent("REPLY-TO").getBytes("ISO8859_1"), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                log.fatal("Failed to encode to UTF-8?  Continuing", e);
                xml_message.setHeader("REPLY-TO",head.getContent("REPLY-TO"));
            }
        }
        if(head.isContentSet("SUBJECT")) {
            // xml_message.setHeader("SUBJECT",head.getContent("SUBJECT"));
            try {
                xml_message.setHeader("SUBJECT", new String(head.getContent("SUBJECT").getBytes("ISO8859_1"), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                log.fatal("Failed to encode to UTF-8?  Continuing", e);
                xml_message.setHeader("SUBJECT",head.getContent("SUBJECT"));
            }
        }
        setEnv();
    }

    /**
       Get a childfolder of a rootfolder for a specified hash value
    */
    public Folder getChildFolder(Folder root, String folderhash) {
        return getFolder(folderhash);
    }

    /**
     * Get the folder with the given hashvalue.
     * @returns Folder with the given hashvalue
     */
    public Folder getFolder(String folderhash) {
        return folders.get(folderhash);
    }

    /**
     * This method tries to generate a unique folder identifier for the given folder.
     * This method generates an MD5 sum over the complete folder URL, if possible.
     * @see getFolderTree
     * @see net.wastl.webmail.misc.MD5
     */
    protected String generateFolderHash(Folder folder) {
        String id=Integer.toHexString(folder.hashCode());
        // If possible, use the MD5-Sum for the folder ID because it is persistant over sessions
        try {
            MD5 md5=new MD5(folder.getURLName());
            id=md5.asHex();
        } catch(MessagingException ex) {
        }

        return id;
    }


    /**
     * Construct the folder subtree for the given folder and append it to
     * xml_parent.
     *
     * N.b. this method does not necessarily create a new XML Folder Element.
     * If called with subscribed_only and the target Folder (and in some
     * cases its descendants) are not subscribed, no Element will be created
     * and 0 will be returned.
     * <P>
     * Pop servers don't support nesting at all, so you'll just get a single
     * level out of this method.
     * <P>
     * There is a critical subscribed_only difference in behavior between
     * Maildir and mbox type mail servers.
     * Maildir folders are all HOLDS_MESSAGES, whether empty or not, and
     * these folders have a subscribed attribute which the user can set,
     * and which we honor.
     * mbox folders, on the other hand, have no subscribed attribute for
     * their !HOLDS_MESSAGE folders, so we must recurse to all of the
     * descendant HOLDS_MESSAGE folders to see if we should show.
     *
     * @param folder the folder where we begin
     * @param xml_parent XML Element where the gathered information will be
     *                   appended
     * @param subscribed_only Only add 'subscribed' folders
     * @param doCount Whether to generate message counts for Elements
     *                corresponding to HOLDS_MESSAGE folders.
     * @returns maximum depth of the folder tree (needed to calculate the
     *     necessary columns in a table).  Returns 0 if no XML elements added.
     */
    protected int getFolderTree(Folder folder, Element xml_parent,
            boolean subscribed_only, boolean doCount) {
        int generatedDepth = 0;
        int folderType;
        Element xml_folder;

        try {
            folderType = folder.getType();
        } catch(MessagingException ex) {
            log.error("Can't get enough info from server to even make Gui node",
                    ex);
            xml_parent.setAttribute("error",
                    "For child '" + folder.getName() + ":  " + ex.getMessage());
            return 0;
        }
        boolean holds_folders = (folderType & Folder.HOLDS_FOLDERS) != 0;
        boolean holds_messages = (folderType & Folder.HOLDS_MESSAGES) != 0;
        // Sanity check:
        if ((!holds_folders) && !holds_messages) {
            log.fatal("Folder can hold neither folders nor messages: "
                    + folder.getFullName());
            throw new RuntimeException(
                    "Folder can hold neither folders nor messages: "
                    + folder.getFullName());
        }
        if (subscribed_only && holds_messages && !folder.isSubscribed())
            return generatedDepth; // Return right away and save a LOT OF WORK
            // N.b. we honor folder.isSubscribed() only for holds_message
            // folders.  That means all Maildir server folders, and all
            // mbox server folders except for mbox directories.  In this
            // last case, we must recurse to determine whether to show folder.
        String id=generateFolderHash(folder);
        xml_folder=model.createFolder(id,folder.getName(),holds_folders,holds_messages);
        // XMLUserModel.createFolder() declares no throws.  If any Exceptions
        // are expected from it, move the statement above into the try block.
        // The xml_folder Element here will be orphaned and GC'd if we don't
        // appendChild (in which case we return 0).

        if (doCount && holds_messages) try {
            // this folder will definitely be added!
            /* This folder can contain messages */
            Element messagelist=model.createMessageList();

            int total_messages=folder.getMessageCount();
            int new_messages=folder.getNewMessageCount();

            if (total_messages == -1 || new_messages == -1 || !folder.isOpen()) {
                folder.open(Folder.READ_ONLY);
                total_messages=folder.getMessageCount();
                new_messages=folder.getNewMessageCount();
            }
            if(folder.isOpen()) folder.close(false);

            messagelist.setAttribute("total",total_messages+"");
            messagelist.setAttribute("new",new_messages+"");
            log.debug("Counted " + new_messages + '/' + total_messages
                    + " for folder " + folder.getFullName());
            xml_folder.appendChild(messagelist);
        } catch(MessagingException ex) {
            log.warn("Failed to count messages in folder '"
                    + folder.getFullName() + "'", ex);
            xml_folder.setAttribute("error",ex.getMessage());
        }

        int descendantDepth = 0;
        if (holds_folders) try {
            Set<String> fullNameSet = new HashSet<String>();

            /* Recursively add subfolders to the XML model */
            // DO NOT USE listSubscribed(), because with !HOLDS_MESSAGE
            // folders, that skips non-Message Folders which may contain
            // subscribed descendants!
            for (Folder f : folder.list()) {
                if (!fullNameSet.add(f.getFullName())) {
                    log.warn("Skipping duplicate subfolder returned by mail"
                            + " server:  " + f.getFullName());
                    continue;
                }
                if (subscribed_only &&
                        (f.getType() & Folder.HOLDS_MESSAGES) != 0
                        && !f.isSubscribed()) continue;
                        /* If we recursed here, the getFolderTree() would
                         * just return 0 and no harm done.
                         * Just helping performance by preventing a recursion
                         * here.
                         * For comment on the logic here, see the same test
                         * towards the top of this method (before recursion).
                         */
                int depth = getFolderTree(f,xml_folder,subscribed_only, doCount);
                if (depth > descendantDepth ) descendantDepth = depth;
            }
            generatedDepth += descendantDepth ;
        } catch(MessagingException ex) {
            xml_folder.setAttribute("error", ex.getMessage());
        }

        // We've already validated that if subscribed_only and holds_message
        //  then folder is subcribed.  Also verified either holds_m or holds_f.
        //  Only have to check the !holds_message case.
        if (subscribed_only && (!holds_messages) && descendantDepth < 1) {
            xml_folder = null;
            // Unnecessary, but may encourage GC
            return generatedDepth;
        }

        /* We ALWAYS return only subscribed folders except for these two
         * distinct cases: */
        xml_folder.setAttribute("subscribed", (
            (holds_messages && !folder.isSubscribed())
            || ((!holds_messages) && descendantDepth < 1)
        ) ? "false" : "true");
        // N.b. our Element's "subscribed" element does not correspond 1:1
        // to Folder.isSubscribed(), since non-message-holding Folders have
        // no "subscribed" attribute.
        folders.put(id,folder);

        xml_parent.appendChild(xml_folder);
        generatedDepth++;  // Add the count for xml_folder
        return generatedDepth;
    }

    /**
     * Refresh Information about folders.
     * Tries to connect folders that are not yet connected.
     *
     * @doCount display message counts for user
     */
    public void refreshFolderInformation(
            boolean subscribed_only, boolean doCount) {
        /* Right now, doCount corresponds exactly to subscribed_only.
         * When we add a user preference setting or one-time action,
         * to present messages from all folders, we will have
         * subscribed_only false and doCount true. */

        //log.fatal("Invoking refreshFolderInformation(boolean, boolean)",
            //new Throwable("Thread Dump"));  FOR DEBUGGING
        setEnv();
        if(folders==null) folders=new Hashtable<String, Folder>();
        Folder rootFolder = null;
        String cur_mh_id="";
        Enumeration mailhosts=user.mailHosts();
        int max_depth=0;
        int folderType;

        while(mailhosts.hasMoreElements()) {
            cur_mh_id=(String)mailhosts.nextElement();

            MailHostData mhd=user.getMailHost(cur_mh_id);

            URLName url=new URLName(mhd.getHostURL());

            Element mailhost=model.createMailhost(mhd.getName(),mhd.getID(),url.toString());

            int depth=0;

            try {
                rootFolder = getRootFolder(cur_mh_id);

                try {
                    rootFolder.setSubscribed(true);
                } catch(MessagingException ex) {
                    // Only IMAP supports subscription
                    log.warn("Folder.setSubscribed failed.  "
                            + "Probably a non-supporting mail service: "
                            + ex);
                }
            } catch (MessagingException ex) {
                mailhost.setAttribute("error", ex.getMessage());
                log.warn("Failed to connect and get Root folder from ("
                        + url + ')', ex);
                return;
            }

            try {
                depth=getFolderTree(rootFolder.getFolder("INBOX"),
                        mailhost, subscribed_only, doCount);
                log.debug("Loaded INBOX folders below Root to a depth of "
                        + depth);
                String extraFolderPath =
                        ((imapBasedir == null) ? "~" : imapBasedir)
                        + mhd.getLogin();
                //String extraFolderPath = "/home/" + mhd.getLogin();
                Folder nonInboxBase = rootFolder.getFolder(extraFolderPath);
                log.debug("Trying extra base dir "
                        + nonInboxBase.getFullName());
                if (nonInboxBase.exists()) {
                    folderType = nonInboxBase.getType();
                    if ((folderType & Folder.HOLDS_MESSAGES) != 0) {
                        // Can only Subscribe to Folders which may hold Msgs.
                        nonInboxBase.setSubscribed(true);
                        if (!nonInboxBase.isSubscribed())
                            log.error("A bug in JavaMail or in the server is "
                                    + "preventing subscription to '"
                                    + nonInboxBase.getFullName() + "' on '"
                                    + url + "'.  Folders will not be visible.");
                    }
                    int extraDepth = extraDepth = getFolderTree(nonInboxBase,
                            mailhost, subscribed_only, doCount);
                    if (extraDepth > depth) depth = extraDepth;
                    log.debug("Loaded additional folders from below "
                            + nonInboxBase.getFullName()
                            + " with max depth of " + extraDepth);
                }
            } catch (Exception ex) {
                if (!url.getProtocol().startsWith("pop"))
                    mailhost.setAttribute("error", ex.getMessage());
                log.warn("Failed to fetch child folders from ("
                        + url + ')', ex);
            }
            if(depth>max_depth) max_depth=depth;
            model.addMailhost(mailhost);
        }
        model.setStateVar("max folder depth",(1+max_depth)+"");
    }

    public void refreshFolderInformation(String folderhash) {
        Folder folder=getFolder(folderhash);
        Element xml_folder=model.getFolder(folderhash);

        if(xml_folder.getAttribute("holds_messages").toLowerCase().equals("true")) {
            try {
                Element messagelist=model.createMessageList();

                int total_messages=folder.getMessageCount();
                int new_messages=folder.getNewMessageCount();

                if((total_messages == -1 || new_messages == -1) && !folder.isOpen()) {
                    folder.open(Folder.READ_ONLY);
                    total_messages=folder.getMessageCount();
                    new_messages=folder.getNewMessageCount();
                }
                if(folder.isOpen()) folder.close(false);

                messagelist.setAttribute("total",total_messages+"");
                messagelist.setAttribute("new",new_messages+"");

                model.removeMessageList(xml_folder);
                xml_folder.appendChild(messagelist);

            } catch(MessagingException ex) {
                xml_folder.setAttribute("error",ex.getMessage());
            }
        }
    }

    /**
     * Try to subscribe to a folder (i.e. unhide it)
     */
    public void subscribeFolder(String folderhash) {
        Folder folder=getFolder(folderhash);

        // Only IMAP supports subscription...
        try {
            folder.setSubscribed(true);
        } catch(MessagingException ex) {
            log.warn("Folder subscription not supported");
        }
    }

    /**
     * Try to unsubscribe from a folder (i.e. hide it)
     */
    public void unsubscribeFolder(String folderhash) {
        Folder folder=getFolder(folderhash);

        // Only IMAP supports subscription...
        try {
            folder.setSubscribed(false);
        } catch(MessagingException ex) {
            log.warn("Folder subscription not supported");
        }
    }

    /**
     * Subscribe all folders for a Mailhost
     * Do it the non-recursive way: Uses a simple Queue :-)
     *
     * @deprecated Don't use this method, it touches every folder in the user's
     *             mail directory, which in some cases may be the complete
     *             home directory.
     */
    @Deprecated
    public void setSubscribedAll(String id, boolean subscribed) throws MessagingException {
        Folder folder=getRootFolder(id);
        net.wastl.webmail.misc.Queue q=new net.wastl.webmail.misc.Queue();
        q.queue(folder);
        // Only IMAP supports subscription...
        try {
            while(!q.isEmpty()) {
                folder=(Folder)q.next();

                folder.setSubscribed(subscribed);
                Folder[] list=folder.list();
                for(int i=0;i<list.length;i++) {
                    q.queue(list[i]);
                }
            }
        } catch(MessagingException ex) {}
    }

    /**
     * Subscribe the root folder.
     *
     * Preferable over setSubscribedAll
     */
    public void setSubscribedDefault(String id, boolean subscribed)
        throws MessagingException {
        Folder folder=getRootFolder(id);
        folder.setSubscribed(subscribed);
    }

    /**
       Disconnect from all Mailhosts
    */
    public void disconnectAll() {
        Enumeration<String> e=user.mailHosts();
        while(e.hasMoreElements()) {
            String name = e.nextElement();
            disconnect(name);
        }
        Set<String> zapKeys = new HashSet<String>();
        for (Map.Entry<String, Store> storeEntry : stores.entrySet()) {
            Store st = storeEntry.getValue();
            try {
                st.close();
                log.info("Mail: Connection to "+st.toString()+" closed.");
            } catch(Exception ex) {
                log.warn("Mail: Failed to close connection to "+st.toString()+". Reason: "+ex.getMessage());
            }
            zapKeys.add(storeEntry.getKey());
        }
        for (String key : zapKeys) stores.remove(key);
        folders=null;
    }


    public Folder getRootFolder(String name) throws MessagingException {
        if(connections != null && connections.containsKey(name)) {
            return connections.get(name);
        } else {
            return connect(name);
        }
    }

    protected Store connectStore(String host,String protocol,String login, String password) throws MessagingException {
        /* Check whether the domain of this user allows to connect to the host */
        WebMailVirtualDomain vdom=parent.getStorage().getVirtualDomain(user.getDomain());
        if(!vdom.isAllowedHost(host)) {
            throw new MessagingException("You are not allowed to connect to this host");
        }
        imapBasedir = vdom.getImapBasedir();

        /* Check if this host is already connected. Use connection if true, create a new one if false. */
        Store st = stores.get(host+"-"+protocol);
        if(st==null) {
            st=mailsession.getStore(protocol);
            stores.put(host+"-"+protocol,st);
        }

        /* Maybe this is a new store or this store has been disconnected. Reconnect if this is the case. */
        if(!st.isConnected()) {
            try {
                st.connect(host,login,password);
                log.info("Mail: Connection to "+st.toString()+".");
            } catch(AuthenticationFailedException ex) {
                /* If login fails, try the login_password */
                if(!login_password.equals(password) &&
                   parent.getStorage().getConfig("FOLDER TRY LOGIN PASSWORD").toUpperCase().equals("YES")) {
                    st.connect(host,login,login_password);
                    log.info("Mail: Connection to "+st.toString()+", second attempt with login password succeeded.");
                } else {
                    throw ex;
                }
            }
        }
        return st;
    }

    /**
       Connect to mailhost "name"
    */
    public Folder connect(String name) throws MessagingException {
        MailHostData m=user.getMailHost(name);
        URLName url=new URLName(m.getHostURL());

        Store st=connectStore(url.getHost(),url.getProtocol(),m.getLogin(),m.getPassword());

        Folder f=st.getDefaultFolder();
        connections.put(name,f);
        log.info("Mail: Default folder '" + f.getFullName() +
                "' retrieved from store " + st + '.');
        return f;
    }


    /**
       Disconnect from mailhost "name"
    */
    public void disconnect(String name) {
        try {
            Folder f = connections.get(name);
            if(f != null && f.isOpen()) {
                f.close(true);
                Store st = connections.get(name).getStore();
                //st.close();
                log.info("Mail: Disconnected from folder "+f.toString()+" at store "+st.toString()+".");
            } else {
                log.warn("Mail: Folder "+name+" was null???.");
            }
        } catch(MessagingException ex) {
            // Should not happen
            log.fatal("Unexpected failure to disconnect", ex);
        } catch(NullPointerException ex) {
            // This happens when deleting a folder with an error
            log.fatal("Failed to disconnect after deleting folder?  " + ex);
            // Log exception too if the comment above is wrong
        } finally {
            connections.remove(name);
        }
    }

    /**
     * Terminate this session.
     *
     * This will expunge deleted messages, close all mailbox connections, save the user data and then
     * remove this session from the session list, effectively destroying this session.
     */
    public void logout() {
        if(!is_logged_out) {
            is_logged_out=true;
            expungeFolders();
            disconnectAll();
            user.logout();
            saveData();
            log.info("WebMail: Session "+getSessionCode()+" logout.");
            // Make sure the session is invalidated
            if(sess != null) {
                try {
                    Class srvltreq=Class.forName("javax.servlet.http.HttpSession");
                    if(srvltreq.isInstance(sess)) {
                        ((javax.servlet.http.HttpSession)sess).invalidate();
                    }
                } catch(Throwable t) {
                }
            }
            if(parent.getSession(getSessionCode()) != null) {
                parent.removeSession(this);
            }
        } else {
            log.warn("Session was already logged out. Ignoring logout request.");
        }
    }

    /**
     * Check whether this session is already logged out.
     * Useful to avoid loops.
     */
    public boolean isLoggedOut() {
        return is_logged_out;
    }

    /**
     * Return the session id that was generated for this session.
     */
    public String getSessionCode() {
        return session_code;
    }

    /**
     * Return the last access time of this session
     *
     * @see TimeableConnection
     */
    public long getLastAccess() {
        return last_access;
    }

    /**
     * Update the last access time.
     * Sets the last access time to the current time.
     *
     * @see TimeableConnection
     */
    public void setLastAccess() {
        last_access=System.currentTimeMillis();
        log.debug("Setting last access to session: "+last_access);
    }

    /**
     * Handle a timeout for this session.
     * This calls the logout method, effectively terminating this session.
     *
     * @see TimeableConnection
     * @see logout()
     */
    public void timeoutOccured() {
        log.warn("WebMail: Session "+getSessionCode()+" timeout.");
        logout();
    }

    public long getTimeout() {
        long i=600000;
        try {
            i=Long.parseLong(parent.getStorage().getConfig("session timeout"));
        } catch(NumberFormatException ex) {
            log.error("Stored 'session timeout' value ("
                    + parent.getStorage().getConfig("session tmeout")
                    + "' malformatted", ex);
        }
        return i;
    }

    public Locale getLocale() {
        return user.getPreferredLocale();
    }

    public void saveData() {
        parent.getStorage().saveUserData(user.getUserName(),user.getDomain());
    }


    protected static int[] getSelectedMessages(HTTPRequestHeader head, int max) {
        // log.debug("select messages...");

        Enumeration e=head.getContent().keys();
        int _msgs[]=new int[max];
        int j=0;

        while(e.hasMoreElements()) {
            String s=(String)e.nextElement();
            if(s.startsWith("CH") && head.getContent(s).equals("on")) {
                try {
                    _msgs[j]=Integer.parseInt(s.substring(3));
                    //    log.debug(_msgs[j]+" ");
                    j++;
                } catch(NumberFormatException ex) {
                    log.error("Value '" + s + "' malformatted?", ex);
                }
            }
        }

        int msgs[]=new int[j];
        for(int i=0;i<j;i++) {
            msgs[i]=_msgs[i];
        }
        return msgs;
    }


    /**
     * Expunge all folders that have messages waiting to be deleted
     */
    public void expungeFolders() {
        if(need_expunge_folders != null) {
            Enumeration<String> enumVar=need_expunge_folders.elements();
            while(enumVar.hasMoreElements()) {
                String hash=(String)enumVar.nextElement();
                if(user.wantsSetFlags()) {
                    Folder f=getFolder(hash);
                    try {
                        if(f.isOpen()) {
                            f.close(false);
                        }
                        f.open(Folder.READ_WRITE);
                        // POP3 doesn't support expunge!
                        try {
                            f.expunge();
                        } catch(MessagingException ex) {}
                        f.close(true);
                    } catch(MessagingException ex) {
                        // XXXX
                        log.error("Failed to expunge folders", ex);
                    }
                }
            }
        }
    }

    /**
       Change the Flags of the messages the user selected.

    */
    public void setFlags(String folderhash, HTTPRequestHeader head) throws MessagingException {
        if(head.isContentSet("copymovemsgs") && head.getContent("COPYMOVE").equals("COPY")) {
            copyMoveMessage(folderhash,head.getContent("TO"),head,false);
        } else if(head.isContentSet("copymovemsgs") && head.getContent("COPYMOVE").equals("MOVE")) {
            copyMoveMessage(folderhash,head.getContent("TO"),head,true);
        } else if(head.isContentSet("flagmsgs")) {
            log.debug("setting message flags");
            Folder folder=getFolder(folderhash);


            //log.debug("Processing Request Header...");

            /* Get selected messages */
            int msgs[]=getSelectedMessages(head,folder.getMessageCount());

            //log.debug("get flags...");

            /* Get selected flags */
            Flags fl=new Flags(Flags.Flag.USER);
            if(head.getContent("MESSAGE FLAG").equals("DELETED")) {
                fl=new Flags(Flags.Flag.DELETED);
                if(need_expunge_folders == null) {
                    need_expunge_folders=new Vector<String>();
                }
                need_expunge_folders.addElement(folderhash);
            } else if(head.getContent("MESSAGE FLAG").equals("SEEN")) {
                fl=new Flags(Flags.Flag.SEEN);
            } else if(head.getContent("MESSAGE FLAG").equals("RECENT")) {
                fl=new Flags(Flags.Flag.RECENT);
            } else if(head.getContent("MESSAGE FLAG").equals("ANSWERED")) {
                fl=new Flags(Flags.Flag.ANSWERED);
            } else if(head.getContent("MESSAGE FLAG").equals("DRAFT")) {
                fl=new Flags(Flags.Flag.DRAFT);
            }

            boolean value=true;
            if(head.getContent("MARK").equals("UNMARK")) {
                value=false;
            }

            //log.debug("Done!");
            //log.debug("Setting flags...");

            if(user.wantsSetFlags()) {
                if(folder.isOpen() && folder.getMode()==Folder.READ_ONLY) {
                    folder.close(false);
                    folder.open(Folder.READ_WRITE);
                } else if(!folder.isOpen()) {
                    folder.open(Folder.READ_WRITE);
                }
                folder.setFlags(msgs,fl,value);
                if(user.getBoolVar("autoexpunge")) {
                    folder.close(true);
                    if(need_expunge_folders != null) {
                        need_expunge_folders.removeElement(folderhash);
                    }
                } else {
                    folder.close(false);
                }
            }

            folder.open(Folder.READ_WRITE);
            refreshFolderInformation(folderhash);
        }
    }

    /**
     * Copy or move the selected messages from folder fromfolder to folder tofolder.
     */
    public void copyMoveMessage(String fromfolder, String tofolder, HTTPRequestHeader head, boolean move) throws MessagingException {
        Folder from=getFolder(fromfolder);
        Folder to=getFolder(tofolder);
        if(user.wantsSetFlags()) {
            if(from.isOpen() && from.getMode() == Folder.READ_ONLY) {
                from.close(false);
                from.open(Folder.READ_WRITE);
            } else if(!from.isOpen()) {
                from.open(Folder.READ_WRITE);
            }
            if(to.isOpen() && to.getMode() == Folder.READ_ONLY) {
                to.close(false);
                to.open(Folder.READ_WRITE);
            } else if(!to.isOpen()) {
                to.open(Folder.READ_WRITE);
            }
        } else {
            if(!from.isOpen()) {
                from.open(Folder.READ_ONLY);
            }
            if(to.isOpen() && to.getMode() == Folder.READ_ONLY) {
                to.close(false);
                to.open(Folder.READ_WRITE);
            } else if(!to.isOpen()) {
                to.open(Folder.READ_WRITE);
            }
        }
        int m[]=getSelectedMessages(head,from.getMessageCount());
        Message msgs[]=from.getMessages(m);
        from.copyMessages(msgs,to);
        if(move && user.wantsSetFlags()) {
            from.setFlags(m,new Flags(Flags.Flag.DELETED),true);
            if(user.getBoolVar("autoexpunge")) {
                from.close(true);
                to.close(true);
            } else {
                if(need_expunge_folders == null) {
                    need_expunge_folders=new Vector<String>();
                }
                need_expunge_folders.addElement(fromfolder);
                from.close(false);
                to.close(false);
            }
        } else {
            from.close(false);
            if(user.getBoolVar("autoexpunge")) {
                to.close(true);
            } else {
                to.close(false);
            }
        }
        from.open(Folder.READ_WRITE);
        to.open(Folder.READ_WRITE);
        refreshFolderInformation(fromfolder);
        refreshFolderInformation(tofolder);
    }


    /**
     * Change a user's configuration.
     * Header fields given in the requestheader are parsed and turned into user options (probably should not be in WebMailSession
     * but in a plugin or something; this is very hacky).
     */
    public void changeSetup(HTTPRequestHeader head) throws WebMailException {
        Enumeration contentkeys=head.getContentKeys();
        user.resetBoolVars();
        while(contentkeys.hasMoreElements()) {
            String key=((String)contentkeys.nextElement()).toLowerCase();
            if(key.startsWith("intvar")) {
                try {
                    long value=Long.parseLong(head.getContent(key));
                    user.setIntVar(key.substring(7),value);
                } catch(NumberFormatException ex) {
                    log.warn("Remote provided illegal intvar in request header: \n("+key+","+head.getContent(key)+")");
                }
            } else if(key.startsWith("boolvar")) {
                boolean value=head.getContent(key).toUpperCase().equals("ON");
                user.setBoolVar(key.substring(8),value);
            }
        }

        /**
         * As described in line #1088, we have to transcode these strings.
         * We only allow SIGNATURE and FULLNAME to contain locale-specific
         * characters.
         */
        // user.setSignature(head.getContent("SIGNATURE"));
        // user.setFullName(head.getContent("FULLNAME"));
        if(head.isContentSet("ADDNEW")) {
                if(head.getContent("NEWEMAIL").equals("")) {
                        throw new WebMailException("Can not add empty email!");
                } else {
                        user.addEmail(head.getContent("NEWEMAIL"));
                }
        }
        if(head.isContentSet("SETDEFAULT")) {
                user.setDefaultEmail(head.getContent("EMAIL"));
        }
        if(head.isContentSet("DELETEEMAIL")) {
                user.removeEmail(head.getContent("EMAIL"));
        }
        if(!head.isContentSet("ADDNEW") && !head.isContentSet("SETDEFAULT") && !head.isContentSet("DELETEEMAIL")) {
            try {
                user.setSignature(new String(head.getContent("SIGNATURE").getBytes("ISO8859_1"), "UTF-8"));
                user.setFullName(new String(head.getContent("FULLNAME").getBytes("ISO8859_1"), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                log.fatal("Failed to encode to UTF-8?  Continuing", e);
                user.setSignature(head.getContent("SIGNATURE"));
                user.setFullName(head.getContent("FULLNAME"));
            }

            if(!head.getContent("PASSWORD").equals("")) {
                net.wastl.webmail.server.Authenticator auth=parent.getStorage().getAuthenticator();
                if(auth.canChangePassword()) {
                    auth.changePassword(user,head.getContent("PASSWORD"),head.getContent("VERIFY"));
                } else {
                    /*
                    log.fatal("Skipping password change since "
                            + "authenicator doesn't permit it:  FIX THIS!");
                    */
                    throw new InvalidDataException(getStringResource("EX NOCHANGE PASSWORD"));
                    //Can't throw when the HTML we generates sends a non-empty
                    //password when the user types nothing.
                    // TODO:  Fixme
                }
            }
            user.setPreferredLocale(head.getContent("LANGUAGE"));
            user.setTheme(head.getContent("THEME"));
            if(head.isContentSet("SENTFOLDER")) {
                    log.debug("SENTFOLDER="+head.getContent("SENTFOLDER"));
                    user.setSentFolder(head.getContent("SENTFOLDER"));
            }
        }

        // Not sure if this is really necessary:
        //refreshFolderInformation(true);
        setEnv();
        model.update();
    }


    /**
     * Add the mailbox with the given parameters to this user's configuration. Subscribe all folders on startup (the
     * user can later unsubscribe them) and update the model.
     *
     * @param name Name for the mailbox (used for identification within the session)
     * @param protocol The protocol used for this mailbox (most likely IMAP or POP3)
     * @param host The hostname of the host this mailbox lives on
     * @param login Login name the user provided for the host
     * @param password Password the user provided to the given login
     */
    public void addMailbox(String name, String protocol, String host, String login, String password)
        throws MessagingException {
        disconnectAll();
        String host_url=protocol+"://"+host;
        user.addMailHost(name, host_url, login, password, null);
        Enumeration enumVar=user.mailHosts();
        while(enumVar.hasMoreElements()) {
            String id=(String)enumVar.nextElement();
            if(user.getMailHost(id).getName().equals(name)) {
                //setSubscribedAll(id,true);
                setSubscribedDefault(id,true);
                break;
            }
        }
        model.update();
    }

    /**
     * Remove the mailbox with the given name.
     * Will first disconnect all mailboxes, remove the given mailbox and then update the model.
     *
     * @param name Name of the mailbox that is to be removed.
     */
    public void removeMailbox(String name) {
        disconnectAll();
        // TODO:  Fix bug here.
        // When removing a mailbox, the XMLUserModel MAILHOST_MODEL element
        // has definitely been removed right at this point; and this mailhost
        // is not traversed during future refreshFolderInformation(bool,bools)s,
        // yet the node tree for the removed host persist on screens until the
        // user logs out.  Is it somehow cached in the Style Sheet invoked
        // in FolderSetup.handleURL()???
        user.removeMailHost(name);
        model.update();
        // Should be called from FolderSetup Plugin
        //refreshFolderInformation(true);
    }

    public void setAddToFolder(String id) {
        model.setStateVar("add to folder",id);
    }

    public void addFolder(String toid, String name, boolean holds_messages, boolean holds_folders)
        throws MessagingException {
        Folder parent=getFolder(toid);
        Folder folder=parent.getFolder(name);
        if(!folder.exists()) {
            int type=0;
            if(holds_messages) {
                type+=Folder.HOLDS_MESSAGES;
            }
            if(holds_folders) {
                type+=Folder.HOLDS_FOLDERS;
            }
            folder.create(type);
        }
        // Should be called from FolderSetup Plugin
        //refreshFolderInformation();
    }


    public void removeFolder(String id, boolean recurse) throws MessagingException {
        Folder folder=getFolder(id);
        if (folder.isOpen()) folder.close(false);
        folder.delete(recurse);

        // Should be called from FolderSetup Plugin
        //refreshFolderInformation();
    }

    public String getEnv(String key) {
        return "";
    }

    public void setEnv(String key, String value) {
    }

    public void setException(Exception ex) {
        model.setException(ex);
    }

    public void setEnv() {
        // This will soon replace "ENV":
        model.setStateVar("base uri",parent.getBasePath());
        model.setStateVar("img base uri",parent.getImageBasePath()+
                          "/"+user.getPreferredLocale().getLanguage()+
                          "/"+user.getTheme());

        model.setStateVar("webmail version",parent.getVersion());
        model.setStateVar("operating system",System.getProperty("os.name")+" "+
                          System.getProperty("os.version")+"/"+System.getProperty("os.arch"));
        model.setStateVar("java virtual machine",System.getProperty("java.vendor")+" "+
                          System.getProperty("java.vm.name")+" "+System.getProperty("java.version"));

        model.setStateVar("last login",user.getLastLogin());
        model.setStateVar("first login",user.getFirstLogin());
        model.setStateVar("session id",session_code);
        model.setStateVar("date",formatDate(System.currentTimeMillis()));
        model.setStateVar("max attach size",parent.getStorage().getConfig("MAX ATTACH SIZE"));
        model.setStateVar("current attach size",""+attachments_size);

        // Add all languages to the state
        model.removeAllStateVars("language");
        String lang=parent.getConfig("languages");
        StringTokenizer tok=new StringTokenizer(lang," ");
        while(tok.hasMoreTokens()) {
            String t=tok.nextToken();
            model.addStateVar("language",t);
            model.removeAllStateVars("themes_"+t);
            StringTokenizer tok2=new StringTokenizer(parent.getConfig("THEMES_"+t.toUpperCase())," ");
            while(tok2.hasMoreElements()) {
                model.addStateVar("themes_"+t,(String)tok2.nextToken());
            }
        }

        model.removeAllStateVars("protocol");
        Provider[] storeProviders =parent.getStoreProviders();
        for(int i=0; i<storeProviders .length; i++) {
            model.addStateVar("protocol",storeProviders [i].getProtocol());
        }

        model.setStateVar("themeset","themes_"+user.getPreferredLocale().getLanguage().toLowerCase());
    }

    public UserData getUser() {
        return user;
    }

    public String getUserName() {
        return user.getLogin();
    }

    public InetAddress getRemoteAddress() {
        return remote;
    }

    public Map<String, Folder> getActiveConnections() {
        return connections;
    }

    public void setSent(boolean b) {
        sent=b;
    }

    public boolean isSent() {
        return sent;
    }

    private String formatDate(long date) {
        TimeZone tz=TimeZone.getDefault();
        DateFormat df=DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.DEFAULT, getLocale());
        df.setTimeZone(tz);
        String now=df.format(new Date(date));
        return now;
    }

    public void handleTransportException(SendFailedException e) {
        model.setStateVar("send status",e.getNextException().getMessage());
        model.setStateVar("valid sent addresses",Helper.joinAddress(e.getValidSentAddresses()));
        model.setStateVar("valid unsent addresses",Helper.joinAddress(e.getValidUnsentAddresses()));
        model.setStateVar("invalid addresses",Helper.joinAddress(e.getInvalidAddresses()));
        sent=true;
    }
}
