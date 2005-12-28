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
import javax.jms.*;
/**
 * MessageProducerProxy. This is used to update messages with 
 * unwrapped destinations and reply destinations.
 */
public class MessageProducerProxy implements QueueSender, TopicPublisher {
    MessageProducer mp = null;

    public MessageProducerProxy(MessageProducer mp) {
        this.mp = mp;
    }
    public int getDeliveryMode() throws JMSException { 
        return getProducer().getDeliveryMode();
    }

    public int getPriority() throws JMSException {
        return getProducer().getPriority();
    }

    public long getTimeToLive() throws JMSException {
        return getProducer().getTimeToLive();
    }

    public void close() throws JMSException {
        getProducer().close();
    }

    public boolean getDisableMessageID() throws JMSException {
        return getProducer().getDisableMessageID();
    }

    public boolean getDisableMessageTimestamp() throws JMSException {
        return getProducer().getDisableMessageTimestamp();
    }

    public void setDeliveryMode(int i) throws JMSException {
        getProducer().setDeliveryMode(i);
    }

    public void setPriority(int i) throws JMSException {
        getProducer().setPriority(i);
    }

    public void setTimeToLive(long l) throws JMSException {
        getProducer().setTimeToLive (l);
    }

    public void setDisableMessageID(boolean b) throws JMSException {
        getProducer().setDisableMessageID(b);
    }

    public void setDisableMessageTimestamp(boolean b) throws JMSException {
        getProducer().setDisableMessageTimestamp(b);
    }

    public void send(Message msg) throws JMSException {
        Message tmp = unwrapDestinations(msg);
        getProducer().send(tmp);
    }

    public void send(Message msg,int i,int j,long l) throws JMSException {
        Message tmp = unwrapDestinations(msg);
        getProducer().send(tmp, i, j, l);
    }

    public void send(Destination dest,Message msg) throws JMSException {
        Message tmpMsg = unwrapDestinations(msg);
        Destination tmpDest = unwrapDestinations(dest);
        getProducer().send(tmpDest, tmpMsg);
    }

    public void send(Destination dest,Message msg,int i,int j,long l)
        throws JMSException {
        Message tmpMsg = unwrapDestinations(msg);
        Destination tmpDest = unwrapDestinations(dest);
        getProducer().send(tmpDest, tmpMsg, i, j, l);
    }

    public javax.jms.Destination getDestination() throws JMSException {
        return getProducer().getDestination();
    }

    public javax.jms.Queue getQueue() throws JMSException {
        return getSender().getQueue();
    }

    public void send(javax.jms.Queue queue,Message msg) throws JMSException {
        Message tmpMsg = unwrapDestinations(msg);
        javax.jms.Queue tmpQueue = unwrapDestinations(queue);
        getSender().send(tmpQueue, tmpMsg);
    }

    public void send(javax.jms.Queue queue,Message msg,int i,int j,long l)
        throws JMSException {
        Message tmpMsg = unwrapDestinations(msg);
        javax.jms.Queue tmpQueue = unwrapDestinations(queue);
        getSender().send(tmpQueue, tmpMsg, i, j, l);
    }

    public void publish(Message msg) throws JMSException {
        Message tmpMsg = unwrapDestinations(msg);
        getPublisher().publish(tmpMsg);
    }

    public void publish(Message msg,int i,int j,long l) throws JMSException {
        Message tmpMsg = unwrapDestinations(msg);
        getPublisher().publish(tmpMsg, i, j, l);
    }

    public javax.jms.Topic getTopic() throws JMSException {
        return getPublisher().getTopic();
    }

    public void publish(javax.jms.Topic topic,Message msg) throws JMSException {
        Message tmpMsg = unwrapDestinations(msg);
        javax.jms.Topic tmpTopic = unwrapDestinations(topic);
        getPublisher().publish(tmpTopic, tmpMsg);
    }

    public void publish(javax.jms.Topic topic,Message msg,int i,int j,long l)
        throws JMSException {
        Message tmpMsg = unwrapDestinations(msg);
        javax.jms.Topic tmpTopic = unwrapDestinations(topic);
        getPublisher().publish(tmpTopic, tmpMsg, i, j, l);
    }

    private MessageProducer getProducer() {
        return this.mp;
    }

    private QueueSender getSender() {
        return (QueueSender) this.mp;
    }

    private TopicPublisher getPublisher() {
        return (TopicPublisher) this.mp;
    }

    private Message unwrapDestinations(Message msg) throws JMSException {
         Destination jmsReply = msg.getJMSReplyTo();
         if (jmsReply != null) {
             msg.setJMSReplyTo(unwrapDestinations(jmsReply));
         }

         Destination jmsDest = msg.getJMSDestination();
         if (jmsDest != null) {
             msg.setJMSDestination(unwrapDestinations(jmsDest));
         }
         return msg;
    }

    private Destination unwrapDestinations(Destination dest) throws JMSException{
         if ( (dest instanceof DestinationAdapter)
            || (dest instanceof QueueProxy)
            || (dest instanceof TopicProxy)) {
              return ((DestinationAdapter) dest)._getPhysicalDestination();
         } else {
              return dest;
         }
    }

    private javax.jms.Queue unwrapDestinations(javax.jms.Queue queue) throws JMSException {
         if (queue instanceof QueueProxy) {
             return (javax.jms.Queue) 
             ((DestinationAdapter) queue)._getPhysicalDestination();
         } else {
              return queue;
         }
    }

    private javax.jms.Topic unwrapDestinations(javax.jms.Topic topic) throws JMSException {
         if (topic instanceof TopicProxy) {
             return (javax.jms.Topic) 
             ((DestinationAdapter) topic)._getPhysicalDestination();
         } else {
              return topic;
         }
    }

}

