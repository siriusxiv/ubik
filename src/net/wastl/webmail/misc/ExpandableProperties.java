/*
 * @(#)$Id: ExpandableProperties.java 117 2008-10-30 06:32:49Z unsaved $
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


package net.wastl.webmail.misc;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpandableProperties extends Properties {
    static final long serialVersionUID = -6501669686410348173L;

    Pattern propVarPattern = Pattern.compile("\\Q${\\E([^}]+?)\\Q}");
    private boolean permitUnset = false;
    private boolean namesToo = false;
    private boolean sysToo = true;

    /*
    static public void main(String[] sa) {
        ExpandableProperties p = new ExpandableProperties();
        Properties e = new Properties();
        //e.setProperty("user.home", "Milo");
        p.setProperty("1", "one");
        p.setProperty("x${user.name}y", "two${user.home}four");
        p.setProperty("3", "three");
        p.expand(e, false, false, true);
        System.out.println(p.toString());
    }
    */

    /**
     * JavaBeans style expander.  Uses JavaBean properties for options.
     */
    public void expand(Properties exp) {
        expand(exp, permitUnset, namesToo, sysToo);
    }

    /**
     * @param exp Data for name/vals to be substituted
     * @param permitUnset  Instead of throw, just do not substitute if
     *                 given var key is not defined.
     * @param namesToo  Whether to expand prop names in addition to Values.
     * @param sysToo  Whether to substitute for System Prop values.
     */
    public void expand(Properties inExp, boolean permitUnset,
            boolean namesToo, boolean sysToo) {
        Properties exp = (sysToo ? (new Properties(System.getProperties()))
                                 : (new Properties()));
        exp.putAll(inExp);
        Properties additions = new Properties();
        Set<String> zaps = new HashSet<String>(); // keys to remove

        Enumeration e = propertyNames();
        String pk, pv;
        String newKey, newVal;
        Matcher m;
        while (e.hasMoreElements()) {
            pk = (String) e.nextElement();
            pv = getProperty(pk);
            newKey = null;
            if (namesToo) {
                m = propVarPattern.matcher(pk);
                if (m.find()) newKey = subst(m, pk, exp, permitUnset);
            }
            m = propVarPattern.matcher(pv);
            newVal = (m.find() ? subst(m, pv, exp, permitUnset) : null);
            if (newKey != null) zaps.add(pk);
            if (newKey != null || newVal != null)
                additions.setProperty(((newKey == null) ? pk : newKey),
                    ((newVal == null) ? pv : newVal));
        }
        for (String k : zaps) remove(k);
        putAll(additions);
    }
            /*
            m = prop
            pv = p.getProperty(k);
            newKey = pk;
            newVal = pv;
            for (String hk : map.keySet()) {
                if (newKey.indexOf("${" + hk + '}') > -1) {
                    newKey = newKey.replaceAll("\\Q${" + hk + '}',
                            map.get(hk));
                }
                if (newVal.indexOf("${" + hk + '}') > -1) {
                    newVal = newval.replaceAll("\\Q${" + hk + '}'
                            map.get(hk));
                }
            }
            if (!equals(pk, newKey)) p.remove(pk);
            if ((!equals(pk, newKey)) || !equals(pv, newVal))
                p.setProperty(newKey, newVal);
            */

    /**
     * @param m A matcher which has had exactly one successful find() already.
     * @param origVal the target content of the matcher
     * @param exp Data for name/vals to be substituted
     * @param permitUnset  If ${unsetvar}, will throw if permitUnset is false,
     *                     otherwise will return it unchanged.
     */
    protected String subst(Matcher m, String origVal,
            Properties exp, boolean permitUnset) {
        int afterLast = 0;
        String substVal;
        StringBuilder sb = new StringBuilder();
        do {
            substVal = exp.getProperty(m.group(1));
            if (substVal == null) {
                if (permitUnset) continue;
                throw new IllegalStateException(
                        "Undefined property '" + m.group(1) + "'");
            }
            sb.append(origVal.substring(afterLast, m.start())
                    + substVal);
            afterLast = m.end();
        } while (m.find());
        sb.append(origVal.substring(afterLast));
        if (origVal.equals(sb.toString())) return null;
        // System.out.println("Val change (" + origVal + ") to (" + sb + ')');
        return sb.toString();
    }
}
