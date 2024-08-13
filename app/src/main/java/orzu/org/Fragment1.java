package orzu.org;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import orzu.org.models.MyTaskApi;
import orzu.org.models.NetworkService;
import orzu.org.ui.login.model;
import retrofit2.Retrofit;


public class Fragment1 extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    String filter = "";
    ArrayList<Map<String, Object>> data;
    ArrayList<Map<String, Object>> truedata;
    private RecyclerView rv;
    private RecyclerView category_rv;
    private RecyclerView subcategory_rv;
    MainCategoryAdapter adapter_category;
    MainSubCategoryAdapter adapter_subcategory;
    RVAdapter adapter;
    Map<String, Object> m = new HashMap<>();
    Map<String, Object> m_new_2 = new HashMap<>();
    final String categoryList = "Категория задачи";
    final String taskList = "Задание";
    private String idList = "ID";
    private String catidList = "CatID";
    private final String priceList = "Цена";
    private final String servList = "Валюта";
    private final String cityList = "Город";
    private String needListdfrom = "Сроки";
    private int count;
    private int countItem;
    private boolean imageClicked = false;
    private boolean clickedCategory = false;
    private SwipeRefreshLayout swipeLayout;
    private AsyncOrzuTasksMain catTask;
    private AsyncOrzuTasksMainRefresh catTaskRef;
    private AsyncOrzuTasksGetSubs getFilteredSubs;
    private AsyncOrzuTasksGetSubsFiltered getFilteredSubsFiltered;
    private AsyncOrzuTasksGetCity getFilteredCity;
    private Boolean track = true;
    private Boolean noTasksYet = false;
    private Boolean noTasks = true;
    private Boolean cityChoose = false;
    private AsyncOrzuTasksFind catTaskFind;
    private ImageView imagenotask;
    private TextView textnotask;
    private TextView create_task_main;
    Dialog dialog;
    private int status;
    private ShimmerFrameLayout shim;
    private NestedScrollView nestshimmer;
    private EditText editFind;
    private String edittextFind;
    private String tokenUser;
    private String idOfSub;
    private List<category_model> categories = new ArrayList<>();
    private List<category_model> subcategories = new ArrayList<>();
    private NestedScrollView scroll_of_fragment1;
    private ProgressBar progress_loading;
    private ProgressBar progress_for_task;
    Context con;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    DBHelper dbHelper;
    String idUser;
    SharedPreferences prefs;
    String catCity = "";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstantState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        dbHelper = new DBHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("orzutable", null, null, null, null, null, null);
        c.moveToFirst();
        int idColIndex = c.getColumnIndex("id");
        int tokenColIndex = c.getColumnIndex("token");
        idUser = c.getString(idColIndex);
        tokenUser = c.getString(tokenColIndex);
        c.moveToFirst();
        c.close();
        db.close();
        prefs = getActivity().getSharedPreferences(" ", Context.MODE_PRIVATE);
        Common.city = prefs.getString("UserCityPref", "");
        if (Common.city1.length() > 0) {
            catCity = Common.city1;
        } else {
            catCity = Common.city;
        }
        nestshimmer = view.findViewById(R.id.nestshimmer);
        shim = (ShimmerFrameLayout) view.findViewById(R.id.parentShimmerLayout);
        shim.startShimmer();
        imagenotask = view.findViewById(R.id.imageNoTask);
        textnotask = view.findViewById(R.id.textViewNoTask);
        category_rv = view.findViewById(R.id.category_main_rv);
        subcategory_rv = view.findViewById(R.id.subcategory_main_rv);
        progress_loading = view.findViewById(R.id.progress_loading);
        progress_for_task = view.findViewById(R.id.progress_for_task);
        create_task_main = view.findViewById(R.id.create_task_main);
        editFind = view.findViewById(R.id.editFind);
        if (Common.referrer.length() > 0 && !Common.referrer.contains("google")) {
            plusBalance();
            Toast.makeText(getContext(), "Бонусы будут защитаны: " + Common.referrer, Toast.LENGTH_SHORT).show();
        }
        if (prefs.getString("UserCityPref", "").equals("")) {
            Intent intent = new Intent(getActivity(), RegistCity.class);
            startActivity(intent);
            getActivity().finish();
        }
        if (!Common.allCity) {
            Common.URL = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=view_task&tasks=all&city=" + prefs.getString("UserCityPref", "") + "&page=";
            getCategories();
            getSubCategories("1");
        } else {
            Common.city1 = "";
            Common.URL = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=view_task&tasks=all&requests=no&page=";
            getCategories();
            getSubCategories("1");
        }
        Set<String> keys = Common.subFilter.keySet();
        for (String key : keys) {
            Set<Integer> key_of_value = Common.subFilter.get(key).keySet();
            for (Integer keyV : key_of_value) {
                filter = filter + "catid[]=" + Common.subFilter.get(key).get(keyV) + "&";
            }
        }

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager_sub
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        category_rv.setLayoutManager(layoutManager);
        subcategory_rv.setLayoutManager(layoutManager_sub);

        progress_for_task.setVisibility(View.VISIBLE);

        editFind.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                noTasksYet = false;
                edittextFind = String.valueOf(editFind.getText());
                catTaskFind = new AsyncOrzuTasksFind();
                catTaskFind.execute();
            }
        });
        scroll_of_fragment1 = view.findViewById(R.id.scroll_of_fragment1);

        rv = view.findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        rv.setNestedScrollingEnabled(false);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.nestedScrollView);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryLight),
                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorPrimary));
        truedata = new ArrayList<>();
        count = 1;
        countItem = 1;
        create_task_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateTaskCategory.class);
                startActivity(intent);
            }
        });
        rv.setNestedScrollingEnabled(false);
        scroll_of_fragment1.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if (!noTasksYet) {
                    View view1 = (View) scroll_of_fragment1.getChildAt(scroll_of_fragment1.getChildCount() - 1);
                    progress_loading.setVisibility(View.VISIBLE);
                    int diff = (view1.getBottom() - (scroll_of_fragment1.getHeight() + scroll_of_fragment1
                            .getScrollY()));
                    if (diff == 0) {
                        swipeLayout.setEnabled(llm.findFirstCompletelyVisibleItemPosition() == 0 || adapter.getItemCount() == 0);
                        if (filter.length() > 8) {
                            getFilteredSubsFiltered = new AsyncOrzuTasksGetSubsFiltered();
                            getFilteredSubsFiltered.execute();
                        } else if (cityChoose) {
                            getFilteredCity = new AsyncOrzuTasksGetCity();
                            getFilteredCity.execute();
                        } else if (imageClicked) {
                            getFilteredSubs = new AsyncOrzuTasksGetSubs();
                            getFilteredSubs.execute();
                        } else {
                            catTask = new AsyncOrzuTasksMain();
                            catTask.execute();
                        }
                    }
                }
            }
        });
        categories.add(new category_model("0", "Все категорий", "0"));
        adapter_category = new MainCategoryAdapter(getContext(), categories);
        category_rv.setAdapter(adapter_category);
        adapter_subcategory = new MainSubCategoryAdapter(getContext(), subcategories);
        subcategory_rv.setAdapter(adapter_subcategory);
        MainSubCategoryAdapter.setSelect(new NameItemSelect() {
            @Override
            public void onItemSelectedListener(View view, int position) {
                imageClicked = true;
                noTasksYet = false;
                cityChoose = false;
                Long[] idArray = new Long[1];
                idArray[0] = 0L;
                model.array = idArray;
                Common.subFilter = new HashMap<>();
                filter = "";
                if (Common.city1.length() > 0) {
                    catCity = Common.city1;
                } else {
                    catCity = Common.city;
                }
                //Common.city1 = "";
                count = 1;
                truedata.clear();
                progress_for_task.setVisibility(View.VISIBLE);
                idOfSub = subcategories.get(position).getId();
                getFilteredSubs = new AsyncOrzuTasksGetSubs();
                getFilteredSubs.execute();
            }

            @Override
            public void onClick(View view) {
            }
        });
        MainCategoryAdapter.setSelect(new NameItemSelect() {
            @Override
            public void onItemSelectedListener(View view, int position) {
                filter = "";
                if (position == 0) {
                    noTasksYet = false;
                    progress_for_task.setVisibility(View.VISIBLE);
                    swipeLayout.setRefreshing(false);
                    catTaskRef = new AsyncOrzuTasksMainRefresh();
                    catTaskRef.execute();
                    imageClicked = false;
                    clickedCategory = false;
                } else {
                    clickedCategory = true;
                    progress_for_task.setVisibility(View.VISIBLE);
                    truedata.clear();
                    getSubCategories(categories.get(position).getId());

                }
                adapter_category.changeColor(position);
            }

            @Override
            public void onClick(View view) {

            }
        });
        return view;
    }

    private void plusBalance() {
        String requestUrl = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=user_param&act=edit_bonus_plus&userid=" + idUser + "&utoken=" + tokenUser + "&useridTo=" + Common.referrer;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, requestUrl, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Common.referrer = "";
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace(); //log the error resulting from the request for diagnosis/debugging
                requireActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Material_Light_NoActionBar);
                        dialog.setContentView(R.layout.dialog_no_internet);
                        Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                plusBalance();
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
        });
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    private Map<String, Object> readMessage(JsonReader reader) throws IOException {
        long id = 1;
        String text = null;
        String date = null;
        String update = null;
        String needfrom = null;
        String needto = null;
        String catid = null;
        String subcut = null;
        String price = null;
        Map<String, Object> m = new HashMap<>();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "id":
                    if (reader.peek() != JsonToken.NULL) {
                        id = reader.nextLong();
                        m.put(idList, id);
                    } else reader.skipValue();
                    break;
                case "task":
                    if (reader.peek() != JsonToken.NULL) {
                        text = reader.nextString();
                        m.put(taskList, text);
                    } else reader.skipValue();
                    break;
                case "current":
                    if (reader.peek() != JsonToken.NULL) {
                        text = reader.nextString();
                        m.put(servList, text);
                    } else reader.skipValue();
                    break;
                case "cdate_l":
                    if (reader.peek() != JsonToken.NULL) {
                        date = reader.nextString();
                        m.put(needListdfrom, "До: " + date);
                    } else reader.skipValue();
                    break;
                case "amount":
                    if (reader.peek() != JsonToken.NULL) {
                        price = reader.nextString();
                        if (price.equals("Предложите цену")) {
                            m.put(priceList, price);
                        } else m.put(priceList, price);
                    } else reader.skipValue();
                    break;
                case "sub_cat_name":
                    if (reader.peek() != JsonToken.NULL) {
                        subcut = reader.nextString();
                        m.put(categoryList, subcut);
                    } else reader.skipValue();
                    break;
                case "city":
                    if (reader.peek() != JsonToken.NULL) {
                        update = reader.nextString();
                        m.put(cityList, update);
                    } else reader.skipValue();
                    break;
                case "cdate":
                    if (reader.peek() != JsonToken.NULL) {
                        needfrom = reader.nextString();
                        m.put(needListdfrom, "Начало: " + needfrom);
                    } else reader.skipValue();
                    break;
                case "edate":
                    if (reader.peek() != JsonToken.NULL) {
                        needto = reader.nextString();
                        m.put(needListdfrom, m.get(needListdfrom) + "  Конец: " + needto);
                    } else reader.skipValue();
                    break;
                case "work_with":
                    if (reader.peek() != JsonToken.NULL) {
                        reader.nextString();
                        m.put(needListdfrom, "Дата по договоренности");
                    } else reader.skipValue();
                    break;
                case "sub_cat_id":
                    if (reader.peek() != JsonToken.NULL) {
                        catid = reader.nextString();
                        m.put(catidList, catid);
                    } else reader.skipValue();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return m;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.up_menu, menu);
        Drawable drawable = menu.findItem(R.id.new_game).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(getActivity(), R.color.colorTextDark));
        menu.findItem(R.id.new_game).setIcon(drawable);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.new_game) {
            Intent intent = new Intent(getActivity(), FiltersActivity.class);
            startActivityForResult(intent, 1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class AsyncOrzuTasksMain extends AsyncTask<String, String, ArrayList<Map<String, Object>>> {
        String result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imagenotask.setVisibility(View.INVISIBLE);
            textnotask.setVisibility(View.INVISIBLE);
        }

        @Override
        protected ArrayList<Map<String, Object>> doInBackground(String... strings) {
            URL orzuEndpoint = null;
            JsonReader jsonReader = null;
            HttpsURLConnection myConnection = null;
            HttpsURLConnection myConnectiontrack = null;
            final char dm = (char) 34;
            try {
                if(Common.city1.length()>0){
                    catCity=Common.city1;
                }else{
                    catCity = Common.city;
                }
                Common.URL = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=view_task&tasks=all&city=" + catCity  + "&page=";
                orzuEndpoint = new URL(Common.URL + "" + count);
                myConnectiontrack =
                        (HttpsURLConnection) orzuEndpoint.openConnection();
                if (myConnectiontrack.getResponseCode() == 200) {
                    // Success
                    InputStream responseBody = myConnectiontrack.getInputStream();
                    Scanner s = new Scanner(responseBody).useDelimiter("\\A");
                    result = s.hasNext() ? s.next() : "";
                }
                myConnection =
                        (HttpsURLConnection) orzuEndpoint.openConnection();
                if (myConnection.getResponseCode() == 200) {
                    // Success
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader =
                            new InputStreamReader(responseBody, "UTF-8");
                    jsonReader = new JsonReader(responseBodyReader);
                }
                status = myConnection.getResponseCode();
                myConnection.setInstanceFollowRedirects(true);
                data = new ArrayList<>();
                if (result.equals("\"Not tasks yet\"")) {
                    track = false;
                    if (count != 1) {
                        catTask.cancel(true);
                        requireActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                progress_loading.setVisibility(View.GONE);
                                progress_for_task.setVisibility(View.GONE);
                            }
                        });
                    }
                } else {
                    jsonReader.beginArray(); // Start processing the JSON object
                    while (jsonReader.hasNext()) { // Loop through all keys
                        m = new HashMap<>();
                        m = readMessage(jsonReader);
                        if (!m.isEmpty()) {
                            data.add(m);
                        }
                    }
                    truedata.addAll(data);
                    jsonReader.endArray();
                    jsonReader.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                if (status != 200) {
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                imagenotask.setVisibility(View.VISIBLE);
                                textnotask.setVisibility(View.VISIBLE);
                                dialog = new Dialog(Fragment1.this.getActivity(), android.R.style.Theme_Material_Light_NoActionBar);
                                dialog.setContentView(R.layout.dialog_no_internet);
                                Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                                // if button is clicked, close the custom dialog
                                dialogButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        catTask = new AsyncOrzuTasksMain();
                                        catTask.execute();
                                        dialog.dismiss();
                                        imagenotask.setVisibility(View.INVISIBLE);
                                        textnotask.setVisibility(View.INVISIBLE);
                                    }
                                });
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.show();
                                    }
                                }, 500);
                                catTask.cancel(true);
                            }
                        });
                    }
                }
                e.printStackTrace();
            }
            try {
                myConnection.disconnect();
            } catch (Exception e) {
            }
            return null;
        }

        protected void onPostExecute(ArrayList<Map<String, Object>> result) {
            super.onPostExecute(result);
            count++;
            adapter = new RVAdapter(getContext(), truedata);
            rv.setAdapter(adapter);
            progress_loading.setVisibility(View.GONE);
            progress_for_task.setVisibility(View.GONE);
            adapter.setClickListener(new RVAdapter.ItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(getContext(), TaskViewMain.class);
                    Map<String, Object> map;
                    map = truedata.get(position);
                    intent.putExtra("id", map.get(idList).toString());
                    intent.putExtra("opt", "view");

                    intent.putExtra("mytask", "not");
                    startActivity(intent);
                }
            });
            noTasks = false;
            if (adapter.getItemCount() == 0) {
                noTasks = true;
                imagenotask.setVisibility(View.VISIBLE);
                textnotask.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onRefresh() {
        catCity = Common.city;
        Common.URL = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=view_task&tasks=all&city=" + catCity + "&page=";
        adapter_category.changeColor(0);
        category_rv.scrollToPosition(0);
        subcategory_rv.scrollToPosition(0);
        Long[] idArray = new Long[1];
        idArray[0] = 0L;
        model.array = idArray;
        Common.allCity = false;
        Common.subFilter = new HashMap<>();
        filter = "";
        Common.city1 = "";
        cityChoose = false;
        noTasksYet = false;
        imageClicked = false;
        swipeLayout.setRefreshing(false);
        catTaskRef = new AsyncOrzuTasksMainRefresh();
        catTaskRef.execute();
    }

    class AsyncOrzuTasksMainRefresh extends AsyncTask<String, String, ArrayList<Map<String, Object>>> {
        final HttpsURLConnection[] myConnection = new HttpsURLConnection[1];
        final URL[] orzuEndpoint = new URL[1];
        String result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imagenotask.setVisibility(View.INVISIBLE);
            textnotask.setVisibility(View.INVISIBLE);
        }

        @Override
        protected ArrayList<Map<String, Object>> doInBackground(String... strings) {
            orzuEndpoint[0] = null;
            JsonReader[] jsonReader = new JsonReader[1];
            try {
                orzuEndpoint[0] = new URL("https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&lang=ru&opt=view_task&tasks=all&status=open&requests=no&page=0");
                myConnection[0] =
                        (HttpsURLConnection) orzuEndpoint[0].openConnection();
                if (myConnection[0].getResponseCode() == 200) {
                    // Success
                    InputStream responseBody = myConnection[0].getInputStream();
                    InputStreamReader responseBodyReader =
                            new InputStreamReader(responseBody, "UTF-8");
                    jsonReader[0] = new JsonReader(responseBodyReader);
                }
                status = myConnection[0].getResponseCode();
                myConnection[0].setInstanceFollowRedirects(true);
                data = new ArrayList<>();
                if (jsonReader[0] == null) {
                    track = false;
                } else {
                    m_new_2 = new HashMap<>();
                    jsonReader[0].beginArray();
                    jsonReader[0].beginObject();
                    jsonReader[0].nextName();
                    Long id = jsonReader[0].nextLong();
                    m_new_2.put(idList, id);
                    jsonReader[0].close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                if (status != 200) {
                    requireActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            imagenotask.setVisibility(View.VISIBLE);
                            textnotask.setVisibility(View.VISIBLE);
                            dialog = new Dialog(Fragment1.this.getActivity(), android.R.style.Theme_Material_Light_NoActionBar);
                            dialog.setContentView(R.layout.dialog_no_internet);
                            Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                            dialogButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    catTaskRef = new AsyncOrzuTasksMainRefresh();
                                    catTaskRef.execute();
                                    dialog.dismiss();
                                }
                            });
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.show();
                                }
                            }, 500);
                            catTaskRef.cancel(true);
                        }
                    });
                }
                e.printStackTrace();
            }
            myConnection[0].disconnect();
            return null;
        }

        protected void onPostExecute(ArrayList<Map<String, Object>> result) {
            super.onPostExecute(result);
            try {
                count = 1;
                imagenotask.setVisibility(View.INVISIBLE);
                textnotask.setVisibility(View.INVISIBLE);
                truedata = new ArrayList<>();
                data = new ArrayList<>();
                adapter.notifyDataSetChanged();
                catTask = new AsyncOrzuTasksMain();
                catTask.execute();
            } catch (Exception e) {
                count = 1;
                imagenotask.setVisibility(View.INVISIBLE);
                textnotask.setVisibility(View.INVISIBLE);
                truedata = new ArrayList<>();
                data = new ArrayList<>();
                adapter.notifyDataSetChanged();
                catTask = new AsyncOrzuTasksMain();
                catTask.execute();
            }
        }
    }

    class AsyncOrzuTasksGetSubs extends AsyncTask<String, String, ArrayList<Map<String, Object>>> {
        String result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imagenotask.setVisibility(View.INVISIBLE);
            textnotask.setVisibility(View.INVISIBLE);
        }

        @Override
        protected ArrayList<Map<String, Object>> doInBackground(String... strings) {
            URL orzuEndpoint = null;
            JsonReader jsonReader = null;
            HttpsURLConnection myConnection = null;
            HttpsURLConnection myConnectiontrack = null;
            try {
                orzuEndpoint = new URL("https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=view_task&tasks=all&citycat=" + catCity + "&requestscat=no&catid[]=" + idOfSub + "&page=" + count);
                myConnectiontrack =
                        (HttpsURLConnection) orzuEndpoint.openConnection();
                if (myConnectiontrack.getResponseCode() == 200) {
                    // Success
                    InputStream responseBody = myConnectiontrack.getInputStream();
                    Scanner s = new Scanner(responseBody).useDelimiter("\\A");
                    result = s.hasNext() ? s.next() : "";
                }
                myConnection =
                        (HttpsURLConnection) orzuEndpoint.openConnection();
                if (myConnection.getResponseCode() == 200) {
                    // Success
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader =
                            new InputStreamReader(responseBody, "UTF-8");
                    jsonReader = new JsonReader(responseBodyReader);
                }
                status = myConnection.getResponseCode();
                myConnection.setInstanceFollowRedirects(true);
                data = new ArrayList<>();
                if (result.equals("[[]]")) {
                    noTasksYet = true;
                    track = false;
                    if (count != 1) {
                        getFilteredSubs.cancel(true);
                    }
                    requireActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            progress_loading.setVisibility(View.GONE);
                            progress_for_task.setVisibility(View.GONE);
                        }
                    });
                } else {
                    noTasksYet = false;
                    jsonReader.beginArray();
                    while (jsonReader.hasNext()) {
                        // Start processing the JSON object
                        jsonReader.beginArray();
                        while (jsonReader.hasNext()) { // Loop through all keys
                            m = new HashMap<>();
                            m = readMessage(jsonReader);
                            if (!m.isEmpty()) {
                                data.add(m);
                            }
                        }
                    }
                    truedata.addAll(data);
                    jsonReader.endArray();
                    jsonReader.close();
                }
            } catch (IOException e) {
                if (status != 200) {
                    requireActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            imagenotask.setVisibility(View.VISIBLE);
                            textnotask.setVisibility(View.VISIBLE);
                            dialog = new Dialog(Fragment1.this.getActivity(), android.R.style.Theme_Material_Light_NoActionBar);
                            dialog.setContentView(R.layout.dialog_no_internet);
                            Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                            // if button is clicked, close the custom dialog
                            dialogButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getFilteredSubs = new AsyncOrzuTasksGetSubs();
                                    getFilteredSubs.execute();
                                    dialog.dismiss();
                                }
                            });
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.show();
                                }
                            }, 500);
                            getFilteredSubs.cancel(true);
                        }
                    });
                }
                e.printStackTrace();
            }
            try {
                myConnection.disconnect();
            } catch (Exception e) {
            }
            return null;
        }

        protected void onPostExecute(ArrayList<Map<String, Object>> result) {
            super.onPostExecute(result);
            count++;
            adapter = new RVAdapter(getContext(), truedata);
            rv.setAdapter(adapter);
            progress_loading.setVisibility(View.GONE);
            progress_for_task.setVisibility(View.GONE);
            adapter.setClickListener(new RVAdapter.ItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(getActivity(), TaskViewMain.class);
                    Map<String, Object> map;
                    map = truedata.get(position);
                    intent.putExtra("id", map.get(idList).toString());
                    intent.putExtra("opt", "view");
                    intent.putExtra("mytask", "not");
                    startActivity(intent);
                }
            });
            noTasks = false;
            if (adapter.getItemCount() == 0) {
                noTasks = true;
                imagenotask.setVisibility(View.VISIBLE);
                textnotask.setVisibility(View.VISIBLE);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncOrzuTasksGetSubsFiltered extends AsyncTask<String, String, ArrayList<Map<String, Object>>> {
        String result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imagenotask.setVisibility(View.INVISIBLE);
            textnotask.setVisibility(View.INVISIBLE);
        }

        @Override
        protected ArrayList<Map<String, Object>> doInBackground(String... strings) {
            URL orzuEndpoint = null;
            JsonReader jsonReader = null;
            HttpsURLConnection myConnection = null;
            HttpsURLConnection myConnectiontrack = null;
            try {
                orzuEndpoint = new URL("https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=view_task&tasks=all&requestscat=no&citycat=" + catCity + "&" + filter + "page=" + count);
                myConnectiontrack =
                        (HttpsURLConnection) orzuEndpoint.openConnection();
                if (myConnectiontrack.getResponseCode() == 200) {
                    // Success
                    InputStream responseBody = myConnectiontrack.getInputStream();
                    Scanner s = new Scanner(responseBody).useDelimiter("\\A");
                    result = s.hasNext() ? s.next() : "";
                }
                myConnection =
                        (HttpsURLConnection) orzuEndpoint.openConnection();
                if (myConnection.getResponseCode() == 200) {
                    // Success
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader =
                            new InputStreamReader(responseBody, "UTF-8");
                    jsonReader = new JsonReader(responseBodyReader);
                }
                status = myConnection.getResponseCode();
                myConnection.setInstanceFollowRedirects(true);
                data = new ArrayList<>();
                if (result.equals("[[]]")) {
                    noTasksYet = true;
                    track = false;
                    if (count != 1) {
                        getFilteredSubsFiltered.cancel(true);
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            progress_loading.setVisibility(View.GONE);
                            progress_for_task.setVisibility(View.GONE);
                        }
                    });
                } else {
                    noTasksYet = false;
                    jsonReader.beginArray();
                    while (jsonReader.hasNext()) {
                        if (JsonToken.BEGIN_ARRAY.equals(jsonReader.peek())) {
                            jsonReader.beginArray();
                            while (jsonReader.hasNext()) {
                                m = new HashMap<>();
                                m = readMessage(jsonReader);
                                if (!m.isEmpty()) {
                                    data.add(m);
                                }
                            }
                            jsonReader.endArray();
                        }
                    }
                    truedata.addAll(data);
                    jsonReader.endArray();
                    jsonReader.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                if (status != 200) {
                    requireActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            imagenotask.setVisibility(View.VISIBLE);
                            textnotask.setVisibility(View.VISIBLE);
                            dialog = new Dialog(Fragment1.this.getActivity(), android.R.style.Theme_Material_Light_NoActionBar);
                            dialog.setContentView(R.layout.dialog_no_internet);
                            Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                            // if button is clicked, close the custom dialog
                            dialogButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getFilteredSubsFiltered = new AsyncOrzuTasksGetSubsFiltered();
                                    getFilteredSubsFiltered.execute();
                                    dialog.dismiss();
                                }
                            });
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.show();
                                }
                            }, 500);
                            getFilteredSubsFiltered.cancel(true);
                        }
                    });
                }
                e.printStackTrace();
            }
            try {
                myConnection.disconnect();
            } catch (Exception e) {
            }
            return null;
        }

        protected void onPostExecute(ArrayList<Map<String, Object>> result) {
            super.onPostExecute(result);
            count++;
            adapter = new RVAdapter(getContext(), truedata);
            rv.setAdapter(adapter);
            progress_loading.setVisibility(View.GONE);
            progress_for_task.setVisibility(View.GONE);
            adapter.setClickListener(new RVAdapter.ItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(getContext(), TaskViewMain.class);
                    Map<String, Object> map;
                    map = truedata.get(position);
                    intent.putExtra("id", map.get(idList).toString());
                    intent.putExtra("opt", "view");
                    intent.putExtra("mytask", "not");
                    startActivity(intent);
                }
            });
            noTasks = false;
            if (adapter.getItemCount() == 0) {
                noTasks = true;
                imagenotask.setVisibility(View.VISIBLE);
                textnotask.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (catTask != null) {
            catTask.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.con = context;
    }

    class AsyncOrzuTasksGetCity extends AsyncTask<String, String, ArrayList<Map<String, Object>>> {
        String result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imagenotask.setVisibility(View.INVISIBLE);
            textnotask.setVisibility(View.INVISIBLE);
        }

        @Override
        protected ArrayList<Map<String, Object>> doInBackground(String... strings) {
            URL orzuEndpoint = null;
            JsonReader jsonReader = null;
            HttpsURLConnection myConnection = null;
            HttpsURLConnection myConnectiontrack = null;
            try {
                orzuEndpoint = new URL("https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=view_task&tasks=all&city=" + catCity + "&page=" + count);
                myConnectiontrack =
                        (HttpsURLConnection) orzuEndpoint.openConnection();
                if (myConnectiontrack.getResponseCode() == 200) {
                    // Success
                    InputStream responseBody = myConnectiontrack.getInputStream();
                    Scanner s = new Scanner(responseBody).useDelimiter("\\A");
                    result = s.hasNext() ? s.next() : "";
                }
                myConnection =
                        (HttpsURLConnection) orzuEndpoint.openConnection();
                if (myConnection.getResponseCode() == 200) {
                    // Success
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader =
                            new InputStreamReader(responseBody, "UTF-8");
                    jsonReader = new JsonReader(responseBodyReader);
                }
                status = myConnection.getResponseCode();
                myConnection.setInstanceFollowRedirects(true);
                data = new ArrayList<>();
                if (result.equals("\"Not tasks yet\"")) {
                    noTasksYet = true;
                    track = false;
                    if (count != 1) {
                        getFilteredCity.cancel(true);
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            requireActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    progress_loading.setVisibility(View.GONE);
                                    progress_for_task.setVisibility(View.GONE);
                                }
                            });
                        }
                    }, 500);
                } else {
                    noTasksYet = false;
                    jsonReader.beginArray(); // Start processing the JSON object
                    while (jsonReader.hasNext()) { // Loop through all keys
                        m = new HashMap<>();
                        m = readMessage(jsonReader);
                        if (!m.isEmpty()) {
                            data.add(m);
                        }
                    }
                    truedata.addAll(data);
                    jsonReader.endArray();
                    jsonReader.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                if (status != 200) {
                    requireActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            imagenotask.setVisibility(View.VISIBLE);
                            textnotask.setVisibility(View.VISIBLE);
                            dialog = new Dialog(Fragment1.this.getActivity(), android.R.style.Theme_Material_Light_NoActionBar);
                            dialog.setContentView(R.layout.dialog_no_internet);
                            Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                            // if button is clicked, close the custom dialog
                            dialogButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getFilteredCity = new AsyncOrzuTasksGetCity();
                                    getFilteredCity.execute();
                                    dialog.dismiss();
                                }
                            });
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.show();
                                }
                            }, 500);
                            getFilteredCity.cancel(true);
                        }
                    });
                }
                e.printStackTrace();
            }
            try {
                myConnection.disconnect();
            } catch (Exception e) {
            }
            return null;
        }

        protected void onPostExecute(ArrayList<Map<String, Object>> result) {
            super.onPostExecute(result);
            count++;
            adapter = new RVAdapter(getContext(), truedata);
            rv.setAdapter(adapter);
            progress_loading.setVisibility(View.GONE);
            progress_for_task.setVisibility(View.GONE);
            adapter.setClickListener(new RVAdapter.ItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(getActivity(), TaskViewMain.class);
                    Map<String, Object> map;
                    map = truedata.get(position);
                    intent.putExtra("id", map.get(idList).toString());
                    intent.putExtra("opt", "view");
                    intent.putExtra("mytask", "not");
                    startActivity(intent);
                }
            });
            noTasks = false;
            if (adapter.getItemCount() == 0) {
                noTasks = true;
                imagenotask.setVisibility(View.VISIBLE);
                textnotask.setVisibility(View.VISIBLE);
            }
        }
    }

    class AsyncOrzuTasksFind extends AsyncTask<String, String, ArrayList<Map<String, Object>>> {
        final HttpsURLConnection[] myConnection = new HttpsURLConnection[1];
        final URL[] orzuEndpoint = new URL[1];

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imagenotask.setVisibility(View.INVISIBLE);
            textnotask.setVisibility(View.INVISIBLE);
            truedata = new ArrayList<>();
            adapter.notifyDataSetChanged();
        }

        @Override
        protected ArrayList<Map<String, Object>> doInBackground(String... strings) {
            orzuEndpoint[0] = null;
            JsonReader[] jsonReader = new JsonReader[1];
            try {
                orzuEndpoint[0] = new URL("https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=view_task&tasks=all&search=" + edittextFind);
                myConnection[0] =
                        (HttpsURLConnection) orzuEndpoint[0].openConnection();
                if (myConnection[0].getResponseCode() == 200) {
                    // Success
                    InputStream responseBody = myConnection[0].getInputStream();
                    InputStreamReader responseBodyReader =
                            new InputStreamReader(responseBody, "UTF-8");
                    jsonReader[0] = new JsonReader(responseBodyReader);
                }
                status = myConnection[0].getResponseCode();
                myConnection[0].setInstanceFollowRedirects(true);
                data = new ArrayList<>();
                if (jsonReader[0] == null) {
                    track = false;
                } else {
                    jsonReader[0].beginArray(); // Start processing the JSON object
                    while (jsonReader[0].hasNext()) { // Loop through all keys
                        m = new HashMap<>();
                        m = readMessage(jsonReader[0]);
                        if (!m.isEmpty()) {
                            data.add(m);
                        }
                    }
                    truedata.addAll(data);
                    jsonReader[0].endArray();
                    jsonReader[0].close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                if (status != 200) {
                    requireActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            imagenotask.setVisibility(View.VISIBLE);
                            textnotask.setVisibility(View.VISIBLE);
                            dialog = new Dialog(Fragment1.this.getActivity(), android.R.style.Theme_Material_Light_NoActionBar);
                            dialog.setContentView(R.layout.dialog_no_internet);
                            Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                            dialogButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    catTask = new AsyncOrzuTasksMain();
                                    catTask.execute();
                                    dialog.dismiss();
                                }
                            });
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.show();
                                }
                            }, 500);
                            catTaskFind.cancel(true);
                        }
                    });
                }
                e.printStackTrace();
            }
            myConnection[0].disconnect();
            return null;
        }

        protected void onPostExecute(ArrayList<Map<String, Object>> result) {
            super.onPostExecute(result);
            adapter = new RVAdapter(getContext(), truedata);
            rv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            adapter.setClickListener(new RVAdapter.ItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(getActivity(), TaskViewMain.class);
                    Map<String, Object> map;
                    map = truedata.get(position);
                    intent.putExtra("id", map.get(idList).toString());
                    intent.putExtra("opt", "view");
                    intent.putExtra("mytask", "not");
                    startActivity(intent);
                }
            });
            if (adapter.getItemCount() == 0) {
                noTasks = true;
                imagenotask.setVisibility(View.VISIBLE);
                textnotask.setVisibility(View.VISIBLE);
            }
        }
    }

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    private void getCategories() {
        Retrofit retrofit = NetworkService.getClient();
        MyTaskApi api = retrofit.create(MyTaskApi.class);
        compositeDisposable.add(api.getCategories().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<List<category_model>>() {
            @Override
            public void onNext(List<category_model> category_models) {
                categories = category_models;
                categories.add(0, new category_model("0", "Все категорий", "0"));
                adapter_category = new MainCategoryAdapter(getContext(), categories);
                category_rv.setAdapter(adapter_category);
            }

            @Override
            public void onError(Throwable e) {
                imagenotask.setVisibility(View.VISIBLE);
                textnotask.setVisibility(View.VISIBLE);
                dialog = new Dialog(con, android.R.style.Theme_Material_Light_NoActionBar);
                dialog.setContentView(R.layout.dialog_no_internet);
                Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getAll();
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

            @Override
            public void onComplete() {

            }
        }));
//        String requestUrl = "https://orzu.org/api?%20appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&lang=ru&opt=view_cat&cat_id=only_parent";
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestUrl, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONArray j = new JSONArray(response);
//                    for (int i = 0; i < j.length(); i++) {
//                        try {
//                            JSONObject object = j.getJSONObject(i);
//                            categories.add(new category_model(object.getString("id"), object.getString("name"), object.getString("parent_id")));
//                            adapter_category = new MainCategoryAdapter(getContext(), categories);
//                            category_rv.setAdapter(adapter_category);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace(); //log the error resulting from the request for diagnosis/debugging
//                imagenotask.setVisibility(View.VISIBLE);
//                textnotask.setVisibility(View.VISIBLE);
//                dialog = new Dialog(con, android.R.style.Theme_Material_Light_NoActionBar);
//                dialog.setContentView(R.layout.dialog_no_internet);
//                Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
//                // if button is clicked, close the custom dialog
//                dialogButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        getAll();
//                        Log.wtf("asdsad", "asdasd");
//                        dialog.dismiss();
//                    }
//                });
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        dialog.show();
//                    }
//                }, 500);
//            }
//        });
//        Volley.newRequestQueue(Objects.requireNonNull(con)).add(stringRequest);
    }

    private void getAll() {
        imagenotask.setVisibility(View.INVISIBLE);
        textnotask.setVisibility(View.INVISIBLE);
        clickedCategory = false;
        getSubCategories("1");
        getCategories();
        catTask = new AsyncOrzuTasksMain();
        catTask.execute();
    }

    private void getSubCategories(String id) {
        subcategories.clear();
        String requestUrl = "https://orzu.org/api?%20appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&lang=ru&opt=view_cat&cat_id=only_subcat&requests=no&id=" + id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray j = new JSONArray(response);
                    for (int i = 0; i < j.length(); i++) {
                        try {
                            JSONObject object = j.getJSONObject(i);
                            subcategories.add(new category_model(object.getString("id"), object.getString("name"), object.getString("parent_id")));
                            adapter_subcategory = new MainSubCategoryAdapter(getContext(), subcategories);
                            subcategory_rv.setAdapter(adapter_subcategory);
                            adapter_category.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (clickedCategory) {
                        for (int k = 0; k < subcategories.size(); k++) {
                            filter = filter + "catid[]=" + subcategories.get(k).getId() + "&";
                        }
                    }
                    shim.setVisibility(View.INVISIBLE);
                    nestshimmer.setVisibility(View.INVISIBLE);
                    if (filter.length() > 8) {
                        truedata.clear();
                        count = 1;
                        getFilteredSubsFiltered = new AsyncOrzuTasksGetSubsFiltered();
                        getFilteredSubsFiltered.execute();
                    } else if (Common.city1.length() > 0) {
                        cityChoose = true;
                        count = 1;
                        getFilteredCity = new AsyncOrzuTasksGetCity();
                        getFilteredCity.execute();
                    } else {
                        cityChoose = false;
                        count = 1;
                        catTask = new AsyncOrzuTasksMain();
                        catTask.execute();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace(); //log the error resulting from the request for diagnosis/debugging
                imagenotask.setVisibility(View.VISIBLE);
                textnotask.setVisibility(View.VISIBLE);
                dialog = new Dialog(con, android.R.style.Theme_Material_Light_NoActionBar);
                dialog.setContentView(R.layout.dialog_no_internet);
                Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getAll();
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
        Volley.newRequestQueue(Objects.requireNonNull(con)).add(stringRequest);
    }
}

