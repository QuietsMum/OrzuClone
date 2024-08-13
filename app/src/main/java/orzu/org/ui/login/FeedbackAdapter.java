package orzu.org.ui.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Map;
import orzu.org.R;

public class FeedbackAdapter extends BaseAdapter {
    Context context;
    ArrayList<Map<String, Object>> data;
    public FeedbackAdapter(Context context, ArrayList<Map<String, Object>> data) {
        this.context = context;
        this.data = data;
    }
    @Override
    public int getCount() {
        return data.size();
    }
    @Override
    public Object getItem(int i) {
        return data.get(i);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }
    public void ChangeData( ArrayList<Map<String, Object>> data){
        this.data = data;
        notifyDataSetChanged();
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        @SuppressLint("ViewHolder") View v = LayoutInflater.from(context).inflate(R.layout.feedback_item, viewGroup, false);
        TextView name = v.findViewById(R.id.feedback_name);
        TextView countFeedback = v.findViewById(R.id.countFeedback);
        TextView narrFeedback = v.findViewById(R.id.narrFeedback);
        ImageView imageFeedback = v.findViewById(R.id.imageFeedback);
        ImageView imageViewOtziv = v.findViewById(R.id.imageViewOtziv);
        name.setText(data.get(i).get("Имена").toString());
        narrFeedback.setText(data.get(i).get("Описание").toString());
        countFeedback.setText(data.get(i).get("Отзыв").toString());
        imageFeedback.setImageResource((int) data.get(i).get("Картинка"));
        imageViewOtziv.setImageBitmap((Bitmap)data.get(i).get("Аватар"));
        return v;
    }
}
