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
package com.sun.genericra.outbound;

import javax.jms.JMSException;


/**
 * Queue wrapper
 * @author Sivakumar Thyagarajan
 */
public class QueueProxy extends DestinationAdapter implements javax.jms.Queue {
    public String getQueueName() throws JMSException {
        return ((javax.jms.Queue) this._getPhysicalDestination()).getQueueName();
    }

    protected String getDestinationClassName() {
        return this.getQueueClassName();
    }
}
