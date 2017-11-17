/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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

