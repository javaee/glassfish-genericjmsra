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

