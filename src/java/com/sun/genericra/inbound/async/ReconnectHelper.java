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
