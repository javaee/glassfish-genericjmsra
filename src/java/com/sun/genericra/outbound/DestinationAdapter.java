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

import com.sun.genericra.GenericJMSRA;
import com.sun.genericra.GenericJMSRAProperties;
import com.sun.genericra.util.*;
import com.sun.genericra.util.Constants;
import com.sun.genericra.util.ObjectBuilder;
import com.sun.genericra.util.ObjectBuilderFactory;
import com.sun.genericra.util.StringUtils;

import java.util.logging.*;

import javax.jms.Destination;
import javax.jms.JMSException;

import javax.print.attribute.SupportedValuesAttribute;

import javax.resource.ResourceException;


/**
 * Following restrictions apply, as of now on AdminObjects.
 *
 * - Setting destination property is mandatory, unless we find a
 * way out. The actual Message provider Destination JavaBean is created
 * only when AS sets DestinationProperty
 * - QueueClassName and TopicClassName if overridden at admin-object level
 * would result in re-creation of physicalDestinationJavaBean if already created.
 * [Connectors Spec neither mandates order of mutator calls nor provides an
 * explicit mechanism to indicate that all overridden properties have been set,
 * to enable us to create the physicalDestination JavaBean then.]
 *
 * @author Sivakumar Thyagarajan
 */
public abstract class DestinationAdapter extends GenericJMSRAProperties
    implements javax.jms.Destination {
    private static Logger logger;

    static {
        logger = LogUtils.getLogger();
    }

    private String destinationProperties = null;
    private String jndiName = null;
    private javax.jms.Destination physicalDestination;

    public DestinationAdapter() {
        debug("Destination adapter is created");
    }

    public javax.jms.Destination _getPhysicalDestination()
        throws JMSException {
        logger.log(Level.FINEST,
            "Returning physical destintion " + this.physicalDestination);

        if (this.physicalDestination == null) {
            try {
                initialize();
            } catch (ResourceException re) {
                throw ExceptionUtils.newJMSException(re);
            }
        }

        if (logger.isLoggable(Level.FINEST)) {
            if (this.physicalDestination != null) {
                logger.log(Level.FINEST,
                    "Physical destintion type" +
                    this.physicalDestination.getClass().getName());
            }
        }

        return this.physicalDestination;
    }

    private void initialize() throws ResourceException {
        /**
         * Connector 1.5 Specification does not support ResourceAdapterAssociation
         * for Administered Objects. This call is made so that the current
         * active resource adapter instance can be set in
         * <code>GenericJMSRAProperties</code> so that all properties
         * already configured for the resource adapter can be reused in this
         * Destination instance (for example ProviderIntegrationMode)
         */
        this.setResourceAdapter(GenericJMSRA.getInstance());

        ObjectBuilder destBuilder = null;
        ObjectBuilderFactory obf = new ObjectBuilderFactory();

        if (this.getProviderIntegrationMode().equalsIgnoreCase(Constants.JNDI_BASED)) {
            debug("Creating destination using jndiName " + jndiName);
            destBuilder = obf.createUsingJndiName(this.getDestinationJndiName(),
                    this.getJndiProperties());
        } else {
            destBuilder = obf.createUsingClassName(this.getDestinationClassName());
            destBuilder.setProperties(this.getDestinationProperties());
        }

        String setMethod = this.getCommonSetterMethodName();

        if (!StringUtils.isNull(setMethod)) {
            destBuilder.setCommonSetterMethodName(setMethod);
        }

        this.physicalDestination = (javax.jms.Destination) destBuilder.build();
    }

    protected abstract String getDestinationClassName();

    public void setDestinationJndiName(String jndiName) {
        debug("Destination Jndi Name is set as " + jndiName);
        this.jndiName = jndiName;
    }

    public String getDestinationJndiName() {
        return this.jndiName;
    }

    /**
     * @return Returns the destinationProperties.
     */
    public String getDestinationProperties() {
        return destinationProperties;
    }

    /**
     * Setting destination property is mandatory, unless we find a
     * way out.
     * @param destinationProperties The destinationProperties to set.
     */
    public void setDestinationProperties(String destinationProperties) {
        debug("Setting destination properties : " + destinationProperties);

        if (!StringUtils.isNull(destinationProperties)) {
            this.destinationProperties = destinationProperties;

            try {
                //Call initialized here.
                initialize();
            } catch (ResourceException e) {
                debug("Cannot create Destination " + e);
            }
        }
    }

    private static void debug(String string) {
        logger.log(Level.FINEST, "[Destination]" + string);
    }

    public String toString() {
        if (this.physicalDestination != null) {
            return this.physicalDestination.toString();
        } else {
            return super.toString();
        }
    }
}
