package com.gezinote.android.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gezinote.android.data.DatabaseHelper;
import com.gezinote.android.R;
import com.gezinote.android.model.TripSpinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class AddNoteActivity extends ActionBarActivity implements View.OnClickListener {

    Spinner spinTrips;
    List<TripSpinner> tripSpinnerList = new ArrayList<TripSpinner>();

    EditText etContent;
    TextView tvDate, tvTime;
    int tripId, noteId, noteYear, noteMonth, noteDay, noteHour, noteMinute;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private SimpleDateFormat dateFormatter, timeFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setCustomView(R.layout.actionbar_add_note);
        mActionBar.setDisplayShowCustomEnabled(true);

        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        noteId = getIntent().getIntExtra("id", 0);

        // Spinner Drop down elements
        tripSpinnerList = db.getTripSpinner();
        // Creating adapter for spinner with a custom spinner style: (by default, android.R.layout.simple_spinner_item)
        ArrayAdapter<TripSpinner> dataAdapter = new ArrayAdapter<TripSpinner>(this, R.layout.layout_spinner_trip, tripSpinnerList);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinTrips = (Spinner) findViewById(R.id.spin_addNote_trip);
        spinTrips.setAdapter(dataAdapter);

        tvDate = (TextView)findViewById(R.id.tv_addNote_date);
        tvTime = (TextView)findViewById(R.id.tv_addNote_time);
        etContent = (EditText)findViewById(R.id.et_addNote_content);

        tvDate.setOnClickListener(this);
        tvTime.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("dd MMM yyyy EEEE");
        timeFormatter = new SimpleDateFormat("HH:mm");

        tvDate.setText(dateFormatter.format(calendar.getTime()));
        tvTime.setText(timeFormatter.format(calendar.getTime()));

        noteYear = calendar.get(Calendar.YEAR);
        noteMonth = calendar.get(Calendar.MONTH);
        noteDay = calendar.get(Calendar.DAY_OF_MONTH);
        noteHour = calendar.get(Calendar.HOUR_OF_DAY);
        noteMinute = calendar.get(Calendar.MINUTE);

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar c1 = Calendar.getInstance();
                c1.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                tvDate.setText(dateFormatter.format(c1.getTime()));

                noteYear = year;
                noteMonth = monthOfYear;
                noteDay = dayOfMonth;
            }
        },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                tvTime.setText(""+hourOfDay+":"+minute);
                noteHour = hourOfDay;
                noteMinute = minute;
            }
        },calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
    }

    @Override
    public void onClick(View view) {
        if(view == tvDate) {
            datePickerDialog.show();
        }
        else if(view == tvTime) {
            timePickerDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.item_add_note) {

            String noteContent = etContent.getText().toString();
            this.tripId = ((TripSpinner) spinTrips.getSelectedItem()).getId();

            if (noteContent.matches("")) {
                Toast.makeText(getApplicationContext(), getString(R.string.addnote__no_note), Toast.LENGTH_SHORT).show();
            } else {
                Calendar noteTime = Calendar.getInstance();
                noteTime.set(noteYear, noteMonth, noteDay, noteHour, noteMinute);

                DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                db.insertNote(this.tripId, noteContent, noteTime.getTimeInMillis());
                db.close();
                // Hide the keyboard
                if (getCurrentFocus() != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                Toast.makeText(getApplicationContext(), getString(R.string.addnote__note_saved), Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getApplicationContext(), ShowNotesActivity.class);
                i.putExtra("id", this.tripId);
                startActivity(i);
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
