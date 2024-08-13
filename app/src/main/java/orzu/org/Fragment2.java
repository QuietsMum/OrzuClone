package orzu.org;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import orzu.org.Notification.AdapterDifferentLayout;
import orzu.org.Notification.ChooseYouItem;
import orzu.org.Notification.Literature;
import orzu.org.Notification.feedbackItem;

public class Fragment2 extends Fragment {
    String url = "https://orzu.org/tasks/new/techrepair/techrepairother?token=";
    DBHelper dbHelper;
    ArrayList<Map<String, Object>> data;
    private List<Literature> lit = new ArrayList<>();
    private List<Literature> filtered = new ArrayList<>();
    private RecyclerView rv;
    AdapterDifferentLayout adapter;
    SQLiteDatabase db;
    private ImageView no_task;
    private TextView no_task_text;
    String idUser;
    String idtask, type;
    CardView cardView;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstantState) {
        final View view = inflater.inflate(R.layout.fragment_main_2, container, false);
        prefs = getActivity().getSharedPreferences(" ", Context.MODE_PRIVATE);
        editor = prefs.edit();
        Objects.requireNonNull(getActivity()).setTitle(Html.fromHtml("<font color='#ffffff'>Уведомления</font>"));
        dbHelper = new DBHelper(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("orzutable", null, null, null, null, null, null);
        c.moveToFirst();
        int idColIndex = c.getColumnIndex("id");
        idUser = c.getString(idColIndex);
        c.close();
        db.close();
        cardView = view.findViewById(R.id.card_of_notif);
        cardView.setBackgroundResource(R.drawable.shape_card_topcorners);
        BottomNavigationView navigationView = view.findViewById(R.id.navigation);
        navigationView.setSelectedItemId(R.id.all_notif);
        navigationView.setItemIconTintList(null);
        ProgressBar progressBar = view.findViewById(R.id.progressBarMain2);
        progressBar.setVisibility(View.INVISIBLE);
        no_task = view.findViewById(R.id.imageNoTask);
        no_task_text = view.findViewById(R.id.textViewNoTask);
        rv = view.findViewById(R.id.rv_notif);
        rv.setHasFixedSize(true);
        rv.setNestedScrollingEnabled(false);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);
        adapter = new AdapterDifferentLayout(getActivity(), lit);
        rv.setAdapter(adapter);
        getNotif();
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.all_notif:
                        getNotif();
                        break;
                    case R.id.feedback_notif:
                        getOtkliki();
                        break;
                    case R.id.system_notif:
                        getTasks();
                        break;
                }
                return true;
            }
        });
        return view;
    }

    private void getOtkliki() {
        filtered.clear();
        for (int i = 0; i < lit.size(); i++) {
            if (lit.get(i).getType() == 102) {
                filtered.add(lit.get(i));
            }
        }
        adapter = new AdapterDifferentLayout(getActivity(), filtered);
        rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setClickListener(new AdapterDifferentLayout.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (adapter.getItemViewType(position) == 103) {
                    Intent intent = new Intent(getActivity(), ItemSubsNews.class);
                    startActivity(intent);
                } else if (adapter.getItemViewType(position) == 101) {
                    ChooseYouItem item = (ChooseYouItem) lit.get(position);
                    Intent intent = new Intent(getActivity(), TaskViewMain.class);
                    editor.putString("idd", item.getId());
                    editor.putString("opt", "view");
                    editor.putString("mytask", item.getType_task());
                    editor.putBoolean("notif", true);
                    editor.apply();
                    startActivity(intent);
                } else if (adapter.getItemViewType(position) == 102) {
                    feedbackItem item = (feedbackItem) lit.get(position);
                    Intent intent = new Intent(getActivity(), FeedbackTask.class);
                    Common.taskId = item.getId();
                    editor.putString("idd", item.getId());
                    editor.putString("opt", "view");
                    editor.putString("mytask", item.getType_of_task());
                    editor.putBoolean("notif", true);
                    editor.apply();
                    startActivity(intent);
                }
            }
        });
    }

    private void getTasks() {
        filtered.clear();
        for (int i = 0; i < lit.size(); i++) {
            if (lit.get(i).getType() == 101) {
                filtered.add(lit.get(i));
            }
        }
        adapter = new AdapterDifferentLayout(getActivity(), filtered);
        rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setClickListener(new AdapterDifferentLayout.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (adapter.getItemViewType(position) == 103) {
                    Intent intent = new Intent(getActivity(), ItemSubsNews.class);
                    startActivity(intent);
                } else if (adapter.getItemViewType(position) == 101) {
                    ChooseYouItem item = (ChooseYouItem) lit.get(position);
                    Intent intent = new Intent(getActivity(), TaskViewMain.class);
                    editor.putString("idd", item.getId());
                    editor.putString("opt", "view");
                    editor.putString("mytask", item.getType_task());
                    editor.putBoolean("notif", true);
                    editor.apply();
                    startActivity(intent);
                } else if (adapter.getItemViewType(position) == 102) {
                    feedbackItem item = (feedbackItem) lit.get(position);
                    Intent intent = new Intent(getActivity(), FeedbackTask.class);
                    Common.taskId = item.getId();
                    editor.putString("idd", item.getId());
                    editor.putString("opt", "view");
                    editor.putString("mytask", item.getType_of_task());
                    editor.putBoolean("notif", true);
                    editor.apply();
                    startActivity(intent);
                }
            }
        });
    }

    private void getNotif() {
        lit.clear();
        dbHelper = new DBHelper(getActivity());
        db = dbHelper.getWritableDatabase();
        Cursor c = db.query("orzunotif", null, null, null, null, null, null);
        c.moveToFirst();
        lit = new ArrayList<>();
        do {
            try {
                int idColIndex = c.getColumnIndex("id");
                int typeIndex = c.getColumnIndex("type");
                int mesColIndex = c.getColumnIndex("title");
                int cityIndex = c.getColumnIndex("city");
                int created_atIndex = c.getColumnIndex("created_at");
                int work_withIndex = c.getColumnIndex("work_with");
                idtask = c.getString(idColIndex);
                String header;
                String title = c.getString(mesColIndex);
                String city = c.getString(cityIndex);
                String created_At = c.getString(created_atIndex);
                String work = c.getString(work_withIndex);
                type = c.getString(typeIndex);
                if (c.getString(typeIndex).equals("my")) {
                    feedbackItem item = new feedbackItem(title, created_At, work, city,idtask,type);
                    lit.add(item);
                } else {
                    header = "+1 задание";
                    work = "предложите цену";
                    ChooseYouItem item = new ChooseYouItem(header, title, work, city, created_At, idtask, type);
                    lit.add(item);
                }
                no_task.setVisibility(View.INVISIBLE);
                no_task_text.setVisibility(View.INVISIBLE);
            } catch (Exception e) {
                no_task.setVisibility(View.VISIBLE);
                no_task_text.setVisibility(View.VISIBLE);
                rv.setVisibility(View.GONE);
            }
        } while (c.moveToNext());
        adapter = new AdapterDifferentLayout(getActivity(), lit);
        rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setClickListener(new AdapterDifferentLayout.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (adapter.getItemViewType(position) == 103) {
                    Intent intent = new Intent(getActivity(), ItemSubsNews.class);
                    startActivity(intent);
                } else if (adapter.getItemViewType(position) == 101) {
                    ChooseYouItem item = (ChooseYouItem) lit.get(position);
                    Intent intent = new Intent(getActivity(), TaskViewMain.class);
                    editor.putString("idd", item.getId());
                    editor.putString("opt", "view");
                    editor.putString("mytask", item.getType_task());
                    editor.putBoolean("notif", true);
                    editor.apply();
                    startActivity(intent);
                } else if (adapter.getItemViewType(position) == 102) {
                    feedbackItem item = (feedbackItem) lit.get(position);
                    Intent intent = new Intent(getActivity(), FeedbackTask.class);
                    Common.taskId = item.getId();
                    editor.putString("idd", item.getId());
                    editor.putString("opt", "view");
                    editor.putString("mytask", item.getType_of_task());
                    editor.putBoolean("notif", true);
                    editor.apply();
                    startActivity(intent);
                }
            }
        });
        c.close();
        dbHelper.close();
        db.close();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.notif_menu, menu);
        Drawable drawable = menu.findItem(R.id.settings_item_notif).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(getActivity(), R.color.colorBackgrndFrg));
        menu.findItem(R.id.settings_item_notif).setIcon(drawable);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings_item_notif) {
            Intent intent = new Intent(getActivity(), NotificationSettings.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

