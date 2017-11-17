<!--
   Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
  
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
  
        http://www.apache.org/licenses/LICENSE-2.0
  
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
    See the License for the specific language governing permissions and
    limitations under the License.
  
-->

<%@ page session="false" %>
<%@ page import="javax.ejb.EJBHome"%>
<%@ page import="javax.naming.*"%>;
<%@ page import="javax.rmi.PortableRemoteObject"%>
<%@ page import="java.awt.*"%>
<%@ page import="java.awt.event.*"%>
<%@ page import="javax.swing.*"%>
<%@ page import="javax.ejb.*"%>
<%@ page import="javax.naming.*"%>
<%@ page import="javax.jms.*"%>
<%@ page import="java.util.logging.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="javax.sql.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.math.*"%>
<%@ page import="hello.*"%>

<html>
<head>
<title>Accept Publisher</title>
<body>
<!-- 
	accept the order data from the user
	and call the publisher bean method publishnews()
-->

<%
String name=request.getParameter("name");
String quantity=request.getParameter("quantity");
String date=request.getParameter("date");
try
{
        Context ic = new InitialContext();
        java.lang.Object objref =
        ic.lookup("java:comp/env/ejb/remote/Publisher");
        PublisherHome pubHome = (PublisherHome) PortableRemoteObject.narrow(objref,PublisherHome.class);
        PublisherRemote phr = pubHome.create();
        phr.publishNews(name,quantity,date);
        phr.remove();
	out.println("<b><i>The details have been entered in the databasei</b></i>");
}
catch (Exception ex)
{
        ex.printStackTrace();
}

%>
</body>
</html>

