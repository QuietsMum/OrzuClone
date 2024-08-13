package orzu.org.chat;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import orzu.org.R;
public class ChattingAdapter extends RecyclerView.Adapter {
    private List<message_interface> logos;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    // data is passed into the constructor
    public ChattingAdapter(Context context, List<message_interface> logos) {
        this.mInflater = LayoutInflater.from(context);
        this.logos = logos;
    }
    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case message_interface.TYPE_MY:
                view = mInflater.inflate(R.layout.message_item, parent, false);
                return new ViewHolder(view);
            case message_interface.TYPE_THEIR:
                view = mInflater.inflate(R.layout.their_message_item, parent, false);
                return new ViewHolder2(view);
        }
        return null;
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case message_interface.TYPE_MY:
                ((ViewHolder) holder).bindView(position);
                break;
            case message_interface.TYPE_THEIR:
                ((ViewHolder2) holder).bindView(position);
                break;
        }
    }
    @Override
    public int getItemViewType(int position) {
        return logos.get(position).getType();
    }
    // total number of rows
    @Override
    public int getItemCount() {
        return logos.size();
    }
    // stores and recycles views as they are scrolled off screen
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView message,date;
        ViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message_body);
            date = itemView.findViewById(R.id.time);
            itemView.setOnClickListener(this);
        }
        void bindView(int position) {
            my_message my_message = (my_message)logos.get(position);
            message.setText(my_message.message);
            date.setText(my_message.date);
        }
        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
    class ViewHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView message,date;
        ViewHolder2(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message_body);
            date = itemView.findViewById(R.id.date);
            itemView.setOnClickListener(this);
        }
        void bindView(int position) {
            their_message their_message = (their_message) logos.get(position);
            message.setText(their_message.message);
            date.setText(their_message.date);
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