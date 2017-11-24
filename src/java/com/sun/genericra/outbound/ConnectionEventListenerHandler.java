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
