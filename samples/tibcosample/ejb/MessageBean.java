/*
 * Copyright (c) 2004-2017 Oracle and/or its affiliates. All rights reserved.
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
