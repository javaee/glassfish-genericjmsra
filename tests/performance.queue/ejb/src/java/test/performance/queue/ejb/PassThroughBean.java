/*
 * Copyright (c) 2004-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
