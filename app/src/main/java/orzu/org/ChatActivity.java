package orzu.org;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import orzu.org.chat.ChatAdapter;
import orzu.org.chat.ChattingAdapter;
import orzu.org.chat.chatItems;
import orzu.org.chat.message_interface;
import orzu.org.chat.my_message;
import orzu.org.chat.their_message;

public class ChatActivity extends AppCompatActivity {

    static RecyclerView messages_view;
    EditText editText;
    ImageButton send;
    static List<message_interface> messages = new ArrayList<>();
    static ChattingAdapter adapter;
    String mMessage;
    DBHelper dbHelper;
    String idUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_back));
        getSupportActionBar().setTitle(Common.nameOfChat);
        getSupportActionBar().setElevation(0);
        messages_view = findViewById(R.id.messages_view);
        editText = findViewById(R.id.editText);
        send = findViewById(R.id.chat_send_button);
        messages = new ArrayList<>();
        Intent intent_o = getIntent();
        if(intent_o.hasExtra("chatID")){
            Common.chatId = intent_o.getStringExtra("chatID");
            Common.user_to = intent_o.getStringExtra("user_to");
        }

        messages_view.setHasFixedSize(true);
        messages_view.setLayoutManager(new LinearLayoutManager(this));
        messages_view.setNestedScrollingEnabled(false);
        final SharedPreferences prefs = Objects.requireNonNull(this).getSharedPreferences(" ", Context.MODE_PRIVATE);
        idUser = prefs.getString(Util.TASK_USERID, "");
        if (prefs.getString("messageVied", "").equals(Common.chatId)){

            messageViewed(prefs.getString("messageVied", ""));
        }

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        messages_view.smoothScrollToPosition(Objects.requireNonNull(messages_view.getAdapter()).getItemCount() - 1);
                    }
                }, 200);
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentTimeStamp = dateFormat.format(new Date());
                messages.add(new my_message(editText.getText().toString(), currentTimeStamp));
                sendMess(editText.getText().toString());
                adapter.notifyDataSetChanged();
                messages_view.smoothScrollToPosition(Objects.requireNonNull(messages_view.getAdapter()).getItemCount() - 1);
                editText.setText("");
            }
        });

        try {
            getMessages();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addhisMes(String mess, String date) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                messages.add(new their_message(mess, date));
                adapter.notifyDataSetChanged();
                messages_view.smoothScrollToPosition(Objects.requireNonNull(messages_view.getAdapter()).getItemCount() - 1);
            }
        });
    }

    private void sendMess(Object message) {
        dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("orzutable", null, null, null, null, null, null);
        c.moveToFirst();
        int idColIndex = c.getColumnIndex("id");
        int tokenColIndex = c.getColumnIndex("token");
        idUser = c.getString(idColIndex);
        String token = c.getString(tokenColIndex);
        c.close();
        db.close();
        String url = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS" +
                "&opt=user_chats" +
                "&act=send_mess" +
                "&chat_id=" + Common.chatId +
                "&user_from=" + Common.userId +
                "&user_to=" + Common.user_to +
                "&message=" + message +
                "&utoken=" + token;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Dialog dialog = new Dialog(ChatActivity.this, android.R.style.Theme_Material_Light_NoActionBar);
                dialog.setContentView(R.layout.dialog_no_internet);
                Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendMess(message);
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

    private void messageViewed(Object id) {
        String url = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS" +
                "&opt=user_chats" +
                "&act=change_saw" +
                "&chat_id=" + id;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Dialog dialog = new Dialog(ChatActivity.this, android.R.style.Theme_Material_Light_NoActionBar);
                dialog.setContentView(R.layout.dialog_no_internet);
                Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        messageViewed(id);
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

    private void getMessages() throws IOException {
        String url = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=user_chats&act=view_mess&chat_id=" + Common.chatId ;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(ChatActivity.this, android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    getMessages();
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
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                mMessage = Objects.requireNonNull(response.body()).string();

                try {

                    JSONArray jsonArray = new JSONArray(mMessage);
                    int lenght = jsonArray.length();
                    for (int i = 0; i < lenght; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String chat_id = jsonObject.getString("chat_id");
                        String user_to = jsonObject.getString("user_to");
                        String user_from = jsonObject.getString("user_from");
                        String message = jsonObject.getString("message");
                        String message_time = jsonObject.getString("message_time");


                        if(user_from.equals(Common.userId)){
                            messages.add(new my_message(message, message_time));
                            Common.user_to = user_to;
                        }else{
                            messages.add(new their_message(message, message_time));
                            Common.user_to = user_from;
                        }

                    }
                    Collections.sort(messages, new Comparator<message_interface>(){
                        public int compare(message_interface obj1, message_interface obj2) {
                            // ## Ascending order
                            return obj1.getDate().compareToIgnoreCase(obj2.getDate()); // To compare string values
                            //return Integer.valueOf(obj1.).compareTo(obj2.getId()); // To compare integer values

                            // ## Descending order
                            // return obj2.getCompanyName().compareToIgnoreCase(obj1.getCompanyName()); // To compare string values
                            // return Integer.valueOf(obj2.getId()).compareTo(obj1.getId()); // To compare integer values
                        }
                    });
                    adapter = new ChattingAdapter(ChatActivity.this, messages);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messages_view.setAdapter(adapter);
                            if(!messages.isEmpty()){
                                messages_view.smoothScrollToPosition(Objects.requireNonNull(messages_view.getAdapter()).getItemCount() - 1);
                            }

                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Common.chatId = "";
        Common.nameOfChat = "";
    }
}
