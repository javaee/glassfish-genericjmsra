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

package com.sun.genericra.monitoring;

import com.sun.genericra.GenericJMSRA;

/**
 * This class is the MBean implementation of generic jms ra configuration
 * monitor. It supports runtime modification of Generic RA through MBean
 * operations.
 */

public class ConfigurationMonitor implements ConfigurationMonitorMBean {
    
    /** Creates a new instance of ConfigurationMonitor */
    public ConfigurationMonitor() {
    }
    
    /**
     * Returns the log level for the RA.
     */
    public  String getLogLevel() {
        return GenericJMSRA.getInstance().getLogLevel();
    }
    
    /**
     * Returns the listener interface that this RA is capable of invoking.
     */    
    public String getListenerMethod(){
        return GenericJMSRA.getInstance().getListeningMethod().toString();
    }
    
    /**
     * Sets the log level of generic jms ra.
     */
    public String setLogLevel(String level){
        String ret = null;
        try {
            GenericJMSRA.getInstance().setLogLevel(level);            
        } catch (Throwable t) {
            ret = "Cannot set log level to " + level + " " + t.getMessage();
        }
        ret = "Log level set to " + GenericJMSRA.getInstance().getLogLevel();;
        return ret;
    }  
    
}
