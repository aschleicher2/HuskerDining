package group12.huskerdining;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;


public class RatingActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        Button submitButton = (Button)findViewById(R.id.button_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RatingBar yourRating = (RatingBar)findViewById(R.id.ratingBar2);
                ConnectDB connectDB = new ConnectDB();
                connectDB.execute("INSERT INTO Rating (item_id,number) VALUES (1, " +  yourRating.getRating() + ")");
                finish();
            }
        });

        RatingBar averageRating = (RatingBar)findViewById(R.id.ratingBar);
        ConnectDB connectDB = new ConnectDB();
        try{
            int average = Integer.parseInt((String) connectDB.execute("SELECT avg(number) FROM Rating WHERE item_id = 1").get().get(0).get(0));
            averageRating.setNumStars(average);
        } catch (Exception e){

        }

        int rating = 0;


    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rating, menu);
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
