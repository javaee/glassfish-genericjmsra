/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.genericra.outbound;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ConnectionEventListener;


/**
 * Encapsulates the event listener usage of the resource adapter.
 * All the connection event listeners registered will be saved
 * in this object.
 *
 * @author Sivakumar Thyagarajan
 */
public class ConnectionEventListenerHandler {
    private ArrayList l = new ArrayList();
    private ManagedConnection mc;

    public ConnectionEventListenerHandler(ManagedConnection mc) {
        this.mc = mc;
    }

    public void addConnectionEventListener(ConnectionEventListener cel) {
        this.l.add(cel);
    }

    public void removeConnectionEventListener(ConnectionEventListener cel) {
        this.l.remove(cel);
    }

    public void sendEvent(int eventType, Exception ex, Object connectionHandle) {
        List lClone = (List) this.l.clone();
        ConnectionEvent cevent = null;

        if (ex != null) {
            cevent = new ConnectionEvent(this.mc, eventType, ex);
        } else {
            cevent = new ConnectionEvent(mc, eventType);
        }

        if (connectionHandle != null) {
            cevent.setConnectionHandle(connectionHandle);
        }

        for (Iterator iter = lClone.iterator(); iter.hasNext();) {
            ConnectionEventListener lstnr = (ConnectionEventListener) iter.next();

            switch (eventType) {
            case ConnectionEvent.CONNECTION_ERROR_OCCURRED:
                lstnr.connectionErrorOccurred(cevent);

                break;

            case ConnectionEvent.CONNECTION_CLOSED:
                lstnr.connectionClosed(cevent);

                break;

            case ConnectionEvent.LOCAL_TRANSACTION_STARTED:
                lstnr.localTransactionStarted(cevent);

                break;

            case ConnectionEvent.LOCAL_TRANSACTION_COMMITTED:
                lstnr.localTransactionCommitted(cevent);

                break;

            case ConnectionEvent.LOCAL_TRANSACTION_ROLLEDBACK:
                lstnr.localTransactionRolledback(cevent);

                break;

            default:
                throw new IllegalArgumentException("Unknown Connection " +
                    "Event Type :" + eventType);
            }
        }
    }
}
