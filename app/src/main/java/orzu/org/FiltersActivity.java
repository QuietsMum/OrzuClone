package orzu.org;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import orzu.org.ui.login.model;

public class FiltersActivity extends AppCompatActivity implements View.OnClickListener {

    public static void start(Context context) {
        Intent intent = new Intent(context, FiltersActivity.class);
        context.startActivity(intent);
    }
    String[] filtname1 = {"Категории", "Город"};
    String[] filtname2 = {"Оплата картой", "Задания без предложений", "Бизнес-задания"};
    String[] filtres1 = {"Все категории", Common.city};
    String[] filtres2 = {"Через сделку без риска", "Успей откликнутся", "Безналичная оплата"};
    int[] filtimg1 = {R.drawable.all_org, R.drawable.point_org};
    int[] filtimg2 = {R.drawable.card_org, R.drawable.flash_org, R.drawable.work_org};
    String cat = "Cat";
    String res = "Res";
    String img = "Img";
    TextView button;
    ArrayList<Map<String, Object>> data1;
    ArrayList<Map<String, Object>> data2;
    SimpleAdapter arrayAdapter1;
    SimpleAdapter arrayAdapter2;
    ListView lvMain1;
    ListView lvMain2;
    CardView cardView;
    ImageView filter_back;
    TextView clear;
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = this.getSharedPreferences(" ", Context.MODE_PRIVATE);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryPurpleTop));
        setContentView(R.layout.activity_filters);
        lvMain1 = (ListView) findViewById(R.id.list_view_filters1);
        lvMain2 = (ListView) findViewById(R.id.list_view_filters2);
        button = findViewById(R.id.buttonDone);
        button.setOnClickListener(this);
        button.setVisibility(View.INVISIBLE);
        filter_back = findViewById(R.id.filter_back);
        filter_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        cardView = findViewById(R.id.card_of_filter);
        cardView.setBackgroundResource(R.drawable.shape_card_topcorners);
        data1 = new ArrayList<>();
        data2 = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Map<String, Object> m1 = new HashMap<>();
            m1.put(cat, filtname1[i]);
            m1.put(res, filtres1[i]);
            m1.put(img, filtimg1[i]);
            data1.add(m1);
        }
        Map<String, Object> m2 = new HashMap<>();
        m2.put(cat, filtname2[1]);
        m2.put(res, filtres2[1]);
        m2.put(img, filtimg2[1]);
        data2.add(m2);
        clear = findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.allCity = false;
                Long[] idArray = new Long[1];
                idArray[0] = 0L;
                model.array = idArray;
                Common.subFilter = new HashMap<>();
                button.setVisibility(View.VISIBLE);
                Common.city1 = "";
                Map<String, Object> m2sort = new HashMap<>();
                m2sort.put(cat, filtname1[1]);
                m2sort.put(res,prefs.getString("UserCityPref","Алматы"));
                m2sort.put(img
                        , filtimg1[1]);
                data1.remove(1);
                data1.add(1, m2sort);
                arrayAdapter1.notifyDataSetChanged();
            }
        });
        String[] from1 = {cat, res, img};
        int[] to1 = {R.id.textView10, R.id.textView11, R.id.imageViewFilt};
        arrayAdapter1 = new SimpleAdapter(this, data1, R.layout.filters_item_2, from1, to1);
        lvMain1.setAdapter(arrayAdapter1);
        lvMain1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Intent intent = new Intent(FiltersActivity.this, CategoryView.class);
                    startActivity(intent);
                }
                if (i == 1) {
                    Intent intent2 = new Intent(FiltersActivity.this, SubCategoryView2.class);
                    startActivityForResult(intent2, 1);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        button.setVisibility(View.VISIBLE);
                    }
                }, 1200);
            }
        });
        String[] from2 = {cat, res, img};
        int[] to2 = {R.id.textView10, R.id.textView11, R.id.imageViewFilt};
        arrayAdapter2 = new SimpleAdapter(this, data2, R.layout.filters_item, from2, to2);
        lvMain2.setAdapter(arrayAdapter2);
        lvMain2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Switch swit = view.findViewById(R.id.switch1);
                if (!swit.isChecked()) {
                    swit.setChecked(true);
                } else swit.setChecked(false);
                button.setVisibility(View.VISIBLE);
            }
        });
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                1500.0f, 0.0f);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                Common.city1 = result;
                if(Common.allCity){
                    Common.city1 = "";
                }
                Map<String, Object> m2sort = new HashMap<>();
                m2sort.put(cat, filtname1[1]);
                m2sort.put(res, result);
                m2sort.put(img, filtimg1[1]);
                m2sort.put(res, result);
                data1.remove(1);
                data1.add(1, m2sort);
                arrayAdapter1.notifyDataSetChanged();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }
    @Override
    public void onClick(View view) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", "changes");
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.up_menu_filters_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_filter_menu_main:
                Long[] idArray = new Long[1];
                idArray[0] = 0L;
                model.array = idArray;
                Common.subFilter = new HashMap<>();
                button.setVisibility(View.VISIBLE);
                Common.city1 = "";
                Map<String, Object> m2sort = new HashMap<>();
                m2sort.put(cat, filtname1[1]);
                m2sort.put(res, prefs.getString("UserCityPref","Алматы"));
                m2sort.put(img, filtimg1[1]);
                data1.remove(1);
                data1.add(1, m2sort);
                arrayAdapter1.notifyDataSetChanged();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
