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

public class MainSubCategoryAdapter extends RecyclerView.Adapter<MainSubCategoryAdapter.MyViewHolder> {

    private static NameItemSelect sw;
    private List<category_model> maps;
    Context context;
    int[] backs = {R.drawable.subcategory_back1, R.drawable.subcategory_back2, R.drawable.subcategory_back3, R.drawable.subcategory_back4, R.drawable.subcategory_back5, R.drawable.subcategory_back6
    ,R.drawable.subcategory_back7,R.drawable.subcategory_back8,R.drawable.subcategory_back9,R.drawable.subcategory_back10,R.drawable.subcategory_back11};
    int last_index = -1;
    public MainSubCategoryAdapter(Context context, List<category_model> maps) {
        this.context = context;
        this.maps = maps;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_subcategory_main, parent, false);
        return new MyViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.a.setText(maps.get(position).getName());
        holder.back.setImageResource(backs[randomInt()]);
    }
    private int randomInt() {
        Random random = new Random();
        int rand = random.nextInt(backs.length);
        while (true) {
            if (rand != last_index) {
                last_index = rand;
                return rand;
            }
            rand = random.nextInt(6);
        }
    }
    @Override
    public int getItemCount() {
        return maps.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView a;
        ImageView back;
        MyViewHolder(View itemView) {
            super(itemView);
            a = itemView.findViewById(R.id.subcategory_name);
            back = itemView.findViewById(R.id.subcategory_back_item);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            sw.onItemSelectedListener(view, getAdapterPosition());
        }
    }
    public static void setSelect(NameItemSelect select) {
        MainSubCategoryAdapter.sw = select;
    }
}