package com.gezinote.android.activity;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.gezinote.android.data.DatabaseHelper;
import com.gezinote.android.R;
import com.gezinote.android.adapter.TripAdapter;
import com.gezinote.android.helper.SessionManager;
import com.gezinote.android.model.Trip;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    List<Trip> tripList = new ArrayList<Trip>();
    TripAdapter tripAdaptor;
    ListView lv;
    FloatingActionButton fab;

    DatabaseHelper db;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SqLite database handler
        db = new DatabaseHelper(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        lv = (ListView) findViewById(R.id.trip_list);
        lv.setOnItemClickListener(viewNoteListener);

        tripList = db.getTrips();

        /*************** FLOATING ACTION POINTS ************************/

        findViewById(R.id.fab_main_newTrip).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddTripActivity.class));
            }
        });
        findViewById(R.id.fab_main_newNote).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddNoteActivity.class));
            }
        });

        if(tripList.size()==0){
            Toast.makeText(getApplicationContext(), getString(R.string.main__no_trip), Toast.LENGTH_LONG).show();
        }else{
            tripAdaptor = new TripAdapter(this, tripList);
            lv.setAdapter(tripAdaptor);
        }
    }
    // Capture ListView item click
    OnItemClickListener viewNoteListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            int tripID = tripList.get(position).getId();

            Intent i = new Intent(getApplicationContext(), ShowNotesActivity.class);
            i.putExtra("id", tripID);
            startActivity(i);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mItem_trip_logout:
                logoutUser();
                return true;
            default:
                return true;
        }
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser()
    {
        session.setLogin(false);

        db.deleteUsers();
        // Launching the login activity
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }
}
