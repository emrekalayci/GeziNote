package com.gezinote.android.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.gezinote.android.data.DatabaseHelper;
import com.gezinote.android.activity.EditTripActivity;
import com.gezinote.android.R;
import com.gezinote.android.model.Trip;
import com.gezinote.android.helper.WebService;

import java.util.List;

/**
 * Created by Emre on 12.4.2015.
 */
public class TripAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<Trip> mTripList;
    private static Activity context = null;
    private DatabaseHelper db;

    public TripAdapter(Activity activity, List<Trip> trips) {
        context = activity;
        db = new DatabaseHelper(context);
        //XML'i alıp View'a çevirecek inflater'ı örnekleyelim
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //gösterilecek listeyi de alalım
        mTripList = trips;
    }

    @Override
    public int getCount() {
        return mTripList.size();
    }

    @Override
    public Trip getItem(int position) {
        return mTripList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View viewRow;
        viewRow = mInflater.inflate(R.layout.layout_list_item_main, null);

        CardView card = (CardView) viewRow.findViewById(R.id.card_trip);
        TextView txtTripTitle = (TextView) viewRow.findViewById(R.id.txt_trip_title);
        TextView txtDayNum = (TextView) viewRow.findViewById(R.id.txt_day_num);
        TextView txtNoteNum = (TextView) viewRow.findViewById(R.id.txt_note_num);
        ImageButton ib = (ImageButton) viewRow.findViewById(R.id.ib_options);

        final Trip trip = mTripList.get(position);

        txtTripTitle.setText(trip.getTitle());

        // Calculate the day difference between two dates
        int dayCount = (int)( (trip.getEndDate() - trip.getStartDate()) / (1000 * 60 * 60 * 24) );
        dayCount++;

        if(dayCount == 1)
            txtDayNum.setText(Integer.toString(dayCount) + " " + context.getString(R.string.trip_label_day) + " | ");
        else
            txtDayNum.setText(Integer.toString(dayCount) + " " + context.getString(R.string.trip_label_days) + " | ");

        // Calculate the number of notes in a trip
        int noteCount = db.countNote(trip.getId());

        if(noteCount == 0)
           txtNoteNum.setText(context.getString(R.string.trip_label_no_note));
        else
        {
            if(noteCount == 1)
                txtNoteNum.setText(Integer.toString(noteCount) + " " +context.getString(R.string.trip_label_note));
            else
                txtNoteNum.setText(Integer.toString(noteCount) + " " +context.getString(R.string.trip_label_notes));
        }


        if (trip.getColorId() != 5)
        {
            TypedArray colors = context.getResources().obtainTypedArray(R.array.country_flags);
            int colorIntegerId = colors.getColor(trip.getColorId(), 1);
            card.setCardBackgroundColor(colorIntegerId);
        }

        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.mItem_options_sync:
                                (new WebService(context)).syncTrip(trip.getId());
                                return true;
                            case R.id.mItem_options_edit:
                                Intent i = new Intent(context, EditTripActivity.class);
                                i.putExtra("id", trip.getId());
                                context.startActivity(i);
                                return true;
                            case R.id.mItem_options_delete:
                                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                alert.setMessage(context.getString(R.string.trip_dialog_permanent_delete));
                                alert.setPositiveButton(context.getString(R.string.trip_dialog_ok), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        db.removeTrip(trip.getId());
                                        // Show successful delete message
                                        Toast.makeText(context, context.getString(R.string.trip_msg_deleted), Toast.LENGTH_LONG).show();
                                        // Update the ListView
                                        mTripList.remove(position);
                                        notifyDataSetChanged();
                                    }
                                });
                                alert.setNegativeButton(context.getString(R.string.trip_dialog_cancel), null);
                                alert.show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.inflate(R.menu.menu_main_options);
                popup.show();
            }
        });

        return viewRow;
    }
}
