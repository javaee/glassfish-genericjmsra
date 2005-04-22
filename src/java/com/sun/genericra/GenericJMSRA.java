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
package com.sun.genericra;
import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.WorkManager;
import javax.transaction.xa.XAResource;
import javax.jms.*;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.*;
import java.security.*;

import com.sun.genericra.inbound.EndpointConsumer;
import com.sun.genericra.util.*;

/**
 * Resource Adapter javabean implementation for JMS resource adapter.
 * 
 * Whenever an application server does a start() on the RA java bean,
 * an instance of the javabean will be saved for singleton usage.
 * This is required since admin objects need to obtain default resource
 * adapter instance.
 *
 * @author Sivakumar Thyagarajan, Binod P.G 
 */

public class GenericJMSRA extends GenericJMSRAProperties implements ResourceAdapter {

    private transient BootstrapContext context = null;

    private transient Hashtable consumers;
    private transient Method onMessageMethod = null;
    private transient ObjectBuilderFactory obf = null;

    // Serialization of resource adapter will not happen since it is a static instance.
    private static GenericJMSRA raInstance = null;

    private String logLevel = Constants.LogLevel.INFO;

    private static Logger logger;
    static {
        logger = LogUtils.getLogger();
    }
    
    
    public static GenericJMSRA getInstance() {
        logger.log(Level.FINEST, "GenericJMSRA - getInstance() orig " + raInstance);
        return GenericJMSRA.raInstance;
    }

    
    public void stop() {
        obf = null;
        onMessageMethod = null;
    }

    public void start(BootstrapContext context) throws ResourceAdapterInternalException {
        
        logger.log(Level.FINEST, "GenericJMSRA.start() ....");
        GenericJMSRA.raInstance = this;
        this.obf = new ObjectBuilderFactory();
        this.consumers = new Hashtable(); 
        this.context = context;
        try {
            Class msgListenerClass = javax.jms.MessageListener.class;
            Class[] paramTypes = { javax.jms.Message.class };
            onMessageMethod = msgListenerClass.getMethod("onMessage", paramTypes);
        } catch (NoSuchMethodException ex) {
            throw ExceptionUtils.newResourceAdapterInternalException(ex);
        }
    }

    public void endpointActivation(MessageEndpointFactory mef, ActivationSpec spec) 
                        throws ResourceException {
         EndpointConsumer consumer = new EndpointConsumer(mef, spec);
         consumer.start();
         Hashtable consumers = getConsumers();
         EndpointKey key = new EndpointKey(mef, spec);
         consumers.put(key, consumer);
    }

    public void endpointDeactivation(MessageEndpointFactory mef, ActivationSpec spec) {
         EndpointKey key = new EndpointKey(mef, spec);
         EndpointConsumer consumer = (EndpointConsumer) getConsumers().remove(key);   
         if (consumer != null) {
             consumer.stop();
         }
    }

    public XAResource[] getXAResources(ActivationSpec[] specs) throws ResourceException {
        ArrayList xars = new ArrayList();
        for (int i=0; i < specs.length; i++) {
            com.sun.genericra.inbound.ActivationSpec tmpSpec = null;
            if (specs[i] instanceof com.sun.genericra.inbound.ActivationSpec) {
                tmpSpec = (com.sun.genericra.inbound.ActivationSpec) specs[i];   
            } else {
                continue;
            }
            if (tmpSpec.getSupportsXA()) {
                XAConnection xacon = null;
                XASession xasess = null;
                EndpointConsumer consumer = null;
                try {
                    consumer = new EndpointConsumer(tmpSpec);
                    consumer.initialize(true);
                    xacon = (XAConnection) consumer.getConnection();
                    xasess = xacon.createXASession();
                    XAResource xaRes = xasess.getXAResource();
                    xars.add(xaRes);
                    logger.log(Level.FINEST, "Added XA Resource : "  + xaRes);
                } catch (Exception e) {
                    logger.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }
        return (XAResource[])xars.toArray();
    }

    public WorkManager getWorkManager() {
        return getInstance().context.getWorkManager();
    }

    public ObjectBuilderFactory getObjectBuilderFactory() {
        return getInstance().obf;
    }

    public void setLogLevel(String level) {
        logger.log(Level.FINEST, "Setting log level:"+level);
        this.logLevel = level;
        setLevelInLogger(level);
    }

    public String getLogLevel() {
        return logLevel;
    }

    private Hashtable getConsumers() {
        return getInstance().consumers;
    }

    private void setLevelInLogger(String level) {
        Level l = Level.INFO;
        if (level.equalsIgnoreCase(Constants.LogLevel.FINEST)) {
            logger.log(Level.FINEST, "Setting finest as log levels");
            l = Level.FINEST;
        } else if (level.equalsIgnoreCase(Constants.LogLevel.FINER)) {
            l = Level.FINER;
        } else if (level.equalsIgnoreCase(Constants.LogLevel.FINE)) {
            l = Level.FINE;
        } else if (level.equalsIgnoreCase(Constants.LogLevel.INFO)) {
            l = Level.INFO;
        } else if (level.equalsIgnoreCase(Constants.LogLevel.WARNING)) {
            l = Level.WARNING;
        } else if (level.equalsIgnoreCase(Constants.LogLevel.SEVERE)) {
            l = Level.SEVERE;
        }
        final Level tmp = l;
        AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                logger.setLevel(tmp);
                return null; // nothing to return
            }
        });
    }


    /** 
     * Retrieves the Method that is called in the MessageListener
     */
    public Method getListeningMethod() {
        return getInstance().onMessageMethod;
    }


    class EndpointKey implements Serializable{
        private MessageEndpointFactory mef;
        private ActivationSpec spec;

        public EndpointKey(MessageEndpointFactory mef, ActivationSpec spec) {
            this.mef = mef;
            this.spec = spec;
        }

        public boolean equals(Object obj) {
            EndpointKey other = (EndpointKey) obj;
            return other.mef.equals(this.mef) && other.spec.equals(this.spec);
        }

        public int hashCode() {
            return mef.hashCode() + spec.hashCode();
        }
    }
    
}
