/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.tests.integration.servlet_request_wrapper_binding;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.glassfish.jersey.internal.util.collection.Ref;
import org.glassfish.jersey.server.spi.RequestScopedInitializer;
import org.glassfish.jersey.servlet.internal.spi.NoOpServletContainerProvider;
import org.glassfish.jersey.servlet.internal.spi.RequestContextProvider;
import org.glassfish.jersey.servlet.internal.spi.RequestScopedInitializerProvider;

import org.glassfish.hk2.api.ServiceLocator;

/**
 * Servlet container provider that wraps the original Servlet request/response.
 *
 * @author Jakub Podlesak (jakub.podlesak at oracle.com)
 */
public class RequestResponseWrapperProvider extends NoOpServletContainerProvider {

    /**
     * Subclass standard wrapper so that we make 100 % sure we are getting the right type.
     */
    public static class RequestWrapper extends HttpServletRequestWrapper {

        public RequestWrapper(HttpServletRequest request) {
            super(request);
        }
    }

    /**
     * Subclass standard wrapper so that we make 100 % sure we are getting the right type.
     */
    public static class ResponseWrapper extends HttpServletResponseWrapper {

        public ResponseWrapper(HttpServletResponse response) {
            super(response);
        }
    }

    @Override
    public RequestScopedInitializerProvider getRequestScopedInitializerProvider() {
        return new RequestScopedInitializerProvider() {

            @Override
            public RequestScopedInitializer get(final RequestContextProvider context) {
                return new RequestScopedInitializer() {

                    @Override
                    public void initialize(ServiceLocator locator) {
                        locator.<Ref<HttpServletRequest>>getService(HTTP_SERVLET_REQUEST_TYPE)
                                .set(wrapped(context.getHttpServletRequest()));
                        locator.<Ref<HttpServletResponse>>getService(HTTP_SERVLET_RESPONSE_TYPE)
                                .set(wrapped(context.getHttpServletResponse()));
                    }
                };
            }
        };
    }

    private HttpServletRequest wrapped(final HttpServletRequest request) {
        return new RequestWrapper(request);
    }

    private HttpServletResponse wrapped(final HttpServletResponse response) {
        return new ResponseWrapper(response);
    }
}
