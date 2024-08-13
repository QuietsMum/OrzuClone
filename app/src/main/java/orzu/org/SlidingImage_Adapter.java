package orzu.org;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.viewpager.widget.PagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SlidingImage_Adapter extends PagerAdapter {

    private ArrayList<Integer> IMAGES;
    private ArrayList<Integer> IMAGESBACK;
    private ArrayList<String> Text;
    private ArrayList<String> Text2;
    private LayoutInflater inflater;
    private Context context;

    public SlidingImage_Adapter(Context context, ArrayList<Integer> IMAGES, ArrayList<Integer> IMAGESBACK, ArrayList<String> Text, ArrayList<String> Text2) {
        this.context = context;
        this.IMAGES=IMAGES;
        this.IMAGESBACK=IMAGESBACK;
        this.Text=Text;
        this.Text2=Text2;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
    @Override
    public int getCount() {
        return IMAGES.size();
    }
    @NotNull
    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);
        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout
                .findViewById(R.id.image);
        final ImageView imageView2 = (ImageView) imageLayout
                .findViewById(R.id.image2);
        final TextView textView = (TextView) imageLayout
                .findViewById(R.id.textViewSlide);
        final TextView textView2 = (TextView) imageLayout
                .findViewById(R.id.textViewSlide2);
        imageView2.setImageResource(IMAGES.get(position));
        imageView.setBackgroundResource(IMAGESBACK.get(position));
        textView.setText(Text.get(position));
        textView2.setText(Text2.get(position));
        view.addView(imageLayout, 0);
        return imageLayout;
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }
    @Override
    public Parcelable saveState() {
        return null;
    }
}