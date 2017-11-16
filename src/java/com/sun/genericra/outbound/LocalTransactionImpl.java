/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
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
