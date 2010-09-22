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
package test.performance.queue.ejb;

import javax.ejb.EJBException;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class PassThroughBean extends GenericPassThroughBean implements MessageDrivenBean, MessageListener {
    private Destination outboundQueue;
    private ConnectionFactory outboundQueueConnectionFactory;

    public PassThroughBean() {
    }

    @Override
    public ConnectionFactory getOutboundConnectionFactory() {
    	return outboundQueueConnectionFactory;
    }

    @Override
    public Destination getOutboundDestination() {
    	return outboundQueue;
    }

	public void ejbCreate() {
		try {
			InitialContext ic = new InitialContext();
			
            outboundQueueConnectionFactory = (ConnectionFactory) ic.lookup("java:comp/env/jms/QCFactory");

            outboundQueue = (Queue) ic.lookup("java:comp/env/jms/outboundQueue");
		} catch (NamingException e) {
			System.out.println("JNDI lookup failed: " + e.toString());
		}
	}
    
	@Override
	public void ejbRemove() throws EJBException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setMessageDrivenContext(MessageDrivenContext arg0) throws EJBException {
		// TODO Auto-generated method stub
		
	}

}