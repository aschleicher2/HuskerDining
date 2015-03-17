package group12.huskerdining;

import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.xml.transform.Result;

/**
 * Created by shortysporty17 on 3/16/15.
 */
public class ConnectDB {

    public ResultSet connectDB(String query) {
        Connection conn = null;
        ResultSet output = null;
        Properties prop = new Properties();
        InputStream input = null;

        try {
            Log.v("In this method", "here");
            String dbUser="askinner";
            String dbPass="t2CJjH6MXn5FAPpB5cwYWoNcrmFs8bkq";
            String dbURL="jdbc:mysql://cse.unl.edu:3306/askinner";
            String driver = "com.mysql.jdbc.Driver";
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
            Statement statement = conn.createStatement();
            output = statement.executeQuery(query);

            conn.close();

        } catch (SQLException e) {

            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return output;
    }
}
