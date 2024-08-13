package orzu.org;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import orzu.org.ChatActivity;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NotificationsMessagingService extends FirebaseMessagingService {
    Context context = this;
    DBHelper dbHelper;
    SharedPreferences prefs;
    String type,taskId;
    String mess = "";
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        prefs = getSharedPreferences(" ", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Log.wtf("asdasd",remoteMessage.getData()+"");
        if (remoteMessage.getData().containsKey("chatID")) {
            showNotification(remoteMessage);
        } else
        if(remoteMessage.getData().get("city").equals(prefs.getString("UserCityPref", ""))) {

            dbHelper = new DBHelper(this);
            editor.putString("New_task", remoteMessage.getData() + "");
            editor.apply();
            if (remoteMessage.getData().containsKey("ID")) {
                showNotification(remoteMessage);
            }
        }
    }

    private void showNotification(@NotNull RemoteMessage remoteMessage) {
        String[] ar = remoteMessage.getData().get("ID").split("[, ?.@]+");
        if (remoteMessage.getData().containsKey("chatID")) {
           mess = remoteMessage.getData().get("chatID");
        }

        Intent intent = new Intent(this, TaskViewMain.class);
        final SharedPreferences prefs = Objects.requireNonNull(this).getSharedPreferences(" ", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String id;
        if (ar.length >= 2) {
            editor.putString("mytask", "my");
            type = "my";
            id = ar[1];
            getFeedback(ar[1]);
        } else {
            editor.putString("mytask", "not");
            id = remoteMessage.getData().get("ID");
            type = "not";
            getTask(id);
        }
        if (!mess.equals("")) {
            messageDeliv(mess);
            if (mess.equals(Common.chatId)){
                ChatActivity.addhisMes(remoteMessage.getNotification().getBody(), remoteMessage.getData().get("date"));
            } else {
                editor.putString("messageVied", mess);
                editor.apply();
                intent = new Intent(this, ChatActivity.class);
                intent.putExtra("chatID", remoteMessage.getData().get("chatID"));
                intent.putExtra("user_to", remoteMessage.getData().get("ID"));
                PendingIntent activity = PendingIntent.getActivity(this, 0, intent, 0);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                String NOTIFICATION_CHANNEL_ID = "orzu.org.test";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification",
                            NotificationManager.IMPORTANCE_DEFAULT);
                    notificationChannel.setDescription("Hello");
                    notificationChannel.enableLights(true);
                    notificationChannel.setLightColor(Color.BLUE);
                    notificationManager.createNotificationChannel(notificationChannel);
                }
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
                notificationBuilder.setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.logo_in_desktop)
                        .setContentTitle(remoteMessage.getNotification().getTitle())
                        .setContentText(remoteMessage.getNotification().getBody())
                        .setContentInfo("Info")
                        .setContentIntent(activity);
                notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
            }
        } else {
            taskId = id;
            editor.putString("idd", id);
            editor.putString("New_task", remoteMessage.getData() + "");
            editor.putString("opt", "view");
            editor.putBoolean("notif", true);
            editor.apply();
            PendingIntent activity = PendingIntent.getActivity(this, 0, intent, 0);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String NOTIFICATION_CHANNEL_ID = "orzu.org.test";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.setDescription("Hello");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.BLUE);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.logo_in_desktop)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setContentInfo("Info")
                    .setContentIntent(activity);
            notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
        }
    }


    private void messageDeliv(Object id) {
        String url = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS" +
                "&opt=user_chats" +
                "&act=change_del" +
                "&chat_id=" + id;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Dialog dialog = new Dialog(NotificationsMessagingService.this, android.R.style.Theme_Material_Light_NoActionBar);
                dialog.setContentView(R.layout.dialog_no_internet);
                Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        messageDeliv(id);
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
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                SharedPreferences prefs = getSharedPreferences(" ", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("messageVied", "");
                editor.apply();

            }
        });
    }


    public void getTask(String id) {
        String requestUrl = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&lang=ru&opt=view_task&tasks=" + id;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, requestUrl, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray j = new JSONArray(response);
                    JSONArray task_detail_arr = j.getJSONArray(0);
                    JSONObject task_detail = task_detail_arr.getJSONObject(0);
                    String task = task_detail.getString("task");
                    String created_at = task_detail.getString("created_at");
                    String id = task_detail.getString("id");


                    JSONArray arr = j.getJSONArray(1);
                    String city = arr.getJSONObject(0).getString("value");
                    String work_with = arr.getJSONObject(4).getString("value");

                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues cv = new ContentValues();
                    cv.put("created_at", created_at);
                    cv.put("type", type);
                    cv.put("id", id);
                    cv.put("title", task);
                    cv.put("city", city);
                    cv.put("work_with", work_with);
                    db.insert("orzunotif", null, cv);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace(); //log the error resulting from the request for diagnosis/debugging
                Dialog dialog = new Dialog(context, android.R.style.Theme_Material_Light_NoActionBar);
                dialog.setContentView(R.layout.dialog_no_internet);
                Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getTask(id);
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

    public void getFeedback(String id) {
        String requestUrl = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=task_requests&act=view&task_id=" + id;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, requestUrl, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if(response.equals("\"No request yet\"")) {
                        JSONArray j = new JSONArray(response);
                        int index = j.length() - 1;
                        JSONObject object = j.getJSONObject(index);
                        Date date = Calendar.getInstance().getTime();
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        String strDate = dateFormat.format(date);

                        String created_at = strDate;
                        String username = object.getString("username");
                        String narrative = object.getString("narrative");
                        String amount = object.getString("amount");
                        String current = object.getString("current");

                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues cv = new ContentValues();
                        cv.put("created_at", created_at);
                        cv.put("type", "my");
                        cv.put("id", taskId);
                        cv.put("title", username);
                        cv.put("city", amount + " " + current);
                        cv.put("work_with", narrative);
                        db.insert("orzunotif", null, cv);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace(); //log the error resulting from the request for diagnosis/debugging
                Dialog dialog = new Dialog(context, android.R.style.Theme_Material_Light_NoActionBar);
                dialog.setContentView(R.layout.dialog_no_internet);
                Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getFeedback(id);
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

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }
}
