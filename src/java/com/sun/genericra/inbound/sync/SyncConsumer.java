/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
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
