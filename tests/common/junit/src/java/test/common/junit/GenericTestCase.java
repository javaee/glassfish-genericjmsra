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

package test.common.junit;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.TestCase;

public class GenericTestCase extends TestCase{
	
	/**
	 * Copy test.out to standard output, searching for the words "PASS" or "FAIL". 
	 * If PASS found, the test is considered to have passed and the method returns normally
	 * If PASS not found, or if FAIL found, the test fails.
	 * 
	 * @throws IOException
	 */
	protected void printAndCheckTestOutput() throws FileNotFoundException, IOException {
		String testDirectoryProperty = "testDirectory";
		String testDirectoryName =System.getProperty(testDirectoryProperty);
		if (testDirectoryName==null){
			fail("Test config error: system property "+testDirectoryProperty+" not set");
		}
		
		boolean failed = true;
		boolean failFound=false;
		boolean passFound=false;
		String lineOfInterest=null;

		BufferedReader in = new BufferedReader(new FileReader(testDirectoryName+"/test.out"));
		try {
			String line;
			while ((line = in.readLine()) != null) {
				System.out.println(line);
				if (line.indexOf("PASS") != -1) {
					System.out.println("The word PASS was detected: the test must have passed");
					passFound=true;
					lineOfInterest=line;
					failed = false;
				}
				if (line.indexOf("FAIL") != -1) {
					System.out.println("The word FAIL was detected: the test must have failed");
					failFound=true;
					lineOfInterest=line;
					failed = true;
				}
			}
		} finally {
			in.close();
		}
		
		if (passFound) {
			System.out.println("The word PASS was found in "+lineOfInterest);
		}
		if (failFound) {
			System.out.println("The word FAIL was found in "+lineOfInterest);
		}
		
		if (failed) {
			if (failFound){
				fail("The word FAIL was found in test.out");
			} else {
				fail("The word PASS was not found in test.out");
			}
		}
	}

}
