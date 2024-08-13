package orzu.org;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import orzu.org.ui.login.model;

public class CategorySubscriptions extends AppCompatActivity implements View.OnClickListener {
    ExpandableListView expandableListView;
    View podstilka;
    ArrayList<String> groupItem = new ArrayList<String>();
    ArrayList<String> groupId = new ArrayList<String>();
    ArrayList<Object> childItem = new ArrayList<Object>();
    AdapterRespandableLV mNewAdapter;
    ShimmerFrameLayout shim;
    TextView btn;
    String idUser;
    String tokenUser;
    DBHelper dbHelper;
    ArrayList<String> subsServer = new ArrayList<>();
    int counter;
    ProgressBar pb;
    CardView cardView;
    ImageView followed_view_back;
    LinearLayout linear_of_subs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryPurpleTop));
        setContentView(R.layout.activity_category_subscriptions);
        requestSubsServer();
        counter = 0;
        String groupFrom[] = new String[]{"Category"};
        int groupTo[] = new int[]{R.id.textViewCatSub};
        String childFrom[] = new String[]{"SubCategory"};
        int childTo[] = new int[]{R.id.radioButnSub};
        pb = findViewById(R.id.progresSubscriptions);
        pb.setVisibility(View.INVISIBLE);
        btn = findViewById(R.id.confirmSubs);
        btn.setOnClickListener(this);
        followed_view_back = findViewById(R.id.followed_view_back);
        followed_view_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        linear_of_subs = findViewById(R.id.linear_of_subs);
        linear_of_subs.setBackgroundResource(R.drawable.shape_card_topcorners);
        cardView = findViewById(R.id.card_of_followed_view);
        cardView.setBackgroundResource(R.drawable.shape_card_topcorners);
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                1500.0f, 0.0f);
        animation.setDuration(500);
        cardView.setAnimation(animation);
        btn.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                btn.setVisibility(View.VISIBLE);
                Animation animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.zoom_in);
                btn.startAnimation(animZoomIn);
            }
        }, animation.getDuration());
        mNewAdapter = new AdapterRespandableLV(groupItem, childItem);
        mNewAdapter
                .setInflater(
                        (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE),
                        this);

        expandableListView = (ExpandableListView) findViewById(R.id.expListView);
        podstilka = findViewById(R.id.podstilkaSuibs);
        podstilka.setBackgroundResource(R.drawable.shape_card_topcorners);
        shim = findViewById(R.id.shimSubs);
        shim.setBackgroundResource(R.drawable.shape_card_topcorners);
        shim.startShimmer();
        expandableListView.setIndicatorBoundsRelative(50, 50);
        expandableListView.setAdapter(mNewAdapter);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                if (((ArrayList<SubItem>) childItem.get(i)).get(i1).getCheck()) {
                    ((ArrayList<SubItem>) childItem.get(i)).get(i1).setCheck(false);
                    model.mapa.remove(((ArrayList<SubItem>) childItem.get(i)).get(i1).getParent_id() + ";" + ((ArrayList<SubItem>) childItem.get(i)).get(i1).getId());
                } else {
                    ((ArrayList<SubItem>) childItem.get(i)).get(i1).setCheck(true);
                    model.mapa.put(((ArrayList<SubItem>) childItem.get(i)).get(i1).getParent_id() + ";" + ((ArrayList<SubItem>) childItem.get(i)).get(i1).getId(), ((ArrayList<SubItem>) childItem.get(i)).get(i1).getId());
                }
                mNewAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }


    public void requestCategoryList() {

        String url = "https://orzu.org/api?%20appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&lang=ru&opt=view_cat&cat_id=only_parent";
        OkHttpClient client = new OkHttpClient();


        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                CategorySubscriptions.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(CategorySubscriptions.this, android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestCategoryList();
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
                final String mMessage = response.body().string();

                try {
                    JSONArray jsonArray = new JSONArray(mMessage);
                    int lenght = jsonArray.length();
                    String feedName = "";
                    String[] feedID = new String[lenght];
                    for (int i = 0; i < lenght; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        feedName = jsonObject.getString("name");
                        feedID[i] = jsonObject.getString("id");
                        groupId.add(jsonObject.getString("id"));
                        groupItem.add(feedName);
                    }
                    requestSubCategoryList();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    int por = 0;

    public void requestSubCategoryList() {

        String url = "https://orzu.org/api?%20appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&lang=ru&opt=view_cat&cat_id=only_subcat&id=" + groupId.get(por);
        por++;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        int finalI = por;
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                CategorySubscriptions.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(CategorySubscriptions.this, android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestSubCategoryList();
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
                final String mMessage = response.body().string();

                try {
                    Map<String, Object> msub;
                    JSONArray jsonArray = new JSONArray(mMessage);
                    int lenght = jsonArray.length();
                    String feedName = "";
                    String feedID = "";
                    String parentId = "";
                    ArrayList<SubItem> child = new ArrayList<SubItem>();
                    for (int i = 0; i < lenght; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        feedName = jsonObject.getString("name");
                        feedID = jsonObject.getString("id");
                        parentId = jsonObject.getString("parent_id");
                        child.add(new SubItem(feedName, parentId, feedID));
                        if (model.arraySubs.contains(feedID)) {
                            child.get(i).setCheck(true);
                            model.mapa.put(parentId + ";" + feedID, parentId + ";" + feedID);
                        }
                    }
                    childItem.add(child);

                    if (por != groupId.size()) {

                        requestSubCategoryList();


                    } else {
                        CategorySubscriptions.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                shim.setVisibility(View.INVISIBLE);
                                podstilka.setVisibility(View.INVISIBLE);
                            }
                        });

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mNewAdapter.notifyDataSetChanged();
                    }
                });

            }
        });

    }

    public void requestSubsServer() {
        dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("orzutable", null, null, null, null, null, null);
        c.moveToFirst();
        int idColIndex = c.getColumnIndex("id");
        int tokenColIndex = c.getColumnIndex("token");
        idUser = c.getString(idColIndex);
        tokenUser = c.getString(tokenColIndex);
        c.close();
        db.close();
        String url = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=view_user&user_cat=" + idUser;
        subsServer = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                CategorySubscriptions.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(CategorySubscriptions.this, android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestSubsServer();
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
                final String mMessage = response.body().string();

                try {
                    JSONObject jsonObject = new JSONObject(mMessage);
                    Iterator<String> iter = jsonObject.keys();
                    int i = 0;
                    while (iter.hasNext()) {
                        String key = iter.next();
                        try {
                            Object value = jsonObject.get(key);
                            subsServer.add(key);
                        } catch (JSONException e) {
                        }
                        i++;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                model.arraySubs = subsServer;
                requestCategoryList();

            }
        });

    }

    public void requestSubsServerAdd() {
        pb.setVisibility(View.VISIBLE);
        String modelString = "";

        if (model.mapa.isEmpty()) {
            modelString = "&cat[]=";
        }

        for (Map.Entry<String, String> entry : model.mapa.entrySet()) {
            modelString = modelString + "&cat[]=" + entry.getKey();
        }

        final char dm = (char) 34;

        String url = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=user_param" +
                "&userid=" + idUser +
                "&utoken=" + tokenUser +
                "&act=subscribe" + modelString;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                CategorySubscriptions.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(CategorySubscriptions.this, android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestSubsServerAdd();
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
                final String mMessage = response.body().string();
                if (mMessage.equals(dm + "Success" + dm)) {
                    CategorySubscriptions.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pb.setVisibility(View.INVISIBLE);
                            Toast.makeText(CategorySubscriptions.this, "Успешно Сохранено", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        requestSubsServerAdd();
    }
}
