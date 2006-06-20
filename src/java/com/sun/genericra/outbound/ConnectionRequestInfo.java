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
package com.sun.genericra.outbound;

import com.sun.genericra.util.SecurityUtils;
import com.sun.genericra.util.StringUtils;

import javax.resource.spi.ManagedConnectionFactory;


/**
 * Generic JMS resource adapter specific request properties.
 * Username, password and clientId;
 * @author Sivakumar Thyagarajan
 */
public class ConnectionRequestInfo
    implements javax.resource.spi.ConnectionRequestInfo {
    private String userName;
    private String password;
    private String clientID;
    private ManagedConnectionFactory mcf;

    public ConnectionRequestInfo(ManagedConnectionFactory mcf, String userName,
        String password) {
        this.userName = userName;
        this.password = password;
        this.mcf = mcf;
        this.clientID = ((AbstractManagedConnectionFactory) mcf).getClientId();
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof ConnectionRequestInfo) {
            ConnectionRequestInfo other = (ConnectionRequestInfo) obj;

            return (StringUtils.isEqual(this.userName, other.userName) &&
            StringUtils.isEqual(this.password, other.password));
        } else {
            return false;
        }
    }

    /**
     * Retrieves the hashcode of the object.
     *
     * @return  hashCode.
     */
    public int hashCode() {
        String result = "" + userName + password;

        return result.hashCode();
    }
}
