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

import com.sun.genericra.util.Constants;
import com.sun.genericra.util.LogUtils;
import com.sun.genericra.util.StringUtils;

import java.io.Serializable;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterAssociation;


/**
 * ResourceAdapter, AdminObject, ManagedConnectionFactory and ActivationSpec
 * extend this class. This class contains properties common to all javabeans.
 *
 * @author Sivakumar Thyagarajan, Binod P.G
 */
public class GenericJMSRAProperties implements ResourceAdapterAssociation,
    Serializable {
    /**
     * Logger object. 
     */
    private static Logger logger;

    static {
        logger = LogUtils.getLogger();
    }

    /**
     *  String describing provider managed from activation spec.
     */
    public static final String PROVIDER_MANAGED = "ProviderManaged";

    /**     
     *  String describing one per physical connection from activation spec.   
     */
    public static final String ONE_PER_PHYSICALCONNECTION = "OnePerPhysicalConnection";

    /**
     * One of the modes by which the jms administered objects can be accessed. 
     */
    private String providerIntegrationMode = null;

    /**
     * Jndi properties for accessing the administered objects.
     */
    private String jndiProperties;

    /**
     * Flag which denotes the XA support.
     */
    private Boolean supportsXA = null;

    /**
     * Resource Manager policy.
     */
    private String rmPolicy = null;

    //MoM specific constants.

    /**
     * Queue Connection Factory implementation class name of the JMS provider.   
     */
    private String queueCFClassName;

    /**
     * Connection Factory implementation class name of JMS provider.
     */
    private String cfClassName;

    /**
     *  Topic Connection factory implementation class name of JMS provider.  
     */
    private String topicCFClassName;

    /**
     * XA Queue connection factory implementation class name of JMS provider.
     */
    private String xAQueueConnectionFactoryClassName;

    /**
     * XA topic connection factory implementation class name of JMS provider.
     */
    private String xATopicConnectionFactoryClassName;

    /**
     *  XA connection factory implementation class name of JMS provider.
     */
    private String xAConnectionFactoryClassName;

    /**
     *  Destination class name.
     */
    private String destinationClassName;

    /**
     *  Queue destination implementation class of provider.
     */
    private String queueClassName;

    /**
     *    Topic destination class name of provider.
     */
    private String topicClassName;

    //Connection defaults

    /**
     * URL connection string.
     */
    private String connectionURL;

    /**
     * Connection user name.
     */
    private String userName;

    /**
     * Connection password.
     */
    private String password;

    /**
     * Setter method name.   
     */
    private String setterMethodName;

    /**
     * Connection factory properties (Activation spec).
     */
    private String cfProperties;

    /**
     *  This instance.
     */
    private GenericJMSRAProperties raprops;
    
    private boolean enableMonitoring = false;
        
    private String deliveryType;
    
    private String deliveryConcurrencyMode;
    /**
     * Sets the connection factory class name.
     *
     * @param className  class name.
     */
    public void setConnectionFactoryClassName(String className) {
        logger.log(Level.FINEST, "setConnectionFactoryClassName :" + className);
        this.cfClassName = className;
    }

    /**
     * Gets the connection factory class name.
     *
     * @return  connection factory class name.
     */
    public String getConnectionFactoryClassName() {
        if (this.cfClassName != null) {
            return this.cfClassName;
        } else if (raprops != null) {
            return raprops.cfClassName;
        } else {
            return null;
        }
    }

    public String getDeliveryConcurrencyMode() {
        return deliveryConcurrencyMode;
    }
    
    public void setDeliveryConcurrencyMode(String mode) {
        deliveryConcurrencyMode = mode;
    }
    
    public String getDeliveryType() {
        if (this.deliveryType != null) {
            return this.deliveryType;
        } else if (raprops != null) {
            return raprops.deliveryType;
        } else {
            return null;
        }
    }
    
    public void setDeliveryType(String delivery) {
        logger.log(Level.FINEST,
            "setDeliveryType :" + delivery);
        deliveryType = delivery;
    }
    /**
     * Sets the queue connection factory class name.
     *
     * @param className  qcf class name.
     */
    public void setQueueConnectionFactoryClassName(String className) {
        logger.log(Level.FINEST,
            "setQueueConnectionFactoryClassName :" + className);
        this.queueCFClassName = className;
    }

    /**
     * Gets the QCF class name.
     *
     * @return  QCF class name.
     */
    public String getQueueConnectionFactoryClassName() {
        if (this.queueCFClassName != null) {
            return this.queueCFClassName;
        } else if (raprops != null) {
            return raprops.queueCFClassName;
        } else {
            return null;
        }
    }

    /**
     * Sets the topic connection factory class name.
     *
     * @param className topic cf class name.
     */
    public void setTopicConnectionFactoryClassName(String className) {
        logger.log(Level.FINEST,
            "setTopicConnectionFactoryClassName: " + className);
        this.topicCFClassName = className;
    }

    /**
     * Gets the topic connection factory class name.
     *
     * @return topic cf class name.
     */
    public String getTopicConnectionFactoryClassName() {
        if (this.topicCFClassName != null) {
            return this.topicCFClassName;
        } else if (raprops != null) {
            return raprops.topicCFClassName;
        } else {
            return null;
        }
    }

    /**
     * Sets the topic CF class name.
     *
     * @param className    topic cf class name.
     */
    public void setTopicClassName(String className) {
        logger.log(Level.FINEST, "setTopicClassName :" + className);
        this.topicClassName = className;
    }

    /**
     * Gets the topic CF class name.
     *
     * @return topic CF class name.
     */
    public String getTopicClassName() {
        if (this.topicClassName != null) {
            return this.topicClassName;
        } else if (raprops != null) {
            return raprops.topicClassName;
        } else {
            return null;
        }
    }

    /**
     * Sets the destination (Q or Topic) class name.
     *
     * @param className  Q or topic impl class.
     */
    public void setUnifiedDestinationClassName(String className) {
        logger.log(Level.FINEST, "setUnifiedDestinationClassName :" +
            className);
        this.destinationClassName = className;
    }

    /**
     * Gets the (Q or topic) impl class name.
     *
     * @return    class name.
     */
    public String getUnifiedDestinationClassName() {
        if (this.destinationClassName != null) {
            return this.destinationClassName;
        } else if (raprops != null) {
            return raprops.destinationClassName;
        } else {
            return null;
        }
    }

    /**
     * Sets the Queue impl class name.
     *
     * @param className queue impl class name.
     */
    public void setQueueClassName(String className) {
        logger.log(Level.FINEST, "setQueueClassName :" + className);
        this.queueClassName = className;
    }

    /**
     * Gets the queue impl class name.
     *
     * @return  queue impl class.
     */
    public String getQueueClassName() {
        if (this.queueClassName != null) {
            return this.queueClassName;
        } else if (raprops != null) {
            return raprops.queueClassName;
        } else {
            return null;
        }
    }

    /**
     * Sets the provider integration mode as JNDI or JavaBean.
     *
     * @param mode  JNDI or JavaBean.
     */
    public void setProviderIntegrationMode(String mode) {
        logger.log(Level.FINEST, "setProviderIntegrationMode :" + mode);
        this.providerIntegrationMode = mode;
    }

    /**
     * Gets the provider integration mode.
     *
     * @return integration mode of RA with JMS provider.
     */
    public String getProviderIntegrationMode() {
        logger.log(Level.FINEST,
            "ProviderIntegrationMode " + this.providerIntegrationMode);

        if (this.providerIntegrationMode != null) {
            return this.providerIntegrationMode;
        } else if (raprops != null) {
            return raprops.providerIntegrationMode;
        } else {
            return null;
        }
    }

    /**
     * Sets the Resource Manager policy.
     *
     * @param policy   policy.
     */
    public void setRMPolicy(String policy) {
        logger.log(Level.FINEST, "setRMPolicy :" + policy);
        this.rmPolicy = policy;
    }

    /**
     * Gets the RM policy for the provider.
     *
     * @return pplicy string , "OnePerPhysicalConnection" or "ProviderManager".
     */
    public String getRMPolicy() {
        logger.log(Level.FINEST, "RMPolicy :" + this.rmPolicy);

        if (this.rmPolicy != null) {
            return this.rmPolicy;
        } else if (raprops != null) {
            return raprops.rmPolicy;
        } else {
            return null;
        }
    }

    /**
     * Sets the XA supported flag.
     *
     * @param supportsXA true for supported.
     */
    public void setSupportsXA(boolean supportsXA) {
        logger.log(Level.FINEST, "setSupportsXA :" + supportsXA);
        this.supportsXA = Boolean.valueOf(supportsXA);
    }

    /**
     * Gets the XAsupport as configure during RA configuration.
     *
     * @return  true if supported.
     */
    public boolean getSupportsXA() {
        if (this.supportsXA != null) {
            return this.supportsXA.booleanValue();
        } else if ((raprops != null) && (raprops.supportsXA != null)) {
            return raprops.supportsXA.booleanValue();
        } else {
            return false;
        }
    }

    /**
     * Sets the connection factory properties.
     *
     * @param props  CF props.
     */
    public void setConnectionFactoryProperties(String props) {
        logger.log(Level.FINEST, "setConnectionFactoryProperties :" + props);
        this.cfProperties = props;
    }

    /**
     * Gets the connection factory properties.
     *
     * @return CF properties.
     */
    public String getConnectionFactoryProperties() {
        if (this.cfProperties != null) {
            return this.cfProperties;
        } else if (raprops != null) {
            return raprops.cfProperties;
        } else {
            return null;
        }
    }

    /**
     * Sets the JNDI properties for "JNDI" integration mode.
     *
     * @param props  properties separated by ":".
     */
    public void setJndiProperties(String props) {
        logger.log(Level.FINEST, "setJndiProperties :" + props);
        this.jndiProperties = props;
    }

    /**
     * Gets the JNDI propeties.
     *
     * @return  Properties separated by ":".
     */
    public String getJndiProperties() {
        if (this.jndiProperties != null) {
            return this.jndiProperties;
        } else if (raprops != null) {
            return raprops.jndiProperties;
        } else {
            return null;
        }
    }

    
    /**
     *  Sets the common setter method.
     *
     * @param methodName    
     */
    public void setCommonSetterMethodName(String methodName) {
        logger.log(Level.FINEST, "setCommonSetterMethodName :" + methodName);
        this.setterMethodName = methodName;
    }

    /**
     * Gets the common setter method.
     *
     * @return  setter method name.
     */
    public String getCommonSetterMethodName() {
        if (this.setterMethodName != null) {
            return this.setterMethodName;
        } else if (raprops != null) {
            return raprops.setterMethodName;
        } else {
            return null;
        }
    }

    /**
     * Gets the user name.
     *
     * @return    user name for creating connection.
     */
    public String getUserName() {
        if (this.userName != null) {
            return this.userName;
        } else if (raprops != null) {
            return raprops.userName;
        } else {
            return null;
        }
    }

    /**
     * Sets the user name.
     *
     * @param userName for connection authentication.
     */
    public void setUserName(String userName) {
        logger.log(Level.FINEST, "setUserName :" + userName);
        this.userName = userName;
    }

    /**
     * Gets the password.
     * 
     * @return password for authentication.
     */
    public String getPassword() {
        if (this.password != null) {
            return this.password;
        } else if (raprops != null) {
            return raprops.password;
        } else {
            return null;
        }
    }

    /**
     * Sets the password.
     *
     * @param password for authentication. 
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets the resource adapter.
     *
     * @param adapter resource adapter object.
     */
    public void setResourceAdapter(ResourceAdapter adapter) {
        logger.log(Level.FINEST, "setResourceAdapter " + adapter);
        this.raprops = (GenericJMSRAProperties) adapter;
    }

    /**
     * Gets the RA.
     *
     * @return  RA.
     */
    public ResourceAdapter getResourceAdapter() {
        return (ResourceAdapter) this.raprops;
    }

    /**
     * Gets the XA CF class name for JavaBean integration mode.
     *
     * @return  XACF class name.
     */
    public String getXAConnectionFactoryClassName() {
        if (this.xAConnectionFactoryClassName != null) {
            return this.xAConnectionFactoryClassName;
        } else if (raprops != null) {
            return raprops.xAConnectionFactoryClassName;
        } else {
            return null;
        }
    }

    /**
     * Sets the XA CF class name.
     *
     * @param connectionFactoryClassName    
     */
    public void setXAConnectionFactoryClassName(
        String connectionFactoryClassName) {
        logger.log(Level.FINEST,
            "setXAConnectionFactoryClassname " + connectionFactoryClassName);
        xAConnectionFactoryClassName = connectionFactoryClassName;
    }

    /**
     * Gets the XA QCF class name.
     * 
     * @return    XAQCF class name.
     */
    public String getXAQueueConnectionFactoryClassName() {
        if (this.xAQueueConnectionFactoryClassName != null) {
            return this.xAQueueConnectionFactoryClassName;
        } else if (raprops != null) {
            return raprops.xAQueueConnectionFactoryClassName;
        } else {
            return null;
        }
    }

    /**
     * Sets the XAQCF class name.
     *
     * @param queueConnectionFactoryClassName  class name.
     */
    public void setXAQueueConnectionFactoryClassName(
        String queueConnectionFactoryClassName) {
        logger.log(Level.FINEST,
            "setXAQueueConnectionFactoryClassname " +
            queueConnectionFactoryClassName);
        xAQueueConnectionFactoryClassName = queueConnectionFactoryClassName;
    }

    /**
     * Gets the XA topic CF class name.
     * 
     * @return XATopicCF class name of provider.
     */
    public String getXATopicConnectionFactoryClassName() {
        if (this.xATopicConnectionFactoryClassName != null) {
            return this.xATopicConnectionFactoryClassName;
        } else if (raprops != null) {
            return raprops.xATopicConnectionFactoryClassName;
        } else {
            return null;
        }
    }

    /**
     * Sets tje XA Topic CF class name.
     *
     * @param topicConnectionFactoryClassName    
     */
    public void setXATopicConnectionFactoryClassName(
        String topicConnectionFactoryClassName) {
        logger.log(Level.FINEST,
            "setXATopicConnectionFactoryClassname " +
            topicConnectionFactoryClassName);
        xATopicConnectionFactoryClassName = topicConnectionFactoryClassName;
    } 
    
    public void setMonitoring(boolean monitor) {
        enableMonitoring = monitor;
    }
    
    public boolean getMonitoring() {
        return enableMonitoring;
    }
    
    /**
     * Overides the equals method of object.
     *
     * @param o another object.
     *
     * @return true if this object is both objetcs are same.
     */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof GenericJMSRAProperties)) {
            return false;
        }

        GenericJMSRAProperties other = (GenericJMSRAProperties) o;

        return ((other.getSupportsXA() == this.getSupportsXA()) &&
        StringUtils.isEqual(other.getCommonSetterMethodName(),
            this.getCommonSetterMethodName()) &&
        StringUtils.isEqual(other.getConnectionFactoryProperties(),
            this.getConnectionFactoryProperties()) &&
        StringUtils.isEqual(other.getJndiProperties(), this.getJndiProperties()) &&
        StringUtils.isEqual(other.getPassword(), this.getPassword()) &&
        StringUtils.isEqual(other.getProviderIntegrationMode(),
            this.getProviderIntegrationMode()) &&
        StringUtils.isEqual(other.getQueueClassName(), this.getQueueClassName()) &&
        StringUtils.isEqual(other.getQueueConnectionFactoryClassName(),
            this.getQueueConnectionFactoryClassName()));
    }

    /**
     * Hash code for this object.
     *
     * @return  hash code.
     */
    public int hashCode() {
        //XXX: build a better hashcode
        return ("" + this.cfClassName + this.cfProperties + this.connectionURL +
        this.jndiProperties + this.password + this.providerIntegrationMode +
        this.queueCFClassName + this.queueClassName + this.setterMethodName +
        this.topicCFClassName + this.topicClassName + this.userName +
        this.xAConnectionFactoryClassName +
        this.xAQueueConnectionFactoryClassName +
        this.xATopicConnectionFactoryClassName).hashCode();
    }

    /**
     * String representation of this object.
     *
     * @return string.
     */
    public String toString() {
        String s = super.toString();
        s = s + "{ConnectionFactoryClassName = " +
            getConnectionFactoryClassName() + "},";
        s = s + "{QueueConnectionFactoryClassName = " +
            getQueueConnectionFactoryClassName() + "},";
        s = s + "{TopicConnectionFactoryClassName = " +
            getTopicConnectionFactoryClassName() + "},";
        s = s + "{XAConnectionFactoryClassName = " +
            getXAConnectionFactoryClassName() + "},";
        s = s + "{XAQueueConnectionFactoryClassName = " +
            getXAQueueConnectionFactoryClassName() + "},";
        s = s + "{XATopicConnectionFactoryClassName = " +
            getXATopicConnectionFactoryClassName() + "},";

        s = s + "{QueueClassName = " + getQueueClassName() + "},";
        s = s + "{TopicClassName = " + getTopicClassName() + "},";
        s = s + "{UnifiedDestinationClassName = " +
            getUnifiedDestinationClassName() + "},";

        s = s + "{ConnectionFactoryProperties = " +
            getConnectionFactoryProperties() + "},";
        s = s + "{JndiProperties = " + getJndiProperties() + "},";
        s = s + "{ProviderIntegrationMode = " + getProviderIntegrationMode() +
            "},";
        s = s + "{CommonSetterMethodName = " + getCommonSetterMethodName() +
            "},";
        s = s + "{SupportsXA = " + getSupportsXA() + "},";
        s = s + "{DeliveryType = " + getDeliveryType() + "},";
        return s;
    }

}
