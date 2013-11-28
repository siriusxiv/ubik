/*
 * @(#)$Id: ContextPathValidator.java 109 2008-10-29 22:38:23Z unsaved $
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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ContextPathValidator implements Filter {
    private static Log log = LogFactory.getLog(ContextPathValidator.class);

    protected String contextPath = null;
    protected boolean validated = false;

    public void init(FilterConfig filterConfig) throws ServletException {
        contextPath = (String) filterConfig.
                getServletContext().getAttribute("app.contextpath");
        if (contextPath == null) {
            log.fatal("'app.contextpath' attr missing from the app context");
            throw new ServletException(
                    "'app.contextpath' attr missing from the app context");
        }
    }

    public void destroy() {}

    public void doFilter(ServletRequest genReq, ServletResponse genResp,
            FilterChain chain) throws IOException, ServletException {
        if (validated) {
            chain.doFilter(genReq, genResp);
            return;
        }
        if (contextPath == null) {
            log.error("Aborting validator because expected contextPath unset");
            throw new ServletException(
                    "Aborting validator because expected contextPath unset");
        }
        HttpServletRequest req = (HttpServletRequest) genReq;
        if (contextPath.equals(req.getContextPath())){
            validated = true;
            log.info("Context root validated");
            chain.doFilter(req, genResp);
            return;
        }
        log.fatal("Request context path '" + req.getContextPath()
                + "' does match match configured context.path '"
                + contextPath + "'");
        throw new ServletException("Request context path '" +
                req.getContextPath()
                + "' does match match configured context.path '"
                + contextPath + "'");
    }
}
