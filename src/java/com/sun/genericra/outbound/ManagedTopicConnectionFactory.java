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

import com.sun.genericra.util.Constants;

import javax.jms.*;

import javax.resource.spi.security.PasswordCredential;


/**
 * MCF for javax.jms.TopicConnectionFactory
 * @author Sivakumar Thyagarajan
 */
public class ManagedTopicConnectionFactory
    extends AbstractManagedConnectionFactory {
    public ManagedTopicConnectionFactory() {
        this.destinationMode = Constants.TOPIC_SESSION;
    }

    protected String getActualConnectionFactoryClassName() {
        if (this.getSupportsXA()) {
            return this.getXATopicConnectionFactoryClassName();
        } else {
            return this.getTopicConnectionFactoryClassName();
        }
    }

    protected XAConnection createXAConnection(PasswordCredential pc,
        javax.jms.ConnectionFactory cf) throws JMSException {
        if (pc != null) {
            return ((XATopicConnectionFactory) cf).createXATopicConnection(pc.getUserName(),
                new String(pc.getPassword()));
        } else {
            return ((XATopicConnectionFactory) cf).createXATopicConnection();
        }
    }

    protected Connection createConnection(PasswordCredential pc,
        javax.jms.ConnectionFactory cf) throws JMSException {
        if (pc != null) {
            return ((TopicConnectionFactory) cf).createTopicConnection(pc.getUserName(),
                new String(pc.getPassword()));
        } else {
            return ((TopicConnectionFactory) cf).createTopicConnection();
        }
    }
}
