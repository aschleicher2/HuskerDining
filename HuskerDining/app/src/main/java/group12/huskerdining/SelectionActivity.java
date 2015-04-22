package group12.huskerdining;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.Calendar;
import java.util.Date;

import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.DatePicker;
import android.widget.ArrayAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class SelectionActivity extends ActionBarActivity {
    String menu_type="";
    String dining_hall="";
    long meal_date=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        Button selectionButton = (Button)findViewById(R.id.button_getMenu);
        selectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MenuActivity.class);
                Spinner spinner_hall = (Spinner) findViewById(R.id.spinner_diningHall);
                dining_hall = spinner_hall.getSelectedItem().toString();
                Spinner spinner_type = (Spinner) findViewById(R.id.spinner_meal);
                menu_type = spinner_type.getSelectedItem().toString();
                DatePicker spinner_date = (DatePicker) findViewById(R.id.datePicker);
//                meal_date = spinner_date.getCalendarView().getDate();


//                Date date = new Date(meal_date);                      // timestamp now
                Calendar cal = Calendar.getInstance();       // get calendar instance
                cal.set(spinner_date.getYear(), spinner_date.getMonth(), spinner_date.getDayOfMonth());
//                cal.setTime(date);                           // set cal to date
                cal.set(Calendar.HOUR, 0);                   // set hour to midnight
                cal.set(Calendar.MINUTE, 0);                 // set minute in hour
                cal.set(Calendar.SECOND, 0);                 // set second in minute
                cal.set(Calendar.MILLISECOND, 0);            // set millis in second
                cal.set(Calendar.AM_PM, Calendar.AM);
                Date meal_date_at_midnight = cal.getTime();

                Log.v("Spinner ", dining_hall);
                Log.v("Spinner ", menu_type);
                Log.v("Spinner ", String.valueOf(meal_date_at_midnight.getTime()));
               // Log.v("Spinner ", date);

                ConnectDB connect = new ConnectDB();

                StringBuilder hall_query = new StringBuilder("select id from Hall where name='");
                hall_query.append(dining_hall);
                hall_query.append("';");
                ArrayList<Object> hall_return = null;

                try {
                    hall_return = connect.execute(hall_query.toString()).get().get(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                ArrayList<Object> menu_id = null;
                try {
                    //hall_return = connect.execute("select", hall_query.toString()).get();
                    StringBuilder menu_id_query = new StringBuilder("select id from Menu where hall_id=");
                    menu_id_query.append(((Integer) hall_return.get(0)));
                    menu_id_query.append(" and date=");
                    menu_id_query.append(meal_date_at_midnight.getTime());
                    menu_id_query.append(" and meal='");
                    menu_id_query.append(menu_type);
                    menu_id_query.append("';");

                    Log.v("Menu query", menu_id_query.toString());

                    ConnectDB connect2 = new ConnectDB();
                    menu_id = connect2.execute(menu_id_query.toString()).get().get(0);

                    Log.v("Menu id", menu_id.get(0).toString());
                    intent.putExtra("Menu Id", ((Integer)menu_id.get(0)));
                    startActivity(intent);
                } catch (Exception e){
                    Toast noMenu = Toast.makeText(getApplicationContext(), "No menu for that day!", Toast.LENGTH_LONG);
                    noMenu.show();
                    e.printStackTrace();
                }


            }
        });

       addItemsOnTypeSpinner();
       addItemsOnHallSpinner();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_selection, menu);
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

    public void addItemsOnTypeSpinner(){
        Spinner meal_type_dropdown = (Spinner)findViewById(R.id.spinner_meal);
        String[] items = new String[]{"Breakfast", "Lunch", "Dinner"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        meal_type_dropdown.setAdapter(adapter);
        //meal_type_dropdown.setOnItemSelectedListener(new itemSelectedListener());
    }

    public void addItemsOnHallSpinner(){
        Spinner dining_hall_dropdown = (Spinner)findViewById(R.id.spinner_diningHall);

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

       String[] items2 = new String[hall_names.size()];
        for(int i=0; i<hall_names.size(); i++){
            items2[i] = hall_names.get(i).toString();
        }
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items2);
        dining_hall_dropdown.setAdapter(adapter2);
    }



}
