/*
 * Copyright (c) 2004-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.sun.genericra.outbound;

import com.sun.genericra.util.ExceptionUtils;

import javax.resource.ResourceException;
import javax.resource.spi.LocalTransactionException;


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
            throw ExceptionUtils.newResourceException(e);
        }
    }

    /**
     * Commit a local transaction.
     */
    public void commit() throws ResourceException {
        try {
            mc._endLocalTx(true);
        } catch (Exception e) {
            throw ExceptionUtils.newResourceException(e);
        }
    }

    /**
     * Rollback a local transaction.
     */
    public void rollback() throws ResourceException {
        try {
            mc._endLocalTx(false);
        } catch (Exception e) {
            throw ExceptionUtils.newResourceException(e);
        }
    }
}
