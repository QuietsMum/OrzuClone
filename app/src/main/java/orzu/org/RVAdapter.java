package orzu.org;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

import orzu.org.Notification.AdapterDifferentLayout;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MyViewHolder> {

    ArrayList<Map<String, Object>> maps;
    Context context;
    ArrayList<Map<String, Object>> filtered = new ArrayList<>();
    private ItemClickListener mClickListener;

    public RVAdapter(Context context, ArrayList<Map<String, Object>> maps) {
        this.context = context;
        this.maps = maps;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.main_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (maps.get(position).get("Категория задачи") == null) {
            holder.a.setText("Категория задачи");
        } else {
            holder.a.setText(maps.get(position).get("Категория задачи").toString());
        }
        if (maps.get(position).get("Задание") == null)
            holder.b.setText("Задание");
        else
            holder.b.setText(maps.get(position).get("Задание").toString());
        if (maps.get(position).get("Сроки") == null)
            holder.c.setText("Сроки не указаны");
        else
            holder.c.setText(maps.get(position).get("Сроки").toString());
        if (maps.get(position).get("Цена") == null)
            holder.d.setText("Цена");
        else
            holder.d.setText(maps.get(position).get("Цена").toString());
        if (maps.get(position).get("Валюта") == null)
            holder.e.setText("");
        else if (maps.get(position).get("Цена").equals("Предложите цену")) {
            holder.e.setText("");
        } else
            holder.e.setText(maps.get(position).get("Валюта").toString());
        if (maps.get(position).get("Город") != null)
            holder.f.setText(maps.get(position).get("Город").toString());
    }

    void filterByCategory(String category) {
        if (category.length() != 0) {
            for (int i = 0; i < maps.size(); i++) {
                if (maps.get(i).get("Категория задачи").toString().equals(category)) {
                    filtered.add(maps.get(i));
                }
            }
            this.maps = filtered;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return maps.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView a, b, c, d, e, f;

        MyViewHolder(View itemView) {
            super(itemView);
            a = itemView.findViewById(R.id.textView);
            b = itemView.findViewById(R.id.textView2);
            c = itemView.findViewById(R.id.textView8);
            d = itemView.findViewById(R.id.textView3);
            e = itemView.findViewById(R.id.textView4);
            f = itemView.findViewById(R.id.textView6);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}