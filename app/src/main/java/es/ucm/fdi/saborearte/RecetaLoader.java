package es.ucm.fdi.saborearte;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

public class RecetaLoader extends AsyncTaskLoader<List<Receta>> {
    private static final String TAG = RecetaLoader.class.getSimpleName();
    private RecetaAPI api;
    private String query;
    private ArrayList<String> excludedIngredientes;
    private String timeRange;
    private String mealType;
    private ArrayList<String> cuisineTypes;
    private String healthOption;

    public RecetaLoader(Context context, String query,  ArrayList<String> excludedIngredientes, String timeRange, String mealType, ArrayList<String> cuisineTypes, String healthOption) {
        super(context);
        this.query = query;
        this.excludedIngredientes = excludedIngredientes;
        this.timeRange = timeRange;
        this.mealType = mealType;
        this.cuisineTypes = cuisineTypes;
        this.healthOption = healthOption;
        this.api = new RecetaAPI();
    }
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<Receta> loadInBackground() {
        return this.api.getRecetas(this.query, this.excludedIngredientes, this.timeRange, this.cuisineTypes, this.mealType, this.healthOption);
    }
}
