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
 *
 * @author ramesh
 */
public interface ResourceMonitorMBean {
    
    /**
     * Return the pool parameters for the endpoint
     *
     * @param endpoint name.
     * @return pool parameters. 
     */
    String getPoolStatistics(String name);
    
    /**
     * Getter for property currentSize.
     * 
     * @param endpoint name.
     * @return Value of property currentSize.
     */
    int getCurrentSize(String name);
    
    /**
     * Getter for property busyResources.
     *
     * @param endpoint name.
     * @return Value of property busyResources.
     */
    int getBusyResources(String name);    
    
    /**
     * Getter for property freeResources.
     *
     * @param endpoint name.
     * @return Value of property freeResources.
     */
    int getFreeResources(String name);
    
    /**
     * Getter for property waiting.
     *
     * @param endpoint name.
     * @return Value of property waiting.
     */
    int getWaiting(String name);
    
    /**
     * Returns the connections used by the endpoint
     *
     * @param endpoint name.
     * @return number of connections.
     */
    int getConnections(String name);
    
    /** 
     * Returns the maximum size of pool.
     * 
     * @param endpoint name.
     * @return max size.
     */
    int getMaxSize(String name);
    
    /**
     * Returns the max wait time of the pool.
     * @param endpoint name.
     * @return wait time.
     */
    long getMaxWaitTime(String name) ;    
    
}
