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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class CreateTaskAmout extends AppCompatActivity implements View.OnClickListener {

    TextView buttonCreate;
    TextView buttonCreateLeft;
    TextView buttonCreateRight;
    TextView texthisprice;
    TextInputLayout amoutEdit;
    TextInputEditText amoutEditPrice;
    int counter;
    int pricetype;
    @SuppressLint("StaticFieldLeak")
    public static Activity fa;
    ImageView tri_left_amount,tri_right_amount,create_amount_back;
    CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryPurpleTop));
        setContentView(R.layout.activity_create_task_amout);
        counter = 1;
        pricetype = 1;

        tri_right_amount = findViewById(R.id.tri_right_amount);
        tri_left_amount = findViewById(R.id.tri_left_amount);

        create_amount_back = findViewById(R.id.create_amount_back);
        create_amount_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        cardView = findViewById(R.id.card_of_create_amount);
        cardView.setBackgroundResource(R.drawable.shape_card_topcorners);
        amoutEdit = findViewById(R.id.amout_text_input);
        amoutEditPrice = findViewById(R.id.editCreateAmout);
        texthisprice = findViewById(R.id.texthisprice);
        texthisprice.setVisibility(View.INVISIBLE);
        buttonCreate = findViewById(R.id.createAmout);
        buttonCreateLeft = findViewById(R.id.createAmout_buttonleft);
        buttonCreateRight = findViewById(R.id.createAmout_buttonright);
        buttonCreate.setOnClickListener(this);
        buttonCreateLeft.setOnClickListener(this);
        buttonCreateRight.setOnClickListener(this);
        fa = this;

        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                1500.0f, 0.0f);
        animation.setDuration(500);
        //animation.setFillAfter(true);
        cardView.startAnimation(animation);
        tri_left_amount.startAnimation(animation);
        buttonCreateLeft.startAnimation(animation);
        buttonCreateRight.startAnimation(animation);
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
        final SharedPreferences prefs = getSharedPreferences(" ", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        switch (view.getId()) {
            case R.id.createAmout:
                if (amoutEdit.getVisibility() == View.VISIBLE) {
                    if (Objects.requireNonNull(amoutEditPrice.getText()).length() != 0) {
                        if (counter == 1) {
                            pricetype = 1;
                            editor.putString(Util.TASK_PPRICE, String.valueOf(amoutEditPrice.getText()));
                            editor.putString(Util.TASK_PPRICE_TYPE, String.valueOf(pricetype));
                            editor.apply();
                        } else if (counter == 2) {
                            pricetype = 2;
                            editor.putString(Util.TASK_PPRICE, "Предложите цену");
                            editor.putString(Util.TASK_PPRICE_TYPE, String.valueOf(pricetype));
                            editor.apply();
                        }
                        Intent intent = new Intent(this, CreateTaskDetail.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (counter == 1) {
                        pricetype = 1;
                        editor.putString(Util.TASK_PPRICE, String.valueOf(amoutEditPrice.getText()));
                        editor.putString(Util.TASK_PPRICE_TYPE, String.valueOf(pricetype));
                        editor.apply();
                    } else if (counter == 2) {
                        pricetype = 2;
                        editor.putString(Util.TASK_PPRICE, "Предложите цену");
                        editor.putString(Util.TASK_PPRICE_TYPE, String.valueOf(pricetype));
                        editor.apply();
                    }
                    Intent intent = new Intent(this, CreateTaskDetail.class);
                    startActivity(intent);
                }
                break;

            case R.id.createAmout_buttonleft:
                counter = 1;
                texthisprice.setVisibility(View.INVISIBLE);
                amoutEdit.setVisibility(View.VISIBLE);
                tri_right_amount.setVisibility(View.INVISIBLE);
                tri_left_amount.setVisibility(View.VISIBLE);
                break;

            case R.id.createAmout_buttonright:
                counter = 2;
                texthisprice.setVisibility(View.VISIBLE);
                amoutEdit.setVisibility(View.INVISIBLE);
                tri_right_amount.setVisibility(View.VISIBLE);
                tri_left_amount.setVisibility(View.INVISIBLE);
                break;
        }
    }
}
