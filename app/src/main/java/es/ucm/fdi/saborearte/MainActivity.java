package es.ucm.fdi.saborearte;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.jaredrummler.materialspinner.MaterialSpinner;
//import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    // variables for our buttons.
    private Button generarPDFReceta;

    // declaring width and height
    // for our PDF file.
    int pageHeight = 1120;
    int pagewidth = 792;

    // creating a bitmap variable
    // for storing our images
    private Bitmap bmp, scaledbmp;

    // constant code for runtime permissions
    private static final int PERMISSION_REQUEST_CODE = 200;

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

        // TODO: cuando se implemente el boton de marcar una receta como favorita, se
        // marcara como favorita y se generara un pdf con
//        this.generarPDFReceta = findViewById(R.id.btnDescargarReceta);
//        this.bmp = BitmapFactory.decodeResource(getResources(), R.drawable.);
//        this.scaledbmp = Bitmap.createScaledBitmap(this.bmp, 140, 140, false);
//        generarPDF();
//        this.generarPDFReceta.setOnClickListener(v -> {
//            generarPDF();
//        });


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       int id=item.getItemId();
       if(id==R.id.cambioCastellano){
           setLanguage("es");
           Toast.makeText(this,"Se ha cambiado de idioma al Español",Toast.LENGTH_SHORT).show();
           return true;
       }
       else if(id==R.id.cambioIngles){
           setLanguage("fr");
           Toast.makeText(this,"You change lenguage to Brithish English",Toast.LENGTH_SHORT).show();
           return true;
       }
       else if(id==R.id.modoClaro){
           Toast.makeText(this,"Day Mode",Toast.LENGTH_SHORT).show();
           return true;
       }
       else if(id==R.id.modoOscuro){
           Toast.makeText(this,"Dark Modes",Toast.LENGTH_SHORT).show();
           return true;
       }
       return false;
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

    public void verFavoritos(View view) {
        Log.i(TAG, "Ver favoritos btn clicked");
    }

    private void generarPDF() {
        // creating an object variable
        // for our PDF document.
        PdfDocument pdfDocument = new PdfDocument();

        // two variables for paint "paint" is used
        // for drawing shapes and we will use "title"
        // for adding text in our PDF file.
        Paint paint = new Paint();
        Paint title = new Paint();

        // we are adding page info to our PDF file
        // in which we will be passing our pageWidth,
        // pageHeight and number of pages and after that
        // we are calling it to create our PDF.
        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();

        // below line is used for setting
        // start page for our PDF file.
        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);

        // creating a variable for canvas
        // from our page of PDF.
        Canvas canvas = myPage.getCanvas();

        // below line is used to draw our image on our PDF file.
        // the first parameter of our drawbitmap method is
        // our bitmap
        // second parameter is position from left
        // third parameter is position from top and last
        // one is our variable for paint.
        canvas.drawBitmap(scaledbmp, 56, 40, paint);

        // below line is used for adding typeface for
        // our text which we will be adding in our PDF file.
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        // below line is used for setting text size
        // which we will be displaying in our PDF file.
        title.setTextSize(15);

        // below line is sued for setting color
        // of our text inside our PDF file.
        title.setColor(ContextCompat.getColor(this, R.color.white));

        // below line is used to draw text in our PDF file.
        // the first parameter is our text, second parameter
        // is position from start, third parameter is position from top
        // and then we are passing our variable of paint which is title.
        canvas.drawText("A portal for IT professionals.", 209, 100, title);
        canvas.drawText("Geeks for Geeks", 209, 80, title);

        // similarly we are creating another text and in this
        // we are aligning this text to center of our PDF file.
        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        title.setColor(ContextCompat.getColor(this, R.color.white));
        title.setTextSize(15);

        // below line is used for setting
        // our text to center of PDF.
        title.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("This is sample document which we have created.", 396, 560, title);

        // after adding all attributes to our
        // PDF file we will be finishing our page.
        pdfDocument.finishPage(myPage);

        // below line is used to set the name of
        // our PDF file and its path.
        File file = new File(Environment.getExternalStorageDirectory(), "GFG.pdf");

        try {
            // after creating a file name we will
            // write our PDF file to that location.
            pdfDocument.writeTo(new FileOutputStream(file));

            // below line is to print toast message
            // on completion of PDF generation.
            Toast.makeText(MainActivity.this, "PDF file generated successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // below line is used
            // to handle error
            e.printStackTrace();
        }
        // after storing our pdf to that
        // location we are closing our PDF file.
        pdfDocument.close();
    }
    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
}
