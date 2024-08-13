package orzu.org;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rilixtech.Country;
import com.rilixtech.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PhoneLoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText phone;
    EditText phoneCount;
    EditText password;
    TextView regist;
    TextView forgot_pwd;
    FloatingActionButton button;
    ProgressBar progressBar;
    String mMessage;
    String mPassword;
    Long mID;
    String mStatus;
    String mToken;
    String code;
    JSONObject obj;
    CountryCodePicker ccp;
    DBHelper dbHelper;
    LinearLayout linLay;
    static Boolean firsttime;
    CardView cardView;
    LocationManager locationManager;
    String lattitude, longitude;
    private static final int REQUEST_LOCATION = 1;
    String country_code;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryOrangeTop));
        getSupportActionBar().hide();
        setContentView(R.layout.activity_phone_login);
        dbHelper = new DBHelper(this);
        firsttime = false;
        dialog = new Dialog(PhoneLoginActivity.this, android.R.style.Theme_Material_Light_NoActionBar);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        progressBar = findViewById(R.id.progressBarLogin);
        progressBar.setVisibility(View.INVISIBLE);
        phone = (EditText) findViewById(R.id.editTextPhone);
        phoneCount = (EditText) findViewById(R.id.editTextPhoneCountry);
        password = (EditText) findViewById(R.id.editTextPassword);
        button = findViewById(R.id.button_change_pwd);
        cardView = findViewById(R.id.constrlog2);
        cardView.setBackgroundResource(R.drawable.shape_card_topcorners);
        forgot_pwd = findViewById(R.id.forgot_pwd);
        forgot_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PhoneLoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });
        regist = findViewById(R.id.button_phone_regist);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        linLay = findViewById(R.id.linearidccp);
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                1000.0f, 0.0f);
        animation.setDuration(500);
        cardView.startAnimation(animation);
        button.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                button.setVisibility(View.VISIBLE);
                Animation animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.zoom_in);
                button.startAnimation(animZoomIn);
            }
        }, animation.getDuration());
        float dip = 16f;
        Resources r = Resources.getSystem();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );
        linLay.setOnClickListener(this);
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
                code = ccp.getSelectedCountryCodeWithPlus();
                phoneCount.setText(code);
            }
        });
        code = ccp.getSelectedCountryCodeWithPlus();
        phoneCount.setText(code);
        button.setOnClickListener(this);
        regist.setOnClickListener(this);
        getLocate();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.button_change_pwd):
                if (phone.getText().length() != 0 && password.getText().length() != 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    mMessage = phone.getText().toString();
                    mPassword = password.getText().toString();
                    try {
                        getHttpResponse();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case (R.id.button_phone_regist):
                Intent intent = new Intent(getApplicationContext(), LoginActivity2.class);
                startActivity(intent);
                finish();
                break;
            case (R.id.linearidccp):
                break;
        }
    }
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(PhoneLoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (PhoneLoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(PhoneLoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);


            } else if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);


            } else if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

            } else {

                Toast.makeText(this, "Unable to Trace your location", Toast.LENGTH_SHORT).show();

            }
        }
        getCountry();
    }
    public void getCountry() {
        String requestUrl = "https://nominatim.openstreetmap.org/reverse?q=1&lat=" + lattitude + "&lon=" + longitude + "&format=json&email=almasnurlanov16@gmail.com&namedetails=1&addressdetails=1&extratags=1";
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, requestUrl, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject j = new JSONObject(response);

                    if (j.has("address")) {
                        JSONObject address = j.getJSONObject("address");
                        country_code = address.getString("country_code");
                    }

                    if (country_code.length() != 0) {
                        ccp.setCountryForNameCode(country_code.toUpperCase());
                    }
                    code = ccp.getSelectedCountryCodeWithPlus();
                    phoneCount.setText(code);
                    Common.countryCode = country_code;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace(); //log the error resulting from the request for diagnosis/debugging
                dialog.setContentView(R.layout.dialog_no_internet);
                Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getCountry();
                        dialog.dismiss();
                    }
                });
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       // dialog.show();
                    }
                }, 500);
            }
        });
        Volley.newRequestQueue(Objects.requireNonNull(this)).add(stringRequest);
    }

    protected void getLocate() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
        }
    }

    @Override
    protected void onPause() {
        dialog.dismiss();
        super.onPause();
    }

    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    public void getHttpResponse() throws IOException {
        String url = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=user_auth&phone=" + ccp.getFullNumberWithPlus() + phone.getText() + "&password=" + mPassword;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mMessage = e.getMessage().toString();
                PhoneLoginActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(PhoneLoginActivity.this, android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    getHttpResponse();
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
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mMessage = response.body().string();
                final char dm = (char) 34;
                if (mMessage.equals(dm + "noAuth" + dm)) {
                    PhoneLoginActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(PhoneLoginActivity.this, "Не правильный логин или пароль", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                } else if (mMessage.contains("API not correct request")) {
                    PhoneLoginActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(PhoneLoginActivity.this, "Пользователя с таким номером не существует", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                } else {
                    try {
                        obj = new JSONObject(mMessage);
                        mStatus = obj.getString("auth_status");
                        mToken = obj.getString("_token");
                        mID = obj.getLong("id");
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues cv = new ContentValues();
                        cv.put("token", mToken);
                        cv.put("id", mID);
                        db.insert("orzutable", null, cv);
                        if (obj.getString("auth_status").equals("yes")) {
                            Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                            startActivity(intent);
                            firsttime = true;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                            Common.referrer = "";
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLocate();
    }

    public static boolean performClick(View view) {
        return view.isEnabled() && view.isClickable() && view.performClick();
    }
}
