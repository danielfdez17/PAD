package es.ucm.fdi.saborearte;

import androidx.appcompat.app.AppCompatActivity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static int RECETA_LOADER_ID = 1;
    private RecetaLoaderCallback recetaLoaderCallback;
    private RecetaResultListAdapter recetaAdapter;
    private EditText etIngredientesDisponibles;
    private EditText etIngredientesBloqueados;
    private RecyclerView rvRecetas;
    private List<Receta> recetas = new ArrayList<>();
    private String tiempo_maximo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_SaboreArte);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvRecetas = findViewById(R.id.rv_recetas);
        etIngredientesDisponibles = findViewById(R.id.et_ingredientes_disponibles);
        etIngredientesBloqueados = findViewById(R.id.et_ingredientes_bloqueados);
        EditText et_tiempo = findViewById(R.id.et_tiempo);
        tiempo_maximo = et_tiempo.getText().toString();

        et_tiempo.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                    (view, hourOfDay, minute) -> {
                        tiempo_maximo = String.format("%02d:%02d", hourOfDay, minute);
                        et_tiempo.setText(tiempo_maximo);
                    }, 0, 0, true);
            timePickerDialog.show();
        });

        setupChipsForIngredientesDisponibles();
        setupChipsForIngredientesBloqueados();
        setupRecyclerView();

        recetaLoaderCallback = new RecetaLoaderCallback(this);
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        if(loaderManager.getLoader(RECETA_LOADER_ID) != null){
            loaderManager.initLoader(RECETA_LOADER_ID, null, recetaLoaderCallback);
        }
        recetaAdapter = new RecetaResultListAdapter(this, new ArrayList<>());

        // Configuración del Spinner para las opciones de dieta
        MaterialSpinner spinnerDietaOpciones = findViewById(R.id.spinner_dieta_opciones);
        spinnerDietaOpciones.setItems("dairy-free", "gluten-free", "peanut-free", "pescatarian", "vegan", "vegetarian");
    }

    private void setupChipsForIngredientesDisponibles() {
        final ChipGroup chipGroupIngredientesDisponibles = findViewById(R.id.chip_group_ingredientes_disponibles);
        etIngredientesDisponibles.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                String nuevoIngrediente = etIngredientesDisponibles.getText().toString().trim();
                if (!nuevoIngrediente.isEmpty()) {
                    createChip(nuevoIngrediente, chipGroupIngredientesDisponibles, etIngredientesDisponibles, false);
                    return true;
                }
            }
            return false;
        });
    }

    private void setupChipsForIngredientesBloqueados() {
        final ChipGroup chipGroupIngredientesBloqueados = findViewById(R.id.chip_group_ingredientes_bloqueados);
        etIngredientesBloqueados.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                String nuevoIngredienteBloqueado = etIngredientesBloqueados.getText().toString().trim();
                if (!nuevoIngredienteBloqueado.isEmpty()) {
                    createChip(nuevoIngredienteBloqueado, chipGroupIngredientesBloqueados, etIngredientesBloqueados, true);
                    return true;
                }
            }
            return false;
        });
    }

    private void createChip(String text, ChipGroup chipGroup, EditText editText, boolean isBloqueado) {
        Chip chip;
        if (isBloqueado) {
            chip = new Chip(this, null, R.style.ChipIngredientesBloqueados);
        } else {
            chip = new Chip(this, null, R.style.ChipIngredientesDisponibles);
        }
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> chipGroup.removeView(chip));
        chipGroup.addView(chip);
        editText.getText().clear();
    }

    private void setupRecyclerView() {
        rvRecetas.setLayoutManager(new LinearLayoutManager(this));
        recetaAdapter = new RecetaResultListAdapter(this, recetas);
        rvRecetas.setAdapter(recetaAdapter);
    }

    public void searchRecetas(View view) {
        Log.i(TAG, "Search recetas btn clicked");
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connMgr.getActiveNetwork();
        NetworkCapabilities networkCap = connMgr.getNetworkCapabilities(network);
        if (networkCap == null) {
            // No hay conexión de red.
            Toast.makeText(this, R.string.internet_required, Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Convertir cosas seleccionadas en query

        Bundle queryBundle = new Bundle();
        queryBundle.putString(RecetaAPI.QUERY_PARAM, "chicken");

        // Parametros opcionales
//        queryBundle.putStringArrayList(RecipeAPI.EXCLUDED_PARAM, new ArrayList<String>());
//        queryBundle.putString(RecipeAPI.MEAL_TYPE_PARAM, "Breakfast");
//        queryBundle.putString(RecipeAPI.CUISINE_TYPE_PARAM, "American");
//        queryBundle.putString(RecipeAPI.TIME_RANGE_PARAM, "20");
//        queryBundle.putString(RecipeAPI.HEALTH_PARAM, "");

        LoaderManager.getInstance(this).restartLoader(RECETA_LOADER_ID, queryBundle, recetaLoaderCallback);
    }

    public void verFavoritos(View view) {
        Log.i(TAG, "Ver favoritos btn clicked");
    }
}
