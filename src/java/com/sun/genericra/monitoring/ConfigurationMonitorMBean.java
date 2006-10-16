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

/**
 * MBean inteface for Generic jms RA configuration changes.
 * 
 */
public interface ConfigurationMonitorMBean {
    /**
     * Returns the log level for the RA.
     */    
    String getLogLevel();
     /**
     * Returns the listener interface that this RA is capable of invoking.
     */    
    String getListenerMethod();
    /**
     * Sets the log level of generic jms ra.
     */    
    String setLogLevel(String level);   

}
