/**
 * Copyright (c) 2004-2005 Oracle and/or its affiliates. All rights reserved.
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

import com.sun.genericra.util.Constants;

import javax.jms.*;

import javax.resource.spi.security.PasswordCredential;


/**
 * MCF for javax.jms.QueueConnectionFactory
 * @author Sivakumar Thyagarajan
 */
public class ManagedQueueConnectionFactory
    extends AbstractManagedConnectionFactory {
    public ManagedQueueConnectionFactory() {
        this.destinationMode = Constants.QUEUE_SESSION;
    }

    protected String getActualConnectionFactoryClassName() {
        if (this.getSupportsXA()) {
            return this.getXAQueueConnectionFactoryClassName();
        } else {
            return this.getQueueConnectionFactoryClassName();
        }
    }

    protected XAConnection createXAConnection(PasswordCredential pc,
        javax.jms.ConnectionFactory cf) throws JMSException {
        if (pc != null && (!pc.getUserName().equals(""))) {
            return ((XAQueueConnectionFactory) cf).createXAQueueConnection(pc.getUserName(),
                new String(pc.getPassword()));
        } else {
            return ((XAQueueConnectionFactory) cf).createXAQueueConnection();
        }
    }

    protected Connection createConnection(PasswordCredential pc,
        javax.jms.ConnectionFactory cf) throws JMSException {
        if (pc != null && (!pc.getUserName().equals(""))) {
            return ((QueueConnectionFactory) cf).createQueueConnection(pc.getUserName(),
                new String(pc.getPassword()));
        } else {
            return ((QueueConnectionFactory) cf).createQueueConnection();
        }
    }
}
