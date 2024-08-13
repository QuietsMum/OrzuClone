package orzu.org.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import orzu.org.ChatActivity;
import orzu.org.R;

public class ChatAdapter extends RecyclerView.Adapter {

    private List<chatItems> logos;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    // data is passed into the constructor
    public ChatAdapter(Context context, List<chatItems> logos) {
        this.mInflater = LayoutInflater.from(context);
        this.logos = logos;
    }
    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = mInflater.inflate(R.layout.chat_item, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).bindView(position);

    }
    // total number of rows
    @Override
    public int getItemCount() {
        return logos.size();
    }
    // stores and recycles views as they are scrolled off screen
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name,message, time;
        String image;
        Bitmap img;
        ImageView imgView,notification;
        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name_of_chat);
            message = itemView.findViewById(R.id.text_of_chat);
            time = itemView.findViewById(R.id.time_of_chat);
            notification = itemView.findViewById(R.id.notification_of_chat);
            imgView = itemView.findViewById(R.id.chat_image);
            itemView.setOnClickListener(this);
        }
        void bindView(int position) {
            name.setText(logos.get(position).getName());
            message.setText(logos.get(position).getNotification());
            time.setText(logos.get(position).getTime());
            if(logos.get(position).getDel().equals("1")){
                notification.setImageResource(R.drawable.double_tick);
                notification.setColorFilter(notification.getContext().getResources().getColor(R.color.colorPrimaryGrey));
            }
            if(logos.get(position).getSaw().equals("1")){
                notification.setImageResource(R.drawable.double_tick);
                notification.setColorFilter(notification.getContext().getResources().getColor(R.color.colorAccent));
            }
            image = logos.get(position).getImg();
            Picasso.get().load("https://orzu.org" + image.replaceAll("\\\\", "")).fit().centerCrop().into(imgView);

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