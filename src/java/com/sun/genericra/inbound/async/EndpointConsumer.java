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

package com.sun.genericra.inbound.async;

import com.sun.genericra.GenericJMSRA;
import com.sun.genericra.inbound.*;
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
public class EndpointConsumer extends com.sun.genericra.inbound.AbstractConsumer {
    

    InboundJmsResourcePool jmsPool = null;
    private ConnectionConsumer consumer = null;
    private ReconnectHelper reconHelper = null;


    public EndpointConsumer(MessageEndpointFactory mef,
        javax.resource.spi.ActivationSpec actspec) throws ResourceException {
        super(mef, actspec);       
    }

    public EndpointConsumer(javax.resource.spi.ActivationSpec actspec)
        throws ResourceException {
        this(null, actspec);
    }

  public AbstractJmsResourcePool getPool() {
        return jmsPool;
    }

    public Connection getConnection() {
        return jmsPool.getConnection();
    }

    public void restart() throws ResourceException {
        consumer = _start(reconHelper.getPool(), dest);
    }

    public void start() throws ResourceException {
        setTransaction();
        logger.log(Level.FINE,
            "Registering a endpoint consumer, transaction support :" +
            this.transacted);
        initialize(this.transacted);
        consumer = _start(jmsPool, dest);
    }

    public void initialize(boolean isTx) throws ResourceException {
        super.validate();
        jmsPool = new InboundJmsResourcePool(this, isTx);
        jmsPool.initialize();
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
           
            this.setClientId();
            if (spec.getSubscriptionDurability().equals(Constants.DURABLE)) {
                    String subscription_name = 
                            ((spec.getInstanceCount() > 1) && (spec.getInstanceID() != 0)) ?
                            (spec.getSubscriptionName() + spec.getInstanceID()) :
                            (spec.getSubscriptionName());
                    consmr = pool.createDurableConnectionConsumer(                
                    dst, subscription_name, spec.getMessageSelector(),1 );
                    logger.log(Level.FINE, "Created durable connection consumer" + dst);               
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
            // stop();
            closeConsumer();
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

}
