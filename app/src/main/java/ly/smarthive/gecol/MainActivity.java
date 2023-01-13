package ly.smarthive.gecol;

import static ly.smarthive.gecol.COMMON.MAIN_URL;
import static ly.smarthive.gecol.COMMON.NOTICES_URL;
import static ly.smarthive.gecol.COMMON.USER_TOKEN;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ly.smarthive.gecol.adapter.NoticesDataAdapter;
import ly.smarthive.gecol.model.Notice;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    SessionManager sessionManager;
    int id;
    TextView name_tv, office_name_tv, office_city_tv, counter_number_tv, reading_last_tv, reading_blat_tv;
    String name, email, n_id, office_name, office_number, office_city, counter_number, reading_last, reading_blat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);
        USER_TOKEN = sessionManager.getToken();

        LinearLayout current_btn,last_btn;
        CardView notices_btn;
        current_btn = findViewById(R.id.current_reading_btn);
        last_btn = findViewById(R.id.last_reading_btn);
        notices_btn = findViewById(R.id.notices);

        name_tv = findViewById(R.id.name_tv);
        office_name_tv = findViewById(R.id.office_name_tv);
        counter_number_tv = findViewById(R.id.counter_number_tv);
        reading_last_tv = findViewById(R.id.reading_last_tv);
        reading_blat_tv = findViewById(R.id.reading_blat_tv);


        current_btn.setOnClickListener(view -> startActivity(showReadings()));
        last_btn.setOnClickListener(view -> startActivity(showReadings()));
        notices_btn.setOnClickListener(view -> startActivity( new Intent(MainActivity.this,NoticesActivity.class)));


        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(NOTICES_URL);
        if (entry != null) {
            String data = new String(entry.data, StandardCharsets.UTF_8);
            try {
                parseJsonFeed(new JSONObject(data));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            GrabMainData();
        }
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    }

    private Intent showReadings() {
        return  new Intent(MainActivity.this,ReadingsActivity.class);
    }

    private void GrabMainData() {
        JsonObjectRequest jsonReq = new JsonObjectRequest(com.android.volley.Request.Method.GET,   MAIN_URL, null, response -> {
            VolleyLog.d(TAG, "Response: " + response.toString());
            Log.e("RE", response.toString());
            parseJsonFeed(response);
        }, error -> Log.d("VOLLEY ERROR", error.toString())) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put("Content-Type", "application/json");
                headerMap.put("Authorization", "Bearer " + USER_TOKEN);
                return headerMap;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

    /**
     * Parsing json response and passing the data to feed view list adapter
     **/
    @SuppressLint("NotifyDataSetChanged")
    private void parseJsonFeed(JSONObject response) {
        try {
            JSONObject data = response.getJSONObject("data");
            JSONObject user = data.getJSONObject("user");
            id = user.getInt("id");
            name = user.getString("name");
            email = user.getString("email");
            n_id = user.getString("n_id");
            counter_number= user.getString("counter_number");
            office_name = user.getString("office_name");
            office_number = user.getString("office_number");
            office_city = user.getString("office_city");
            reading_blat = user.getString("reading_blat");
            reading_last = user.getString("reading_last");
            viewData();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void viewData() {
        name_tv.setText(name);
        office_name_tv.setText(office_name);
        //office_city_tv.setText(office_city);
        counter_number_tv.setText(counter_number);
        reading_last_tv.setText(reading_last);
        reading_blat_tv.setText(reading_blat);
    }
}