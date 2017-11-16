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
