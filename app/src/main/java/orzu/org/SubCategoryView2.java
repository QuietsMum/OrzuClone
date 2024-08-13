package orzu.org;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SubCategoryView2 extends AppCompatActivity {

    ArrayList<Map<String, Object>> data;
    RecyclerView suggestResultView;
    AdapterCityFilter resultAdapter;
    List<String> suggestResult;
    ProgressBar pr;
    ImageView cities_back;
    CardView cardView;
    TextView all_city_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryPurpleTop));
        setContentView(R.layout.activity_sub_category_view2);
        pr = findViewById(R.id.progrescity);
        all_city_text = findViewById(R.id.all_city_text_in_filter);
        all_city_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.allCity = true;
                finish();
            }
        });
        suggestResultView = findViewById(R.id.result_city);
        suggestResultView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        suggestResultView.setLayoutManager(layoutManager);
        suggestResult = new ArrayList<>();
        resultAdapter = new AdapterCityFilter(this, suggestResult);
        suggestResultView.setAdapter(resultAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(suggestResultView.getContext(),
                layoutManager.getOrientation());
        suggestResultView.addItemDecoration(dividerItemDecoration);
        cardView = findViewById(R.id.card_of_city_view);
        cardView.setBackgroundResource(R.drawable.shape_card_topcorners);
        cities_back = findViewById(R.id.cities_back);
        cities_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                1500.0f, 0.0f);
        animation.setDuration(500);
        cardView.startAnimation(animation);
        requestCity();
    }
    public void requestCity() {
        String url = "https://orzu.org/api?%20appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=getOther&get=cities";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                SubCategoryView2.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(SubCategoryView2.this, android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestCity();
                                dialog.dismiss();
                            }
                        });
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.show();
                            }
                        }, 500);
                        pr.setVisibility(View.INVISIBLE);
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String mMessage = response.body().string();
                int i;
                try {
                    JSONArray jsonArray = new JSONArray(mMessage);
                    int lenght = jsonArray.length();
                    suggestResult.clear();
                    for (i = 0; i < lenght; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        suggestResult.add(jsonObject.getString("name"));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resultAdapter.notifyDataSetChanged();
                        }
                    });
                    SubCategoryView2.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pr.setVisibility(View.INVISIBLE);
                        }
                    });

                    resultAdapter.setClickListener(new AdapterCityFilter.ItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("result", suggestResult.get(position));
                            Common.allCity = false;
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
