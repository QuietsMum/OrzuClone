package orzu.org;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

public class NotificationSettings extends AppCompatActivity {
    Switch switch_notif,switch_time;
    ConstraintLayout disable_notif;
    ConstraintLayout time_enable;
    View divider1,divider2;
    LinearLayout from_time,to_time,time_of_notif;
    Dialog dialog;
    TimePicker time;
    TextView to_time_text,from_time_text;
    CardView cardView;
    ImageView imageView;
    TextView notif_set_save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences(" ", Context.MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryOrangeTop));
        setContentView(R.layout.activity_notification_settings);
        switch_notif = findViewById(R.id.switch_notif);
        switch_time = findViewById(R.id.switch_time);
        disable_notif = findViewById(R.id.disable_notif);
        time_enable = findViewById(R.id.time_enable);
        divider1 = findViewById(R.id.notif_divider1);
        divider2 = findViewById(R.id.notif_divider2);
        from_time = findViewById(R.id.from_time);
        to_time = findViewById(R.id.to_time);
        time_of_notif = findViewById(R.id.time_of_notif);
        to_time_text = findViewById(R.id.to_time_text);
        from_time_text = findViewById(R.id.from_time_text);
        cardView = findViewById(R.id.card_of_notif_settings);
        cardView.setBackgroundResource(R.drawable.shape_card_topcorners);
        imageView = findViewById(R.id.notif_setting_back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        dialog = new Dialog(this);
        switch_notif.setChecked(prefs.getBoolean("disableNotifiaction",false));
        if(switch_notif.isChecked()){
            time_enable.setVisibility(View.VISIBLE);
            divider1.setVisibility(View.VISIBLE);
        }else{
            time_enable.setVisibility(View.INVISIBLE);
            divider1.setVisibility(View.INVISIBLE);
            divider2.setVisibility(View.INVISIBLE);
            time_of_notif.setVisibility(View.INVISIBLE);
        }
        notif_set_save = findViewById(R.id.notif_set_save);
        notif_set_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if(prefs.getBoolean("enableTime",false)){
            switch_time.setChecked(true);
            switch_time.setChecked(true);
            divider2.setVisibility(View.VISIBLE);
            time_of_notif.setVisibility(View.VISIBLE);
        }else {
            switch_time.setChecked(false);
            divider2.setVisibility(View.INVISIBLE);
            time_of_notif.setVisibility(View.INVISIBLE);
        }
        SharedPreferences.Editor editor = prefs.edit();
        from_time_text.setText(prefs.getString("from_time","07:00"));
        to_time_text.setText(prefs.getString("to_time","00:00"));
        disable_notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switch_notif.isChecked()){
                    editor.putBoolean("disableNotifiaction",false);
                    editor.putBoolean("enableTime",false);
                    editor.apply();
                    switch_notif.setChecked(false);
                    time_enable.setVisibility(View.INVISIBLE);
                    divider1.setVisibility(View.INVISIBLE);
                    divider2.setVisibility(View.INVISIBLE);
                    time_of_notif.setVisibility(View.INVISIBLE);
                    switch_time.setChecked(false);
                }else {
                    editor.putBoolean("disableNotifiaction",true);
                    editor.apply();
                    switch_notif.setChecked(true);
                    time_enable.setVisibility(View.VISIBLE);
                    divider1.setVisibility(View.VISIBLE);
                }
            }
        });
        time_enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switch_time.isChecked()){
                    editor.putBoolean("enableTime",false);
                    editor.apply();
                    switch_time.setChecked(false);
                    divider2.setVisibility(View.INVISIBLE);
                    time_of_notif.setVisibility(View.INVISIBLE);
                }else {
                    editor.putBoolean("enableTime",true);
                    editor.apply();
                    switch_time.setChecked(true);
                    divider2.setVisibility(View.VISIBLE);
                    time_of_notif.setVisibility(View.VISIBLE);
                }
            }
        });
        from_time.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.timepicker_dialog);
                time = dialog.findViewById(R.id.date_picker);
                time.setIs24HourView(true);
                time.setHour(7);
                time.setMinute(0);
                TextView choose = dialog.findViewById(R.id.button_choose);
                choose.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint({"SetTextI18n", "DefaultLocale"})
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View view) {
                        from_time_text.setText(String.format("%02d:%02d", time.getHour(), time.getMinute()));
                        editor.putString("from_time",from_time_text.getText().toString());
                        editor.putString("to_time",to_time_text.getText().toString());
                        editor.apply();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        to_time.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.timepicker_dialog);
                time = dialog.findViewById(R.id.date_picker);
                time.setIs24HourView(true);
                time.setHour(0);
                time.setMinute(0);
                TextView choose = dialog.findViewById(R.id.button_choose);
                choose.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint({"SetTextI18n", "DefaultLocale"})
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View view) {
                        to_time_text.setText(String.format("%02d:%02d", time.getHour(), time.getMinute()));
                        editor.putString("to_time",to_time_text.getText().toString());
                        editor.putString("from_time",from_time_text.getText().toString());
                        editor.apply();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
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
}
