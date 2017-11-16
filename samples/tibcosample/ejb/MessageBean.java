/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004-2017 Oracle and/or its affiliates. All rights reserved.
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

import javax.ejb.*;
import javax.naming.*;
import javax.jms.*;
import java.util.logging.*;
import java.sql.*;
import javax.sql.*;
import java.util.*;
import java.math.*;
import hello.*;

/*
	This class implements the MessageDriven Bean
	that asynchronously listens for messages 
	at the message queue 
*/

public class MessageBean implements MessageDrivenBean, MessageListener {
    private final static String dbName = "java:comp/env/jdbc/PublisherDB";
    private String Message;
    private java.sql.Connection con;
    private Context context;
    static final Logger logger = Logger.getLogger("MessageBean");
    private transient MessageDrivenContext mdc = null;

    public MessageBean() {
        logger.info("In MessageBean.MessageBean()");
    }

    public void setMessageDrivenContext(MessageDrivenContext mdc) {
        logger.info("In MessageBean.setMessageDrivenContext()");
        this.mdc = mdc;
    }

    public void ejbCreate() {
        logger.info("In MessageBean.ejbCreate()");
    }

    /*
	This method is invoked by the message driven bean
	when the message bean detects the message in the 
	queue and sends the data to the database
    */ 

    public void onMessage(Message inMessage) {
        try {
		if (inMessage instanceof ObjectMessage) {
                ObjectMessage msg =  (ObjectMessage)inMessage;
                Object incoming =  msg.getObject(); 
                OrderMessage incoming_ = (OrderMessage)incoming;        
                insertRow(incoming_.name,incoming_.quantity,incoming_.date);
            } else {
                logger.warning("Message of wrong type: " +
                    inMessage.getClass().getName());
            }
        } catch (JMSException e) {
            logger.severe("MessageBean.onMessage: JMSException: " +
                e.toString());
            e.printStackTrace();
            mdc.setRollbackOnly();
        } catch (Throwable te) {
            logger.severe("MessageBean.onMessage: Exception: " + te.toString());
            te.printStackTrace();
        }
    }

    public void ejbRemove() {
        logger.info("In MessageBean.remove()");
    }

/****************************************************************************/
/*                           Database Connections                          */
/***************************************************************************/


    private void makeConnection() {
        try {
            InitialContext ic = new InitialContext();
            DataSource ds = (DataSource) ic.lookup(dbName);

            con = ds.getConnection();
        } catch (Exception ex) {
            throw new EJBException("Unable to connect to database. " +
                ex.getMessage());
        }
    }

    private void releaseConnection() {
        try {
            con.close();
        } catch (SQLException ex) {
            throw new EJBException("releaseConnection: " + ex.getMessage());
        }
    }

    private void insertRow(String name,String quantity,String date) throws SQLException
    {        
        makeConnection();

        String insertStatement =
            "insert into publisher values ( ?,?,? )";
        PreparedStatement prepStmt = con.prepareStatement(insertStatement);

        prepStmt.setString(1,name );
        prepStmt.setString(2,quantity);
        prepStmt.setString(3,date);
        

        prepStmt.executeUpdate();
        prepStmt.close();
        releaseConnection();
    }


/**************************************************************************/
}
