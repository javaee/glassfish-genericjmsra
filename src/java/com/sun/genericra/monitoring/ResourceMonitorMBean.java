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
