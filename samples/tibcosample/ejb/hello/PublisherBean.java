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
