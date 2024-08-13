package orzu.org;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.ImageQuality;
import com.fxn.utility.PermUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class CreateTaskDetail extends AppCompatActivity implements View.OnClickListener {

    TextView buttonCreate;
    @SuppressLint("StaticFieldLeak")
    public static Activity fa;
    LinearLayout linearImg;
    EditText annot;
    ImageView image1;
    ImageView image2;
    ImageView image3;
    ImageView image4;
    ImageView image5;
    ImageView image6;
    ImageView delete1;
    ImageView delete2;
    ImageView delete3;
    ImageView delete4;
    ImageView delete5;
    ImageView delete6;
    ImageView back;
    LinearLayout linearImages;
    LinearLayout pluslinearyImages;
    int counter;
    ArrayList<String> returnValue = new ArrayList<>();
    Boolean multy = true;

    CardView cardView;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String permissions[], @NotNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (requestCode == PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Pix.start(this, Options.init().setRequestCode(100));
            } else {
                Toast.makeText(this, "Approve permissions to open Pix ImagePicker", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryPurpleTop));
        setContentView(R.layout.activity_create_task_detail);

        counter = 0;
        linearImages = findViewById(R.id.linearyimages);
        linearImages.setVisibility(View.INVISIBLE);
        pluslinearyImages = findViewById(R.id.pluslinearyimages);
        pluslinearyImages.setVisibility(View.VISIBLE);
        pluslinearyImages.setOnClickListener(this);
        image1 = findViewById(R.id.imageTaskCreate1);
        image2 = findViewById(R.id.imageTaskCreate2);
        image3 = findViewById(R.id.imageTaskCreate3);
        image4 = findViewById(R.id.imageTaskCreate4);
        image5 = findViewById(R.id.imageTaskCreate5);
        image6 = findViewById(R.id.imageTaskCreate6);
        annot = findViewById(R.id.detail_textinput);
        delete1 = findViewById(R.id.imagedelete1);
        delete2 = findViewById(R.id.imagedelete2);
        delete3 = findViewById(R.id.imagedelete3);
        delete4 = findViewById(R.id.imagedelete4);
        delete5 = findViewById(R.id.imagedelete5);
        delete6 = findViewById(R.id.imagedelete6);
        delete1.setOnClickListener(this);
        delete2.setOnClickListener(this);
        delete3.setOnClickListener(this);
        delete4.setOnClickListener(this);
        delete5.setOnClickListener(this);
        delete6.setOnClickListener(this);
        delete1.setVisibility(View.INVISIBLE);
        delete2.setVisibility(View.INVISIBLE);
        delete3.setVisibility(View.INVISIBLE);
        delete4.setVisibility(View.INVISIBLE);
        delete5.setVisibility(View.INVISIBLE);
        delete6.setVisibility(View.INVISIBLE);
        linearImg = findViewById(R.id.linear_image_click);
        buttonCreate = findViewById(R.id.createDetails);
        buttonCreate.setOnClickListener(this);
        image1.setOnClickListener(this);
        image2.setOnClickListener(this);
        image3.setOnClickListener(this);
        image4.setOnClickListener(this);
        image5.setOnClickListener(this);
        image6.setOnClickListener(this);
        linearImg.setOnClickListener(this);
        cardView = findViewById(R.id.card_of_create_details);
        cardView.setBackgroundResource(R.drawable.shape_card_topcorners);
        back = findViewById(R.id.create_details_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        fa = this;
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                1500.0f, 0.0f);
        animation.setDuration(500);
        cardView.startAnimation(animation);
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

        switch (view.getId()) {
            case R.id.createDetails:
                if (annot.getText().length() != 0) {
                    final SharedPreferences prefs = getSharedPreferences(" ", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(Util.TASK_ANNAT, String.valueOf(annot.getText()));
                    editor.apply();
                    Intent intent = new Intent(this, TaskViewMain.class);
                    intent.putExtra("opt", "add");
                    intent.putExtra("mytask", "not");
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.linear_image_click:
                multy = true;
                counter = 1;
                Options options = Options.init()
                        .setRequestCode(100)                                                 //Request code for activity results
                        .setCount(6)                                                         //Number of images to restict selection count
                        .setFrontfacing(false)                                                //Front Facing camera on start
                        .setImageQuality(ImageQuality.HIGH)                                  //Image Quality
                        .setPreSelectedUrls(returnValue)                                     //Pre selected Image Urls
                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)           //Orientaion
                        .setPath("/orzu/images");                                             //Custom Path For Image Storage

                Pix.start(CreateTaskDetail.this, options);

                break;
            case R.id.imageTaskCreate1:
                multy = false;
                Pix.start(this, Options.init().setRequestCode(100));

                counter = 1;
                break;
            case R.id.imageTaskCreate2:
                multy = false;
                Pix.start(this, Options.init().setRequestCode(100));

                counter = 2;
                break;
            case R.id.imageTaskCreate3:
                multy = false;
                Pix.start(this, Options.init().setRequestCode(100));

                counter = 3;
                break;
            case R.id.imageTaskCreate4:
                multy = false;
                Pix.start(this, Options.init().setRequestCode(100));

                counter = 4;
                break;
            case R.id.imageTaskCreate5:
                multy = false;
                Pix.start(this, Options.init().setRequestCode(100));

                counter = 5;
                break;
            case R.id.imageTaskCreate6:
                multy = false;
                Pix.start(this, Options.init().setRequestCode(100));

                counter = 6;
                break;
            case R.id.imagedelete1:
                image1.setImageResource(R.drawable.gal_image_create);
                delete1.setVisibility(View.INVISIBLE);
                break;
            case R.id.imagedelete2:
                image2.setImageResource(R.drawable.gal_image_create);
                delete2.setVisibility(View.INVISIBLE);
                break;
            case R.id.imagedelete3:
                image3.setImageResource(R.drawable.gal_image_create);
                delete3.setVisibility(View.INVISIBLE);
                break;
            case R.id.imagedelete4:
                image4.setImageResource(R.drawable.gal_image_create);
                delete4.setVisibility(View.INVISIBLE);
                break;
            case R.id.imagedelete5:
                image5.setImageResource(R.drawable.gal_image_create);
                delete5.setVisibility(View.INVISIBLE);
                break;
            case R.id.imagedelete6:
                image6.setImageResource(R.drawable.gal_image_create);
                delete6.setVisibility(View.INVISIBLE);
                break;
            case R.id.pluslinearyimages:
                pluslinearyImages.setVisibility(View.INVISIBLE);
                linearImages.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);

        }
        assert returnValue != null;
        if (returnValue.size() != 0) {
            if (!multy) {
                if (counter == 1) {
                    image1.setImageURI(Uri.parse(returnValue.get(0)));
                    Common.values.put(0, returnValue.get(0));
                    delete1.setVisibility(View.VISIBLE);
                } else if (counter == 2) {
                    image2.setImageURI(Uri.parse(returnValue.get(0)));
                    Common.values.put(1, returnValue.get(0));
                    delete2.setVisibility(View.VISIBLE);
                } else if (counter == 3) {
                    image3.setImageURI(Uri.parse(returnValue.get(0)));
                    Common.values.put(2, returnValue.get(0));
                    delete3.setVisibility(View.VISIBLE);
                } else if (counter == 4) {
                    image4.setImageURI(Uri.parse(returnValue.get(0)));
                    Common.values.put(3, returnValue.get(0));
                    delete4.setVisibility(View.VISIBLE);
                } else if (counter == 5) {
                    image5.setImageURI(Uri.parse(returnValue.get(0)));
                    Common.values.put(4, returnValue.get(0));
                    delete5.setVisibility(View.VISIBLE);
                } else if (counter == 6) {
                    image6.setImageURI(Uri.parse(returnValue.get(0)));
                    Common.values.put(5, returnValue.get(0));
                    delete6.setVisibility(View.VISIBLE);
                }

            } else {

                int size = returnValue.size();
                if (size == 1) {
                    image1.setImageURI(Uri.parse(returnValue.get(0)));
                    Common.values.put(0, returnValue.get(0));
                    delete1.setVisibility(View.VISIBLE);
                } else if (size == 2) {
                    image1.setImageURI(Uri.parse(returnValue.get(0)));
                    Common.values.put(0, returnValue.get(0));
                    delete1.setVisibility(View.VISIBLE);
                    image2.setImageURI(Uri.parse(returnValue.get(1)));
                    Common.values.put(1, returnValue.get(1));
                    delete2.setVisibility(View.VISIBLE);
                } else if (size == 3) {
                    image1.setImageURI(Uri.parse(returnValue.get(0)));
                    Common.values.put(0, returnValue.get(0));
                    delete1.setVisibility(View.VISIBLE);
                    image2.setImageURI(Uri.parse(returnValue.get(1)));
                    Common.values.put(1, returnValue.get(1));
                    delete2.setVisibility(View.VISIBLE);
                    image3.setImageURI(Uri.parse(returnValue.get(2)));
                    Common.values.put(2, returnValue.get(2));
                    delete3.setVisibility(View.VISIBLE);
                } else if (size == 4) {
                    image1.setImageURI(Uri.parse(returnValue.get(0)));
                    Common.values.put(0, returnValue.get(0));
                    delete1.setVisibility(View.VISIBLE);
                    image2.setImageURI(Uri.parse(returnValue.get(1)));
                    Common.values.put(1, returnValue.get(1));
                    delete2.setVisibility(View.VISIBLE);
                    image3.setImageURI(Uri.parse(returnValue.get(2)));
                    Common.values.put(2, returnValue.get(2));
                    delete3.setVisibility(View.VISIBLE);
                    image4.setImageURI(Uri.parse(returnValue.get(3)));
                    Common.values.put(3, returnValue.get(3));
                    delete4.setVisibility(View.VISIBLE);
                } else if (size == 5) {
                    image1.setImageURI(Uri.parse(returnValue.get(0)));
                    Common.values.put(0, returnValue.get(0));
                    delete1.setVisibility(View.VISIBLE);
                    image2.setImageURI(Uri.parse(returnValue.get(1)));
                    Common.values.put(1, returnValue.get(1));
                    delete2.setVisibility(View.VISIBLE);
                    image3.setImageURI(Uri.parse(returnValue.get(2)));
                    Common.values.put(2, returnValue.get(2));
                    delete3.setVisibility(View.VISIBLE);
                    image4.setImageURI(Uri.parse(returnValue.get(3)));
                    Common.values.put(3, returnValue.get(3));
                    delete4.setVisibility(View.VISIBLE);
                    image5.setImageURI(Uri.parse(returnValue.get(4)));
                    Common.values.put(4, returnValue.get(4));
                    delete5.setVisibility(View.VISIBLE);
                } else if (size == 6) {
                    image1.setImageURI(Uri.parse(returnValue.get(0)));
                    Common.values.put(0, returnValue.get(0));
                    delete1.setVisibility(View.VISIBLE);
                    image2.setImageURI(Uri.parse(returnValue.get(1)));
                    Common.values.put(1, returnValue.get(1));
                    delete2.setVisibility(View.VISIBLE);
                    image3.setImageURI(Uri.parse(returnValue.get(2)));
                    Common.values.put(2, returnValue.get(2));
                    delete3.setVisibility(View.VISIBLE);
                    image4.setImageURI(Uri.parse(returnValue.get(3)));
                    Common.values.put(3, returnValue.get(3));
                    delete4.setVisibility(View.VISIBLE);
                    image5.setImageURI(Uri.parse(returnValue.get(4)));
                    Common.values.put(4, returnValue.get(4));
                    delete5.setVisibility(View.VISIBLE);
                    image6.setImageURI(Uri.parse(returnValue.get(5)));
                    Common.values.put(5, returnValue.get(5));
                    delete6.setVisibility(View.VISIBLE);
                }
                counter = 1;
            }
        }
    }


}
