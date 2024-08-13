package orzu.org;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Objects;

public class CreateTaskPhoto extends AppCompatActivity implements View.OnClickListener {

    Button buttonCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
        setContentView(R.layout.activity_create_task_photo);

        getSupportActionBar().setTitle("Фотография");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        buttonCreate = findViewById(R.id.createPhoto);
        buttonCreate.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

        finish();
        CreateTaskDetail.fa.finish();
        CreateTaskAmout.fa.finish();
        CreateTaskTerm.fa.finish();
        CreateTaskPlace.fa.finish();
        CreateTaskName.fa.finish();
        CreateTaskCategory.fa.finish();
        CreateTaskSubCategory.fa.finish();

    }
}
