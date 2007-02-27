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

package com.sun.s1peqe.connector.mq.simplestress.client;

import javax.jms.*;
import javax.naming.*;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Simple stress test with Queue. The test starts NUM_CLIENT threads. Each thread sends
 * NUM_CYCLES messages to a Queue. An MDB consumes these messages concurrently
 * and responds with a reply Message to a ReplyQueue. While the threads are being exercised,
 * an attempt to read NUM_CLIENT*NUM_CYCLES messages is performed. An attempt to
 * read an extra message is also done to ascertain that no more than
 * NUM_CLIENT*NUM_CYCLES messages are available.
 */
public class SimpleMessageClient {
    static boolean debug = false;


    int id =0;

    public SimpleMessageClient(int i) {
        this.id = i;
    }

    public static void main(String[] args) throws Exception{
        /**
	 * Start the threads that will send messages to MDB
	 */
        Context                 jndiContext = null;
        QueueConnectionFactory  queueConnectionFactory = null;
        QueueConnection         queueConnection = null;
        QueueConnection         queueConnection1 = null;
        QueueConnection         queueConnection2 = null;
        QueueSession            queueSession = null;
        QueueSession            queueSession1 = null;
        QueueSession            queueSession2 = null;
        Queue                   queue = null;
        Queue                   queuer = null;
        Queue                   ejbqueue = null;
        QueueReceiver           queueReceiver = null;
        QueueReceiver           queueReceiver1 = null;
        QueueSender 		queueSender= null;
        TextMessage             message = null;

        try {
            jndiContext = new InitialContext();
            queueConnectionFactory = (QueueConnectionFactory)
                jndiContext.lookup
                ("java:comp/env/jms/MyQueueConnectionFactory");
            queue = (Queue) jndiContext.lookup("java:comp/env/jms/MDB_QUEUE");
            queuer = (Queue) jndiContext.lookup("java:comp/env/jms/MDB_REPLY_QUEUE");
           ejbqueue = (Queue) jndiContext.lookup("java:comp/env/jms/EJB_QUEUE");

            queueConnection =
                queueConnectionFactory.createQueueConnection();
            queueConnection1 =
                queueConnectionFactory.createQueueConnection();
            queueConnection2 =
                queueConnectionFactory.createQueueConnection();
	    System.out.println("Created conection");
            queueSession =
                queueConnection.createQueueSession(false,
                    Session.AUTO_ACKNOWLEDGE);
	    System.out.println("Created session 1");
            queueSession1 =
                queueConnection1.createQueueSession(false,
                    Session.AUTO_ACKNOWLEDGE);
	    System.out.println("Created session 2");
            queueSession2 =
                queueConnection2.createQueueSession(false,
                    Session.AUTO_ACKNOWLEDGE);
	    System.out.println("Created session 3");
            queueConnection.start();
            queueConnection1.start();
            queueConnection2.start();
	    queueSender = queueSession.createSender(queue);
            queueReceiver = queueSession.createReceiver(queuer);
            queueReceiver1 = queueSession.createReceiver(ejbqueue);
	    TextMessage txtmsg = queueSession.createTextMessage("HI");
	    txtmsg.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
	    queueSender.send(txtmsg);
	    System.out.println("Sent message");
	    Thread.sleep(3000);
	    Message msg = queueReceiver.receive(1000);
	    if (msg == null) {
	    	System.out.println("Failed : No reply from REPLY QUEUE ");	
	    } else {
	    	System.out.println("Success : Received message from REPLY QUEUE " + msg);
	    }
	    Message msg1 = queueReceiver1.receive(1000);
	    if (msg1 == null) {
	    	System.out.println("Failed : No reply from EJB QUEUE ");	
	    } else {
	    	System.out.println("Success : Received message from EJB QUEUE "+ msg1);
	    }
	    queueConnection.close();
	    queueConnection1.close();
	    queueConnection2.close();
	}
	catch (Exception e){
		
            System.out.println("Concurrent message delivery test - Queue stress test :  FAIL");
        }finally {
            System.exit(0);
        }
         

    }


    static void debug(String msg) {
        if (debug) {
	   System.out.println(msg);
	}
    }
} // class

