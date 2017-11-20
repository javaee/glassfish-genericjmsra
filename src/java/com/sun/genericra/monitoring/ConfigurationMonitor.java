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
