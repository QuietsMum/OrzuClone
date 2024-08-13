package orzu.org;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CreateTaskName extends AppCompatActivity implements View.OnClickListener {

    TextView buttonCreate;
    TextView name_of_subcategory;
    EditText editName;
    @SuppressLint("StaticFieldLeak")
    public static Activity fa;
    RecyclerView suggestResultView;
    ArrayAdapterMy resultAdapter;
    List<String> suggestResult;
    CardView cardView;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryPurpleTop));
        setContentView(R.layout.activity_create_task_name);
        editName = findViewById(R.id.editCreateName);
        buttonCreate = findViewById(R.id.createName);
        buttonCreate.setOnClickListener(this);
        fa = this;
        name_of_subcategory = findViewById(R.id.name_of_subcategory);
        name_of_subcategory.setText(Common.taskName);
        cardView = findViewById(R.id.card_of_name_task);
        cardView.setBackgroundResource(R.drawable.shape_card_topcorners);
        back = findViewById(R.id.create_name_back);
        suggestResultView = findViewById(R.id.suggest_result_name);
        suggestResultView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        suggestResultView.setLayoutManager(layoutManager);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                1500.0f, 0.0f);
        animation.setDuration(500);
        //animation.setFillAfter(true);
        cardView.startAnimation(animation);
        name_of_subcategory.startAnimation(animation);
        buttonCreate.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                buttonCreate.setVisibility(View.VISIBLE);
                Animation animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.zoom_in);
                buttonCreate.startAnimation(animZoomIn);
            }
        }, animation.getDuration());

        editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (editName.getText().length() != 0) {

                    requestSuggest();
                }
            }
        });

    }



    @Override
    public void onClick(View view) {
        if (editName.getText().length() != 0) {
            final SharedPreferences prefs = getSharedPreferences(" ", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Util.TASK_NAME, String.valueOf(editName.getText()));
            editor.apply();
            Intent intent = new Intent(this, CreateTaskPlace.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
        }
    }

    public void requestSuggest() {

        String url = "https://orzu.org/tasks/taskajaxupload?find=" + editName.getText();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                CreateTaskName.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(CreateTaskName.this, android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestSuggest();
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
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String mMessage = Objects.requireNonNull(response.body()).string();
                suggestResult = new ArrayList<>();

                try {
                    JSONArray jsonArray = new JSONArray(mMessage);
                    int length = jsonArray.length();

                    if (length > 0) {
                        for (int i = 0; i < length; i++) {

                            suggestResult.add(jsonArray.getString(i));
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                resultAdapter = new ArrayAdapterMy(CreateTaskName.this, suggestResult);
                                suggestResultView.setAdapter(resultAdapter);
                                suggestResultView.getRecycledViewPool().clear();
                                resultAdapter.notifyDataSetChanged();
                                suggestResultView.setVisibility(View.VISIBLE);

                            }
                        });


                        ArrayAdapterMy.setSelect(new NameItemSelect() {
                            @Override
                            public void onItemSelectedListener(View view, int position) {
                                if (suggestResult.size() != 0) {
                                    editName.setText(suggestResult.get(position));
                                    editName.setSelection(editName.getText().length());
                                }

                            }

                            @Override
                            public void onClick(View view) {

                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
