package group12.huskerdining;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;


public class InfoActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void connectBDMySQL () {
        Connection conn = null;
        Properties prop = new Properties();
        InputStream input = null;

        String dbURL = null;
        String dbUser = null;
        String dbPass = null;
        try {
            input = new FileInputStream("config.properties");
            prop.load(input);
            dbURL = prop.getProperty("dbURL");
            dbUser = prop.getProperty("dbUser");
            dbPass = prop.getProperty("dbPass");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String driver = "com.mysql.jdbc.Driver";
            Class.forName(driver).newInstance();

            conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
            Statement query = conn.createStatement();
            ResultSet output = query.executeQuery("select name, address, phone, manager from Hall where id=1");

            while(output.next()){
                EditText newName = (EditText)findViewById(R.id.hall_name);
                newName.setText(output.getString(1), TextView.BufferType.EDITABLE);
                EditText newAddress = (EditText)findViewById(R.id.hall_name);
                newAddress.setText(output.getString(2), TextView.BufferType.EDITABLE);
                EditText newHours = (EditText)findViewById(R.id.hall_name);
                newHours.setText(output.getString(3), TextView.BufferType.EDITABLE);
                EditText newManager = (EditText)findViewById(R.id.hall_name);
                newManager.setText(output.getString(4), TextView.BufferType.EDITABLE);
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
    }
}
