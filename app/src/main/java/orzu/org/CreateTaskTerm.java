package orzu.org;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class CreateTaskTerm extends AppCompatActivity implements View.OnClickListener {

    TextView buttonCreate;
    @SuppressLint("StaticFieldLeak")
    public static Activity fa;
    TextView switch1;
    TextView switch2;
    TextView switch3;
    TextView text_examle1;
    TextView text_date1;
    TextView text_date1_2;
    TextView text_date2;
    LinearLayout lin_examlpe1_1;
    LinearLayout lin_examlpe1_2;
    LinearLayout lin_examlpe2;
    Spinner spiner;
    int count;
    int counterDef;
    SimpleDateFormat sdf;
    String selectedDate;
    CalendarView calendar;
    WindowManager.LayoutParams lp;
    Locale myLocale = new Locale("ru", "RU");
    int dateType;
    ImageView tri_left, tri_center, tri_right, create_term_back;
    CardView cardView;
    View view_layout_term_text_from1, view_layout_term_text_from2, view_layout_term_onedate1, view_layout_term_onedate2;
    BottomSheetDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryPurpleTop));
        setContentView(R.layout.activity_create_task_term);
        counterDef = 2;
        dateType = 2;
        switch1 = findViewById(R.id.createTerm_buttonleft);
        switch1.setOnClickListener(this);
        switch2 = findViewById(R.id.createTerm_buttoncenter);
        switch2.setOnClickListener(this);
        switch3 = findViewById(R.id.createTerm_buttonright);
        switch3.setOnClickListener(this);
        spiner = findViewById(R.id.spinner_term);
        spiner.setVisibility(View.INVISIBLE);
        text_examle1 = findViewById(R.id.createTerm_textisp);
        text_date1 = findViewById(R.id.term_text_from_date);
        text_date1_2 = findViewById(R.id.term_text_to_date);
        text_date2 = findViewById(R.id.term_text_one_date);
        tri_center = findViewById(R.id.tri_center);
        tri_left = findViewById(R.id.tri_left);
        tri_right = findViewById(R.id.tri_right);
        create_term_back = findViewById(R.id.create_term_back);
        create_term_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        cardView = findViewById(R.id.card_of_create_term);
        cardView.setBackgroundResource(R.drawable.shape_card_topcorners);
        lin_examlpe1_1 = findViewById(R.id.layout_term_text_from);
        lin_examlpe1_1.setOnClickListener(this);
        lin_examlpe1_1.setVisibility(View.VISIBLE);
        lin_examlpe1_2 = findViewById(R.id.layout_term_text_to);
        lin_examlpe1_2.setOnClickListener(this);
        lin_examlpe1_2.setVisibility(View.VISIBLE);
        lin_examlpe2 = findViewById(R.id.layout_term_onedate);
        lin_examlpe2.setOnClickListener(this);
        lin_examlpe2.setVisibility(View.INVISIBLE);
        text_examle1.setVisibility(View.INVISIBLE);
        lp = new WindowManager.LayoutParams();
        view_layout_term_onedate1 = findViewById(R.id.view_layout_term_onedate1);
        view_layout_term_onedate2 = findViewById(R.id.view_layout_term_onedate2);
        view_layout_term_text_from1 = findViewById(R.id.view_layout_term_text_from1);
        view_layout_term_text_from2 = findViewById(R.id.view_layout_term_text_from2);
        buttonCreate = findViewById(R.id.createTerm);
        buttonCreate.setOnClickListener(this);
        fa = this;
        dialog = new BottomSheetDialog(this);
        View sheetView = this.getLayoutInflater().inflate(R.layout.calendar_dialog, null);
        dialog.setContentView(sheetView);
        CardView cardView = dialog.findViewById(R.id.card_of_calendar);
        Objects.requireNonNull(cardView).setBackgroundResource(R.drawable.shape_card_topcorners);
        calendar = dialog.findViewById(R.id.calendarView);
        Calendar today = Calendar.getInstance();
        sdf = new SimpleDateFormat("dd.MM.yyyy", myLocale);
        selectedDate = sdf.format(today.getTime());
        calendar.setDate(System.currentTimeMillis(),false,true);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                selectedDate = sdf.format(new Date(i - 1900, i1, i2));
            }
        });
        TextView dialogButton = dialog.findViewById(R.id.ok_calendar);
        // if button is clicked, close the custom dialog
        Objects.requireNonNull(dialogButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count == 1) {
                    text_date1.setText(selectedDate);
                } else if (count == 2) {
                    text_date1_2.setText(selectedDate);
                } else if (count == 3) {
                    text_date2.setText(selectedDate);
                }
                dialog.dismiss();
            }
        });
        TextView close = dialog.findViewById(R.id.cancel_calendar);
        // if button is clicked, close the custom dialog
        Objects.requireNonNull(close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                1500.0f, 0.0f);
        animation.setDuration(500);
        cardView.startAnimation(animation);
        tri_center.startAnimation(animation);
        switch1.startAnimation(animation);
        switch2.startAnimation(animation);
        switch3.startAnimation(animation);
        buttonCreate.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                buttonCreate.setVisibility(View.VISIBLE);
                Animation animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.zoom_in);
                buttonCreate.startAnimation(animZoomIn);
            }
        }, animation.getDuration());
    }
    @Override
    public void onClick(View view) {

        // 1. Точная дата 
        // 2. Указать период 
        // 3. Договорюсь с исполнителем

        switch (view.getId()) {
            case R.id.createTerm:
                if (lin_examlpe1_1.getVisibility() == View.VISIBLE) {
                    if (text_date1.getText().length() > 8 && text_date1_2.getText().length() > 8) {
                        final SharedPreferences prefs = getSharedPreferences(" ", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();

                        if (counterDef == 1) {
                            dateType = 3;
                            editor.putString(Util.TASK_WORKWITH, "Дата по договоренности");
                            editor.putString(Util.TASK_DATETYPE, String.valueOf(dateType));
                            editor.apply();
                        } else if (counterDef == 2) {
                            dateType = 2;
                            editor.putString(Util.TASK_CDATE, String.valueOf(text_date1.getText()));
                            editor.putString(Util.TASK_EDATE, String.valueOf(text_date1_2.getText()));
                            editor.putString(Util.TASK_DATETYPE, String.valueOf(dateType));
                            editor.apply();
                        } else if (counterDef == 3) {
                            dateType = 1;
                            editor.putString(Util.TASK_DATETYPE, String.valueOf(dateType));
                            editor.putString(Util.TASK_CDATEL, String.valueOf(text_date2.getText()));
                            editor.putString(Util.TASK_LEVEL_L, String.valueOf(spiner.getSelectedItemPosition()));
                            editor.apply();
                        }
                        Intent intent = new Intent(this, CreateTaskAmout.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                    }
                } else if (spiner.getVisibility() == View.VISIBLE) {
                    if (spiner.getSelectedItem().toString().length() != 0 && text_date2.getText().length() > 8) {
                        final SharedPreferences prefs = getSharedPreferences(" ", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        if (counterDef == 1) {
                            dateType = 3;
                            editor.putString(Util.TASK_WORKWITH, "Дата по договоренности");
                            editor.putString(Util.TASK_DATETYPE, String.valueOf(dateType));
                            editor.apply();
                        } else if (counterDef == 2) {
                            dateType = 2;
                            editor.putString(Util.TASK_CDATE, String.valueOf(text_date1.getText()));
                            editor.putString(Util.TASK_EDATE, String.valueOf(text_date1_2.getText()));
                            editor.putString(Util.TASK_DATETYPE, String.valueOf(dateType));
                            editor.apply();
                        } else if (counterDef == 3) {
                            dateType = 1;
                            editor.putString(Util.TASK_DATETYPE, String.valueOf(dateType));
                            editor.putString(Util.TASK_CDATEL, String.valueOf(text_date2.getText()));
                            editor.putString(Util.TASK_LEVEL_L, String.valueOf(spiner.getSelectedItemPosition()));
                            editor.apply();
                        }
                        Intent intent = new Intent(this, CreateTaskAmout.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    final SharedPreferences prefs = getSharedPreferences(" ", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    if (counterDef == 1) {
                        dateType = 3;
                        editor.putString(Util.TASK_WORKWITH, "Дата по договоренности");
                        editor.putString(Util.TASK_DATETYPE, String.valueOf(dateType));
                        editor.apply();
                    } else if (counterDef == 2) {
                        dateType = 2;
                        editor.putString(Util.TASK_CDATE, String.valueOf(text_date1.getText()));
                        editor.putString(Util.TASK_EDATE, String.valueOf(text_date1_2.getText()));
                        editor.putString(Util.TASK_DATETYPE, String.valueOf(dateType));
                        editor.apply();
                    } else if (counterDef == 3) {
                        dateType = 1;
                        editor.putString(Util.TASK_DATETYPE, String.valueOf(dateType));
                        editor.putString(Util.TASK_CDATEL, String.valueOf(text_date2.getText()));
                        editor.putString(Util.TASK_LEVEL_L, String.valueOf(spiner.getSelectedItemPosition()));
                        editor.apply();
                    }
                    Intent intent = new Intent(this, CreateTaskAmout.class);
                    startActivity(intent);
                }
                break;
            case R.id.createTerm_buttonleft:
                counterDef = 1;
                spiner.setVisibility(View.INVISIBLE);
                text_examle1.setVisibility(View.VISIBLE);
                tri_left.setVisibility(View.VISIBLE);
                tri_center.setVisibility(View.INVISIBLE);
                tri_right.setVisibility(View.INVISIBLE);
                lin_examlpe1_1.setVisibility(View.INVISIBLE);
                lin_examlpe1_2.setVisibility(View.INVISIBLE);
                lin_examlpe2.setVisibility(View.INVISIBLE);
                view_layout_term_onedate1.setVisibility(View.INVISIBLE);
                view_layout_term_onedate2.setVisibility(View.INVISIBLE);
                view_layout_term_text_from1.setVisibility(View.INVISIBLE);
                view_layout_term_text_from2.setVisibility(View.INVISIBLE);
                break;
            case R.id.createTerm_buttoncenter:
                counterDef = 2;
                tri_left.setVisibility(View.INVISIBLE);
                tri_center.setVisibility(View.VISIBLE);
                tri_right.setVisibility(View.INVISIBLE);
                spiner.setVisibility(View.INVISIBLE);
                text_examle1.setVisibility(View.INVISIBLE);
                lin_examlpe1_1.setVisibility(View.VISIBLE);
                lin_examlpe1_2.setVisibility(View.VISIBLE);
                lin_examlpe2.setVisibility(View.INVISIBLE);
                view_layout_term_onedate1.setVisibility(View.VISIBLE);
                view_layout_term_onedate2.setVisibility(View.VISIBLE);
                view_layout_term_text_from1.setVisibility(View.INVISIBLE);
                view_layout_term_text_from2.setVisibility(View.INVISIBLE);
                break;
            case R.id.createTerm_buttonright:
                counterDef = 3;
                tri_left.setVisibility(View.INVISIBLE);
                tri_center.setVisibility(View.INVISIBLE);
                tri_right.setVisibility(View.VISIBLE);
                spiner.setVisibility(View.VISIBLE);
                text_examle1.setVisibility(View.INVISIBLE);
                lin_examlpe1_1.setVisibility(View.INVISIBLE);
                lin_examlpe1_2.setVisibility(View.INVISIBLE);
                lin_examlpe2.setVisibility(View.VISIBLE);
                view_layout_term_onedate1.setVisibility(View.INVISIBLE);
                view_layout_term_onedate2.setVisibility(View.INVISIBLE);
                view_layout_term_text_from1.setVisibility(View.VISIBLE);
                view_layout_term_text_from2.setVisibility(View.VISIBLE);
                break;
            case R.id.layout_term_text_from:
                dialog.show();
                count = 1;
                break;
            case R.id.layout_term_text_to:
                dialog.show();
                count = 2;
                break;
            case R.id.layout_term_onedate:
                dialog.show();
                count = 3;
                break;
        }
    }
}
