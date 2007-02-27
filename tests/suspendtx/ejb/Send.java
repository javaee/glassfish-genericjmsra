/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * %W% %E%
 */

package com.sun.ejte.j2ee.genericra.ejb;

import javax.ejb.EJBObject;
import java.rmi.RemoteException;
import javax.ejb.EJBException;

public interface Send extends EJBObject {
    void sendTextMessageToQ(String Property) throws RemoteException ;
}

