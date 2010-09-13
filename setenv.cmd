REM Convenience script for Windows which sets the environment so you can run a build and run tests
REM You will need to change this to appropriate directories for your installation

REM Set environment so you can perform a build

SET RA_HOME=C:\mq\genericjmsra

REM Set J2EE_JAR to a JavaEE 1.4 version of j2ee.jar
SET J2EE_JAR=C:\Programs\JavaEE1.4SDK\lib\j2ee.jar

REM Set Environment so you can run tests

SET S1AS_HOME=C:\Sun\glassfish-2.1.2-v04c
REM SET S1AS_HOME=C:\Sun\glassfish-3.0.1\glassfish

SET PATH=%S1AS_HOME%/bin;%PATH%