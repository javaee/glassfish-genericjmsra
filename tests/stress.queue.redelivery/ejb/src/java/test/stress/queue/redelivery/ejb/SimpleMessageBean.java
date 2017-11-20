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

package test.stress.queue.redelivery.ejb;

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
 * A simple message driven bean, which on receipt of a message, publishes a message to a
 * reply destination. Then a RuntimeException will be thrown so that the message is redeliverd.
 * When the message is redelivered, then:
 * - for even messages the message priority is reduced an an exception is thrown a second time
 * - for odd messages an exception is thrown a second time 
 * When the message is redelivered a second time then:
 * - for the (even) messages which have the default priority, an exception is thrown a third time, 
 *       and this process continues until the message is sent to the DMQ
 * - for the (odd) messages which had reduced priority, an outbound message is sent and committed
 * reduced priority
 *
 * So when the test is finished, for N inbound messages, we should expect to see
 * N/2 message in the DMQ
 * N/2 messages in the outbound queue
 *
 */
public class SimpleMessageBean extends GenericMDB implements MessageDrivenBean, MessageListener {

	Context jndiContext = null;
	QueueConnectionFactory queueConnectionFactory = null;
	Queue queue = null;
	final int NUM_MSGS = 100;
	final int MAX_PR = 50;

	private transient MessageDrivenContext mdc = null;

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
			queue = (Queue) jndiContext.lookup("java:comp/env/jms/clientQueue");
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
				int incomingId = msg.getIntProperty("id");
				message.setIntProperty("replyid", incomingId);
				System.out.println("Sending message: " + message.getText());
				queueSender.send(message);
				if (msg.getJMSRedelivered() == false) {
					throw new RuntimeException("Setting redelivery flag" + msg.getJMSPriority());
				}
				if (msg.getJMSPriority() == Message.DEFAULT_PRIORITY) {
					if (incomingId % 2 == 0) {
						msg.setJMSPriority(Message.DEFAULT_PRIORITY - 1);
					}
					throw new RuntimeException("Resetting the priority");
				}
			} else {
				System.out.println("Message of wrong type: " + inMessage.getClass().getName());
			}
		} catch (Exception te) {
			te.printStackTrace();
			//mdc.setRollbackOnly();
			throw new RuntimeException(te);
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

