/**
 * Copyright 2004-2005 Sun Microsystems, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
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
