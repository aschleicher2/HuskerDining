package group12.huskerdining;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class InfoActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        setHallProperties();
        setHallHours();
    }

    private void setHallHours() {
        ConnectDB connect = new ConnectDB();
        ArrayList<Object> breakfast_return = null;

        Calendar c = Calendar.getInstance();
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.HOUR,0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.AM_PM, Calendar.AM);

        try{
            ArrayList<ArrayList<Object>> mealHours = connect.execute("SELECT meal, fromTime, toTime FROM Menu WHERE date = " + c.getTimeInMillis()  +" AND hall_id = 1").get();
            for(ArrayList<Object> meal : mealHours){

                if(meal.get(0).equals("Breakfast")){
                    TextView breakfastHours = (TextView)findViewById(R.id.breakfast_hours);
                    breakfastHours.setText(meal.get(1) + " - " + meal.get(2));
                } else if (meal.get(0).equals("Lunch")){
                    TextView lunchHours = (TextView)findViewById(R.id.lunch_hours);
                    lunchHours.setText(meal.get(1) + " - " + meal.get(2));
                } else if (meal.get(0).equals("Dinner")){
                    TextView dinnerHours = (TextView)findViewById(R.id.dinner_hours);
                    dinnerHours.setText(meal.get(1) + " - " + meal.get(2));
                }
            }
        } catch (Exception e) {

        }
    }

    private void setHallProperties() {
        ConnectDB connect = new ConnectDB();
        ArrayList<Object> returnSet = null;
        try{
            returnSet = connect.execute("select name, address, phone, manager from Hall where id=1").get().get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            //Log.v("Return address", (String)returnSet.get(1));
            TextView name = (TextView)findViewById(R.id.name_info);
            name.setText((String)returnSet.get(0));
            TextView address = (TextView)findViewById(R.id.address_info);
            address.setText((String)returnSet.get(1));
            TextView hours = (TextView)findViewById(R.id.phone_info);
            hours.setText((String)returnSet.get(2));
            TextView manager = (TextView)findViewById(R.id.manager_info);
            manager.setText((String) returnSet.get(3));



        } catch (Exception e) {

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
