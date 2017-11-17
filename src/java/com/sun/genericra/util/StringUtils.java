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


/**
 * Common utility functions for <code>String</code>
 * @author Sivakumar Thyagarajan
 */
public class StringUtils {
    public static boolean isNull(String s) {
        return ((s == null) || s.trim().equals(""));
    }

    /**
     * Returns true if two strings are equal; false otherwise
     *
     * @param   str1    <code>String</code>
     * @param   str2    <code>String</code>
     * @return  true    if the two strings are equal
     *          false   otherwise
     */
    static public boolean isEqual(String str1, String str2) {
        boolean result = false;

        if (str1 == null) {
            result = (str2 == null);
        } else {
            result = str1.equals(str2);
        }

        return result;
    }
}
