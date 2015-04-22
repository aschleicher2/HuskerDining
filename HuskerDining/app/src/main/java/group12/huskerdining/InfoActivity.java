package group12.huskerdining;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class InfoActivity extends ActionBarActivity {

    private String[] spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        setHallNameSpinner();


        Spinner spinner_select = (Spinner) findViewById(R.id.spinner_name);
        spinner_select.setOnItemSelectedListener(
            new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    Object item = parent.getItemAtPosition(pos);
                    setHallProperties(item.toString());
                    setHallHours(item.toString());
                }
                public void onNothingSelected(AdapterView<?> parent) {
                }
         });

     }



    private void setHallProperties(String dining_hall_name) {

        ConnectDB connect = new ConnectDB();

        StringBuilder hall_info_query = new StringBuilder("select address, phone, manager from Hall where name= '");
        hall_info_query.append(dining_hall_name);
        hall_info_query.append("';");

        Log.v("Query", hall_info_query.toString());

        ArrayList<Object> returnSet = null;
        try {
            returnSet = connect.execute(hall_info_query.toString()).get().get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            Log.v("Return Info", returnSet.get(0).toString());
            TextView address = (TextView)findViewById(R.id.address_info);
            address.setText((String)returnSet.get(0));
            TextView hours = (TextView)findViewById(R.id.phone_info);
            hours.setText((String)returnSet.get(1));
            TextView manager = (TextView)findViewById(R.id.manager_info);
            manager.setText((String) returnSet.get(2));
        } catch (Exception e) {

        }
    }

    private void setHallNameSpinner() {
        ConnectDB connect = new ConnectDB();

        StringBuilder hall_query = new StringBuilder("select name from Hall;");
        ArrayList<String> hall_names = new ArrayList<>();
        ArrayList<ArrayList<Object>> temp= null;
        try {
            temp = connect.execute(hall_query.toString()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        for(int i=0; i<temp.size(); i++){
            hall_names.add(i,temp.get(i).get(0).toString());
            Log.v("Hall name", hall_names.get(i));
        }

        this.spinner = new String[hall_names.size()];
        for(int i=0; i<hall_names.size(); i++){
            spinner[i] = hall_names.get(i).toString();
        }

        Spinner s = (Spinner) findViewById(R.id.spinner_name);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, spinner);
        s.setAdapter(adapter);
    }
    private void setHallHours(String hall_name) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.HOUR,0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.AM_PM, Calendar.AM);

        ConnectDB connect = new ConnectDB();
        int hall_id = 0;
        try {
            hall_id = (int) connect.execute("select id from Hall where name= '" + hall_name + "';").get().get(0).get(0);
            Log.v("Hall id", String.valueOf(hall_id));
        } catch (Exception e) {

        }

        ConnectDB connect2 = new ConnectDB();
        ArrayList<ArrayList<Object>> mealHours = null;
        try {
            mealHours = connect2.execute("SELECT meal, fromTime, toTime FROM Menu WHERE date = " + c.getTimeInMillis()  +" AND hall_id = "+hall_id+";").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        TextView breakfastHours = (TextView)findViewById(R.id.breakfast_hours);
        breakfastHours.setText("Closed");
        TextView lunchHours = (TextView)findViewById(R.id.lunch_hours);
        lunchHours.setText("Closed");
        TextView dinnerHours = (TextView)findViewById(R.id.dinner_hours);
        dinnerHours.setText("Closed");
        for(int i=0; i<mealHours.size(); i++){
                ArrayList<Object> meal = mealHours.get(i);
                Log.v("meal type", meal.get(0).toString());
                Log.v("from time", meal.get(1).toString());
                Log.v("to time", meal.get(2).toString());

                if(meal.get(0).equals("Breakfast")){
                    breakfastHours.setText(meal.get(1) + " - " + meal.get(2));
                } else if (meal.get(0).equals("Lunch")){
                    lunchHours.setText(meal.get(1) + " - " + meal.get(2));
                } else if (meal.get(0).equals("Dinner")){
                    dinnerHours.setText(meal.get(1) + " - " + meal.get(2));
                }
            }

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

}
