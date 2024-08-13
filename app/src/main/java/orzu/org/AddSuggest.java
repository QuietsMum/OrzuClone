package orzu.org;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddSuggest extends AppCompatActivity implements View.OnClickListener {
    ConstraintLayout backButton;
    ConstraintLayout addButton;
    EditText editNarr;
    EditText editAmout;
    String idUser;
    String tokenUser;
    String nameUser;
    String idTask;
    TextView username;
    ImageView person_logo_sugg2,suggest_back;
    ProgressBar progress_suggest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.back_for_feed));
        setContentView(R.layout.activity_add_suggest);

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("orzutable", null, null, null, null, null, null);
        c.moveToFirst();
        int idColIndex = c.getColumnIndex("id");
        int tokenColIndex = c.getColumnIndex("token");
        idUser = c.getString(idColIndex);
        tokenUser = c.getString(tokenColIndex);
        c.close();
        db.close();
        final SharedPreferences prefs = getSharedPreferences(" ", Context.MODE_PRIVATE);
        nameUser = prefs.getString(Util.TASK_USERNAME, "");
        progress_suggest = findViewById(R.id.progress_suggest);

        suggest_back = findViewById(R.id.suggest_back);
        suggest_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        idTask = Objects.requireNonNull(getIntent().getExtras()).getString("idTaskSuggest");


        backButton = findViewById(R.id.button_back_sugg);
        backButton.setOnClickListener(this);
        addButton = findViewById(R.id.button_add_sugg);
        addButton.setOnClickListener(this);


        editNarr = findViewById(R.id.edit_task_sugg);
        editAmout = findViewById(R.id.edit_task_sugg_amout);

        username = findViewById(R.id.name_sugg);
        username.setText(nameUser);

        person_logo_sugg2 = findViewById(R.id.person_logo_sugg2);
        person_logo_sugg2.setImageBitmap(Common.bitmap);

    }

    public void sendSuggest() throws IOException {
        progress_suggest.setVisibility(View.VISIBLE);
        String url = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=task_requests" +
                "&act=input" +
                "&task_id=" + idTask +
                "&narrative=" + editNarr.getText() +
                "&userid=" + idUser +
                "&amount=" + editAmout.getText() +
                "&utoken=" + tokenUser;
        OkHttpClient client = new OkHttpClient();
        Log.wtf("asdas",url);
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, IOException e) {
                AddSuggest.this.runOnUiThread(new Runnable() {
                    public void run() {
                        progress_suggest.setVisibility(View.INVISIBLE);
                        Dialog dialog = new Dialog(AddSuggest.this, android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    sendSuggest();
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

                String mMessage = Objects.requireNonNull(response.body()).string();
                Log.wtf("asdasd",mMessage);
                AddSuggest.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(AddSuggest.this, "Отклик добавлен!", Toast.LENGTH_SHORT).show();
                    }
                });

                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_back_sugg:
                finish();
                break;

            case R.id.button_add_sugg:
                try {
                    sendSuggest();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

        }
    }
}
