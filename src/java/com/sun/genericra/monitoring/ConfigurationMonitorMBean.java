/*
 * ConfigurationMonitorMBean.java
 *
 */

package com.sun.genericra.monitoring;

/**
 *
 * @author ramesh
 */
public interface ConfigurationMonitorMBean {
    
    String getLogLevel();
    
    String getListenerMethod();
    
    String setLogLevel(String level);   

}
