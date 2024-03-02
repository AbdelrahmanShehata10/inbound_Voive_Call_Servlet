package newpackage;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageReader;
import com.twilio.twiml.TwiMLException;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Say;
import com.twilio.type.PhoneNumber;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;

@SuppressWarnings("serial")
public class IncomingCallServlet extends HttpServlet {

    private DAO dao;
    private ServletContext servletContext;

    @Override
    public void init() throws ServletException {
        super.init();
        servletContext = getServletContext();
        dao = new DAO(servletContext);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(IncomingCallServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(IncomingCallServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(IncomingCallServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(IncomingCallServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException, ClassNotFoundException {

       try {
    ResultSet resultSet = dao.getDataFromDbForTwilio();
    if (resultSet != null) {
        try {
            while (resultSet.next()) {
                String ACCOUNT_SID = resultSet.getString("ACCOUNT_SID");
                String AUTH_TOKEN = resultSet.getString("AUTH_TOKEN");
                // Initialize Twilio with obtained credentials
                Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            }
        } finally {
            dao.closeResultSet(resultSet); // Closing ResultSet
        }
    } else {
        throw new SQLException("ResultSet is null");
    }
} catch (SQLException e) {
    e.printStackTrace(); // Handle the exception as needed
}


        // Assuming you have access to the HttpServletRequest object
        String accountSid = request.getParameter("account_sid");
        String answeredBy = request.getParameter("answered_by");
      
        insertMessageIntoDatabase(accountSid, answeredBy);
        System.out.println(accountSid);
        // Insert message into the database
        Say say = new Say.Builder("Hello world!").build();
        VoiceResponse twiml = new VoiceResponse.Builder().say(say).build();

        // Render TwiML as XML
        response.setContentType("text/xml");

        try {
            response.getWriter().print(twiml.toXml());
        } catch (TwiMLException e) {
            e.printStackTrace();
        }
    }

    private void insertMessageIntoDatabase(String accountSID,String answeredBy) {
        Connection connection;
        PreparedStatement statement = null;

        try {
            connection = (Connection) getServletContext().getAttribute("connection");

String sql = "INSERT INTO calls (account_sid, answered_by) VALUES (?, ?)";
            statement = connection.prepareStatement(sql);

            // Set values for parameters
            statement.setString(1, accountSID);
            statement.setString(2, answeredBy);
          

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new call record has been inserted into the database.");
            }
        } catch (SQLException e) {
            // Handle SQL exceptions
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
