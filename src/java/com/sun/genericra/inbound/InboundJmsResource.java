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
import javax.resource.ResourceException;
import javax.resource.spi.*;
import javax.resource.spi.endpoint.*;
import javax.resource.spi.work.*;
import javax.jms.*;
import javax.transaction.xa.XAResource;
import java.util.logging.Logger;
import java.util.logging.Level;
import com.sun.genericra.GenericJMSRA;
import com.sun.genericra.util.*;

/**
 * ServerSession implementation as per JMS 1.1 specification.
 * This serves as a placeholder for a MessageEndpoint obtained from
 * application server.
 *
 * @author Binod P.G
 */
public class InboundJmsResource  implements ServerSession {

    private Session session;
    private XAResource xaresource;
    private MessageEndpoint endPoint;

    private boolean free;

    private GenericJMSRA ra;
    private InboundJmsResourcePool pool;
    private DeliveryHelper helper;

    private static Logger _logger;
    static {
        _logger = LogUtils.getLogger();
    }

    public InboundJmsResource(Session session, InboundJmsResourcePool pool) 
        throws JMSException{
        this(session, pool, null);
    }

    public InboundJmsResource(Session session, InboundJmsResourcePool pool,
                              XAResource xaresource) throws JMSException{
        this.session = session;
        this.xaresource = xaresource;
        this.pool = pool;
        this.ra = (GenericJMSRA) pool.getConsumer().getResourceAdapter();
    }

    public void start() throws JMSException {
        try {
            _logger.log(Level.FINER, "Provider is starting the message consumtion" );
            Work w = new WorkImpl(this);
            WorkManager wm = ra.getWorkManager();
            wm.scheduleWork(w);
        } catch (WorkException e) {
            throw ExceptionUtils.newJMSException(e);
        }
    }

    /**
     * Each time a serversession is checked out from the pool, the listener
     * will be recreated.
     */
    public InboundJmsResource refreshListener() throws JMSException {
        MessageListener listener = new MessageListener(this, pool);
        this.session.setMessageListener(listener);
        helper = new DeliveryHelper(this, pool);
        return this;
    }

    public void destroy() {
        if (session != null) {
            try {
                session.close();
            } catch (Exception e) {
                if (_logger.isLoggable(Level.FINE)) {
                    _logger.log(Level.FINE, e.getMessage(), e);
                }
            }
        }
        releaseEndpoint();
    }

    public boolean isFree() {
        return free;
    }

    public InboundJmsResource markAsBusy() {
        this.free = false;
        return this;
    }

    public InboundJmsResource markAsFree() {
        this.free = true;
        return this;
    }

    public DeliveryHelper getDeliveryHelper() {
        return this.helper;
    }

    public XAResource getXAResource() {
        return this.xaresource;
    }

    public Session getSession() {
        _logger.log(Level.FINEST, "Message provider got the session :" + session);
        return session;
    }

    public InboundJmsResourcePool getPool() {
        return this.pool;
    }

    public XASession getXASession() {
        return (XASession) session;
    }

    public MessageEndpoint getEndpoint() {
        return this.endPoint;
    }

    public void release() {
        getPool().put(this);
    }

    /**
     * Creates the MessageEndpoint and start the delivery.
     */
    public void refresh() throws JMSException {
        MessageEndpointFactory mef = pool.getConsumer().getMessageEndpointFactory();
        try {
            _logger.log(Level.FINER, "Creating message endpoint : " + xaresource);
            endPoint = mef.createEndpoint(helper.getXAResource());
            endPoint.beforeDelivery(this.ra.getListeningMethod());
            _logger.log(Level.FINE, "Binod.IN: Created endpoint : ");
        } catch (Exception e) {
            // TODO. Should we eat this exception?
            //throw ExceptionUtils.newJMSException(e);
        }
    }

    /**
     * Completes the Message delivery and release the MessageEndpoint.
     */
    public void releaseEndpoint() {
        try {
            if (this.endPoint != null) {
                this.endPoint.afterDelivery();
            }
        } catch (ResourceException re) {
            _logger.log(Level.WARNING, ""+ re.getMessage(), re);
        } finally {
            if (this.endPoint != null) {
                this.endPoint.release();
                _logger.log(Level.FINE, "Binod.OUT: released endpoint : ");
                this.endPoint = null;
            }
        }
    }
}
