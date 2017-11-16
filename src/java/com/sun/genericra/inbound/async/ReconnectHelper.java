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

package com.sun.genericra.inbound.async;

import com.sun.genericra.inbound.*;
import com.sun.genericra.util.*;

import java.util.logging.*;

import javax.jms.*;

import javax.resource.*;
import javax.resource.spi.*;


/**
 * Helper class that enables redelivery in case connection
 * generates an Exception.
 *
 * @author Binod P.G
 */
public class ReconnectHelper implements ExceptionListener {
    private static Logger _logger;

    static {
        _logger = LogUtils.getLogger();
    }

    private InboundJmsResourcePool pool = null;
    private EndpointConsumer consumer = null;
    private int attempts = 0;
    private long interval = 0;
    private boolean reconnectInitiated = false;

    public ReconnectHelper(InboundJmsResourcePool pool,
        EndpointConsumer consumer) {
        this.pool = pool;
        this.consumer = consumer;
        this.attempts = consumer.getSpec().getReconnectAttempts();
        this.interval = consumer.getSpec().getReconnectInterval() * 1000;
    }

    public void onException(JMSException exception) {
        if (reconnectInitiated || this.consumer.isStopped()) {
            _logger.log(Level.INFO, "Reconnect is in progress");

            return;
        } else {
            reconnectInitiated = true;
        }

        _logger.log(Level.INFO, "Reconnecting now");
        _logger.log(Level.INFO, "Reconnecting now");
        _logger.log(Level.INFO, "Reconnecting now");
         if (reestablishPool()) {
                // this.consumer.closeConsumer();
                // this.consumer.restart();
                _logger.log(Level.INFO, "Reconnected!!");

                // TO DO i18n
            } else {
                _logger.log(Level.SEVERE, "Reconnect failed in pool!!");

                // TO DO i18n 
            }
    }
    private void createConsumer() throws ResourceException {
        try {
                this.consumer.closeConsumer();
                this.consumer.restart();
        } catch (ResourceException re) {
                _logger.log(Level.INFO,"Reconnection failed, Cannot create consumer");
                try {
                        this.pool.stop();
                        // this.consumer.stop();
                }catch (Exception e) {
                        _logger.log(Level.SEVERE, "Reconnect failed while stopping pool");
                }
                _logger.log(Level.INFO,"Stopping the consumer");
                throw re;
        }
    }

    /**
     * Pause all waiting threads. recreate the serversession pool.
     */
    private boolean reestablishPool() {
        try {
            this.pool.stop();
        } catch (JMSException je) {
            _logger.log(Level.SEVERE, "Reconnect failed while stopping pool");
            je.printStackTrace();

            // TO DO log a message 
            return false;
        } catch (Exception e) {
            _logger.log(Level.SEVERE,
                "Reconnect failed while stopping pool " + e);

            // TO DO log a message 
        }

        boolean result = false;

        for (int i = 0; i < attempts; i++) {
            _logger.log(Level.INFO, "Reconnect attempt->" + i);

            try {
                this.pool.initialize();
                this.pool.releaseAllWaitingThreads();
                createConsumer();
                _logger.log(Level.INFO, "Reconnect successful with pool->" + i);
                result = true;

                break;
            } catch (ResourceException re) {
                _logger.log(Level.INFO,
                    "Reconnect attempt failed. Now sleeping for" + interval);

                // TODO. log a message.
                try {
                    Thread.sleep(interval);
                } catch (Exception e) {
                    _logger.info("Thread.sleep exception");
                }
            }
        }

        return result;
    }

    public InboundJmsResourcePool getPool() {
        return this.pool;
    }
}
