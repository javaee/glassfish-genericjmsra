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
