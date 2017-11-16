/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
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

package com.sun.genericra.util;

import java.lang.reflect.*;

import java.security.*;

import java.util.Hashtable;
import java.util.logging.*;

import javax.naming.*;

import javax.resource.ResourceException;


/**
 * Build an object based on classname or look up
 * based on JNDI name.
 *
 * @author Binod P.G
 */
public class ObjectBuilderFactory {
    private static boolean debug = false;
    private static Logger logger;

    static {
        logger = LogUtils.getLogger();
    }

    public ObjectBuilder createUsingClassName(String name) {
        return new ClassObjectBuilder(name);
    }

    public ObjectBuilder createUsingJndiName(String jndiName, String jndiProps) {
        return new JndiObjectBuilder(jndiName, jndiProps);
    }

    void debug(String str) {
        logger.log(Level.FINEST, str);
    }

    class ClassObjectBuilder extends ObjectBuilder {
        private String className = null;

        public ClassObjectBuilder(String className) {
            this.className = className;
        }

        public Object createObject() throws ResourceException {
            try {
                return Class.forName(className).newInstance();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();                       
                throw ExceptionUtils.newInvalidPropertyException(e);
            } catch (InstantiationException ie) {
                ie.printStackTrace();
                throw ExceptionUtils.newInvalidPropertyException(ie);
            } catch (IllegalAccessException iae) {
                iae.printStackTrace();
                throw ExceptionUtils.newSecurityException(iae);
            }
        }
    }

    class JndiObjectBuilder extends ObjectBuilder {
        private String jndiName = null;
        private String jndiProps = null;

        JndiObjectBuilder(String jndiName, String jndiProps) {
            this.jndiName = jndiName;
            this.jndiProps = jndiProps;
        }

        public Object createObject() throws ResourceException {
        	InitialContext ic = null;
            try {
                Hashtable props = parseToProperties(this.jndiProps);
                debug("Properties passed to InitialContext :: " + props);

                ic = new InitialContext(props);
                debug("Looking the JNDI name :" + this.jndiName);

                return ic.lookup(this.jndiName);
            } catch (Exception e) {
                throw ExceptionUtils.newInvalidPropertyException(e);
            } finally {
            	if (ic!=null)
					try {
						ic.close();
					} catch (NamingException e) {
						// ignore errors on closing the InitialContext
					}
            }
        }
    }
}
