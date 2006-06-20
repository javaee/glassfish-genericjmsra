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
package com.sun.genericra.inbound;

import com.sun.genericra.GenericJMSRA;
import com.sun.genericra.util.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.*;

import javax.resource.ResourceException;

import javax.transaction.xa.XAResource;


/**
 * ServerSesionPool implementation as per JMS 1.1 spec.
 * @author Binod P.G
 */
public class InboundJmsResourcePool implements ServerSessionPool {
    private static Logger _logger;

    static {
        _logger = LogUtils.getLogger();
    }

    private ArrayList resources;
    private EndpointConsumer consumer;
    private int maxSize;
    private int connectionsInUse = 0;
    private long maxWaitTime;
    private Connection con = null;
    private Connection dmdCon = null;
    private ConnectionFactory cf = null;
    private boolean transacted = false;
    private boolean destroyed = false;
    private boolean stopped = false;
    private LinkedList waitQ = null;
    private long TIME_OUT = 180 * 1000;
    private StringManager sm = StringManager.getManager(GenericJMSRA.class);

    public InboundJmsResourcePool(EndpointConsumer consumer, boolean transacted) {
        this.consumer = consumer;
        this.transacted = transacted;
        this.waitQ = new LinkedList();
    }

    public synchronized void initialize() throws ResourceException {
        try {
            resources = new ArrayList();
            this.maxSize = consumer.getSpec().getMaxPoolSize();
            this.maxWaitTime = consumer.getSpec().getMaxWaitTime() * 1000;

            if (consumer.getSpec().getSupportsXA()) {
                XAConnectionFactory xacf = (XAConnectionFactory) consumer.getConnectionFactory();
                this.con = createXAConnection(xacf);

                ConnectionFactory cf = (ConnectionFactory) consumer.getDmdConnectionFactory();

                if (consumer.getSpec().getSendBadMessagesToDMD() == true) {
                    this.dmdCon = createDmdConnection(cf);
                }
            } else {
                if (!(consumer.getConnectionFactory() instanceof ConnectionFactory)) {
                    String msg = sm.getString("classtype_not_correct",
                            consumer.getConnectionFactory().getClass().getName());
                    throw new ResourceException(msg);
                }

                cf = (ConnectionFactory) consumer.getConnectionFactory();
                this.con = createConnection(cf);
            }

            stopped = false;
        } catch (JMSException e) {
            throw ExceptionUtils.newResourceException(e);
        }
    }

    public EndpointConsumer getConsumer() {
        return this.consumer;
    }

    public Connection getConnection() {
        return this.con;
    }

    public Connection getConnectionForDMD() throws  JMSException {
        return this.dmdCon;
    }

    public InboundJmsResource create() throws JMSException {
        _logger.log(Level.FINER, "Creating the ServerSession");

        Session sess = null;
        XAResource xar = null;

        if (transacted) {
            sess = createXASession((XAConnection) con);
            xar = getXAResource((XASession) sess);
            _logger.log(Level.FINE, "Created new XA ServerSession");
        } else {
            sess = createSession(con);
            _logger.log(Level.FINE, "Created new ServerSession");
        }

        return new InboundJmsResource(sess, this, xar);
    }

    XAConnection createXAConnection(XAConnectionFactory xacf)
        throws JMSException {
        XAConnection xac = null;
        String user = consumer.getSpec().getUserName();
        String password = consumer.getSpec().getPassword();

        if (isQueue()) {
            xac = ((XAQueueConnectionFactory) xacf).createXAQueueConnection(user,
                    password);
        } else if (isTopic()) {
            xac = ((XATopicConnectionFactory) xacf).createXATopicConnection(user,
                    password);
        } else {
            xac = xacf.createXAConnection(user, password);
        }

        return xac;
    }

    XASession createXASession(XAConnection con) throws JMSException {
        XASession result = null;

        if (isQueue()) {
            result = ((XAQueueConnection) con).createXAQueueSession();
        } else if (isTopic()) {
            result = ((XATopicConnection) con).createXATopicSession();
        } else {
            result = con.createXASession();
        }

        return result;
    }

    private XAResource getXAResource(XASession session)
        throws JMSException {
        XAResource result = null;

        if (isTopic()) {
            result = ((XATopicSession) session).getXAResource();
        } else if (isQueue()) {
            result = ((XAQueueSession) session).getXAResource();
        } else {
            result = session.getXAResource();
        }

        return result;
    }

    Connection createConnection(ConnectionFactory cf) throws JMSException {
        Connection con = null;
        String user = consumer.getSpec().getUserName();
        String password = consumer.getSpec().getPassword();

        if (isTopic()) {
            con = ((TopicConnectionFactory) cf).createTopicConnection(user,
                    password);
        } else if (isQueue()) {
            con = ((QueueConnectionFactory) cf).createQueueConnection(user,
                    password);
        } else {
            con = cf.createConnection(user, password);
        }

        return con;
    }

    Session createSession(Connection con) throws JMSException {
        Session sess = null;

        if (isTopic()) {
            sess = ((TopicConnection) con).createTopicSession(false,
                    Session.AUTO_ACKNOWLEDGE);
        } else if (isQueue()) {
            sess = ((QueueConnection) con).createQueueSession(false,
                    Session.AUTO_ACKNOWLEDGE);
        } else {
            sess = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
        }

        return sess;
    }

    ConnectionConsumer createConnectionConsumer(Destination dest, String name,
        int maxMessages) throws JMSException {
        ConnectionConsumer consumer = null;
        Connection con = getConnection();

        if (isTopic()) {
            consumer = ((TopicConnection) con).createConnectionConsumer((Topic) dest,
                    name, this, maxMessages);
        } else if (isQueue()) {
            consumer = ((QueueConnection) con).createConnectionConsumer((javax.jms.Queue) dest,
                    name, this, maxMessages);
        } else {
            consumer = con.createConnectionConsumer(dest, name, this,
                    maxMessages);
        }

        return consumer;
    }

    Connection createDmdConnection(ConnectionFactory cf)
        throws JMSException {
        Connection con = null;
        String user = consumer.getSpec().getUserName();
        String password = consumer.getSpec().getPassword();

        if (consumer.getSpec().getDeadMessageDestinationType().equals(Constants.TOPIC)) {
            con = ((TopicConnectionFactory) cf).createTopicConnection(user,
                    password);
        } else if (consumer.getSpec().getDeadMessageDestinationType().equals(Constants.QUEUE)) {
            con = ((QueueConnectionFactory) cf).createQueueConnection(user,
                    password);
        } else {
            con = cf.createConnection(user, password);
        }

        return con;
    }

    private boolean isTopic() {
        return consumer.getSpec().getDestinationType().equals(Constants.TOPIC);
    }

    private boolean isQueue() {
        return consumer.getSpec().getDestinationType().equals(Constants.QUEUE);
    }

    public ServerSession getServerSession() throws JMSException {
        InboundJmsResource result = null;
        PauseObject obj = null;

        while (result == null) {
            validate();
            result = _getServerSession();

            if (result == null) {
                if (maxWaitTime >= 0) {
                    if (obj == null) {
                        obj = new PauseObject();
                    }

                    obj.pauseCallingThread();
                }
            }
        }

        return result.refreshListener();
    }

    public void validate() throws JMSException {
        if (destroyed) {
            String msg = sm.getString("serversession_pool_destroyed");
            throw new JMSException(msg);
        }
    }

    private synchronized InboundJmsResource _getServerSession()
        throws JMSException {
        _logger.log(Level.FINER, "JMS provider is getting the ServerSession");

        if (stopped) {
            return null;
        }

        Iterator it = resources.iterator();

        while (it.hasNext()) {
            InboundJmsResource resource = (InboundJmsResource) it.next();

            if (resource.isFree()) {
                connectionsInUse++;

                return resource.markAsBusy();
            }
        }

        if (resources.size() < this.maxSize) {
            InboundJmsResource res = create();
            resources.add(res);
            connectionsInUse++;

            return res.markAsBusy();
        }

        return null;
    }

    public synchronized void put(InboundJmsResource resource) {
        resource.markAsFree();
        connectionsInUse--;

        if (stopped) {
            if (connectionsInUse <= 0) {
                notify();
            }
        } else {
            resumeWaitingThread();
        }
    }

    /**
     * Stops message delivery. Any message that is currently being delivered
     * will not be affected. It can be resumed later.
     */
    public void stop() throws JMSException {
        this.stopped = true;
        this.maxWaitTime = 0;
        waitForAll();
        releaseAllResources();

        if (dmdCon != null) {
            this.dmdCon.close();
        }
    }

    /**
     * Destroys the ServerSessionPool.
     */
    public void destroy() throws JMSException {
        this.destroyed = true;
        stop();
        releaseAllWaitingThreads();
    }

    public synchronized void waitForAll() {
        if (connectionsInUse > 0) {
            _logger.log(Level.FINE,
                "Waiting for " + connectionsInUse + " ServerSessions" +
                " to come back to pool");

            try {
                wait(this.consumer.getSpec().getEndpointReleaseTimeout() * 1000);
            } catch (InterruptedException ie) {
            }
        }
    }

    public void releaseAllWaitingThreads() {
        Iterator it = waitQ.iterator();
        int count = 0;

        while (it.hasNext()) {
            PauseObject obj = (PauseObject) it.next();
            obj.resume();
            count++;
        }

        _logger.log(Level.FINE, "Released a total of " + count + " requests");
    }

    public void releaseAllResources() {
        Iterator it = resources.iterator();

        while (it.hasNext()) {
            InboundJmsResource obj = (InboundJmsResource) it.next();

            try {
                obj.destroy();
            } catch (Exception e) {
                // This is just to make sure that if one resource fails to destroy
                // we still call  destroy on others.
                _logger.log(Level.SEVERE,
                    "Cannot destroy resource " + obj.toString());
            }
        }
    }

    public boolean isTransacted() {
        return transacted;
    }

    public void resumeWaitingThread() {
        PauseObject obj = null;

        synchronized (waitQ) {
            if (waitQ.size() > 0) {
                obj = (PauseObject) waitQ.removeFirst();
            }
        }

        if (obj != null) {
            obj.resume();
        }
    }

    /**
     * The class that holds the logic of wait queue.
     */
    class PauseObject {
        long startTime = 0;
        long elapsedWaitTime = 0;
        long remainingWaitTime = 0;

        void pauseCallingThread() throws JMSException {
            if (maxWaitTime == 0) {
                remainingWaitTime = 0;
                startTime = 0;
            } else {
                if (startTime == 0) {
                    this.startTime = System.currentTimeMillis();
                }

                elapsedWaitTime = startTime - System.currentTimeMillis();

                if (elapsedWaitTime > maxWaitTime) {
                    /** WE SHOULD LOG SOMETHING **/
                    String msg = sm.getString("pool_limit_reached");
                    throw new JMSException(msg);
                }

                remainingWaitTime = startTime - elapsedWaitTime;
            }

            pause();
        }

        synchronized void pause() {
            synchronized (waitQ) {
                waitQ.addLast(this);
            }

            try {
                _logger.log(Level.FINE, "Waiting for :" + remainingWaitTime);
                wait(remainingWaitTime);
            } catch (InterruptedException ie) {
            }

            synchronized (waitQ) {
                waitQ.remove(this);
            }
        }

        synchronized void resume() {
            _logger.log(Level.FINE, "Notifying the thread");
            notify();
        }
    }
}
