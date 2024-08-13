package orzu.org;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Random;

public class SaleAdapter extends RecyclerView.Adapter<SaleAdapter.MyViewHolder> {

    private List<category_model> maps;
    Context context;
    int index;
    private ItemClickListener mClickListener;

    public SaleAdapter(Context context, List<category_model> maps) {
        this.context = context;
        this.maps = maps;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_sale_percent, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.cat.setText(maps.get(position).getId());
        holder.desc.setText(maps.get(position).getName());
        holder.perc.setText(maps.get(position).getParent_id());
        if (index == position) {
            holder.back.setBackgroundResource(R.drawable.gradient_of_next_button);
        }else{
            holder.back.setBackgroundResource(R.drawable.circle_shape_intro_white);
        }
    }

    public void changeColor(int position) {
        index = position;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return maps.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView cat, desc, perc;
        CardView back;

        MyViewHolder(View itemView) {
            super(itemView);
            cat = itemView.findViewById(R.id.sale_cat);
            desc = itemView.findViewById(R.id.sale_desc);
            perc = itemView.findViewById(R.id.sale_percent);
            back = itemView.findViewById(R.id.card_main);
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