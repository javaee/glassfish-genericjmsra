/**
 * Copyright (c) 2003-2017 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.genericra.inbound.sync;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import java.io.Serializable;

/**
 * See WMessage
 *
 * @author Frank Kieviet
 * @version $Revision: 1.1 $
 */
public class WObjectMessageIn extends WMessageIn implements ObjectMessage {
    private ObjectMessage mDelegate;
    
    /**
     * Constructor
     * 
     * @param delegate real msg
     * @param ackHandler callback to call when ack() or recover() is called
     * @param ibatch index of this message in a batch; -1 for non-batched
     */
    public WObjectMessageIn(ObjectMessage delegate, AckHandler ackHandler, int ibatch) {
        super(delegate, ackHandler, ibatch);
        mDelegate = delegate;
    }

    /**
     * @see javax.jms.ObjectMessage#getObject()
     */
    public Serializable getObject() throws JMSException {
        return mDelegate.getObject();
    }

    /**
     * @see javax.jms.ObjectMessage#setObject(java.io.Serializable)
     */
    public void setObject(Serializable arg0) throws JMSException {
        mDelegate.setObject(arg0);
    }

}
