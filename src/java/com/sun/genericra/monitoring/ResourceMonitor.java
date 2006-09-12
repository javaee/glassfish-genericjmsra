/*
 * ResourceMonitor.java
 *
 */

package com.sun.genericra.monitoring;

import java.util.Hashtable;


import com.sun.genericra.inbound.InboundJmsResourcePool;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.sun.genericra.util.LogUtils;


/**
 *
 * @author ramesh
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
    
    public void addPool(String consumer, InboundJmsResourcePool ps) {
        
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
        InboundJmsResourcePool pool = getPool(appname);
        if (pool == null) {
            return "Endpoint not found OR could not be monitored";
        }
        PoolStatistics ps = new PoolStatistics(pool);
        return ps.formatStatistics();
    }
    
    private InboundJmsResourcePool getPool(String name) {
        InboundJmsResourcePool pool = null;
        try {
            pool = (InboundJmsResourcePool) poolstatistics.get(name.trim());
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
        InboundJmsResourcePool pool = getPool(appname);
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
        InboundJmsResourcePool pool = getPool(appname);
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
        InboundJmsResourcePool pool = getPool(appname);
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
        InboundJmsResourcePool pool = getPool(appname);
        if (pool == null) {
            return -2;
        }
        return pool.getWaiting();
    }
    
    public int getConnections(String appname) {
        if (!validate(appname)) {
            return -1;
        }
        InboundJmsResourcePool pool = getPool(appname);
        if (pool == null) {
            return -2;
        }
        return pool.getConnectionsInUse();
    }
    
    public int getMaxSize(String appname) {
        if (!validate(appname)) {
            return -1;
        }
        InboundJmsResourcePool pool = getPool(appname);
        if (pool == null) {
            return -2;
        }
        return pool.getMaxSize();
    }
    
    public long getMaxWaitTime(String appname) {
        if (!validate(appname)) {
            return -1;
        }
        InboundJmsResourcePool pool = getPool(appname);
        if (pool == null) {
            return -2;
        }
        return  pool.getMaxWaitTime();
    }
    
}
