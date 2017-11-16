/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
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
