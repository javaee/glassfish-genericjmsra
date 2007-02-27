/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * %W% %E%
 */

package com.sun.ejte.j2ee.genericra.ejb;

import javax.jms.*;
import javax.ejb.*;
import java.util.Enumeration;
import java.util.Properties;
import javax.naming.*;

public class SendBean implements SessionBean {

    private SessionContext sessioncontext;
    private java.util.Properties p = null;

    private transient javax.jms.Queue queue;
    private transient QueueConnection connection;
    private transient QueueConnectionFactory qcFactory;

    private transient Topic topic;
    private transient TopicConnection tConn;
    private transient TopicConnectionFactory tcFactory;

    private String jmsUser;
    private String jmsPassword;

    Context context;

 
    // Use this stateful session bean to test asynchronous receives of message driven beans.
    // Has send methods for each type of jms message.
 
    public SendBean() {
    }

    public void ejbCreate() throws CreateException {
        try {
            context = new javax.naming.InitialContext();
             qcFactory = (QueueConnectionFactory)context.lookup("java:comp/env/jms/MyQueueConnectionFactory");
             queue = (Queue) context.lookup("java:comp/env/jms/EJB_QUEUE");
             connection =  qcFactory.createQueueConnection();
             connection.start();
        } catch ( Exception e) {
            e.printStackTrace();
            System.out.println("init failed"+ e);
        }
    }

  
    /**
     * Send a message.  In bmt case, create session BEFORE
     * starting tx.
     */
    public void sendTextMessageToQ(String prop)  {
        try {
            String msg="HI EJB GOT MESSAGE"; 
            QueueSession session = connection.createQueueSession(true, Session.AUTO_ACKNOWLEDGE); 
            QueueSender sender = session.createSender(queue);
            TextMessage message = session.createTextMessage(msg);
            System.out.println("before sending text message" + msg);
            sender.send(message);
            System.out.println("after sending text message" + msg);
            session.close();
	    cleanup();
            } catch(Exception e) {
            e.printStackTrace();
                  System.out.println(e.getMessage());
            }
    }
    
 
    public void cleanup() {
        try {
            if( connection != null ) {
                connection.close();
            }

            if( tConn != null ) {
                tConn.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Exception in cleanup"+ e);
        }
    }

    public void setSessionContext(SessionContext sc) {
		//context = sc;
    }

    public void ejbRemove() {
		System.out.println("@ejbRemove");
    }


    public void ejbActivate() {
        System.out.println("@ejbActivate");
        try {
             qcFactory = (QueueConnectionFactory)context.lookup("java:comp/env/jms/MyQueueConnectionFactory");

             queue = (Queue) context.lookup("java:comp/env/jms/EJB_QUEUE");
             connection.start();

        } catch (Exception e ) {
            e.printStackTrace();
             System.out.println("Exception restoring Queue... " + e);
        }
    }


    public void ejbPassivate() {
       System.out.println("@ejbPassivate");

        queue     = null;
        qcFactory = null;
        topic     = null;
        tcFactory = null;

        cleanup();
    }
}

