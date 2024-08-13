package orzu.org;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserEditProfile extends AppCompatActivity implements View.OnClickListener {
    EditText userName;
    EditText userFname;
    EditText userNarr;
    TextView userDate;
    Spinner userCity;
    ConstraintLayout datePicker;
    TextView buttonEdit;
    RadioButton radioMale;
    RadioButton radioFemale;
    RadioButton radioButton;
    RadioGroup radioGroup;
    String gender;
    Dialog dialog;
    String selectedDate;
    DatePicker calendar;
    Locale myLocale = new Locale("ru", "RU");
    SimpleDateFormat sdf;
    DBHelper dbHelper;
    String encodedString;
    String idUser;
    String tokenUser;
    String text;
    String mMessage;
    String mName;
    String mFiName;
    String mCount;
    String mCountReq;
    String mCity;
    String mBday;
    String mSex;
    String mNarr;
    ImageView mAvatar, create_user_edit_back;
    String mAvatarstr;
    ArrayList<String> returnValue = new ArrayList<>();
    String[] cities;
    ArrayAdapter<String> adapter;
    ProgressBar bar;
    ShimmerFrameLayout shim;
    ImageView image_back_name_tast3;
    CardView cardView,card_of_shimmer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryOrangeTop));
        setContentView(R.layout.activity_user_edit_profile);
        create_user_edit_back = findViewById(R.id.create_user_edit_back);
        create_user_edit_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        cardView = findViewById(R.id.card_of_user_edit);
        cardView.setBackgroundResource(R.drawable.shape_card_topcorners);
        userName = findViewById(R.id.user_edit_name);
        mAvatar = findViewById(R.id.userAvatar);
        userFname = findViewById(R.id.user_edit_fname);
        userNarr = findViewById(R.id.useri_edit_about);
        userCity = findViewById(R.id.user_edit_place);
        userDate = findViewById(R.id.birth_textview);
        datePicker = findViewById(R.id.user_edit_birth);
        buttonEdit = findViewById(R.id.edit_user_btn);
        radioMale = findViewById(R.id.male);
        radioFemale = findViewById(R.id.female);
        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.check(R.id.male);
        radioMale.setActivated(true);
        buttonEdit.setOnClickListener(this);
        datePicker.setOnClickListener(this);
        mAvatar.setOnClickListener(this);
        shim = findViewById(R.id.userviewShimmerLayout);
        shim.startShimmer();
        image_back_name_tast3 = findViewById(R.id.image_back_name_tast3);
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.calendar_dialog_spinner);
        calendar = dialog.findViewById(R.id.calendarView);
        bar = findViewById(R.id.progres_edit_profile);
        bar.setVisibility(View.INVISIBLE);
        requestCity();
        Calendar today = Calendar.getInstance();
        calendar.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                        sdf = new SimpleDateFormat("dd.MM.yyyy", myLocale);
                        selectedDate = sdf.format(new Date(year - 1900, monthOfYear, dayOfMonth));
                    }
                });
        TextView dialogButton = dialog.findViewById(R.id.button_choose);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userDate.setText(selectedDate);
                dialog.dismiss();
            }
        });
        card_of_shimmer = findViewById(R.id.card_of_user_edit2);
        card_of_shimmer.setBackgroundResource(R.drawable.shape_card_topcorners);
        TranslateAnimation animation_of_shim = new TranslateAnimation(0.0f, 0.0f,
                1500.0f, 0.0f);
        animation_of_shim.setDuration(300);
        card_of_shimmer.startAnimation(animation_of_shim);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(this, Options.init().setRequestCode(100));
                } else {
                    Toast.makeText(this, "Approve permissions to open Pix ImagePicker", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_edit_birth:
                dialog.show();
                break;
            case R.id.edit_user_btn:
                bar.setVisibility(View.VISIBLE);
                if (userName.getText().length() != 0 && userFname.getText().length() != 0
                        && userCity.getSelectedItem().toString().length() != 0 && userNarr.getText().length() != 0 && userDate.getText().length() != 0) {
                    try {
                        getEditResponse();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                    bar.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.userAvatar:
                Pix.start(this, Options.init().setRequestCode(100));
                break;
        }
    }
    public void requestCity() {
        String url = "https://orzu.org/api?%20appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=getOther&get=cities";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UserEditProfile.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(UserEditProfile.this, android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestCity();
                                dialog.dismiss();
                            }
                        });
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.show();
                            }
                        }, 500);
                        bar.setVisibility(View.INVISIBLE);
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String mMessage = response.body().string();
                int i;
                try {
                    JSONArray jsonArray = new JSONArray(mMessage);
                    int lenght = jsonArray.length();
                    cities = new String[lenght];
                    for (i = 0; i < lenght; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        cities[i] = (jsonObject.getString("name"));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new ArrayAdapter<String>(UserEditProfile.this,
                                    android.R.layout.simple_spinner_dropdown_item, cities);
                            userCity.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            try {
                                getUserForEdit();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void getEditResponse() throws IOException {
        int day = calendar.getDayOfMonth();
        int monthNumber = calendar.getMonth() + 1;
        int year = calendar.getYear();
        radioButton = findViewById(R.id.male);
        if (radioButton.isChecked()) {
            gender = "male";
        } else {
            gender = "female";
        }
        String url = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS" +
                "&opt=user_param" +
                "&act=edit" +
                "&userid=" + idUser +
                "&utoken=" + tokenUser +
                "&name=" + userName.getText() +
                "&fname=" + userFname.getText() +
                "&city=" + userCity.getSelectedItem().toString() +
                "&about=" + userNarr.getText() +
                "&gender=" + gender +
                "&bday=" + day +
                "&bmonth=" + monthNumber +
                "&byear=" + year;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UserEditProfile.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(UserEditProfile.this, android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    getEditResponse();
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
                        bar.setVisibility(View.INVISIBLE);
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String mMessage = response.body().string();
                if (returnValue.isEmpty()) {
                    UserEditProfile.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(UserEditProfile.this, mMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                    final SharedPreferences prefs = UserEditProfile.this.getSharedPreferences(" ", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("UserCityPref",userCity.getSelectedItem().toString());
                    editor.apply();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bar.setVisibility(View.INVISIBLE);
                        }
                    });

                    Common.d = mAvatar.getDrawable();
                    Common.name = userName.getText() + " " + userFname.getText();
                    Common.birth = userDate.getText().toString();
                    Common.city = userCity.getSelectedItem().toString();
                    Common.about = userNarr.getText().toString();
                    if (gender.equals("male")) {
                        Common.sex = "мужской";
                    } else
                        Common.sex = "женский";
                    finish();
                } else {
                    getEditAvatarResponse();
                }
            }
        });
    }
    public void getEditAvatarResponse() throws IOException {
        String url = "https://orzu.org/api/avatar";
        OkHttpClient client = new OkHttpClient();
        File myFile = new File(Uri.parse(returnValue.get(0)).getPath());
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", myFile.getName(),
                        RequestBody.create(MediaType.parse("text/csv"), myFile))
                .addFormDataPart("userid", idUser)
                .addFormDataPart("utoken", tokenUser)
                .addFormDataPart("appid", "$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bar.setVisibility(View.INVISIBLE);
                    }
                });
                UserEditProfile.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(UserEditProfile.this, android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    getEditAvatarResponse();
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
                        bar.setVisibility(View.INVISIBLE);
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                UserEditProfile.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(UserEditProfile.this, "Ваш профиль изменен", Toast.LENGTH_SHORT).show();
                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bar.setVisibility(View.INVISIBLE);
                    }
                });
                Common.d = mAvatar.getDrawable();
                finish();
            }
        });
    }
    public void getUserForEdit() throws IOException {
        dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("orzutable", null, null, null, null, null, null);
        c.moveToFirst();
        int idColIndex = c.getColumnIndex("id");
        int tokenColIndex = c.getColumnIndex("token");
        idUser = c.getString(idColIndex);
        tokenUser = c.getString(tokenColIndex);
        c.moveToFirst();
        c.close();
        db.close();
        String url = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&lang=ru&opt=view_user&user=" + idUser + "&param=more";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UserEditProfile.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(UserEditProfile.this, android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    getUserForEdit();
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
                        bar.setVisibility(View.INVISIBLE);
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mMessage = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(mMessage);
                    mName = jsonObject.getString("name");
                    mFiName = jsonObject.getString("fname");
                    mCount = jsonObject.getString("tasks");
                    mCountReq = jsonObject.getString("task_requests");
                    mCity = jsonObject.getString("city");
                    mBday = jsonObject.getString("birthday");
                    mSex = jsonObject.getString("sex");
                    mNarr = jsonObject.getString("about");
                    mAvatarstr = jsonObject.getString("avatar");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Picasso.get().load("https://orzu.org" + mAvatarstr.replaceAll("\\\\","")).fit().centerCrop().into(mAvatar);
                            TranslateAnimation animationn = new TranslateAnimation(0.0f, 0.0f,
                                    1500.0f, 0.0f);
                            animationn.setDuration(200);
                            //animation.setFillAfter(true);
                            shim.setVisibility(View.GONE);
                            buttonEdit.setVisibility(View.GONE);
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    buttonEdit.setVisibility(View.VISIBLE);
                                    Animation animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(),
                                            R.anim.zoom_in);
                                    buttonEdit.startAnimation(animZoomIn);
                                }
                            }, animationn.getDuration());
                            image_back_name_tast3.setVisibility(View.GONE);
                        }
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            userName.setText(mName);
                            userFname.setText(mFiName);
                            userCity.setSelection(Arrays.asList(cities).indexOf(mCity));
                            userNarr.setText(mNarr);
                            userDate.setText(mBday);
                            SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy");
                            Date datesimple = new Date();
                            Calendar calendarnew = new GregorianCalendar();
                            try {
                                datesimple = fmt.parse(mBday);
                                if (datesimple != null) {
                                    calendarnew.setTime(datesimple);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            calendar.updateDate(calendarnew.get(Calendar.YEAR), calendarnew.get(Calendar.MONTH), calendarnew.get(Calendar.DAY_OF_MONTH));
                            if (mSex.equals("male")) {
                                radioGroup.check(R.id.male);
                            } else radioGroup.check(R.id.female);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            mAvatar.setImageURI(Uri.parse(returnValue.get(0)));
            Bitmap bitmap = ((BitmapDrawable) mAvatar.getDrawable()).getBitmap();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] byteArray = outputStream.toByteArray();
            //Use your Base64 String as you wish
            encodedString = Base64.encodeToString(byteArray, Base64.DEFAULT);
        }
    }
}
