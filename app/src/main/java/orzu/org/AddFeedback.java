package orzu.org;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class AddFeedback extends AppCompatActivity implements View.OnClickListener {
    ConstraintLayout backButton;
    ConstraintLayout addButton;
    ImageView sadImage;
    ImageView natImage;
    ImageView hapImage;
    ImageView person_logo_sugg2;
    EditText editNarr;
    String idUser;
    String tokenUser;
    String idUserTo;
    int like;
    TextView username;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.back_for_feed));
        setContentView(R.layout.activity_add_feedback);

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
        like = 1;
        idUserTo = Objects.requireNonNull(getIntent().getExtras()).getString("idUserFeedbackto");

        back = findViewById(R.id.feedback_back_in_feed);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        backButton = findViewById(R.id.button_back_feed);
        backButton.setOnClickListener(this);
        addButton = findViewById(R.id.button_add_feed);
        addButton.setOnClickListener(this);

        sadImage = findViewById(R.id.id_sad);
        sadImage.setAlpha(0.5f);
        sadImage.setOnClickListener(this);
        natImage = findViewById(R.id.id_neutral);
        natImage.setAlpha(1f);
        natImage.setOnClickListener(this);
        hapImage = findViewById(R.id.id_happy);
        hapImage.setOnClickListener(this);
        hapImage.setAlpha(0.5f);

        editNarr = findViewById(R.id.edit_task_feedback);
        username = findViewById(R.id.name_surname);
        username.setText(getIntent().getExtras().getString("nameUserFeedbackto"));

        person_logo_sugg2 = findViewById(R.id.person_logo2);
        person_logo_sugg2.setImageBitmap(Common.bitmap);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void sendFeedback() throws IOException {

        String url = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=reviews" +
                "&act=input" +
                "&userid=" + idUser +
                "&narrative=" + editNarr.getText() +
                "&like_user_id=" + idUserTo +
                "&like=" + like +
                "&utoken=" + tokenUser;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                AddFeedback.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(AddFeedback.this, android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    sendFeedback();
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
                if (like == 0) {
                   // sendMinusFeedback();
                } else {
                    sendPlusFeedback();
                }
            }
        });
    }

    public void sendMinusFeedback() throws IOException {

        String url = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=user_param" +
                "&act=edit_bonus_feedback_minus" +
                "&userid=" + idUser +
                "&useridTo=" + idUserTo +
                "&utoken=" + tokenUser;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                AddFeedback.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(AddFeedback.this, android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    sendMinusFeedback();
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
                finish();
            }
        });
    }

    public void sendPlusFeedback() throws IOException {

        String url = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=user_param" +
                "&act=edit_bonus_feedback_plus" +
                "&userid=" + idUser +
                "&useridTo=" + idUserTo +
                "&utoken=" + tokenUser;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                AddFeedback.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(AddFeedback.this, android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    sendPlusFeedback();
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
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_back_feed:
                finish();
                break;
            case R.id.id_sad:
                sadImage.setAlpha(1f);
                natImage.setAlpha(0.5f);
                hapImage.setAlpha(0.5f);
                like = 0;
                break;
            case R.id.id_neutral:
                sadImage.setAlpha(0.5f);
                natImage.setAlpha(1f);
                hapImage.setAlpha(0.5f);
                like = 1;
                break;
            case R.id.id_happy:
                sadImage.setAlpha(0.5f);
                natImage.setAlpha(0.5f);
                hapImage.setAlpha(1f);
                like = 2;
                break;
            case R.id.button_add_feed:
                if (idUser.equals(idUserTo)) {
                    Toast.makeText(this, "Вы не можете оставлять отзывы себе", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        sendFeedback();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
        }
    }
}
