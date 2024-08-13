package orzu.org;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextPaint;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TaskViewMain extends AppCompatActivity implements View.OnClickListener {
    int count = 0;
    String id = "";
    String opt = "";
    String myTask = "";
    ArrayList<Map<String, Object>> data;
    Map<String, Object> m = new HashMap<>();
    Map<String, Object> m_new = new HashMap<>();
    final String taskList = "Задание";
    String narrList = "Описание";
    String idList = "ID";
    String useridList = "IDUser";
    String catidList = "CatID";
    String adress = "Adress";
    String param = "Param";
    final String priceList = "Цена";
    final String priceList2 = "Валюта";
    final String createList = "Создано";
    final String cityList = "Город";
    final String imageList = "Картинки";
    String needListdfrom = "Сроки";
    ProgressBar progressBar;
    AsyncOrzuTask task;
    TextView taskName;
    TextView taskAdr;
    TextView taskBeg;
    TextView taskAmt;
    TextView taskAmtOnce;
    TextView taskAmtTo;
    TextView taskNarr;
    TextView taskOpen;
    TextView nav_user;
    TextView taskId;
    TextView sad;
    TextView nat;
    TextView hap;
    ImageView taskMaketBack;
    ImageView taskMaketView;
    String mMessage;
    String mName;
    String image;
    String mFiName;
    String text;
    CardView card_of_shimmer;
    ShimmerFrameLayout shim;
    TextView buttonGettask;
    TextView buttonGettaskShim;
    LinearLayout viewUserInfo;
    LinearLayout img_of_task;
    String name = "";
    String idUser = "";
    String tokenUser = "";
    String nameUser = "";
    String catid = "";
    String amout = "";
    String cdate = "";
    String edate = "";
    String cdate_l = "";
    String level_l = "";
    String city = "";
    String address = "";
    String narrative = "";
    String date = "";
    String wallet = "";
    ImageView imageViewName;
    ImageView delete;
    int datetype;
    int placetype;
    int pricetype;
    DBHelper dbHelper;
    ImageView back;
    CardView cardView;
    List<String> images = new ArrayList<>();
    private ViewPager viewPager;
    private CirclePageIndicator circleIndicator;
    private pager_adapter myPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("orzutable", null, null, null, null, null, null);
        c.moveToFirst();
        int idColIndex = c.getColumnIndex("id");
        int tokenColIndex = c.getColumnIndex("token");
        idUser = c.getString(idColIndex);
        tokenUser = c.getString(tokenColIndex);
        c.moveToFirst();
        c.close();
        db.close();
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryPurpleTop));
        setContentView(R.layout.activity_task_view_main);
        Intent intent = getIntent();
        SharedPreferences prefs = getSharedPreferences(" ", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (intent != null) {
            id = "" + intent.getStringExtra("id");
            opt = intent.getStringExtra("opt");
            myTask = intent.getStringExtra("mytask");
            sad = findViewById(R.id.textSad);
            sad.setText(Common.neutral);
            nat = findViewById(R.id.textNatural);
            nat.setText(Common.sad);
            hap = findViewById(R.id.textHappy);
            hap.setText(Common.happy);
        }
        if (prefs.getBoolean("notif", false)) {
            id = prefs.getString("idd", "");
            opt = prefs.getString("opt", "");
            myTask = prefs.getString("mytask", "");
            editor.putBoolean("notif", false);
            editor.apply();
        }
        taskId = findViewById(R.id.taskId);
        taskId.setText(id);
        delete = findViewById(R.id.task_view_main_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(TaskViewMain.this).create();
                alertDialog.setTitle("Удаление");
                alertDialog.setMessage("Вы уверенны что хотите удалить задание?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ДА",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteTask(id);
                                Intent returnIntent = new Intent(TaskViewMain.this, Main2Activity.class);
                                startActivity(returnIntent);
                                finish();
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "НЕТ",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
        img_of_task = findViewById(R.id.img_of_task);
        shim = (ShimmerFrameLayout) findViewById(R.id.parentShimmerLayoutViewTask);
        shim.startShimmer();
        progressBar = findViewById(R.id.progres_create_task);
        progressBar.setVisibility(View.INVISIBLE);
        buttonGettask = findViewById(R.id.button_gettask);
        buttonGettaskShim = findViewById(R.id.button_gettask_shim);
        buttonGettask.setOnClickListener(this);
        viewUserInfo = findViewById(R.id.view_user_info);
        viewUserInfo.setOnClickListener(this);
        taskName = findViewById(R.id.taskName);
        sad = findViewById(R.id.textSad);
        nat = findViewById(R.id.textNatural);
        hap = findViewById(R.id.textHappy);
        taskAdr = findViewById(R.id.taskAdr);
        taskBeg = findViewById(R.id.taskDate);
        taskAmt = findViewById(R.id.taskPrice);
        taskAmtOnce = findViewById(R.id.taskPriceOnce);
        taskAmtTo = findViewById(R.id.taskMoney);
        taskNarr = findViewById(R.id.taskNarr);
        taskOpen = findViewById(R.id.taskOpen);

        nav_user = findViewById(R.id.taskCreator);
        taskMaketBack = findViewById(R.id.maket_task_white);
        taskMaketView = findViewById(R.id.imageback_new);
        imageViewName = findViewById(R.id.imageViewName);
        back = findViewById(R.id.task_view_main_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        card_of_shimmer = findViewById(R.id.card_of_shimmer);
        cardView = findViewById(R.id.card_of_task_view);
        cardView.setBackgroundResource(R.drawable.shape_card_topcorners);
        card_of_shimmer.setBackgroundResource(R.drawable.shape_card_topcorners);
        TranslateAnimation animation_of_shim = new TranslateAnimation(0.0f, 0.0f,
                1500.0f, 0.0f);
        animation_of_shim.setDuration(300);
        card_of_shimmer.startAnimation(animation_of_shim);
        Animation animation = new AlphaAnimation(1, 0); //to change visibility from visible to invisible
        animation.setDuration(1000); //1 second duration for each animation cycle
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE); //repeating indefinitely
        animation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.
        if (myTask.equals("my")) {
            myTaskBaby();
        }
        if (opt.equals("view")) {
            task = new AsyncOrzuTask();
            task.execute();
        } else {
            // DATE TYPE
            // 1 - в любое время 
            // 2 - утром (до 12)
            // 3 - днем (с 12 до 17)
            // 4 - вечером (с 17 до 22)
            // 5 - ночью (после 22)
            getSupportActionBar().setTitle("Создать задание");
            buttonGettask.setText("Опубликовать");
            taskMaketView.setVisibility(View.INVISIBLE);
            taskMaketBack.setVisibility(View.INVISIBLE);
            shim.setVisibility(View.INVISIBLE);

            TranslateAnimation animationn = new TranslateAnimation(0.0f, 0.0f,
                    1500.0f, 0.0f);
            animationn.setDuration(500);
            //animation.setFillAfter(true);
            cardView.startAnimation(animationn);
            buttonGettask.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    buttonGettask.setVisibility(View.VISIBLE);
                    Animation animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.zoom_in);
                    buttonGettask.startAnimation(animZoomIn);
                }
            }, animationn.getDuration());
            datetype = Integer.parseInt(prefs.getString(Util.TASK_DATETYPE, ""));
            placetype = Integer.parseInt(prefs.getString(Util.TASK_PLACETYPE, ""));
            pricetype = Integer.parseInt(prefs.getString(Util.TASK_PPRICE_TYPE, ""));
            name = prefs.getString(Util.TASK_NAME, "");
            nameUser = prefs.getString(Util.TASK_USERNAME, "");
            address = prefs.getString(Util.TASK_PLACE, "");
            cdate = prefs.getString(Util.TASK_CDATE, "");
            edate = prefs.getString(Util.TASK_EDATE, "");
            cdate_l = prefs.getString(Util.TASK_CDATEL, "");
            level_l = prefs.getString(Util.TASK_LEVEL_L, "");
            amout = prefs.getString(Util.TASK_PPRICE, "");
            catid = prefs.getString(Util.CAT_ID, "");
            narrative = prefs.getString(Util.TASK_ANNAT, "");
            if (datetype == 2) {
                date = "С: " + cdate + " До: " + edate;
            }
            if (datetype == 1) {
                date = cdate_l + " " + level_l;
            }
            if (datetype == 3) {
                date = "Дата по договоренности";
            }
            String outputPattern = "EEE, d MMMM yyyy HH:mm:ss";
            Locale myLocale = new Locale("ru", "RU");
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, myLocale);
            Date currentTime = Calendar.getInstance().getTime();
            taskName.setText(name);
            taskNarr.setText(narrative);
            taskAmt.setText(amout);
            taskAdr.setText(address);
            taskBeg.setText(date);
            taskOpen.setText(outputFormat.format(currentTime));
            nav_user.setText(nameUser);
            imageViewName.setImageBitmap(Common.bitmap);
            if (taskAmt.equals("Предложите цену")) {
                taskAmtOnce.setVisibility(View.INVISIBLE);
            }
        }
        if (!Common.values.isEmpty()) {
            img_of_task.setVisibility(View.VISIBLE);
            img_of_task.setBackgroundResource(R.drawable.shape_viewpager_corners);
            myPager = new pager_adapter(TaskViewMain.this, Common.values);
            viewPager = findViewById(R.id.view_pager3);
            viewPager.setAdapter(myPager);
            circleIndicator = findViewById(R.id.circle2);
            circleIndicator.setViewPager(viewPager);
        }
    }

    private void myTaskBaby() {
        buttonGettask.setText("Посмотреть отклики");
        buttonGettaskShim.setText("Посмотреть отклики");
        delete.setVisibility(View.VISIBLE);
        TranslateAnimation animationn = new TranslateAnimation(0.0f, 0.0f,
                1500.0f, 0.0f);
        animationn.setDuration(500);
        cardView.startAnimation(animationn);
        buttonGettask.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                buttonGettask.setVisibility(View.VISIBLE);
                Animation animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.zoom_in);
                buttonGettask.startAnimation(animZoomIn);
            }
        }, animationn.getDuration());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_gettask:
                if (!opt.equals("view")) {
                    try {
                        getCreateResponse();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (myTask.equals("my")) {
                    Intent intent = new Intent(this, FeedbackTask.class);
                    Common.taskId = id;
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(this, AddSuggest.class);
                    intent.putExtra("idTaskSuggest", id);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.view_user_info:
                if (!opt.equals("add")) {
                    Intent intent = new Intent(getApplication(), UserView.class);
                    intent.putExtra("idhis", m_new.get(useridList).toString());
                    startActivity(intent);
                    }

                break;
        }
    }

    class AsyncOrzuTask extends AsyncTask<String, String, ArrayList<Map<String, Object>>> {
        final HttpsURLConnection[] myConnection = new HttpsURLConnection[1];
        final URL[] orzuEndpoint = new URL[1];

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Map<String, Object>> doInBackground(String... strings) {
            m = new HashMap<>();
            m_new = new HashMap<>();
            m.put(priceList, "Предложите цену");
            m.put(priceList2, "");
            m.put(adress, "Удаленно");
            m.put(needListdfrom, "Дата по договоренности");
            m.put(narrList, "");
            orzuEndpoint[0] = null;
            JsonReader[] jsonReader = new JsonReader[1];
            try {
                orzuEndpoint[0] = new URL("https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&lang=ru&opt=view_task&tasks=" + id);
                myConnection[0] =
                        (HttpsURLConnection) orzuEndpoint[0].openConnection();
                if (myConnection[0].getResponseCode() == 200) {
                    // Success
                    InputStream responseBody = myConnection[0].getInputStream();
                    InputStreamReader responseBodyReader =
                            new InputStreamReader(responseBody, "UTF-8");
                    jsonReader[0] = new JsonReader(responseBodyReader);
                }
                jsonReader[0].beginArray();
                while (jsonReader[0].hasNext()) {
                    jsonReader[0].beginArray(); // Start processing the JSON object
                    while (jsonReader[0].hasNext()) { // Loop through all keys
                        m = readMessage(jsonReader[0]);
                        m_new.put(idList, m.get(idList));
                        m_new.put(taskList, m.get(taskList));
                        m_new.put(narrList, m.get(narrList));
                        m_new.put(priceList, m.get(priceList));
                        m_new.put(adress, m.get(adress));
                        m_new.put(createList, m.get(createList));
                        m_new.put(priceList2, m.get(priceList2));
                        m_new.put(needListdfrom, m.get(needListdfrom));
                        m_new.put(useridList, m.get(useridList));
                    }
                    jsonReader[0].endArray();
                }

                jsonReader[0].endArray();
                jsonReader[0].close();
                myConnection[0].disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                getUserResponse();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(ArrayList<Map<String, Object>> result) {
            super.onPostExecute(result);
            if(m_new.get(useridList).equals(Common.userId)){
                myTask = "my";
                myTaskBaby();
            }
            try {
                if (myConnection[0].getResponseCode() == 200) {
                    taskName.setText(m_new.get(taskList).toString());
                    taskNarr.setText(m_new.get(narrList).toString());
                    taskAmt.setText(m_new.get(priceList).toString());
                    taskAmtOnce.setText(m_new.get(priceList2).toString());
                    taskAdr.setText(m_new.get(adress).toString());
                    taskBeg.setText(m_new.get(needListdfrom).toString());
                    taskOpen.setText(m_new.get(createList).toString());
                    if (m.containsKey(imageList)) {
                        img_of_task.setVisibility(View.VISIBLE);
                        img_of_task.setBackgroundResource(R.drawable.shape_viewpager_corners);
                        myPager = new pager_adapter(TaskViewMain.this, images);
                        viewPager = findViewById(R.id.view_pager3);
                        viewPager.setAdapter(myPager);
                        circleIndicator = findViewById(R.id.circle2);
                        circleIndicator.setViewPager(viewPager);
                    }
                    if (m_new.get(priceList).toString().equals("Предложите цену")) {
                        taskAmtOnce.setVisibility(View.INVISIBLE);
                    }
                } else {
                    Dialog dialog = new Dialog(TaskViewMain.this, android.R.style.Theme_Material_Light_NoActionBar);
                    dialog.setContentView(R.layout.dialog_no_internet);
                    Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                    // if button is clicked, close the custom dialog
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AsyncOrzuTask task = new AsyncOrzuTask();
                            task.execute();
                            dialog.dismiss();
                        }
                    });
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.show();
                        }
                    }, 500);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Map<String, Object> readMessage(JsonReader reader) throws IOException {
        long id = 1;
        long userid = 1;
        String text = null;
        String narr = null;
        String adr = null;
        String creat = null;
        long catid = 1;
        String price = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "id":
                    if (reader.peek() != JsonToken.NULL) {
                        id = reader.nextLong();
                        m.put(idList, id);
                    } else reader.skipValue();
                    break;
                case "task":
                    if (reader.peek() != JsonToken.NULL) {
                        text = reader.nextString();
                        m.put(taskList, text);
                    } else reader.skipValue();
                    break;
                case "cat_id":
                    if (reader.peek() != JsonToken.NULL) {
                        catid = reader.nextLong();
                        m.put(catidList, catid);
                    } else reader.skipValue();
                    break;
                case "created_at":
                    if (reader.peek() != JsonToken.NULL) {
                        creat = reader.nextString();
                        m.put(createList, parseDateToddMMyyyy(creat));
                    } else reader.skipValue();
                    break;
                case "narrative":
                    if (reader.peek() != JsonToken.NULL) {
                        narr = reader.nextString();
                        m.put(narrList, narr);
                    } else reader.skipValue();
                    break;
                case "user_id":
                    if (reader.peek() != JsonToken.NULL) {
                        userid = reader.nextLong();
                        if (idUser.equals(userid + "")) {
                            TaskViewMain.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    delete.setVisibility(View.VISIBLE);
                                    myTaskBaby();
                                    myTask = "my";
                                }
                            });
                        }
                        m.put(useridList, userid);
                    } else reader.skipValue();
                    break;
                case "param":
                    param = reader.nextString();
                    if (reader.peek() != JsonToken.NULL) {
                        switch (param) {
                            case "address":
                                reader.nextName();
                                adr = reader.nextString();
                                m.put(adress, adr);
                                break;
                            case "remote":
                                reader.nextName();
                                adr = reader.nextString();
                                m.put(adress, "Удалённо");
                                break;
                            case "current":
                                reader.nextName();
                                if (reader.peek() != JsonToken.NULL) {
                                    adr = " " + reader.nextString();
                                    m.put(priceList2, adr);
                                } else {
                                    m.put(priceList2, " орзуcoin");
                                    reader.skipValue();
                                }
                                break;
                            case "amout":
                                reader.nextName();
                                price = reader.nextString();
                                m.put(priceList, price);
                                break;
                            case "no_amount":
                                reader.nextName();
                                price = reader.nextString();
                                m.put(priceList, "Предложите цену");
                                break;
                            case "cdate_l":
                                reader.nextName();
                                adr = reader.nextString();
                                m.put(needListdfrom, adr);
                                break;
                            case "cdate":
                                reader.nextName();
                                adr = reader.nextString();
                                m.put(needListdfrom, adr);
                                break;
                            case "edate":
                                reader.nextName();
                                adr = reader.nextString();
                                m.put(needListdfrom, "С: " + adr + "  До: " + m.get(needListdfrom));
                                break;
                            case "city":
                                reader.nextName();
                                if (reader.peek() != JsonToken.NULL) {
                                    adr = reader.nextString();
                                    m.put(cityList, adr);
                                } else {
                                    m.put(cityList, "Неизвестно");
                                    reader.skipValue();
                                }
                                break;
                            case "work_with":
                                reader.nextName();
                                adr = reader.nextString();
                                m.put(needListdfrom, "Дата по договоренности");
                                break;
                            case "image":
                                reader.nextName();
                                adr = reader.nextString();
                                m.put(imageList, adr);
                                images.add(adr);
                                break;
                            case "level_l":
                                reader.nextName();
                                adr = reader.nextString();
                                m.put("level_l", adr);
                                break;
                        }
                    } else reader.skipValue();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return m;
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

    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "EEE, d MMMM yyyy HH:mm:ss";
        String outputPatternTime = "HH:mm";
        Locale myLocale = new Locale("ru", "RU");
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, myLocale);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, myLocale);
        SimpleDateFormat outputFormatTime = new SimpleDateFormat(outputPatternTime, myLocale);
        int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int day = 0;
        String strday = null;
        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(time);
            strday = (String) android.text.format.DateFormat.format("dd", date);
            day = Integer.parseInt(strday);
            str = "в " + outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (today == day) {
            str = "сегодня в " + outputFormatTime.format(date);
            ;
        } else if (today == (day + 1)) {
            str = "вчера в " + outputFormatTime.format(date);
        }
        return str;
    }

    public void getUserResponse() throws IOException {
        String url = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&lang=ru&opt=view_user&user=" + m.get(useridList) + "&param=more";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                TaskViewMain.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(TaskViewMain.this, android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    getUserResponse();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                                dialog.dismiss();
                            }
                        });
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.show();
                            }
                        }, 500);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mMessage = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(mMessage);
                    mName = jsonObject.getString("name");
                    image = jsonObject.getString("avatar");
                    mFiName = jsonObject.getString("fname");
                    String sadnum = String.valueOf(jsonObject.getLong("sad"));
                    String natnum = String.valueOf(jsonObject.getLong("neutral"));
                    String hapnum = String.valueOf(jsonObject.getLong("happy"));
                    if (myTask.equals("my")) {
                        wallet = jsonObject.getString("wallet") + " Ni" + " \n -100 Ni";
                        Common.wallet = jsonObject.getString("wallet");
                    }
                    if (mFiName.equals("null")) {
                        text = mName;
                    } else text = mName + " " + mFiName;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            nav_user.setText(text);
                            sad.setText(sadnum);
                            nat.setText(natnum);
                            hap.setText(hapnum);

                            Picasso.get().load("https://orzu.org" + image).fit().centerCrop().into(imageViewName);
                            Animation animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(),
                                    R.anim.zoom_in);
                            buttonGettask.startAnimation(animZoomIn);
                        }
                    });
                    TaskViewMain.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            taskMaketView.setVisibility(View.INVISIBLE);
                            taskMaketBack.setVisibility(View.INVISIBLE);
                            shim.setVisibility(View.INVISIBLE);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getCreateResponse() throws IOException {
        progressBar.setVisibility(View.VISIBLE);
        // DATE TYPE
        // 1. Точная дата 
        // 2. Указать период 
        // 3. Договорюсь с исполнителем
        //   SPINNER NUMBER
        //   1 - в любое время 
        //   2 - утром (до 12)
        //   3 - днем (с 12 до 17)
        //   4 - вечером (с 17 до 22)
        //   5 - ночью (после 22)
        // PLACE TYPE
        // 1. Указать место 
        // 2. Удаленно
        // PRICE TYPE
        // 1. Указать цену самому 
        // 2. Исполнитель предложит цену
        String urlLocation = "";
        String urlTerm = "";
        String urlPrice = "";
        if (datetype == 2) {
            urlTerm = "&date=period" + "&periodA=" + cdate + "&periodB=" + edate;
        }
        if (datetype == 1) {
            urlTerm = "&date=exact" + "&exactD=" + cdate_l + "&exactT=" + level_l;
        }
        if (datetype == 3) {
            urlTerm = "&date=wtasker";
        }
        if (placetype == 1) {
            urlLocation = "&location=indicate" + "&locationVal=" + address;
        }
        if (placetype == 2) {
            urlLocation = "&location=remote";
        }
        if (pricetype == 1) {
            urlPrice = "&price=indicate&priceVal=" + amout;
        }
        if (pricetype == 2) {
            urlPrice = "&price=wtasker";
        }
        String url = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS" +
                "&opt=input_task" +
                "&task=" + name +
                "&catid=" + catid +
                "&narrative=" + narrative +
                "&userid=" + idUser +
                urlTerm +
                urlLocation +
                urlPrice +
                "&utoken=" + tokenUser;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                TaskViewMain.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(TaskViewMain.this, android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    getCreateResponse();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                                dialog.dismiss();
                            }
                        });
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.show();
                            }
                        }, 500);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String[] mMessage = response.body().string().split(":");
                final char dm = (char) 34;
                if (mMessage[0].equals(dm + "Task created")) {
                    if (!Common.values.isEmpty()) {
                        getEditAvatarResponse(mMessage[1].substring(0, mMessage[1].length() - 1));
                    } else {
                        Intent intent = new Intent(TaskViewMain.this, Congratz.class);
                        Common.values.clear();
                        startActivity(intent);
                        finish();
                        CreateTaskDetail.fa.finish();
                        CreateTaskAmout.fa.finish();
                        CreateTaskTerm.fa.finish();
                        CreateTaskPlace.fa.finish();
                        CreateTaskName.fa.finish();
                        CreateTaskCategory.fa.finish();
                        CreateTaskSubCategory.fa.finish();
                    }
                }
               // minusBalance();
                TaskViewMain.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });

            }
        });
    }

    public boolean getEditAvatarResponse(String id) throws IOException {
        if (count >= Common.values.size()) {
            TaskViewMain.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(TaskViewMain.this, "Фото добавленно", Toast.LENGTH_SHORT).show();
                }
            });
            count = 0;
            Intent intent = new Intent(TaskViewMain.this, Congratz.class);
            Common.values.clear();
            startActivity(intent);
            finish();
            CreateTaskDetail.fa.finish();
            CreateTaskAmout.fa.finish();
            CreateTaskTerm.fa.finish();
            CreateTaskPlace.fa.finish();
            CreateTaskName.fa.finish();
            CreateTaskCategory.fa.finish();
            CreateTaskSubCategory.fa.finish();
            return true;
        }
        String url = "https://orzu.org/api/avatar";
        OkHttpClient client = new OkHttpClient();
        File myFile = new File(Uri.parse(Common.values.get(count)).getPath());
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", myFile.getName(),
                        RequestBody.create(MediaType.parse("text/csv"), myFile))
                .addFormDataPart("task_id", id)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                TaskViewMain.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(TaskViewMain.this, android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    getEditAvatarResponse(id);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
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
            public void onResponse(Call call, Response response) throws IOException {
                count++;
                String mMessage = response.body().string();
                TaskViewMain.this.runOnUiThread(new Runnable() {
                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
                        buttonGettask.setVisibility(View.GONE);
                    }
                });
                getEditAvatarResponse(id);
            }
        });
        return true;
    }

    private void deleteTask(String id) {
        String requestUrl = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&lang=ru&opt=view_task&tasks=all&userid=" + idUser + "&delete=" + id;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, requestUrl, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace(); //log the error resulting from the request for diagnosis/debugging
                Dialog dialog = new Dialog(TaskViewMain.this, android.R.style.Theme_Material_Light_NoActionBar);
                dialog.setContentView(R.layout.dialog_no_internet);
                Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteTask(id);
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
//make the request to your server as indicated in your request url
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void minusBalance() {
        String requestUrl = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=user_param&act=edit_bonus_minus&userid=" + idUser + "&utoken=" + tokenUser;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, requestUrl, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Common.wallet = response;
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace(); //log the error resulting from the request for diagnosis/debugging
                Dialog dialog = new Dialog(TaskViewMain.this, android.R.style.Theme_Material_Light_NoActionBar);
                dialog.setContentView(R.layout.dialog_no_internet);
                Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        minusBalance();
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
//make the request to your server as indicated in your request url
        Volley.newRequestQueue(this).add(stringRequest);
    }
}
