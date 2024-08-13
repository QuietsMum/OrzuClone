package orzu.org;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import orzu.org.ui.login.FeedbackAdapter;

public class Feedback extends AppCompatActivity implements View.OnClickListener {

    ShimmerFrameLayout shim;
    ArrayList<Map<String, Object>> all;
    ArrayList<Map<String, Object>> bad;
    ArrayList<Map<String, Object>> neutral;
    ArrayList<Map<String, Object>> happy;
    ListView lvCat;
    String idUser;
    String nameUserFeedbackto;
    TextView textFeeds;
    LinearLayout sort_all, sort_bad, sort_neutral, sort_happy, sort;
    FeedbackAdapter arrayAdapter1;
    TextView count_bad, count_neutral, count_happy, button_add_feed_in_feed;
    ImageView feedback_back;
    ImageView imagenotask;
    TextView textnotask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.back_for_feed));
        setContentView(R.layout.activity_feedback);
        idUser = Objects.requireNonNull(getIntent().getExtras()).getString("idUserFeedback");

        nameUserFeedbackto = getIntent().getExtras().getString("nameUserFeedbackto");
        sort = findViewById(R.id.sort);
        sort.setVisibility(View.INVISIBLE);
        shim = (ShimmerFrameLayout) findViewById(R.id.feedbackshimmer);
        shim.startShimmer();
        lvCat = (ListView) findViewById(R.id.list_feedback);
        sort_all = findViewById(R.id.sort_all);
        textFeeds = findViewById(R.id.textAllFeeds);
        sort_bad = findViewById(R.id.sort_bad);
        sort_neutral = findViewById(R.id.sort_neutral);
        sort_happy = findViewById(R.id.sort_happy);
        sort_all.setOnClickListener(this);
        sort_bad.setOnClickListener(this);
        sort_neutral.setOnClickListener(this);
        sort_happy.setOnClickListener(this);
        imagenotask = findViewById(R.id.imageNoTask2);
        textnotask = findViewById(R.id.textViewNoTask2);
        feedback_back = findViewById(R.id.feedback_back);
        count_bad = findViewById(R.id.count_of_sad);
        count_neutral = findViewById(R.id.count_of_neutral);
        count_happy = findViewById(R.id.count_of_happy);
        button_add_feed_in_feed = findViewById(R.id.button_add_feed_in_feed);
        feedback_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        button_add_feed_in_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(Feedback.this, AddFeedback.class);
                intent2.putExtra("idUserFeedbackto", idUser);
                intent2.putExtra("nameUserFeedbackto", nameUserFeedbackto);
                startActivity(intent2);
            }
        });
        if(idUser.equals(Common.userId)){

        }
        requestFeedback();
    }

    public void requestFeedback() {
        String url = "https://orzu.org/api?%20appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=reviews&act=view&userid=" + idUser + "&sort=all";
        OkHttpClient client = new OkHttpClient();
        String cat = "Имена";
        String nar = "Описание";
        String img = "Картинка";
        String count = "Отзыв";
        String avatar = "Аватар";
        all = new ArrayList<>();
        bad = new ArrayList<>();
        happy = new ArrayList<>();
        neutral = new ArrayList<>();
        arrayAdapter1 = new FeedbackAdapter(this, all);
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, IOException e) {
                Feedback.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(Feedback.this, android.R.style.Theme_Material_Light_NoActionBar);
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
                if (!mMessage.equals("\"No reviews yet\"")) {
                    try {
                        Map<String, Object> m;
                        JSONArray jsonArray = new JSONArray(mMessage);
                        int bpSad = R.drawable.ic_bad;
                        int bpNorm = R.drawable.ic_neutral2;
                        int bpHappy = R.drawable.ic_happy2;
                        int lenght = jsonArray.length();
                        String feedName = "";
                        for (int i = 0; i < lenght; i++) {
                            m = new HashMap<>();
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            feedName = jsonObject.getString("username");

                            String[] splited = jsonObject.getString("avatar").split(Character.toString((char) 94));
                            String str = Arrays.toString(splited);

                            try {
                                Bitmap bitmap = Picasso.get().load("https://orzu.org" + str.substring(1, str.length() - 1)).get();
                                m.put(avatar, bitmap);
                            } catch (Exception ex) {
                                Bitmap icon = BitmapFactory.decodeResource(Feedback.this.getResources(), Common.drawable);
                                m.put(avatar, icon);
                            }
                            m.put(cat, feedName);
                            long countFeed = jsonObject.getLong("like");
                            m.put(nar, jsonObject.getString("narrative"));
                            if (countFeed == 0) {
                                m.put(count, "-1");
                                m.put(img, bpSad);
                                bad.add(m);
                            }
                            if (countFeed == 1) {
                                m.put(count, "0");
                                m.put(img, bpNorm);
                                neutral.add(m);
                            }
                            if (countFeed == 2) {
                                m.put(count, "+1");
                                m.put(img, bpHappy);
                                happy.add(m);
                            }
                            if (countFeed == 3) {
                                m.put(count, "+3");
                                m.put(img, bpHappy);
                            }
                            all.add(m);
                        }
                        runOnUiThread(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                shim.setVisibility(View.INVISIBLE);
                                sort.setVisibility(View.VISIBLE);
                                lvCat.setAdapter(arrayAdapter1);
                                arrayAdapter1.notifyDataSetChanged();
                                count_bad.setText(bad.size() + "");
                                count_neutral.setText(neutral.size() + "");
                                count_happy.setText(happy.size() + "");
                                imagenotask.setVisibility(View.INVISIBLE);
                                textnotask.setVisibility(View.INVISIBLE);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    runOnUiThread(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            shim.setVisibility(View.INVISIBLE);
                            sort.setVisibility(View.VISIBLE);
                            lvCat.setAdapter(arrayAdapter1);
                            arrayAdapter1.notifyDataSetChanged();
                            count_bad.setText(bad.size() + "");
                            count_neutral.setText(neutral.size() + "");
                            count_happy.setText(happy.size() + "");
                            imagenotask.setVisibility(View.VISIBLE);
                            textnotask.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
    }
    @Override
    public void onClick(View view) {
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (6 * scale + 0.5f);
        switch (view.getId()) {
            case R.id.sort_all:
                sort_all.setBackgroundResource(R.drawable.shape_for_feedback_gray);
                sort_bad.setBackgroundResource(R.drawable.shape_for_feedback);
                sort_neutral.setBackgroundResource(R.drawable.shape_for_feedback);
                sort_happy.setBackgroundResource(R.drawable.shape_for_feedback);
                sort_happy.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
                sort_neutral.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
                sort_bad.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
                getAll();
                break;
            case R.id.sort_bad:
                sort_all.setBackgroundResource(R.drawable.shape_for_feedback);
                sort_bad.setBackgroundResource(R.drawable.shape_for_feedback_gray);
                sort_neutral.setBackgroundResource(R.drawable.shape_for_feedback);
                sort_happy.setBackgroundResource(R.drawable.shape_for_feedback);
                sort_happy.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
                sort_neutral.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
                sort_bad.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
                getBad();
                break;
            case R.id.sort_neutral:
                sort_all.setBackgroundResource(R.drawable.shape_for_feedback);
                sort_bad.setBackgroundResource(R.drawable.shape_for_feedback);
                sort_neutral.setBackgroundResource(R.drawable.shape_for_feedback_gray);
                sort_happy.setBackgroundResource(R.drawable.shape_for_feedback);
                sort_happy.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
                sort_neutral.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
                sort_bad.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
                getNeutral();
                break;
            case R.id.sort_happy:
                sort_all.setBackgroundResource(R.drawable.shape_for_feedback);
                sort_bad.setBackgroundResource(R.drawable.shape_for_feedback);
                sort_neutral.setBackgroundResource(R.drawable.shape_for_feedback);
                sort_happy.setBackgroundResource(R.drawable.shape_for_feedback_gray);
                sort_happy.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
                sort_neutral.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
                sort_bad.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
                getHappy();
                break;
        }
    }
    private void getHappy() {
        arrayAdapter1.ChangeData(happy);
    }
    private void getNeutral() {
        arrayAdapter1.ChangeData(neutral);
    }
    private void getBad() {
        arrayAdapter1.ChangeData(bad);
    }
    private void getAll() {
        arrayAdapter1.ChangeData(all);
    }
}
