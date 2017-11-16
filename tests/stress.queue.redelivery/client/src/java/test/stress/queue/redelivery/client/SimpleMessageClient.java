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

package test.stress.queue.redelivery.client;

import javax.jms.*;
import javax.naming.*;

import test.common.client.GenericClient;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Simple stress test with Queue. The test starts NUM_CLIENT threads. Each thread sends NUM_CYCLES messages to a Queue.
 * An MDB consumes these messages concurrently and responds with a reply Message to a ReplyQueue. While the threads are
 * being exercised, an attempt to read NUM_CLIENT*NUM_CYCLES messages is performed. An attempt to read an extra message
 * is also done to ascertain that no more than NUM_CLIENT*NUM_CYCLES messages are available.
 */
public class SimpleMessageClient extends GenericClient implements Runnable {
	static int NUM_CLIENTS = 30;
	static int NUM_CYCLES = 30;
//	 static int NUM_CLIENTS = 6;
//	 static int NUM_CYCLES = 5;
	static int TIME_OUT = 60000;
	static long MDB_SLEEP_TIME = 100;
	static boolean debug = false;

	int id = 0;

	public SimpleMessageClient(int i) {
		this.id = i;
	}

	public static void main(String[] args) throws Exception {

		drainQueue("java:comp/env/jms/QCFactory","java:comp/env/jms/SampleQueue");
		drainQueue("java:comp/env/jms/QCFactory","java:comp/env/jms/clientQueue");
		
		/**
		 * Start the threads that will send messages to MDB
		 */
		ArrayList al = new ArrayList();
		try {
			for (int i = 0; i < NUM_CLIENTS / 2; i++) {
				// for (int i =0; i < NUM_CLIENTS; i++) {
				Thread client = new Thread(new SimpleMessageClient(i));
				al.add(client);
				client.start();
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}

		for (int i = 0; i < al.size(); i++) {
			Thread client = (Thread) al.get(i);
			try {
				client.join();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		System.out.println("Now reading the reply queue");

		al = new ArrayList();
		try {
			for (int i = NUM_CLIENTS / 2; i < NUM_CLIENTS; i++) {
				Thread client = new Thread(new SimpleMessageClient(i));
				al.add(client);
				client.start();
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}

		Context jndiContext = null;
		QueueConnectionFactory queueConnectionFactory = null;
		QueueConnection queueConnection = null;
		QueueSession queueSession = null;
		Queue queue = null;
		QueueReceiver queueReceiver = null;
		TextMessage message = null;
		Queue deadMessageQueue = null;
		QueueReceiver dmdQueueReceiver = null;

		try {
			jndiContext = new InitialContext();
			queueConnectionFactory = (QueueConnectionFactory) jndiContext.lookup("java:comp/env/jms/QCFactory");
			queue = (Queue) jndiContext.lookup("java:comp/env/jms/clientQueue");
			deadMessageQueue = (Queue) jndiContext.lookup("java:comp/env/jms/deadMessageQueue");

			queueConnection = queueConnectionFactory.createQueueConnection();
			queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			queueConnection.start();
			queueReceiver = queueSession.createReceiver(queue);
			dmdQueueReceiver = queueSession.createReceiver(deadMessageQueue);

			HashMap map = new HashMap();

			long startTime = System.currentTimeMillis();
			boolean pass = true;
			//
			// Receive messages from the outbound queue (and keep in a data structure)
			//			
			for (int i = 0; i < ((NUM_CLIENTS * NUM_CYCLES) / 2); i++) {							
				TextMessage msg = (TextMessage) queueReceiver.receive(TIME_OUT);
				if (msg == null) {
					System.out.println("Received only:" + i + " messages from outbound queue");
					pass = false;
					break;
				}
				Integer id = new Integer(msg.getIntProperty("replyid"));
				if (map.containsKey(id)) {
					pass = false;
					debug("Duplicate :" + id);
				}
				map.put(id, msg.getText());
			}
			long totalTime = System.currentTimeMillis() - startTime;
			System.out.println("Received messages in :" + totalTime + " milliseconds");
			System.out.println("------------------------------------------------------");

			// Now attempting to read the rest from the DMQ
			for (int i = 0; i < ((NUM_CLIENTS * NUM_CYCLES) / 2); i++) {
				TextMessage msg = (TextMessage) dmdQueueReceiver.receive(TIME_OUT);
				System.out.print("." + i);
				if (msg == null) {
					pass = false;
					System.out.println("Received only " + i + " messages from DMQ");
					break;
				}
				Integer id = new Integer(msg.getIntProperty("id"));
				if (map.containsKey(id)) {
					pass = false;
					debug("Duplicate :" + id);
				}
				map.put(id, msg.getText());
			}
			totalTime = System.currentTimeMillis() - startTime;
			if (pass) {
				System.out.println("Received the other half the messages from the " + "DMD queue in :" + totalTime
						+ " milliseconds");
			} else {
				System.out.println("Received less that expected messages from DMD");
			}
			System.out.println("------------------------------------------------------");

			// Try to receive one more message than expected.
			TextMessage msg = (TextMessage) queueReceiver.receive(TIME_OUT);

			if (msg != null) {
				pass = false;
				System.out.println("Received more than expected number of messages :" + msg.getText());
			}

			if (pass) {
				System.out.println("Concurrent message delivery test - Queue Stess Test : PASS");
			} else {
				System.out.println("Concurrent message delivery test - Queue Stress Test : FAIL");
			}
		} catch (Throwable t) {
			t.printStackTrace();
			System.out.println("Concurrent message delivery test - Queue stress test :  FAIL");
		} finally {
			for (int i = 0; i < al.size(); i++) {
				Thread client = (Thread) al.get(i);
				try {
					client.join();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
			System.exit(0);
		}

	}

	private static void drainQueue(String queueJNDIname) throws NamingException, JMSException {

		Context jndiContext = new InitialContext();
		QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory) jndiContext
				.lookup("java:comp/env/jms/QCFactory");
		Queue queue = (Queue) jndiContext.lookup(queueJNDIname);
		QueueConnection queueConnection = queueConnectionFactory.createQueueConnection();
		QueueSession queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		QueueReceiver queueReceiver = queueSession.createReceiver(queue);
		queueConnection.start();
		int numMessages = 0;
		while (true) {
			Message message = queueReceiver.receive(1000);
			if (message == null)
				break;
			numMessages++;
		}
		if (numMessages > 0)
			System.out.println("Drained " + numMessages + " from queue with JNDI name " + queueJNDIname);
		queueConnection.close();
	}

	public void run() {

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
			queue = (Queue) jndiContext.lookup("java:comp/env/jms/SampleQueue");

			int startId = id * NUM_CYCLES;
			int endId = (id * NUM_CYCLES) + NUM_CYCLES;
			for (int i = startId; i < endId; i++) {
				try {
					queueConnection = queueConnectionFactory.createQueueConnection();
					queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
					queueConnection.start();
					queueSender = queueSession.createSender(queue);
					message = queueSession.createTextMessage();
					message.setText("CLIENT");
					message.setIntProperty("id", i);
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
					} // if
				} // finally
			}
		} catch (Throwable e) {
			System.out.println("Exception occurred: " + e.toString());
		} // finally
	} // main

	static void debug(String msg) {
		if (debug) {
			System.out.println(msg);
		}
	}
} // class

