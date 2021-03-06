package group12.huskerdining;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class MenuActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        int menuId = getIntent().getIntExtra("Menu Id",0);

        ConnectDB connect = new ConnectDB();
        try{
            ArrayList<ArrayList<Object>> hallNameList = connect.execute("SELECT name FROM Hall " +
                    "JOIN Menu on hall_id = Hall.id " +
                    "WHERE Menu.id = " + menuId).get();
            final String hallName = (String)hallNameList.get(0).get(0);
            TextView name = (TextView)findViewById(R.id.diningHall_menu);
            name.setText(hallName);

            ConnectDB connect2 = new ConnectDB();
            ArrayList<Object> menuInfo= connect2.execute("Select fromTime, toTime from Menu where id = " + menuId).get().get(0);
            TextView menuInfoText = (TextView)findViewById(R.id.meal_date);
            menuInfoText.setText(menuInfo.get(0) + " - " + menuInfo.get(1));

            ConnectDB connect3 = new ConnectDB();
            ArrayList<ArrayList<Object>> menuItems = connect3.execute("SELECT name, category, id FROM ItemOnMenu "
                    + "JOIN MenuItem on item_id = MenuItem.id "
                    + "WHERE menu_id = " + menuId).get();

            final ArrayList<String> menuList = new ArrayList<String>();
            final ArrayList<Integer> itemIds = new ArrayList<Integer>();

            String currentCategory = "";
            for (ArrayList<Object> row : menuItems) {
                String category = (String)row.get(1);
                if(!category.equals(currentCategory)){
                    currentCategory = category;
                    menuList.add("---" + currentCategory + "---");
                    itemIds.add(0);
                }
                menuList.add((String)row.get(0));
                itemIds.add((Integer)row.get(2));
            }

            ListView listview = (ListView)findViewById(R.id.food_list);
            ArrayAdapter adapter = new ArrayAdapter(this,
                    android.R.layout.simple_list_item_1, menuList);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int itemId = itemIds.get(position);
                    if(itemId != 0){
                        Intent intent = new Intent(view.getContext(), RatingActivity.class);
                        intent.putExtra("itemId",itemId);
                        intent.putExtra("itemName",menuList.get(position));
                        intent.putExtra("hallName",hallName);
                        startActivity(intent);
                    }
                }
            });
            listview.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
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
