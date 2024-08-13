package orzu.org;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LastPageSale extends AppCompatActivity {
    CardView cardView;
    ImageView back;
    RecyclerView perc_rv;
    SaleAdapter adapter;
    List<category_model> list = new ArrayList<>();
    TextView btn_save;
    String Uid, utoken, Partner_Name, Partner_City, Partner_Desc, Partner_SubCat, Partner_Logo, Sale_Name, Sale_Logo, Sale_Desc, Sale_Cat, Sale_SubCat;
    String[] types = {"50", "30", "15"};
    int index = 0;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryPurpleTop));
        setContentView(R.layout.activity_last_page_sale);
        initiateStrings();
        cardView = findViewById(R.id.card_of_perc_sale);
        cardView.setBackgroundResource(R.drawable.shape_card_topcorners2);

        progressBar = findViewById(R.id.progress_of_last_page);

        list.add(new category_model("VIP", "asdkasdadladas", "50%"));
        list.add(new category_model("Standard", "asdkasdadladas", "30%"));
        list.add(new category_model("Beginner", "asdkasdadladas", "15%"));

        btn_save = findViewById(R.id.createSalePart);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                createPartner();
            }
        });

        adapter = new SaleAdapter(this, list);

        perc_rv = findViewById(R.id.sale_perc);
        perc_rv.setLayoutManager(new LinearLayoutManager(this));
        perc_rv.setAdapter(adapter);
        adapter.setClickListener(new SaleAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                adapter.changeColor(position);
                index = position;
            }
        });
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                1500.0f, 0.0f);
        animation.setDuration(500);
        //animation.setFillAfter(true);
        cardView.startAnimation(animation);
        btn_save.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                btn_save.setVisibility(View.VISIBLE);
                Animation animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.zoom_in);
                btn_save.startAnimation(animZoomIn);
            }
        }, animation.getDuration());
    }

    private void initiateStrings() {
        final SharedPreferences prefs = getSharedPreferences(" ", Context.MODE_PRIVATE);
        Uid = Common.userId;
        utoken = Common.utoken;
        Partner_Name = prefs.getString("Partner_Name", "");
        Partner_City = prefs.getString("Partner_City", "");
        Partner_Logo = prefs.getString("Partner_Logo", "");
        Partner_Desc = prefs.getString("Partner_Desc", "");
        Partner_SubCat = prefs.getString("Partner_SubCat", "");
        Sale_Name = prefs.getString("Sale_Name", "");
        Sale_Logo = prefs.getString("Sale_Logo", "");
        Sale_Desc = prefs.getString("Sale_Desc", "");
        Sale_Cat = prefs.getString("Sale_Cat", "");
        Sale_SubCat = prefs.getString("Sale_SubCat", "");
    }

    private void createPartner() {
        String requestUrl = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=user_param&act=create_partner&" +
                "userid=" + Uid +
                "&name=" + Partner_Name +
                "&utoken=" + utoken +
                "&partnersdisc=" + Partner_Desc +
                "&city=" + Partner_City +
                "&subcatid=" + Partner_SubCat +
                "&percent=" + types[index];
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, requestUrl, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (Partner_Logo.length() != 0) {
                    try {
                        String[] id_of_partner = response.split(":");
                        addPartnerLogo(id_of_partner[1]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    String[] id_of_partner = response.split(":");
                    createSale(id_of_partner[1]);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace(); //log the error resulting from the request for diagnosis/debugging
                Dialog dialog = new Dialog(LastPageSale.this, android.R.style.Theme_Material_Light_NoActionBar);
                dialog.setContentView(R.layout.dialog_no_internet);
                Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createPartner();
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
        Volley.newRequestQueue(Objects.requireNonNull(LastPageSale.this)).add(stringRequest);
    }

    private void createSale(String id) {
        id = id.replace("\"","");
        String requestUrl = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=user_param&act=create_partners_sale" +
                "&idPartner=" + id +
                "&namePartner=" + Partner_Name +
                "&utoken=" + utoken +
                "&sale_name=" + Sale_Name +
                "&description=" + Sale_Desc +
                "&partner_city=" + Partner_City +
                "&partners_cat=" + Sale_Cat +
                "&partners_subcat=" + Sale_SubCat +
                "&sale_percent=" + types[index] +
                "&idUser=" + Uid;
        String finalId = id;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, requestUrl, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (Sale_Logo.length() != 0) {
                    String[] id_of_partner = response.split(":");
                    addSaleLogo(id_of_partner[1]);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace(); //log the error resulting from the request for diagnosis/debugging
                Dialog dialog = new Dialog(LastPageSale.this, android.R.style.Theme_Material_Light_NoActionBar);
                dialog.setContentView(R.layout.dialog_no_internet);
                Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createSale(finalId);
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
        Volley.newRequestQueue(Objects.requireNonNull(LastPageSale.this)).add(stringRequest);
    }

    private void addSaleLogo(String id) {
        String url = "https://orzu.org/api/avatar";
        OkHttpClient client = new OkHttpClient();
        File myFile = new File(Uri.parse(Sale_Logo).getPath());
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", myFile.getName(),
                        RequestBody.create(MediaType.parse("text/csv"), myFile))
                .addFormDataPart("sale_id", id)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LastPageSale.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(LastPageSale.this, android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addSaleLogo(id);
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
            public void onResponse(Call call, Response response) throws IOException {
                LastPageSale.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(LastPageSale.this, "Все изменения добавлены", Toast.LENGTH_SHORT).show();
                    }
                });
                CreatePartnerSale.fa.finish();
                PartnerDetails.fa.finish();
                finish();
            }
        });
    }

    public void addPartnerLogo(String id) throws IOException {
        String url = "https://orzu.org/api/avatar";
        OkHttpClient client = new OkHttpClient();
        File myFile = new File(Uri.parse(Partner_Logo).getPath());
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", myFile.getName(),
                        RequestBody.create(MediaType.parse("text/csv"), myFile))
                .addFormDataPart("partnerid_logo", id)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LastPageSale.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(LastPageSale.this, android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    addPartnerLogo(id);
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
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LastPageSale.this.runOnUiThread(new Runnable() {
                    public void run() {
                        createSale(id);
                    }
                });
            }
        });
    }
}
