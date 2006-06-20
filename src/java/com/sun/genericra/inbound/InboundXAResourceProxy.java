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
package com.sun.genericra.inbound;

import com.sun.genericra.AbstractXAResourceType;
import com.sun.genericra.XAResourceType;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import com.sun.genericra.util.LogUtils;

import java.util.logging.*;

/**
 * <code>XAResource</code> wrapper for Generic JMS Connector. This class
 * intercepts all calls to the actual XAResource to facilitate redelivery.
 *
 *  Basically each (re)delivery for message will happen in different transactions
 *  from appserver perspective. However they will be intercepted and only
 *  one XID will be actually used with JMS provider.
 *
 *  @author Binod P.G
 */
public class InboundXAResourceProxy extends AbstractXAResourceType {
    private XAResource xar = null;
    private boolean toRollback = true;
    private boolean rolledback = false;
    private boolean suspended = false;
    private boolean endCalled = false;
    private static Logger logger;
    private Xid startXid = null;
      static {
        logger = LogUtils.getLogger();
    }
  
    private int startflags;
    private boolean startedDelayedXA = false;
    
    public InboundXAResourceProxy(XAResource xar) {
        this.xar = xar;
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
        
        debug("COMMIT , Xid got is " +
            printXid(xid));
             
        if (xid != null)
        {
            debug("COMMITTED is " +
            printXid(xid));
            xar.commit(xid, onePhase);
        }
        else
        {            
            debug("COMMITTED is " +
            printXid(startXid));
            xar.commit(startXid, onePhase);   
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
        debug("END , Xid got is " + printXid(xid));
        
        if (xid == null)
        {
            xid = startXid;
        }
        
        if ((startedDelayedXA == true) && (endCalled == false))
        {
            /**
             * Lets make sure that we are calling end only for the transaction
             * that was actually started.
             *
             */
            debug("ENDED is " + printXid(xid));
            endCalled = true; 
            xar.end(xid, flags);
        }
        

        /*
        if (beingRedelivered() == false) {
            debug("XAResourceProxy END , Xid ended is " +
                printXid(savedXid()));

            try {
                Thread.sleep(250);
                xar.end(savedXid(), flags);
            } catch (XAException xae) {
                debug("Got an exception at XAR end, " +
                    savedXid());
                debug("Error Code is " + xae.errorCode);

                if (xae.errorCode == XAException.XA_RBROLLBACK) {
                    debug("Got an RB_ROLLBACK error code");
                }

                xae.printStackTrace();

                // xar.rollback(savedXid());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (flags == XAResource.TMSUSPEND) {
                suspended = true;
            }

            endCalled = true;
        }
         */
    }

    /**
     * When message is being redelivered, i.e, end is called
     * and that too without TMSUSPEND flag, return true.
     *
     * This also assumes that, when the message is being
     * redelivered, the MDB wouldnt be coded to such that
     * transaction would need to be suspended.
     */
    private boolean beingRedelivered() {
        return (endCalled == true) && (suspended == false);
    }

    /**
     * Tell the resource manager to forget about a heuristically completed
     * transaction branch.
     *
     * @param xid
     *            A global transaction identifier
     */
    public void forget(Xid xid) throws XAException {
        xar.forget(xid);
    }

    /**
     * Obtain the current transaction timeout value set for this
     * <code>XAResource</code> instance.
     *
     * @return the transaction timeout value in seconds
     */
    public int getTransactionTimeout() throws XAException {
        return xar.getTransactionTimeout();
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
        XAResource inxa = xares;

        if (xares instanceof XAResourceType) {
            XAResourceType wrapper = (XAResourceType) xares;
            inxa = (XAResource) wrapper.getWrappedObject();

            if (!compare(wrapper)) {
                return false;
            }
        }

        return xar.isSameRM(inxa);
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
        debug("PREPARED got is " +
            printXid(xid));
        if (xid == null)
        {
            debug("PREPARED is " +
            printXid(startXid));
            return xar.prepare(startXid);
        }
        else
        {
            debug("PREPARED is " +
            printXid(xid));
            return xar.prepare(xid);
        }
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
        return xar.recover(flag);
    }

    /**
     * Inform the resource manager to roll back work done on behalf of a
     * transaction branch
     *
     * @param xid
     *            A global transaction identifier
     */
    public void rollback(Xid xid) throws XAException {
        debug("ROLLEDBACK , Xid got is " +
            printXid(xid));


        //rolledback = true;

        if (toRollback) {
            debug("ROLLEDBACK , Xid  is " +
            printXid(xid));
            xar.rollback(xid);
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
        return xar.setTransactionTimeout(seconds);
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


        if (startedDelayedXA)
        {
            debug("START startDelayedXA =true, Xid started is " +
            printXid(startXid));
            xar.start(xid, flags);            
        }
        else
        {
            startXid = xid;
            if (flags != xar.TMRESUME){
                startflags = flags;
            }
            debug("START startDelayed=false, Xid got is " +
            printXid(xid));
        }
        

        /*
        
        if (beingRedelivered()) {
            debug("redelivering " + printXid(xid));

            return;
        }

        int actualflag = flags;

        if (this.savedxid == null) {
            debug("DBG : Starting XA id" + printXid(xid));
            this.savedxid = xid;
        } else if (flags == XAResource.TMNOFLAGS) {
            if (rolledback) {
                rolledback = false;
                endCalled = false;

                if (suspended) {
                    suspended = false;
                    actualflag = XAResource.TMRESUME;
                } else {
                    actualflag = XAResource.TMJOIN;
                }
            }
        }

        xar.start(savedXid(), actualflag);
         */
    }

    public Object getWrappedObject() {
        return this.xar;
    }

    void setToRollback(boolean flag) {
        toRollback = flag;
    }

    boolean endCalled() {
        return endCalled;
    }

    /*
    private Xid savedXid() {
        return this.savedxid;
    }
     */

    
    public void startDelayedXA()
    {
        /**
         * This is done to cover the redelivery feature.
         * We make sure that start on an XA is called only when the message
         * delivery to the endpoint is successful. 
         * If not there will be new transactions started by TM for every
         * delivery attempt, and the same cannot be mnanaged with the broker's RM. 
         */
        try
        {
            debug("Delayed start of XID " + printXid(startXid));
            xar.start(startXid, startflags);            
        }
        catch (XAException xae)
        {
            // What can we do with this ?
            // Only make sure that end fails.
            xae.printStackTrace();
        }
        catch (Exception  e)
        {
            e.printStackTrace();
        }
        startedDelayedXA = true;
    }
    
    void debug(String s) {
        logger.log(Level.FINEST, "InboundXAResourceProxy : " + s);
    }    
    /**
     * The following is a method that has been added to aid printing of XID.
     * This method of printing the XID may differ for different providers.
     */
    private String printXid(Xid xid) {
        if (xid != null) {
            return xid.toString();
        }
        else {
            return "null";
        }
        
    /* The following code can be enabled for TM implementations 
     * that do not have a toString implementation for xids
     */
        /*
        String hextab = "0123456789ABCDEF";
        StringBuffer data = new StringBuffer(256);
        int i;
        int value;

        if (xid == null) {
            return "null";
        }

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

        return new String(data);
         */
    }
}
