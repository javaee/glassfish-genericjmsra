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
package com.sun.genericra.inbound;


import com.sun.genericra.inbound.sync.*;

import com.sun.genericra.inbound.async.*;

import com.sun.genericra.util.*;

import javax.resource.ResourceException;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.endpoint.MessageEndpointFactory;
/**
 *
 * @author rp138409
 */
public class EndpointConsumerFactory {
    
    /** Creates a new instance of EndpointConsumerFactory */
    public EndpointConsumerFactory() {
    }
    
    public static AbstractConsumer createEndpointConsumer(MessageEndpointFactory mef,
        javax.resource.spi.ActivationSpec actspec) throws ResourceException   {        
        AbstractConsumer ret = null;
        String type = ((com.sun.genericra.inbound.ActivationSpec)actspec).getDeliveryType();
        if ((type == null) || (type.trim().equals(""))) {
            ret = new EndpointConsumer(mef, actspec);
        } else if ("Synchronous".equals(type.trim())) {
            ret = new SyncConsumer(mef, actspec);
        } else if ("Asynchronous".equals(type.trim())) {
            ret = new EndpointConsumer(mef, actspec);        
        } else {
            ret = new EndpointConsumer(mef, actspec);
        }    
        return ret;
    }           
    
    public static AbstractConsumer createEndpointConsumer(
            javax.resource.spi.ActivationSpec actspec) throws ResourceException   {
        return createEndpointConsumer(null,actspec);
    }
}
