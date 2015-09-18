package com.gezinote.android.activity;



/**
 * A login screen that offers login via email/password.
 */

import android.app.Activity;

import com.gezinote.android.data.DatabaseHelper;
import com.gezinote.android.R;
import com.gezinote.android.app.AppConfig;
import com.gezinote.android.app.AppController;
import com.gezinote.android.helper.SessionManager;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class LoginActivity extends Activity {
    // LogCat tag
    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Oturum yönetimi
        session = new SessionManager(getApplicationContext());

        // kullanıcı oturumu var mı kontrol et
        if (session.isLoggedIn()) {
            // varsa, anasayfaya yönlendir
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // giriş butonu click eventi
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();

                // formda boş veri var mı kontrol et
                if (email.trim().length() > 0 && password.trim().length() > 0) {
                    // kullanıcı giriş kontrolü
                    checkLogin(email, password);
                } else {
                    // boş alan varsa
                    Toast.makeText(getApplicationContext(), getString(R.string.login__unfilled_field), Toast.LENGTH_LONG).show();
                }
            }

        });

        // Kayıt ol ekranına link
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * mysql den kullanıcı bilgilerini doğrula
     * */
    private void checkLogin(final String email, final String password) {
        // istek iptali için tag
        String tag_string_req = "req_login";

        pDialog.setMessage(getString(R.string.login__logging_in));
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Giriş Yanıtı: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // json hatası kontrolü
                    if (!error) {
                        // başarılı
                        // oturum yarat

                        JSONObject user = jObj.getJSONObject("user");

                        int id = user.getInt("id");
                        String email = user.getString("email");
                        String username = user.getString("username");
                        String fullname = user.getString("fullname");
                        String created_at = "";

                        // Inserting row in users table
                        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                        db.insertUser(id, username, email, fullname, created_at);

                        session.setLogin(true);

                        // main activity başlat
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // giriş hatası
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON hatası
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Giriş Hatası: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // login urle bilgileri gönder
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "login");
                params.put("email", email);
                params.put("password", password);

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
