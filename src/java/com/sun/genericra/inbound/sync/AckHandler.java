/*
 * AckHandler.java
 *
 * Created on April 6, 2007, 8:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.genericra.inbound.sync;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * A callback that can be installed in a message that should be triggered when 
 * acknowledge() is called on the message. The message must make sure that the 
 * callback is called only once.
 *
 * @author Frank Kieviet
 * @version $Revision: 1.2 $
 */
public abstract class AckHandler {
    
    /**
     * @param isRollbackOnly true if setRollbackOnly was called first
     * @param m message on which this was called
     * @throws JMSException delegated
     */
    public abstract void ack(boolean isRollbackOnly, Message m) throws JMSException;
}

