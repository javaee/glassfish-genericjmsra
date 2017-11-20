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
