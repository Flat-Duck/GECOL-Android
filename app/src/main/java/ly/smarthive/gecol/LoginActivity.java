package ly.smarthive.gecol;


import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    SessionManager session;
    EditText inputPassword, inputEmail;
    private ProgressDialog pDialog;
    String  email, password;
    boolean status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = new SessionManager(this);
        inputPassword = findViewById(R.id.passwrod);
        inputEmail = findViewById(R.id.email);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        checkLogin();
        /*--------------------------------------------------------------------------*/
        /*to add onclick event to login button*/
        Button loginBtn = findViewById(R.id.btnSignIn);
        loginBtn.setOnClickListener(v -> {
            if (checkNotNULL()) {
                password = Objects.requireNonNull(inputPassword.getText()).toString().trim();
                email = Objects.requireNonNull(inputEmail.getText()).toString().trim();
                Log.d(TAG, "P:" + email + "//W:" + password);
                login();
            } else {
                //   Toast.makeText(getApplicationContext(), R.string.please_enter_credentials, Toast.LENGTH_LONG).show();
            }

        });

    }



    private void login() {
        if (checkNotEmpty()) {
            sendLogin();
        } else {
            Toast.makeText(getApplicationContext(), "Please Enter your Credentials", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    public boolean onSupportNavigateUp() {
        //onBackPressed();
        finish();
        return true;
    }
    /**
     * function to verify login details in mysql db
     */
    private void sendLogin() {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        pDialog.setMessage("Please Wait ...");
        showDialog();
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, COMMON.LOGIN_URL, response -> {
            Log.d(TAG, "Login Response: " + response);
            hideDialog();
            try {
                JSONObject jObj = new JSONObject(response);
                boolean success = jObj.getBoolean("success");
                if (success) {
                    session.setLogin(true);
                    JSONObject data = jObj.getJSONObject("data");
                    String token = data.getString("accessToken");
                    session.setToken(token);
                    COMMON.CURRENT_USER_PASSWORD = password;
                    COMMON.CURRENT_USER_EMAIL = email;
                    status = true;
                    session.setEmailPassword(email, password, status);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    String errorMsg = jObj.getString("message");
                    Log.e(TAG,errorMsg);
                    //Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG,e.getMessage());
            }
        }, error -> {
            Log.e(TAG, "Login Error: " + error.getMessage());
            //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            hideDialog();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("Accept", "application/json");
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_string_req);
    }



    private void checkLogin() {

        if (session.isLoggedIn() ) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean checkNotEmpty() {
        return (!password.isEmpty() || !email.isEmpty());
    }

    private boolean checkNotNULL() {
        return (!inputPassword.toString().isEmpty() || !inputEmail.toString().isEmpty());
    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void onStart() {
        super.onStart();
        checkLogin();
    }
}
