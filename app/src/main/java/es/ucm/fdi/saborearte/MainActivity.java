package es.ucm.fdi.saborearte;

import androidx.appcompat.app.AppCompatActivity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;


import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private EditText etIngredientesDisponibles;
    private EditText etIngredientesBloqueados;
    private EditText etTiempo;
    private String tiempo_maximo;
    private List<String> lista_ingredientes;
    private List<String> lista_bloqueados;
    private InternalStorage internalStorage;
    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";
    private String selectedDiet = "";
    private List<Receta> listaDeRecetas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        setTheme(R.style.Theme_SaboreArte);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.internalStorage = new InternalStorage(this);
        lista_ingredientes= new ArrayList<>();
        lista_bloqueados=new ArrayList<>();
        etIngredientesDisponibles = findViewById(R.id.et_ingredientes_disponibles);
        etIngredientesBloqueados = findViewById(R.id.et_ingredientes_bloqueados);
        this.etTiempo = findViewById(R.id.et_tiempo);
        tiempo_maximo = this.etTiempo.getText().toString();

        Button btnMostrarRecetasFavoritas = findViewById(R.id.btn_ver_favoritas);

        listaDeRecetas = internalStorage.readRecetas();

        // Crear una instancia del adaptador y pasar la lista de recetas y el contexto
        RecetaResultListAdapter adapter = new RecetaResultListAdapter(this, listaDeRecetas);

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

        //Configuración del Spinner para las opciones de dieta
        TextInputLayout layoutSpinnerDieta = findViewById(R.id.layout_spinner_dieta);
        Spinner spinnerDieta = layoutSpinnerDieta.findViewById(R.id.spinner_dieta_opciones);

        // Obtiene las opciones de dieta desde strings.xml
        String[] dietOptions = getResources().getStringArray(R.array.health_options);

        ArrayAdapter<String> dietAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dietOptions);
        dietAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDieta.setAdapter(dietAdapter);
    }

    public void verFavoritos(View view) {
        Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
        intent.putExtra("mostrarFavoritos", true);
        startActivity(intent);
    }

    private void createChips(ChipGroup chipGroup, EditText eText, List<String> list){
        String ingString = eText.getText().toString().trim();
        if(ingString.isEmpty())
            return;
        String[] ingredientes = ingString.split(",");
        for(String s : ingredientes) {
            if(!list.contains(s)) {
                list.add(s);
                createChip(s, chipGroup, eText, false);
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

    public void searchRecetas(View view) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connMgr.getActiveNetwork();
        NetworkCapabilities networkCap = connMgr.getNetworkCapabilities(network);
        if (networkCap == null) {
            // No hay conexión de red.
            Toast.makeText(this, R.string.internet_required, Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle queryBundle = new Bundle();

        if(lista_ingredientes.isEmpty() && !etIngredientesDisponibles.getText().equals("")){
            final ChipGroup chipGroupIngredientesDisponibles = findViewById(R.id.chip_group_ingredientes_disponibles);
            createChips(chipGroupIngredientesDisponibles, etIngredientesDisponibles, lista_ingredientes);
        }

        // INGREDIENTES DISPONIBLES / A INCLUIR
        if (lista_ingredientes.isEmpty()) {
            Toast.makeText(this, R.string.manadatoryTextEmpty, Toast.LENGTH_SHORT).show();
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
            ingBloqueados = new ArrayList<>();
            for (String s : lista_bloqueados)
                ingBloqueados.add(s);
        }
        else {
            Log.i(TAG, "No se han bloqueado ingredientes");
        }
        queryBundle.putStringArrayList(RecetaAPI.EXCLUDED_PARAM, ingBloqueados);

        // TIEMPO MAXIMO
        String timeRange = "1";
        String maxTime = this.etTiempo.getText().toString();
        if (!maxTime.isEmpty()) {
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
            tiposCocina = new ArrayList<String>();
            String[] strSplit = cuisineType.split(",");
            for (String s : strSplit) {
                tiposCocina.add(s);
            }
        }
        queryBundle.putStringArrayList(RecetaAPI.CUISINE_TYPE_PARAM, tiposCocina);

        // ALERGENOS
        TextInputLayout layoutSpinnerDieta = findViewById(R.id.layout_spinner_dieta);
        Spinner spinnerDieta = layoutSpinnerDieta.findViewById(R.id.spinner_dieta_opciones);
        String health = (String) spinnerDieta.getSelectedItem();
        queryBundle.putString(RecetaAPI.HEALTH_PARAM, health);

        clearFields();

        Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
        intent.putExtra("queryBundle", queryBundle);
        startActivity(intent);
    }

    private void clearFields(){
        etIngredientesDisponibles.setText("");
        etIngredientesBloqueados.setText("");
        lista_ingredientes.clear();
        lista_bloqueados.clear();
        removeAllChips(findViewById(R.id.chip_group_ingredientes_disponibles));
        removeAllChips(findViewById(R.id.chip_group_ingredientes_bloqueados));
    }
    private void removeAllChips(ChipGroup chipGroup) {
        int childCount = chipGroup.getChildCount();

        // Remove all chips in the ChipGroup
        for (int i = 0; i < childCount; i++) {
            View child = chipGroup.getChildAt(i);
            if (child instanceof Chip) {
                chipGroup.removeView(child);
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.modoClaro) {
            cambiarModo(false);
            return true;
        } else if (id == R.id.modoOscuro) {
            cambiarModo(true);
            return true;
        }

        // Tus otros casos de menú aquí...
        // Por ejemplo:
        // if (id == R.id.cambioCastellano) {
        //     setLanguage("es");
        //     Toast.makeText(this, "Se ha cambiado de idioma al Español", Toast.LENGTH_SHORT).show();
        //     return true;
        // } else if (id == R.id.cambioIngles) {
        //     setLanguage("en");
        //     Toast.makeText(this, "You change language to British English", Toast.LENGTH_SHORT).show();
        //     return true;
        // }

        return super.onOptionsItemSelected(item);
    }

    private void setLanguage(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resourses=getResources();
        Configuration conf=resourses.getConfiguration();
        conf.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(conf,resourses.getDisplayMetrics());
        recreate();
    }

    private void cambiarModo(boolean modoOscuro) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(PREF_DARK_THEME, modoOscuro);
        editor.apply();

        if (modoOscuro) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            Toast.makeText(this, R.string.nightModeActivated, Toast.LENGTH_SHORT).show();
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            Toast.makeText(this, R.string.dayModeActivated, Toast.LENGTH_SHORT).show();
        }

        recreate();
    }
}
