package orzu.org;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class UserSettings extends AppCompatActivity implements View.OnClickListener {

    LinearLayout buttonEdit;
    LinearLayout buttonLogout;
    LinearLayout button_change_password;
    LinearLayout btn_notif_settings;
    ImageView user_settings_back;
    CardView card_of_user_settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryPurpleTop));
        setContentView(R.layout.activity_user_settings);
        user_settings_back = findViewById(R.id.user_settings_back);
        user_settings_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        card_of_user_settings = findViewById(R.id.card_of_user_settings);
        card_of_user_settings.setBackgroundResource(R.drawable.shape_card_topcorners);
        buttonEdit = findViewById(R.id.button_edit_profile);
        buttonLogout = findViewById(R.id.button_logout);
        button_change_password = findViewById(R.id.button_change_password);
        btn_notif_settings = findViewById(R.id.btn_notif_settings);
        buttonEdit.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);
        button_change_password.setOnClickListener(this);
        btn_notif_settings.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_edit_profile:
                Intent intent = new Intent(this, UserEditProfile.class);
                startActivity(intent);
                break;
            case R.id.button_change_password:
                Intent intent2 = new Intent(this, ChangePasswordActivity.class);
                startActivity(intent2);
                break;
            case R.id.btn_notif_settings:
                Intent intent3 = new Intent(this, NotificationSettings.class);
                startActivity(intent3);
                break;
            case R.id.button_logout:
                AlertDialog alertDialog = new AlertDialog.Builder(UserSettings.this).create();
                alertDialog.setTitle("Выход");
                alertDialog.setMessage("Вы уверенны что хотите выйти из профиля?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ДА",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DBHelper dbHelper = new DBHelper(UserSettings.this);
                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                db.execSQL("delete from " + "orzutable");
                                db.execSQL("delete from " + "orzunotif");
                                db.close();
                                final SharedPreferences prefs = getSharedPreferences(" ", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.clear();
                                Intent mStartActivity = new Intent(UserSettings.this, CoreActivity.class);
                                startActivity(mStartActivity);
                                finishAffinity();
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

//                int mPendingIntentId = 123456;
//                PendingIntent mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
//                AlarmManager mgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
//                System.exit(0);
                break;
        }
    }
}
