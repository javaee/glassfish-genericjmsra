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

import com.sun.genericra.*;

import java.lang.reflect.*;

import java.util.*;
import java.util.logging.*;

import javax.resource.ResourceException;


/**
 * Abstract class. This sets properties on javabeans.
 * @author Binod P.G
 */
public abstract class ObjectBuilder {
    private static Logger _logger;

    static {
        _logger = LogUtils.getLogger();
    }

    private String setterMethodName = null;
    private String propString = null;
    private String separator = Constants.SEPARATOR;
    private String delimiter = Constants.DELIMITER;
    private Object builtObject = null;
    private StringManager sm = StringManager.getManager(GenericJMSRA.class);

    protected ObjectBuilder() {
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public void setCommonSetterMethodName(String name) {
        this.setterMethodName = name;
    }

    public void setProperties(String props) {
        this.propString = props;
    }

    protected Hashtable parseToProperties(String prop)
        throws ResourceException {
        _logger.log(Level.FINE,
            "parseToProperties:" + prop + " delimited:" + delimiter +
            " seperator:" + separator);

        Hashtable result = new Hashtable();

        if ((prop == null) || prop.trim().equals("")) {
            return result;
        } else if ((delimiter == null) || delimiter.equals("")) {
            String msg = sm.getString("delim_not_specified");
            throw new ResourceException(msg);
        }

        CustomTokenizer tokenList = new CustomTokenizer(prop, delimiter);

        while (tokenList.hasMoreTokens()) {
            String propValuePair =
                (String) tokenList.nextTokenWithoutEscapeAndQuoteChars();
            _logger.log(Level.FINEST,
                "PropertyValuePair : " + propValuePair + ", separator:" +
                separator);

            int loc = propValuePair.indexOf(separator);
            String propName = propValuePair.substring(0, loc);
            String propValue = propValuePair.substring(loc +
                    separator.length());
            _logger.log(Level.FINER, "Property : " + propName + ":" +
                propValue);
            result.put(propName, propValue);
        }

        return result;
    }

    public Object build() throws ResourceException {
        if (builtObject == null) {
            builtObject = createObject();
            _logger.log(Level.FINEST,
                "Created the object based on class :" +
                builtObject.getClass().getName());

            if ((this.setterMethodName != null) &&
                    !this.setterMethodName.trim().equals("")) {
                _logger.log(Level.FINEST,
                    "About to set properties on " + builtObject +
                    " using methodName :" + setterMethodName);
                setUsingCommonSetterMethod(builtObject,
                    parseToProperties(propString));
            } else {
                _logger.log(Level.FINEST,
                    "About to set properties on " + builtObject);
                setProperties(builtObject, parseToProperties(propString));
            }
        }

        return builtObject;
    }

    protected abstract Object createObject() throws ResourceException;

    private void setUsingCommonSetterMethod(Object obj, Hashtable props)
        throws ResourceException {
        Class[] params = new Class[] { String.class, String.class };

        try {
            Method m = obj.getClass().getMethod(setterMethodName, params);
            Iterator it = props.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry me = (Map.Entry) it.next();
                String key = (String) me.getKey();
                String value = (String) me.getValue();
                _logger.log(Level.FINER,
                    "Setting property " + key + ":" + value + " on " +
                    obj.getClass().getName());
                MethodExecutor.runJavaBeanMethod(key, value, m, obj);
            }
        } catch (Exception e) {
            throw ExceptionUtils.newInvalidPropertyException(e);
        }
    }

    private void setProperties(Object obj, Hashtable props)
        throws ResourceException {
        try {
            Method[] methods = obj.getClass().getMethods();

            for (int i = 0; i < methods.length; i++) {
                String name = methods[i].getName();

                if (name.startsWith("set")) {
                    String propName = name.substring(3);

                    if (props.containsKey(propName)) {
                        String propValue = (String) props.get(propName);
                        _logger.log(Level.FINER,
                            "Setting property " + propName + ":" + propValue +
                            " on " + obj.getClass().getName());
                        MethodExecutor.runJavaBeanMethod(propValue, methods[i],
                            obj);
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionUtils.newInvalidPropertyException(e);
        }
    }
}
