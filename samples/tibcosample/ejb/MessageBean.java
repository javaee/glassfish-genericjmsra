/**
 * Copyright 2004-2005 Sun Microsystems, Inc.
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
