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
