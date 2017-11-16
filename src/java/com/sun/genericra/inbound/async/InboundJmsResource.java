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

package com.sun.genericra.inbound.async;

import com.sun.genericra.inbound.*;
import com.sun.genericra.util.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.*;
import javax.resource.spi.endpoint.*;
import javax.resource.spi.work.*;

import javax.transaction.xa.XAResource;


/**
 * ServerSession implementation as per JMS 1.1 specification.
 * This serves as a placeholder for a MessageEndpoint obtained from
 * application server.
 *
 * @author Binod P.G
 */
public class InboundJmsResource extends AbstractJmsResource implements ServerSession {
    private static Logger _logger;

    static {
        _logger = LogUtils.getLogger();
    }
    private DeliveryHelper helper;

    public InboundJmsResource(Session session, InboundJmsResourcePool pool)
        throws JMSException {
        super(session, pool, null);
    }

    public InboundJmsResource(Session session, InboundJmsResourcePool pool,
        XAResource xaresource) throws JMSException {
        super(session, pool, xaresource);
    }

    public void start() throws JMSException {
        try {
            _logger.log(Level.FINER,
                "Provider is starting the message consumtion");

            Work w = new WorkImpl(this);
            WorkManager wm = ra.getWorkManager();
            wm.scheduleWork(w);
        } catch (WorkException e) {
            throw ExceptionUtils.newJMSException(e);
        }
    }

    /**
     * Each time a serversession is checked out from the pool, the listener
     * will be recreated.
     */
    public InboundJmsResource refreshListener() throws JMSException {
        MessageListener listener = new MessageListener(this,(InboundJmsResourcePool) pool);
        this.session.setMessageListener(listener);
          helper = new DeliveryHelper(this, (InboundJmsResourcePool)pool);

        return this;
    }

    public void destroy() {
        if (session != null) {
            try {
                session.close();
            } catch (Exception e) {
                if (_logger.isLoggable(Level.FINE)) {
                    _logger.log(Level.FINE, e.getMessage(), e);
                }
            }
        }

        releaseEndpoint();
    }

    public boolean isFree() {
        return free;
    }

    public InboundJmsResource markAsBusy() {
        this.free = false;

        return this;
    }

    public InboundJmsResource markAsFree() {
        this.free = true;

        return this;
    }

    public DeliveryHelper getDeliveryHelper() {
        return this.helper;
    }



    public Session getSession() {
        _logger.log(Level.FINEST, "Message provider got the session :" +
            session);

        return session;
    }



    public void release() {
        ((InboundJmsResourcePool)getPool()).put(this);
    }

    /**
     * Creates the MessageEndpoint and start the delivery.
     */
    public void refresh() throws JMSException {
        MessageEndpointFactory mef = pool.getConsumer()
                                         .getMessageEndpointFactory();

        try {
            _logger.log(Level.FINER, "Creating message endpoint : " +
                xaresource);
            endPoint = mef.createEndpoint(helper.getXAResource());
            endPoint.beforeDelivery(this.ra.getListeningMethod());
            _logger.log(Level.FINE, "Created endpoint : ");
        } catch (Exception e) {
            _logger.log(Level.SEVERE, "Refresh resource failed");
            // TODO. Should we eat this exception?
            //throw ExceptionUtils.newJMSException(e);
        }
    }

    /**
     * Completes the Message delivery and release the MessageEndpoint.
     */
    public void releaseEndpoint() {
        try {
            if (this.endPoint != null) {
                this.endPoint.afterDelivery();
            }
        } catch (Exception re) {
            _logger.log(Level.SEVERE, "After delivery failed " + re.getMessage(), re);
        } finally {
            if (this.endPoint != null) {
                try {
                    this.endPoint.release ();
                    _logger.log(Level.FINE, "InboundJMSResource: released endpoint : ");
                } catch (Exception e) {
                    _logger.log(Level.SEVERE,
                        "InboundJMSResource: release endpoint failed ");
                    ;
                }

                this.endPoint = null;
            }
        }
    }
}
