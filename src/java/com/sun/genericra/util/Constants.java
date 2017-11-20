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
