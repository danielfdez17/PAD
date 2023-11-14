package es.ucm.fdi.saborearte;

import androidx.appcompat.app.AppCompatActivity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TimePicker;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rvRecetas;
    private List<Receta> recetas = new ArrayList<>();
    private RecetaResultListAdapter recetaAdapter;
    private EditText etIngredientesDisponibles;
    private EditText etIngredientesBloqueados;
    private String tiempo_maximo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
}
