/*
 * ResourceMonitorMBean.java
 */

package com.sun.genericra.monitoring;

/**
 *
 * @author ramesh
 */
public interface ResourceMonitorMBean {
    
    String getPoolStatistics(String name);
    
    /**
     * Getter for property currentSize.
     * @return Value of property currentSize.
     */
    int getCurrentSize(String name);
    
    /**
     * Getter for property busyResources.
     * @return Value of property busyResources.
     */
    int getBusyResources(String name);
    
    
    /**
     * Getter for property freeResources.
     * @return Value of property freeResources.
     */
    int getFreeResources(String name);
    
    /**
     * Getter for property waiting.
     * @return Value of property waiting.
     */
    int getWaiting(String name);
    
    int getConnections(String name);
    
    int getMaxSize(String name);
    
    long getMaxWaitTime(String name) ;    
    
}
