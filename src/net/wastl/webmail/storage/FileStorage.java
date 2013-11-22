/*
 * @(#)$Id: FileStorage.java 136 2008-10-31 21:14:28Z unsaved $
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


package net.wastl.webmail.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.Collator;
import java.text.DateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;

import javax.servlet.UnavailableException;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import net.wastl.webmail.config.ConfigurationListener;
import net.wastl.webmail.exceptions.BinaryNotFoundException;
import net.wastl.webmail.exceptions.StylesheetNotFoundException;
import net.wastl.webmail.exceptions.WebMailException;
import net.wastl.webmail.misc.AttributedExpireableCache;
import net.wastl.webmail.misc.ByteStore;
import net.wastl.webmail.misc.ExpireableCache;
import net.wastl.webmail.server.Authenticator;
import net.wastl.webmail.server.Storage;
import net.wastl.webmail.server.WebMailServer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This is the FileStorage class is common to all other storage classes in WebMail
 * It provides means of getting and storing data in ZIPFiles and ResourceBundles,
 * for example HTML-templates, binary files and MIME-types
 *
 * @see Storage
 * @author Sebastian Schaffert
 * @versin $Revision: 136 $
 */
public abstract class FileStorage extends Storage implements ConfigurationListener {
    private static Log log = LogFactory.getLog(FileStorage.class);

    protected Map<String, ResourceBundle> resources;

    protected Map<String, AttributedExpireableCache> stylesheet_cache;
    protected Map<String, AttributedExpireableCache> binary_cache;

    /** Stores Locale/ExpireableCache pairs */
    //protected Hashtable file_cache;;

    protected Authenticator auth;

    protected static Map<String,String> mime_types;

    protected static DateFormat df=null;

    private boolean init_complete=false;

    protected int file_cache_size=40;

    /**
     * Initialize SimpleStorage.
     * Fetch Configuration Information etc.
     */
    public FileStorage(WebMailServer parent)
        throws UnavailableException {
        super(parent);

        initConfig();

        cs.addConfigurationListener("AUTH",this);
        String mimetypesSetting = parent.getProperty(
                "webmail.mimetypes.filepath");
        if (mimetypesSetting != null)
            cs.configRegisterStringKey(this, "MIME TYPES", mimetypesSetting,
                   "File with mime type information.");
        cs.configRegisterYesNoKey("SHOW ADVERTISEMENTS","Whether or not to include the WebMail advertisement "+
                                  "messages in default user signatures and HTTP response headers");
        cs.setDefaultValue("SHOW ADVERTISEMENTS","NO");
        cs.configRegisterStringKey("ADVERTISEMENT MESSAGE","JWebMail "+parent.getVersion()+" WWW to Mail Gateway", "Advertisement to attach to user signatures");

        resources = new Hashtable<String, ResourceBundle>();

        initCache();

        // Now included in configuration:
//      initVirtualDomains();

        initMIME();

        initAuth();

        initLanguages();

        init_complete=true;
    }

    /**
     * initialize XMLSystemData sysdata
     */
    protected abstract void initConfig() throws UnavailableException;

    protected void initCache() {
        // Initialize the file cache
        cs.configRegisterIntegerKey(this,"CACHE SIZE FILE","40","Size for the file system cache for every locale");
        try {
            file_cache_size=Integer.parseInt("CACHE SIZE FILE");
        } catch(NumberFormatException e) {}

        // Now the same for the stylesheet cache
        if(stylesheet_cache == null)
            stylesheet_cache = new Hashtable<String, AttributedExpireableCache>(10);
        for (ExpireableCache eCache : stylesheet_cache.values())
            eCache.setCapacity(file_cache_size);

        // And for binary files
        if(binary_cache == null)
            binary_cache = new Hashtable<String, AttributedExpireableCache>(10);
        for (ExpireableCache eCache : binary_cache.values())
            eCache.setCapacity(file_cache_size);
    }

    protected void initAuth() {
        log.info("Authenticator ... ");
        Authenticator a=parent.getAuthenticatorHandler().getAuthenticator(getConfig("AUTH"));
        if(a!=null) {
            // IMAP level authentication
            auth=a;
            auth.init(this);
            log.info("ok. Using "+auth.getClass().getName()+" (v"+auth.getVersion()+") for authentication.");
        } else {
            log.fatal("Could not initalize any authenticator. Users will not be able to log on.");
            auth=null;
        }
    }


    protected void initMIME() {
        log.info("Initializing MIME types ... ");
        if(getConfig("mime types") == null) {
            log.warn("Mime Types not configured. Will use standard MIME types.");
            return;
        }
        File f=new File(getConfig("mime types"));
        if (!f.canRead()) {
            log.warn("Could not find "+getConfig("mime types")
                    + ". Will use standard MIME types.");
            return;
        }
        BufferedReader in = null;
        try {
            mime_types=new Hashtable<String, String>();
            in=new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String line=in.readLine();
            while(line != null) {
                if(!line.startsWith("#")) {
                    StringTokenizer tok=new StringTokenizer(line);
                    if(tok.hasMoreTokens()) {
                        String type=tok.nextToken();
                        while(tok.hasMoreTokens()) {
                            String key=tok.nextToken();
                            mime_types.put(key,type);
                            log.debug(key+" -> "+type);
                        }
                    }
                }
                line=in.readLine();
            }
            log.info("Mime Types loaded from "+getConfig("mime types")+".");
        } catch(IOException ex) {
            log.error("Could not find "+getConfig("mime types")+". Will use standard MIME types.");
        } finally {
            if (in != null) try {
                in.close();
            } catch (IOException ioe) {
                log.error("Failed to close mime types file '"
                        + f.getAbsolutePath() + "'");
            }
        }
    }

    protected void initLanguages() {
        log.info("Initializing available languages ... ");
        File f=new File(parent.getProperty("webmail.template.path")+System.getProperty("file.separator"));
        String[] flist=f.list(new FilenameFilter() {
                public boolean accept(File myf, String s) {
                    if(myf.isDirectory() && s.equals(s.toLowerCase()) && (s.length()==2 || s.equals("default"))) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });

        File cached=new File(parent.getProperty("webmail.data.path")+System.getProperty("file.separator")+"locales.cache");
        Locale[] available1=null;

        /* Now we try to cache the Locale list since it takes really long to gather it! */
        boolean exists=cached.exists();
        if(exists) {
            try {
                ObjectInputStream in=new ObjectInputStream(new FileInputStream(cached));
                available1=(Locale[])in.readObject();
                in.close();
                log.info("Using disk cache for langs... ");
            } catch(Exception ex) {
                exists=false;
            }
        }
        if(!exists) {
            // We should cache this on disk since it is so slow!
            available1=Collator.getAvailableLocales();
            ObjectOutputStream os = null;
            try {
                os=new ObjectOutputStream(new FileOutputStream(cached));
                os.writeObject(available1);
            } catch(IOException ioe) {
                log.error("Failed to write to storage", ioe);
            } finally {
                try {
                    os.close();
                } catch(IOException ioe) {
                    log.error("Failed to close stream", ioe);
                }
            }
        }

        // Do this manually, as it is not JDK 1.1 compatible ...
        //Vector available=new Vector(Arrays.asList(available1));
        Vector<Locale> available = new Vector<Locale>(available1.length);
        for(int i=0; i<available1.length; i++) {
            available.addElement(available1[i]);
        }
        String s="";
        int count=0;
        for(int i=0;i<flist.length;i++) {
            String cur_lang=flist[i];
            Locale loc=new Locale(cur_lang,"","");
            Enumeration<Locale> enumVar=available.elements();
            boolean added=false;
            while(enumVar.hasMoreElements()) {
                Locale l=(Locale)enumVar.nextElement();
                if(l.getLanguage().equals(loc.getLanguage())) {
                    s+=l.toString()+" ";
                    count++;
                    added=true;
                }
            }
            if(!added) {
                s+=loc.toString()+" ";
                count++;
            }
        }
        log.info(count+" languages initialized.");
        cs.configRegisterStringKey(this,"LANGUAGES",s,"Languages available in WebMail");
        setConfig("LANGUAGES",s);

        /*
          Setup list of themes for each language
        */
        for(int j=0;j<flist.length;j++) {
            File themes=new File(parent.getProperty("webmail.template.path")+System.getProperty("file.separator")
                                 +flist[j]+System.getProperty("file.separator"));
            String[] themelist=themes.list(new FilenameFilter() {
                    public boolean accept(File myf, String s3) {
                        if(myf.isDirectory() && !s3.equals("CVS")) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
            String s2="";
            for(int k=0;k<themelist.length;k++) {
                s2+=themelist[k]+" ";
            }
            cs.configRegisterStringKey(this,"THEMES_"+flist[j].toUpperCase(),s2,"Themes for language "+flist[j]);
            setConfig("THEMES_"+flist[j].toUpperCase(),s2);
        }
    }

    /**
     * Get the String for key and the specified locale.
     * @param key Identifier for the String
     * @param locale locale of the String to fetch
     */
    public String getStringResource(String key, Locale locale) {
        if(resources.get(locale.getLanguage()) != null) {
                String s = resources.get(locale.getLanguage()).getString(key);
            return resources.get(locale.getLanguage()).getString(key);
        } else {
            try {
                // ResourceBundle rc=XMLResourceBundle.getBundle("resources",locale,null);
                log.info("Loading locale");
                ResourceBundle rc = ResourceBundle.getBundle("org.bulbul.webmail.xmlresource.Resources", locale);
                resources.put(locale.getLanguage(),rc);
                return rc.getString(key);
            } catch(Exception e) {
                log.error((e.getMessage().indexOf("NOCHANGE PASSWORD") > 0)
                        ?  ("FIXME!  Looks like user submitted wrong password\n"
                                + "Need to catch this problem where can handle "
                                + "it appropriately")
                        : ("Failed to load resource bundle '"
                                + "org.bulbul.webmail.xmlresource.Resources' "
                                + "for locale '" + locale
                                + "'.  Continuing without handling?"), e);
                /* Terrible error handling to pretend to user that there
                 * was no problem.
                 * Returning empty string here (in some if not all cases)
                 * results in a future call failing and dumping a stack trace
                 * in the user's face. */
                return "";
            }
        }
    }


    /**
     * Return the requested Stylesheet, precompiled and fitting to the locale and theme
     */
    public Templates getStylesheet(String name, Locale locale, String theme) throws WebMailException {
        String key = locale.getLanguage()+"/"+theme;

        AttributedExpireableCache cache = stylesheet_cache.get(key);

        if(cache == null) {
            cache = new AttributedExpireableCache(file_cache_size);
            stylesheet_cache.put(key,cache);
        }

        Templates stylesheet=null;

        String basepath=getBasePath(locale,theme);

        File f=new File(basepath+name);
        if(!f.exists()) {
            throw new StylesheetNotFoundException("The requested stylesheet "+name+" could not be found (path tried: "+basepath+".");
        }

        if(cache.get(name) != null && ((Long)cache.getAttributes(name)).longValue() >= f.lastModified()) {
            // Keep statistics :-)
            cache.hit();
            return (Templates)cache.get(name);
        } else {
            try {
                StreamSource msg_xsl=new StreamSource("file://"+basepath+name);
                TransformerFactory factory=TransformerFactory.newInstance();
                stylesheet=factory.newTemplates(msg_xsl);
                cache.put(name,stylesheet, new Long(f.lastModified()));
                cache.miss();
            } catch(Exception ex) {
                //log.error("Error while compiling stylesheet "+name+", language="+locale.getLanguage()+", theme="+theme+".");
                throw new WebMailException("Error while compiling stylesheet "+name+", language="+locale.getLanguage()+", theme="+theme+":\n"+ex.toString());
            }
            return stylesheet;
        }
    }

    /**
     * Get a binary file for the specified locale.
     * @param key Identifier for the String
     * @param locale locale of the String to fetch
     */
    public synchronized byte[] getBinaryFile(String name, Locale locale, String theme) throws BinaryNotFoundException {
        String key = locale.getLanguage()+"/"+theme;

        AttributedExpireableCache cache = binary_cache.get(key);

        if (cache == null) {
            cache=new AttributedExpireableCache(file_cache_size);
            binary_cache.put(key,cache);
        }

        ByteStore bs=null;


        String basepath=getBasePath(locale,theme);
        File f=new File(basepath+name);
        if(!f.exists()) {
            throw new BinaryNotFoundException("The file "+name+" could not be found!");
        }

        if(cache.get(name) != null && ((Long)cache.getAttributes(name)).longValue() >= f.lastModified()) {
            // Keep statistics :-)
            cache.hit();
            return ((ByteStore)cache.get(name)).getBytes();
        } else {
            try {
                bs=ByteStore.getBinaryFromIS(new FileInputStream(f),(int)f.length());
            } catch(IOException ex) {
                log.error("Failed to get load bytes from file '"
                        + f.getAbsolutePath() + "'", ex);
            }
            cache.put(name,bs,new Long(f.lastModified()));

            if(bs != null) {
                return bs.getBytes();
            } else {
                return new byte[1];
            }
        }
    }


    public Authenticator getAuthenticator() {
        return auth;
    }

    protected String formatDate(long date) {
        if(df==null) {
            TimeZone tz=TimeZone.getDefault();
            df=DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.DEFAULT, Locale.getDefault());
            df.setTimeZone(tz);
        }
        String now=df.format(new Date(date));
        return now;
    }

    public void shutdown() {
    }


    public String getMimeType(String name) {
        if (mime_types == null) return super.getMimeType(name);
        if (name == null) return "UNKNOWN";
        String type="application/unknown";
        for (Map.Entry<String, String> e : mime_types.entrySet())
            if (name.toLowerCase().endsWith(e.getKey())) return e.getValue();
        return type;
    }

    public void notifyConfigurationChange(String key) {
        log.debug("FileStorage: Configuration change notify for key "+key+".");
        if(key.toUpperCase().startsWith("AUTH")) {
            initAuth();
        } else if(key.toUpperCase().startsWith("MIME")) {
            initMIME();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        ExpireableCache cache;
        for (Map.Entry<String, AttributedExpireableCache> e :
                stylesheet_cache.entrySet()) {
            cache = e.getValue();
            sb.append(" - stylesheet cache for " + e.getKey() + ": Capacity "
                    + cache.getCapacity() +", Usage " + cache.getUsage()
                    + ", " + cache.getHits() + " hits, " + cache.getMisses()
                    + " misses\n");
        }
        for (Map.Entry<String, AttributedExpireableCache> e :
                binary_cache.entrySet()) {
            cache = e.getValue();
            sb.append(" - binary cache for " + e.getKey() + ": Capacity "
                    + cache.getCapacity() + ", Usage " + cache.getUsage()
                    + ", " + cache.getHits() + " hits, " + cache.getMisses()
                    +" misses\n");
        }
        return sb.toString();
    }
}
