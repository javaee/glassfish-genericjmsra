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
import javax.jms.*;
import java.util.logging.*;

import com.sun.genericra.util.*;

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

    public ReconnectHelper(InboundJmsResourcePool pool, EndpointConsumer consumer) { 
        this.pool = pool;
        this.consumer = consumer;
        this.attempts = consumer.getSpec().getReconnectAttempts();
        this.interval = consumer.getSpec().getReconnectInterval() * 1000;
    }

    public void onException(JMSException exception) { 
        if (reconnectInitiated || this.consumer.isStopped() ) {
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
        }

        boolean result = false;
        for (int i =0; i < attempts; i++) {
            _logger.log(Level.INFO, "Reconnect attempt->" + i);
            try {
                this.pool.initialize();
                this.pool.releaseAllWaitingThreads();
		createConsumer();
                _logger.log(Level.INFO, "Reconnect successful with pool->" + i);
                result = true;
                break;
            } catch (ResourceException re) {
                _logger.log(Level.INFO, "Reconnect attempt failed. Now sleeping for" + interval);
		
                // TODO. log a message.
                try {
                    Thread.sleep(interval);
                } catch (Exception e) {
                }
            }
        }
        return result;
    }

    public InboundJmsResourcePool getPool() {
        return this.pool;
    }

}
