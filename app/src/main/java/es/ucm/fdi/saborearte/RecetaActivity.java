package es.ucm.fdi.saborearte;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import android.text.method.ScrollingMovementMethod;

public class RecetaActivity extends AppCompatActivity {
    private static final String TAG = RecetaActivity.class.getSimpleName();
    private TextView title;
    private TextView time;
    private TextView ingredients;
    private TextView instructions;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receta);

        Log.i(TAG, "Receta view accessed");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Receta receta = (Receta) getIntent().getSerializableExtra("receta");

        image = findViewById(R.id.receta_imagen);
        title = findViewById(R.id.receta_title);
        time = findViewById(R.id.receta_time);
        ingredients = findViewById(R.id.receta_ingredients);
        instructions = findViewById(R.id.receta_instructions);

        createActivity(receta);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return true;
    }

    private void createActivity(Receta receta){
        title.setText(receta.getTitulo());
        time.setText(receta.getTiempoPreparacion() + " min.");
        ingredients.setText(receta.getIngredientes());
        ingredients.setMovementMethod(new ScrollingMovementMethod());
        instructions.setText(receta.getInstructions());
        instructions.setMovementMethod(new ScrollingMovementMethod());

        // Glide para cargar imágenes desde URLs
        Glide.with(this).load(receta.getImage_uri()).into(image);
    }
}