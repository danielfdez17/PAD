package es.ucm.fdi.saborearte;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;

import android.text.method.ScrollingMovementMethod;
import android.widget.ToggleButton;
import android.widget.Toolbar;

public class RecetaActivity extends AppCompatActivity {
    private static final String TAG = RecetaActivity.class.getSimpleName();
    private TextView title;
    private TextView time;
    private TextView ingredients;
    private TextView instructions;
    private ImageView image;
    private ToggleButton toggleButton;
    private InternalStorage internalStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receta);

        Log.i(TAG, "Receta view accessed");

        MaterialToolbar toolbar = (MaterialToolbar) findViewById(R.id.materialToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Receta receta = (Receta) getIntent().getSerializableExtra("receta");

        image = findViewById(R.id.receta_imagen);
        title = findViewById(R.id.receta_title);
        time = findViewById(R.id.receta_time);
        ingredients = findViewById(R.id.receta_ingredients);
        instructions = findViewById(R.id.receta_instructions);
        toggleButton = findViewById(R.id.toggleButton);
        internalStorage = new InternalStorage(this);

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

        ToggleButton favoriteBtn = (ToggleButton) findViewById(R.id.toggleButton);
        favoriteBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // TODO: add to favorites
                    Log.i(TAG, "Added to favorites");
                } else {
                    // TODO: remove from favorites
                    Log.i(TAG, "Removed from favorites");
                }
            }
        });
    }

    public void toggleButtonClicked(Receta receta) {
        // Si esta activo, se desea eliminar la receta
        if (this.toggleButton.isChecked()) {
            this.internalStorage.deleteReceta(receta);
        }
        // Si no esta activo, se desea guardar la receta
        else {
            this.internalStorage.saveReceta(receta);
        }
    }
}