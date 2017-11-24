/*
 * Copyright (c) 2003-2017 Oracle and/or its affiliates. All rights reserved.
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

