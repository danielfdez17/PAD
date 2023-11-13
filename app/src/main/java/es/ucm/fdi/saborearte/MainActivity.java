package es.ucm.fdi.saborearte;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private RecyclerView rvRecetas;
    private List<Receta> recetas = new ArrayList<>();  // Puedes llenar esta lista como prefieras.
    private List<String> ingredientes_disponibles = new ArrayList<>();
    private List<String> ingredientes_bloqueados = new ArrayList<>();
    private String tiempo_maximo;
    private RecetaResultListAdapter recetaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvRecetas = findViewById(R.id.rv_recetas);
        EditText et_ingredientes_disponibles = findViewById(R.id.et_ingredientes_disponibles);
        String et_string_dispo = et_ingredientes_disponibles.getText().toString();
        ingredientes_disponibles = new ArrayList<String>(Arrays.asList(et_string_dispo.split("\\s+")));
        EditText et_ingredientes_bloqueados = findViewById(R.id.et_ingredientes_bloqueados);
        String et_string_bloqueados = et_ingredientes_bloqueados.getText().toString();
        ingredientes_bloqueados = new ArrayList<String>(Arrays.asList(et_string_bloqueados.split("\\s+")));
        EditText et_tiempo = findViewById(R.id.et_tiempo);
        tiempo_maximo = et_tiempo.getText().toString();
        rvRecetas.setLayoutManager(new LinearLayoutManager(this));
        recetaAdapter = new RecetaResultListAdapter(this, recetas);
        rvRecetas.setAdapter(recetaAdapter);
    }
}