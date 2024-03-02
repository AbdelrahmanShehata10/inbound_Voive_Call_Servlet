package newpackage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletContext;

public class DAO {
    private ServletContext servletContext;

    public DAO(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    ResultSet getDataFromDbForTwilio() throws SQLException, ClassNotFoundException {
           Class.forName("org.postgresql.Driver");
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = (Connection) servletContext.getAttribute("connection");
            preparedStatement = connection.prepareStatement("SELECT * FROM credentials");
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultSet;
    
    }

    public static void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
