/**
 * Copyright 2004-2010 Sun Microsystems, Inc.
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

import javax.jms.Connection;


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
     * Decide whether to override the underlying XAResource's implementation of isSameRM() 
     * so that it returns false.
     * 
     * If the decision can be delegated to the underlying XAResourceImplementations,
     * return true.
     * 
     * If this isSameRM() must return false, return false.
     */
    public boolean compare(XAResourceType other);

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
