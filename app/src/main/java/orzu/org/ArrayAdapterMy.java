package orzu.org;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;


public class ArrayAdapterMy extends RecyclerView.Adapter<ArrayAdapterMy.MyViewHolder>{

    private static NameItemSelect select;
    private List<String> maps;
    Context context;

    ArrayAdapterMy(Context context, List<String> maps) {
        this.context = context;
        this.maps = maps;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.simple_list_item_suggest, parent,false);
        return new MyViewHolder(v);
    }



    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            holder.a.setText(maps.get(position));

    }

    @Override
    public int getItemCount() {
        return maps.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView a;

        MyViewHolder(View itemView) {
            super(itemView);
            a = itemView.findViewById(R.id.suggest_text);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            select.onItemSelectedListener(view, getAdapterPosition());
        }
    }

    static void setSelect(NameItemSelect select) {
        ArrayAdapterMy.select = select;
    }
}