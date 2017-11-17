/**
 * Copyright (c) 2004-2017 Oracle and/or its affiliates. All rights reserved.
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

package test.performance.queue.ejb;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import test.common.ejb.GenericMDB;

public abstract class GenericPassThroughBean extends GenericMDB {

    private static final Logger logger = Logger.getLogger(GenericPassThroughBean.class.getName());

    // set this to true to log every message received
    boolean logMessages=false;
  
    // set the delivery mode of the outgoing message
    static int deliveryMode=DeliveryMode.PERSISTENT;

    // increasing this sleep time will cause more MDBs to be put into use - don't use when measuring performance!
    int beanSleepTimeMillis = 0;

    public void onMessage(Message message) {

        if (monitorNoOfBeansInUse) incrementBeansInUseCount();
        try {
            if (beanSleepTimeMillis>0){
                Thread.sleep(beanSleepTimeMillis);
            }
            TextMessage tm = (TextMessage) message;
            String text = tm.getText();
            if (logMessages) logger.log(Level.INFO, "Received message: " + text);
            sendJMSMessageToOutboundQueue(text);
            if (reportThroughput) updateMessageCount();
        } catch (InterruptedException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "InterruptedException encountered", ex);
        } catch (JMSException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error processing message", ex);
        } finally {
            if (monitorNoOfBeansInUse) decrementBeansInUseCount();
        }
    }

    private void sendJMSMessageToOutboundQueue(String text) throws JMSException {
        Connection connection = null;
        Session session = null;
        try {
            connection = getOutboundConnectionFactory().createConnection();
            // Note that the arguments to createSession() are ignored
            session = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
            TextMessage tm = session.createTextMessage(text);

            MessageProducer messageProducer = session.createProducer(getOutboundDestination());
            messageProducer.setDeliveryMode(deliveryMode);
            messageProducer.send(tm);
            messageProducer.close();
        } finally {
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public abstract ConnectionFactory getOutboundConnectionFactory();
    public abstract Destination getOutboundDestination();

}
