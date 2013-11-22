/*
 * @(#)$Id: XMLSystemData.java 116 2008-10-30 06:12:51Z unsaved $
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

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import net.wastl.webmail.config.ChoiceConfigParameter;
import net.wastl.webmail.config.ConfigParameter;
import net.wastl.webmail.config.ConfigScheme;
import net.wastl.webmail.config.ConfigStore;
import net.wastl.webmail.server.WebMailVirtualDomain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class represents methods for accessing WebMail's system configuration in a
 * XML tree.
 *
 * @author Sebastian Schaffert
 */
public class XMLSystemData extends ConfigStore {
    private static Log log = LogFactory.getLog(ConfigStore.class);

    protected Document root;

    protected Element sysdata;

    /* Save the time when this document has been loaded. Might be used to reload
       a document with a higher modification time
    */
    protected long loadtime;

    public XMLSystemData(Document d, ConfigScheme cs) {
        super(cs);
        root=d;
        sysdata=root.getDocumentElement();
        if(sysdata==null) {
            sysdata=root.createElement("SYSDATA");
            root.appendChild(sysdata);
        }
        loadtime=System.currentTimeMillis();
    }

    public long getLoadTime() {
        return loadtime;
    }

    public void setLoadTime(long time) {
        loadtime=time;
    }

    public Document getRoot() {
        return root;
    }

    public Element getSysData() {
        return sysdata;
    }

    public DocumentFragment getDocumentFragment() {
        DocumentFragment df=root.createDocumentFragment();
        df.appendChild(sysdata);
        return df;
    }

    protected String getConfigRaw(String key) {
        NodeList nl=sysdata.getElementsByTagName("KEY");
        for(int i=0;i<nl.getLength();i++) {
            Element e=(Element)nl.item(i);
            if(XMLCommon.getElementTextValue(e).equals(key)) {
                Element p=(Element)e.getParentNode();
                NodeList valuel=p.getElementsByTagName("VALUE");
                if(valuel.getLength()>=0) {
                    return XMLCommon.getElementTextValue((Element)valuel.item(0));
                }
            }
        }
        return null;
    }

    public void setConfigRaw(String groupname,String key, String value, String type) {
        String curval=getConfigRaw(key);
        if(curval == null || !curval.equals(value)) {
            log.debug("XMLSystemData: "+groupname+"/"+key+" = "+value);
            /* Find all GROUP elements */
            NodeList groupl=sysdata.getElementsByTagName("GROUP");
            int i=0;
            for(i=0; i<groupl.getLength();i++) {
                Element group=(Element)groupl.item(i);
                if(group.getAttribute("name").equals(groupname)) {
                    /* If the group name matches, find all keys */
                    NodeList keyl=group.getElementsByTagName("KEY");
                    int j=0;
                    for(j=0;j<keyl.getLength();j++) {
                        Element keyelem=(Element)keyl.item(j);
                        if(key.equals(XMLCommon.getElementTextValue(keyelem))) {
                            /* If the key already exists, replace it */
                            Element conf=(Element)keyelem.getParentNode();
                            group.replaceChild(createConfigElement(key,value,type),conf);
                            return;
                        }
                    }
                    /* If the key was not found, append it */
                    if(j>=keyl.getLength()) {
                        group.appendChild(createConfigElement(key,value,type));
                        return;
                    }
                }
            }
            if(i>=groupl.getLength()) {
                Element group=createConfigGroup(groupname);
                group.appendChild(createConfigElement(key,value,type));
            }
        }
    }

    protected Element createConfigGroup(String groupname) {
        Element group=root.createElement("GROUP");
        group.setAttribute("name",groupname);
        sysdata.appendChild(group);
        return group;
    }

    protected void deleteConfigGroup(String groupname) {
        NodeList nl=sysdata.getElementsByTagName("GROUP");
        for(int i=0;i<nl.getLength();i++) {
            if(((Element)nl.item(i)).getAttribute("name").equals(groupname)) {
                sysdata.removeChild(nl.item(i));
            }
        }
    }

    protected Element getConfigElementByKey(String key) {
        NodeList nl=sysdata.getElementsByTagName("KEY");

        Element config=null;
        for(int i=0;i<nl.getLength();i++) {
            Element keyelem=(Element)nl.item(i);
            Element parent=(Element)keyelem.getParentNode();
            if(XMLCommon.getElementTextValue(keyelem).equals(key) &&
               parent.getTagName().equals("CONFIG")) {
                config=parent;
                break;
            }
        }
        return config;
    }

    public void initChoices() {
        for (String configKey : getConfigKeys())
            initChoices(configKey);
    }

    public void initChoices(String key) {
        Element config=getConfigElementByKey(key);

        XMLCommon.genericRemoveAll(config,"CHOICE");


        ConfigParameter param=scheme.getConfigParameter(key);
        if(param instanceof ChoiceConfigParameter) {
            Enumeration enumVar=((ChoiceConfigParameter)param).choices();
            while(enumVar.hasMoreElements()) {
                Element choice=root.createElement("CHOICE");
                choice.appendChild(root.createTextNode((String)enumVar.nextElement()));
                config.appendChild(choice);
            }
        }
    }

    protected Element createConfigElement(String key, String value, String type) {
        Element config=root.createElement("CONFIG");
        Element keyelem=root.createElement("KEY");
        Element desc=root.createElement("DESCRIPTION");
        Element valueelem=root.createElement("VALUE");
        keyelem.appendChild(root.createTextNode(key));
        desc.appendChild(root.createTextNode(scheme.getDescription(key)));
        valueelem.appendChild(root.createTextNode(value));
        config.appendChild(keyelem);
        config.appendChild(desc);
        config.appendChild(valueelem);
        config.setAttribute("type",type);
        ConfigParameter param=scheme.getConfigParameter(key);
        if(param instanceof ChoiceConfigParameter) {
            Enumeration enumVar=((ChoiceConfigParameter)param).choices();
            while(enumVar.hasMoreElements()) {
                Element choice=root.createElement("CHOICE");
                choice.appendChild(root.createTextNode((String)enumVar.nextElement()));
                config.appendChild(choice);
            }
        }
        return config;
    }

    public boolean getVirtuals() {
            final NodeList nl=sysdata.getElementsByTagName("VIRTUALS");
            Element virtuals=(Element) nl.item(0);
            if(virtuals != null &&
               virtuals.getAttribute("enabled")!=null &&
               virtuals.getAttribute("enabled").equals("true")) {
                    return true;
            } else {
                    return false;
            }
    }
    public void setVirtuals(boolean state) {
            NodeList nl=sysdata.getElementsByTagName("VIRTUALS");
            Element virtuals=(Element) nl.item(0);
            if(state==false) {
                    virtuals.setAttribute("enabled","false");
            } else {
                    virtuals.setAttribute("enabled","true");
            }
    }

    public Enumeration getVirtualDomains() {
        final NodeList nl=sysdata.getElementsByTagName("DOMAIN");
        return new Enumeration() {
                int i=0;

                public boolean hasMoreElements() {
                    return i<nl.getLength();
                }

                public Object nextElement() {
                    Element elem=(Element)nl.item(i++);
                    String value=XMLCommon.getTagValue(elem,"NAME");
                    return value==null?"unknown"+(i-1):value;
                }
            };
    }

    public WebMailVirtualDomain getVirtualDomain(String domname) {
        // Check if virtual domains are disabled
        if(getVirtuals()==false) {
            // No, default to localhost
            return new WebMailVirtualDomain() {
                    public String getDomainName() {
                        return "localhost";
                    }
                    public void setDomainName(String name) throws Exception {
                        log.error("Ignoring DefaultDomain.setDomainName().  "
                                + "Should not call this method.");
                    }

                    public String getDefaultServer() {
                        return "localhost";
                    }

                    public void setDefaultServer(String name) {
                        log.error("Ignoring DefaultDomain.setDomainServer().  "
                                + "Should not call this method.");
                    }

                    public String getAuthenticationHost() {
                        return "localhost";
                    }

                    public void setAuthenticationHost(String name) {
                        log.error(
                            "Ignoring DefaultDomain.setAuthenticationHost().  "
                                + "Should not call this method.");
                    }

                    public boolean isAllowedHost(String host) {
                            return true;
                    }

                    public void setAllowedHosts(String hosts) {
                        log.error("Ignoring DefaultDomain.setAllowedHosts().  "
                                + "Should not call this method.");
                    }

                    public Enumeration<String> getAllowedHosts() {
                        return new Enumeration<String>() {
                                int i=0;
                                public boolean hasMoreElements() {
                                    return i<1;
                                }

                                public String nextElement() {
                                    i++;
                                    return "localhost";
                                }
                        };
                    }

                    public void setHostsRestricted(boolean b) {
                        log.error(
                                "Ignoring DefaultDomain.setHostsRestricted().  "
                                + "Should not call this method.");
                    }

                    public boolean getHostsRestricted() {
                        return false;
                    }

                    public String getImapBasedir() {
                        return null;
                    }
            };
        }
        // Virtual domains are allowed, get that wanted one
        NodeList nodel=sysdata.getElementsByTagName("DOMAIN");
        Element elem=null;
        int j;
        for(j=0;j<nodel.getLength();j++) {
            elem=(Element)nodel.item(j);
            elem.normalize();
            NodeList namel=elem.getElementsByTagName("NAME");
            if(namel.getLength()>0) {
                if(XMLCommon.getElementTextValue((Element)namel.item(0)).equals(domname)) {
                    break;
                }
            }
        }
        if(j<nodel.getLength() && elem != null) {
            final Element domain=elem;
            return new WebMailVirtualDomain() {
                    public String getDomainName() {
                        String value=XMLCommon.getTagValue(domain,"NAME");
                        return value==null?"unknown":value;
                    }
                    public void setDomainName(String name) throws Exception {
                        XMLCommon.setTagValue(domain,"NAME",name,true,"Virtual Domain names must be unique!");
                    }

                    public String getDefaultServer() {
                        String value=XMLCommon.getTagValue(domain,"DEFAULT_HOST");
                        return value==null?"unknown":value;
                    }

                    /* Override the IMAP base directory for this domain,
                     * for imap and imaps protocols */
                    public String getImapBaseDir() {
                        String value=XMLCommon.getTagValue(domain,"IMAP_BASEDIR");
                        return value==null?"unknown":value;
                    }

                    public void setDefaultServer(String name) {
                        XMLCommon.setTagValue(domain,"DEFAULT_HOST",name);
                    }

                    public String getAuthenticationHost() {
                        String value=XMLCommon.getTagValue(domain,"AUTHENTICATION_HOST");
                        return value==null?"unknown":value;
                    }

                    public void setAuthenticationHost(String name) {
                        XMLCommon.setTagValue(domain,"AUTHENTICATION_HOST",name);
                    }

                    public boolean isAllowedHost(String host) {
                        if(getHostsRestricted()) {
                            Vector<String> v = new Vector<String>();
                            v.addElement(getDefaultServer());
                            Enumeration<String> e=getAllowedHosts();
                            while(e.hasMoreElements()) {
                                v.addElement(e.nextElement());
                            }
                            Enumeration<String> enumVar = v.elements();
                            while (enumVar.hasMoreElements()) {
                                String next = enumVar.nextElement();
                                if(host.toUpperCase().endsWith(next.toUpperCase())) {
                                    return true;
                                }
                            }
                            return false;
                        } else {
                            return true;
                        }
                    }

                    public void setAllowedHosts(String hosts) {
                        NodeList nl=domain.getElementsByTagName("ALLOWED_HOST");
                        for(int i=0;i<nl.getLength();i++) {
                            domain.removeChild(nl.item(i));
                        }
                        StringTokenizer tok=new StringTokenizer(hosts,", ");
                        while(tok.hasMoreElements()) {
                            Element ahost=root.createElement("ALLOWED_HOST");
                            XMLCommon.setElementTextValue(ahost,tok.nextToken());
                            domain.appendChild(ahost);
                        }
                    }

                    public Enumeration<String> getAllowedHosts() {
                        final NodeList nl=domain.getElementsByTagName("ALLOWED_HOST");
                        return new Enumeration<String>() {
                            int i=0;
                            public boolean hasMoreElements() {
                                return i<nl.getLength();
                            }

                            public String nextElement() {
                                String value=XMLCommon.getElementTextValue((Element)nl.item(i++));
                                return value==null?"error":value;
                            }
                        };
                    }

                    public void setHostsRestricted(boolean b) {
                        NodeList nl=domain.getElementsByTagName("RESTRICTED");
                        for(int i=0;i<nl.getLength();i++) {
                            domain.removeChild(nl.item(i));
                        }
                        if(b) {
                            domain.appendChild(root.createElement("RESTRICTED"));
                        }
                    }

                    public boolean getHostsRestricted() {
                        NodeList nl=domain.getElementsByTagName("RESTRICTED");
                        return nl.getLength()>0;
                    }

                    public String getImapBasedir() {
                        NodeList nl=domain.getElementsByTagName("IMAP_BASEDIR");
                        return ((nl.getLength() > 0) ?
                                XMLCommon.getElementTextValue(
                                        (Element) nl.item(0)) : null);
                    }
                };
        } else {
            return null;
        }
    }

    /**
     * This is just completely useless, since you can change virtual domains directly.
     * It should be removed ASAP
     */
    public void setVirtualDomain(String name,WebMailVirtualDomain domain) {
        log.fatal("Called useless net.wastl.webmail.xml.XMLSystemData::setVirtualDomain/2");
        // TODO: Throw here, so we will be confident this is not used
    }

    public void deleteVirtualDomain(String name) {
        NodeList nl=sysdata.getElementsByTagName("NAME");
        for(int i=0;i<nl.getLength();i++) {
            if(nl.item(i).getParentNode().getNodeName().equals("DOMAIN") &&
               XMLCommon.getElementTextValue((Element)nl.item(i)).equals(name)) {
                sysdata.removeChild(nl.item(i).getParentNode());
            }
        }
        log.info("XMLSystemData: Deleted WebMail virtual domain "+name);
    }

    public void createVirtualDomain(String name) throws Exception {
        WebMailVirtualDomain dom=getVirtualDomain(name);
        if(dom!=null) {
            throw new Exception("Domain names must be unique!");
        }
        Element domain=root.createElement("DOMAIN");
        sysdata.appendChild(domain);
        domain.appendChild(root.createElement("NAME"));
        domain.appendChild(root.createElement("DEFAULT_HOST"));
        domain.appendChild(root.createElement("AUTHENTICATION_HOST"));
        domain.appendChild(root.createElement("ALLOWED_HOST"));
        XMLCommon.setTagValue(domain,"NAME",name);
        XMLCommon.setTagValue(domain,"DEFAULT_HOST","localhost");
        XMLCommon.setTagValue(domain,"AUTHENTICATION_HOST","localhost");
        XMLCommon.setTagValue(domain,"ALLOWED_HOST","localhost");
        log.info("XMLSystemData: Created WebMail virtual domain "+name);
    }
}
