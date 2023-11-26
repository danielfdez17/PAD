package es.ucm.fdi.saborearte;

import androidx.appcompat.app.AppCompatActivity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;



import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.jaredrummler.materialspinner.MaterialSpinner;
//import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static int RECETA_LOADER_ID = 1;
    private RecetaLoaderCallback recetaLoaderCallback;
    private RecetaResultListAdapter recetaAdapter;
    private EditText etIngredientesDisponibles;
    private EditText etIngredientesBloqueados;
    private EditText etTiempo;
    private RecyclerView rvRecetas;
    private List<Receta> recetas = new ArrayList<>();
    private String tiempo_maximo;
    private List<String> lista_ingredientes;
    private List<String> lista_bloqueados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_SaboreArte);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lista_ingredientes= new ArrayList<>();
        lista_bloqueados=new ArrayList<>();
        rvRecetas = findViewById(R.id.rv_recetas);
        etIngredientesDisponibles = findViewById(R.id.et_ingredientes_disponibles);
        etIngredientesBloqueados = findViewById(R.id.et_ingredientes_bloqueados);
        this.etTiempo = findViewById(R.id.et_tiempo);
        tiempo_maximo = this.etTiempo.getText().toString();

        this.etTiempo.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                    (view, hourOfDay, minute) -> {
                        tiempo_maximo = String.format("%02d:%02d", hourOfDay, minute);
                        this.etTiempo.setText(tiempo_maximo);
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

        //Configuración del Spinner para las opciones de dieta
        MaterialSpinner spinner = (MaterialSpinner) findViewById(R.id.spinner_dieta_opciones);
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.health_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Specify the layout to use when the list of choices appears.
        spinner.setAdapter(adapter);    // Apply the adapter to the spinner.
    }
    private void createChips(ChipGroup chipGroupIngredientesDisponibles, EditText eText, List<String> list){
        String ingString = eText.getText().toString().trim();
        if(ingString.isEmpty())
            return;
        String[] ingredientes = ingString.split(",");
        for(String s : ingredientes) {
            if(!list.contains(s)) {
                list.add(s);
                createChip(s, chipGroupIngredientesDisponibles, eText, false);
            }
        }
    }

    private void setupChipsForIngredientesDisponibles() {
        final ChipGroup chipGroupIngredientesDisponibles = findViewById(R.id.chip_group_ingredientes_disponibles);
        etIngredientesDisponibles.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                createChips(chipGroupIngredientesDisponibles, etIngredientesDisponibles, lista_ingredientes);
                return true;
            }
            return false;
        });
        etIngredientesDisponibles.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                createChips(chipGroupIngredientesDisponibles, etIngredientesDisponibles, lista_ingredientes);
            }
        });
    }

    private void setupChipsForIngredientesBloqueados() {
        final ChipGroup chipGroupIngredientesBloqueados = findViewById(R.id.chip_group_ingredientes_bloqueados);
        etIngredientesBloqueados.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                createChips(chipGroupIngredientesBloqueados, etIngredientesBloqueados, lista_bloqueados);
                return true;
            }
            return false;
        });
        etIngredientesBloqueados.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                createChips(chipGroupIngredientesBloqueados, etIngredientesBloqueados, lista_bloqueados);
            }
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
        chip.setOnCloseIconClickListener(v ->
        {
            if(isBloqueado){
                lista_bloqueados.remove(text);
            }
            else{
                lista_ingredientes.remove(text);
            }
            chipGroup.removeView(chip);
        });
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

        Bundle queryBundle = new Bundle();

        // INGREDIENTES DISPONIBLES / A INCLUIR
        if (lista_ingredientes.isEmpty()) {
            Toast.makeText(this, "¡No se puede comer del aire! :)", Toast.LENGTH_SHORT).show();
            return;
        }
        String ingredientes = "";
        for(String s : lista_ingredientes)
            ingredientes += s + ",";

        ingredientes = ingredientes.substring(0, ingredientes.length()-1);
        queryBundle.putString(RecetaAPI.QUERY_PARAM, ingredientes);

        // INGREDIENTES BLOQUEADOS
        ArrayList<String> ingBloqueados = new ArrayList<>();
        if (!this.lista_bloqueados.isEmpty()) {
            Log.i(TAG, "Existen ingredientes para bloquear");
            ingBloqueados = new ArrayList<>();
            for (String s : lista_bloqueados)
                ingBloqueados.add(s);
            Log.i(TAG, "Ingredientes bloqueados: " + ingBloqueados);
        }
        else {
            Log.i(TAG, "No se han bloqueado ingredientes");
        }
        queryBundle.putStringArrayList(RecetaAPI.EXCLUDED_PARAM, ingBloqueados);

        // TIEMPO MAXIMO
        String timeRange = "1";
        String maxTime = this.etTiempo.getText().toString();
        if (!maxTime.isEmpty()) {
            Log.i(TAG, "Hay tiempo maximo");
            String[] horasMinutos = maxTime.split(":");
            String horasStr = horasMinutos[0];
            String minutosStr = horasMinutos[1];
            if (!horasStr.equals("00") || !minutosStr.equals("00")) {
                int horas = Integer.parseInt(horasStr);
                int minutos = Integer.parseInt(minutosStr);
                minutos += (horas * 60);
                timeRange += ("-" + minutos);
            }
        } else {
            timeRange += "+";
        }
        Log.i(TAG, "Time range: " + timeRange);
        queryBundle.putString(RecetaAPI.TIME_RANGE_PARAM, timeRange);

        // TIPOS DE COMIDA
        String mealType = "";
//      mealType = this.etTipoComida.getText().toString();
        if (mealType.isEmpty()) {
            mealType = null;
        }
        else {
            Log.i(TAG, "Hay tipos de comida seleccionado");
        }
        queryBundle.putString(RecetaAPI.MEAL_TYPE_PARAM, mealType);

        // TIPOS DE COCINA
        String cuisineType = "";
//        cuisineType = this.etTipoCocina.getText().toString();
        cuisineType.replace(" ", "");
        ArrayList<String> tiposCocina = new ArrayList<>();

        if (!cuisineType.isEmpty()) {
            Log.i(TAG, "Hay tipos de comida seleccionados");
            tiposCocina = new ArrayList<String>();
            String[] strSplit = cuisineType.split(",");
            for (String s : strSplit) {
                tiposCocina.add(s);
            }
        }
        queryBundle.putStringArrayList(RecetaAPI.CUISINE_TYPE_PARAM, tiposCocina);

        // ALERGENOS
        MaterialSpinner dietaSpinner = findViewById(R.id.spinner_dieta_opciones);
        String health = dietaSpinner.getText().toString();
        Log.i(TAG, "Alergias seleccionadas: " + health);
        queryBundle.putString(RecetaAPI.HEALTH_PARAM, health);

        LoaderManager.getInstance(this).restartLoader(RECETA_LOADER_ID, queryBundle, recetaLoaderCallback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public void verFavoritos(View view) {
        Log.i(TAG, "Ver favoritos btn clicked");
    }
}
