/**
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
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
