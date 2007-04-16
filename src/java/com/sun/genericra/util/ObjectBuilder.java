/**
 * Copyright 2004-2005 Sun Microsystems, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
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
            String propValuePair = (String) tokenList.nextToken();
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
