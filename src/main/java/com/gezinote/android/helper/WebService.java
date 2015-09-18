package com.gezinote.android.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gezinote.android.data.DatabaseHelper;
import com.gezinote.android.app.AppConfig;
import com.gezinote.android.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;



/**
 * Created by Emre on 5.6.2015.
 */
public class WebService {

    // LogCat tag
    private static final String TAG = WebService.class.getSimpleName();

    private static Context context = null;
    private ProgressDialog pDialog;
    private final DatabaseHelper db;
    private SessionManager session;

    public WebService(Context context) {
        this.context = context;
        db = new DatabaseHelper(context);

        // Progress dialog
        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(false);

        // Oturum yönetimi
        session = new SessionManager(context);
    }

    public void syncTrip(final int tripId) {
        // istek iptali için tag
        String tag_string_req = "req_sync";

        pDialog.setMessage("Gezi senkronize ediliyor...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_SYNC, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Sync Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // json hatası kontrolü
                    if (!error) {
                        Toast.makeText(context, "Gezi senkronize edildi.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Senkronize sırasında hata oluştu.", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON hatası
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Sync Error: " + error.getMessage());
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams()
            {
                Log.e("ses", ""+tripId+" "+db.getUser().getGyId()+ " "+db.getTrip(tripId).getTitle()+ " "+db.getJsonNotes(tripId));

                Map<String, String> params = new HashMap<String, String>();
                params.put("trip_id", ""+tripId);
                params.put("user_id", ""+db.getUser().getGyId());
                params.put("title", db.getTrip(tripId).getTitle());
                params.put("content", db.getJsonNotes(tripId));

                return params;
            }
        };

        // istek kuyruğuna isteği ekle
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
