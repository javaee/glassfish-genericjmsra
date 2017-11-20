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

import javax.resource.ResourceException;
import javax.resource.spi.endpoint.MessageEndpointFactory;

import java.util.logging.Level;
import java.util.ArrayList;
import com.sun.genericra.inbound.AbstractJmsResourcePool;
import com.sun.genericra.util.ExceptionUtils;
import javax.jms.*;
import javax.transaction.TransactionManager;
/**
 *
 * @author rp138409
 */
public class SyncConsumer extends  com.sun.genericra.inbound.AbstractConsumer {
    
    private boolean mIsStopped = true;
    private String applicationName;
    private int mBatchSize;
    private boolean mHoldUntilAck;
    private SyncJmsResourcePool jmsPool;
    private ArrayList mWorkers;
    private SyncReconnectHelper reconHelper = null;
    /** Creates a new instance of SyncConsumer */
    public SyncConsumer(MessageEndpointFactory mef,
            javax.resource.spi.ActivationSpec actspec) throws ResourceException {
        super(mef, actspec);
    }
    
    
    public AbstractJmsResourcePool getPool() {
        return jmsPool;
    }
    public void initialize(boolean istx) throws ResourceException {
        super.validate();
        
        if ((mBatchSize > 1 || mHoldUntilAck) && this.transacted) {
            TxMgr txmgr = new TxMgr();
            TransactionManager mgr = null;
            try {
                mgr = txmgr.getTransactionManager();
            }catch (Exception e) {
                throw ExceptionUtils.newResourceException(e);
            }
            if (( mgr == null) && mHoldUntilAck) {
                logger.log(Level.FINE, "TxMgr could not be obtained: ");
                throw new RuntimeException("Could not obtain TxMgr which is crucial for HUA mode: " );
            }
            
        }
        jmsPool = new SyncJmsResourcePool(this, istx);
        jmsPool.initialize();
    }
    
    public void start() throws ResourceException {
        setTransaction();
        initialize(this.transacted);
        _start(jmsPool, this.dest);
    }
    public void restart() throws ResourceException {
         _start(reconHelper.getPool(), dest);
    }
    private void _start(SyncJmsResourcePool pool, Destination destination) throws ResourceException {
        try {
            this.reconHelper = new SyncReconnectHelper(pool, this);
            if (spec.getReconnectAttempts() > 0) {
                pool.getConnection().setExceptionListener(reconHelper);
            }
            pool.getConnection().start();
        } catch (JMSException e) {
            stop();
            throw ExceptionUtils.newResourceException(e);
        }
    }
    
    public void stop() {
        try {
            jmsPool.destroy();
            logger.log(Level.FINE, "Destroyed the pool ");
            if (jmsPool.getConnection() != null) {
                jmsPool.getConnection().close();
                logger.log(Level.FINE, "Closed the connection ");
            }
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Unexpected exception stopping JMS connection: " + ex, ex);
        }
    }
    
    public javax.jms.Connection getConnection() {
        return jmsPool.getConnection();
    }
}
