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
package com.sun.s1peqe.connector.mq.simplestress.ejb;

import java.io.Serializable;
import java.rmi.RemoteException;
import javax.ejb.EJBException;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.ejb.CreateException;
import javax.naming.*;
import javax.jms.*;
/**
 * A simple message drive bean, which on receipt of a message, publishes a message to a
 * reply Queue.
 * After replying to simulate redelivery of message, a runtimeexception is thrown
 * for all messages.
 * 
 * Half the messages RuntimeExceptions are thrown > RedeliveryAttempts and hence sent
 * to the Dead Message Destination. The appclient checks both the ReplyQueue
 * and the Dead Message Queue before printing a PASS. 
 */
public class SimpleMessageBean implements MessageDrivenBean,
    MessageListener {

    Context                 jndiContext = null;
    QueueConnectionFactory  queueConnectionFactory = null;
    QueueConnection         queueConnection = null;
    QueueSession            queueSession = null;
    Queue                   queue = null;
    QueueSender             queueSender = null;
    final int               NUM_MSGS = 100;
    final int               MAX_PR = 50;

    private transient MessageDrivenContext mdc = null;
    private Context context;

    public SimpleMessageBean() {
        System.out.println("In SimpleMessageBean.SimpleMessageBean()");
    }

    public void setMessageDrivenContext(MessageDrivenContext mdc) {
        System.out.println("In "
            + "SimpleMessageBean.setMessageDrivenContext()");
	this.mdc = mdc;
    }

    public void ejbCreate() {
	System.out.println("In SimpleMessageBean.ejbCreate()");
        try {
            jndiContext = new InitialContext();
            queueConnectionFactory = (QueueConnectionFactory)
                jndiContext.lookup
                ("java:comp/env/jms/QCFactory");
            queue = (Queue) jndiContext.lookup("java:comp/env/jms/clientQueue");
        } catch (NamingException e) {
            System.out.println("JNDI lookup failed: " +
                e.toString());
        }
    }

    public void onMessage(Message inMessage) {
        TextMessage msg = null;

        try {
            if (inMessage instanceof TextMessage) {
                msg = (TextMessage) inMessage;
                System.out.println("MESSAGE BEAN: Message received: "
                    + msg.getText());
		long sleepTime = msg.getLongProperty("sleeptime");
		System.out.println("Sleeping for : " + sleepTime + " milli seconds ");
		Thread.sleep(sleepTime);
		queueConnection =
	            queueConnectionFactory.createQueueConnection();
		queueSession =
	            queueConnection.createQueueSession(false,
		    Session.AUTO_ACKNOWLEDGE);
		queueSender = queueSession.createSender(queue);
		TextMessage message = queueSession.createTextMessage();

		message.setText("REPLIED:" + msg.getText());
                int incomingId =  msg.getIntProperty("id");
		message.setIntProperty("replyid", incomingId );
		System.out.println("Sending message: " +
		message.getText());
		queueSender.send(message);
                String propName = "thrownException";
                    if (msg.getJMSRedelivered() == false) {
                        throw new RuntimeException("Setting redelivery flag"+msg.getJMSPriority());
                    }
                    if (msg.getJMSPriority() == Message.DEFAULT_PRIORITY) {
                        if (incomingId % 2 == 0) {
                            msg.setJMSPriority(Message.DEFAULT_PRIORITY -1);
                        }
                        throw new RuntimeException("Resetting the priority");
                    }
            } else {
                System.out.println("Message of wrong type: "
                    + inMessage.getClass().getName());
            }
        } catch (RuntimeException re) {
            System.out.println("Throwing >>> Runtime except : " + re.getMessage());
            throw re;
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
	    try {
	        queueSession.close();
		queueConnection.close();
	    } catch (Exception e) {
	    }
	}
    }  // onMessage

    public void ejbRemove() {
        System.out.println("In SimpleMessageBean.remove()");
    }
} // class

