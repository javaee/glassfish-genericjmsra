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
        getProducer().setTimeToLive(l);
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

    public void send(Message msg, int i, int j, long l)
        throws JMSException {
        Message tmp = unwrapDestinations(msg);
        getProducer().send(tmp, i, j, l);
    }

    public void send(Destination dest, Message msg) throws JMSException {
        Message tmpMsg = unwrapDestinations(msg);
        Destination tmpDest = unwrapDestinations(dest);
        getProducer().send(tmpDest, tmpMsg);
    }

    public void send(Destination dest, Message msg, int i, int j, long l)
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

    public void send(javax.jms.Queue queue, Message msg)
        throws JMSException {
        Message tmpMsg = unwrapDestinations(msg);
        javax.jms.Queue tmpQueue = unwrapDestinations(queue);
        getSender().send(tmpQueue, tmpMsg);
    }

    public void send(javax.jms.Queue queue, Message msg, int i, int j, long l)
        throws JMSException {
        Message tmpMsg = unwrapDestinations(msg);
        javax.jms.Queue tmpQueue = unwrapDestinations(queue);
        getSender().send(tmpQueue, tmpMsg, i, j, l);
    }

    public void publish(Message msg) throws JMSException {
        Message tmpMsg = unwrapDestinations(msg);
        getPublisher().publish(tmpMsg);
    }

    public void publish(Message msg, int i, int j, long l)
        throws JMSException {
        Message tmpMsg = unwrapDestinations(msg);
        getPublisher().publish(tmpMsg, i, j, l);
    }

    public javax.jms.Topic getTopic() throws JMSException {
        return getPublisher().getTopic();
    }

    public void publish(javax.jms.Topic topic, Message msg)
        throws JMSException {
        Message tmpMsg = unwrapDestinations(msg);
        javax.jms.Topic tmpTopic = unwrapDestinations(topic);
        getPublisher().publish(tmpTopic, tmpMsg);
    }

    public void publish(javax.jms.Topic topic, Message msg, int i, int j, long l)
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

    private Destination unwrapDestinations(Destination dest)
        throws JMSException {
        if ((dest instanceof DestinationAdapter) ||
                (dest instanceof QueueProxy) || (dest instanceof TopicProxy)) {
            return ((DestinationAdapter) dest)._getPhysicalDestination();
        } else {
            return dest;
        }
    }

    private javax.jms.Queue unwrapDestinations(javax.jms.Queue queue)
        throws JMSException {
        if (queue instanceof QueueProxy) {
            return (javax.jms.Queue) ((DestinationAdapter) queue)._getPhysicalDestination();
        } else {
            return queue;
        }
    }

    private javax.jms.Topic unwrapDestinations(javax.jms.Topic topic)
        throws JMSException {
        if (topic instanceof TopicProxy) {
            return (javax.jms.Topic) ((DestinationAdapter) topic)._getPhysicalDestination();
        } else {
            return topic;
        }
    }
}
