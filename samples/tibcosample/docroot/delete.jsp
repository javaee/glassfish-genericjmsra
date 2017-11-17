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

<!-- 
connects to and clears the database
does not do selective deletion but
deletes the database as a whole
-->


<%! java.sql.Connection con; %>
<%! String dbName = "java:comp/env/jdbc/PublisherDB"; %>
<%! 
void makeConnection(JspWriter out)
{
   try
   {
        Properties p = new Properties();

        Context initCtx = new InitialContext(p);
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
	The function that actually performs 
	the deletion
-->

<%!
public void deleteRows(JspWriter out) throws SQLException
{
  ResultSet rs;
  int rows=0,columns=3,cnt=0;

  String listStatement="delete from publisher";
  PreparedStatement prepStmt = con.prepareStatement(listStatement);
  prepStmt.executeUpdate();
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
      	deleteRows(out);
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

