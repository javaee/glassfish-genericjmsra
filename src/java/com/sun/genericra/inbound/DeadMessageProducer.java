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

package com.sun.genericra.inbound;

import com.sun.genericra.inbound.async.InboundJmsResourcePool;
import com.sun.genericra.util.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.*;


/**
 * InternalMessageProducer implementation used by
 * Inbound code that send the messages to dead
 * message destination.
 *
 * The objective of this class is to handle
 * any type of JMS destination
 *
 * @author Binod P.G
 */
public class DeadMessageProducer {
    private static Logger logger;

    static {
        logger = LogUtils.getLogger();
    }

    private Session session; 
    private String destinationType;
    private MessageProducer producer;

    public DeadMessageProducer(Connection con, AbstractJmsResourcePool pool, Destination dest)
                throws JMSException {
        
        destinationType = pool.getConsumer().getSpec()
                              .getDeadMessageDestinationType();
        logger.log(Level.FINE,
            "DeadMessageDestinationType is" +
            " obtained for message sender : " + destinationType);
         
        createSession(con);
        createProducer(dest);
    }

    private void createSession(Connection con) throws JMSException {
        if (destinationType.equals(Constants.QUEUE)) {
            session = ((QueueConnection) con).createQueueSession(false,
                    Session.AUTO_ACKNOWLEDGE);
        } else if (destinationType.equals(Constants.TOPIC)) {
            session = ((TopicConnection) con).createTopicSession(false,
                    Session.AUTO_ACKNOWLEDGE);
        } else {
            session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
        }
    }

    private void createProducer(Destination dest) throws JMSException {
        if (destinationType.equals(Constants.QUEUE)) {
            producer = ((QueueSession) session).createSender((Queue) dest);
        } else if (destinationType.equals(Constants.TOPIC)) {
            producer = ((TopicSession) session).createPublisher((Topic) dest);
        } else {
            producer = session.createProducer(dest);
        }
    }

    public void send(Message msg) throws JMSException {
        try {
            if (destinationType.equals(Constants.QUEUE)) {
                ((QueueSender) producer).send(msg);
            } else if (destinationType.equals(Constants.TOPIC)) {
                ((TopicPublisher) producer).publish(msg);
            } else {
                producer.send(msg);
            }
        } catch (Exception e) {
            throw ExceptionUtils.newJMSException(e);
        }
    }

    public void close() throws JMSException {
        try {
            if (producer != null) {
                producer.close();
            }
        } catch (Exception e) {
            logger.log(Level.FINE, e.getMessage(), e);
        }

        try {
            session.close();
        } catch (Exception e) {
            logger.log(Level.FINE, e.getMessage(), e);
        }
    }
}
