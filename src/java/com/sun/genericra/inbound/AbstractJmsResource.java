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
