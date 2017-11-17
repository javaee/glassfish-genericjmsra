/**
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
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

package test.common.client;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class GenericClient {

	/**
	 * Using the specified connection factory, drain all messages from the specified queue
	 * and return the number that were read.
     *
	 * @param connectionFactoryJNDIName
	 * @param queueJNDIname
	 * @return The number of messages in the queue
	 * @throws NamingException
	 * @throws JMSException
	 */
	protected static int drainQueue(String connectionFactoryJNDIName, String queueJNDIname) throws NamingException, JMSException {
	
		Context jndiContext = new InitialContext();
		QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory) jndiContext.lookup(connectionFactoryJNDIName);
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
		if (numMessages > 0) {
			System.out.println("Read " + numMessages + " from queue with JNDI name " + queueJNDIname + "and physical queue name "+queue.getQueueName());
		}
		queueConnection.close();
		
		return numMessages;
	}

}
