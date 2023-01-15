package ly.smarthive.gecol.activity;

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

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    SessionManager session;
    EditText inputPassword, inputEmail,inputNId,inputName;
    private ProgressDialog pDialog;
    String  email, password,nId,name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        session = new SessionManager(this);
        inputName = findViewById(R.id.name);
        inputNId = findViewById(R.id.Nid);
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.passwrod);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        checkLogin();
        /*--------------------------------------------------------------------------*/
        /*to add onclick event to login button*/
        Button registerBtn = findViewById(R.id.btnRegister);
        Button cancelBtn = findViewById(R.id.btnCancel);
        cancelBtn.setOnClickListener(view -> finish());
        registerBtn.setOnClickListener(v -> {
            if (checkNotNULL()) {
                password = Objects.requireNonNull(inputPassword.getText()).toString().trim();
                email = Objects.requireNonNull(inputEmail.getText()).toString().trim();
                nId = Objects.requireNonNull(inputNId.getText()).toString().trim();
                name = Objects.requireNonNull(inputName.getText()).toString().trim();
                Log.d(TAG, "P:" + email + "//W:" + password);
                register();
            } else {
                   Toast.makeText(getApplicationContext(), "الرجاء ادخال البيانات كلها", Toast.LENGTH_LONG).show();
            }

        });
    }

    private void register() {
        if (checkNotEmpty()) {
            sendRegisterData();
        } else {
            Toast.makeText(getApplicationContext(), "Please Enter your Credentials", Toast.LENGTH_LONG).show();
        }
    }

    private void sendRegisterData() {

        String tag_string_req = "req_login";
        pDialog.setMessage("Please Wait ...");
        showDialog();
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, COMMON.REGISTER_URL, response -> {
            Log.d(TAG, "REGISTER_URL Response: " + response);
            hideDialog();
            try {
                JSONObject jObj = new JSONObject(response);
                boolean success = jObj.getBoolean("success");
                if (success) {
                    session.setEmailPassword(email,password,false);
                    startActivity(new Intent(RegisterActivity.this, VerifyActivity.class));
                    finish();
                } else {
                    String errorMsg = jObj.getString("message");
                    Log.e(TAG,errorMsg);
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG,e.getMessage());
            }
        }, error -> {
            Log.e(TAG, "REGISTER Error: " + error.getMessage());
            //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            hideDialog();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("Accept", "application/json");
                params.put("n_id", nId);
                params.put("password", password);
                params.put("name", name);
                params.put("email", email);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_string_req);
    }


    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }



    private void checkLogin() {

        if (session.isLoggedIn() ) {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean checkNotEmpty() {
        return (!password.isEmpty() || !email.isEmpty() || !name.isEmpty() || !nId.isEmpty());
    }

    private boolean checkNotNULL() {
        return (!inputPassword.toString().isEmpty() || !inputEmail.toString().isEmpty() || !inputNId.toString().isEmpty() || !inputName.toString().isEmpty());
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
