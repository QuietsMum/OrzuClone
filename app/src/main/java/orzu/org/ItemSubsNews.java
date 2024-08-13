package orzu.org;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class ItemSubsNews extends AppCompatActivity implements View.OnClickListener {
    TextView btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_back));
        setContentView(R.layout.activity_item_subs_news);
        getSupportActionBar().setTitle("Новости");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        btn = findViewById(R.id.close_news);
        btn.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        finish();
    }
}
