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
package com.sun.genericra.inbound.async;

import com.sun.genericra.inbound.*;
import com.sun.genericra.util.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.Message;


/**
 * MessageListener for the server session. This is the basic receiver of
 * the message.
 *
 * @author Binod P.G
 */
public class MessageListener implements javax.jms.MessageListener {
    private static Logger _logger;

    static {
        _logger = LogUtils.getLogger();
    }

    private static boolean debug = false;
    private InboundJmsResource jmsResource;
    private InboundJmsResourcePool pool;
    private EndpointConsumer consumer;

    public MessageListener(InboundJmsResource jmsResource,
        InboundJmsResourcePool pool) {
        this.jmsResource = jmsResource;
        this.consumer = (EndpointConsumer)pool.getConsumer();
    }

    public void onMessage(Message message){
        if (debug) {
            _logger.log(Level.FINE, "Consuming the message :" + message);
        }
        consumer.consumeMessage(message, jmsResource);
    }
}
