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

package hello;

import java.rmi.RemoteException;
import java.util.*;
import java.util.logging.*;
import javax.ejb.*;
import javax.naming.*;
import javax.jms.*;
import java.io.*;

/*
	The Bean class that encapsulates the message as an
	ObjectMessage and sends it to the MessageQueue that 
 	is then picked up by the MessageBean
*/

public class PublisherBean implements SessionBean {
    static final Logger logger = Logger.getLogger("PublisherBean");
    SessionContext sc = null;
    Connection connection = null;
    javax.jms.Queue queue = null;

    public PublisherBean() {
        logger.info("In PublisherBean() (constructor)");
    }

    public void setSessionContext(SessionContext sc) {
        this.sc = sc;
    }

    public void ejbCreate() {
        Context context = null;
        ConnectionFactory connectionFactory = null;

        logger.info("In PublisherBean.ejbCreate()");

        try {
            context = new InitialContext();
            queue = (javax.jms.Queue) context.lookup("java:comp/env/jms/QueueName");

            connectionFactory =
                (ConnectionFactory) context.lookup(
                    "java:comp/env/jms/MyConnectionFactory");
            connection = connectionFactory.createConnection();
        } catch (Throwable t) {
            logger.severe("PublisherBean.ejbCreate:" + "Exception: " +
                t.toString());
        }
    }

    /*
	Method is called by the jsp page to encapsulate 
	the data as a ObjectMessage and send it to the 
	Queue
    */

    public void publishNews(String Name,String Quantity,String Date) {
        Session session = null;
        MessageProducer publisher = null;
        ObjectMessage message = null;
        OrderMessage objMessage = new OrderMessage();

        try { 
                logger.info("Connection is : " + connection.getClass().getName());
                session = connection.createSession(true, 0);
                logger.info("Session is : " + session.getClass().getName());
                publisher = session.createProducer(queue);
                logger.info("Producer is : " + publisher.getClass().getName());
                objMessage.name = new String(Name);
                objMessage.quantity = new String(Quantity);
                objMessage.date = new String(Date);
                message = session.createObjectMessage(objMessage);

                logger.info("PUBLISHER: Setting " + "message text to: " +
                    objMessage.name);

                publisher.send(message);
            }  catch (Throwable t)  {
                logger.severe("PublisherBean.publishNews: " + "Exception: "); 
                t.printStackTrace();
                sc.setRollbackOnly();
            } 
          finally  {
                if (session != null) {
			try {
			   session.close();
			} 
                        catch (JMSException e) {
			   e.printStackTrace();
		        }
		}
           }
    }

    public void ejbRemove() throws RemoteException {
        System.out.println("In PublisherBean.ejbRemove()");

        if (connection != null) {
            try {
                connection.close();
            } 
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void ejbActivate() {
    }

    public void ejbPassivate() {
    }
}
