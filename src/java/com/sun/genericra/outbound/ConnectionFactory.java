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
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.TopicConnection;
import javax.naming.Reference;
import javax.resource.Referenceable;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ManagedConnectionFactory;
import com.sun.genericra.util.ExceptionUtils;
import com.sun.genericra.util.LogUtils;
import java.util.logging.*;

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

public class ConnectionFactory implements 
                javax.jms.ConnectionFactory, javax.jms.TopicConnectionFactory, 
                javax.jms.QueueConnectionFactory, Serializable, Referenceable  {
    
    private ManagedConnectionFactory mcf;
    private ConnectionManager cm;
    private Reference ref;

    private static Logger logger;
    static {
        logger = LogUtils.getLogger();
    }

    public ConnectionFactory(ManagedConnectionFactory mcf, ConnectionManager cm){
        this.mcf = mcf;
        this.cm = cm;
    }

    //create a connection with the default configured username and password!
    public Connection createConnection() throws JMSException {
        Connection con = null;
        try {
            con = (Connection)this.cm.allocateConnection(this.mcf, null);
        } catch (ResourceException e) {
            logger.log(Level.INFO, e.getMessage(), e);
            //Failed to allocate!
            throw ExceptionUtils.newJMSException(e);
        }
        return con;
    }

    //allocate a connection via the connection manager
    public Connection createConnection(String userName, String password) throws JMSException {
        ConnectionRequestInfo info = new ConnectionRequestInfo(this.mcf, userName, password);
        Connection con = null;
        try {
            con = (Connection)this.cm.allocateConnection(this.mcf, info);
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

    public TopicConnection createTopicConnection(String userName, String password) throws JMSException {
        return (TopicConnection) createConnection(userName, password); 
    }

    public QueueConnection createQueueConnection() throws JMSException {
        return (QueueConnection) createConnection(); 
    }

    public QueueConnection createQueueConnection(String userName, String password) throws JMSException {
        return (QueueConnection) createConnection(userName, password); 
    }
    
    public void setReference(Reference ref) {
        this.ref = ref;
    }
    
    public Reference getReference() {
        return this.ref;
    }
}
