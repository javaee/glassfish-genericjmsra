/*
 * EndpointConsumerFactory.java
 *
 * Created on April 2, 2007, 6:11 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
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
