package ly.smarthive.gecol.activity;

import static ly.smarthive.gecol.COMMON.NOTICES_URL;

import static ly.smarthive.gecol.COMMON.USER_TOKEN;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

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

import ly.smarthive.gecol.AppController;
import ly.smarthive.gecol.MyDividerItemDecoration;
import ly.smarthive.gecol.R;
import ly.smarthive.gecol.SessionManager;
import ly.smarthive.gecol.activity.MainActivity;
import ly.smarthive.gecol.adapter.NoticesDataAdapter;
import ly.smarthive.gecol.model.Notice;

public class NoticesActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private final List<Notice> noticesList = new ArrayList<>();
    private NoticesDataAdapter mAdapter;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices);

        mAdapter = new NoticesDataAdapter(noticesList);
        sessionManager = new SessionManager(this);

        USER_TOKEN = sessionManager.getToken();

        RecyclerView recyclerView = findViewById(R.id.notices_rv);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(mAdapter);

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
        JsonObjectRequest jsonReq = new JsonObjectRequest(com.android.volley.Request.Method.GET,   NOTICES_URL, null, response -> {
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
        noticesList.clear();
        try {
            JSONArray feedArray = response.getJSONArray("data");
            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);
                Notice notice = new Notice();
                notice.setId(feedObj.getInt("id"));
                notice.setDate(feedObj.getString("date"));
                noticesList.add(notice);
                mAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}