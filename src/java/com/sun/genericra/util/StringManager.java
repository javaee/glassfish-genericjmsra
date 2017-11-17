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

import java.text.MessageFormat;

import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Implementation of a local string manager. Provides access to i18n messages
 * for classes that need them.
 *
 * <p> One StringManager per package can be created and accessed by the
 * getManager method call. The ResourceBundle name is constructed from
 * the given package name in the constructor plus the suffix of "LocalStrings".
 * Thie means that localized information will be contained in a
 * LocalStrings.properties file located in the package directory of the
 * classpath.
 *
 *
 */
public class StringManager extends StringManagerBase {
    /** logger used for this class */
    private static Logger _logger = LogUtils.getLogger();

    /** name of the resource bundle property file name */
    private static final String RES_BUNDLE_NM = ".LocalStrings";

    /** cache for all the local string managers (per pkg) */
    private static Hashtable managers = new Hashtable();

    /** resource bundle to be used by this manager */
    private ResourceBundle _resourceBundle = null;

    /**
     * Initializes the resource bundle.
     *
     * @param    packageName    name of the package
     */
    private StringManager(String packageName) {
        super(packageName + RES_BUNDLE_NM);
    }

    /**
     * Returns a local string manager for the given package name.
     *
     * @param    packageName    name of the package of the src
     *
     * @return   a local string manager for the given package name
     */
    public synchronized static StringManager getManager(String packageName) {
        StringManager mgr = (StringManager) managers.get(packageName);

        if (mgr == null) {
            mgr = new StringManager(packageName);

            try {
                managers.put(packageName, mgr);
            } catch (Exception e) {
                _logger.log(Level.SEVERE, "iplanet_util.error_while_caching", e);
            }
        }

        return mgr;
    }

    /**
     *
     * Returns a local string manager for the given package name.
     *
     * @param    callerClass    the object making the call
     *
     * @return   a local string manager for the given package name
     */
    public synchronized static StringManager getManager(Class callerClass) {
        try {
            Package pkg = callerClass.getPackage();

            if (pkg != null) {
                String pkgName = pkg.getName();

                return getManager(pkgName);
            } else {
                // class does not belong to any pkg
                String pkgName = callerClass.getName();

                return getManager(pkgName);
            }
        } catch (Exception e) {
            _logger.log(Level.SEVERE, "iplanet_util.error_in_getMgr", e);

            // dummy string manager
            return getManager("");
        }
    }
}
