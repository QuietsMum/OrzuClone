package orzu.org;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.viewpagerindicator.CirclePageIndicator;
import java.util.ArrayList;

public class ViewpagerStart extends AppCompatActivity implements View.OnClickListener {
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private static final Integer[] IMAGES= {R.drawable.f1, R.drawable.f1, R.drawable.f2};
    private static final Integer[] IMAGESBACK= {R.drawable.ic_intro_back_1, R.drawable.ic_intro_back_2, R.drawable.ic_intro_back_3};
    private static final String[] TEXT= {"Создайте задачу" , "Выберите исполнителя", "Задача выполнена"};
    private static final String[] TEXT2= {
            "Расскажите нам, что нужно для вас сделать.\n Укажите время, место и описание.",
            "Предложение от доверенных исполнителей.\n Выберите подходящего человека по отзывам и цене для работы.",
            "Ваш исполнитель прибывает и выполняет свою работу.\n Оплачивайте через безопасную сделку."};
    private ArrayList<Integer> ImagesArray = new ArrayList<Integer>();
    private ArrayList<Integer> ImagesArrayBack = new ArrayList<Integer>();
    private ArrayList<String> TextArray = new ArrayList<String>();
    private ArrayList<String> TextArray2 = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_viewpager_start);
        TextView button = findViewById(R.id.buttonPager);
        button.setOnClickListener(this);
        init();
    }
    private void init() {
        for(int i=0;i<IMAGES.length;i++) {
            ImagesArray.add(IMAGES[i]);
            ImagesArrayBack.add(IMAGESBACK[i]);
            TextArray.add(TEXT[i]);
            TextArray2.add(TEXT2[i]);
        }
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new SlidingImage_Adapter(ViewpagerStart.this, ImagesArray, ImagesArrayBack, TextArray, TextArray2));
        CirclePageIndicator indicator = (CirclePageIndicator)
                findViewById(R.id.indicator);
        indicator.setViewPager(mPager);
        final float density = getResources().getDisplayMetrics().density;
//Set circle indicator radius
        indicator.setRadius(5 * density);
        NUM_PAGES =IMAGES.length;
        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }
            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {
                if(currentPage == 0){
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryGreen));
                } else if (currentPage == 1){
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryBlue));
                } else getWindow().setStatusBarColor(getResources().getColor(R.color.redview));
            }
            @Override
            public void onPageScrollStateChanged(int pos) {
            }
        });
    }
    @Override
    public void onClick(View view) {
        if(currentPage == 0){
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryGreen));
        } else if (currentPage == 1){
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryBlue));
        } else getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryRed));
        currentPage++;
        if(currentPage == mPager.getAdapter().getCount()){
            Intent intent = new Intent(ViewpagerStart.this, PhoneLoginActivity.class);
            startActivity(intent);
            currentPage = 0;
            finish();
        }  else  mPager.setCurrentItem(currentPage, true);
    }
}
