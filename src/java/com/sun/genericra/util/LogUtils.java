/**
 * Copyright (c) 2004-2005 Oracle and/or its affiliates. All rights reserved.
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

import java.util.logging.*;


/**
 * Utility to obtain  the logger.
 *
 * @author Binod P.G
 */
public class LogUtils {
    public static Logger getLogger() {
        return Logger.getLogger(Constants.LOGGER_NAME);
    }
}
