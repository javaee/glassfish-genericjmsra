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
package test.stress.topic.nondurable.ejb;

import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import test.common.ejb.GenericMDB;

/**
 * A simple message drive bean, which on receipt of a message, publishes a message to a reply destination.
 * 
 */
public class SimpleMessageBean extends GenericMDB implements MessageDrivenBean, MessageListener {

	Context jndiContext = null;
	QueueConnectionFactory queueConnectionFactory = null;
	Queue queue = null;
	final int NUM_MSGS = 100;

	private transient MessageDrivenContext mdc = null;
	private Context context;

	public SimpleMessageBean() {
		System.out.println("In SimpleMessageBean.SimpleMessageBean()");
	}

	public void setMessageDrivenContext(MessageDrivenContext mdc) {
		System.out.println("In " + "SimpleMessageBean.setMessageDrivenContext()");
		this.mdc = mdc;
	}

	public void ejbCreate() {
		System.out.println("In SimpleMessageBean.ejbCreate()");
		try {
			jndiContext = new InitialContext();
			queueConnectionFactory = (QueueConnectionFactory) jndiContext.lookup("java:comp/env/jms/QCFactory");
			queue = (Queue) jndiContext.lookup("java:comp/env/jms/outboundQueue");
		} catch (NamingException e) {
			System.out.println("JNDI lookup failed: " + e.toString());
		}
	}

	public void onMessage(Message inMessage) {
		TextMessage msg = null;

		QueueConnection queueConnection = null;
		QueueSession queueSession = null;
		QueueSender queueSender = null;

		try {
			if (inMessage instanceof TextMessage) {
				msg = (TextMessage) inMessage;
				System.out.println("MESSAGE BEAN: Message received: " + msg.getText());
				long sleepTime = msg.getLongProperty("sleeptime");
				System.out.println("Sleeping for : " + sleepTime + " milli seconds ");
				Thread.sleep(sleepTime);
				queueConnection = queueConnectionFactory.createQueueConnection();
				queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
				queueSender = queueSession.createSender(queue);
				TextMessage message = queueSession.createTextMessage();

				message.setText("REPLIED:" + msg.getText());
				message.setIntProperty("replyid", msg.getIntProperty("id"));
				System.out.println("Sending message: " + message.getText());
				queueSender.send(message);
			} else {
				System.out.println("Message of wrong type: " + inMessage.getClass().getName());
			}
		} catch (Exception te) {
			te.printStackTrace();
			mdc.setRollbackOnly();
			// throw new RuntimeException(te);
		} finally {
			try {
				queueSession.close();
				queueConnection.close();
			} catch (Exception e) {
			}
		}
	} // onMessage

	public void ejbRemove() {
		System.out.println("In SimpleMessageBean.remove()");
	}
} // class

