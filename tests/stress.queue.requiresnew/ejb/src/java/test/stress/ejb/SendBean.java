/**
 * Copyright (c) 2003-2017 Oracle and/or its affiliates. All rights reserved.
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

/*
 * %W% %E%
 */

package test.stress.ejb;

import javax.ejb.CreateException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.jms.JMSException;
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

public class SendBean implements SessionBean {
	
    private transient Context context;
    private transient QueueConnectionFactory queueConnectionFactory;
    private transient javax.jms.Queue queue;

    public SendBean() {
    }

    public void ejbCreate() throws CreateException {
 
            try {
				context = new InitialContext();
				queueConnectionFactory = (QueueConnectionFactory)context.lookup("java:comp/env/jms/QCFactory");
				queue = (Queue) context.lookup("java:comp/env/jms/outboundQueue2");
			} catch (NamingException e) {
				e.printStackTrace();
			} 
    }

    /**
     * Send a TextMessage containing the specified String to the configured outbound queue
     * @param text
     */
    public void sendTextMessageToQ(String text)  {
    	QueueConnection queueConnection=null;
    	QueueSession queueSession=null;
        try {
			queueConnection =  queueConnectionFactory.createQueueConnection();
			queueConnection.start();
			queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE); 
			QueueSender queueSender = queueSession.createSender(queue);
			TextMessage testMessage = queueSession.createTextMessage(text);
			queueSender.send(testMessage);
		} catch (JMSException e) {
			e.printStackTrace();
		} finally {
	        if (queueConnection!=null) {
				try {
					queueConnection.close();
				} catch (JMSException e) {
					e.printStackTrace();
				}
	        }
		}
    }
    
 
    public void setSessionContext(SessionContext sc) {
    }

    public void ejbRemove() {
        context = null;
        queueConnectionFactory = null;
        queue = null;
    }

    // not called for stateless session bean
    public void ejbActivate() {
    }

    // not called for stateless session bean
    public void ejbPassivate() {
    }
}

