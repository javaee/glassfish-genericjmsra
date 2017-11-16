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
import com.sun.genericra.util.*;
import com.sun.genericra.monitoring.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.*;

import javax.resource.ResourceException;

import javax.transaction.xa.XAResource;
/**
 * This class has all the common methods, that are used by a sync/async
 * pool implementation.  Can be extended to create custom resource pools.
 *
 */
public abstract class AbstractJmsResourcePool {
    protected static Logger _logger;
    
    /* Get the logger for the RA.
     */
    static {
        _logger = LogUtils.getLogger();
    }
    
    /* Consumer that creates this pool
     */
    protected AbstractConsumer consumer;
    
    /* Jms connection that is used by this pool
     */
    protected Connection con = null;
    
    /* Dead message destination connection used to send messages to dmq
     */
    protected Connection dmdCon = null;
    
    /* Connection factory for creating the connections.
     */
    protected ConnectionFactory cf = null;
    
    /* Flag to indicate if the operations in pool are transacted.
     */
    protected boolean transacted = false;
    
    /* Alive or dead pool.
     */
    protected boolean destroyed = false;
    
    /* has the pool been stopped.
     */
    protected boolean stopped = false;
    
    /* Time out for getting a new session from pool.
     */
    protected long TIME_OUT = 180 * 1000;
    
    /* i18n.
     */
    protected StringManager sm = StringManager.getManager(GenericJMSRA.class);
    
    /** Creates a new instance of AbstractJmsResourcePool */
    public AbstractJmsResourcePool(AbstractConsumer cons, boolean transacted) {
        this.consumer = cons;
        this.transacted = transacted;
    }
    
       public boolean isTransacted() {
        return transacted;
    } 
    /**
     * Creates the XA connection from the XA connection factory.
     *
     * @param xacf XA connection factory
     * @return XAconnection object.
     */
    public  XAConnection createXAConnection(XAConnectionFactory xacf)
    throws JMSException {
        XAConnection xac = null;
        String user = consumer.getSpec().getUserName();
        String password = consumer.getSpec().getPassword();
        
		if (user == null || user.equals("")) {
	        if (isQueue()) {
	            xac = ((XAQueueConnectionFactory) xacf).createXAQueueConnection();
	        } else if (isTopic()) {
	            xac = ((XATopicConnectionFactory) xacf).createXATopicConnection();
	        } else {
	            xac = xacf.createXAConnection();
	        }			
		} else {
	        if (isQueue()) {
	            xac = ((XAQueueConnectionFactory) xacf).createXAQueueConnection(user,
	                    password);
	        } else if (isTopic()) {
	            xac = ((XATopicConnectionFactory) xacf).createXATopicConnection(user,
	                    password);
	        } else {
	            xac = xacf.createXAConnection(user, password);
	        }
		}
        return xac;
    }
    
    
    /**
     * Creates the XA session from the XA connection.
     *
     * @param con XA connection.
     * @return XASession object.
     */
    public XASession createXASession(XAConnection con) throws JMSException {
        XASession result = null;
        
        if (isQueue()) {
            result = ((XAQueueConnection) con).createXAQueueSession();
        } else if (isTopic()) {
            result = ((XATopicConnection) con).createXATopicSession();
        } else {
            result = con.createXASession();
        }
        
        return result;
    }
    
    public XAResource getXAResource(XASession session)
    throws JMSException {
        XAResource result = null;
        
        if (isTopic()) {
            result = ((XATopicSession) session).getXAResource();
        } else if (isQueue()) {
            result = ((XAQueueSession) session).getXAResource();
        } else {
            result = session.getXAResource();
        }
        
        return result;
    }
    
    public Connection createConnection(ConnectionFactory cf) throws JMSException {
        Connection con = null;
        String user = consumer.getSpec().getUserName();
        String password = consumer.getSpec().getPassword();
        
		if (user == null || user.equals("")) {
			if (isTopic()) {
				con = ((TopicConnectionFactory) cf).createTopicConnection();
			} else if (isQueue()) {
				con = ((QueueConnectionFactory) cf).createQueueConnection();
			} else {
				con = cf.createConnection();
			}
		} else {
			if (isTopic()) {
				con = ((TopicConnectionFactory) cf).createTopicConnection(user, password);
			} else if (isQueue()) {
				con = ((QueueConnectionFactory) cf).createQueueConnection(user, password);
			} else {
				con = cf.createConnection(user, password);
			}
		}
        
        return con;
    }
    
    public Session createSession(Connection con) throws JMSException {
        Session sess = null;
        
        if (isTopic()) {
            sess = ((TopicConnection) con).createTopicSession(false,
                    Session.AUTO_ACKNOWLEDGE);
        } else if (isQueue()) {
            sess = ((QueueConnection) con).createQueueSession(false,
                    Session.AUTO_ACKNOWLEDGE);
        } else {
            sess = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
        }
        
        return sess;
    }
    
  
    public String constructSelector(String name) {
        String selector = null;
        try {
            int instancecount = this.consumer.getSpec().getInstanceCount();
            int instanceid = this.consumer.getSpec().getInstanceID();
            String customeloadbalanceselector = this.consumer.getSpec().getLoadBalancingSelector();
            if ((this.consumer.getSpec().getLoadBalancingRequired()) &&
                    (instancecount > 1)) {
                /**
                 * We have the instance number, total number of instances and
                 * the selector, create the message selector for load balancing
                 * and concatenate it with any message selector that is configured
                 * through the depoyment descriptor
                 */
                String loadbalancingselector = "(JMSTimestamp - (JMSTimestamp/"
                        + instancecount + ")*" +
                        instancecount + ") = " + instanceid;
                String tmpselector = "";
                if ((name != null) && (!(name.equals("")))) {
                    tmpselector = "(" + name + ")" + " AND ";
                }
                _logger.log(Level.FINE, "Temporary selector  is " + tmpselector);
                if (!(customeloadbalanceselector.equals(""))) {
                    selector = tmpselector + "(" + customeloadbalanceselector + ")";
                } else {
                    selector = tmpselector + "(" + loadbalancingselector + ")";
                }
            } else {
                _logger.log(Level.FINE, "Returning default selector " + selector);
                return name;
            }
        } catch (Exception e) {
            e.printStackTrace();
            selector = null;
        }
        _logger.log(Level.FINE, "Returning selector " + selector);
        return selector;
    }
    
    public Connection createDmdConnection(ConnectionFactory cf)
    throws JMSException {
        Connection con = null;
        String user = consumer.getSpec().getUserName();
        String password = consumer.getSpec().getPassword();
        
		if (user == null || user.equals("")) {
	        if (consumer.getSpec().getDeadMessageDestinationType().equals(Constants.TOPIC)) {
	            con = ((TopicConnectionFactory) cf).createTopicConnection();
	        } else if (consumer.getSpec().getDeadMessageDestinationType().equals(Constants.QUEUE)) {
	            con = ((QueueConnectionFactory) cf).createQueueConnection();
	        } else {
	            con = cf.createConnection();
	        }			
		} else {
	        if (consumer.getSpec().getDeadMessageDestinationType().equals(Constants.TOPIC)) {
	            con = ((TopicConnectionFactory) cf).createTopicConnection(user,
	                    password);
	        } else if (consumer.getSpec().getDeadMessageDestinationType().equals(Constants.QUEUE)) {
	            con = ((QueueConnectionFactory) cf).createQueueConnection(user,
	                    password);
	        } else {
	            con = cf.createConnection(user, password);
	        }
		}
        return con;
    }
    
    public boolean isTopic() {
        return consumer.getSpec().getDestinationType().equals(Constants.TOPIC);
    }
     /**
     * Creates a message consumer for the inbound part of the RA
	*
     * @param sess Session
	* @param isXA boolean
	* @param isTopic boolean
	* @param dest Destination
	* @param spec RAJMSActivationSpec
	* @param ra RAJMSResourceAdapter
	* @throws JMSException failure
	* @return MessageConsumer
	*/
    public MessageConsumer createMessageConsumer(Session sess) throws JMSException {
        Destination dest = this.consumer.getDestination();
	if (this.transacted) {
            if (isTopic()) {
		if (Constants.DURABLE.equals(this.consumer.getSpec().getSubscriptionDurability())) {
                    return ((XATopicSession) sess).getTopicSession().
			createDurableSubscriber((Topic) dest,
			this.consumer.getSpec().getSubscriptionName(),
			this.consumer.getSpec().getMessageSelector(), false);
		} else {
                    return ((XATopicSession) sess).getTopicSession().
			createSubscriber((Topic) dest,
			this.consumer.getSpec().getMessageSelector(), false);
		}
            } else {
		return ((XAQueueSession) sess).getQueueSession().createReceiver(
                (javax.jms.Queue) dest, this.consumer.getSpec().getMessageSelector());
            }
	} else {
            if (isTopic()) {
		if (Constants.DURABLE.equals(this.consumer.getSpec().getSubscriptionDurability())) {
                    return ((TopicSession) sess).
			createDurableSubscriber((Topic) dest,
			this.consumer.getSpec().getSubscriptionName(),
			this.consumer.getSpec().getMessageSelector(), false);
                } else {
		return ((TopicSession) sess).
		createSubscriber((Topic) dest,
		this.consumer.getSpec().getMessageSelector(), false);
                }
            } else {
		 return ((QueueSession) sess).createReceiver((javax.jms.Queue) dest,
		this.consumer.getSpec().getMessageSelector());
            }
	}
    }   
    public boolean isQueue() {
        return consumer.getSpec().getDestinationType().equals(Constants.QUEUE);
    }
    
    public AbstractConsumer getConsumer() {
        return this.consumer;
    }
    
    public Connection getConnection() {
        return this.con;
    }
    
    public Connection getConnectionForDMD() throws  JMSException {
        return this.dmdCon;
    }
    
    public abstract int getMaxSize();
    
    public abstract long getMaxWaitTime();
    
    public abstract int  getCurrentResources();
    
    public abstract int getBusyResources();
    
    public abstract int getFreeResources();
    
    public abstract int getConnectionsInUse();
    public abstract int getWaiting();
}
