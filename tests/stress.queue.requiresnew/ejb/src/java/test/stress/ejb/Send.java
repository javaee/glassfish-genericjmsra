/**
 * Copyright (c) 2003-2017 Oracle and/or its affiliates. All rights reserved.
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

/*
 * %W% %E%
 */

package test.stress.ejb;

import java.rmi.RemoteException;

import javax.ejb.EJBObject;

public interface Send extends EJBObject {
	
    /**
     * Send a TextMessage containing the specified String to the configured outbound queue
     * @param text
     */
    void sendTextMessageToQ(String Property) throws RemoteException ;
}

