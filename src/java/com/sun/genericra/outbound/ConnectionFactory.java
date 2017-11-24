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

import com.sun.genericra.util.ExceptionUtils;
import com.sun.genericra.util.LogUtils;

import java.io.Serializable;

import java.util.logging.*;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.TopicConnection;

import javax.naming.Reference;

import javax.resource.Referenceable;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ManagedConnectionFactory;


/**
 * <code>ConnectionFactory<code> implementation for the generic JMS resource adapter
 * Implements the <code>ConnectionFactory</code>, <code>TopicConnectionFactory</code>,
 * <code>QueueConnectionFactory</code> interfaces. Provides an interface for getting
 * a connection to a JMS provider. Application code looks up a
 * <code>ConnectionFactory </code> instance from JNDI namespace and uses it to
 * get EIS connections.
 *
 * The <code>ManagedConnectionFactory</code> creates an instance of this class
 * while asked to create a <code>ConnectionFactory</code>.
 *
 * @author Sivakumar Thyagarajan
 */
public class ConnectionFactory implements javax.jms.ConnectionFactory,
    javax.jms.TopicConnectionFactory, javax.jms.QueueConnectionFactory,
    Serializable, Referenceable {
    private static Logger logger;

    static {
        logger = LogUtils.getLogger();
    }

    private ManagedConnectionFactory mcf;
    private ConnectionManager cm;
    private Reference ref;

    public ConnectionFactory(ManagedConnectionFactory mcf, ConnectionManager cm) {
        this.mcf = mcf;
        this.cm = cm;
    }

    //create a connection with the default configured username and password!
    public Connection createConnection() throws JMSException {
        Connection con = null;

        try {
            con = (Connection) this.cm.allocateConnection(this.mcf, null);
        } catch (ResourceException e) {
            logger.log(Level.INFO, e.getMessage(), e);

            //Failed to allocate!
            throw ExceptionUtils.newJMSException(e);
        }

        return con;
    }

    //allocate a connection via the connection manager
    public Connection createConnection(String userName, String password)
        throws JMSException {
        ConnectionRequestInfo info = new ConnectionRequestInfo(this.mcf,
                userName, password);
        Connection con = null;

        try {
            con = (Connection) this.cm.allocateConnection(this.mcf, info);
        } catch (ResourceException e) {
            logger.log(Level.INFO, e.getMessage(), e);

            //Failed to allocate!
            throw ExceptionUtils.newJMSException(e);
        }

        return con;
    }

    public TopicConnection createTopicConnection() throws JMSException {
        return (TopicConnection) createConnection();
    }

    public TopicConnection createTopicConnection(String userName,
        String password) throws JMSException {
        return (TopicConnection) createConnection(userName, password);
    }

    public QueueConnection createQueueConnection() throws JMSException {
        return (QueueConnection) createConnection();
    }

    public QueueConnection createQueueConnection(String userName,
        String password) throws JMSException {
        return (QueueConnection) createConnection(userName, password);
    }

    public void setReference(Reference ref) {
        this.ref = ref;
    }

    public Reference getReference() {
        return this.ref;
    }
}
