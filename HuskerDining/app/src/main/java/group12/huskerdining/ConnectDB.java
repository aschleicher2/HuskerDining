package group12.huskerdining;

import android.os.AsyncTask;
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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.transform.Result;

/**
 * Created by shortysporty17 on 3/16/15.
 */
public class ConnectDB extends AsyncTask<String, Void, ArrayList<ArrayList<Object>>> {

    @Override
    protected ArrayList<ArrayList<Object>> doInBackground(String... params) {
        if(params[0].toLowerCase().startsWith("select")){
            return selectQuery(params[0]);
        }
        return null;
    }

    public static ArrayList<ArrayList<Object>> selectQuery(String query) {
        ArrayList<ArrayList<Object>> rows = new ArrayList<ArrayList<Object>>();
        Connection conn = null;
        ResultSet rs = null;

        try {
            String dbUser="askinner";
            String dbPass="skc94fan";
            String dbURL="jdbc:mysql://cse.unl.edu:3306/askinner";
            String driver = "com.mysql.jdbc.Driver";
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
            Statement statement = conn.createStatement();
            rs = statement.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            int numColumns = rsmd.getColumnCount();

            while(rs.next()){
                ArrayList<Object> row = new ArrayList<Object>();
                for (int i = 0; i < numColumns; i++) {
                    row.add(rs.getObject(i+1));
                }
                rows.add(row);
            }


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
        return rows;
    }
}
