package orzu.org;

import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Objects;

import orzu.org.ui.login.model;

public class CoreActivity extends AppCompatActivity {
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.activity_core);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "orzu.org",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        Long[] idArray = new Long[1];
        idArray[0] = 0L;
        model.array = idArray;
        Common.subFilter = new HashMap<>();

        ImageView logo = (ImageView)findViewById(R.id.imageLogoNew);

        dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query("orzutable", null, null, null, null, null, null);
        c.moveToFirst();



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                final AnimatedVectorDrawableCompat anim = AnimatedVectorDrawableCompat.create(getApplication(), R.drawable.logo_animated);
                logo.setImageDrawable(anim);
                assert anim != null;
                anim.start();
            }
        }, 500);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                if (c.getCount() > 0){
                    Intent intent = new Intent(CoreActivity.this, Main2Activity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(CoreActivity.this, ViewpagerStart.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2200);
        c.close();
        db.close();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
