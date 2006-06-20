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
 * AbstractXAResource object used by all XAResource
 * objects in generic jms ra. The class contains the
 * logic to compare the XAResource object, when
 * RMPolicy is set to "One for Physical Connection".
 *
 * @author Binod P.G
 */
public abstract class AbstractXAResourceType implements XAResourceType,
    XAResource {
    private Connection con;
    private String rmPolicy;

    /**
     * Abstract method declaration.
     */
    public abstract Object getWrappedObject();

    /**
     * Set the physical jms connection object associated with
     * this XAResource wrapper
     */
    public void setConnection(Connection con) {
        this.con = con;
    }

    /**
     * Retrieves the physical JMS connection object.
     */
    public Connection getConnection() {
        return this.con;
    }

    /**
     * Set the Resource Manager policy
     */
    public void setRMPolicy(String policy) {
        this.rmPolicy = policy;
    }

    /**
     * Retrieve the RM policy
     */
    public String getRMPolicy() {
        return this.rmPolicy;
    }

    /**
     * If any one of the resources are configured with
     * a policy of "OneForPhysicalConnection", then
     * compare physical connection. Otherwise, return true
     * so that the actual XAResource wrapper can delegate it
     * to the underlying XAResource implementation.
     */
    public boolean compare(XAResourceType type) {
        String rmPerMc = GenericJMSRAProperties.ONE_PER_PHYSICALCONNECTION;

        if (rmPerMc.equalsIgnoreCase(rmPolicy) ||
                rmPerMc.equalsIgnoreCase(type.getRMPolicy())) {
            return type.getConnection().equals(getConnection());
        } else {
            return true;
        }
    }
}
