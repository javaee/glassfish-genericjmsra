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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.resource.ResourceException;


/**
 * Execute the methods based on the parameters.
 *
 * @author        Binod P.G
 */
public class MethodExecutor implements java.io.Serializable {
    private static Logger _logger;

    static {
        _logger = LogUtils.getLogger();
    }

    private static StringManager sm = StringManager.getManager(com.sun.genericra.GenericJMSRA.class);
    private boolean debug = false;

    /**
     * Exceute a simple set Method.
     *
     * @param        value        Value to be set.
     * @param        method        <code>Method</code> object.
     * @param        obj        Object on which the method to be executed.
     * @throws  <code>ResourceException</code>, in case of the mismatch of parameter values or
     *                a security violation.
     */
    public static void runJavaBeanMethod(String name, String value,
        Method method, Object obj) throws ResourceException {
        if ((value == null) || value.trim().equals("")) {
            return;
        }

        try {
            Class[] parameters = method.getParameterTypes();

            if (parameters.length == 2) {
                Object[] values = new Object[2];
                values[0] = convertType(parameters[0], name);
                values[1] = convertType(parameters[1], value);
                method.invoke(obj, values);
            }
        } catch (IllegalAccessException iae) {
            _logger.log(Level.SEVERE, "jdbc.exc_jb_val", iae);

            String msg = sm.getString("me.access_denied", method.getName());
            throw new ResourceException(msg);
        } catch (IllegalArgumentException ie) {
            _logger.log(Level.SEVERE, "jdbc.exc_jb_val", ie);

            String msg = sm.getString("me.illegal_args", method.getName());
            throw new ResourceException(msg);
        } catch (InvocationTargetException ite) {
            _logger.log(Level.SEVERE, "jdbc.exc_jb_val", ite);

            String msg = sm.getString("me.inv_denied", method.getName());
            throw new ResourceException(msg);
        }
    }

    /**
     * Exceute a simple set Method.
     *
     * @param        value        Value to be set.
     * @param        method        <code>Method</code> object.
     * @param        obj        Object on which the method to be executed.
     * @throws  <code>ResourceException</code>, in case of the mismatch of parameter values or
     *                a security violation.
     */
    public static void runJavaBeanMethod(String value, Method method, Object obj)
        throws ResourceException {
        if ((value == null) || value.trim().equals("")) {
            return;
        }

        try {
            Class[] parameters = method.getParameterTypes();

            if (parameters.length == 1) {
                Object[] values = new Object[1];
                values[0] = convertType(parameters[0], value);
                method.invoke(obj, values);
            }
        } catch (IllegalAccessException iae) {
            _logger.log(Level.SEVERE, "jdbc.exc_jb_val", iae);

            String msg = sm.getString("me.access_denied", method.getName());
            throw new ResourceException(msg);
        } catch (IllegalArgumentException ie) {
            _logger.log(Level.SEVERE, "jdbc.exc_jb_val", ie);

            String msg = sm.getString("me.illegal_args", method.getName());
            throw new ResourceException(msg);
        } catch (InvocationTargetException ite) {
            _logger.log(Level.SEVERE, "jdbc.exc_jb_val", ite);

            String msg = sm.getString("me.access_denied", method.getName());
            throw new ResourceException(msg);
        }
    }

    /**
     * Executes the method.
     *
     * @param        method <code>Method</code> object.
     * @param        obj        Object on which the method to be executed.
     * @param        values        Parameter values for executing the method.
     * @throws  <code>ResourceException</code>, in case of the mismatch of parameter values or
     *                a security violation.
     */
    public static void runMethod(Method method, Object obj, Vector values)
        throws ResourceException {
        try {
            Class[] parameters = method.getParameterTypes();

            if (values.size() != parameters.length) {
                return;
            }

            Object[] actualValues = new Object[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                String val = (String) values.get(i);

                if (val.trim().equals("NULL")) {
                    actualValues[i] = null;
                } else {
                    actualValues[i] = convertType(parameters[i], val);
                }
            }

            method.invoke(obj, actualValues);
        } catch (IllegalAccessException iae) {
            _logger.log(Level.SEVERE, "jdbc.exc_jb_val", iae);

            String msg = sm.getString("me.access_denied", method.getName());
            throw new ResourceException(msg);
        } catch (IllegalArgumentException ie) {
            _logger.log(Level.SEVERE, "jdbc.exc_jb_val", ie);

            String msg = sm.getString("me.illegal_args", method.getName());
            throw new ResourceException(msg);
        } catch (InvocationTargetException ite) {
            _logger.log(Level.SEVERE, "jdbc.exc_jb_val", ite);

            String msg = sm.getString("me.access_denied", method.getName());
            throw new ResourceException(msg);
        }
    }

    /**
     * Converts the type from String to the Class type.
     *
     * @param        type                Class name to which the conversion is required.
     * @param        parameter        String value to be converted.
     * @return        Converted value.
     * @throws  <code>ResourceException</code>, in case of the mismatch of parameter values or
     *                a security violation.
     */
    private static Object convertType(Class type, String parameter)
        throws ResourceException {
        try {
            String typeName = type.getName();

            if (typeName.equals("java.lang.String") ||
                    typeName.equals("java.lang.Object")) {
                return parameter;
            }

            if (typeName.equals("int") || typeName.equals("java.lang.Integer")) {
                return new Integer(parameter);
            }

            if (typeName.equals("short") || typeName.equals("java.lang.Short")) {
                return new Short(parameter);
            }

            if (typeName.equals("byte") || typeName.equals("java.lang.Byte")) {
                return new Byte(parameter);
            }

            if (typeName.equals("long") || typeName.equals("java.lang.Long")) {
                return new Long(parameter);
            }

            if (typeName.equals("float") || typeName.equals("java.lang.Float")) {
                return new Float(parameter);
            }

            if (typeName.equals("double") ||
                    typeName.equals("java.lang.Double")) {
                return new Double(parameter);
            }

            if (typeName.equals("java.math.BigDecimal")) {
                return new java.math.BigDecimal(parameter);
            }

            if (typeName.equals("java.math.BigInteger")) {
                return new java.math.BigInteger(parameter);
            }

            if (typeName.equals("boolean") ||
                    typeName.equals("java.lang.Boolean")) {
                return new Boolean(parameter);
            }

            return parameter;
        } catch (NumberFormatException nfe) {
            _logger.log(Level.SEVERE, "jdbc.exc_nfe", parameter);

            String msg = sm.getString("me.invalid_param", parameter);
            throw new ResourceException(msg);
        }
    }
}
