package orzu.org;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import orzu.org.ui.login.TaskAdapter;

public class FeedbackTask extends AppCompatActivity implements MainItemSelect {

    ShimmerFrameLayout shim;
    @SuppressLint("StaticFieldLeak")
    public static Activity fa;
    ArrayList<Map<String, Object>> data;
    RecyclerView lvCat;
    CardView cardView;
    TextView empty;
    MainItemSelect itemSelect;
    ImageView feedback_task_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryPurpleTop));
        setContentView(R.layout.activity_feedback_task);
        shim = (ShimmerFrameLayout) findViewById(R.id.feedbackshimmertask);
        shim.setBackgroundResource(R.drawable.shape_card_topcorners);
        shim.startShimmer();
        cardView = findViewById(R.id.card_of_feedback);
        cardView.setBackgroundResource(R.drawable.shape_card_topcorners);
        feedback_task_back = findViewById(R.id.feedback_task_back);
        feedback_task_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        lvCat = findViewById(R.id.list_feedback_task);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        lvCat.setLayoutManager(layoutManager);
        empty = findViewById(R.id.fedbacktaskempty);
        itemSelect = this;
        requestFeedback();
        fa = this;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(FeedbackTask.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(FeedbackTask.this, new String[]{Manifest.permission.CALL_PHONE},0);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                else
                {                }
                return;
            }
        }
    }

    public void requestFeedback(){

        String url = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=task_requests&act=view&task_id=" + Common.taskId;
        OkHttpClient client = new OkHttpClient();
        String cat = "ID";
        String name = "Имена";
        String nar = "Описание";
        String amt = "Цена";
        String idSug = "SugID";
        String stars_sad = "Sad";
        String stars_nat = "Nat";
        String stars_hap = "Hap";
        String selected = "Select";
        String phone = "Phone";
        String avatar = "Avatar";
        data = new ArrayList<>();
        TaskAdapter arrayAdapter1 = new TaskAdapter(this, data);
        arrayAdapter1.setClickListener(new TaskAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Map<String, Object> m2 = data.get(position);
                Intent intent = new Intent(getApplication(), UserView.class);
                intent.putExtra("idhis", String.valueOf(m2.get(cat)));

                startActivity(intent);
            }
        });
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                FeedbackTask.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(FeedbackTask.this, android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestFeedback();
                                dialog.dismiss();
                            }
                        });
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.show();
                            }
                        }, 500);
                    }
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String mMessage = Objects.requireNonNull(response.body()).string();
                if (mMessage.equals("\"No request yet\"")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            empty.setVisibility(View.VISIBLE);
                            shim.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                try {
                    Map<String, Object> m;
                    JSONArray jsonArray = new JSONArray(mMessage);
                    String feedID;

                    int lenght = jsonArray.length();
                    for (int i = 0; i < lenght; i++){
                        m = new HashMap<>();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        feedID = jsonObject.getString("user_id");
                        m.put(cat, feedID);
                        m.put(nar, jsonObject.getString("narrative"));
                        m.put(name, jsonObject.getString("username"));
                        m.put(amt, jsonObject.getString("amount"));
                        m.put(idSug, jsonObject.getString("id"));
                        m.put(stars_sad, jsonObject.getString("userstars_sad"));
                        m.put(stars_nat, jsonObject.getString("userstars_neutral"));
                        m.put(stars_hap, jsonObject.getString("userstars_happy"));
                        m.put(selected, jsonObject.getString("selected"));
                        m.put(phone, jsonObject.getString("userphone"));
                        m.put(avatar, jsonObject.getString("avatar"));
                        data.add(m);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            lvCat.setAdapter(arrayAdapter1);
                            shim.setVisibility(View.INVISIBLE);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace(); }
            }
        });
    }
    @Override
    public void onClick(View view) {
    }
    @Override
    public void onItemSelectedListener(View view, int position) {
    }
}
