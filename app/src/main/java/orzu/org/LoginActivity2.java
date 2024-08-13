package orzu.org;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rilixtech.Country;
import com.rilixtech.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class LoginActivity2 extends AppCompatActivity implements View.OnClickListener {
    EditText name;
    EditText phon;
    EditText phonCount;
    EditText pass;
    FloatingActionButton button;
    String mMessage;
    String mPhone;
    String mName;
    String mPassword;
    String mStatus;
    String mToken;
    Long mID;
    String code;
    ProgressBar progressBar;
    JSONObject obj;
    CountryCodePicker ccp;
    Dialog dialog;
    EditText input;
    DBHelper dbHelper;
    CardView cardview;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private FirebaseAuth fbAuth;
    private String phoneVerificationId;
    CountDownTimer timer;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login2);
        dbHelper = new DBHelper(this);
        fbAuth = FirebaseAuth.getInstance();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, MY_REQUEST);
            }
        }

        cardview = findViewById(R.id.card_of_registr);
        cardview.setBackgroundResource(R.drawable.shape_card_topcorners);
        progressBar = findViewById(R.id.progressBarLogin_reg);
        progressBar.setVisibility(View.INVISIBLE);
        name = (EditText) findViewById(R.id.editTextNameUser);
        phon = (EditText) findViewById(R.id.editTextPhone_reg);
        phonCount = (EditText) findViewById(R.id.editTextPhoneCountry);
        pass = (EditText) findViewById(R.id.editTextPass);
        button = findViewById(R.id.button_phone_login);
        float dip = 16f;
        Resources r = Resources.getSystem();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );
        ccp = (CountryCodePicker) findViewById(R.id.ccp_reg);
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
                code = ccp.getSelectedCountryCodeWithPlus();
                phonCount.setText(code);
            }
        });
        if (Common.countryCode.length() != 0) {
            ccp.setCountryForNameCode(Common.countryCode.toUpperCase());

        }
        code = ccp.getSelectedCountryCodeWithPlus();
        phonCount.setText(code);
        button.setOnClickListener(this);

    }

    private static final int MY_REQUEST = 0;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Хорошо", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Напишите код вручную", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        View arv = view;
        if (name.getText().length() != 0 && phon.getText().length() != 0 && pass.getText().length() != 0) {
            progressBar.setVisibility(View.VISIBLE);
            mPhone = phonCount.getText() + phon.getText().toString();
            mName = name.getText().toString();
            mPassword = pass.getText().toString();
            sendCode();
            dialog = new Dialog(this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
            dialog.setContentView(R.layout.alert_dialog);
            input = dialog.findViewById(R.id.editTextSms);
            Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
            // if button is clicked, close the cust
            TextView textView = dialog.findViewById(R.id.count);
            TextView btn = dialog.findViewById(R.id.btn);
            timer = new CountDownTimer(60000, 1000) {
                @SuppressLint("SetTextI18n")
                public void onTick(long millisUntilFinished) {
                    textView.setText("" + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    textView.setVisibility(View.INVISIBLE);
                    btn.setVisibility(View.VISIBLE);
                }
            }.start();
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btn.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.VISIBLE);
                    timer.start();
                    sendCode();
                }
            });
            timer.start();
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!input.getText().toString().isEmpty()) {
                        verifyCode();
                    } else {
                        Toast.makeText(LoginActivity2.this, "Введите код", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.show();
        } else {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), PhoneLoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void getHttpResponse() throws IOException {
        String url = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=register_user&phone="
                + mPhone
                + "&password=" + mPassword
                + "&name=" + mName;
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
                if (mMessage.equals("noAuth")) {
                    Toast.makeText(getApplicationContext(), "No registered user!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity2.class);
                    startActivity(intent);
                } else {
                    Dialog dialog = new Dialog(LoginActivity2.this, android.R.style.Theme_Material_Light_NoActionBar);
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
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mMessage = response.body().string();
                if (mMessage.equals("\"This user alreday registered\"")) {
                    LoginActivity2.this.runOnUiThread(new Runnable() {
                        public void run() {
                            AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity2.this).create();
                            alertDialog.setTitle("");
                            alertDialog.setMessage("Этот номер уже зарегистрирован");
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Готово",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog1, int which) {
                                            dialog.dismiss();
                                            Intent intent = new Intent(getApplicationContext(), PhoneLoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                            alertDialog.show();
                            Toast.makeText(LoginActivity2.this, "", Toast.LENGTH_SHORT).show();
                            Common.referrer = "";
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
                        cv.put("id", mID);
                        cv.put("token", mToken);
                        cv.put("name", mName);
                        db.insert("orzutable", null, cv);
                        db.close();
                        dbHelper.close();
                        Intent intent = new Intent(getApplicationContext(), RegistCity.class);
                        startActivity(intent);
                        progressBar.setVisibility(View.INVISIBLE);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    public void sendCode() {
        setUpVerificatonCallbacks();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mPhone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                verificationCallbacks);
    }

    private void setUpVerificatonCallbacks() {
        verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(
                            PhoneAuthCredential credential) {
                        signInWithPhoneAuthCredential(credential);
                        Toast.makeText(LoginActivity2.this, "Успешно доставлен", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity2.this).create();
                            alertDialog.setTitle("");
                            alertDialog.setMessage("Не правильный номер");
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Готово",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog1, int which) {
                                            dialog.dismiss();
                                            progressBar.setVisibility(View.INVISIBLE);
                                            alertDialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity2.this).create();
                            alertDialog.setTitle("");
                            alertDialog.setMessage("На сегодня ваш лимит исчерпан. Повторите позже");
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Готово",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog1, int which) {
                                            dialog.dismiss();
                                            alertDialog.dismiss();
                                            progressBar.setVisibility(View.INVISIBLE);
                                            startActivity(new Intent(LoginActivity2.this, PhoneLoginActivity.class));
                                            finish();
                                        }
                                    });
                            alertDialog.show();
                        } else {
                            Log.wtf("ASdasd",e+"");
                            AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity2.this).create();
                            alertDialog.setTitle("");
                            alertDialog.setMessage("Нету интернет подключения");
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Готово",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog1, int which) {
                                            timer.onFinish();
                                            progressBar.setVisibility(View.INVISIBLE);
                                            alertDialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {
                        phoneVerificationId = verificationId;
                        resendToken = token;
                        Toast.makeText(LoginActivity2.this, "Код отправлен", Toast.LENGTH_SHORT).show();
                    }
                };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            FirebaseUser user = task.getResult().getUser();
                            try {
                                getHttpResponse();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                //dialog.dismiss();
                                Toast.makeText(LoginActivity2.this, "Код не правильный", Toast.LENGTH_SHORT).show();
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void verifyCode() {
        String code = input.getText().toString();
        PhoneAuthCredential credential =
                PhoneAuthProvider.getCredential(phoneVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }
}