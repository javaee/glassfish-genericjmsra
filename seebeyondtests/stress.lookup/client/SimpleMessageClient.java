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

package com.sun.s1peqe.connector.mq.simplestress.client;

import javax.jms.*;
import javax.naming.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Simple stress test with Queue. The test starts NUM_CLIENT threads. Each
 * thread sends NUM_CYCLES messages to a Queue. An MDB consumes these messages
 * concurrently and responds with a reply Message to a ReplyQueue. While the
 * threads are being exercised, an attempt to read NUM_CLIENT*NUM_CYCLES
 * messages is performed. An attempt to read an extra message is also done to
 * ascertain that no more than NUM_CLIENT*NUM_CYCLES messages are available.
 */
public class SimpleMessageClient implements Runnable {
    static int NUM_CLIENTS = 20;
    static int NUM_CYCLES = 20;
    //static int NUM_CLIENTS = 2;
    //static int NUM_CYCLES = 2;
    static int TIME_OUT = 60000;
    static long MDB_SLEEP_TIME = 2000;
    static boolean debug = true;
    int id = 0;

    public SimpleMessageClient(int i) {
        this.id = i;
    }

    public static void main(String[] args) throws Exception {
        debug("In stress client " + args);
        /**
         * Start the threads that send messages to MDB
         */
        ArrayList al = new ArrayList();
        /*
         * Send first half
         */
        try {
            //for (int i =0; i < NUM_CLIENTS/2; i++) {
            for (int i = 0; i < NUM_CLIENTS; i++) {
                debug("creating Thread " + i);
                Thread client = new Thread(new SimpleMessageClient(i));
                al.add(client);
                client.start();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        debug("Now reading the reply queue");
        /*
         * Send second half
         */
        /*
         * al = new ArrayList(); try { for (int i = NUM_CLIENTS/2; i <
         * NUM_CLIENTS; i++) { Thread client = new Thread(new
         * SimpleMessageClient(i)); al.add(client); client.start(); } } catch
         * (Throwable t) { t.printStackTrace(); }
         *  
         */
        Context jndiContext = null;
        QueueConnectionFactory queueConnectionFactory = null;
        QueueConnection queueConnection = null;
        QueueSession queueSession = null;
        Queue queue = null;
        QueueReceiver queueReceiver = null;
        TextMessage message = null;
        try {
            jndiContext = new InitialContext();
            queueConnectionFactory = (QueueConnectionFactory) jndiContext
                            .lookup("java:comp/env/jms/QCFactory");
            queue = (Queue) jndiContext.lookup("java:comp/env/jms/clientQueue");
            queueConnection = queueConnectionFactory.createQueueConnection();
            queueSession = queueConnection.createQueueSession(false,
                            Session.AUTO_ACKNOWLEDGE);
            queueConnection.start();
            queueReceiver = queueSession.createReceiver(queue);
            HashMap map = new HashMap();
            long startTime = System.currentTimeMillis();
            boolean pass = true;
            // Receives all the messages and keep in the data structure
            for (int i = 0; i < NUM_CLIENTS * NUM_CYCLES ; i++) {
                TextMessage msg = (TextMessage) queueReceiver.receive(TIME_OUT);
                if (msg == null) {
                    debug("Received :" + i + " messages before exiting");
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
            debug("Received messages in :" + totalTime + " milliseconds");
            debug("------------------------------------------------------");

            // Now examine the received data
            for (int i = 0; i < NUM_CLIENTS * NUM_CYCLES ; i++) {
                String reply = (String) map.get(new Integer(i));
                if (!reply.equals("REPLIED:CLIENT")) {
                    pass = false;
                }
                debug("Receeived :" + i + ":" + reply);
            }
            // NEGATIVE TEST ::
            // Try to receive one more message than expected with a timeout
            TextMessage msg = (TextMessage) queueReceiver.receive(TIME_OUT);
            if (msg != null) {
                pass = false;
                debug("Received more than expected number of messages :"
                                + msg.getText());
            }
            if (pass) {
                System.out.println("Concurrent message delivery test - Queue : PASS");
            } else {
                System.out.println("Concurrent message delivery test - Queue : FAIL");
            }
        } catch (Throwable t) {
            t.printStackTrace();
            System.out.println("Concurrent message delivery test - Queue : FAIL");
        } finally {
            /*
             * Wait for all threads to finish
             */
            for (int i = 0; i < al.size(); i++) {
                Thread client = (Thread) al.get(i);
                try {
                    client.join();
                } catch (Exception e) {
                    debug(e.getMessage());
                }
            }
            try {
                queueConnection.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.exit(0);
        }
    }

    public void run() {
        debug(" run of thread called");
        Context jndiContext = null;
        QueueConnectionFactory queueConnectionFactory = null;
        QueueConnection queueConnection = null;
        QueueSession queueSession = null;
        Queue queue = null;
        QueueSender queueSender = null;
        TextMessage message = null;
        try {
            jndiContext = new InitialContext();
            debug("Attempting to lookup QCF");
            queueConnectionFactory = (QueueConnectionFactory) jndiContext
                            .lookup("java:comp/env/jms/QCFactory");
            debug("Attempting to lookup QCF");
            queue = (Queue) jndiContext.lookup("java:comp/env/jms/SampleQueue");
            int startId = id * NUM_CYCLES;
            int endId = (id * NUM_CYCLES) + NUM_CYCLES;
            for (int i = startId; i < endId; i++) {
                try {
                    queueConnection = queueConnectionFactory
                                    .createQueueConnection();
                    queueSession = queueConnection.createQueueSession(false,
                                    Session.AUTO_ACKNOWLEDGE);
                    queueConnection.start();
                    queueSender = queueSession.createSender(queue);
                    message = queueSession.createTextMessage();
                    message.setText("CLIENT");
                    message.setIntProperty("id", i);
                    message.setLongProperty("sleeptime", MDB_SLEEP_TIME);
                    queueSender.send(message);
                    debug("Send the message :" + message.getIntProperty("id")
                                    + ":" + message.getText());
                } catch (Exception e) {
                    e.printStackTrace();
                    debug("Exception occurred: " + e.toString());
                } finally {
                    if (queueSender != null) {
                        try {
                            //debug("about to close sender");
                            queueSender.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (queueSession != null) {
                        try {
                            //debug("about to close session");
                            queueSession.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (queueConnection != null) {
                        try {
                            //debug("about to close connection");
                            queueConnection.close();
                            //debug("Closed connection");
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                    } 
                }
            }
        } catch (Throwable e) {
            debug("Exception occurred: " + e.toString());
        } 
    } 

    static void debug(String msg) {
        if (debug) {
            System.out.println("[SimpleClient] " + msg);
        }
    }
} 
