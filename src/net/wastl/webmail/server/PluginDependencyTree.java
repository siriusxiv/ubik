/*
 * @(#)$Id: PluginDependencyTree.java 116 2008-10-30 06:12:51Z unsaved $
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
import java.util.Vector;

/**
 * @author Sebastian Schaffert
 */
public class PluginDependencyTree {
    protected Plugin node;
    protected String meprovides;

    protected Vector<PluginDependencyTree> children;

    public PluginDependencyTree(Plugin p) {
        this.node=p;
        this.meprovides=p.provides();
        children = new Vector<PluginDependencyTree>();
    }

    public PluginDependencyTree(String s) {
        this.node=null;
        this.meprovides=s;
        children = new Vector<PluginDependencyTree>();
    }

    public boolean provides(String s) {
        return s.equals(meprovides);
    }

    public String provides() {
        StringBuilder sb = new StringBuilder(meprovides);
        Enumeration<PluginDependencyTree> e=children.elements();
        while(e.hasMoreElements())
            sb.append(","
                    + ((PluginDependencyTree) e.nextElement()).provides());
        return sb.toString();
    }


    public boolean addPlugin(Plugin p) {
        if(p.requires().equals(meprovides)) {
            children.addElement(new PluginDependencyTree(p));
            return true;
        } else {
            boolean flag=false;
            Enumeration<PluginDependencyTree> e=children.elements();
            while(e.hasMoreElements()) {
                PluginDependencyTree pt = e.nextElement();
                flag = flag || pt.addPlugin(p);
            }
            return flag;
        }
    }


    public void register(WebMailServer parent) {
        if(node!=null) {
            //log.debugnode.getName()+" ");
            node.register(parent);
        }

        /* Perform depth-first registraion. Breadth-first would be better, but
           it will work anyway */
        Enumeration<PluginDependencyTree> e=children.elements();
        while(e.hasMoreElements()) {
            PluginDependencyTree p = e.nextElement();
            p.register(parent);
        }
    }
}
