package orzu.org;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Fragment3 extends Fragment implements View.OnClickListener {

    String idUser;
    String text;
    String mMessage;
    private String mName;
    private String mFiName;
    private String mCount;
    private String mCountReq;
    private String mCity;
    private String mBday;
    private String mSex;
    private String mNarr;
    private TextView nameUser;
    private TextView noReviews;
    private TextView taskCountReq;
    private TextView taskCount;
    private TextView userCity;
    private TextView userBday;
    private TextView userSex;
    private TextView userNarr;
    private TextView feedbackname1;
    private TextView feedbackplus1;
    private TextView feedbacknarr1;
    private TextView feedbackcat1;
    private ImageView feedbackimg1;
    private ImageView feedbackimgUser1;
    private TextView feedbackname2;
    private TextView feedbackplus2;
    private TextView feedbacknarr2;
    private TextView feedbackcat2;
    private ImageView feedbackimg2;
    private ImageView feedbackimgUser2;
    private View devider;
    private ShimmerFrameLayout shim;
    private ImageView image_back;
    private String mStatus;
    private String image1;
    private String image2;
    private ImageView statusImg;
    private ImageView imageViewName;
    private ArrayList<String> returnValue = new ArrayList<>();
    TextView wallet;
    String wallet_of_str;
    LinearLayout feedbackButtun;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstantState) {
        final View view = inflater.inflate(R.layout.fragment_main_3, container, false);
        final SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences(" ", Context.MODE_PRIVATE);
        idUser = prefs.getString(Util.TASK_USERID, "");
        String my = prefs.getString(Util.TASK_USERIDMY, "");
        DBHelper dbHelper = new DBHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("orzutable", null, null, null, null, null, null);
        c.moveToFirst();
        int idColIndex = c.getColumnIndex("id");
        int tokenColIndex = c.getColumnIndex("token");
        idUser = c.getString(idColIndex);
        String tokenUser = c.getString(tokenColIndex);
        c.moveToFirst();
        c.close();
        db.close();
        feedbackButtun= view.findViewById(R.id.linear_feed_click);
        feedbackButtun.setOnClickListener(this);
        nameUser = view.findViewById(R.id.name_fname);
        feedbackname1 = view.findViewById(R.id.userview_feedbacknamemy);
        feedbackplus1 = view.findViewById(R.id.userview_feedbackplusmy);
        feedbacknarr1 = view.findViewById(R.id.userview_feedbacknarrmy);
        feedbackcat1 = view.findViewById(R.id.userview_feedbackcatmy);
        feedbackimg1 = view.findViewById(R.id.userview_feedbackplus_imagemy);
        feedbackimgUser1 = view.findViewById(R.id.imageViewOtziv1my);
        feedbackname2 = view.findViewById(R.id.userview_feedbackname2my);
        feedbackplus2 = view.findViewById(R.id.userview_feedbackplus2my);
        feedbacknarr2 = view.findViewById(R.id.userview_feedbacknarr2my);
        feedbackcat2 = view.findViewById(R.id.userview_feedbackcat2my);
        feedbackimg2 = view.findViewById(R.id.userview_feedbackplus_image2my);
        feedbackimgUser2 = view.findViewById(R.id.imageViewOtziv2my);
        noReviews = view.findViewById(R.id.no_reviewsmy);
        devider = view.findViewById(R.id.divider_viewmy);
        image_back = view.findViewById(R.id.img_back);
        wallet = view.findViewById(R.id.wallet);
        shim = (ShimmerFrameLayout) view.findViewById(R.id.userviewShimmerLayoutmy);
        shim.startShimmer();
        CardView cardView = view.findViewById(R.id.card_of_user_view);
        cardView.setBackgroundResource(R.drawable.shape_card_topcorners);
        CardView cardView_shim = view.findViewById(R.id.card_of_user_view_activity_shim);
        cardView_shim.setBackgroundResource(R.drawable.shape_card_topcorners);
        taskCountReq = view.findViewById(R.id.viewuser_task_count_reqmy);
        taskCount = view.findViewById(R.id.viewuser_task_countmy);
        userCity = view.findViewById(R.id.viewuser_citymy);
        userBday = view.findViewById(R.id.userview_bdaymy);
        userSex = view.findViewById(R.id.userview_sexmy);
        userNarr = view.findViewById(R.id.viewuser_narrativemy);
        statusImg = view.findViewById(R.id.imageViewStatusMy);
        imageViewName = view.findViewById(R.id.imageViewName);
        imageViewName.setImageBitmap(Common.bitmap);
        TextView subscribe = view.findViewById(R.id.subscribe);
        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(getActivity(), CategorySubscriptions.class);
                startActivity(intent2);
            }
        });
        imageViewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pix.start(getActivity(), Options.init().setRequestCode(100));
            }
        });
        try {
            getUserResponse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        requestFeedbackMy();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            assert data != null;
            returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            assert returnValue != null;
            imageViewName.setImageURI(Uri.parse(returnValue.get(0)));
            Common.d = imageViewName.getDrawable();
            ((Main2Activity) Objects.requireNonNull(getActivity())).changeImage();
            try {
                getEditAvatarResponse();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void getEditAvatarResponse() throws IOException {
        String url = "https://orzu.org/api/avatar";
        OkHttpClient client = new OkHttpClient();
        File myFile = new File(Objects.requireNonNull(Uri.parse(returnValue.get(0)).getPath()));
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", myFile.getName(),
                        RequestBody.create(MediaType.parse("text/csv"), myFile))
                .addFormDataPart("userid", idUser)
                .addFormDataPart("utoken", Common.utoken)
                .addFormDataPart("appid", "$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                requireActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(Objects.requireNonNull(getContext()), android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    getEditAvatarResponse();
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
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (getActivity() == null)
                    return;
                requireActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getContext(), "Ваш профиль изменен", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void getUserResponse() throws IOException {
        String url = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&lang=ru&opt=view_user&user=" + idUser + "&param=more";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                requireActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    getUserResponse();
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
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                mMessage = Objects.requireNonNull(response.body()).string();
                try {
                    JSONObject jsonObject = new JSONObject(mMessage);
                    mName = jsonObject.getString("name");
                    mFiName = jsonObject.getString("fname");
                    mCount = jsonObject.getString("task_requests");
                    mCountReq = jsonObject.getString("tasks");
                    mCity = jsonObject.getString("city");
                    Common.city = mCity;
                    mBday = jsonObject.getString("birthday");
                    Common.birth = mBday;
                    mSex = jsonObject.getString("sex");
                    Common.sex = mSex;
                    mNarr = jsonObject.getString("about");
                    Common.about = mNarr;
                    mStatus = jsonObject.getString("status");
                    wallet_of_str = jsonObject.getString("wallet") + " TJS";
                    Common.wallet = jsonObject.getString("wallet");
                    if (mSex.equals("male")) {
                        mSex = "мужской";
                    } else mSex = "женский";
                    if (mFiName.equals("null")) {
                        text = mName;
                    } else text = mName + " " + mFiName;
                    if (mStatus.equals("false")) {

                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                statusImg.setVisibility(View.INVISIBLE);
                            }
                        });
                    } else {
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                statusImg.setVisibility(View.VISIBLE);
                            }
                        });

                    }
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            wallet.setText(wallet_of_str);
                            nameUser.setText(text);
                            taskCountReq.setText(mCountReq);
                            taskCount.setText(mCount);
                            userCity.setText(mCity);
                            userBday.setText(mBday);
                            userSex.setText(mSex);
                            userNarr.setText(mNarr);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.user_menu, menu);
        Drawable drawable = menu.findItem(R.id.share_item).getIcon();
        Drawable drawable2 = menu.findItem(R.id.settings_item).getIcon();
       // Drawable drawable3 = menu.findItem(R.id.item_subscribe).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        drawable2 = DrawableCompat.wrap(drawable2);
        //drawable3 = DrawableCompat.wrap(drawable3);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.colorBackgrndFrg));
        DrawableCompat.setTint(drawable2, ContextCompat.getColor(getActivity(), R.color.colorBackgrndFrg));
       // DrawableCompat.setTint(drawable3, ContextCompat.getColor(getActivity(), R.color.colorBackgrndFrg));
        menu.findItem(R.id.share_item).setIcon(drawable);
        menu.findItem(R.id.settings_item).setIcon(drawable2);
        //menu.findItem(R.id.item_subscribe).setIcon(drawable3);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_item:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Привет, посмотри мой профиль на Orzu: https://orzu.org/profile/" + idUser);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;
            case R.id.settings_item:
                Intent intent = new Intent(getActivity(), UserSettings.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), Feedback.class);
        intent.putExtra("idUserFeedback", idUser);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        imageViewName.setImageDrawable(Common.d);
        ((Main2Activity) Objects.requireNonNull(getActivity())).changeImage();
        if (Common.sex != null) {
            userCity.setText(Common.city);
            userBday.setText(Common.birth);
            userNarr.setText(Common.about);
            userSex.setText(Common.sex);
            nameUser.setText(Common.name);
        }
    }

    private void requestFeedbackMy() {
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
            public void onFailure(@NotNull Call call, IOException e) {
                requireActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(Objects.requireNonNull(getContext()), android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestFeedbackMy();
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
                if (mMessage.equals(Character.toString((char) dchar) + "No reviews yet" + Character.toString((char) dchar))) {
                    requireActivity().runOnUiThread(new Runnable() {
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
                            image_back.setVisibility(View.INVISIBLE);
                            noReviews.setVisibility(View.VISIBLE);
                            feedbackButtun.setVisibility(View.INVISIBLE);

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
                            requireActivity().runOnUiThread(new Runnable() {
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
                                    requireActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            feedbackplus1.setText("-1");
                                            feedbackimg1.setImageResource(bpSad);
                                        }
                                    });
                                } else if (like == 1L) {
                                    requireActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            feedbackplus1.setText("0");
                                            feedbackimg1.setImageResource(bpNorm);
                                        }
                                    });
                                } else if (like == 2L) {
                                    requireActivity().runOnUiThread(new Runnable() {
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
                                requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        feedbackname1.setText(name[0]);
                                        feedbacknarr1.setText(narr[0]);
                                        feedbackcat1.setText(date[0]);
                                        Picasso.get().load("https://orzu.org" + image1.replaceAll("\\\\", "")).into(feedbackimgUser1);
                                    }
                                });
                            } else {
                                long like = jsonObject.getLong("like");
                                if (like == 0L) {
                                    requireActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            feedbackplus2.setText("-1");
                                            feedbackimg2.setImageResource(bpSad);
                                        }
                                    });
                                } else if (like == 1L) {
                                    requireActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            feedbackplus2.setText("0");
                                            feedbackimg2.setImageResource(bpNorm);
                                        }
                                    });
                                } else if (like == 2L) {
                                    requireActivity().runOnUiThread(new Runnable() {
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
                                requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        feedbackname2.setText(name[1]);
                                        feedbacknarr2.setText(narr[1]);
                                        feedbackcat2.setText(date[1]);
                                        Picasso.get().load("https://orzu.org" + image2.replaceAll("\\\\", "")).into(feedbackimgUser2);
                                    }
                                });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            shim.setVisibility(View.INVISIBLE);
                            image_back.setVisibility(View.INVISIBLE);
                        }
                    });

                }
            }
        });
    }
}

