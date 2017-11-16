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
