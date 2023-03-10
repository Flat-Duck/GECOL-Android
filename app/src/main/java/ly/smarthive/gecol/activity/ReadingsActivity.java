package ly.smarthive.gecol.activity;

import static com.android.volley.Request.Method.POST;
import static ly.smarthive.gecol.COMMON.PAY_URL;
import static ly.smarthive.gecol.COMMON.READINGS_URL;
import static ly.smarthive.gecol.COMMON.USER_TOKEN;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ly.smarthive.gecol.AppController;
import ly.smarthive.gecol.COMMON;
import ly.smarthive.gecol.MyDividerItemDecoration;
import ly.smarthive.gecol.R;
import ly.smarthive.gecol.SessionManager;
import ly.smarthive.gecol.activity.MainActivity;
import ly.smarthive.gecol.adapter.ReadingsDataAdapter;
import ly.smarthive.gecol.model.Reading;


public class ReadingsActivity extends AppCompatActivity implements ReadingsDataAdapter.SelectedItem {

    private static final String TAG = MainActivity.class.getSimpleName();
    private final List<Reading> readingsList = new ArrayList<>();
    private ReadingsDataAdapter mAdapter;
    SessionManager sessionManager;
Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readings);

        context = this;
        mAdapter = new ReadingsDataAdapter(readingsList,this,ReadingsActivity.this);
        sessionManager = new SessionManager(this);

        USER_TOKEN = sessionManager.getToken();

        RecyclerView recyclerView = findViewById(R.id.readings_rv);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(mAdapter);

        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(READINGS_URL);
        if (entry != null) {
            String data = new String(entry.data, StandardCharsets.UTF_8);
            try {
                parseJsonFeed(new JSONObject(data));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            GrabAllRequests();
        }
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void GrabAllRequests() {
        JsonObjectRequest jsonReq = new JsonObjectRequest(com.android.volley.Request.Method.GET,   READINGS_URL, null, response -> {
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
        readingsList.clear();
        try {
            JSONArray feedArray = response.getJSONArray("data");
            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);
                Reading reading = new Reading();
                reading.setId(feedObj.getInt("id"));
                reading.setDate(feedObj.getString("date"));
                reading.setValue(feedObj.getString("value"));
                reading.setPaid(feedObj.getBoolean("is_paid"));
                readingsList.add(reading);
                mAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void selectedItem(Reading reading, boolean pay) {
        String ACTION_URL = PAY_URL + reading.getId() ;
        StringRequest jsonObjectRequest = new StringRequest(POST,ACTION_URL, response -> {
            VolleyLog.d(TAG, "Response: " + response.toString());
            Log.e("RE", response.toString());
            try {
                JSONObject jObj = new JSONObject(response);
                if (jObj.getBoolean("success")) {
                    GrabAllRequests();
                }
                Toast.makeText(context, jObj.getString("message"), Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d("VOLLEY ERROR","dd" + error.getMessage());}) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put("Accept","application/json");
                headerMap.put("Content-Type", "application/json");
                headerMap.put("Authorization", "Bearer " + USER_TOKEN);
                return headerMap;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }
}