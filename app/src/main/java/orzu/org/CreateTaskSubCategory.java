package orzu.org;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.facebook.shimmer.ShimmerFrameLayout;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class CreateTaskSubCategory extends AppCompatActivity {

    ArrayList<Map<String, Object>> data;
    Dialog dialog;
    ShimmerFrameLayout progressBar;

    CardView cardView;
    TextView name_of_subcategory;
    ImageView back;
    @SuppressLint("StaticFieldLeak")
    public static Activity fa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryPurpleTop));
        setContentView(R.layout.activity_create_task_sub_category);

        String idIntent = Objects.requireNonNull(getIntent().getExtras()).getString("id");
        String nameIntent = getIntent().getExtras().getString("name");

        progressBar = findViewById(R.id.progressBarSubCatCreate);
        progressBar.setBackgroundResource(R.drawable.shape_card_topcorners);
        progressBar.startShimmer();
        fa = this;
        cardView = findViewById(R.id.card_of_subcategory);
        cardView.setBackgroundResource(R.drawable.shape_card_topcorners);

        name_of_subcategory = findViewById(R.id.name_of_subcategory);
        name_of_subcategory.setText(nameIntent);

        back = findViewById(R.id.subcategory_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TranslateAnimation animation_of_shim = new TranslateAnimation(0.0f, 0.0f,
                1500.0f, 0.0f);
        animation_of_shim.setDuration(300);
        progressBar.startAnimation(animation_of_shim);
        String cat = "Категории";
        ListView lvCat = (ListView)findViewById(R.id.list_subcat_create);
        data = new ArrayList<>();
              final String categoryList = "Категория задачи";
        String taskList = "Категория";
        final HttpsURLConnection[] myConnection = new HttpsURLConnection[1];
        final URL[] orzuEndpoint = new URL[1];


        @SuppressLint("StaticFieldLeak")
        class AsyncOrzuTasks extends AsyncTask<String,String,ArrayList<Map<String, Object>>> {



            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected ArrayList<Map<String, Object>> doInBackground(String... strings)  {
                orzuEndpoint[0] = null;
                JsonReader[] jsonReader = new JsonReader[1];

                try {
                    orzuEndpoint[0] = new URL("https://orzu.org/api?%20appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&lang=ru&opt=view_cat&cat_id=only_subcat&id=" + idIntent);


                    myConnection[0] =
                            (HttpsURLConnection) orzuEndpoint[0].openConnection();
                    if (myConnection[0].getResponseCode() == 200) {
                        // Success
                        InputStream responseBody = myConnection[0].getInputStream();
                        InputStreamReader responseBodyReader =
                                new InputStreamReader(responseBody, "UTF-8");

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
                }catch (MalformedURLException e) {
                    e.printStackTrace();
                }catch (IOException e) {
                    CreateTaskSubCategory.this.runOnUiThread(new Runnable() {
                        public void run() {
                            dialog = new Dialog(CreateTaskSubCategory.this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
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


                String[] from = { taskList};
                int[] to = { R.id.textItemCat};

                SimpleAdapter arrayAdapter = new SimpleAdapter(getBaseContext(), data, R.layout.cat_item, from, to);
                lvCat.setAdapter(arrayAdapter);
                lvCat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Map<String, Object> m_id = new HashMap<>(data.get(i));
                        final SharedPreferences prefs = getSharedPreferences(" ", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(Util.CAT_ID, String.valueOf(m_id.get("ID")));
                        editor.apply();
                        Common.taskName = data.get(i).get("Категория").toString();
                        Intent intent = new Intent(getApplication(), CreateTaskName.class);
                        startActivity(intent);
                    }
                });

                progressBar.setVisibility(View.GONE);
            }
        }


        AsyncOrzuTasks catTask = new AsyncOrzuTasks();
        catTask.execute();

    }

    private Map<String, Object> readMessage(JsonReader reader) throws IOException {
        long id;
        String text;
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
            }  else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return m;
    }


}