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

import javax.transaction.xa.Xid;

import javax.transaction.xa.XAException;
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

    public abstract int prepare(Xid xid) throws XAException;
    
     public abstract void commit(Xid xid, boolean onePhase) throws XAException;
     
     public abstract void end(Xid xid, int flags) throws XAException;
     
     public abstract void rollback(Xid xid) throws XAException;
     
     public abstract void start(Xid xid, int flags) throws XAException ;
     
     public abstract void setToRollback(boolean flag);
     
     public abstract boolean endCalled();
     
     public abstract void startDelayedXA();
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
    
   /**
     * The following is a method that has been added to aid printing of XID.
     * This method of printing the XID may differ for different providers.
     */
    public String printXid(Xid xid) {
        if (xid == null) {
            return "null";
        }
        
    /* The following code can be enabled for TM implementations 
     * that do not have a toString implementation for xids
     */
        
        String hextab = "0123456789ABCDEF";
        StringBuffer data = new StringBuffer(256);
        int i;
        int value;
        try {
        
        if (xid.getFormatId() == -1) {
            return "-1";
        }

        // Add branch qualifier. Convert data string to hex
        for (i = 0; i < xid.getBranchQualifier().length; i++) {
            value = xid.getBranchQualifier()[i] & 0xff;
            data.append(hextab.charAt(value / 16));
            data.append(hextab.charAt(value & 15));
        }

        // Add global transaction id
        for (i = 0; i < xid.getGlobalTransactionId().length; i++) {
            value = xid.getGlobalTransactionId()[i] & 0xff;
            data.append(hextab.charAt(value / 16));
            data.append(hextab.charAt(value & 15));
        }
        }
        catch (Exception e)
        {
            //for some providers this may not work.
            ;
        }

        return new String(data);
        
    }
}
