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

package com.sun.genericra.util;


/**
 * Constant values used by Generic Resource Adapter.
 *
 * @author Binod P.G
 */
public final class Constants {
    /**
     * Logger name used by the resource adapter
     */
    public static final String LOGGER_NAME = "com.sun.genericjmsra";

    /**
     * default separator used by resource adapter
     */
    public static final String SEPARATOR = "=";

    /**
     * default delimiter used by resource adapter
     */
    public static final String DELIMITER = ",";

    /**
     * Name for Jndi based provider integration mode
     */
    public static final String JNDI_BASED = "jndi";

    /**
     * Name for Javabean provider integration mode
     */
    public static final String JAVABEAN_BASED = "javabean";

    /**
     * String indicating a queue
     */
    public static final String QUEUE = "javax.jms.Queue";

    /**
     * String indicating a topic
     */
    public static final String TOPIC = "javax.jms.Topic";

    /**
     * String indicating a destination
     */
    public static final String DESTINATION = "javax.jms.Destination";

    /**
     * String indicating a durable
     */
    public static final String DURABLE = "Durable";
    
    public static int DEFAULT_ACK_TIMEOUT = 2;

    /**
     * String indicating a non-durable
     */
    public static final String NONDURABLE = "Non-Durable";
    public static final int UNIFIED_SESSION = 0;
    public static final int TOPIC_SESSION = 1;
    public static final int QUEUE_SESSION = 2;

    public class LogLevel {
        /**
         * String indicating FINEST log level
         */
        public static final String FINEST = "finest";

        /**
         * String indicating FINER log level
         */
        public static final String FINER = "finer";

        /**
         * String indicating FINE log level
         */
        public static final String FINE = "fine";

        /**
         * String indicating INFO log level
         */
        public static final String INFO = "info";

        /**
         * String indicating WARNING log level
         */
        public static final String WARNING = "warning";

        /**
         * String indicating SEVERE log level
         */
        public static final String SEVERE = "severe";
        
    }
}
