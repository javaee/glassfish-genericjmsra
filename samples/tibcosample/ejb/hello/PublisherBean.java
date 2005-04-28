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
