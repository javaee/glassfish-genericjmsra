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
 * $RCSfile: WBytesMessageIn.java,v $
 * $Revision: 1.1 $
 * $Date: 2007-04-16 07:17:00 $
 *
 * Copyright 2003-2007 Sun Microsystems, Inc. All Rights Reserved.  
 */


package com.sun.genericra.inbound.sync;

import javax.jms.BytesMessage;
import javax.jms.JMSException;

/**
 * See WMessage
 *
 * @author Frank Kieviet
 * @version $Revision: 1.1 $
 */
public class WBytesMessageIn extends WMessageIn implements BytesMessage {
    private BytesMessage mDelegate;
    
    /**
     * Constructor
     * 
     * @param delegate real msg
     * @param ackHandler callback to call when ack() or recover() is called
     * @param ibatch index of this message in a batch; -1 for non-batched
     */
    public WBytesMessageIn(BytesMessage delegate, AckHandler ackHandler, int ibatch) {
        super(delegate, ackHandler, ibatch);
        mDelegate = delegate;
    }

    /**
     * @see javax.jms.BytesMessage#readBoolean()
     */
    public boolean readBoolean() throws JMSException {
        return mDelegate.readBoolean();
    }

    /**
     * @see javax.jms.BytesMessage#readByte()
     */
    public byte readByte() throws JMSException {
        return mDelegate.readByte();
    }

    /**
     * @see javax.jms.BytesMessage#readBytes(byte[], int)
     */
    public int readBytes(byte[] arg0, int arg1) throws JMSException {
        return mDelegate.readBytes(arg0, arg1);
    }

    /**
     * @see javax.jms.BytesMessage#readBytes(byte[])
     */
    public int readBytes(byte[] arg0) throws JMSException {
        return mDelegate.readBytes(arg0);
    }

    /**
     * @see javax.jms.BytesMessage#readChar()
     */
    public char readChar() throws JMSException {
        return mDelegate.readChar();
    }

    /**
     * @see javax.jms.BytesMessage#readDouble()
     */
    public double readDouble() throws JMSException {
        return mDelegate.readDouble();
    }

    /**
     * @see javax.jms.BytesMessage#readFloat()
     */
    public float readFloat() throws JMSException {
        return mDelegate.readFloat();
    }

    /**
     * @see javax.jms.BytesMessage#readInt()
     */
    public int readInt() throws JMSException {
        return mDelegate.readInt();
    }

    /**
     * @see javax.jms.BytesMessage#readLong()
     */
    public long readLong() throws JMSException {
        return mDelegate.readLong();
    }

    /**
     * @see javax.jms.BytesMessage#readShort()
     */
    public short readShort() throws JMSException {
        return mDelegate.readShort();
    }

    /**
     * @see javax.jms.BytesMessage#readUnsignedByte()
     */
    public int readUnsignedByte() throws JMSException {
        return mDelegate.readUnsignedByte();
    }

    /**
     * @see javax.jms.BytesMessage#readUnsignedShort()
     */
    public int readUnsignedShort() throws JMSException {
        return mDelegate.readUnsignedShort();
    }

    /**
     * @see javax.jms.BytesMessage#readUTF()
     */
    public String readUTF() throws JMSException {
        return mDelegate.readUTF();
    }

    /**
     * @see javax.jms.BytesMessage#writeBoolean(boolean)
     */
    public void writeBoolean(boolean arg0) throws JMSException {
        mDelegate.writeBoolean(arg0);
    }

    /**
     * @see javax.jms.BytesMessage#writeByte(byte)
     */
    public void writeByte(byte arg0) throws JMSException {
        mDelegate.writeByte(arg0);
    }

    /**
     * @see javax.jms.BytesMessage#writeBytes(byte[], int, int)
     */
    public void writeBytes(byte[] arg0, int arg1, int arg2) throws JMSException {
        mDelegate.writeBytes(arg0, arg1, arg2);
    }

    /**
     * @see javax.jms.BytesMessage#writeBytes(byte[])
     */
    public void writeBytes(byte[] arg0) throws JMSException {
        mDelegate.writeBytes(arg0);
    }

    /**
     * @see javax.jms.BytesMessage#writeChar(char)
     */
    public void writeChar(char arg0) throws JMSException {
        mDelegate.writeChar(arg0);
    }

    /**
     * @see javax.jms.BytesMessage#writeDouble(double)
     */
    public void writeDouble(double arg0) throws JMSException {
        mDelegate.writeDouble(arg0);
    }

    /**
     * @see javax.jms.BytesMessage#writeFloat(float)
     */
    public void writeFloat(float arg0) throws JMSException {
        mDelegate.writeFloat(arg0);
    }

    /**
     * @see javax.jms.BytesMessage#writeInt(int)
     */
    public void writeInt(int arg0) throws JMSException {
        mDelegate.writeInt(arg0);
    }

    /**
     * @see javax.jms.BytesMessage#writeLong(long)
     */
    public void writeLong(long arg0) throws JMSException {
        mDelegate.writeLong(arg0);
    }

    /**
     * @see javax.jms.BytesMessage#writeObject(java.lang.Object)
     */
    public void writeObject(Object arg0) throws JMSException {
        mDelegate.writeObject(arg0);
    }

    /**
     * @see javax.jms.BytesMessage#writeShort(short)
     */
    public void writeShort(short arg0) throws JMSException {
        mDelegate.writeShort(arg0);
    }

    /**
     * @see javax.jms.BytesMessage#writeUTF(java.lang.String)
     */
    public void writeUTF(String arg0) throws JMSException {
        mDelegate.writeUTF(arg0);
    }

    /**
     * @see javax.jms.BytesMessage#getBodyLength()
     */
    public long getBodyLength() throws JMSException {
        return mDelegate.getBodyLength();
    }

    /**
     * @see javax.jms.BytesMessage#reset()
     */
    public void reset() throws JMSException {
        mDelegate.reset();
    }

}
