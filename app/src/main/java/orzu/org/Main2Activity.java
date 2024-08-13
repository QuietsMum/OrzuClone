package orzu.org;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.CampaignTrackingReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.pusher.pushnotifications.PushNotificationReceivedListener;
import com.pusher.pushnotifications.PushNotifications;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.UserAttributes;
import io.intercom.android.sdk.identity.Registration;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    ArrayList<String> subsServer = new ArrayList<>();
    Fragment fragment = null;
    Class fragmentClass = null;
    String mMessage;
    String mName;
    String image;
    String mFiName;
    String text;
    String idUser;
    String token;
    DBHelper dbHelper;
    NavigationView navigationView;
    NavigationView nav_right;
    LinearLayout intercomBtn;
    ImageView img;
    RelativeLayout userviewBtn;
    TextView nav_user_name;
    String city = "";
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    int index = 1;
    DrawerLayout drawer;
    Drawable drawable;
    static String[] category;
    static List<category_model> categories = new ArrayList<>();
    List<category_model> subcategories = new ArrayList<>();
    Spinner category_right_side;
    Spinner subcategory_right_side;
    TextView next_right_side;
    FragmentManager fm;
    List<Bonuses> bonuses = new ArrayList<>();
    SharedPreferences prefs;
    private void setupBeams() {
        prefs = getSharedPreferences(" ", Context.MODE_PRIVATE);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    }
                });
        FirebaseMessaging.getInstance().subscribeToTopic("user_" + idUser)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
        for (int i = 0; i < subsServer.size(); i++) {
            FirebaseMessaging.getInstance().subscribeToTopic("cat_"+subsServer.get(i))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });
        }

        try {
            getUserResponse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        fm = getSupportFragmentManager();
    }

    public void requestSubsServerMain() {
        dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("orzutable", null, null, null, null, null, null);
        c.moveToFirst();
        int idColIndex = c.getColumnIndex("id");
        int tokenColIndex = c.getColumnIndex("token");
        idUser = c.getString(idColIndex);
        token = c.getString(tokenColIndex);

        Log.e("qweerty", token);
        c.close();
        db.close();
        String url = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=view_user&user_cat=" + idUser;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Main2Activity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(Main2Activity.this, android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestSubsServerMain();
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
                final String mMessage = response.body().string();
                if (!mMessage.equals("\"No subscribe categories\"")) {
                    try {
                        JSONObject jsonObject = new JSONObject(mMessage);
                        Iterator<String> iter = jsonObject.keys();
                        while (iter.hasNext()) {
                            String key = iter.next();
                            subsServer.add(key);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setupBeams();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        PushNotifications.setOnMessageReceivedListenerForVisibleActivity(this, new PushNotificationReceivedListener() {
            @Override
            public void onMessageReceived(RemoteMessage remoteMessage) {
                String messagePayload = remoteMessage.getData().get("inAppNotificationMessage");
                if (messagePayload == null) {
                    // Message payload was not set for this notification
                } else {
                    // Now update the UI based on your message payload!
                }
            }
        });
        if (index == 0) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, new Fragment3(), "fragment3").commit();
            navigationView.setCheckedItem(R.id.menu_none);
        } else if (index == 1) {
            toolbar = findViewById(R.id.toolbar);
            toolbar.setBackgroundColor(getResources().getColor(R.color.back_for_feed));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryGrey));
            setTitle("Найти задания");
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, new Fragment1()).commit();
            navigationView.setCheckedItem(R.id.first);
        } else if (index == 3) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, new Fragment4()).commit();
            navigationView.setCheckedItem(R.id.third);
        } else if (index == 4) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, new Fragment2()).commit();
            navigationView.setCheckedItem(R.id.fourth);
        } else if (index == 6) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, new ChatsView()).commit();
            navigationView.setCheckedItem(R.id.sixth);
        }else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, new Fragment5(), "Fragment5").commit();
            navigationView.setCheckedItem(R.id.fifth);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorTextDark));
        setContentView(R.layout.activity_main2);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.nav_view);
        nav_right = findViewById(R.id.nav_view2);
        category_right_side = findViewById(R.id.category_right_side);
        subcategory_right_side = findViewById(R.id.subcategory_right_side);
        next_right_side = findViewById(R.id.next_right_side);
        next_right_side.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAllPartnersSorted(subcategories.get(subcategory_right_side.getSelectedItemPosition()).getId(), "Алматы", "DESC");
            }
        });

        getCategories();

        navigationView.setItemIconTintList(null);
        MaterialShapeDrawable navViewBackground = (MaterialShapeDrawable) navigationView.getBackground();
        navViewBackground.setShapeAppearanceModel(
                navViewBackground.getShapeAppearanceModel()
                        .toBuilder()
                        .setBottomRightCorner(CornerFamily.ROUNDED, 160)
                        .build());
        SharedPreferences prefs = Main2Activity.this.getSharedPreferences(" ", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("notif", false);
        editor.apply();
        dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("orzutable", null, null, null, null, null, null);
        c.moveToFirst();
        int idColIndex = c.getColumnIndex("id");
        int tokenColIndex = c.getColumnIndex("token");
        idUser = c.getString(idColIndex);
        token = c.getString(tokenColIndex);
        Common.userId = idUser;
        Common.utoken = c.getString(tokenColIndex);
        c.close();

        Intercom.initialize(getApplication(), "android_sdk-805f0d44d62fbc8e72058b9c8eee61c94c43c874", "p479kps8");
        intercomBtn = findViewById(R.id.techsupp);
        intercomBtn.setOnClickListener(this);
        drawer = findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, findViewById(R.id.nav_view2));
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(false);
        drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu, getApplication().getTheme());
        toggle.setHomeAsUpIndicator(drawable);
        toggle.syncState();
        View header = navigationView.getHeaderView(0);
        img = header.findViewById(R.id.textView);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));

        Common.city1 = Common.city;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        SharedPreferences prefs = getSharedPreferences(" ", Context.MODE_PRIVATE);
        if (prefs.getBoolean("openNotification", false)) {
            onNavigationItemSelected(navigationView.getMenu().getItem(3));
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("openNotification", false);
            editor.apply();
        }
        if (prefs.getBoolean("openChats", false)) {
            onNavigationItemSelected(navigationView.getMenu().getItem(6));
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("openChats", false);
            editor.apply();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (id == R.id.first) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, findViewById(R.id.nav_view2));
            index = 1;
            fragmentClass = Fragment1.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            toolbar.setBackgroundColor(getResources().getColor(R.color.back_for_feed));
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorTextDark));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryGrey));


            toggle.setDrawerIndicatorEnabled(false);
            drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu2, getApplication().getTheme());
            toggle.setHomeAsUpIndicator(drawable);
            toggle.syncState();
            toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (drawer.isDrawerVisible(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    } else {
                        drawer.openDrawer(GravityCompat.START);
                    }
                }
            });


            // Вставляем фрагмент, заменяя текущий фрагмент
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
            // Выделяем выбранный пункт меню в шторке
            item.setChecked(true);
            // Выводим выбранный пункт в заголовке
            setTitle(item.getTitle());
        } else if (id == R.id.second) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, findViewById(R.id.nav_view2));
            index = 1;
            toggle.setDrawerIndicatorEnabled(false);
            drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu2, getApplication().getTheme());
            toggle.setHomeAsUpIndicator(drawable);
            toggle.syncState();
            toolbar.setBackgroundColor(getResources().getColor(R.color.back_for_feed));
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorTextDark));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryGrey));
            Intent intent = new Intent(this, CreateTaskCategory.class);
            startActivity(intent);
        } else if (id == R.id.third) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, findViewById(R.id.nav_view2));
            index = 3;
            toolbar.setBackgroundColor(getResources().getColor(R.color.back_for_feed));
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorTextDark));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryGrey));


            toggle.setDrawerIndicatorEnabled(false);
            drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu2, getApplication().getTheme());
            toggle.setHomeAsUpIndicator(drawable);
            toggle.syncState();
            toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (drawer.isDrawerVisible(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    } else {
                        drawer.openDrawer(GravityCompat.START);
                    }
                }
            });


            fragmentClass = Fragment4.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Вставляем фрагмент, заменяя текущий фрагмент
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
            // Выделяем выбранный пункт меню в шторке
            item.setChecked(true);
            // Выводим выбранный пункт в заголовке
            setTitle(item.getTitle());
        } else if (id == R.id.fourth) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, findViewById(R.id.nav_view2));
            index = 4;
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryPurpleTop));
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryLight));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryPurpleTop));

            toggle.setDrawerIndicatorEnabled(false);
            drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu, getApplication().getTheme());
            toggle.setHomeAsUpIndicator(drawable);
            toggle.syncState();
            toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (drawer.isDrawerVisible(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    } else {
                        drawer.openDrawer(GravityCompat.START);
                    }
                }
            });


            fragmentClass = Fragment2.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Вставляем фрагмент, заменяя текущий фрагмент
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
            // Выделяем выбранный пункт меню в шторке
            item.setChecked(true);
            // Выводим выбранный пункт в заголовке
            setTitle(item.getTitle());
        } else if (id == R.id.fifth) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, findViewById(R.id.nav_view2));
            index = 5;
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryPurpleTop));
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryLight));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryPurpleTop));

            toggle.setDrawerIndicatorEnabled(false);
            drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu, getApplication().getTheme());
            toggle.setHomeAsUpIndicator(drawable);
            toggle.syncState();
            toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (drawer.isDrawerVisible(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    } else {
                        drawer.openDrawer(GravityCompat.START);
                    }
                }
            });


            // Вставляем фрагмент, заменяя текущий фрагмент
            FragmentManager fragmentManager = getSupportFragmentManager();
            fm = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, new Fragment5(), "Fragment5").commit();
            // Выделяем выбранный пункт меню в шторке
            item.setChecked(true);
            // Выводим выбранный пункт в заголовке
            setTitle(item.getTitle());
        }
        else if (id == R.id.sixth) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, findViewById(R.id.nav_view2));
            index = 6;
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryPurpleTop));
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryLight));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryPurpleTop));

            toggle.setDrawerIndicatorEnabled(false);
            drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu, getApplication().getTheme());
            toggle.setHomeAsUpIndicator(drawable);
            toggle.syncState();
            toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (drawer.isDrawerVisible(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    } else {
                        drawer.openDrawer(GravityCompat.START);
                    }
                }
            });


            // Вставляем фрагмент, заменяя текущий фрагмент
            FragmentManager fragmentManager = getSupportFragmentManager();
            fm = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, new ChatsView(), "Fragment5").commit();
            // Выделяем выбранный пункт меню в шторке
            item.setChecked(true);
            // Выводим выбранный пункт в заголовке
            setTitle(item.getTitle());
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("fragment3");
            fragment.onActivityResult(requestCode, resultCode, data);
        } else if (data != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, new Fragment1()).commit();
        }
    }

    private void getAllPartnersSorted(String catid, String city, String sort) {
        bonuses.clear();
        String requestUrl = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=user_param&act=partners_list_sort" +
                "&catid=" + catid +
                "&city=" + city +
                "&sort=" + sort;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, requestUrl, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("\"\\u041d\\u0435\\u0442 \\u043f\\u0430\\u0440\\u0442\\u043d\\u0451\\u0440\\u043e\\u0432 \\u0432 \\u0434\\u0430\\u043d\\u043d\\u043e\\u0439 \\u043a\\u0430\\u0442\\u0435\\u0433\\u043e\\u0440\\u0438\\u0438\"")) {
                    Fragment5 fragment5 = (Fragment5) fm.findFragmentByTag("Fragment5");
                    fragment5.sortIsPressed(bonuses);
                } else {
                    try {
                        JSONArray j = new JSONArray(response);
                        for (int i = 0; i < j.length(); i++) {
                            JSONObject object = j.getJSONObject(i);
                            bonuses.add(new Bonuses(object.getString("id"), object.getString("name"), object.getString("percent"), object.getString("logo"), object.getString("discription")));
                        }

                        Fragment5 fragment5 = (Fragment5) fm.findFragmentByTag("Fragment5");
                        fragment5.sortIsPressed(bonuses);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace(); //log the error resulting from the request for diagnosis/debugging
                Dialog dialog = new Dialog(Main2Activity.this, android.R.style.Theme_Material_Light_NoActionBar);
                dialog.setContentView(R.layout.dialog_no_internet);
                Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getAllPartnersSorted(catid, city, sort);
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
        Volley.newRequestQueue(Main2Activity.this).add(stringRequest);
    }


    public void getUserResponse() throws IOException {
        dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("orzutable", null, null, null, null, null, null);
        c.moveToFirst();
        int idColIndex = c.getColumnIndex("id");
        int tokenColIndex = c.getColumnIndex("token");
        idUser = c.getString(idColIndex);
        Common.userId = idUser;
        Common.utoken = c.getString(tokenColIndex);
        c.close();
        db.close();
        String url = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&lang=ru&opt=view_user&user=" + idUser + "&param=more";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Main2Activity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(Main2Activity.this, android.R.style.Theme_Material_Light_NoActionBar);
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
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mMessage = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(mMessage);
                    mName = jsonObject.getString("name");
                    mFiName = jsonObject.getString("fname");
                    image = jsonObject.getString("avatar");
                    city = jsonObject.getString("city");
                    Common.sad = jsonObject.getString("sad");
                    Common.neutral = jsonObject.getString("neutral");
                    Common.happy = jsonObject.getString("happy");
                    Common.wallet = jsonObject.getString("wallet");
                    if (mFiName.equals("null")) {
                        text = mName;
                    } else text = mName + "\n" + mFiName;
                    final SharedPreferences prefs = Main2Activity.this.getSharedPreferences(" ", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(Util.TASK_USERNAME, mName);
                    if (!city.isEmpty()) {
                        editor.putString("UserCityPref", city);
                    } else {
                        Intent intent = new Intent(Main2Activity.this, RegistCity.class);
                        startActivity(intent);
                        finish();
                    }
                    editor.apply();
                    View hView = navigationView.getHeaderView(0);
                    nav_user_name = (TextView) hView.findViewById(R.id.textViewName);
                    userviewBtn = hView.findViewById(R.id.headerOfdrawer);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            nav_user_name.setText(text);
                            Common.name = text;
                            Common.name_only = mName;
                            userviewBtn.setOnClickListener(Main2Activity.this);
                            Common.fragmentshimmer = true;
                            new DownloadImage().execute("https://orzu.org" + image);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.techsupp:
                Registration registration = Registration.create().withUserId(idUser);
                UserAttributes userAttributes = new UserAttributes.Builder()
                        .withName(mName)
                        .withUserId(idUser)
                        .build();
                Intercom.client().updateUser(userAttributes);
                Intercom.client().handlePushMessage();
                Intercom.client().registerIdentifiedUser(registration);
                Intercom.client().displayMessenger();
                Intercom.client().setBottomPadding(20);
                break;
            case R.id.headerOfdrawer:
                navigationView.setCheckedItem(R.id.menu_none);
                index = 0;
                fragmentClass = Fragment3.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryPurpleTop));
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryPurpleTop));

                toggle.setDrawerIndicatorEnabled(false);
                drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu, getApplication().getTheme());
                toggle.setHomeAsUpIndicator(drawable);
                toggle.syncState();
                toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (drawer.isDrawerVisible(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        } else {
                            drawer.openDrawer(GravityCompat.START);
                        }
                    }
                });


                // Вставляем фрагмент, заменяя текущий фрагмент
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, fragment, "fragment3").commit();
                setTitle("");
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                final SharedPreferences prefs = getSharedPreferences(" ", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(Util.TASK_USERID, idUser);
                editor.apply();
                break;
        }
    }


    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        private String TAG = "DownloadImage";

        private Bitmap downloadImageBitmap(String sUrl) {
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new URL(sUrl).openStream();   // Download Image from URL
                bitmap = BitmapFactory.decodeStream(inputStream);       // Decode Bitmap
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadImageBitmap(params[0]);
        }

        protected void onPostExecute(Bitmap result) {
            saveImage(getApplicationContext(), result, "my_image.jpeg");
        }
    }

    ImageView nav_user;

    public void saveImage(Context context, Bitmap b, String imageName) {
        FileOutputStream foStream;
        try {
            foStream = context.openFileOutput(imageName, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.PNG, 100, foStream);
            foStream.close();
            File file = getApplicationContext().getFileStreamPath("my_image.jpeg");
            if (file.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                View hView = navigationView.getHeaderView(0);
                nav_user = (ImageView) hView.findViewById(R.id.imageViewName);
                nav_user.setImageBitmap(myBitmap);
                Common.bitmap = myBitmap;
                Common.d = nav_user.getDrawable();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeImage() {
        nav_user.setImageDrawable(Common.d);
        nav_user_name.setText(Common.name);
    }

    private void getCategories() {
        String requestUrl = "https://orzu.org/api?%20appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&lang=ru&opt=view_cat&cat_id=only_parent";
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, requestUrl, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray j = new JSONArray(response);
                    category = new String[j.length()];
                    for (int i = 0; i < j.length(); i++) {
                        try {
                            JSONObject object = j.getJSONObject(i);
                            categories.add(new category_model(object.getString("id"), object.getString("name"), object.getString("parent_id")));
                            category[i] = object.getString("name");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(Main2Activity.this,
                            android.R.layout.simple_spinner_dropdown_item, category);
                    category_right_side.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    category_right_side.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            getSubCategories(categories.get(i).getId());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    requestSubsServerMain();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace(); //log the error resulting from the request for diagnosis/debugging
                Dialog dialog = new Dialog(Main2Activity.this, android.R.style.Theme_Material_Light_NoActionBar);
                dialog.setContentView(R.layout.dialog_no_internet);
                Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getCategories();
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
        Volley.newRequestQueue(Objects.requireNonNull(this)).add(stringRequest);
    }

    private void getSubCategories(String id) {
        subcategories.clear();
        String requestUrl = "https://orzu.org/api?%20appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&lang=ru&opt=view_cat&cat_id=only_subcat&id=" + id;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, requestUrl, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray j = new JSONArray(response);
                    String[] cities = new String[j.length()];
                    for (int i = 0; i < j.length(); i++) {
                        try {
                            JSONObject object = j.getJSONObject(i);
                            subcategories.add(new category_model(object.getString("id"), object.getString("name"), object.getString("parent_id")));
                            cities[i] = object.getString("name");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(Main2Activity.this,
                            android.R.layout.simple_spinner_dropdown_item, cities);
                    subcategory_right_side.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace(); //log the error resulting from the request for diagnosis/debugging
                Dialog dialog = new Dialog(Main2Activity.this, android.R.style.Theme_Material_Light_NoActionBar);
                dialog.setContentView(R.layout.dialog_no_internet);
                Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getSubCategories(id);
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
        Volley.newRequestQueue(Objects.requireNonNull(Main2Activity.this)).add(stringRequest);
    }
}
