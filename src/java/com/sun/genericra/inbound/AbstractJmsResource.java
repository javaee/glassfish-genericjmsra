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

package com.sun.genericra.inbound;
import com.sun.genericra.GenericJMSRA;
import com.sun.genericra.inbound.*;
import com.sun.genericra.inbound.async.DeliveryHelper;
import com.sun.genericra.util.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.*;

import javax.resource.ResourceException;
import javax.resource.spi.*;
import javax.resource.spi.endpoint.*;
import javax.resource.spi.work.*;

import javax.transaction.xa.XAResource;
/**
 *
 * @author rp138409
 */
public abstract class AbstractJmsResource {
    protected static Logger _logger;
    
    static {
        _logger = LogUtils.getLogger();
    }
    
    protected Session session;
    protected XAResource xaresource;
    protected MessageEndpoint endPoint;
    protected boolean free;
    protected GenericJMSRA ra;
    protected AbstractJmsResourcePool pool;
    protected DeliveryHelper helper;
    
    public AbstractJmsResource(Session session, AbstractJmsResourcePool pool,
            XAResource xaresource) throws JMSException {
        this.session = session;
        this.xaresource = xaresource;
        this.pool = pool;
        this.ra = (GenericJMSRA) pool.getConsumer().getResourceAdapter();
    }
    public AbstractJmsResourcePool getPool() {
        return this.pool;
    }
    
    public XASession getXASession() {
        return (XASession) session;
    }
      public XAResource getXAResource() {
        return this.xaresource;
    }  
    public MessageEndpoint getEndpoint() {
        return this.endPoint;
    }
}
