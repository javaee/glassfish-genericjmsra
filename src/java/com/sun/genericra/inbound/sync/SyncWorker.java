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
import com.sun.genericra.util.*;

import java.util.logging.*;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import javax.resource.spi.work.*;
import javax.transaction.Status;
import javax.transaction.Transaction;
import javax.transaction.xa.XAResource;

public class SyncWorker implements Work {
    private static Logger _logger;
    static {
        _logger = LogUtils.getLogger();
    }
    
    private volatile boolean mIsStopped = true;
    private Object mIsStoppedLock = new Object();
    private javax.jms.MessageConsumer mReceiver;
    private javax.jms.Session mSess;
    private SyncJmsResource resource = null;
    private int sessionid;
    private static long TIMEOUT = 100;
    
    private static long WAIT_TIMEOUT = 1000;
    boolean requiresrefresh = true;
    /**
     * Constructor
     *
     * @param name threadname
     */
    public SyncWorker(SyncJmsResource res) throws JMSException {
        this.resource = res;
        sessionid = this.resource.getSessionid();
        mReceiver = this.resource.getReceiver();
    }
    
    
    /**
     * Closes the allocated resources. Must be called after the thread has ended.
     */
    private void close() {
        
        /* Close the connection here  ?, no, there might be
         * other sessions using the connection, close only the receiver.
         */
        if (mReceiver != null) {
            try {
                mReceiver.close();
            } catch (JMSException e) {
                _logger.log(Level.WARNING, "Non-critical failure to close a " +
                        "message consumer: " + e);
            }
            mReceiver = null;
        }
        //Thread.dumpStack();
        _logger.log(Level.FINE,"Closed Synchronouse receiver #" +
                sessionid);
        // this.resource.releaseEndpoint();
    }
    
    
    
    /**
     * Called by a separate Thread: polls the receiver until it is time to exit
     * or if an exception occurs.
     *
     * @see java.lang.Runnable#run()
     */
    public void run() {
        synchronized (mIsStoppedLock) {
            if (!mIsStopped) {
                return;
            }
            mIsStopped = false;
        }
        _logger.log(Level.INFO,"Starting synchronouse receiver #" +
                sessionid);
        for (;;) {
            try {
		//_logger.log(Level.FINEST,"Running Receiver #" + sessionid);
                if (requiresrefresh) {
		_logger.log(Level.FINE,"Refreshing Receiver #" + sessionid);
                    this.resource.refreshListener();
                    this.resource.refresh();
                    requiresrefresh = false;
		_logger.log(Level.FINE,"Refresed Receiver #" + sessionid);
                }
                Message m = mReceiver.receive(TIMEOUT);
                if (m != null) {
                    requiresrefresh = true;
                    SyncDeliveryHelper helper = this.resource.getDeliveryHelper();
                    /* The destination will not be null when the TODO for
                     * dmd is done
                     */
                    
                    /* The helper is supposed to call onMessage on the endpoint
                     * and depending on the the success /failure of it  it has to
                     * do the needful to the inbound message (commit/rollback).
                     */
                    helper.deliver(m, this.resource.getPool().getConsumer().getDmdDestination());
		_logger.log(Level.FINE,"Delivered message Receiver #" + sessionid);
                }else {
                    requiresrefresh = false;
                }
                
                synchronized (mIsStoppedLock) {
                    if (mIsStopped) {
                        _logger.log(Level.INFO, "Stopping synchronous receiver #" +
                                sessionid);
                        mIsStoppedLock.notifyAll();
                        break;
                    }
                }
                
            } catch (Exception ex) {
                _logger.log(Level.SEVERE, "Exception during receive , Receiver #" +
                        sessionid + ex);
                break;
            } catch (Throwable ex) {
                _logger.log(Level.SEVERE, "Exception during receive, Receiver #"
                        + sessionid + ex);
                break;
            } finally {
                if (requiresrefresh) {
                    try {
                        this.resource.releaseEndpoint();
                    } catch (Exception ee) {
                        ;
                    }
                    SyncDeliveryHelper helper = this.resource.getDeliveryHelper();
                    if (helper.markedForDMD()) {
                        helper.sendMessageToDMD();
                    }
                    
                }
                /* Check for DMD sending part, at this point, tha XA will
                 * be in end state, its a bit tricky if we want to include
                 * the DMD sending part in the same inbound transaction.
                 * If DMD send is successful then we have to commit inbound,
                 * else we have to roll back. This would guarantee that there
                 * is no message loss. TODO
                 */
                
            }
            
        }
        /* Shutdown everything and close the thread.
         * We expect this to happen only when the consumer is closed.
         * We dont have a logic to create new receiver threads instead of
         * a closed thread, so this thread should be alive for the duration of
         * the consumer
         */
        this.resource.releaseEndpoint();
         _logger.log(Level.FINE, "Closing the receiver from run #" + sessionid);
        close();
    }
    /**
     * Indicates if this object has been stopped
     *
     * @return true if the state is stopped
     */
    public boolean isStopped() {
        synchronized (mIsStoppedLock) {
            return mIsStopped;
        }
    }
    public void release() {
        synchronized (mIsStoppedLock) {
            if (mIsStopped) {
                return;
            }
            _logger.log(Level.FINE, "Stopping the receiver #" + sessionid);
            mIsStopped = true;
            try {
                mIsStoppedLock.wait(WAIT_TIMEOUT);
            }catch (InterruptedException ie) {
                _logger.log(Level.FINE, "Notification received for the receiver #" + sessionid);
            }
            /* Close it here just to be sure that the session is not closed
             * before the receiver is,
             */
            _logger.log(Level.FINE, "Closing the receiver from release #" + sessionid);
            close();
            
        }
    }
    
}
