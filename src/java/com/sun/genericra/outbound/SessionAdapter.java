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
package com.sun.genericra.outbound;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.IllegalStateException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import java.util.logging.*;
import com.sun.genericra.util.*;

/**
 * SessionWrapper of the resource adapter.
 *
 * @author Sivakumar Thyagarajan
 */
public class SessionAdapter implements Session, TopicSession, QueueSession {
    
    private Session physicalSession;
    private Session swappedSession;
    private boolean isClosed;
    private boolean isValid = true;
    
    private ConnectionHandle ch;
    private boolean isInUse = false;
    private ArrayList messageConsumers = new ArrayList();
    private ArrayList messageProducers = new ArrayList();
    private ArrayList queueBrowsers = new ArrayList();

    private static Logger logger;
    static {
        logger = LogUtils.getLogger();
    }


    public SessionAdapter(Session physicalSession, ConnectionHandle ch) {
        this.physicalSession = physicalSession;
        this.ch = ch;
    }

    public int getAcknowledgeMode() throws JMSException {
        checkIfClosed();
        return physicalSession.getAcknowledgeMode();
    }
    
    public void setInUse(){
        this.isInUse = true;
    }
    
    public void setInvalid() throws JMSException{
        debug("setinvalid - closeAllJMSEntities" );
        this.isValid = false;
        closeAllJMSEntities();
    }
    
    public boolean isInUse(){
        return isInUse;
    }

    private void checkIfClosed() throws JMSException {
        if (this.isClosed) throw new IllegalStateException("JMS Session closed");
        if (!this.isValid) throw new IllegalStateException("JMS Session invalid");
    }
    

    public void close() throws JMSException {
        if(this.isClosed) return;
        this.isClosed = true;
        this.isInUse = false;
        
        debug(" close() called - txinprogress " +  
                        this.ch.getManagedConnection().isTransactionInProgress());
        closeAllJMSEntities();

        this.ch.getManagedConnection()._closeSession(this.physicalSession);
           
    }
    
    private void closeAllJMSEntities() throws JMSException {
        debug("closeAllJMSEntities" );
        //Close all entities created via this session
        debug("closing message consumers " + this.messageConsumers.size() );
        for (Iterator iter = this.messageConsumers.iterator(); iter.hasNext();) {
            MessageConsumer mc = (MessageConsumer) iter.next();
            debug("closing message consumer " + mc );            
            mc.close();
        }
        debug("closing message producers " + this.messageProducers.size() );
        for (Iterator iter = this.messageProducers.iterator(); iter.hasNext();) {
            MessageProducer mp = (MessageProducer) iter.next();
            debug("closing message producer " + mp );
            mp.close();
        }
        debug("closing queue browsers " + this.queueBrowsers.size() );
        for (Iterator iter = this.queueBrowsers.iterator(); iter.hasNext();) {
            QueueBrowser qp = (QueueBrowser) iter.next();
            debug("closing Queue browser " + qp );
            qp.close();
        }
        
        this.physicalSession.setMessageListener(null); //XXX:??
        this.messageConsumers.clear();
        this.messageProducers.clear();
        this.queueBrowsers.clear();
        ch.getSessions().remove(this); 
    }

    public void commit() throws JMSException {
        checkIfClosed();
        this.physicalSession.commit();
    }

    public void recover() throws JMSException {
        checkIfClosed();
        this.physicalSession.recover();
    }

    public void rollback() throws JMSException {
        checkIfClosed();
        this.physicalSession.rollback();
    }

    public void run() {
        try {
            checkIfClosed();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
        this.physicalSession.run();
    }

    public boolean getTransacted() throws JMSException {
        checkIfClosed();
        return this.physicalSession.getTransacted();
    }

    public void unsubscribe(String name) throws JMSException {
        checkIfClosed();
        this.physicalSession.unsubscribe(name);
    }

    public BytesMessage createBytesMessage() throws JMSException {
        checkIfClosed();
        return this.physicalSession.createBytesMessage();
    }

    public MapMessage createMapMessage() throws JMSException {
        checkIfClosed();
        return this.physicalSession.createMapMessage();
    }

    public Message createMessage() throws JMSException {
        checkIfClosed();
        return this.physicalSession.createMessage();
    }

    public MessageListener getMessageListener() throws JMSException {
        checkIfClosed();
        return this.physicalSession.getMessageListener();
    }

    public void setMessageListener(MessageListener ml) throws JMSException {
        checkIfClosed();
        this.physicalSession.setMessageListener(ml);
    }

    public ObjectMessage createObjectMessage() throws JMSException {
        checkIfClosed();
        return this.physicalSession.createObjectMessage();
    }

    public StreamMessage createStreamMessage() throws JMSException {
        checkIfClosed();
        return this.physicalSession.createStreamMessage();
    }

    public TemporaryQueue createTemporaryQueue() throws JMSException {
        checkIfClosed();
        TemporaryQueue q =  this.physicalSession.createTemporaryQueue();
        ch._addTemporaryDest(q);
        return q;
    }

    public TemporaryTopic createTemporaryTopic() throws JMSException {
        checkIfClosed();
        TemporaryTopic t = this.physicalSession.createTemporaryTopic();
        ch._addTemporaryDest(t);
        return t;
    }

    public TextMessage createTextMessage() throws JMSException {
        checkIfClosed();
        return this.physicalSession.createTextMessage();
    }

    public MessageConsumer createConsumer(Destination dest) throws JMSException {
        checkIfClosed();
        MessageConsumer mc = this.physicalSession.createConsumer(getWrappedDestination(dest));
        this.messageConsumers.add(mc);
        return mc;
    }

    public MessageProducer createProducer(Destination dest) throws JMSException {
        logger.log(Level.FINEST, "Creating producer with " + dest);
        logger.log(Level.FINEST, "Physical Session " + physicalSession);
        checkIfClosed();

        MessageProducer mp = this.physicalSession.createProducer(getWrappedDestination(dest));
        MessageProducer wrappedMp = new MessageProducerProxy(mp);
        this.messageProducers.add(wrappedMp);
        return wrappedMp;
    }

    public ObjectMessage createObjectMessage(Serializable ser)
                    throws JMSException {
        checkIfClosed();
        return this.physicalSession.createObjectMessage(ser);
    }

    public Queue createQueue(String name) throws JMSException {
        checkIfClosed();
        return this.physicalSession.createQueue(name);
    }

    public QueueBrowser createBrowser(Queue name) throws JMSException {
        checkIfClosed();
        QueueBrowser qb = this.physicalSession.createBrowser(getWrappedQueue(name));
        this.queueBrowsers.add(qb);
        return qb;
    }

    public TextMessage createTextMessage(String msg) throws JMSException {
        checkIfClosed();
        return this.physicalSession.createTextMessage(msg);
    }

    public Topic createTopic(String name) throws JMSException {
        checkIfClosed();
        return this.physicalSession.createTopic(name);
    }

    public MessageConsumer createConsumer(Destination dest, String msgSel)
                    throws JMSException {
        checkIfClosed();
        MessageConsumer mc = this.physicalSession.createConsumer(getWrappedDestination(dest), msgSel);
        this.messageConsumers.add(mc);
        return mc;
    }

    public MessageConsumer createConsumer(Destination dest, String msgSel,
                    boolean noLocal) throws JMSException {
        checkIfClosed();
        MessageConsumer mc = this.physicalSession.createConsumer(getWrappedDestination(dest), msgSel, noLocal);
        this.messageConsumers.add(mc);
        return mc;
    }

    public QueueBrowser createBrowser(Queue queue, String msgSel)
                    throws JMSException {
        checkIfClosed();
        debug("PhysicalSession class is " + this.physicalSession.getClass().getName());
        QueueBrowser qb = this.physicalSession.createBrowser(getWrappedQueue(queue), msgSel);
        this.queueBrowsers.add(qb);
        return qb;
        
    }

    public TopicSubscriber createDurableSubscriber(Topic topic, String name)
                    throws JMSException {
        checkIfClosed();
        TopicSubscriber ts = this.physicalSession.createDurableSubscriber(getWrappedTopic(topic), name);
        this.messageConsumers.add(ts);
        return ts;
    }

    public TopicSubscriber createDurableSubscriber(Topic topic, String name,
                    String msgSel, boolean noLocal) throws JMSException {
        checkIfClosed();
        TopicSubscriber ts = this.physicalSession.createDurableSubscriber(getWrappedTopic(topic), name, msgSel, noLocal);
        this.messageConsumers.add(ts);
        return ts;
        
    }

    public TopicPublisher createPublisher(Topic topic) throws JMSException {
        checkIfClosed();
        TopicPublisher tp =((TopicSession) this.physicalSession).createPublisher(getWrappedTopic(topic));
        TopicPublisher wrappedTp = new MessageProducerProxy(tp);
        this.messageProducers.add(wrappedTp);
        return wrappedTp;
                
    }
    

    public TopicSubscriber createSubscriber(Topic topic) throws JMSException {
        checkIfClosed();
        TopicSubscriber ts = ((TopicSession) this.physicalSession).createSubscriber(getWrappedTopic(topic));
        this.messageConsumers.add(ts);
        return ts;
    }

    public TopicSubscriber createSubscriber(Topic topic, String msgSel,
                    boolean noLocal) throws JMSException {
        checkIfClosed();
        TopicSubscriber ts = ((TopicSession) this.physicalSession).createSubscriber(getWrappedTopic(topic), msgSel, noLocal);
        this.messageConsumers.add(ts);
        return ts;
    }

    public QueueReceiver createReceiver(Queue queue) throws JMSException {
        checkIfClosed();
        QueueReceiver qr = ((QueueSession) this.physicalSession).createReceiver(getWrappedQueue(queue));
        this.messageConsumers.add(qr);
        return qr;
    }

    public QueueSender createSender(Queue queue) throws JMSException {
        checkIfClosed();
        QueueSender qs = ((QueueSession) this.physicalSession).createSender(getWrappedQueue(queue));
        QueueSender wrappedQs = new MessageProducerProxy(qs);
        this.messageProducers.add(wrappedQs);
        return wrappedQs;
    }

    public QueueReceiver createReceiver(Queue queue, String msgSel)
                    throws JMSException {
        checkIfClosed();
        QueueReceiver qr = ((QueueSession) this.physicalSession).createReceiver(getWrappedQueue(queue), msgSel);
        this.messageConsumers.add(qr);
        return qr;
        
    }

    private javax.jms.Topic getWrappedTopic(Topic topic) throws JMSException {
        if (topic instanceof TopicProxy) {
            return (javax.jms.Topic)(((TopicProxy)topic)._getPhysicalDestination());        
        } else {
            return topic;
        }
    }

    private javax.jms.Queue getWrappedQueue(Queue queue) throws JMSException {
        if (queue instanceof QueueProxy) {
            return (javax.jms.Queue)(((QueueProxy)queue)._getPhysicalDestination());        
        } else {
            return queue;
        }
    }
    
    private javax.jms.Destination getWrappedDestination(Destination dest) throws JMSException {
        if (dest instanceof DestinationAdapter)  {
            return (javax.jms.Destination)(((DestinationAdapter)dest)._getPhysicalDestination());        
        } else {
            return dest;
        }
    }

    void _swapPhysicalSession (Session in) {
        if (in != this.physicalSession) {
            this.swappedSession = this.physicalSession;
            this.physicalSession = in;
        }
    }

    void _swapPhysicalSession() {
        if (this.swappedSession != null) {
            this.physicalSession = this.swappedSession;
            this.swappedSession = null;
        }
    }
    
    private void debug(String s) {
        logger.log(Level.FINEST, "[SessionAdapter] " + s);
    }
}
