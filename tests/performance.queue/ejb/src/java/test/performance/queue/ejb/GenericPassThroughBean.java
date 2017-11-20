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
