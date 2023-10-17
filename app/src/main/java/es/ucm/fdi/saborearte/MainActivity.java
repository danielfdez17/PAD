package es.ucm.fdi.saborearte;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private RecyclerView rvRecetas;
    private List<Receta> recetas = new ArrayList<>();  // Puedes llenar esta lista como prefieras.
    private RecetaAdapter recetaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvRecetas = findViewById(R.id.rv_recetas);
        rvRecetas.setLayoutManager(new LinearLayoutManager(this));
        recetaAdapter = new RecetaAdapter(this, recetas);
        rvRecetas.setAdapter(recetaAdapter);
    }
}