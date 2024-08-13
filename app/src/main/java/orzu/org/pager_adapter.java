package orzu.org;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.viewpager.widget.PagerAdapter;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class pager_adapter extends PagerAdapter {
    private Context context;
    List<String> imgs = new ArrayList<>();
    HashMap<Integer, String> values = new HashMap<>();
    public pager_adapter(Context context, List<String> imgs) {
        this.context = context;
        this.imgs = imgs;
    }
    public pager_adapter(TaskViewMain context, HashMap<Integer, String> values) {
        this.context = context;
        this.values = values;
    }
    /*
    This callback is responsible for creating a page. We inflate the layout and set the drawable
    to the ImageView based on the position. In the end we add the inflated layout to the parent
    container .This method returns an object key to identify the page view, but in this example page view
    itself acts as the object key
    */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.pager_item, null);
        ImageView imageView = view.findViewById(R.id.image);
        if(!imgs.isEmpty()) {
            Picasso.get().load("https://orzu.org/" + imgs.get(position).replaceAll("\\\\","")).into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, FullScreenImageActivity.class);
                    intent.setData(Uri.parse("https://orzu.org/" + imgs.get(position).replaceAll("\\\\","")));
                    context.startActivity(intent);
                }
            });
        }else{
            imageView.setImageURI(Uri.parse(values.get(position)));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, FullScreenImageActivity.class);
                    intent.setData(Uri.parse(values.get(position)));
                    intent.putExtra("isFromCreate",true);
                    context.startActivity(intent);
                }
            });
        }
        container.addView(view);
        return view;
    }
    /*
    This callback is responsible for destroying a page. Since we are using view only as the
    object key we just directly remove the view from parent container
    */
    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeView((View) view);
    }
    /*
    Returns the count of the total pages
    */
    @Override
    public int getCount() {
        if(!imgs.isEmpty()) {
            return imgs.size();
        }else{
            return values.size();
        }
    }
    /*
    Used to determine whether the page view is associated with object key returned by instantiateItem.
    Since here view only is the key we return view==object
    */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }
}
