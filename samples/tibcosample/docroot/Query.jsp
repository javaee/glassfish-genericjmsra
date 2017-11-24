<%--

    Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions
    are met:

      - Redistributions of source code must retain the above copyright
        notice, this list of conditions and the following disclaimer.

      - Redistributions in binary form must reproduce the above copyright
        notice, this list of conditions and the following disclaimer in the
        documentation and/or other materials provided with the distribution.

      - Neither the name of Oracle nor the names of its
        contributors may be used to endorse or promote products derived
        from this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
    IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
    THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
    PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
    CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
    EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
    PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
    PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
    LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
    NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
    SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

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

<!--
	Queries and displays the rows of the
	database
-->


<HTML>
<HEAD>
<TITLE>Display All Orders</TITLE>
</HEAD>
<BODY>
<CENTER>
<BR>
<TABLE border="1">
<TR>
<TH>Product Name</TH>
<TH>Quantity</TH>
<TH>Date</TH>
</TR>
<%! java.sql.Connection con; %>
<%! String dbName = "java:comp/env/jdbc/PublisherDB"; %>
<%! 
void makeConnection(JspWriter out)
{
   try
   {
        Context initCtx = new InitialContext();
	DataSource ds = (DataSource)initCtx.lookup(dbName);
	con = ds.getConnection();
   }
   catch (Exception ex) {
        ex.printStackTrace();
	System.out.println("Unable to connect to database. " +
   ex);
   }
}
%>

<!--
	Displays the rows of the database
	to the user
-->

<%!
public void listRows(JspWriter out) throws SQLException
{
  ResultSet rs;
  int rows=0,columns=3,cnt=0;

  String listStatement="select * from publisher";
  PreparedStatement prepStmt = con.prepareStatement(listStatement);
  rs = prepStmt.executeQuery();
  rows = rs.getFetchSize();
  String tableValues[][] = new String[rows+1][columns];
  while(rs.next()) 
  {
      	try{ 
        out.println("<TR>");
      	out.println("<TD>" + rs.getString(1) + "</TD>");
      	out.println("<TD>" + rs.getString(2) + "</TD>");
        out.println("<TD>" + rs.getString(3) + "</TD>");
 	out.println("</TR>");}
        catch(Exception e){e.printStackTrace();}
  }
  rs.close();
}
%>
<%!
private void releaseConnection()
{
    try
    {
	con.close();
    }
    catch (SQLException ex)
    {
       	throw new EJBException("releaseConnection: " + ex.getMessage());
    }
}
%>
<%
	makeConnection(out);
	try
        {
      	listRows(out);
	}
        catch(Exception e)
	{
      	System.out.println(""+e);
	}
        releaseConnection();
%>

</TABLE>
</CENTER>
</BODY>
</HTML>

