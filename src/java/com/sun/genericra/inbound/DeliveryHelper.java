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

import javax.resource.*;
import javax.resource.spi.*;
import javax.resource.spi.endpoint.*;
import javax.jms.*;
import javax.transaction.xa.XAResource;
import java.util.logging.*;

import com.sun.genericra.util.*;
import com.sun.genericra.AbstractXAResourceType;

/**
 * Helper class that delivers a message to MDB.
 * Important assumptions:
 * - There is one delivery helper for each message(delivery).
 * - Redelivery will be carried out by the same DeliveryHelper.
 * @author Binod P.G
 */
public class DeliveryHelper {

    ActivationSpec spec;
    InboundJmsResource jmsResource;
    XAResource xar;
    Message msg = null;
    Destination dest = null;

    boolean transacted; 
    boolean sentToDmd =false; 

    private static Logger _logger;
    static {
        _logger = LogUtils.getLogger();
    }

    public DeliveryHelper(InboundJmsResource jmsResource, 
                          InboundJmsResourcePool pool) { 
        this.spec = pool.getConsumer().getSpec();
        this.jmsResource = jmsResource;
        this.transacted = pool.isTransacted();
        AbstractXAResourceType xarObject = null;
        if (redeliveryRequired()) {
            xarObject = new InboundXAResourceProxy(jmsResource.getXAResource());
        } else {
            xarObject = new SimpleXAResourceProxy(jmsResource.getXAResource());
            //this.xar = jmsResource.getXAResource();
        }
        xarObject.setRMPolicy(this.spec.getRMPolicy());
        xarObject.setConnection(pool.getConnection());
        this.xar = xarObject;
    }

    public boolean redeliveryRequired() {
        return this.transacted &&
               this.spec.getRedeliveryAttempts() > 0; 
    }

    public XAResource getXAResource() {
        return this.xar;
    }

    public void sendMessageToDMD() {
        _logger.log(Level.FINE, "Trying to send message  to DMD :" + dest);
        Session session = null;
        MessageProducer msgProducer = null;
        try {
            if (this.dest != null && this.spec.getSendBadMessagesToDMD()) {
                _logger.log(Level.FINE, "Sending the message to DMD :" + dest);
                InboundXAResourceProxy localXar = (InboundXAResourceProxy) this.xar;
                if (localXar.endCalled() == false) {
                    localXar.end(null, XAResource.TMSUCCESS);
                }
                localXar.prepare(null);
                Connection connection = jmsResource.getPool().getConnectionForDMD();
                session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
                msgProducer = session.createProducer(this.dest);
                msgProducer.send(this.msg);
                localXar.commit(null,false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.msg = null;
            this.dest = null;
            this.sentToDmd = false;

            if (msgProducer != null) {
                try {
                    msgProducer.close();
                } catch (Exception me) {
                    me.printStackTrace();
                }
            }
            if (session != null) {
                try {
                    session.close();
                } catch (Exception se) {
                    se.printStackTrace();
                }
            }
        }
    }

    public void deliver(Message message, Destination d) {
        this.msg = message;
        this.dest = d;
        deliver();
    }
    public void deliver() {

        int myattempts = 0;
        int attempts = this.spec.getRedeliveryAttempts();
        InboundXAResourceProxy localXar = null;

        while(true)  {
            try {
                deliverMessage(msg);
                if (localXar != null) {
                    localXar.setToRollback(true);
                }
                break;
            } catch(Exception e) {
                if (redeliveryRequired()) {
                    if (localXar == null) {
                        localXar = (InboundXAResourceProxy) xar;
                    }
                    localXar.setToRollback(false);
                }
                if (transacted) { 
                    try {
                        msg.setJMSRedelivered(true);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    if (myattempts < attempts) {
                        myattempts++;
                        _logger.log(Level.FINEST, "Releasing the endpoint after an exception");
                        this.jmsResource.releaseEndpoint();
                        try {
                            Thread.sleep(spec.getRedeliveryInterval() * 1000);
                            _logger.log(Level.FINE, "getting the endpoint after an exception");
                            this.jmsResource.refresh();
                        } catch (Exception ie) {
                            ie.printStackTrace();
                        }
                    } else {
                        this.markForDMD();
                        _logger.log(Level.FINEST, "Sent the message to DMD :" + dest);
                        break;
                    }
                } else {
                    break;
                }
            }
        } 
    }

    public void markForDMD() {
        this.sentToDmd = true;
    }

    public boolean markedForDMD(){
        return this.sentToDmd; 
    }

    private void deliverMessage(Message message) throws ResourceException {
        MessageEndpoint endPoint = jmsResource.getEndpoint();
        try {
            _logger.log(Level.FINEST, "Now it is feeding the message to MDB instance");
            ((javax.jms.MessageListener)endPoint).onMessage(message);
        } catch (Exception e) {
            if (transacted) {
                throw ExceptionUtils.newResourceException(e);
            }
        }
    }

}
