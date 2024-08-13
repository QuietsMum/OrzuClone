package orzu.org;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Random;

public class BonusProgrammAdapter extends RecyclerView.Adapter<BonusProgrammAdapter.MyViewHolder> {

    private static NameItemSelect sw;
    private List<String> maps;
    Context context;
    private ItemClickListener mClickListener;
    int[] backs = {R.drawable.back_bonus1, R.drawable.back_bonus2,R.drawable.back_bonus1, R.drawable.back_bonus2};

    public BonusProgrammAdapter(Context context, List<String> maps) {
        this.context = context;
        this.maps = maps;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_bonus_programm, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.a.setText(maps.get(position));
        holder.back.setImageResource(backs[position]);
    }

    @Override
    public int getItemCount() {
        return maps.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView a;
        ImageView back;

        MyViewHolder(View itemView) {
            super(itemView);
            a = itemView.findViewById(R.id.programm_name);
            back = itemView.findViewById(R.id.programm_back_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}