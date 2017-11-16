/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
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
