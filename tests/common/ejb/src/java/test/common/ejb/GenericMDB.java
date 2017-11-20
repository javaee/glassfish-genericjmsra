/*
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
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

package test.common.ejb;

public class GenericMDB {
	
    // set this to true if you want to monitor number of MDBs in use at a time
    // typically this will increase if onMessage() performs a sleep
    protected boolean monitorNoOfBeansInUse=false;

    // set this to true to log the time taken to process each each batch of messages (across all instances of this MDB)
    // batchsize is set below
    protected boolean reportThroughput=true;
	
	// every time batchsize messages are processed, the time taken is reported
    private static int batchsize=100;
	
    private static int beansInUseCount=0;
    protected synchronized static void incrementBeansInUseCount(){
        beansInUseCount++;
        System.out.println("No of beans in use now: " + beansInUseCount);
    }

    protected synchronized static void decrementBeansInUseCount(){
        beansInUseCount--;
        System.out.println("No of beans in use now: " + beansInUseCount);
    }

    private static long messageCount=0;
    private static long startTime;
    protected static void updateMessageCount(){

        long thisMessageCount = incrementMessageCount();
        if (thisMessageCount==1){
            startTime =System.currentTimeMillis();
        }
        if ((thisMessageCount % batchsize)==0){
            float timeTakenMillis = System.currentTimeMillis()-startTime;
            float timeTakenSecs=timeTakenMillis/1000;
            float rate = batchsize/timeTakenSecs;
            System.out.println("Total messages is "+thisMessageCount+" messages, time for last "+batchsize+" was "+timeTakenSecs+" sec, rate was "+rate+" msgs/sec");
            startTime =System.currentTimeMillis();
        }
    }

    private synchronized static long incrementMessageCount(){
        messageCount++;
        return messageCount;
    }

}
