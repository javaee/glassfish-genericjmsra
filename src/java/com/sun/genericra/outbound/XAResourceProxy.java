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
import javax.transaction.xa.Xid;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import java.util.logging.*;
import com.sun.genericra.util.ExceptionUtils;
import com.sun.genericra.util.LogUtils;


/**
 * <code>XAResource</code> wrapper for Generic JMS Connector. This class
 * intercepts all calls to the actual XAResource object of the physical
 * JMS connection and performs corresponding book-keeping tasks in the 
 * ManagedConnection representing the physical connection.
 * 
 *  @todo: This should be a dynamic proxy as well!!
 */
public class XAResourceProxy implements XAResource {
    ManagedConnection mc;

    private static Logger logger;
    static {
        logger = LogUtils.getLogger();
    }

    /**
     * Constructor for XAResourceImpl
     * 
     * @param xar
     *            <code>XAResource</code>
     * @param mc
     *            <code>ManagedConnection</code>
     */
    public XAResourceProxy(ManagedConnection mc) {
        this.mc = mc;
    }

    /**
     * Commit the global transaction specified by xid.
     * 
     * @param xid
     *            A global transaction identifier
     * @param onePhase
     *            If true, the resource manager should use a one-phase commit
     *            protocol to commit the work done on behalf of xid.
     */
    public void commit(Xid xid, boolean onePhase) throws XAException {
        debug("COmmitting tx...");
        try {
            _getXAResource().commit(xid, onePhase);
        } finally {
            try {
                mc._endXaTx();
            } catch (Exception e) {
                throw ExceptionUtils.newXAException( e );
            }
        }
    }

    /**
     * Ends the work performed on behalf of a transaction branch.
     * 
     * @param xid
     *            A global transaction identifier that is the same as what was
     *            used previously in the start method.
     * @param flags
     *            One of TMSUCCESS, TMFAIL, or TMSUSPEND
     */
    public void end(Xid xid, int flags) throws XAException {
        debug("Ending tx...");
        _getXAResource().end(xid, flags);
        //mc.transactionCompleted();
    }

    /**
     * Tell the resource manager to forget about a heuristically completed
     * transaction branch.
     * 
     * @param xid
     *            A global transaction identifier
     */
    public void forget(Xid xid) throws XAException {
        _getXAResource().forget(xid);
    }

    /**
     * Obtain the current transaction timeout value set for this
     * <code>XAResource</code> instance.
     * 
     * @return the transaction timeout value in seconds
     */
    public int getTransactionTimeout() throws XAException {
        return _getXAResource().getTransactionTimeout();
    }

    /**
     * This method is called to determine if the resource manager instance
     * represented by the target object is the same as the resouce manager
     * instance represented by the parameter xares.
     * 
     * @param xares
     *            An <code>XAResource</code> object whose resource manager
     *            instance is to be compared with the resource
     * @return true if it's the same RM instance; otherwise false.
     */
    public boolean isSameRM(XAResource xares) throws XAException {
        return _getXAResource().isSameRM(xares);
    }

    /**
     * Ask the resource manager to prepare for a transaction commit of the
     * transaction specified in xid.
     * 
     * @param xid
     *            A global transaction identifier
     * @return A value indicating the resource manager's vote on the outcome of
     *         the transaction. The possible values are: XA_RDONLY or XA_OK. If
     *         the resource manager wants to roll back the transaction, it
     *         should do so by raising an appropriate <code>XAException</code>
     *         in the prepare method.
     */
    public int prepare(Xid xid) throws XAException {
        debug("Preparing tx...");
        return _getXAResource().prepare(xid);
    }

    /**
     * Obtain a list of prepared transaction branches from a resource manager.
     * 
     * @param flag
     *            One of TMSTARTRSCAN, TMENDRSCAN, TMNOFLAGS. TMNOFLAGS must be
     *            used when no other flags are set in flags.
     * @return The resource manager returns zero or more XIDs for the
     *         transaction branches that are currently in a prepared or
     *         heuristically completed state. If an error occurs during the
     *         operation, the resource manager should throw the appropriate
     *         <code>XAException</code>.
     */
    public Xid[] recover(int flag) throws XAException {
        return _getXAResource().recover(flag);
    }

    /**
     * Inform the resource manager to roll back work done on behalf of a
     * transaction branch
     * 
     * @param xid
     *            A global transaction identifier
     */
    public void rollback(Xid xid) throws XAException {
        debug("Rolling back tx...");
        try {
            _getXAResource().rollback(xid);
        } finally {
            try {
                mc._endXaTx();
            } catch (Exception e) {
                throw ExceptionUtils.newXAException( e );
            }
        }
    }

    /**
     * Set the current transaction timeout value for this
     * <code>XAResource</code> instance.
     * 
     * @param seconds
     *            the transaction timeout value in seconds.
     * @return true if transaction timeout value is set successfully; otherwise
     *         false.
     */
    public boolean setTransactionTimeout(int seconds) throws XAException {
        return _getXAResource().setTransactionTimeout(seconds);
    }

    /**
     * Start work on behalf of a transaction branch specified in xid.
     * 
     * @param xid
     *            A global transaction identifier to be associated with the
     *            resource
     * @return flags One of TMNOFLAGS, TMJOIN, or TMRESUME
     */
    public void start(Xid xid, int flags) throws XAException {
        try {
            mc._startXaTx();
        } catch (Exception e) {
            XAException xae = new XAException();
            xae.initCause(e);
            throw xae;
        }
        debug("Starting tx...");
        _getXAResource().start(xid, flags);
    }

    private XAResource _getXAResource() throws XAException {
        try { 
            return  this.mc._getXAResource();
        } catch (Exception e) {
            throw ExceptionUtils.newXAException(e);
        }
    }
    
    void debug(String s) {
        logger.log(Level.FINEST, "Managed Connection = " + mc + " XAResourceProxy"+s);
    }
}
