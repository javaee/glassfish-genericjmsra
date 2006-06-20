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
package com.sun.genericra.outbound;

import com.sun.genericra.util.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import java.util.logging.*;

import javax.jms.*;


/**
 * ProxyMessage. This is a Proxy Message that overrides the setJMSReplyTo
 * method.
 */
public final class ProxyMessage implements InvocationHandler {
    private static Logger logger;

    static {
        logger = LogUtils.getLogger();
    }

    Message msg = null;

    public ProxyMessage(Message msg) {
        this.msg = msg;
    }

    /**
     * Invokes the method
     *
     * @param proxy Object
     * @param method <code>Method</code> to be executed.
     * @param args Arguments
     * @throws Throwable.
     */
    public Object invoke(Object proxy, Method method, Object[] args)
        throws Throwable {
        String methodName = method.getName();

        if (methodName.equals("setJMSReplyTo") ||
                methodName.equals("setJMSDestination")) {
            if (args[0] instanceof DestinationAdapter) {
                args[0] = ((DestinationAdapter) args[0])._getPhysicalDestination();

                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE,
                        methodName +
                        "is being called with unwrapped destination");
                }
            }
        }

        return method.invoke(msg, args);
    }
}
