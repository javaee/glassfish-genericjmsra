/*
 * Copyright (c) 2004-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
