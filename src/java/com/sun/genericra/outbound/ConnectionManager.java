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
package com.sun.genericra.outbound;

import java.io.Serializable;

/**
 * Default simple <code>ConnectionManager</code> implementation of the generic
 * JMS Resource Adapter
 * 
 * Creates a new <code>ManagedConnection</code> for every 
 * <code>allocateConnection</code>
 * 
 * @author Sivakumar Thyagarajan 
 */
public class ConnectionManager implements 
                    javax.resource.spi.ConnectionManager, Serializable {
    public Object allocateConnection(
                    javax.resource.spi.ManagedConnectionFactory mcf,
                    javax.resource.spi.ConnectionRequestInfo cxRequestInfo)
                    throws javax.resource.ResourceException {
        javax.resource.spi.ManagedConnection mc = mcf.createManagedConnection(
                        null, cxRequestInfo);
        return mc.getConnection(null, cxRequestInfo);
    }

}
