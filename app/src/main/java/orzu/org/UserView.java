package orzu.org;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserView extends AppCompatActivity implements View.OnClickListener {
    String idUser;
    String text;
    String mMessage;
    String mName;
    String mFiName;
    String mCount;
    String mCountReq;
    String mCity;
    String mBday;
    String mSex;
    String mNarr;
    String mStatus;
    String image;
    String image1;
    String image2;
    TextView nameUser;
    TextView noReviews;
    TextView taskCountReq;
    TextView taskCount;
    TextView userCity;
    TextView userBday;
    TextView userSex;
    TextView userNarr;
    LinearLayout feedbackButtun;
    LinearLayout feedbackButtunAdd;
    TextView feedbackname1;
    TextView feedbackplus1;
    TextView feedbacknarr1;
    TextView feedbackcat1;
    ImageView feedbackimg1;
    ImageView feedbackimgUser1;
    TextView feedbackname2;
    TextView feedbackplus2;
    TextView feedbacknarr2;
    TextView feedbackcat2;
    ImageView feedbackimg2;
    ImageView feedbackimgUser2;
    ImageView imageViewName;
    ShimmerFrameLayout shim;
    View devider;
    ImageView imageView;
    ImageView statusImg, back;
    CardView cardView;
    CardView cardView_shim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryPurpleTop));
        setContentView(R.layout.activity_user_view);
        nameUser = findViewById(R.id.name_fname_his);
        idUser = getIntent().getExtras().getString("idhis");

        feedbackButtun = findViewById(R.id.linear_feedback_click);
        feedbackButtunAdd = findViewById(R.id.linear_feedback_addto);
        feedbackButtun.setOnClickListener(this);
        feedbackButtunAdd.setOnClickListener(this);
        cardView = findViewById(R.id.card_of_user_view_activity);
        cardView.setBackgroundResource(R.drawable.shape_card_topcorners);
        cardView_shim = findViewById(R.id.card_of_user_view_activity_shim);
        cardView_shim.setBackgroundResource(R.drawable.shape_card_topcorners);
        back = findViewById(R.id.view_activity_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if(idUser.equals(Common.userId)){
            feedbackButtunAdd.setVisibility(View.GONE);
        }
        imageView = findViewById(R.id.img);
        feedbackname1 = findViewById(R.id.userview_feedbackname);
        feedbackplus1 = findViewById(R.id.userview_feedbackplus);
        feedbacknarr1 = findViewById(R.id.userview_feedbacknarr);
        feedbackcat1 = findViewById(R.id.userview_feedbackcat);
        feedbackimg1 = findViewById(R.id.userview_feedbackplus_image);
        feedbackimgUser1 = findViewById(R.id.imageViewOtziv1);
        feedbackname2 = findViewById(R.id.userview_feedbackname2);
        feedbackplus2 = findViewById(R.id.userview_feedbackplus2);
        feedbacknarr2 = findViewById(R.id.userview_feedbacknarr2);
        feedbackcat2 = findViewById(R.id.userview_feedbackcat2);
        feedbackimg2 = findViewById(R.id.userview_feedbackplus_image2);
        feedbackimgUser2 = findViewById(R.id.imageViewOtziv2);
        noReviews = findViewById(R.id.no_reviews);
        devider = findViewById(R.id.divider_view);
        shim = (ShimmerFrameLayout) findViewById(R.id.userviewShimmerLayout);
        shim.startShimmer();
        taskCountReq = findViewById(R.id.viewuser_task_count_req);
        taskCount = findViewById(R.id.viewuser_task_count);
        userCity = findViewById(R.id.viewuser_city);
        userBday = findViewById(R.id.userview_bday);
        userSex = findViewById(R.id.userview_sex);
        userNarr = findViewById(R.id.viewuser_narrative);
        statusImg = findViewById(R.id.imageViewStatusMy);
        imageViewName = findViewById(R.id.imageViewName);
        try {
            getUserResponseView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_menu_his, menu);
        Drawable drawable = menu.findItem(R.id.share_item_his).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.colorBackgrndFrg));
        menu.findItem(R.id.share_item_his).setIcon(drawable);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_item_his:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Привет, посмотри мой профиль на Orzu: https://orzu.org/profile/" + idUser);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void getUserResponseView() throws IOException {
        String url = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&lang=ru&opt=view_user&user=" + idUser + "&param=more";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UserView.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(UserView.this, android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    getUserResponseView();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
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
                mMessage = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(mMessage);
                    mName = jsonObject.getString("name");
                    mFiName = jsonObject.getString("fname");
                    mCount = jsonObject.getString("tasks");
                    mCountReq = jsonObject.getString("task_requests");
                    mCity = jsonObject.getString("city");
                    mBday = jsonObject.getString("birthday");
                    mSex = jsonObject.getString("sex");
                    mNarr = jsonObject.getString("about");
                    mStatus = jsonObject.getString("status");
                    image = jsonObject.getString("avatar");
                    if (mSex.equals("male")) {
                        mSex = "мужской";
                    } else mSex = "женский";
                    if (mFiName.equals("null")) {
                        text = mName;
                    } else text = mName + " " + mFiName;

                    if (mStatus.equals("false")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                statusImg.setVisibility(View.INVISIBLE);
                            }
                        });

                    } else statusImg.setVisibility(View.VISIBLE);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            nameUser.setText(text);
                            taskCountReq.setText(mCount);
                            taskCount.setText(mCountReq);
                            userCity.setText(mCity);
                            userBday.setText(mBday);
                            userSex.setText(mSex);
                            userNarr.setText(mNarr);
                            Picasso.get().load("https://orzu.org" + image).fit().centerCrop().into(imageViewName);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                requestFeedback();
            }
        });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linear_feedback_click:
                Intent intent = new Intent(this, Feedback.class);
                intent.putExtra("idUserFeedback", idUser);
                intent.putExtra("nameUserFeedbackto", text);
                startActivity(intent);
                break;
            case R.id.linear_feedback_addto:
                Intent intent2 = new Intent(this, AddFeedback.class);
                intent2.putExtra("idUserFeedbackto", idUser);
                intent2.putExtra("nameUserFeedbackto", text);
                startActivity(intent2);
                finish();
                break;
        }
    }
    public void requestFeedback() {
        String url = "https://orzu.org/api?%20appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=reviews&act=view&userid=" + idUser + "&sort=all";
        OkHttpClient client = new OkHttpClient();
        final String[] name = new String[2];
        final String[] narr = new String[2];
        final String[] date = new String[2];
        int dchar = 34;
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UserView.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(UserView.this, android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestFeedback();
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
                if (mMessage.equals(Character.toString((char) dchar) + "No reviews yet" + Character.toString((char) dchar))) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            feedbackname1.setVisibility(View.GONE);
                            feedbacknarr1.setVisibility(View.GONE);
                            feedbackcat1.setVisibility(View.GONE);
                            feedbackplus1.setVisibility(View.GONE);
                            feedbackimg1.setVisibility(View.GONE);
                            feedbackimgUser1.setVisibility(View.GONE);
                            feedbackname2.setVisibility(View.GONE);
                            feedbacknarr2.setVisibility(View.GONE);
                            feedbackcat2.setVisibility(View.GONE);
                            feedbackplus2.setVisibility(View.GONE);
                            feedbackimg2.setVisibility(View.GONE);
                            feedbackimgUser2.setVisibility(View.GONE);
                            devider.setVisibility(View.GONE);
                            shim.setVisibility(View.INVISIBLE);
                            imageView.setVisibility(View.INVISIBLE);
                            noReviews.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    try {
                        JSONArray jsonArray = new JSONArray(mMessage);
                        int bpSad = R.drawable.ic_bad;
                        int bpNorm = R.drawable.ic_neutral2;
                        int bpHappy = R.drawable.ic_happy2;
                        int lenght = jsonArray.length();
                        if (lenght > 1) {
                            lenght = 2;
                        } else if (lenght == 1) {
                            lenght = 1;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    feedbackname2.setVisibility(View.GONE);
                                    feedbacknarr2.setVisibility(View.GONE);
                                    feedbackcat2.setVisibility(View.GONE);
                                    feedbackplus2.setVisibility(View.GONE);
                                    feedbackimg2.setVisibility(View.GONE);
                                    feedbackimgUser2.setVisibility(View.GONE);
                                    devider.setVisibility(View.GONE);
                                }
                            });
                        } else {
                            lenght = 0;
                        }
                        for (int i = 0; i < lenght; i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (i == 0) {
                                long like = jsonObject.getLong("like");
                                if (like == 0L) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            feedbackplus1.setText("-1");
                                            feedbackimg1.setImageResource(bpSad);
                                        }
                                    });
                                } else if (like == 1L) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            feedbackplus1.setText("0");
                                            feedbackimg1.setImageResource(bpNorm);
                                        }
                                    });
                                } else if (like == 2L) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            feedbackplus1.setText("+1");
                                            feedbackimg1.setImageResource(bpHappy);
                                        }
                                    });
                                }
                                name[0] = jsonObject.getString("username");
                                narr[0] = jsonObject.getString("narrative");
                                date[0] = jsonObject.getString("datein");
                                image1 = jsonObject.getString("avatar");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        feedbackname1.setText(name[0]);
                                        feedbacknarr1.setText(narr[0]);
                                        feedbackcat1.setText(date[0]);
                                        Picasso.get().load("https://orzu.org" + image1).into(feedbackimgUser1);
                                    }
                                });
                            } else {
                                long like = jsonObject.getLong("like");
                                if (like == 0L) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            feedbackplus2.setText("-1");
                                            feedbackimg2.setImageResource(bpSad);
                                        }
                                    });
                                } else if (like == 1L) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            feedbackplus2.setText("0");
                                            feedbackimg2.setImageResource(bpNorm);
                                        }
                                    });
                                } else if (like == 2L) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            feedbackplus2.setText("+1");
                                            feedbackimg2.setImageResource(bpHappy);
                                        }
                                    });
                                }
                                name[1] = jsonObject.getString("username");
                                narr[1] = jsonObject.getString("narrative");
                                date[1] = jsonObject.getString("datein");
                                image2 = jsonObject.getString("avatar");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        feedbackname2.setText(name[1]);
                                        feedbacknarr2.setText(narr[1]);
                                        feedbackcat2.setText(date[1]);
                                        Picasso.get().load("https://orzu.org" + image2).into(feedbackimgUser2);
                                    }
                                });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            shim.setVisibility(View.INVISIBLE);
                            imageView.setVisibility(View.INVISIBLE);
                        }
                    });

                }
            }
        });
    }
}
