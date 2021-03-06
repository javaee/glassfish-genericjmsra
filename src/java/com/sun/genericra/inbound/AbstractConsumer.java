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

package com.sun.genericra.inbound;


import javax.resource.ResourceException;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.endpoint.MessageEndpointFactory;


import com.sun.genericra.GenericJMSRA;
import com.sun.genericra.util.LogUtils;
import com.sun.genericra.util.*;

import javax.jms.Destination;
import java.util.logging.Logger;

import java.util.logging.Level;
import com.sun.genericra.inbound.ActivationSpec;
/**
 *
 * @author rp138409
 */
public abstract class AbstractConsumer {
    
    protected static Logger logger;
    
    protected static StringManager sm = StringManager.getManager(GenericJMSRA.class);
    static {
        logger = LogUtils.getLogger();
    }
    protected GenericJMSRA ra = null;
    protected ActivationSpec spec = null;
    Object cf = null;
    Object dmdCf = null;
    protected Destination dest = null;
    protected Destination dmd = null;
    protected transient MessageEndpointFactory mef = null;
    protected boolean stopped = false;
    protected boolean transacted = false;
    /** Creates a new instance of AbstractConsumer */
    public AbstractConsumer(MessageEndpointFactory mef,
            javax.resource.spi.ActivationSpec actspec) throws ResourceException {
        actspec.validate();
        this.spec = (ActivationSpec) actspec;
        this.ra = (GenericJMSRA) spec.getResourceAdapter();
        this.mef = mef;
        initializeAdministeredObjects();
    }
    
    public Destination getDestination(){
        return this.dest;
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
    
    public Destination getDmdDestination() {
        return this.dmd;
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
    
    public void validate() throws ResourceException {
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
    
  
    public void setTransaction()  {
        try {
            this.transacted = mef.isDeliveryTransacted(this.ra.getListeningMethod());
        } catch (NoSuchMethodException e) {
            ;
        }
    }
    
    public void setClientId() throws ResourceException {
        try {
            if (spec.getClientID() != null) {                
                if ((!spec.getShareClientid()) && (spec.getInstanceCount() > 1)) {
                    try{
                        String clientid = null;
                        if (spec.getInstanceClientId() != null) {
                            clientid = spec.getInstanceClientId();
                        } else {
                            clientid = (spec.getInstanceID() == 0) ?
                                spec.getClientID() : ((spec.getClientID().substring(0,
                                    (spec.getClientID().length()-1)))
                                    + spec.getInstanceID());
                        }
                        getConnection().setClientID(clientid);
                        logger.log(Level.INFO, "Setting the clientID to : " + clientid);
                    } catch (Exception ce) {
                        logger.log(Level.SEVERE, "Failed to generate clientID to : " + ce.getMessage());
                    }
                } else {
                    getConnection().setClientID(spec.getClientID());
                    logger.log(Level.INFO, "Setting the clientID to : " + spec.getClientID());
                }
            }
        }catch (Exception e) {
            throw ExceptionUtils.newResourceException(e);
        }
    }
    public abstract void initialize(boolean isTx) throws ResourceException ;
    
    public abstract void start() throws ResourceException ;
    
    public abstract void stop();
    
    public abstract javax.jms.Connection getConnection();
    
    public abstract AbstractJmsResourcePool getPool();
}
