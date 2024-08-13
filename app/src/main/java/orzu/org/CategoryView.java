package orzu.org;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.JsonReader;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;


public class CategoryView extends AppCompatActivity {

    ArrayList<Map<String, Object>> data;
    Dialog dialog;
    CardView card_of_category_view;
    ProgressBar progressBar;
    ImageView category_view_back;
    TextView button_categ;

    public static void start(Context context) {
        Intent intent = new Intent(context, CategoryView.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryPurpleTop));
        setContentView(R.layout.activity_category_view);

        button_categ = findViewById(R.id.button_categ);
        button_categ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        progressBar = findViewById(R.id.progressBarCat);
        card_of_category_view = findViewById(R.id.card_of_category_view);
        card_of_category_view.setBackgroundResource(R.drawable.shape_card_topcorners);
        category_view_back = findViewById(R.id.category_view_back);
        category_view_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                1500.0f, 0.0f);
        animation.setDuration(500);
        card_of_category_view.startAnimation(animation);

        ListView lvCat = (ListView) findViewById(R.id.list_cat);
        data = new ArrayList<>();

        button_categ.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                button_categ.setVisibility(View.VISIBLE);
                Animation animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.zoom_in);
                button_categ.startAnimation(animZoomIn);
            }
        }, animation.getDuration());
        String taskList = "Категория";
        String idList = "ID";
        final HttpsURLConnection[] myConnection = new HttpsURLConnection[1];
        final URL[] orzuEndpoint = new URL[1];


        @SuppressLint("StaticFieldLeak")
        class AsyncOrzuTasks extends AsyncTask<String, String, ArrayList<Map<String, Object>>> {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected ArrayList<Map<String, Object>> doInBackground(String... strings) {
                orzuEndpoint[0] = null;
                JsonReader[] jsonReader = new JsonReader[1];

                try {
                    orzuEndpoint[0] = new URL("https://orzu.org/api?%20appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&lang=ru&opt=view_cat&cat_id=only_parent");


                    myConnection[0] =
                            (HttpsURLConnection) orzuEndpoint[0].openConnection();
                    if (myConnection[0].getResponseCode() == 200) {
                        // Success
                        InputStream responseBody = myConnection[0].getInputStream();
                        InputStreamReader responseBodyReader =
                                new InputStreamReader(responseBody, StandardCharsets.UTF_8);

                        jsonReader[0] = new JsonReader(responseBodyReader);
                    }

                    data = new ArrayList<>();
                    jsonReader[0].beginArray(); // Start processing the JSON object
                    while (jsonReader[0].hasNext()) { // Loop through all keys

                        data.add(readMessage(jsonReader[0]));

                    }
                    jsonReader[0].endArray();


                    jsonReader[0].close();
                    myConnection[0].disconnect();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    CategoryView.this.runOnUiThread(new Runnable() {
                        public void run() {
                            dialog = new Dialog(CategoryView.this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
                            dialog.setContentView(R.layout.dialog_no_internet);
                            Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                            // if button is clicked, close the custom dialog
                            dialogButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AsyncOrzuTasks taskBack = new AsyncOrzuTasks();
                                    taskBack.execute();
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        }
                    });
                    e.printStackTrace();
                }

                return null;
            }

            protected void onPostExecute(ArrayList<Map<String, Object>> result) {
                super.onPostExecute(result);


                String[] from = {taskList};
                int[] to = {R.id.textItemCat};

                SimpleAdapter arrayAdapter = new SimpleAdapter(getBaseContext(), data, R.layout.cat_item, from, to);
                lvCat.setAdapter(arrayAdapter);
                lvCat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(getApplication(), SubCategoryView.class);
                        Map<String, Object> map;
                        map = data.get(i);
                        intent.putExtra("id", map.get(idList).toString());
                        intent.putExtra("name", map.get(taskList).toString());
                        startActivity(intent);
                    }
                });

                progressBar.setVisibility(View.INVISIBLE);
            }
        }


        AsyncOrzuTasks catTask = new AsyncOrzuTasks();
        catTask.execute();

    }

    private Map<String, Object> readMessage(JsonReader reader) throws IOException {
        long id = 1;
        String text = null;
        Map<String, Object> m = new HashMap<>();
        String taskList = "Категория";
        String idList = "ID";
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                id = reader.nextLong();
                m.put(idList, id);
            } else if (name.equals("name")) {
                text = reader.nextString();
                m.put(taskList, text);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return m;
    }

}
