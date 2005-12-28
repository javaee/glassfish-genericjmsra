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

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterAssociation;

import com.sun.genericra.util.Constants;
import com.sun.genericra.util.LogUtils;
import com.sun.genericra.util.StringUtils;


/**
 * ResourceAdapter, AdminObject, ManagedConnectionFactory and ActivationSpec
 * extend this class. This class contains properties common to all javabeans.
 *
 * @author Sivakumar Thyagarajan, Binod P.G
 */
public class GenericJMSRAProperties implements ResourceAdapterAssociation, Serializable {

    private static Logger logger;
    
    static {
        logger = LogUtils.getLogger();
    }

    public static final String PROVIDER_MANAGED 
                             ="ProviderManaged";

    public static final String ONE_PER_PHYSICALCONNECTION 
                             ="OnePerPhysicalConnection";

    private String providerIntegrationMode = null; 
    
    private String jndiProperties; 
    private Boolean supportsXA = null; 

    private String rmPolicy  = null; 

    //MoM specific constants.
    private String queueCFClassName;
    private String CFClassName;
    private String topicCFClassName;
    private String cfClassName;
    
    private String xAQueueConnectionFactoryClassName;
    private String xATopicConnectionFactoryClassName;
    private String xAConnectionFactoryClassName;
    
    private String destinationClassName;
    private String queueClassName;
    private String topicClassName;

    //Connection defaults
    private String connectionURL;
    private String userName;
    private String password;
        
    private String setterMethodName;
    private String cfProperties;
    private GenericJMSRAProperties raprops;

    public void setConnectionFactoryClassName(String className) {
        logger.log(Level.FINEST, "setConnectionFactoryClassName :" + className);
        this.cfClassName = className;
    }

    public String getConnectionFactoryClassName() {
        if (this.cfClassName != null) {
            return this.cfClassName;
        } else if (raprops != null) {
            return raprops.cfClassName;
        } else {
            return null;
        }
    }

    public void setQueueConnectionFactoryClassName(String className) {
        logger.log(Level.FINEST, "setQueueConnectionFactoryClassName :" + className);
        this.queueCFClassName = className;
    }

    public String getQueueConnectionFactoryClassName() {
        if (this.queueCFClassName != null) {
            return this.queueCFClassName;
        } else if (raprops != null) {
            return raprops.queueCFClassName;
        } else {
            return null;
        }
    }
    
    public void setTopicConnectionFactoryClassName(String className) {
        logger.log(Level.FINEST, "setTopicConnectionFactoryClassName: " + className);
        this.topicCFClassName = className;
    }

    public String getTopicConnectionFactoryClassName() {
        if (this.topicCFClassName != null) {
            return this.topicCFClassName;
        } else if (raprops != null) {
            return raprops.topicCFClassName;
        } else {
            return null;
        }
    }
    
    public void setTopicClassName(String className) {
        logger.log(Level.FINEST, "setTopicClassName :" + className);
        this.topicClassName = className;
    }

    public String getTopicClassName() {
        if (this.topicClassName != null) {
            return this.topicClassName;
        } else if (raprops != null) {
            return raprops.topicClassName;
        } else {
            return null;
        }
    }
    
    public void setUnifiedDestinationClassName(String className) {
        logger.log(Level.FINEST, "setUnifiedDestinationClassName :" + className);
        this.destinationClassName = className;
    }

    public String getUnifiedDestinationClassName() {
        if (this.destinationClassName != null) {
            return this.destinationClassName;
        } else if (raprops != null) {
            return raprops.destinationClassName;
        } else {
            return null;
        }
    }
    
    public void setQueueClassName(String className) {
        logger.log(Level.FINEST, "setQueueClassName :" + className);
        this.queueClassName = className;
    }

    public String getQueueClassName() {
        if (this.queueClassName != null) {
            return this.queueClassName;
        } else if (raprops != null) {
            return raprops.queueClassName;
        } else {
            return null;
        }
    }
    
    public void setProviderIntegrationMode(String mode) {
        logger.log(Level.FINEST, "setProviderIntegrationMode :" + mode);
        this.providerIntegrationMode = mode;
    }

    public String getProviderIntegrationMode() {
        logger.log(Level.FINEST, "ProviderIntegrationMode " + this.providerIntegrationMode );
        if (this.providerIntegrationMode != null) {
            return this.providerIntegrationMode;
        } else if (raprops != null) {
            return raprops.providerIntegrationMode;
        } else {
            return null;
        }
    }

    public void setRMPolicy(String policy) {
        logger.log(Level.FINEST, "setRMPolicy :" + policy);
        this.rmPolicy = policy;
    }
    
    public String getRMPolicy() {
        logger.log(Level.FINEST, "RMPolicy :" + this.rmPolicy );
        if (this.rmPolicy != null) {
            return this.rmPolicy;
        } else if (raprops != null) {
            return raprops.rmPolicy;
        } else {
            return null;
        }
    }

    public void setSupportsXA(boolean supportsXA) {
        logger.log(Level.FINEST, "setSupportsXA :" + supportsXA);
        this.supportsXA = new Boolean(supportsXA);
    } 

    public boolean getSupportsXA() {
        if (this.supportsXA != null) {
            return this.supportsXA.booleanValue();
        } else if (raprops != null) {
            return raprops.supportsXA.booleanValue();
        } else {
            return false;
        }
    }

    public void setConnectionFactoryProperties(String props) {
        logger.log(Level.FINEST, "setConnectionFactoryProperties :" + props);
        this.cfProperties = props;
    }

    public String getConnectionFactoryProperties() {
        if (this.cfProperties != null) {
            return this.cfProperties;
        } else if (raprops != null) {
            return raprops.cfProperties;
        } else {
            return null;
        }
    }

    public void setJndiProperties(String props) {
        logger.log(Level.FINEST, "setJndiProperties :" + props);
        this.jndiProperties = props;
    }

    public String getJndiProperties() {
        if (this.jndiProperties != null) {
            return this.jndiProperties;
        } else if (raprops != null) {
            return raprops.jndiProperties;
        } else {
            return null;
        }
    }

    public void setCommonSetterMethodName(String methodName) {
        logger.log(Level.FINEST, "setCommonSetterMethodName :" + methodName);
        this.setterMethodName = methodName;
    }

    public String getCommonSetterMethodName() {
        if (this.setterMethodName != null) {
            return this.setterMethodName;
        } else if (raprops != null) {
            return raprops.setterMethodName;
        } else {
            return null;
        }
    }

    public String getUserName(){
        if (this.userName != null) {
            return this.userName;
        } else if (raprops != null) {
            return raprops.userName;
        } else {
            return null;
        }
    }

    public void setUserName(String userName) {
        logger.log(Level.FINEST, "setUserName :" + userName);
        this.userName = userName;
    }

    
                                                                                                                                              
    public String getPassword(){
        if (this.password != null) {
            return this.password;
        } else if (raprops != null) {
            return raprops.password;
        } else {
            return null;
        }
    }
                                                                                                                                              
    public void setPassword(String password) {
        logger.log(Level.FINEST, "setPassword :" + password);
        this.password = password;
    }

    public void setResourceAdapter(ResourceAdapter adapter) {
        logger.log(Level.FINEST, "setResourceAdapter " + adapter);
        this.raprops = (GenericJMSRAProperties) adapter;
    }
                                                         
    public ResourceAdapter getResourceAdapter() {
        return (ResourceAdapter) this.raprops;
    }
    
    public String getXAConnectionFactoryClassName() {
        if (this.xAConnectionFactoryClassName != null) {
            return this.xAConnectionFactoryClassName;
        } else if (raprops != null) {
            return raprops.xAConnectionFactoryClassName;
        } else {
            return null;
        }
    }
    
    public void setXAConnectionFactoryClassName(
                    String connectionFactoryClassName) {
        logger.log(Level.FINEST, "setXAConnectionFactoryClassname " + connectionFactoryClassName);
        xAConnectionFactoryClassName = connectionFactoryClassName;
    }
    
    public String getXAQueueConnectionFactoryClassName() {
        if (this.xAQueueConnectionFactoryClassName != null) {
            return this.xAQueueConnectionFactoryClassName;
        } else if (raprops != null) {
            return raprops.xAQueueConnectionFactoryClassName;
        } else {
            return null;
        }
    }
    
    public void setXAQueueConnectionFactoryClassName(
                    String queueConnectionFactoryClassName) {
        logger.log(Level.FINEST, "setXAQueueConnectionFactoryClassname " + 
                                  queueConnectionFactoryClassName);
        xAQueueConnectionFactoryClassName = queueConnectionFactoryClassName;
    }
    
    public String getXATopicConnectionFactoryClassName() {
        if (this.xATopicConnectionFactoryClassName != null) {
            return this.xATopicConnectionFactoryClassName;
        } else if (raprops != null) {
            return raprops.xATopicConnectionFactoryClassName;
        } else {
            return null;
        }
    }
    
    public void setXATopicConnectionFactoryClassName(
                    String topicConnectionFactoryClassName) {
        logger.log(Level.FINEST, "setXATopicConnectionFactoryClassname " + 
                                  topicConnectionFactoryClassName);
        xATopicConnectionFactoryClassName = topicConnectionFactoryClassName;
    }    
    
    public boolean equals(Object o){
        if(o == null) return false;
        if (!(o instanceof GenericJMSRAProperties)) return false;
        GenericJMSRAProperties other = (GenericJMSRAProperties) o;
        return (
                  (other.getSupportsXA() == this.getSupportsXA()) &&
                  StringUtils.isEqual(other.getCommonSetterMethodName(), this.getCommonSetterMethodName()) &&
                  StringUtils.isEqual(other.getConnectionFactoryProperties(), this.getConnectionFactoryProperties()) &&
                  StringUtils.isEqual(other.getJndiProperties(), this.getJndiProperties()) &&
                  StringUtils.isEqual(other.getPassword(), this.getPassword()) &&
                  StringUtils.isEqual(other.getProviderIntegrationMode(), this.getProviderIntegrationMode()) &&
                  StringUtils.isEqual(other.getQueueClassName(), this.getQueueClassName()) &&
                  StringUtils.isEqual(other.getQueueConnectionFactoryClassName(), this.getQueueConnectionFactoryClassName())
                );
    }
    
    public int hashCode(){
        //XXX: build a better hashcode
       return ("" + this.cfClassName + this.cfProperties + this.connectionURL 
                + this.jndiProperties + 
                this.password + this.providerIntegrationMode + 
                this.queueCFClassName + 
                this.queueClassName + this.setterMethodName + 
                this.topicCFClassName + this.topicClassName + 
                this.userName + this.xAConnectionFactoryClassName + 
                this.xAQueueConnectionFactoryClassName + 
                this.xATopicConnectionFactoryClassName).hashCode(); 
    }

    public String toString() {
        String s = super.toString();
        s = s + "{ConnectionFactoryClassName = " + getConnectionFactoryClassName() + "},";
        s = s + "{QueueConnectionFactoryClassName = " + getQueueConnectionFactoryClassName() + "},";
        s = s + "{TopicConnectionFactoryClassName = " + getTopicConnectionFactoryClassName() + "},";
        s = s + "{XAConnectionFactoryClassName = " + getXAConnectionFactoryClassName() + "},";
        s = s + "{XAQueueConnectionFactoryClassName = " + getXAQueueConnectionFactoryClassName() + "},";
        s = s + "{XATopicConnectionFactoryClassName = " + getXATopicConnectionFactoryClassName() + "},";
        
        s = s + "{QueueClassName = " + getQueueClassName() + "},";
        s = s + "{TopicClassName = " + getTopicClassName() + "},";
        s = s + "{UnifiedDestinationClassName = " + getUnifiedDestinationClassName() + "},";
        
        s = s + "{ConnectionFactoryProperties = " + getConnectionFactoryProperties() + "},";
        s = s + "{JndiProperties = " + getJndiProperties() + "},";
        s = s + "{ProviderIntegrationMode = " + getProviderIntegrationMode() + "},";
        s = s + "{CommonSetterMethodName = " + getCommonSetterMethodName() + "},";
        s = s + "{SupportsXA = " + getSupportsXA() + "},";
        return s;
    }
 }
