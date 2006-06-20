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
package com.sun.genericra;

import com.sun.genericra.inbound.EndpointConsumer;
import com.sun.genericra.util.*;

import java.io.Serializable;

import java.lang.reflect.Method;

import java.security.*;

import java.util.*;
import java.util.logging.*;

import javax.jms.*;

import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.WorkManager;

import javax.transaction.xa.XAResource;


/**
 * Interface used by all XAResource objecrts that are wrapped.
 *
 * @author Binod P.G
 */
public interface XAResourceType {
    /**
     * Retrieve the XAResource object wrapped.
     */
    public Object getWrappedObject();

    /**
     * Set the Resource Manager policy
     */
    public void setRMPolicy(String policy);

    /**
     * Retrieve the RM policy
     */
    public String getRMPolicy();

    /**
     * If any one of the resources are configured with
     * a policy of "OneForPhysicalConnection", then
     * compare physical connection. Otherwise, return true
     * so that the actual XAResource wrapper can delegate it
     * to the underlying XAResource implementation.
     */
    public boolean compare(XAResourceType type);

    /**
     * Retrieves the physical JMS connection object.
     */
    public Connection getConnection();

    /**
     * Set the physical jms connection object associated with
     * this XAResource wrapper
     */
    public void setConnection(Connection con);
}
