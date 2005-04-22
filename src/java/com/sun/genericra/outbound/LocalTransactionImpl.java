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

import javax.resource.ResourceException;
import javax.resource.spi.LocalTransactionException;
import com.sun.genericra.util.ExceptionUtils;

/**
 * <code>LocalTransaction</code> implementation for Generic JMS Resource Adapter.
 *
 * @author Sivakumar Thyagarajan
 */
public class LocalTransactionImpl implements javax.resource.spi.LocalTransaction {
    
    private ManagedConnection mc;
    
    /**
     * Constructor for <code>LocalTransaction</code>.
     * @param   mc  <code>ManagedConnection</code> that returns
     *          this <code>LocalTransaction</code> object as
     *          a result of <code>getLocalTransaction</code>
     */
    public LocalTransactionImpl(ManagedConnection mc) {
        this.mc = mc;
    }
    
    /**
     * Begin a local transaction.
     *
     */
    public void begin() throws ResourceException {
        try {
            mc._startLocalTx();
        } catch (Exception e) {
            throw ExceptionUtils.newResourceException( e );
        } 
    }
    
    /**
     * Commit a local transaction.
     */
    public void commit() throws ResourceException {
        try {
            mc._endLocalTx(true);
        } catch (Exception e) {
            throw ExceptionUtils.newResourceException( e );
        } 
    }
    
    /**
     * Rollback a local transaction.
     */
    public void rollback() throws ResourceException {
        try {
            mc._endLocalTx(false);
        } catch (Exception e) {
            throw ExceptionUtils.newResourceException( e );
        } 
    }
    
}
