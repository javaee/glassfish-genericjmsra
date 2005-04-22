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
import javax.resource.spi.security.PasswordCredential;

/**
 * ManagedConnectionFactory implementation for javax.jms.ConnectionFactory.
 * @author Sivakumar Thyagarajan
 */
public class ManagedJMSConnectionFactory extends AbstractManagedConnectionFactory {

    public ManagedJMSConnectionFactory(){
    }

    protected String getActualConnectionFactoryClassName() {
        if (this.getSupportsXA()){
            return this.getXAConnectionFactoryClassName();
        } else {
            return this.getConnectionFactoryClassName();
        }
    }

    protected javax.jms.XAConnection createXAConnection(PasswordCredential pc, 
                           javax.jms.ConnectionFactory cf ) throws JMSException {
        return ((XAConnectionFactory)cf).createXAConnection(
                           pc.getUserName(), new String(pc.getPassword()));

    }

    protected javax.jms.Connection createConnection(PasswordCredential pc, 
                    javax.jms.ConnectionFactory cf ) throws JMSException {
        return ((javax.jms.ConnectionFactory)cf).createConnection(
                           pc.getUserName(), new String(pc.getPassword()));
    }

}
