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
package com.sun.ejte.j2ee.genericra.ejb;

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
 * reply destination.
 *
 */
public class SimpleMessageBean implements MessageDrivenBean,
    MessageListener {

    Context                 jndiContext = null;
    QueueConnectionFactory  queueConnectionFactory = null;
    Queue                   queue = null;
    final int               NUM_MSGS = 100;
    Send 		sendEjb;
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
	    SendHome sendHome = (SendHome)jndiContext.lookup("java:comp/env/ejb/Send");
	 try{
	    sendEjb = sendHome.create();
	}
	catch (Exception e) {
		e.printStackTrace();
	}
            queueConnectionFactory = (QueueConnectionFactory)
                jndiContext.lookup
                ("java:comp/env/jms/MyQueueConnectionFactory");
            queue = (Queue) jndiContext.lookup("java:comp/env/jms/MDB_REPLY_QUEUE");
		System.out.println("REPLY_QUEUE " + queue);
        } catch (NamingException e) {
            System.out.println("JNDI lookup failed: " +
                e.toString());
        }
    }

    public void onMessage(Message inMessage){
        TextMessage msg = null;

    QueueConnection         queueConnection = null;
    QueueSession            queueSession = null;
    QueueSender             queueSender = null;

        try {
            if (inMessage instanceof TextMessage) {
                msg = (TextMessage) inMessage;
                System.out.println("MESSAGE BEAN: Message received: "
                    + msg.getText());
		queueConnection =
	            queueConnectionFactory.createQueueConnection();
		queueSession =
	            queueConnection.createQueueSession(false,
		    Session.AUTO_ACKNOWLEDGE);
		queueSender = queueSession.createSender(queue);
		queueSender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		sendEjb.sendTextMessageToQ(msg.getText());
		TextMessage message = queueSession.createTextMessage();
		message.setText("REPLIED:" + msg.getText());
		System.out.println("Sending message: " +
		message.getText());
		message.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
		queueSender.send(message);
            } else {
                System.out.println("Message of wrong type: "
                    + inMessage.getClass().getName());
            }
        } catch (Exception te) {
            te.printStackTrace();
            mdc.setRollbackOnly();
            //throw new RuntimeException(te);
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

