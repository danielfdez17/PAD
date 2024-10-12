package es.ucm.fdi.saborearte;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;

import android.text.method.ScrollingMovementMethod;
import android.widget.ToggleButton;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class RecetaActivity extends AppCompatActivity {
    private static final String TAG = RecetaActivity.class.getSimpleName();
    private TextView title;
    private TextView time;
    private TextView ingredients;
    private TextView instructions;
    private ImageView image;
    private ToggleButton toggleButton;
    private InternalStorage internalStorage;
    private List<Receta> listaDeRecetas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receta);

        Log.i(TAG, "Receta view accessed");

        MaterialToolbar toolbar = (MaterialToolbar) findViewById(R.id.materialToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Receta receta = (Receta) getIntent().getSerializableExtra("receta");
        Log.d(TAG, "Receta recibida: " + receta.getTitulo());
        image = findViewById(R.id.receta_imagen);
        title = findViewById(R.id.receta_title);
        time = findViewById(R.id.receta_time);
        ingredients = findViewById(R.id.receta_ingredients);
        instructions = findViewById(R.id.receta_instructions);
        toggleButton = findViewById(R.id.toggleButton);
        internalStorage = new InternalStorage(this);
        listaDeRecetas = internalStorage.readRecetas();

        createActivity(receta);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return true;
    }

    public void verFavoritos(View view) {
        List<Receta> recetasFavoritas = internalStorage.readRecetas();

        if (recetasFavoritas != null && !recetasFavoritas.isEmpty()) {
            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            RecetaResultListAdapter adapter = (RecetaResultListAdapter) recyclerView.getAdapter();

            adapter.setRecetas(recetasFavoritas);
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "No hay recetas favoritas guardadas", Toast.LENGTH_SHORT).show();
        }
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

        toggleButton.setChecked(isRecetaFavorita(receta));
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    if (listaDeRecetas == null) {
                        listaDeRecetas = new ArrayList<>();
                    }

                    listaDeRecetas.add(receta);

                    internalStorage.saveRecetas(listaDeRecetas);
                } else {
                    List<Receta> newRecetaList = new ArrayList<>();

                    for(Receta r : listaDeRecetas){
                        if(!r.getSource_uri().equals(receta.getSource_uri())){
                            newRecetaList.add(r);
                        }
                    }

                    internalStorage.saveRecetas(newRecetaList);
                }
            }
        });
    }
    private boolean isRecetaFavorita(Receta receta) {
        List<Receta> listaDeRecetas = internalStorage.readRecetas();
        if (listaDeRecetas != null) {
            for(Receta r : listaDeRecetas){
                if(r.getSource_uri().equals(receta.getSource_uri())){
                    return true;
                }
            }
        }
        return false;
    }
}