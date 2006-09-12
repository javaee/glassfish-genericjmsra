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
package com.sun.genericra.inbound;

import com.sun.genericra.GenericJMSRA;
import com.sun.genericra.util.*;

import java.util.logging.*;

import javax.jms.*;

import javax.resource.ResourceException;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.WorkManager;

import javax.transaction.xa.XAResource;

import com.sun.genericra.GenericJMSRA;
import com.sun.genericra.util.*;
import com.sun.genericra.monitoring.*;
/**
 * One <code>EndpointConsumer</code> represents one MDB deployment.
 * Important assumptions:
 *   - Each EndpointCOnsumer holds one InboundJmsResourcePool
 *     (ServerSessionPool) which holds a javax.jms.Connection object.
 *   - EndpointConsumer is also created when ra.getXAResources() is
 *     is called for transaction recovery.
 * @author Binod P.G
 */
public class EndpointConsumer {
    private static Logger logger;

    static {
        logger = LogUtils.getLogger();
    }

    private StringManager sm = StringManager.getManager(GenericJMSRA.class);
    private boolean transacted = false;
    private GenericJMSRA ra = null;
    private ActivationSpec spec = null;
    Object cf = null;
    Object dmdCf = null;
    Destination dest = null;
    Destination dmd = null;
    InboundJmsResourcePool jmsPool = null;
    private ConnectionConsumer consumer = null;
    private transient MessageEndpointFactory mef = null;
    private ReconnectHelper reconHelper = null;
    private boolean stopped = false;

    public EndpointConsumer(MessageEndpointFactory mef,
        javax.resource.spi.ActivationSpec actspec) throws ResourceException {
        actspec.validate();
        this.spec = (ActivationSpec) actspec;
        this.ra = (GenericJMSRA) spec.getResourceAdapter();

        this.mef = mef;
        initializeAdministeredObjects();
    }

    public EndpointConsumer(javax.resource.spi.ActivationSpec actspec)
        throws ResourceException {
        this(null, actspec);
    }

    public Object getDmdConnectionFactory() {
        return dmdCf;
    }

    public Object getConnectionFactory() {
        return cf;
    }
    
  

    public MessageEndpointFactory getMessageEndpointFactory() {
        return mef;
    }

    public ActivationSpec getSpec() {
        return spec;
    }

    public ResourceAdapter getResourceAdapter() {
        return ra;
    }

    public boolean isStopped() {
        return this.stopped;
    }

    public Connection getConnection() {
        return jmsPool.getConnection();
    }

    public void restart() throws ResourceException {
        consumer = _start(reconHelper.getPool(), dest);
    }

    public void start() throws ResourceException {
        try {
            this.transacted = mef.isDeliveryTransacted(this.ra.getListeningMethod());
        } catch (NoSuchMethodException e) {
            throw ExceptionUtils.newResourceException(e);
        }

        logger.log(Level.FINE,
            "Registering a endpoint consumer, transaction support :" +
            this.transacted);
        initialize(this.transacted);
        consumer = _start(jmsPool, dest);
    }

    public void initialize(boolean isTx) throws ResourceException {
        this.validate();
        jmsPool = new InboundJmsResourcePool(this, isTx);
        jmsPool.initialize();
    }

    public InboundJmsResourcePool getPool() {
        return this.jmsPool;
    }

    private ConnectionConsumer _start(InboundJmsResourcePool pool, 
                               Destination dst) throws ResourceException {
        ConnectionConsumer consmr = null;
        logger.log(Level.FINE, "Starting the message consumption");

        try {
            Connection con = pool.getConnection();    
            /*
             * Code for tackling the client id uniqueness requirement
             * for durable subscriptions
            */ 
            if (spec.getClientID() != null) {
                if ((!spec.getShareClientid()) && (spec.getInstanceCount() > 1))
                {           
                    try{          
                        String clientid = null;
                        if (spec.getInstanceClientId() != null) {
                            clientid = spec.getInstanceClientId();
                        }
                        else {
                            clientid = (spec.getInstanceID() == 0) ? 
                                    spec.getClientID() : ((spec.getClientID().substring(0, 
                                        (spec.getClientID().length()-1)))
                                            + spec.getInstanceID());
                        }
                        con.setClientID(clientid);                    
                        logger.log(Level.INFO, "Setting the clientID to : " + clientid);              
                    }
                    catch (Exception ce) {                     
                        logger.log(Level.SEVERE, "Failed to generate clientID to : " + ce.getMessage());                               
                    }              
                }
                else {                    
                    con.setClientID(spec.getClientID());
                    logger.log(Level.INFO, "Setting the clientID to : " + spec.getClientID());                
                }
                
                logger.log(Level.FINE,
                    "Setting the clientID to : " + spec.getClientID());
                con.setClientID(spec.getClientID());
            }

            if (spec.getSubscriptionDurability().equals(Constants.DURABLE)) {
                    String subscription_name = 
                            ((spec.getInstanceCount() > 1) && (spec.getInstanceID() != 0)) ?
                            (spec.getSubscriptionName() + spec.getInstanceID()) :
                            (spec.getSubscriptionName());
                    consmr = pool.createDurableConnectionConsumer(                
                    dst, subscription_name, spec.getMessageSelector(),1 );
                    logger.log(Level.FINE, "Created durable connection consumer" + dst);               
                consmr = con.createDurableConnectionConsumer((javax.jms.Topic) dst,
                        spec.getSubscriptionName(), spec.getMessageSelector(),
                        pool, 1);
                logger.log(Level.FINE,
                    "Created durable connection consumer" + dst);
            } else {
                consmr = pool.createConnectionConsumer(dst,
                        spec.getMessageSelector(), 1);
                logger.log(Level.FINE,
                    "Created non durable connection consumer" + dst);
            }

            con.start();
            this.reconHelper = new ReconnectHelper(pool, this);

            if (spec.getReconnectAttempts() > 0) {
                con.setExceptionListener(reconHelper);
            }
        } catch (JMSException je) {
            stop();
            throw ExceptionUtils.newResourceException(je);
        }

        logger.log(Level.INFO, "Generic resource adapter started consumption ");

        return consmr;
    }

    public void stop() {
        logger.log(Level.FINE, "Now stopping the message consumption");
        this.stopped = true;

        if (jmsPool != null) {
            try {
                jmsPool.destroy();
            } catch (Throwable t) {
                logger.log(Level.SEVERE, "" + t.getMessage(), t);
            }
        }

        closeConsumer();

        Connection con = jmsPool.getConnection();

        if (con != null) {
            try {
                con.close();
            } catch (Throwable t) {
                logger.log(Level.SEVERE, "" + t.getMessage(), t);
            }
        }
    }

    public void closeConsumer() {
        if (consumer != null) {
            try {
                consumer.close();
            } catch (Throwable t) {
                logger.log(Level.SEVERE, "" + t.getMessage(), t);
            }
        }
    }

    public void consumeMessage(Message message, InboundJmsResource jmsResource) {
        DeliveryHelper helper = jmsResource.getDeliveryHelper();
        helper.deliver(message, dmd);
    }

    void validate() throws ResourceException {
        if (this.transacted && !this.spec.getSupportsXA()) {
            String msg = sm.getString("provider_cannot_support_transaction");
            throw new ResourceException(msg);
        }

        if (spec.getSubscriptionDurability().equals(Constants.DURABLE)) {
            if (!(dest instanceof javax.jms.Topic)) {
                String msg = sm.getString("durable_shouldbe_topic");
                throw new ResourceException(msg);
            }
        }

        if (this.spec.getSupportsXA()) {
            if (!(cf instanceof javax.jms.XAConnectionFactory)) {
                String msg = sm.getString("cf_doesnot_supportsxa");
                throw new ResourceException(msg);
            }
        }
    }

    private String getAppropriateCFClassName() throws ResourceException {
        if (this.spec.getSupportsXA()) {
            if ((this.spec.getDestinationType() == null) ||
                    this.spec.getDestinationType().equalsIgnoreCase("")) {
                throw new ResourceException(
                    "DestionationType not specified in the activation spec.");
            }

            if (this.spec.getDestinationType().equalsIgnoreCase(Constants.QUEUE)) {
                return this.spec.getXAQueueConnectionFactoryClassName();
            } else if (this.spec.getDestinationType().equalsIgnoreCase(Constants.TOPIC)) {
                return this.spec.getXATopicConnectionFactoryClassName();
            } else {
                return this.spec.getXAConnectionFactoryClassName();
            }
        } else {
            if (this.spec.getDestinationType().equalsIgnoreCase(Constants.QUEUE)) {
                return this.spec.getQueueConnectionFactoryClassName();
            } else if (this.spec.getDestinationType().equalsIgnoreCase(Constants.TOPIC)) {
                return this.spec.getTopicConnectionFactoryClassName();
            } else {
                return this.spec.getConnectionFactoryClassName();
            }
        }
    }

    private String getAppropriateDMDCFClassName() throws ResourceException {
        if (this.spec.getDeadMessageDestinationType().equalsIgnoreCase(Constants.QUEUE)) {
            return this.spec.getQueueConnectionFactoryClassName();
        } else if (this.spec.getDeadMessageDestinationType().equalsIgnoreCase(Constants.TOPIC)) {
            return this.spec.getTopicConnectionFactoryClassName();
        } else {
            return this.spec.getConnectionFactoryClassName();
        }
    }

    private void initializeAdministeredObjects() throws ResourceException {
        String className = getAppropriateCFClassName();
        ObjectBuilder cfBuilder = null;
        ObjectBuilderFactory obf = this.ra.getObjectBuilderFactory();
        ObjectBuilder destBuilder = null;

        if (this.spec.getProviderIntegrationMode().equalsIgnoreCase(Constants.JNDI_BASED)) {
            cfBuilder = obf.createUsingJndiName(this.spec.getConnectionFactoryJndiName(),
                    this.spec.getJndiProperties());
            destBuilder = obf.createUsingJndiName(this.spec.getDestinationJndiName(),
                    this.spec.getJndiProperties());
        } else {
            cfBuilder = obf.createUsingClassName(className);
            cfBuilder.setProperties(this.spec.getConnectionFactoryProperties());
            destBuilder = obf.createUsingClassName(getDestinationClassNameFromType(
                        this.spec.getDestinationType()));
            destBuilder.setProperties(this.spec.getDestinationProperties());
        }

        String setMethod = spec.getCommonSetterMethodName();

        if (isNotNull(setMethod)) {
            cfBuilder.setCommonSetterMethodName(setMethod);
            destBuilder.setCommonSetterMethodName(setMethod);
        }

        this.cf = cfBuilder.build();
        this.dest = (Destination) destBuilder.build();

        ObjectBuilder dmdBuilder = null;
        ObjectBuilder dmdCfBuilder = null;

        if (spec.getSendBadMessagesToDMD()) {
            if (spec.getProviderIntegrationMode().equalsIgnoreCase(Constants.JNDI_BASED)) {
                dmdCfBuilder = obf.createUsingJndiName(this.spec.getDeadMessageConnectionFactoryJndiName(),
                        this.spec.getJndiProperties());
                dmdBuilder = obf.createUsingJndiName(this.spec.getDeadMessageDestinationJndiName(),
                        this.spec.getJndiProperties());
            } else {
                if (this.spec.getDeadMessageDestinationClassName() == null) {
                    dmdCfBuilder = obf.createUsingClassName(getAppropriateDMDCFClassName());
                    dmdCfBuilder.setProperties(this.spec.getDeadMessageConnectionFactoryProperties());
                    dmdBuilder = obf.createUsingClassName(getDestinationClassNameFromType(
                                this.spec.getDeadMessageDestinationType()));
                } else {
                    dmdCfBuilder = obf.createUsingClassName(getAppropriateCFClassName());
                    dmdCfBuilder.setProperties(this.spec.getConnectionFactoryProperties());
                    dmdBuilder = obf.createUsingClassName(this.spec.getDeadMessageDestinationClassName());
                }

                dmdBuilder.setProperties(this.spec.getDeadMessageDestinationProperties());
            }

            if (isNotNull(setMethod)) {
                dmdBuilder.setCommonSetterMethodName(setMethod);
                dmdCfBuilder.setCommonSetterMethodName(setMethod);
            }

            this.dmd = (Destination) dmdBuilder.build();
            this.dmdCf = dmdCfBuilder.build();
        }
    }

    private String getDestinationClassNameFromType(String destinationType) {
        if (destinationType.equals(Constants.QUEUE)) {
            return this.spec.getQueueClassName();
        } else if (destinationType.equals(Constants.TOPIC)) {
            return this.spec.getTopicClassName();
        } else {
            return this.spec.getUnifiedDestinationClassName();
        }
    }

    private boolean isNotNull(String s) {
        return ((s != null) && !s.trim().equals(""));
    }
}
