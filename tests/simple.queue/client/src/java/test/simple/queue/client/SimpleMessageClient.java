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

package test.simple.queue.client;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

import test.common.client.GenericClient;

public class SimpleMessageClient extends GenericClient {

	static private long MDB_SLEEP_TIME = 2000;
	static private boolean debug = false;

	public static void main(String[] args) throws Exception {

		boolean pass = true;

		drainQueue("java:comp/env/jms/QCFactory","java:comp/env/jms/inboundQueue");
		drainQueue("java:comp/env/jms/QCFactory","java:comp/env/jms/outboundQueue");

		// send one message to the MDB
		send();

		System.out.println("Now reading the reply queue");
		Context jndiContext = null;
		QueueConnectionFactory queueConnectionFactory = null;
		QueueConnection queueConnection = null;
		QueueSession queueSession = null;
		Queue queue = null;
		QueueReceiver queueReceiver = null;

		try {
			jndiContext = new InitialContext();
			queueConnectionFactory = (QueueConnectionFactory) jndiContext.lookup("java:comp/env/jms/QCFactory");
			queue = (Queue) jndiContext.lookup("java:comp/env/jms/outboundQueue");

			queueConnection = queueConnectionFactory.createQueueConnection();
			queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			queueConnection.start();
			queueReceiver = queueSession.createReceiver(queue);

			TextMessage msg = (TextMessage) queueReceiver.receive(10000);
			if (msg == null) {
				System.out.println("Expected message not received");
				pass = false;
			} else {
				String reply = msg.getText();
				if (!reply.equals("REPLIED:CLIENT")) {
					System.out.println("Reply message contains unexpected payload");
					pass = false;
				}

				// Check no more messages can be received
				TextMessage msg2 = (TextMessage) queueReceiver.receive(1000);
				if (msg2 != null) {
					pass = false;
					System.out.println("Received more messages than expected");
				}
			}

			if (pass) {
				System.out.println("Concurrent message delivery test - Queue Stess Test : PASS");
			} else {
				System.out.println("Concurrent message delivery test - Queue Stress Test : FAIL");
			}
			
			queueConnection.close();
		} catch (Throwable t) {
			t.printStackTrace();
			System.out.println("Concurrent message delivery test - Queue stress test :  FAIL");
		}
		System.exit(0);
	}


	public static void send() {

		Context jndiContext = null;
		QueueConnectionFactory queueConnectionFactory = null;
		QueueConnection queueConnection = null;
		QueueSession queueSession = null;
		Queue queue = null;
		QueueSender queueSender = null;
		TextMessage message = null;

		try {
			jndiContext = new InitialContext();
			queueConnectionFactory = (QueueConnectionFactory) jndiContext.lookup("java:comp/env/jms/QCFactory");
			queue = (Queue) jndiContext.lookup("java:comp/env/jms/inboundQueue");

			try {
				queueConnection = queueConnectionFactory.createQueueConnection();
				queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
				queueConnection.start();
				queueSender = queueSession.createSender(queue);
				message = queueSession.createTextMessage();
				message.setText("CLIENT");
				message.setIntProperty("id", 0);
				message.setLongProperty("sleeptime", MDB_SLEEP_TIME);
				queueSender.send(message);
				debug("Send the message :" + message.getIntProperty("id") + ":" + message.getText());
			} catch (Exception e) {
				System.out.println("Exception occurred: " + e.toString());
			} finally {
				if (queueConnection != null) {
					try {
						queueConnection.close();
					} catch (JMSException e) {
					}
				}
			}
		} catch (Throwable e) {
			System.out.println("Exception occurred: " + e.toString());
		}
	}

	static void debug(String msg) {
		if (debug) {
			System.out.println(msg);
		}
	}
}
