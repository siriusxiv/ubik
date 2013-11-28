/*
 * @(#)$Id: ExtConfigListener.java 131 2008-10-31 17:09:46Z unsaved $
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


package net.wastl.webmail.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.wastl.webmail.misc.ExpandableProperties;
import net.wastl.webmail.misc.Helper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Purpose
 * <UL>
 * <LI>Set webapp attribute app.contextpath to the app's unique runtime Context
 * Path. This will be unique even for multiple deployments of the same
 * application distro.
 * <LI>Set webapp attribute deployment.name to a String base on the context path
 * <LI>Set webapp attribute rtconfig.dir to the absolute path of a runtime
 * config directory. Value is determined dynamically at runtime, based on the
 * Runtime environment. The directory is specific to this deployment instance of
 * this web app. To keep configs independent of the distributable app., the
 * designated directory should be external to the application.
 * <LI>Load a runtime Properties object from file "meta.properties" in the
 * rtconfig.dir directory described above, and write the Properties object to a
 * webapp attribute so the properties will be available to the app.
 * <LI>In addition to primary purposes, also automatically sets Java System
 * property 'webapps.rtconfig.dir'.
 * </UL>
 * <P>
 * The System Property SHOULD NOT be application-specific or
 * app-instance-specific if the app is to remain portable, since some app
 * servers share one set of System Properties for all web app instances.
 * </P>
 * <P>
 * The property contextPath or application attribute 'context.path' satisfies
 * the need for application-specific switching. Example config files with
 * webapps.rtconfig.dir set to '/local/configs'
 * <UL>
 * <LI>/local/configs/appa/meta.properties
 * <LI>/local/configs/appc/meta.properties
 * <LI>/local/configs/appd/meta.properties
 * </UL>
 * webapps.rtconfig.dir defaults to <CODE>${user.home}</CODE>. Since the app
 * also has access to the rt.configdir value, you can put any and all kinds of
 * runtime resources alongside the meta.properties file.
 * <P>
 * The variables ${rt.configdir} and ${app.contextpath} will be expanded if they
 * occur inside a meta.properties file. The latter allows for safely specifying
 * other files alongside the meta.properties file without worrying about the
 * vicissitudes of relative paths.
 * </P>
 * <P>
 * One would think that the running app could easily detect its own runtime
 * context path, but alas, that's impossible to do in a portable way (until
 * after requests are being served... and that is too late).
 * </P>
 * 
 * @author blaine.simpson@admc.com
 */
public class ExtConfigListener implements ServletContextListener {
    /*
     * It's very difficult to choose between camelBack and dot.delimited keys
     * for attributes. dot.delimited is much more elegant on the configuration
     * side, in .properties and XML files, but these dots break the ability for
     * JavaBean tools and utilities to dereference (e.g. EL, JSTL, Spring).
     * Also, can't have a getter or setter with a dot in it. Due to the
     * convenience factor, going with dot-delimited until and if this causes us
     * problems.
     */
    private static Log log = LogFactory.getLog(ExtConfigListener.class);

    /** Corresponds to the context.path setting. */
    protected String contextPath = null;
    /** Derived from contextPath. */
    protected String deploymentName = null;
    protected File lockFile = null;

    public void contextInitialized(ServletContextEvent sce) {
        Helper.logThreads("Top of ExtCongigListener.contextInitialized()");
        final ServletContext sc = sce.getServletContext();
        contextPath = sc.getInitParameter("default.contextpath");
        try {
            final Object o =
                new InitialContext().lookup("java:comp/env/app.contextpath");
            contextPath = (String) o;
            log.debug("app.contextpath set by webapp env property");
        } catch (final NameNotFoundException nnfe) {
        } catch (final NamingException nnfe) {
            log.fatal("Runtime failure when looking up env property", nnfe);
            throw new RuntimeException(
                    "Runtime failure when looking up env property", nnfe);
        }
        if (contextPath == null) {
            log.fatal("Required setting 'app.contextpath' is not set as either "
                    + "a app webapp JNDI env param, nor by default context "
                    + "init parameter 'default.contextpath'");
            throw new IllegalStateException(
                    "Required setting 'app.contextpath' is not set as either "
                    + "a app webapp JNDI env param, nor by default context "
                    + "init parameter 'default.contextpath'");
        }
        if (contextPath.equals("/ROOT")) {
            log.fatal("Refusing to use context path of '/ROOT' to avoid "
                    + "ambiguity with default context path");
            throw new IllegalStateException(
                    "Refusing to use context path of '/ROOT' to avoid "
                    + "ambiguity with default context path");
        }
        deploymentName = generateDeploymentName();
        log.info("Initializing configs for runtime deployment name '" 
                + deploymentName + "'");
        String dirProp = System.getProperty("webapps.rtconfig.dir");
        if (dirProp == null) {
            dirProp = System.getProperty("user.home");
            System.setProperty("webapps.rtconfig.dir", dirProp);
        }
        final File rtConfigDir = new File(dirProp, deploymentName);
        final File metaFile = new File(rtConfigDir, "meta.properties");
        lockFile = new File(rtConfigDir, "lock.txt");
        if (lockFile.exists()) {
            log.fatal("Presence of lock file '" 
                    + lockFile.getAbsolutePath() 
                    + "' indicates the instance is already running");
            lockFile = null;
            throw new IllegalStateException("Presence of lock file " 
                    + "indicates the instance is already running");
        }
        // From this point on, we know that:
        // IF LOCK FILE EXISTS, we have created it and all is well
        // IF LOCK FILE DOES NOT EXIST, we need to create it ASAP
        if (rtConfigDir.isDirectory()) {
            mkLockFile();
        }
        // We create lock file as early as possible.
        // If we can't make it here, it will be created in installXmlStorage.
        if (!rtConfigDir.isDirectory() || !metaFile.isFile()) {
            try {
                installXmlStorage(rtConfigDir, metaFile);
                log.warn("New XML storage system successfully loaded.  " 
                        + "Metadata file '" + metaFile.getAbsolutePath() + "'");
            } catch (final IOException e) {
                log.fatal("Failed to set up a new XML storage system", e);
                throw new IllegalStateException(
                        "Failed to set up a new XML storage system", e);
            }
        }
        if (!lockFile.exists()) {
            // Being extra safe
            log.fatal("Assertion failed.  Internal locking error in " 
                    + getClass().getName() + '.');
            lockFile = null;
            throw new IllegalStateException(
                    "Assertion failed.  Internal locking error in " 
                    + getClass().getName() + '.');
        }
        final ExpandableProperties metaProperties = new ExpandableProperties();
        try {
            metaProperties.load(new FileInputStream(metaFile));
        } catch (final IOException ioe) {
            log.fatal("Failed to read meta props file '"
                    + metaFile.getAbsolutePath() + "'", ioe);
            throw new IllegalStateException("Failed to read meta props file '"
                    + metaFile.getAbsolutePath() + "'", ioe);
        }
        final Properties expandProps = new Properties();
        expandProps.setProperty("rtconfig.dir", rtConfigDir.getAbsolutePath());
        expandProps.setProperty("app.contextpath", contextPath);
        expandProps.setProperty("deployment.name", deploymentName);
        try {
            metaProperties.expand(expandProps); // Expand ${} properties
        } catch (final Throwable t) {
            log.fatal("Failed to expand properties in meta file '"
                    + metaFile.getAbsolutePath() + "'", t);
            throw new IllegalStateException(
                    "Failed to expand properties in meta file '"
                    + metaFile.getAbsolutePath() + "'", t);
        }
        String requiredKeysString;
        requiredKeysString = sc.getInitParameter("required.metaprop.keys");
        if (requiredKeysString != null) {
            final Set<String> requiredKeys = new HashSet<String>(
                    Arrays.asList(requiredKeysString.split("\\s*,\\s*", -1)));
            requiredKeys.removeAll(metaProperties.keySet());
            if (requiredKeys.size() > 0) {
                log.fatal("Meta properties file '" + metaFile.getAbsolutePath()
                        + "' missing required property(s): " + requiredKeys);
                throw new IllegalStateException("Meta properties file '"
                        + metaFile.getAbsolutePath()
                        + "' missing required property(s): " + requiredKeys);
            }
        }
        sc.setAttribute("app.contextpath", contextPath);
        sc.setAttribute("deployment.name", deploymentName);
        sc.setAttribute("rtconfig.dir", rtConfigDir);
        sc.setAttribute("meta.properties", metaProperties);

        log.debug("'app.contextpath', 'rtconfig.dir', 'meta.properties' "
                + "successfully published to app context for "
                + deploymentName);
    }

    public void contextDestroyed(ServletContextEvent sce) {
        log.info("App '" + deploymentName + "' shutting down.\n"
                + "All Servlets and Filters have been destroyed");
        if (lockFile != null) {
            if (lockFile.delete()) {
                // In my experience, this return status is unreliable.
                log.info("Lock file '" + lockFile.getAbsolutePath()
                        + "' removed");
            } else {
                log.error("Failed to remove lock file '"
                        + lockFile.getAbsolutePath() + "' removed");
            }
        }
    }

    /**
     * @param baseDir
     *            Parent directory of metaFile
     * @param metaFile
     *            Properties file to be created. IT CAN NOT EXIST YET!
     * @throws IOException
     *             if fail to create new XML Storage system
     */
    protected void installXmlStorage(File baseDir, File metaFile)
            throws IOException {
        log.warn("Will attempt install a brand new data store");
        final File dataDir = new File(baseDir, "data");
        if (dataDir.exists())
            throw new IOException("Target data path dir already exists: "
                    + dataDir.getAbsolutePath());
        if (!baseDir.isDirectory()) {
            final File parentDir = baseDir.getParentFile();
            if (!parentDir.canWrite())
                throw new IOException("Cannot create base RT directory '"
                        + baseDir.getAbsolutePath() + "'");
            if (!baseDir.mkdir())
                throw new IOException("Failed to create base RT directory '"
                        + baseDir.getAbsolutePath() + "'");
            log.debug(
                    "Created base RT dir '" + baseDir.getAbsolutePath() + "'");
            mkLockFile();
        }
        if (!baseDir.canWrite()) throw new IOException(
                "Do not have privilegest to create meta file '"
                + metaFile.getAbsolutePath() + "'");
        if (!dataDir.mkdir())
            throw new IOException("Failed to create data directory '"
                    + dataDir.getAbsolutePath() + "'");
        log.debug("Created data dir '" + dataDir.getAbsolutePath() + "'");
        // In my experience, you can't trust the return values of the
        // File.mkdir() method. But the file creations or extractions
        // wild fail below in that case, so that's no problem.

        // Could create a Properties object and save it, but why?
        final PrintWriter pw = new PrintWriter(new FileWriter(metaFile));
        try {
            pw.println("webmail.data.path: ${rtconfig.dir}/data");
            pw.println("webmail.mimetypes.filepath: "
                    + "${rtconfig.dir}/mimetypes.txt");
            pw.flush();
        } finally {
            pw.close();
        }

        final InputStream zipFileStream =
                getClass().getResourceAsStream("/data.zip");
        if (zipFileStream == null) throw new IOException(
                "Zip file 'data.zip' missing from web application");
        final InputStream mimeInStream =
                getClass().getResourceAsStream("/mimetypes.txt");
        if (mimeInStream == null) throw new IOException(
                "Mime-types file 'mimetypes.txt' missing from web application");
        ZipEntry entry;
        File newNode;
        FileOutputStream fileStream;
        long fileSize, bytesRead;
        int i;
        final byte[] buffer = new byte[10240];

        final FileOutputStream mimeOutStream =
                new FileOutputStream(new File(baseDir, "mimetypes.txt"));
        try {
            while ((i = mimeInStream.read(buffer)) > 0) {
                mimeOutStream.write(buffer, 0, i);
            }
            mimeOutStream.flush();
        } finally {
            mimeOutStream.close();
        }
        log.debug("Extracted mime types file");

        final ZipInputStream zipStream = new ZipInputStream(zipFileStream);
        try {
            while ((entry = zipStream.getNextEntry()) != null) {
                newNode = new File(dataDir, entry.getName());
                if (entry.isDirectory()) {
                    if (!newNode.mkdir())
                        throw new IOException("Failed to extract dir '"
                                + entry.getName() + "' from 'data.zip' file");
                    log.debug("Extracted dir '" + entry.getName() + "' to '"
                            + newNode.getAbsolutePath() + "'");
                    zipStream.closeEntry();
                    continue;
                }
                fileSize = entry.getSize();
                fileStream = new FileOutputStream(newNode);
                try {
                    bytesRead = 0;
                    while ((i = zipStream.read(buffer)) > 0) {
                        fileStream.write(buffer, 0, i);
                        bytesRead += i;
                    }
                    fileStream.flush();
                } finally {
                    fileStream.close();
                }
                zipStream.closeEntry();
                if (bytesRead != fileSize)
                    throw new IOException("Expected " + fileSize
                            + " bytes for '" + entry.getName()
                            + ", but extracted " + bytesRead + " bytes to '"
                            + newNode.getAbsolutePath() + "'");
                log.debug("Extracted file '" + entry.getName() + "' to '"
                        + newNode.getAbsolutePath() + "'");
            }
        } finally {
            zipStream.close();
        }
    }

    static Pattern cpPattern = Pattern.compile("/(\\w+)$");

    protected String generateDeploymentName() {
        if (contextPath == null) return null;
        if (contextPath.length() == 0) return "ROOT";
        final Matcher m = cpPattern.matcher(contextPath);
        if (m.matches()) return m.group(1);
        log.error("Malformatted context path '" + contextPath + "'");
        return null;
    }

    protected void mkLockFile() {
        if (lockFile.exists()) throw new IllegalStateException(
                "Attempting to create Lock file, but it already exists");
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter(lockFile));
            pw.println(deploymentName + " started at " + new java.util.Date());
            pw.flush();
        } catch (final IOException ioe) {
            log.fatal("Failed to write lock file '"
                    + lockFile.getAbsolutePath() + "'", ioe);
            throw new IllegalStateException("Failed to write lock file '"
                    + lockFile.getAbsolutePath() + "'", ioe);
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }
}
