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

package com.sun.genericra.inbound.sync;

import com.sun.genericra.GenericJMSRA;
import com.sun.genericra.inbound.*;
import com.sun.genericra.inbound.async.DeliveryHelper;
import com.sun.genericra.inbound.async.MessageListener;
import com.sun.genericra.inbound.async.WorkImpl;
import com.sun.genericra.util.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.*;

import javax.resource.ResourceException;
import javax.resource.spi.*;
import javax.resource.spi.endpoint.*;
import javax.resource.spi.work.*;

import javax.transaction.xa.XAResource;


/**
 *
 */
public class SyncJmsResource extends AbstractJmsResource {
    private static Logger _logger;
    
    static {
        _logger = LogUtils.getLogger();
    }
    private boolean free;
    
    private SyncDeliveryHelper helper;
    
    private Work w ;
    
    private boolean stopWork = false;
    
    private int sessionid;
    
    public SyncJmsResource(Session session, SyncJmsResourcePool pool)
    throws JMSException {        
        this(session, pool, null);
    }  
    
    
    public SyncJmsResource(Session session, SyncJmsResourcePool pool,
            XAResource xaresource) throws JMSException {
        super(session, pool, xaresource);
    }
    
    public void setSessionid(int id) {
        sessionid = id;
    }
    public int getSessionid() {
        return sessionid;
    }
    public MessageConsumer getReceiver() throws JMSException {
        return getPool().createMessageConsumer(session);
    }
    public void start() throws JMSException {
        try {
            _logger.log(Level.FINER,
                    "Sync Provider is starting the message consumtion #" + sessionid);
            
            w = new SyncWorker(this);
            WorkManager wm = ra.getWorkManager();
            wm.scheduleWork(w);
        } catch (WorkException we) {
            throw ExceptionUtils.newJMSException(we);
        } 
    }
    
    /**
     * Each time a serversession is checked out from the pool, the listener
     * will be recreated.
     */
    public void refreshListener() throws JMSException {
        
        helper = new SyncDeliveryHelper(this, (SyncJmsResourcePool)pool);
        
      //  return this;
    }
    
    public boolean getIsWorkStopped() {
        return stopWork;
    }
    
    public void destroy() {        
        if (session != null) {
            try {
                stopWork = true;
                w.release();
                _logger.log(Level.FINE, "Released the Worker the session #" + sessionid);
                session.close();
                _logger.log(Level.FINE, "Closed the session #" + sessionid);
            } catch (Exception e) {
                if (_logger.isLoggable(Level.FINE)) {
                    _logger.log(Level.FINE, e.getMessage(), e);
                }
            }
        }        
       // releaseEndpoint();
    }
    
    public SyncDeliveryHelper getDeliveryHelper() {
        return this.helper;
    }
    
    public XAResource getXAResource() {
        return this.xaresource;
    }
    
    public Session getSession() {
        _logger.log(Level.FINEST, "Message provider got the session :" +
                session);
        
        return session;
    }
    
    public XASession getXASession() {
        return (XASession) session;
    }
    
    /**
     * Creates the MessageEndpoint and start the delivery.
     */
    public void refresh() throws JMSException {
        MessageEndpointFactory mef = pool.getConsumer()
        .getMessageEndpointFactory();       
        try {
            _logger.log(Level.FINER, "Creating message endpoint #" + sessionid +
                    xaresource);
            endPoint = mef.createEndpoint(helper.getXAResource());
            endPoint.beforeDelivery(this.ra.getListeningMethod());
            _logger.log(Level.FINE, "Created endpoint  #" + sessionid);
        } catch (Exception e) {
            e.printStackTrace();
            _logger.log(Level.SEVERE, "Refresh resource failed #" + sessionid);
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
                _logger.log(Level.FINE,"After Delivery success in SyncJmsResource");
            }
        } catch (Exception re) {
            _logger.log(Level.SEVERE, "After delivery failed in resource #" + sessionid
                        + re.getMessage());
        } finally {
            this.release();
            /*
            if (this.endPoint != null) {
                try {
                    this.endPoint.release();
                    _logger.log(Level.FINE, "SyncJmsResource: released endpoint in #" 
                            + sessionid);
                } catch (Exception e) {
                    _logger.log(Level.SEVERE,
                            "SyncJmsResource: release endpoint failed #" + sessionid);                    
                }                
                this.endPoint = null;
            }
             */
        }
    }
    
    public void release() {
            if (this.endPoint != null) {
                try {
                    this.endPoint.release();
                    _logger.log(Level.FINE, "SyncJmsResource: released endpoint in #" 
                            + sessionid);
                } catch (Exception e) {
                    _logger.log(Level.SEVERE,
                            "SyncJmsResource: release endpoint failed #" + sessionid);                    
                }                
                this.endPoint = null;
            }        
    }
}
