package orzu.org;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import orzu.org.models.MyTaskApi;
import orzu.org.models.MyTasks;
import orzu.org.models.NetworkService;
import orzu.org.ui.login.model;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Fragment4 extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    String idUser;
    private Dialog dialog;
    private int status;
    private ArrayList<Map<String, Object>> data;
    private ArrayList<Map<String, Object>> truedata;
    private RecyclerView rv;
    private RVAdapter adapter;
    private Map<String, Object> m = new HashMap<>();
    private Map<String, Object> m_new_2 = new HashMap<>();
    private AsyncOrzuTasksMainRefreshMy catTaskRef;
    private final String categoryList = "Категория задачи";
    private final String taskList = "Задание";
    private String idList = "ID";
    private String catidList = "CatID";
    private final String priceList = "Цена";
    private final String servList = "Валюта";
    private final String cityList = "Город";
    private final String needList = "Сроки";
    private String needListdfrom = "Сроки";
    private ProgressBar progressBar;
    private int count;
    private SwipeRefreshLayout swipeLayout;
    private AsyncOrzuTasksMy catTask;
    private Boolean track = true;
    private Boolean noTasks = true;
    private ImageView imagenotask;
    private TextView textnotask;
    boolean countPager = true;
    private boolean noTasksYet = false;
    private NestedScrollView scroll_of_fragment4;
    private ProgressBar progress_loading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstantState) {
        final View view = inflater.inflate(R.layout.fragment_main_4, container, false);
        DBHelper dbHelper = new DBHelper(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        @SuppressLint("Recycle") Cursor c = db.query("orzutable", null, null, null, null, null, null);
        c.moveToFirst();
        int idColIndex = c.getColumnIndex("id");
        int tokenColIndex = c.getColumnIndex("token");
        idUser = c.getString(idColIndex);
        String tokenUser = c.getString(tokenColIndex);
        TextView floaBtn = view.findViewById(R.id.create_task_main);
        floaBtn.setOnClickListener(this);
        progressBar = view.findViewById(R.id.progressBarMy);
        int counter = 1;
        imagenotask = view.findViewById(R.id.imageViewMy);
        textnotask = view.findViewById(R.id.textViewMy);
        imagenotask.setVisibility(View.INVISIBLE);
        textnotask.setVisibility(View.INVISIBLE);
        rv = view.findViewById(R.id.rvMy);
        progress_loading = view.findViewById(R.id.progress_loading2);
        rv.setNestedScrollingEnabled(false);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.nestedScrollViewMy);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark),
                getResources().getColor(R.color.colorPrimaryLight),
                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorPrimary));
        truedata = new ArrayList<>();
        count = 1;
        int countItem = 1;

        catTask = new AsyncOrzuTasksMy();
        catTask.execute();
        scroll_of_fragment4 = view.findViewById(R.id.scroll_of_fragment4);
        scroll_of_fragment4.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if (!noTasksYet) {
                    View view1 = (View) scroll_of_fragment4.getChildAt(scroll_of_fragment4.getChildCount() - 1);
                    int diff = (view1.getBottom() - (scroll_of_fragment4.getHeight() + scroll_of_fragment4
                            .getScrollY()));
                    progress_loading.setVisibility(View.VISIBLE);
                    if (diff == 0) {
                        swipeLayout.setEnabled(llm.findFirstCompletelyVisibleItemPosition() == 0 || adapter.getItemCount() == 0);
                        catTask = new AsyncOrzuTasksMy();
                        catTask.execute();
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), CreateTaskCategory.class);
        startActivity(intent);
    }

    private Map<String, Object> readMessage(JsonReader reader) throws IOException {
        long id;
        String text;
        String date;
        String update;
        String needfrom;
        String needto;
        String catid;
        String subcut;
        String price;
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
                        m.put(servList, "За услугу");
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
    public void onRefresh() {
        noTasksYet = false;
        swipeLayout.setRefreshing(false);
        catTaskRef = new AsyncOrzuTasksMainRefreshMy();
        catTaskRef.execute();
    }

    @SuppressLint("StaticFieldLeak")
    class AsyncOrzuTasksMy extends AsyncTask<String, String, ArrayList<Map<String, Object>>> {
        final HttpsURLConnection[] myConnection = new HttpsURLConnection[1];
        final URL[] orzuEndpoint = new URL[1];

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
            String result = "";
            try {
                orzuEndpoint[0] = new URL("https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&lang=ru&opt=view_task&tasks=all&userid= " + idUser + "&page=" + count);
                myConnection[0] =
                        (HttpsURLConnection) orzuEndpoint[0].openConnection();
                if (myConnection[0].getResponseCode() == 200) {
                    // Success
                    InputStream responseBody = myConnection[0].getInputStream();
                    Scanner s = new Scanner(responseBody).useDelimiter("\\A");
                    result = s.hasNext() ? s.next() : "";

                }
                if (result.equals("\"Not tasks yet\"")) {
                    noTasksYet = true;
                    requireActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            progress_loading.setVisibility(View.GONE);
                        }
                    });
                    if (count != 1) {
                        catTask.cancel(true);
                    }
                } else {
                    noTasksYet = false;
                }
                myConnection[0] =
                        (HttpsURLConnection) orzuEndpoint[0].openConnection();
                if (myConnection[0].getResponseCode() == 200) {
                    // Success
                    InputStream responseBody = myConnection[0].getInputStream();
                    InputStreamReader responseBodyReader =
                            new InputStreamReader(responseBody, StandardCharsets.UTF_8);
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
                        Map<String, Object> m_new = new HashMap<>();
                        m_new_2 = new HashMap<>();
                        m = readMessage(jsonReader[0]);
                        Long[] savedList = model.array;
                        Long det = 0L;
                        if (savedList != null) {
                            for (Long aLong : savedList) {
                                if (aLong != null && aLong != 0) {
                                    det = aLong;
                                }
                            }
                        }
                        String dateList = "Дата публицации";
                        if (det == 0L) {
                            m_new.put(idList, m.get(idList));
                            m_new.put(taskList, m.get(taskList));
                            m_new.put(categoryList, m.get(categoryList));
                            m_new.put(priceList, m.get(priceList));
                            m_new.put(servList, m.get(servList));
                            m_new.put(dateList, m.get(dateList));
                            m_new.put(cityList, m.get(cityList));
                            m_new.put(needListdfrom, m.get(needListdfrom));
                            m_new.put(catidList, m.get(catidList));
                        } else {
                            for (Long aLong : savedList) {
                                if (aLong != null) {
                                    if (aLong.toString().equals(m.get(catidList).toString())) {
                                        m_new.put(idList, m.get(idList));
                                        m_new.put(taskList, m.get(taskList));
                                        m_new.put(categoryList, m.get(categoryList));
                                        m_new.put(priceList, m.get(priceList));
                                        m_new.put(servList, m.get(servList));
                                        m_new.put(dateList, m.get(dateList));
                                        m_new.put(cityList, m.get(cityList));
                                        m_new.put(needListdfrom, m.get(needListdfrom));
                                        m_new.put(catidList, m.get(catidList));
                                    }
                                }
                            }
                        }
                        if (!m_new.isEmpty()) {
                            data.add(m_new);
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
                            dialog = new Dialog(Fragment4.this.getActivity(), android.R.style.Theme_Material_Light_NoActionBar);
                            dialog.setContentView(R.layout.dialog_no_internet);
                            Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                            // if button is clicked, close the custom dialog
                            dialogButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    catTask = new AsyncOrzuTasksMy();
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
                            catTask.cancel(true);
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
            count++;
            progressBar.setVisibility(View.INVISIBLE);
            progress_loading.setVisibility(View.GONE);
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
                    intent.putExtra("mytask", "my");
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
    class AsyncOrzuTasksMainRefreshMy extends AsyncTask<String, String, ArrayList<Map<String, Object>>> {
        final HttpsURLConnection[] myConnection = new HttpsURLConnection[1];
        final URL[] orzuEndpoint = new URL[1];

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
                orzuEndpoint[0] = new URL("https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&lang=ru&opt=view_task&tasks=all&userid= " + idUser + "&page=0");
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
                    Long[] savedList = model.array;
                    Long det = 0L;
                    if (savedList != null) {
                        for (int i = 0; i < savedList.length; i++) {
                            if (savedList[i] != null && savedList[i] != 0) {
                                det = savedList[i];
                            }
                        }
                    }
                    if (det == 0L) {
                        m_new_2.put(idList, id);
                    } else {
                        for (Long aLong : savedList) {
                            if (aLong != null) {
                                if (aLong.toString().equals(m.get(catidList).toString())) {
                                    m_new_2.put(idList, id);
                                }
                            }
                        }
                    }
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
                            dialog = new Dialog(Objects.requireNonNull(Fragment4.this.getActivity()), android.R.style.Theme_Material_Light_NoActionBar);
                            dialog.setContentView(R.layout.dialog_no_internet);
                            Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                            // if button is clicked, close the custom dialog
                            dialogButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    catTaskRef = new AsyncOrzuTasksMainRefreshMy();
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
            if (truedata.size() != 0) {
                Map<String, Object> m_ref = truedata.get(0);
                Long idold = (Long) m_ref.get(idList);
                Long idnew = (Long) m_new_2.get(idList);
                if (!idold.equals(idnew) || noTasks) {
                    count = 1;
                    imagenotask.setVisibility(View.INVISIBLE);
                    textnotask.setVisibility(View.INVISIBLE);
                    truedata = new ArrayList<>();
                    data = new ArrayList<>();
                    adapter.notifyDataSetChanged();
                    catTask = new AsyncOrzuTasksMy();
                    catTask.execute();
                }
            } else {
                imagenotask.setVisibility(View.VISIBLE);
                textnotask.setVisibility(View.VISIBLE);
            }
        }
    }
}