package orzu.org;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PortfolioActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView backAct,logo_of_partner;
    CardView cardView;
    RecyclerView rv;
    List<Bonuses> list = new ArrayList<>();
    TextView ok_portfolio,name,description,name_of_partner,partnerName,partnerDis;
    String idOfPartner,logoPartner,descPartner,namePartner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryPurpleTop));
        setContentView(R.layout.activity_portfolio);

        idOfPartner = getIntent().getExtras().getString("idpartner","");
        logoPartner = getIntent().getExtras().getString("logopartner","");
        descPartner = getIntent().getExtras().getString("descpartner","");
        namePartner = getIntent().getExtras().getString("namepartner","");

        name_of_partner = findViewById(R.id.name_of_partner);
        partnerName = findViewById(R.id.partnerName);
        partnerDis = findViewById(R.id.partnerDis);
        logo_of_partner = findViewById(R.id.logo_of_partner);

        Picasso.get().load("https://orzu.org/"+logoPartner).into(logo_of_partner);
        name_of_partner.setText(namePartner);
        partnerName.setText(namePartner);
        partnerDis.setText(descPartner);
        getPartnersSales();

        rv = findViewById(R.id.rv_portfolio);

        ok_portfolio = findViewById(R.id.ok_portfolio);
        ok_portfolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        cardView = findViewById(R.id.card_of_portfolio);
        cardView.setBackgroundResource(R.drawable.shape_card_topcorners2);
        backAct = findViewById(R.id.create_amount_back);
        backAct.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        finish();
    }
    private void getPartnersSales() {
        String requestUrl = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=user_param&act=sales_list_my&id="+idOfPartner;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, requestUrl, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (response.equals("\"\\u041d\\u0435\\u0442 \\u043f\\u0430\\u0440\\u0442\\u043d\\u0451\\u0440\\u043e\\u0432 \\u0432 \\u0434\\u0430\\u043d\\u043d\\u043e\\u0439 \\u043a\\u0430\\u0442\\u0435\\u0433\\u043e\\u0440\\u0438\\u0438\"")) {
                        list.add(new Bonuses("Список пуст",""));
                    }else {
                        JSONArray j = new JSONArray(response);
                        for (int i = 0; i < j.length(); i++) {
                            JSONObject object = j.getJSONObject(i);
                            list.add(new Bonuses(object.getString("sale_name"), object.getString("sale_percent") + "%"));
                        }
                        AdapterPartnerBonuses adapter = new AdapterPartnerBonuses(PortfolioActivity.this, list);
                        rv.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace(); //log the error resulting from the request for diagnosis/debugging
                Dialog dialog = new Dialog(PortfolioActivity.this, android.R.style.Theme_Material_Light_NoActionBar);
                dialog.setContentView(R.layout.dialog_no_internet);
                Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getPartnersSales();
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
        Volley.newRequestQueue(Objects.requireNonNull(this)).add(stringRequest);
    }
}
