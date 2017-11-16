<%--

    DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

    Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.

    The contents of this file are subject to the terms of either the GNU
    General Public License Version 2 only ("GPL") or the Common Development
    and Distribution License("CDDL") (collectively, the "License").  You
    may not use this file except in compliance with the License.  You can
    obtain a copy of the License at
    https://oss.oracle.com/licenses/CDDL+GPL-1.1
    or LICENSE.txt.  See the License for the specific
    language governing permissions and limitations under the License.

    When distributing the software, include this License Header Notice in each
    file and include the License file at LICENSE.txt.

    GPL Classpath Exception:
    Oracle designates this particular file as subject to the "Classpath"
    exception as provided by Oracle in the GPL Version 2 section of the License
    file that accompanied this code.

    Modifications:
    If applicable, add the following below the License Header, with the fields
    enclosed by brackets [] replaced by your own identifying information:
    "Portions Copyright [year] [name of copyright owner]"

    Contributor(s):
    If you wish your version of this file to be governed by only the CDDL or
    only the GPL Version 2, indicate your decision by adding "[Contributor]
    elects to include this software in this distribution under the [CDDL or GPL
    Version 2] license."  If you don't indicate a single choice of license, a
    recipient has the option to distribute your version of this file under
    either the CDDL, the GPL Version 2 or to extend the choice of license to
    its licensees as provided above.  However, if you add GPL Version 2 code
    and therefore, elected the GPL Version 2 license, then the option applies
    only if the new code is made subject to such option by the copyright
    holder.

--%>

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

