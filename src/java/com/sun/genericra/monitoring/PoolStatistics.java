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
