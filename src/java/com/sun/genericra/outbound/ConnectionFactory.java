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
