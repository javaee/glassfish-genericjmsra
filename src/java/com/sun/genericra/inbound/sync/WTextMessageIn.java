/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the "License").  You may not use this file except
 * in compliance with the License.
 *
 * You can obtain a copy of the license at
 * https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * HEADER in each file and include the License file at
 * https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * If applicable add the following below this CDDL HEADER,
 * with the fields enclosed by brackets "[]" replaced with
 * your own identifying information: Portions Copyright
 * [year] [name of copyright owner]
 */
/*
 * $RCSfile: WTextMessageIn.java,v $
 * $Revision: 1.1 $
 * $Date: 2007-04-16 07:17:00 $
 *
 * Copyright 2003-2007 Sun Microsystems, Inc. All Rights Reserved.  
 */


package com.sun.genericra.inbound.sync;

import javax.jms.TextMessage;
import javax.jms.JMSException;

/**
 * See WMessage
 *
 * @author Frank Kieviet
 * @version $Revision: 1.1 $
 */
public class WTextMessageIn extends WMessageIn implements TextMessage {
    private TextMessage mDelegate;
    
    /**
     * Constructor
     * 
     * @param delegate real msg
     * @param ackHandler callback to call when ack() or recover() is called
     * @param ibatch index of this message in a batch; -1 for non-batched
     */
    public WTextMessageIn(TextMessage delegate, AckHandler ackHandler, int ibatch) {
        super(delegate, ackHandler, ibatch);
        mDelegate = delegate;
    }

    /**
     * @see javax.jms.TextMessage#getText()
     */
    public String getText() throws JMSException {
        return mDelegate.getText();
    }

    /**
     * @see javax.jms.TextMessage#setText(java.lang.String)
     */
    public void setText(String arg0) throws JMSException {
        mDelegate.setText(arg0);
    }
}
