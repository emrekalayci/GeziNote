package com.gezinote.android.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.gezinote.android.data.DatabaseHelper;
import com.gezinote.android.model.Note;
import com.gezinote.android.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Emre on 18.4.2015.
 */
public class NoteAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<Note> mNoteList;
    private static Activity context = null;

    public NoteAdapter(Activity activity, List<Note> notes) {
        this.context = activity;
        // XML'i alıp View'a çevirecek inflater'ı örnekleyelim
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Gösterilecek listeyi de alalım
        mNoteList = notes;
    }

    @Override
    public int getCount() {
        return mNoteList.size();
    }

    @Override
    public Note getItem(int position) {
        return mNoteList.get(position);
    }

    // buraya bi ayar çek.
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View viewRow;
        viewRow = mInflater.inflate(R.layout.layout_list_item_note, null);
        final Note note = mNoteList.get(position);

        TextView txtNoteContent = (TextView) viewRow.findViewById(R.id.txt_note_content);
        TextView txtNoteTime = (TextView) viewRow.findViewById(R.id.txt_note_time);
        ImageButton ibNoteOptions = (ImageButton) viewRow.findViewById(R.id.ib_note_options);

        // Date(long milliseconds) takes UTC time and returns the time based on the system's time zone.
        Date date = new Date(note.getTime());
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");

        txtNoteTime.setText(df.format(date));
        txtNoteContent.setText(note.getContent());

        ibNoteOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.mItem_show_note_delete:
                                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                alert.setMessage(context.getString(R.string.note__dialog_permanate_delete));
                                alert.setPositiveButton(context.getString(R.string.note__dialog_ok), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        DatabaseHelper db = new DatabaseHelper(context);

                                        if (db.removeNote(note.getId())) {
                                            // Show successful delete message
                                            Toast.makeText(context, context.getString(R.string.note__msg_deleted), Toast.LENGTH_LONG).show();
                                            // Update the ListView
                                            mNoteList.remove(position);
                                            notifyDataSetChanged();
                                        } else {
                                            Toast.makeText(context, context.getString(R.string.note__msg_error), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                alert.setNegativeButton(context.getString(R.string.note__dialog_cancel), null);
                                alert.show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.inflate(R.menu.menu_show_note_options);
                popup.show();
            }
        });

        return viewRow;
    }
}

