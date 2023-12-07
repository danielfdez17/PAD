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
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public class ResultsActivity extends AppCompatActivity {
    private static final String TAG = ResultsActivity.class.getSimpleName();
    public static int RECETA_LOADER_ID = 1;
    private RecyclerView resultsRecyclerView;
    private RecetaLoaderCallback recetaLoaderCallback;
    private TextView tvNoResults;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        resultsRecyclerView = findViewById(R.id.recyclerView);
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        tvNoResults = findViewById(R.id.tvNoResults);
        progressBar = findViewById(R.id.progressBar);

        recetaLoaderCallback = new RecetaLoaderCallback(this);
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        if(loaderManager.getLoader(RECETA_LOADER_ID) != null){
            loaderManager.initLoader(RECETA_LOADER_ID, null, recetaLoaderCallback);
        }

        Bundle queryBundle = getIntent().getBundleExtra("queryBundle");

        Log.i(TAG, "Creating results view");

        progressBar.setVisibility(View.VISIBLE);
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
        progressBar.setVisibility(View.GONE);
        Log.i(TAG, "Progress bar hidden");
        if (recetas == null || recetas.isEmpty()) {
            Log.i(TAG, "No results visible");
            tvNoResults.setVisibility(View.VISIBLE);
        } else {
            RecetaResultListAdapter adapter = new RecetaResultListAdapter(this, recetas);
            resultsRecyclerView.setAdapter(adapter);
        }
    }
}