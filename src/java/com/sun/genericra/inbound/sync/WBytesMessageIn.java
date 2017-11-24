/*
 * Copyright (c) 2003-2017 Oracle and/or its affiliates. All rights reserved.
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
