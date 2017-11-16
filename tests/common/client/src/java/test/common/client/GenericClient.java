/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
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
