package orzu.org;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

public class FullScreenImageActivity extends AppCompatActivity {
    ImageView fullScreenImageView_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorTextLight));
        setContentView(R.layout.activity_full_screen_image);
        PhotoView fullScreenImageView = findViewById(R.id.fullScreenImageView);
        Intent callingActivityIntent = getIntent();
        Boolean bool = getIntent().getBooleanExtra("isFromCreate",false);
        fullScreenImageView_back = findViewById(R.id.fullScreenImageView_back);
        fullScreenImageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if(callingActivityIntent != null) {
            Uri imageUri = callingActivityIntent.getData();
            if(imageUri != null && fullScreenImageView != null&&!bool) {
                Glide.with(this)
                        .load(imageUri)
                        .into(fullScreenImageView);
            }else if(imageUri != null && fullScreenImageView != null&&bool){
                fullScreenImageView.setImageURI(callingActivityIntent.getData());
            }
        }
    }
}
