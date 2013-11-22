/*
 * @(#)$Id: XMLUserData.java 116 2008-10-30 06:12:51Z unsaved $
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


package net.wastl.webmail.xml;

import java.text.DateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javax.xml.transform.TransformerException;

import net.wastl.webmail.exceptions.InvalidPasswordException;
import net.wastl.webmail.exceptions.WebMailException;
import net.wastl.webmail.misc.Helper;
import net.wastl.webmail.server.MailHostData;
import net.wastl.webmail.server.UserData;
import net.wastl.webmail.server.WebMailServer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * @author Sebastian Schaffert
 */
public class XMLUserData extends XMLData implements UserData {
    private static Log log = LogFactory.getLog(XMLUserData.class);
    protected long login_time;
    protected boolean logged_in;

    public XMLUserData(Document d) {
        super(d);
        if(data==null) {
            // Should we not throw here so us developers will see and
            // fix the problem? - blaine
            log.error("Data was null ???");
            data=root.createElement("USERDATA");
            root.appendChild(data);
        }
    }

    public void init(String user, String domain, String password) {
        setUserName(user);
        setDomain(domain);
        setFullName(user);
        if(user.indexOf("@")!=-1) {
          // This is a special case when the user already contains the domain
          // e.g. QMail
          setEmail(user);
        } else {
          setEmail(user+"@"+domain);
        }
        try {
            setPassword(password,password);
        } catch(InvalidPasswordException ex) {}

        setPreferredLocale(WebMailServer.getDefaultLocale().toString());
        setTheme(WebMailServer.getDefaultTheme());
        setIntVar("first login",System.currentTimeMillis());
        setIntVar("last login", System.currentTimeMillis());
        setIntVar("login count",0);
        setIntVar("max show messages",20);
        setIntVar("icon size",48);
        setBoolVar("break lines",true);
        setIntVar("max line length",79);
    }

    public String getProperty(String xpath) {
        return getValueXPath(xpath);
    }

    public Document getRoot() {
        return root;
    }

    public Document getDocumentInstance() {
        return root;
    }

    public Element getUserData() {
        return data;
    }

    public DocumentFragment getDocumentFragment() {
        DocumentFragment df=root.createDocumentFragment();
        df.appendChild(data);
        return df;
    }

    /**
     * Create specified element if it doesn't exist already.
     */
    protected void ensureElement(String tag, String attribute, String att_value) {
        /* TODO:  Fix behavior of this method.
         * If it is really an ERROR for element to be missing, then this
         * method should throw if it's missing.
         * If it is not an error, then this method should be a boolean
         * getter, not a validator void method.
         */

        StringBuilder xp_query = new StringBuilder(tag);
        /*  Blaine disables this "//" prefix.
         *  Besides this causing indisputable failures when input tag is
         *  begins with slash, why would we want to use a wildcard when
         *  "ensureing" that a specific element exists?
         * String xp_query="//"+tag;
         */
        if(attribute != null && att_value != null) {
            xp_query.append("[@"+attribute+"='"+att_value+"']");
        } else if(attribute != null) {
            xp_query.append("[@"+attribute+"]");
        }

        try {
            if (getNodeXPath(xp_query.toString()) != null) return;
        } catch (TransformerException te) {
            log.warn("Got an exception instead of a null return from XPath "
                    + "for '" + xp_query.toString() + "'.  Continuing.", te);
            return;
        }

//      NodeList nl=data.getElementsByTagName(tag);
//      boolean flag=false;
//      for(int i=0;nl != null && i<nl.getLength();i++) {
//          Element e=(Element)nl.item(i);
//          if(attribute == null) {
//              // No attribute required
//              flag=true;
//              break;
//          } else if(att_value == null) {
//              if(e.getAttributeNode(attribute) != null) {
//                  // Attribute exists, value is not requested
//                  flag=true;
//                  break;
//              }
//          } else if(e.getAttribute(attribute).equals(att_value)) {
//              flag=true;
//              break;
//          }
//      }
        Element elem=root.createElement(tag);
         if(attribute != null)
            elem.setAttribute(attribute,att_value==null?"":att_value);
        if (tag.equalsIgnoreCase("BOOLVAR")) elem.setAttribute("value", "no");
        data.appendChild(elem);
        invalidateCache();
    }

    public void login() {
        // Increase login count and last login pointer
        //setIntVar("last login",System.currentTimeMillis());
        if(!logged_in) {
            setIntVar("login count",getIntVar("login count")+1);
            login_time=System.currentTimeMillis();
            logged_in=true;
        } else {
            log.error("Trying to log in a second time for user "+getLogin());
        }
    }

    public void logout() {
        if(logged_in) {
            setIntVar("last login",login_time);
            logged_in = false;
        } else {
            log.error("Logging out a user that wasn't logged in.");
        }
    }

    public void addMailHost(String name, String host, String login,
            String password, String imapBaseDir) {
        // First, check whether a mailhost with this name already exists.
        // Delete, if yes.
        try {
            log.debug("Adding mailhost "+name);
            if(getMailHost(name) != null) {
                removeMailHost(name);
            }
            Element mailhost=root.createElement("MAILHOST");
            mailhost.setAttribute("name",name);
            mailhost.setAttribute("id",Long.toHexString(Math.abs(name.hashCode()))+Long.toHexString(System.currentTimeMillis()));

            Element mh_login=root.createElement("MH_LOGIN");
            XMLCommon.setElementTextValue(mh_login,login);
            mailhost.appendChild(mh_login);

            Element mh_pass=root.createElement("MH_PASSWORD");
            XMLCommon.setElementTextValue(mh_pass,Helper.encryptTEA(password));
            mailhost.appendChild(mh_pass);

            Element mh_uri=root.createElement("MH_URI");
            XMLCommon.setElementTextValue(mh_uri,host);
            mailhost.appendChild(mh_uri);

            if (imapBaseDir != null)
                log.fatal("Implement persistence of MH_IMAP_BASEDIR.  "
                        + "Ignoring setting for now");

            data.appendChild(mailhost);
            log.debug("Done with mailhost "+name);
            //XMLCommon.writeXML(root,System.err,"");
            invalidateCache();
        } catch(Exception ex) {
            log.error("Failed to add mailhost.  Just aborting and continuing.",
                    ex);
        }
    }

    public void removeMailHost(String id) {
        Element n = null;
        String xPathString = "/USERDATA/MAILHOST[@id='"+id+"']";
        try {
            n = (Element) getNodeXPath(xPathString);
        } catch (TransformerException te) {
            log.error("Failed to get extract node for XPath '"
                    + xPathString + "'", te);
            XMLCommon.dumpXML(log, xPathString, root);
        }
        if (n == null) return;
        data.removeChild(n);
        invalidateCache();
    }

    public MailHostData getMailHost(String id) {
        //final Element mailhost=XMLCommon.getElementByAttribute(data,"MAILHOST","id",id);
        String xPathString = "/USERDATA/MAILHOST[@id='"+id+"']";
        Element e = null;
        try {
            e = (Element) getNodeXPath(xPathString);
        } catch (TransformerException te) {
            log.error("Failed to get extract node for XPath '"
                    + xPathString + "'.  Continuing with null mailhost.", te);
            XMLCommon.dumpXML(log, xPathString, root);
        }
        final Element mailhost = e;
        return new MailHostData() {
                public String getPassword() {
                    return Helper.decryptTEA(XMLCommon.getValueXPath(mailhost,"MH_PASSWORD/text()"));
                }

                public void setPassword(String s) {
                    XMLCommon.setValueXPath(mailhost,"MH_PASSWORD/text()",Helper.encryptTEA(s));
                }

                public String getLogin() {
                    return XMLCommon.getValueXPath(mailhost,"MH_LOGIN/text()");
                }

                public String getName() {
                    return mailhost.getAttribute("name");
                }

                public void setLogin(String s) {
                    XMLCommon.setValueXPath(mailhost,"MH_LOGIN/text()",s);
                }

                public void setName(String s) {
                    mailhost.setAttribute("name",s);
                }

                public String getHostURL() {
                    return XMLCommon.getValueXPath(mailhost,"MH_URI/text()");
                }

                public void setHostURL(String s) {
                    XMLCommon.setValueXPath(mailhost,"MH_URI/text()",s);
                }

                public String getID() {
                    return mailhost.getAttribute("id");
                }
            };
    }

    public Enumeration<String> mailHosts() {
        final NodeList nl=getNodeListXPath("//MAILHOST");
        return new Enumeration<String>() {
                int i=0;
                public boolean hasMoreElements() {
                    return i<nl.getLength();
                }

                public String nextElement() {
                    Element e=(Element)nl.item(i++);
                    return e.getAttribute("id");
                }
            };
    }

    public int getMaxShowMessages() {
        int retval=(int)getIntVarWrapper("max show messages");
        return retval==0?20:retval;
    }

    public void setMaxShowMessages(int i) {
        setIntVarWrapper("max show messages",i);
    }

    /**
     * As of WebMail 0.7.0 this is different from the username, because it
     * consists of the username and the domain.
     * @see getUserName()
     */
    public String getLogin() {
        return getUserName()+"@"+getDomain();
    }

    public String getFullName() {
        return getValueXPath("/USERDATA/FULL_NAME/text()");
    }
    public void setFullName(String s) {
        setValueXPath("/USERDATA/FULL_NAME/text()",s);
    }

    public String getSignature() {
        return XMLCommon.getTagValue(data,"SIGNATURE");
    }
    public void setSignature(String s) {
        XMLCommon.setTagValue(data,"SIGNATURE",s,true);
    }

    public String getEmail() {
        return getValueXPath("/USERDATA/EMAIL/ADDY/text()");
    }
    public void setEmail(String s) {
        ensureElement("/USERDATA/EMAIL/ADDY", "default", "yes");
        setValueXPath("/USERDATA/EMAIL/ADDY/text()",s);
    }

    public Locale getPreferredLocale() {
        String loc=getValueXPath("/USERDATA/LOCALE/text()");
        StringTokenizer t=new StringTokenizer(loc,"_");
        String language=t.nextToken().toLowerCase();
        String country="";
        if(t.hasMoreTokens()) {
            country=t.nextToken().toUpperCase();
        }
        return new Locale(language,country);
    }

    public void setPreferredLocale(String newloc) {
        setValueXPath("/USERDATA/LOCALE/text()",newloc);
    }


    public void addEmail(String s) {
        Element email = null;
        try {
            email = (Element)getNodeXPath("/USERDATA/EMAIL");
        } catch (TransformerException te) {
            log.error(
                    "Failed to get extract node for XPath '/USERDATA/EMAIL'",
                    te);
            XMLCommon.dumpXML(log, "/USERDATA/EMAIL", root);
        }
        Element addy=root.createElement("ADDY");
        XMLCommon.setElementTextValue(addy,s);
        email.appendChild(addy);
    }

    public void removeEmail(String s)
        throws WebMailException {
        NodeList nl=getNodeListXPath("/USERDATA/EMAIL/ADDY");
        if(nl.getLength()==1) {
                throw new WebMailException("Can not delete last email address!");
        }
        for(int i=0;i<nl.getLength();i++) {
            Element addy=(Element) nl.item(i);
            if(XMLCommon.getElementTextValue(addy).equals(s)) {
                Element email=(Element)addy.getParentNode();
                if(addy.getAttribute("default").equals("yes")) {
                    // Need to set a new default
                    email.removeChild(addy);
                    addy=(Element) nl.item(0);
                    addy.setAttribute("default","yes");
                } else {
                    // Just remove it
                    email.removeChild(addy);
                }
                break;
            }
        }
        invalidateCache();
    }

    /**
     * Does nothing in case of failure.
     */
    public void setDefaultEmail(String s) {
        NodeList nl=getNodeListXPath("/USERDATA/EMAIL/ADDY");
        for(int i=0;i<nl.getLength();i++) {
            Element email=(Element) nl.item(i);
            // Be sure there is no other 'default'
            email.removeAttribute("default");
        }

        Element email = null;
        String xPathString = "/USERDATA/EMAIL/ADDY[./text() = '"+s+"']";
        try {
            email = (Element)getNodeXPath(xPathString);
        } catch (TransformerException te) {
            log.error("Failed to get extract node for XPath '"
                    + xPathString + "'", te);
            XMLCommon.dumpXML(log, xPathString, root);
            return;
        }
        email.setAttribute("default","yes");
        invalidateCache();
    }

    public String getDefaultEmail()
            throws WebMailException {
        // XXX Still buggy, check out why
        String v=getValueXPath("/USERDATA/EMAIL/ADDY[@default='yes']/@value");
        if(v == null) {
            // It should not happen, but take care...
            throw new WebMailException("There is no default email address!");
        } else {
            return v;
        }
    }


    public String getTheme() {
        String retval=getValueXPath("/USERDATA/THEME/text()");
        if(retval.equals("")) {
            return WebMailServer.getDefaultTheme();
        } else {
            return retval;
        }
    }

    public void setTheme(String theme) {
        setValueXPath("/USERDATA/THEME/text()",theme);
    }

    private String formatDate(long date) {
        TimeZone tz=TimeZone.getDefault();
        DateFormat df=DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.DEFAULT, getPreferredLocale());
        df.setTimeZone(tz);
        String now=df.format(new Date(date));
        return now;
    }

    public String getFirstLogin() {
        long date=getIntVarWrapper("first login");
        return formatDate(date);
    }

    public String getLastLogin() {
        long date=getIntVarWrapper("last login");
        return formatDate(date);
    }

    public String getLoginCount() {
        return getIntVarWrapper("login count")+"";
    }

    public boolean checkPassword(String s) {
        String password=getValueXPath("/USERDATA/PASSWORD/text()");
        if(password.startsWith(">")) {
            password=password.substring(1);
        }
        return password.equals(Helper.crypt(password,s));
    }

    public void setPassword(String newpasswd, String verify) throws InvalidPasswordException {
        if(newpasswd.equals(verify)) {
            Random r=new Random();
            // Generate the crypted password; avoid problems with XML parsing
            String crypted=">";
            while(crypted.lastIndexOf(">") >= 0 || crypted.lastIndexOf("<") >= 0) {
                // This has to be some integer between 46 and 127 for the Helper
                // class
                String seed=(char)(r.nextInt(80)+46) + "" + (char)(r.nextInt(80)+46);
                log.debug("Seed: "+seed); // Probably need to comment out
                crypted=Helper.crypt(seed,newpasswd);
            }
            setValueXPath("/USERDATA/PASSWORD/text()",crypted);
        } else {
            throw new InvalidPasswordException("The passwords did not match!");
        }
    }

    public void setPasswordData(String data) {
        setValueXPath("/USERDATA/PASSUSERDATA/text()", data);
    }

    public String getPasswordData() {
        return getValueXPath("/USERDATA/PASSUSERDATA/text()");
    }

    public int getMaxLineLength() {
        int retval=(int)getIntVarWrapper("max line length");
        return retval==0?79:retval;
    }

    public void setMaxLineLength(int i) {
        setIntVarWrapper("max line length",i);
    }

    public boolean wantsBreakLines() {
        return getBoolVarWrapper("break lines");
    }

    public void setBreakLines(boolean b) {
        setBoolVarWrapper("break lines",b);
    }

    public boolean wantsShowImages() {
        return getBoolVarWrapper("show images");
    }

    public void setShowImages(boolean b) {
        setBoolVarWrapper("show images",b);
    }

    public boolean wantsShowFancy() {
        return getBoolVarWrapper("show fancy");
    }
    public void setShowFancy(boolean b) {
        setBoolVarWrapper("show fancy",b);
    }

    public boolean wantsSetFlags() {
        return getBoolVarWrapper("set message flags");
    }
    public void setSetFlags(boolean b) {
        setBoolVarWrapper("set message flags",b);
    }

    public void setSaveSent(boolean b) {
        setBoolVarWrapper("save sent messages",b);
    }
    public boolean wantsSaveSent() {
        return getBoolVarWrapper("save sent messages");
    }
    public String getSentFolder() {
        return getValueXPath("/USERDATA/SENT_FOLDER/text()");
    }
    public void setSentFolder(String s) {
        setValueXPath("/USERDATA/SENT_FOLDER/text()",s);
    }

    public String getDomain() {
        return getValueXPath("/USERDATA/USER_DOMAIN/text()");
    }
    public void setDomain(String s) {
        setValueXPath("/USERDATA/USER_DOMAIN/text()",s);
    }

    /**
     * Return the username without the domain (in contrast to getLogin()).
     * @see getLogin()
     */
    public String getUserName() {
        return getValueXPath("/USERDATA/LOGIN/text()");
    }

    public void setUserName(String s) {
        setValueXPath("/USERDATA/LOGIN/text()",s);
    }

    public void setIntVar(String var, long value) {
        setIntVarWrapper(var,value);
    }

    public long getIntVar(String var) {
        return getIntVarWrapper(var);
    }

    public void setBoolVar(String var, boolean value) {
        setBoolVarWrapper(var,value);
    }

    public boolean getBoolVar(String var) {
        return getBoolVarWrapper(var);
    }

    /**
     * Wrapper method for setting all int vars.
     *
     * Does nothing but log in case of failure.
     */
    protected void setIntVarWrapper(String var, long value) {
        if (getIntVarWrapper(var) == value) return;
        ensureElement("INTVAR","name",var);
        Element e = null;
        String xPathString = "/USERDATA/INTVAR[@name='"+var+"']";
        try {
            e=(Element)getNodeXPath("/USERDATA/INTVAR[@name='"+var+"']");
        } catch (TransformerException te) {
            log.error("Failed to get extract node for XPath '"
                    + xPathString + "'", te);
            XMLCommon.dumpXML(log, xPathString, root);
            return;
        }
        e.setAttribute("value",value+"");
        invalidateCache();
    }

    /**
     * @returns 0 if no element for specified variable.
     */
    protected long getIntVarWrapper(String var) {
        ensureElement("INTVAR","name",var);
        long r=0;
        String xPathString = "/USERDATA/INTVAR[@name='"+var+"']/@value";
        String attValue = null;
        try {
            //r=Long.parseLong(e.getAttribute("value"));
            attValue = getValueXPath(xPathString);
            if (attValue == null) return 0;
            r = Long.parseLong(attValue);
        } catch(NumberFormatException ex) {
            log.warn("Value '" + attValue + "', from XPath '" + xPathString
                    + "' is not a well-formatted integer");
            // Should we not throw here so us developers will see and
            // fix the problem? - blaine
        }
        return r;
    }

    /**
     * Wrapper method for setting all bool vars.
     *
     * Does nothing but log in case of failure.
     */
    protected void setBoolVarWrapper(String var, boolean value) {
        if (getBoolVarWrapper(var) == value) return;
        ensureElement("BOOLVAR","name",var);
        String xPathString = "/USERDATA/BOOLVAR[@name='"+var+"']";
        Element e = null;
        try {
            e=(Element)getNodeXPath(xPathString);
        } catch (TransformerException te) {
            log.error("Failed to get extract node for XPath '"
                    + xPathString + "'.  Ignoring Boolean set request", te);
            XMLCommon.dumpXML(log, xPathString, root);
            return;
        }
        e.setAttribute("value",value?"yes":"no");
        invalidateCache();
    }

    protected boolean getBoolVarWrapper(String var) {
        ensureElement("BOOLVAR","name",var);
        String value = getValueXPath("/USERDATA/BOOLVAR[@name='"+var+"']/@value");
        return (value.toUpperCase().equals("YES") || value.toUpperCase().equals("TRUE"));
    }

    /**
     * Set all boolvars to "false".
     */
    public void resetBoolVars() {
        NodeList nl=getNodeListXPath("/USERDATA/BOOLVAR");
        for(int i=0;i<nl.getLength();i++) {
            Element elem=(Element)nl.item(i);
            elem.setAttribute("value","no");
        }
        invalidateCache();
    }
}
