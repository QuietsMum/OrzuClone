package orzu.org;


import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterPartnerBonuses extends RecyclerView.Adapter<AdapterPartnerBonuses.MyViewHolder> {
    List<Bonuses> maps;
    Context context;
    private ItemClickListener mClickListener;

    AdapterPartnerBonuses(Context context, List<Bonuses> maps) {
        this.context = context;
        this.maps = maps;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.partner_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.header.setText(maps.get(position).getName());
        holder.percent.setText(maps.get(position).getPercent());
        TextPaint paint = holder.percent.getPaint();
        float width = paint.measureText(maps.get(position).getPercent());
        Shader textShader = new LinearGradient(0, 0, width, holder.percent.getTextSize(),
                new int[]{
                        Color.parseColor("#fe8c00"),
                        Color.parseColor("#f83600"),
                }, null, Shader.TileMode.CLAMP);
        holder.percent.getPaint().setShader(textShader);
    }

    @Override
    public int getItemCount() {
        return maps.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView header;
        TextView percent;

        MyViewHolder(View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.city_text);
            percent = itemView.findViewById(R.id.item_percentage);
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
