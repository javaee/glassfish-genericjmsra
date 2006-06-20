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

import javax.jms.JMSException;

import javax.resource.*;
import javax.resource.spi.*;

import javax.transaction.xa.XAException;


/**
 * Utility class that generate Exceptions
 *
 * @author Binod P.G
 */
public class ExceptionUtils {
    public static javax.resource.spi.SecurityException newSecurityException(
        Throwable t) {
        javax.resource.spi.SecurityException se = new javax.resource.spi.SecurityException(t.getMessage());

        return (javax.resource.spi.SecurityException) se.initCause(t);
    }

    public static InvalidPropertyException newInvalidPropertyException(
        Throwable t) {
        InvalidPropertyException ipe = new InvalidPropertyException(t.getMessage());

        return (InvalidPropertyException) ipe.initCause(t);
    }

    public static Exception newException(Throwable t) {
        Exception se = new Exception(t.getMessage());

        return (Exception) se.initCause(t);
    }

    public static RuntimeException newRuntimeException(Throwable t) {
        RuntimeException se = new RuntimeException(t.getMessage());

        return (RuntimeException) se.initCause(t);
    }

    public static ResourceAdapterInternalException newResourceAdapterInternalException(
        Throwable t) {
        ResourceAdapterInternalException se = new ResourceAdapterInternalException(t.getMessage());

        return (ResourceAdapterInternalException) se.initCause(t);
    }

    public static JMSException newJMSException(Throwable t) {
        JMSException se = new JMSException(t.getMessage());

        return (JMSException) se.initCause(t);
    }

    public static ResourceException newResourceException(Throwable t) {
        ResourceException se = new ResourceException(t.getMessage());

        return (ResourceException) se.initCause(t);
    }

    public static XAException newXAException(Throwable t) {
        XAException se = new XAException(t.getMessage());

        return (XAException) se.initCause(t);
    }
}
