/*
 * ConfigurationMonitor.java
 *
 */

package com.sun.genericra.monitoring;

import com.sun.genericra.GenericJMSRA;
/**
 *
 * @author ramesh
 */
public class ConfigurationMonitor implements ConfigurationMonitorMBean {
    
    /** Creates a new instance of ConfigurationMonitor */
    public ConfigurationMonitor() {
    }
    
    /**
     * Returns the log level for the RA
     */
    public  String getLogLevel() {
        return GenericJMSRA.getInstance().getLogLevel();
    }
    
    
    public String getListenerMethod(){
        return GenericJMSRA.getInstance().getListeningMethod().toString();
    }
    
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
