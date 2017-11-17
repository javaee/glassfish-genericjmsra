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

package com.sun.genericra.inbound.sync;

import javax.jms.StreamMessage;
import javax.jms.JMSException;

/**
 * See WMessage
 *
 * @author Frank Kieviet
 * @version $Revision: 1.1 $
 */
public class WStreamMessageIn extends WMessageIn implements StreamMessage {
    private StreamMessage mDelegate;
    
    /**
     * Constructor
     * 
     * @param delegate real msg
     * @param ackHandler callback to call when ack() or recover() is called
     * @param ibatch index of this message in a batch; -1 for non-batched
     */
    public WStreamMessageIn(StreamMessage delegate, AckHandler ackHandler, int ibatch) {
        super(delegate, ackHandler, ibatch);
        mDelegate = delegate;
    }

    /**
     * @see javax.jms.StreamMessage#readByte()
     */
    public byte readByte() throws JMSException {
        return mDelegate.readByte();
    }

    /**
     * @see javax.jms.StreamMessage#readBytes(byte[])
     */
    public int readBytes(byte[] arg0) throws JMSException {
        return mDelegate.readBytes(arg0);
    }

    /**
     * @see javax.jms.StreamMessage#readChar()
     */
    public char readChar() throws JMSException {
        return mDelegate.readChar();
    }

    /**
     * @see javax.jms.StreamMessage#readDouble()
     */
    public double readDouble() throws JMSException {
        return mDelegate.readDouble();
    }

    /**
     * @see javax.jms.StreamMessage#readFloat()
     */
    public float readFloat() throws JMSException {
        return mDelegate.readFloat();
    }

    /**
     * @see javax.jms.StreamMessage#readInt()
     */
    public int readInt() throws JMSException {
        return mDelegate.readInt();
    }

    /**
     * @see javax.jms.StreamMessage#readLong()
     */
    public long readLong() throws JMSException {
        return mDelegate.readLong();
    }

    /**
     * @see javax.jms.StreamMessage#readObject()
     */
    public Object readObject() throws JMSException {
        return mDelegate.readObject();
    }

    /**
     * @see javax.jms.StreamMessage#readShort()
     */
    public short readShort() throws JMSException {
        return mDelegate.readShort();
    }

    /**
     * @see javax.jms.StreamMessage#readString()
     */
    public String readString() throws JMSException {
        return mDelegate.readString();
    }

    /**
     * @see javax.jms.StreamMessage#reset()
     */
    public void reset() throws JMSException {
        mDelegate.reset();
    }

    /**
     * @see javax.jms.StreamMessage#writeBoolean(boolean)
     */
    public void writeBoolean(boolean arg0) throws JMSException {
        mDelegate.writeBoolean(arg0);
    }

    /**
     * @see javax.jms.StreamMessage#writeByte(byte)
     */
    public void writeByte(byte arg0) throws JMSException {
        mDelegate.writeByte(arg0);
    }

    /**
     * @see javax.jms.StreamMessage#writeBytes(byte[], int, int)
     */
    public void writeBytes(byte[] arg0, int arg1, int arg2) throws JMSException {
        mDelegate.writeBytes(arg0, arg1, arg2);
    }

    /**
     * @see javax.jms.StreamMessage#writeBytes(byte[])
     */
    public void writeBytes(byte[] arg0) throws JMSException {
        mDelegate.writeBytes(arg0);
    }

    /**
     * @see javax.jms.StreamMessage#writeChar(char)
     */
    public void writeChar(char arg0) throws JMSException {
        mDelegate.writeChar(arg0);
    }

    /**
     * @see javax.jms.StreamMessage#writeDouble(double)
     */
    public void writeDouble(double arg0) throws JMSException {
        mDelegate.writeDouble(arg0);
    }

    /**
     * @see javax.jms.StreamMessage#writeFloat(float)
     */
    public void writeFloat(float arg0) throws JMSException {
        mDelegate.writeFloat(arg0);
    }

    /**
     * @see javax.jms.StreamMessage#writeInt(int)
     */
    public void writeInt(int arg0) throws JMSException {
        mDelegate.writeInt(arg0);
    }

    /**
     * @see javax.jms.StreamMessage#writeLong(long)
     */
    public void writeLong(long arg0) throws JMSException {
        mDelegate.writeLong(arg0);
    }

    /**
     * @see javax.jms.StreamMessage#writeObject(java.lang.Object)
     */
    public void writeObject(Object arg0) throws JMSException {
        mDelegate.writeObject(arg0);
    }

    /**
     * @see javax.jms.StreamMessage#writeShort(short)
     */
    public void writeShort(short arg0) throws JMSException {
        mDelegate.writeShort(arg0);
    }

    /**
     * @see javax.jms.StreamMessage#writeString(java.lang.String)
     */
    public void writeString(String arg0) throws JMSException {
        mDelegate.writeString(arg0);
    }

    /**
     * @see javax.jms.StreamMessage#readBoolean()
     */
    public boolean readBoolean() throws JMSException {
        return mDelegate.readBoolean();
    }

}
