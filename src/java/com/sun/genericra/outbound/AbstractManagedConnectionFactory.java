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

import com.sun.genericra.GenericJMSRAProperties;
import com.sun.genericra.util.*;

import java.io.PrintWriter;

import java.util.Set;
import java.util.logging.*;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.QueueConnectionFactory;
import javax.jms.TopicConnectionFactory;
import javax.jms.XAConnectionFactory;
import javax.jms.XAQueueConnectionFactory;
import javax.jms.XATopicConnectionFactory;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterAssociation;
import javax.resource.spi.security.PasswordCredential;

import javax.security.auth.Subject;


/**
 * <code>ManagedConnectionFactory</code> implementation of the Generic
 * JMS resource adapter and is a factory of both <code>ManagedConnection</code> and
 * JMS-specific <code>ConnectionFactory</code> instances.
 *
 * @author Sivakumar Thyagarajan
 */
public abstract class AbstractManagedConnectionFactory
    extends GenericJMSRAProperties
    implements javax.resource.spi.ManagedConnectionFactory,
        ResourceAdapterAssociation {
    //by default, run as non-ACC. Use System Property or MCF property to enable
    private static boolean inAppClientContainer = false;

    //MCF state
    private static final String INACC_SYSTEM_PROP_KEY = "genericra.inAppClientContainer";
    private static Logger logger;

    static {
        logger = LogUtils.getLogger();
    }

    //Use system property to determine ACC status. 
    static {
        String s = System.getProperty(INACC_SYSTEM_PROP_KEY);

        if (s != null) {
            inAppClientContainer = (Boolean.valueOf(s)).booleanValue();
        }
    }

    //MCF-specific configurable properties
    private String connectionFactoryJndiName;
    private String clientId = null;
    private boolean connectionValidationEnabled = false; //disabled by default
    private boolean useProxyMessages = false; //disabled by default
    private PrintWriter logWriter;
    private ConnectionFactory connectionFactory = null;
    protected int destinationMode = Constants.UNIFIED_SESSION;

    public AbstractManagedConnectionFactory() {
    }

    public PrintWriter getLogWriter() throws ResourceException {
        return logWriter;
    }

    public void setLogWriter(PrintWriter pw) throws ResourceException {
        this.logWriter = pw;
    }

    public Object createConnectionFactory() throws ResourceException {
        //instantiate connection factory with RA's default simple ConnectionManager
        ConnectionManager cm = new com.sun.genericra.outbound.ConnectionManager();

        return new com.sun.genericra.outbound.ConnectionFactory(this, cm);
    }

    public Object createConnectionFactory(ConnectionManager cm)
        throws ResourceException {
        return new com.sun.genericra.outbound.ConnectionFactory(this, cm);
    }

    /*
     * @see javax.resource.spi.ManagedConnectionFactory#createManagedConnection(javax.security.auth.Subject, javax.resource.spi.ConnectionRequestInfo)
     */
    public ManagedConnection createManagedConnection(Subject subject,
        ConnectionRequestInfo cri) throws ResourceException {
        //Create or lookup JMS connection factory if not already done
        //Create a connection from the JMS CF and create a ManagedConnection
        //from the created connection
        try {
            initializeConnectionFactory();

            PasswordCredential pc = SecurityUtils.getPasswordCredential(this,
                    subject, cri);
            javax.jms.Connection physicalCon = createPhysicalConnection(pc);

            return new com.sun.genericra.outbound.ManagedConnection(this, pc,
                (com.sun.genericra.outbound.ConnectionRequestInfo) cri,
                physicalCon);
        } catch (ResourceException e) {
            throw ExceptionUtils.newResourceException(e);
        } catch (JMSException e) {
            throw ExceptionUtils.newResourceException(e);
        }
    }

    private javax.jms.Connection createPhysicalConnection(PasswordCredential pc)
        throws JMSException {
        javax.jms.Connection physicalCon = null;

        if (this.getSupportsXA()) {
            physicalCon = createXAConnection(pc, this.connectionFactory);
        } else {
            physicalCon = createConnection(pc, this.connectionFactory);
        }

        return physicalCon;
    }

    protected abstract javax.jms.XAConnection createXAConnection(
        PasswordCredential pc, ConnectionFactory cf) throws JMSException;

    protected abstract javax.jms.Connection createConnection(
        PasswordCredential pc, ConnectionFactory cf) throws JMSException;

    /**
     * Overridden by MCF for TCF, QCF and JMS CF to return their appropriate
     * CF class names, so that the relevant JavaBean class can be used
     * by the builder.
     *
     * @return String ClassName of the MoM-specific ConnectionFactory to be
     *                 created by the MCF.
     */
    protected abstract String getActualConnectionFactoryClassName();

    private void initializeConnectionFactory() throws ResourceException {
        if (this.connectionFactory == null) {
            ObjectBuilder cfBuilder = null;
            ObjectBuilderFactory obf = new ObjectBuilderFactory();

            if (this.getProviderIntegrationMode().equalsIgnoreCase(Constants.JNDI_BASED)) {
                cfBuilder = obf.createUsingJndiName(this.getConnectionFactoryJndiName(),
                        this.getJndiProperties());
            } else {
                cfBuilder = obf.createUsingClassName(getActualConnectionFactoryClassName());
                cfBuilder.setProperties(this.getConnectionFactoryProperties());
            }

            String setMethod = this.getCommonSetterMethodName();

            if (!StringUtils.isNull(setMethod)) {
                cfBuilder.setCommonSetterMethodName(setMethod);
            }

            this.connectionFactory = (ConnectionFactory) cfBuilder.build();
        }
    }

    /*
     * @see javax.resource.spi.ManagedConnectionFactory#matchManagedConnections(java.util.Set, javax.security.auth.Subject, javax.resource.spi.ConnectionRequestInfo)
     */
    public ManagedConnection matchManagedConnections(Set connectionSet,
        Subject subject, ConnectionRequestInfo cxRequestInfo)
        throws ResourceException {
        if (connectionSet == null) {
            return null;
        }

        PasswordCredential pc = SecurityUtils.getPasswordCredential(this,
                subject, cxRequestInfo);

        java.util.Iterator iter = connectionSet.iterator();
        com.sun.genericra.outbound.ManagedConnection mc = null;

        while (iter.hasNext()) {
            try {
                mc = (com.sun.genericra.outbound.ManagedConnection) iter.next();
                debug("Matching managed connections ->" + mc);
            } catch (java.util.NoSuchElementException nsee) {
                throw ExceptionUtils.newResourceException(nsee);
            }

            if ((pc == null) && this.equals(mc.getManagedConnectionFactory())) {
                if (!mc.isDestroyed()) {
                    return mc;
                }
            } else if (SecurityUtils.isPasswordCredentialEqual(pc,
                        mc.getPasswordCredential()) == true) {
                if (!mc.isDestroyed()) {
                    return mc;
                }
            }
        }

        return null;
    }

    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * @return Returns the connectionFactoryJndiName.
     */
    public String getConnectionFactoryJndiName() {
        return connectionFactoryJndiName;
    }

    /**
     * @param connectionFactoryJndiName The connectionFactoryJndiName to set.
     */
    public void setConnectionFactoryJndiName(String connectionFactoryJndiName) {
        this.connectionFactoryJndiName = connectionFactoryJndiName;
    }

    /**
     * @return Returns the inAppClientContainer.
     */
    public boolean isInAppClientContainer() {
        return inAppClientContainer;
    }

    /**
     * @return Whether to use a Proxy object to wrap javax.jms.Message objects.
     */
    public boolean getUseProxyMessages() {
        return useProxyMessages;
    }

    /**
     * @param flag Indicating whether to use a Proxy object to wrap
     *             javax.jms.Message objects.
     */
    public void setUseProxyMessages(boolean flag) {
        this.useProxyMessages = flag;
    }

    /**
     * @return Returns the enableValidation.
     */
    public boolean getConnectionValidationEnabled() {
        return this.connectionValidationEnabled;
    }

    /**
     * @param enableValidation The enableValidation to set.
     */
    public void setConnectionValidationEnabled(
        boolean connectionValidationEnabled) {
        this.connectionValidationEnabled = connectionValidationEnabled;
    }

    public int hashCode() {
        //XXX: enhance
        return super.hashCode();
    }

    public boolean equals(Object obj) {
        //debug("equals" + obj);
        //XXX: enhance
        if (obj == null) {
            return false;
        }
        if (!(super.equals(obj))) {
            return false;
        }

        if (!(obj instanceof AbstractManagedConnectionFactory)) {
            return false;
        }

        debug("equals - no false yet");

        AbstractManagedConnectionFactory other = (AbstractManagedConnectionFactory) obj;
        boolean eq = (StringUtils.isEqual(this.clientId, other.clientId) &&
            StringUtils.isEqual(this.connectionFactoryJndiName,
                other.connectionFactoryJndiName) &&
            (this.destinationMode == other.destinationMode));
        debug(" equals - final: " + eq);

        return eq;
    }

    public int getDestinationMode() {
        return this.destinationMode;
    }

    private void debug(String s) {
        logger.log(Level.FINEST, "[AbstractMCF] " + s);
    }
}
