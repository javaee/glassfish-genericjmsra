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
