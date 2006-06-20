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

import com.sun.genericra.inbound.EndpointConsumer;
import com.sun.genericra.util.*;

import java.io.Serializable;

import java.lang.reflect.Method;

import java.security.*;

import java.util.*;
import java.util.logging.*;

import javax.jms.*;

import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.WorkManager;

import javax.transaction.xa.XAResource;


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
public class GenericJMSRA extends GenericJMSRAProperties
    implements ResourceAdapter {
    // Serialization of resource adapter will not happen since it is a static instance.
    
    /**
     * Singleton instance for the Generic JMS RA
     */
    private static GenericJMSRA raInstance = null;
    
    /**
     * Logger object to log messages.
     */
    private static Logger logger;

    /**
     * Gets the default logger
     */    
    static {
        logger = LogUtils.getLogger();
    }

    /** 
     * JCA contract bootstarp context supplied by the container.
     */
    private transient BootstrapContext context = null;
    
    /**
     * Table that stores all the consumer endpoints
     */
    private transient Hashtable consumers;
    
    /**
     * Method to be invoked on the endpoint.
     */
    private transient Method onMessageMethod = null;
    
    /** 
     * Util class to create objects from jndi names , or reflection.
     */
    private transient ObjectBuilderFactory obf = null;
    
    /**
     * Default log level for logging in genric JMS RA.
     */
    private String logLevel = Constants.LogLevel.INFO;

    /**
     * Returns the singleton implementation of this RA.     
     *
     * @return GenericJMSRA singleton instance.
     */     
    public static GenericJMSRA getInstance() {
        logger.log(Level.FINEST,
            "GenericJMSRA - getInstance() orig " + raInstance);

        return GenericJMSRA.raInstance;
    }

    /**
     * Stops the resource adaptor and all its endpoints.
     */
    public void stop() {
        obf = null;
        onMessageMethod = null;
    }
    
    /** 
     * Starts/bootstraps the RA. This method is a lifecycle method
     * that is invoked by the application server to start the RA.
     * 
     * @param context Bootstrap context supplied by the application server.
     */
    public void start(BootstrapContext context)
        throws ResourceAdapterInternalException {
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

    /**
     * This method is invoked by the application server when it requires 
     * the RA to activate an endpoint. This method would typically be called
     * when an MDB is deployed in the application server and that uses the RA
     * for inbound communication.
     *
     * @param mef message endpoint factory given by the app server.
     */
    public void endpointActivation(MessageEndpointFactory mef,
        ActivationSpec spec) throws ResourceException {
        EndpointConsumer consumer = new EndpointConsumer(mef, spec);
        consumer.start();

        Hashtable consumers = getConsumers();
        EndpointKey key = new EndpointKey(mef, spec);
        consumers.put(key, consumer);
    }

    /**
     * This method is invoked by the application server when it requires 
     * the RA to de-activate an endpoint. This method would typically be called
     * when an MDB is un-deployed in the application server and that uses the RA
     * for inbound communication.
     *
     * @param mef message endpoint factory of the endpoint given by the app server.
     */
    public void endpointDeactivation(MessageEndpointFactory mef,
        ActivationSpec spec) {
        EndpointKey key = new EndpointKey(mef, spec);
        EndpointConsumer consumer = (EndpointConsumer) getConsumers().remove(key);

        if (consumer != null) {
            consumer.stop();
        }
    }

    /**
     * This method is used by the application server during crash recovery.
     * It returns all the XAResources of the activated endpoints, that are described
     * through the activation spec. The application server uses these objects to 
     * query the RM of in-doubt transactions.
     *
     * @ returns an array of XAResource objects corresponding to the endpoints.
     */
    public XAResource[] getXAResources(ActivationSpec[] specs)
        throws ResourceException {
        ArrayList xars = new ArrayList();

        for (int i = 0; i < specs.length; i++) {
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
                    logger.log(Level.FINEST, "Added XA Resource : " + xaRes);
                } catch (Exception e) {
                    logger.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }

        return (XAResource[]) xars.toArray();
    }

    /**
     * WorkManager that is provided by the container.
     *
     * @returns work manager provided by the app server.
     */
    public WorkManager getWorkManager() {
        return getInstance().context.getWorkManager();
    }

    /**
     * Object builder factory object that is used to create JMS 
     * administered objects through jndi lookup or reflection.
     *
     * @return ObjectBuilderFactory util class.
     */
    public ObjectBuilderFactory getObjectBuilderFactory() {
        return getInstance().obf;
    }

    
    /**
     * Sets the log level.
     * 
     * @param level log level.
     */
    public void setLogLevel(String level) {
        logger.log(Level.FINEST, "Setting log level:" + level);
        this.logLevel = level;
        setLevelInLogger(level);
    }

    /**
     * Returns the log level for the RA.
     *
     * @return loglevel.
     */
    public String getLogLevel() {
        return logLevel;
    }

    /**
     * Returns the list of inbound endpoints.
     * 
     * @return table of endpoints.
     */
    private Hashtable getConsumers() {
        return getInstance().consumers;
    }

    /**
     * Sets the level for the RA logger.
     *
     * @param level loglevel.
     */
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
     *
     * @return listening method.
     */
    public Method getListeningMethod() {
        return getInstance().onMessageMethod;
    }

    /** 
     * Unique key for every endpoint.
     */
    class EndpointKey implements Serializable {
        /**
         * MEssage endpoint Factory
         */
        private MessageEndpointFactory mef;
        /**
         * Activation spec.
         */
        private ActivationSpec spec;

        /** 
         * Constructor.
         */
        public EndpointKey(MessageEndpointFactory mef, ActivationSpec spec) {
            this.mef = mef;
            this.spec = spec;
        }

        /**
         * Tests if 2 keys are the same.
         *
         * @param obj key object.
         */
        public boolean equals(Object obj) {
            EndpointKey other = (EndpointKey) obj;

            return other.mef.equals(this.mef) && other.spec.equals(this.spec);
        }

        /**
         * Hash code for the endpoint key.
         * 
         * @returns unique integer.
         */
        public int hashCode() {
            return mef.hashCode() + spec.hashCode();
        }
    }
}
