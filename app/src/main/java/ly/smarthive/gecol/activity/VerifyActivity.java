package ly.smarthive.gecol.activity;

import static ly.smarthive.gecol.COMMON.CURRENT_USER_EMAIL;
import static ly.smarthive.gecol.COMMON.CURRENT_USER_PASSWORD;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import ly.smarthive.gecol.AppController;
import ly.smarthive.gecol.COMMON;
import ly.smarthive.gecol.R;
import ly.smarthive.gecol.SessionManager;

public class VerifyActivity extends AppCompatActivity {
    private static final String TAG = VerifyActivity.class.getSimpleName();
    SessionManager session;
    EditText inputPin;
    private ProgressDialog pDialog;
    String  pin;
    boolean status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        session = new SessionManager(this);
        pDialog = new ProgressDialog(this);
        CURRENT_USER_EMAIL = session.getEmail();
        CURRENT_USER_PASSWORD = session.getPassword();
        pDialog.setCancelable(false);
        checkLogin();
        inputPin = findViewById(R.id.pin);
        /*--------------------------------------------------------------------------*/
        Button loginBtn = findViewById(R.id.btnSignIn);
        loginBtn.setOnClickListener(v -> {
            if (checkNotNULL()) {
                pin = Objects.requireNonNull(inputPin.getText()).toString().trim();
                Log.d(TAG, "Pin:" + pin);
                verify();
            } else {
                   Toast.makeText(getApplicationContext(), "الرجاء ادخال الرمز السري", Toast.LENGTH_LONG).show();
            }

        });

    }

    private void verify() {
        if (checkNotEmpty()) {
            verifyUser();
        } else {
            Toast.makeText(getApplicationContext(), "الرجاء ادخال الرمز السري", Toast.LENGTH_LONG).show();
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
    private void verifyUser() {
        // Tag used to cancel the request
        String tag_string_req = "req_verifyUser";
        pDialog.setMessage("Please Wait ...");
        showDialog();
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, COMMON.VERIFY_URL, response -> {
            Log.d(TAG, "verifyUser Response: " + response);
            hideDialog();
            try {
                JSONObject jObj = new JSONObject(response);
                boolean success = jObj.getBoolean("success");
                if (success) {
                    session.setLogin(true);
                    JSONObject data = jObj.getJSONObject("data");
                    String token = data.getString("accessToken");
                    session.setToken(token);
                    status = true;
                    session.setEmailPassword(CURRENT_USER_EMAIL, CURRENT_USER_PASSWORD, status);
                    startActivity(new Intent(VerifyActivity.this, MainActivity.class));
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
                params.put("pin", pin);
                params.put("email", CURRENT_USER_EMAIL);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_string_req);
    }



    private void checkLogin() {

        if (session.isLoggedIn() ) {
            Intent intent = new Intent(VerifyActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean checkNotEmpty() {
        return (!pin.isEmpty());
    }

    private boolean checkNotNULL() {
        return (!inputPin.toString().isEmpty());
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
