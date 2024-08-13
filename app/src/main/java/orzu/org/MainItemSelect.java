package orzu.org;

import android.view.View;

public interface MainItemSelect extends View.OnClickListener {
    void onItemSelectedListener(View view, int position);
}
