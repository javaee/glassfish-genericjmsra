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

import java.text.MessageFormat;

import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Implementation of a local string manager. Provides access to i18n messages
 * for classes that need them.
 */
public class StringManagerBase {
    /** logger used for this class */
    private static Logger _logger = LogUtils.getLogger();

    /** default value used for undefined local string */
    private static final String NO_DEFAULT = "No local string defined";

    /** cache for all the local string managers (per pkg) */
    private static Hashtable managers = new Hashtable();

    /** resource bundle to be used by this manager */
    private ResourceBundle _resourceBundle = null;

    /**
     * Initializes the resource bundle.
     *
     * @param    resourceBundleName    name of the resource bundle
     */
    protected StringManagerBase(String resourceBundleName) {
        try {
            _resourceBundle = ResourceBundle.getBundle(resourceBundleName);
        } catch (Exception e) {
            _logger.log(Level.SEVERE, "string_util.no_resource_bundle", e);
        }
    }

    /**
     * Returns a local string manager for the given resourceBundle name.
     *
     * @param    resourceBundleName    name of the resource bundle
     *
     * @return   a local string manager for the given package name
     */
    public synchronized static StringManagerBase getStringManager(
        String resourceBundleName) {
        StringManagerBase mgr = (StringManagerBase) managers.get(resourceBundleName);

        if (mgr == null) {
            mgr = new StringManagerBase(resourceBundleName);

            try {
                managers.put(resourceBundleName, mgr);
            } catch (Exception e) {
                _logger.log(Level.SEVERE, "string_util.error_while_caching", e);
            }
        }

        return mgr;
    }

    /**
     * Returns a localized string.
     *
     * @param    key           the name of the resource to fetch
     *
     * @return   the localized string
     */
    public String getString(String key) {
        return getStringWithDefault(key, NO_DEFAULT);
    }

    /**
     * Returns a localized string. If the key is not found, it will
     * return the default given value.
     *
     * @param    key           the name of the resource to fetch
     * @param    defaultValue  the default return value if not found
     *
     * @return   the localized string
     */
    public String getStringWithDefault(String key, String defaultValue) {
        String value = null;

        try {
            value = this._resourceBundle.getString(key);
        } catch (Exception e) {
            _logger.log(Level.FINE, "No local string for: " + key, e);
        }

        if (value != null) {
            return value;
        } else {
            return defaultValue;
        }
    }

    /**
     * Returns a local string for the caller and format the arguments
     * accordingly. If the key is not found, it will use the given
     * default format.
     *
     * @param   key            the key to the local format string
     * @param   defaultFormat  the default format if not found in the resources
     * @param   arguments      the set of arguments to provide to the formatter
     *
     * @return  a formatted localized string
     */
    public String getStringWithDefault(String key, String defaultFormat,
        Object[] arguments) {
        MessageFormat f = new MessageFormat(getStringWithDefault(key,
                    defaultFormat));

        for (int i = 0; i < arguments.length; i++) {
            if (arguments[i] == null) {
                arguments[i] = "null";
            } else if (!(arguments[i] instanceof String) &&
                    !(arguments[i] instanceof Number) &&
                    !(arguments[i] instanceof java.util.Date)) {
                arguments[i] = arguments[i].toString();
            }
        }

        String fmtStr = null;

        try {
            fmtStr = f.format(arguments);
        } catch (Exception e) {
            _logger.log(Level.WARNING, "string_util.error_while_formating", e);

            // returns default format
            fmtStr = defaultFormat;
        }

        return fmtStr;
    }

    /**
     * Returns a local string for the caller and format the arguments
     * accordingly.
     *
     * @param   key     the key to the local format string
     * @param   arg1    the one argument to be provided to the formatter
     *
     * @return  a formatted localized string
     */
    public String getString(String key, Object arg1) {
        return getStringWithDefault(key, NO_DEFAULT, new Object[] { arg1 });
    }

    /**
     * Returns a local string for the caller and format the arguments
     * accordingly.
     *
     * @param   key     the key to the local format string
     * @param   arg1    first argument to be provided to the formatter
     * @param   arg2    second argument to be provided to the formatter
     *
     * @return  a formatted localized string
     */
    public String getString(String key, Object arg1, Object arg2) {
        return getStringWithDefault(key, NO_DEFAULT, new Object[] { arg1, arg2 });
    }

    /**
     * Returns a local string for the caller and format the arguments
     * accordingly.
     *
     * @param   key     the key to the local format string
     * @param   arg1    first argument to be provided to the formatter
     * @param   arg2    second argument to be provided to the formatter
     * @param   arg3    third argument to be provided to the formatter
     *
     * @return  a formatted localized string
     */
    public String getString(String key, Object arg1, Object arg2, Object arg3) {
        return getStringWithDefault(key, NO_DEFAULT,
            new Object[] { arg1, arg2, arg3 });
    }

    /**
     * Returns a local string for the caller and format the arguments
     * accordingly.
     *
     * @param   key     the key to the local format string
     * @param   arg1    first argument to be provided to the formatter
     * @param   arg2    second argument to be provided to the formatter
     * @param   arg3    third argument to be provided to the formatter
     * @param   arg4    fourth argument to be provided to the formatter
     *
     * @return  a formatted localized string
     */
    public String getString(String key, Object arg1, Object arg2, Object arg3,
        Object arg4) {
        return getStringWithDefault(key, NO_DEFAULT,
            new Object[] { arg1, arg2, arg3, arg4 });
    }

    /**
     * Returns a local string for the caller and format the arguments
     * accordingly.
     *
     * @param   key     the key to the local format string
     * @param   args    the array of arguments to be provided to the formatter
     *
     * @return  a formatted localized string
     */
    public String getString(String key, Object[] args) {
        return getStringWithDefault(key, NO_DEFAULT, args);
    }
}
