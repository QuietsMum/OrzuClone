package orzu.org;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {
    ProgressBar bar;
    EditText oldPwd, newPwd, newPwdRe;
    FloatingActionButton ok;
    ImageView back;
    CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryOrangeTop));
        setContentView(R.layout.activity_change_password);

        bar = findViewById(R.id.progressBarLogin_reg);
        back = findViewById(R.id.change_back);
        cardView = findViewById(R.id.constrlog2);
        cardView.setBackgroundResource(R.drawable.shape_card_topcorners);
        oldPwd = findViewById(R.id.old_pwd_edit);
        newPwd = findViewById(R.id.new_pwd_edit);
        newPwdRe = findViewById(R.id.new_pwd_edit_rewrite);
        ok = findViewById(R.id.button_change_pwd);
        ok.setOnClickListener(this);
        back.setOnClickListener(this);

        bar.setVisibility(View.GONE);

        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                1000.0f, 0.0f);
        animation.setDuration(500);
        cardView.startAnimation(animation);
        ok.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                ok.setVisibility(View.VISIBLE);
                Animation animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.zoom_in);
                ok.startAnimation(animZoomIn);
            }
        }, animation.getDuration());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.change_back:
                finish();
                break;
            case R.id.button_change_pwd:
                bar.setVisibility(View.VISIBLE);
                if (!newPwd.getText().toString().equals(newPwdRe.getText().toString())) {
                    Toast.makeText(this, "Пароли не совпадают " + newPwd.getText() + " " + newPwdRe.getText(), Toast.LENGTH_SHORT).show();
                    bar.setVisibility(View.GONE);
                } else {
                    try {
                        getEditPassword();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public void getEditPassword() throws IOException {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("orzutable", null, null, null, null, null, null);
        c.moveToFirst();
        int idColIndex = c.getColumnIndex("id");
        int tokenColIndex = c.getColumnIndex("token");
        String idUser = c.getString(idColIndex);
        String tokenUser = c.getString(tokenColIndex);
        c.moveToFirst();
        c.close();
        db.close();



        String url = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS" +
                "&opt=user_param" +
                "&act=edit_password" +
                "&userid=" + idUser +
                "&utoken=" + tokenUser +
                "&name=" + Common.name_only +
                "&password=" + newPwd.getText().toString() +
                "&old_password=" + oldPwd.getText().toString();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                ChangePasswordActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(ChangePasswordActivity.this, android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    getEditPassword();
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
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String mMessage = Objects.requireNonNull(response.body()).string();
                final char dm = (char) 34;

                if (mMessage.equals(dm+"Old password Error"+dm)) {
                    ChangePasswordActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(ChangePasswordActivity.this, "Не правильный старый пароль", Toast.LENGTH_SHORT).show();
                            bar.setVisibility(View.INVISIBLE);
                        }
                    });
                } else if(mMessage.equals(dm+"Profile password edited"+dm)){
                    ChangePasswordActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(ChangePasswordActivity.this, "Пароль изменён", Toast.LENGTH_SHORT).show();
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    finish();
                                }
                            }, 400);
                        }
                    });
                }
            }
        });
    }
}
