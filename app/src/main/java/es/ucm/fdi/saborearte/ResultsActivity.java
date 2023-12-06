package es.ucm.fdi.saborearte;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

public class ResultsActivity extends AppCompatActivity {
    private static final String TAG = ResultsActivity.class.getSimpleName();
    public static int RECETA_LOADER_ID = 1;
    private RecyclerView resultsRecyclerView;
    private RecetaLoaderCallback recetaLoaderCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        resultsRecyclerView = findViewById(R.id.recyclerView);
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        recetaLoaderCallback = new RecetaLoaderCallback(this);
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        if(loaderManager.getLoader(RECETA_LOADER_ID) != null){
            loaderManager.initLoader(RECETA_LOADER_ID, null, recetaLoaderCallback);
        }

        Bundle queryBundle = getIntent().getBundleExtra("queryBundle");

        Log.i(TAG, "Creating results view");

        LoaderManager.getInstance(this).restartLoader(RECETA_LOADER_ID, queryBundle, recetaLoaderCallback);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return true;
    }

    public void updateBooksResultList(List<Receta> recetas) {
        Log.i(TAG, "Updating results view");
        if (recetas == null) {
            resultsRecyclerView.setAdapter(null);
        } else {
            RecetaResultListAdapter adapter = new RecetaResultListAdapter(this, recetas);
            resultsRecyclerView.setAdapter(adapter);
        }
    }
}