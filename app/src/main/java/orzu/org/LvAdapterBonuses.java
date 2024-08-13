package orzu.org;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class  LvAdapterBonuses extends RecyclerView.Adapter<LvAdapterBonuses.MyViewHolder> {
    Context context;
    List<BonusList> list;
    LayoutInflater inflater;
    private ItemClickListener mClickListener;

    public LvAdapterBonuses(Context context, List<BonusList> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_bonuses_qr, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        holder.text.setText(list.get(i).getReason());
        if (list.get(i).getPlmn().equals("+")) {
            holder.wallet.setText("+" + list.get(i).getValue());
        } else {
            holder.wallet.setText("-" + list.get(i).getValue());
        }
        if (i != 0) {
            if (!list.get(i - 1).date.equals(list.get(i).getDate())) {
                holder.date_bonus.setText(list.get(i).getDate());
                holder.date_bonus.setVisibility(View.VISIBLE);
            }
        } else {
            holder.date_bonus.setText(list.get(i).getDate());
            holder.date_bonus.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView text;
        TextView wallet;
        TextView date_bonus;

        MyViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
            wallet = itemView.findViewById(R.id.wallet_cost);
            date_bonus = itemView.findViewById(R.id.date_bonus);
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
