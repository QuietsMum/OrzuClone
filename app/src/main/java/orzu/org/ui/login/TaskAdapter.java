package orzu.org.ui.login;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import orzu.org.ChatActivity;
import orzu.org.ChatsView;
import orzu.org.Common;
import orzu.org.FeedbackTask;
import orzu.org.Main2Activity;
import orzu.org.R;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private List<Map<String, Object>> logos;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context context2;
    // data is passed into the constructor
    public TaskAdapter(Context context, List<Map<String, Object>> logos) {
        this.mInflater = LayoutInflater.from(context);
        this.logos = logos;
        this.context2 = context;
    }
    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.feedback_task_item, parent, false);
        return new ViewHolder(view);
    }
    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(logos.get(position).get("Имена").toString());
        holder.desc.setText(logos.get(position).get("Описание").toString());
        holder.sad.setText(logos.get(position).get("Sad").toString());
        holder.nat.setText(logos.get(position).get("Nat").toString());
        holder.happy.setText(logos.get(position).get("Hap").toString());
        holder.price.setText(logos.get(position).get("Цена").toString());
        Picasso.get().load("https://orzu.org" + logos.get(position).get("Avatar").toString()).fit().centerCrop().into(holder.image);
        if (logos.get(position).get("Select").toString().equals("1")) {
            holder.button2.setVisibility(View.VISIBLE);
            holder.button3.setVisibility(View.VISIBLE);
            holder.button4.setVisibility(View.VISIBLE);
        }
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseSuggester(logos.get(position).get("SugID"));
            }
        });
        holder.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelSuggester(logos.get(position).get("SugID"));
            }
        });
        holder.button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createChat(logos.get(position).get("SugID"));
            }
        });
        holder.button2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + logos.get(position).get("Phone").toString()));
                if (context2.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                context2.startActivity(intent);
            }
        });
    }
    // total number of rows
    @Override
    public int getItemCount() {
        return logos.size();
    }

    private void createChat(Object id) {
        String url = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS" +
                "&opt=user_chats" +
                "&act=create_chat" +
                "&user_first=" + Common.userId +
                "&user_second=" + id;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Dialog dialog = new Dialog(context2, android.R.style.Theme_Material_Light_NoActionBar);
                dialog.setContentView(R.layout.dialog_no_internet);
                Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createChat(id);
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
            public void onResponse(Call call, Response response) throws IOException {
                FeedbackTask.fa.finish();
                Intent intent = new Intent(context2, ChatActivity.class);
                SharedPreferences prefs = context2.getSharedPreferences(" ", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("openChats", true);
                editor.apply();
                String[] id_of_partner = response.body().string().split(":");
                Common.chatId =  id_of_partner[1];;
                Common.user_to = String.valueOf(id);

                context2.startActivity(intent);

            }
        });
    }
    private void chooseSuggester(Object id) {
        String url = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS" +
                "&opt=task_requests" +
                "&act=selected" +
                "&req_id=" + id +
                "&userid=" + Common.userId +
                "&utoken=" + Common.utoken;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Dialog dialog = new Dialog(context2, android.R.style.Theme_Material_Light_NoActionBar);
                dialog.setContentView(R.layout.dialog_no_internet);
                Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseSuggester(id);
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
            public void onResponse(Call call, Response response) throws IOException {
                FeedbackTask.fa.finish();
                Intent intent = new Intent(context2, FeedbackTask.class);
                context2.startActivity(intent);

            }
        });
    }
    private void cancelSuggester(Object id) {
        String url = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS" +
                "&opt=task_requests" +
                "&act=cancel" +
                "&req_id=" + id +
                "&userid=" + Common.userId +
                "&utoken=" + Common.utoken;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Dialog dialog = new Dialog(context2, android.R.style.Theme_Material_Light_NoActionBar);
                dialog.setContentView(R.layout.dialog_no_internet);
                Button dialogButton = (Button) dialog.findViewById(R.id.buttonInter);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseSuggester(id);
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
            public void onResponse(Call call, Response response) throws IOException {
                FeedbackTask.fa.finish();
                Intent intent = new Intent(context2, FeedbackTask.class);
                context2.startActivity(intent);

            }
        });
    }
    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        TextView desc, sad, nat, happy, price;
        LinearLayout button;
        LinearLayout button2;
        LinearLayout button3;
        LinearLayout button4;
        ImageView image;
        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.feedback_task_name);
            desc = itemView.findViewById(R.id.narrFeedbackTask);
            sad = itemView.findViewById(R.id.countFeedbackTask1);
            nat = itemView.findViewById(R.id.countFeedbackTask2);
            happy = itemView.findViewById(R.id.countFeedbackTask3);
            price = itemView.findViewById(R.id.amtFeedbackTask);
            button = itemView.findViewById(R.id.button_addsugester);
            button2 = itemView.findViewById(R.id.button_callsugester);
            button3 = itemView.findViewById(R.id.button_callsugester2);
            button4 = itemView.findViewById(R.id.button_chatsugester);
            image = itemView.findViewById(R.id.imageViewOtklik);
            for (int i = 0; i < logos.size(); i++) {
                if (logos.get(i).get("Select").toString().equals("1")) {
                    button.setVisibility(View.INVISIBLE);
                }
            }
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}