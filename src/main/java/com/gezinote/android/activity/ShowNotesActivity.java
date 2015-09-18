package com.gezinote.android.activity;

import com.gezinote.android.data.DatabaseHelper;
import com.gezinote.android.R;
import com.gezinote.android.adapter.NoteAdapter;
import com.gezinote.android.helper.WebService;
import com.gezinote.android.model.Note;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class ShowNotesActivity extends ActionBarActivity {

    private List<Note> noteList = new ArrayList<Note>();
    private NoteAdapter noteAdapter;
    private int tripId;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_notes);

        // Enable the app icon as the Up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        tripId = getIntent().getIntExtra("id", 0);

        // Set activity title
        setTitle(db.getTrip(tripId).getTitle());

        lv = (ListView) findViewById(R.id.lv_notes);
        lv.setOnItemClickListener(editNoteListener);

        noteList = db.getNotes(tripId);

        if(noteList.size()==0){
            Toast.makeText(getApplicationContext(), getString(R.string.shownote__no_post), Toast.LENGTH_LONG).show();
        }else{
            noteAdapter = new NoteAdapter(this, noteList);
            lv.setAdapter(noteAdapter);
        }

        findViewById(R.id.fab_showNote_new).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShowNotesActivity.this, AddNoteActivity.class));
            }
        });
    }

    // Capture ListView item click
    AdapterView.OnItemClickListener editNoteListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            int noteId = noteList.get(position).getId();

            Intent i = new Intent(getApplicationContext(), EditNoteActivity.class);
            i.putExtra("id", noteId);
            startActivity(i);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mItem_showNote_sync:
                (new WebService(this)).syncTrip(tripId);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
