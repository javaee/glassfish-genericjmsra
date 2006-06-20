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
