package orzu.org;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.BoundingBox;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;
import com.yandex.mapkit.search.SearchOptions;
import com.yandex.mapkit.search.SearchType;
import com.yandex.mapkit.search.SuggestItem;
import com.yandex.runtime.Error;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class CreateTaskPlace extends AppCompatActivity implements View.OnClickListener, SearchManager.SuggestListener {
    TextView buttonCreate;
    TextView buttonCreateLeft;
    TextView buttonCreateRight;
    TextView textFar;
    @SuppressLint("StaticFieldLeak")
    public static Activity fa;
    int counter;
    int placetype;
    final int RESULT_NUMBER_LIMIT = 5;
    ImageView tri_left;
    ImageView tri_right;
    ImageView create_place_back;
    CardView card_of_create_place;

    SearchManager searchManager;
    RecyclerView suggestResultView;
    ArrayAdapterMy resultAdapter;
    List<String> suggestResult;
    EditText queryEdit;
    final Point CENTER = new Point(55.75, 37.62);
    final double BOX_SIZE = 0.2;
    final BoundingBox BOUNDING_BOX = new BoundingBox(
            new Point(CENTER.getLatitude() - BOX_SIZE, CENTER.getLongitude() - BOX_SIZE),
            new Point(CENTER.getLatitude() + BOX_SIZE, CENTER.getLongitude() + BOX_SIZE));
    final SearchOptions SEARCH_OPTIONS = new SearchOptions().setSearchTypes(
            SearchType.GEO.value |
                    SearchType.BIZ.value |
                    SearchType.TRANSIT.value);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setApiKey("38c4c9bc-766a-4574-8088-18a4e7583a90");
        MapKitFactory.initialize(this);
        SearchFactory.initialize(this);
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryPurpleTop));
        setContentView(R.layout.activity_create_task_place);
        counter = 1;
        placetype = 1;
        textFar = findViewById(R.id.text_far);
        textFar.setVisibility(View.INVISIBLE);
        card_of_create_place = findViewById(R.id.card_of_create_place);
        card_of_create_place.setBackgroundResource(R.drawable.shape_card_topcorners);
        buttonCreate = findViewById(R.id.createPlace);
        buttonCreateLeft = findViewById(R.id.createPlace_buttonleft);
        buttonCreateRight = findViewById(R.id.createPlace_buttonright);

        tri_left = findViewById(R.id.tri_left);
        tri_right = findViewById(R.id.tri_right);
        create_place_back = findViewById(R.id.create_place_back);

        buttonCreate.setOnClickListener(this);
        buttonCreateLeft.setOnClickListener(this);
        buttonCreateRight.setOnClickListener(this);
        fa = this;
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);
        queryEdit = (EditText) findViewById(R.id.suggest_query);
        suggestResultView = findViewById(R.id.suggest_result);
        suggestResultView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        suggestResultView.setLayoutManager(layoutManager);
        suggestResult = new ArrayList<>();
        resultAdapter = new ArrayAdapterMy(this, suggestResult);
        suggestResultView.setAdapter(resultAdapter);


        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                1500.0f, 0.0f);
        animation.setDuration(500);
        //animation.setFillAfter(true);
        card_of_create_place.startAnimation(animation);
        tri_left.startAnimation(animation);
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

        queryEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                requestSuggest(editable.toString());
            }
        });
        create_place_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createPlace:
                if (queryEdit.getVisibility() == View.VISIBLE) {
                    if (queryEdit.getText().length() != 0) {
                        final SharedPreferences prefs = getSharedPreferences(" ", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        if (counter == 1) {
                            placetype = 1;
                            editor.putString(Util.TASK_PLACE, String.valueOf(queryEdit.getText()));
                            editor.putString(Util.TASK_PLACETYPE, String.valueOf(placetype));
                        } else if (counter == 2) {
                            placetype = 2;
                            editor.putString(Util.TASK_PLACE, "Удаленно");
                            editor.putString(Util.TASK_PLACETYPE, String.valueOf(placetype));
                        }
                        editor.apply();
                        Intent intent = new Intent(this, CreateTaskTerm.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    final SharedPreferences prefs = getSharedPreferences(" ", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    if (counter == 1) {
                        placetype = 1;
                        editor.putString(Util.TASK_PLACE, String.valueOf(queryEdit.getText()));
                        editor.putString(Util.TASK_PLACETYPE, String.valueOf(placetype));
                    } else if (counter == 2) {
                        placetype = 2;
                        editor.putString(Util.TASK_PLACE, "Удаленно");
                        editor.putString(Util.TASK_PLACETYPE, String.valueOf(placetype));
                    }
                    editor.apply();
                    Intent intent = new Intent(this, CreateTaskTerm.class);
                    startActivity(intent);
                }
                break;

            case R.id.createPlace_buttonleft:
                counter = 1;
                textFar.setVisibility(View.INVISIBLE);
                queryEdit.setVisibility(View.VISIBLE);
                tri_left.setVisibility(View.VISIBLE);
                tri_right.setVisibility(View.INVISIBLE);
                break;

            case R.id.createPlace_buttonright:
                counter = 2;
                textFar.setVisibility(View.VISIBLE);
                queryEdit.setVisibility(View.INVISIBLE);
                tri_left.setVisibility(View.INVISIBLE);
                tri_right.setVisibility(View.VISIBLE);
                break;

        }
    }

    @Override
    protected void onStop() {
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
    }

    @Override
    public void onSuggestResponse(@NotNull List<SuggestItem> suggest) {
        suggestResult.clear();
        for (int i = 0; i < Math.min(RESULT_NUMBER_LIMIT, suggest.size()); i++) {
            suggestResult.add(suggest.get(i).getDisplayText());
        }
        resultAdapter.notifyDataSetChanged();
        suggestResultView.setVisibility(View.VISIBLE);

        ArrayAdapterMy.setSelect(new NameItemSelect() {
            @Override
            public void onItemSelectedListener(View view, int position) {
                queryEdit.setText(suggest.get(position).getDisplayText());
                queryEdit.setSelection(queryEdit.getText().length());
            }

            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public void onSuggestError(@NotNull Error error) {
        String errorMessage = "unknown_error_message";
        if (error instanceof RemoteError) {
            errorMessage = "remote_error_message";
        } else if (error instanceof NetworkError) {
            errorMessage = "network_error_message";
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void requestSuggest(String query) {
        suggestResultView.setVisibility(View.INVISIBLE);
        searchManager.suggest(query, BOUNDING_BOX, SEARCH_OPTIONS, this);
    }


}
