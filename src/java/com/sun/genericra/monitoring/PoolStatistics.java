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

import com.sun.genericra.inbound.async.InboundJmsResourcePool;

import com.sun.genericra.inbound.async.InboundJmsResource;

/**
 * Object that stores the pool parameters of an inbound resource pool.
 * 
 */
public class PoolStatistics {
    
    
    private InboundJmsResourcePool pool;
    
    private static String MAX_SIZE = "Maximum Size of the pool";
    
    private static String MAX_WAIT_TIME = "Maximum wait time to queue the request for a resource";
    
    private static String CURRENT_RESOURCES = "No of resources in the pool (FREE + BUSY)";
    
    private static String BUSY_RESOURCES = "No of resources busy";
    
    private static String FREE_RESOURCES = "No of free resources";
    
    private static String CONNECTIONS_IN_USE = "No of connections being used ";
    
    private static String REQUESTS_WAITING = "No of requests waiting for resource";
    private static String SEPARATOR = " : ";
    private static String NEW_LINE = "\n";
    
    /** Creates a new instance of PoolStatistics */
    public PoolStatistics(InboundJmsResourcePool pool) {
        this.pool = pool;
    }
    
    public void setPool(InboundJmsResourcePool pool) {
        this.pool = pool;
    }
    
    
    /**
     * Getter for property currentSize.
     * @return Value of property currentSize.
     */
    private  int getCurrentSize() {
        return pool.getCurrentResources();
    }
    
    /**
     * Getter for property busyResources.
     * @return Value of property busyResources.
     */
    private int getBusyResources() {
        return pool.getBusyResources();
    }
    
    
    /**
     * Getter for property freeResources.
     * @return Value of property freeResources.
     */
    private int getFreeResources() {
        return pool.getFreeResources();
    }
    
    /**
     * Getter for property waiting.
     * @return Value of property waiting.
     */
    private int getWaiting() {
        return pool.getWaiting();
    }
    
    private int getConnections() {
        return pool.getConnectionsInUse();
    }
    
    private int getMaxSize() {
        return pool.getMaxSize();
    }
    
    private long getMaxWaitTime() {
        return  pool.getMaxWaitTime();
    }
    public String formatStatistics() {
        StringBuffer output = new StringBuffer();
        
        output.append(this.MAX_SIZE);
        output.append(this.SEPARATOR);
        output.append(this.getMaxSize());
        output.append(this.NEW_LINE);
        
        
        output.append(this.MAX_WAIT_TIME);
        output.append(this.SEPARATOR);
        output.append(this.getMaxWaitTime());
        output.append(this.NEW_LINE);
        
        output.append(this.CONNECTIONS_IN_USE);
        output.append(this.SEPARATOR);
        output.append(this.getConnections());
        output.append(this.NEW_LINE);
        
        output.append(this.CURRENT_RESOURCES);
        output.append(this.SEPARATOR);
        output.append(this.getCurrentSize());
        output.append(this.NEW_LINE);
        
        output.append(this.BUSY_RESOURCES);
        output.append(this.SEPARATOR);
        output.append(this.getBusyResources());
        output.append(this.NEW_LINE);
        
        output.append(this.FREE_RESOURCES);
        output.append(this.SEPARATOR);
        output.append(this.getFreeResources());
        output.append(this.NEW_LINE);
        
        output.append(this.REQUESTS_WAITING);
        output.append(this.SEPARATOR);
        output.append(this.getWaiting());
        output.append(this.NEW_LINE);
        
        
        return output.toString();
    }
    
}
