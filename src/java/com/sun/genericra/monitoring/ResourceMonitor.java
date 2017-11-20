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

import java.util.Hashtable;


import com.sun.genericra.inbound.AbstractJmsResourcePool;

import com.sun.genericra.inbound.async.InboundJmsResourcePool;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.sun.genericra.util.LogUtils;


/**
 * MBEan implemenation to monitor the inbound resource pool.
 * 
 */
public class ResourceMonitor implements ResourceMonitorMBean {
    
    public Hashtable poolstatistics;
    
    public static Logger logger;
    
    
    static {
        logger = LogUtils.getLogger();
    }
    /** Creates a new instance of ResourceMonitor */
    public ResourceMonitor() {
        poolstatistics = new Hashtable();
    }
    
    public void addPool(String consumer, AbstractJmsResourcePool ps) {
        
        if ((ps != null) && (consumer != null)) {
            try {
                poolstatistics.put(consumer, ps);
            } catch (Throwable t ){
                
                t.printStackTrace();
                logger.log(Level.SEVERE, "Exception while initializing " +
                        "monitoring for endpoint " + consumer);
            }
            logger.log(Level.FINE, "Including endpoint " + consumer + " for monitoring");
        }
    }
    
    public void removePool(String consumer) {
        try {
            poolstatistics.remove(consumer);
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Unable to remove " + consumer +
                    " from monitoring table");
        }
    }
    
    private boolean validate(String appname) {
        if ((appname == null) || (appname.trim().equals(""))) {
            return false;
        }
        return true;
    }
    
    public String getPoolStatistics(String appname) {
        if (!validate(appname)) {
            return "Invalid application name";
        }
        AbstractJmsResourcePool pool = getPool(appname);
        if (pool == null) {
            return "Endpoint not found OR could not be monitored";
        }
        PoolStatistics ps = new PoolStatistics((InboundJmsResourcePool)pool);
        return ps.formatStatistics();
    }
    
    private AbstractJmsResourcePool getPool(String name) {
        AbstractJmsResourcePool pool = null;
        try {
            pool = (AbstractJmsResourcePool) poolstatistics.get(name.trim());
        } catch (Exception e) {
            ;
        }
        return pool;
    }
    /**
     * Getter for property currentSize.
     * @return Value of property currentSize.
     */
    public  int getCurrentSize(String appname) {
        if (!validate(appname)) {
            return -1;
        }
        AbstractJmsResourcePool pool = getPool(appname);
        if (pool == null) {
            return -2;
        }
        return pool.getCurrentResources();
    }
    
    /**
     * Getter for property busyResources.
     * @return Value of property busyResources.
     */
    public int getBusyResources(String appname) {
        if (!validate(appname)) {
            return -1;
        }
        AbstractJmsResourcePool pool = getPool(appname);
        if (pool == null) {
            return -2;
        }
        return pool.getBusyResources();
    }
    
    
    /**
     * Getter for property freeResources.
     * @return Value of property freeResources.
     */
    public int getFreeResources(String appname) {
        if (!validate(appname)) {
            return -1;
        }
        AbstractJmsResourcePool pool = getPool(appname);
        if (pool == null) {
            return -2;
        }
        return pool.getFreeResources();
    }
    
    /**
     * Getter for property waiting.
     * @return Value of property waiting.
     */
    public int getWaiting(String appname) {
        if (!validate(appname)) {
            return -1;
        }
        AbstractJmsResourcePool pool = getPool(appname);
        if (pool == null) {
            return -2;
        }
        return pool.getWaiting();
    }
    
    public int getConnections(String appname) {
        if (!validate(appname)) {
            return -1;
        }
        AbstractJmsResourcePool pool = getPool(appname);
        if (pool == null) {
            return -2;
        }
        return pool.getConnectionsInUse();
    }
    
    public int getMaxSize(String appname) {
        if (!validate(appname)) {
            return -1;
        }
        AbstractJmsResourcePool pool = getPool(appname);
        if (pool == null) {
            return -2;
        }
        return pool.getMaxSize();
    }
    
    public long getMaxWaitTime(String appname) {
        if (!validate(appname)) {
            return -1;
        }
        AbstractJmsResourcePool pool = getPool(appname);
        if (pool == null) {
            return -2;
        }
        return  pool.getMaxWaitTime();
    }
    
}
