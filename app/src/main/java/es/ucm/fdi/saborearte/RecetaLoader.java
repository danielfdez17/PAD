package es.ucm.fdi.saborearte;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

public class RecetaLoader extends AsyncTaskLoader<List<Receta>> {
    private RecetaAPI api;
    private String query;
    private ArrayList<String> excludedIngredientes;
    private String timeRange;
    private String mealType;
    private ArrayList<String> cuisineTypes;
    private ArrayList<String> healthOptions;

    public RecetaLoader(Context context, String query,  ArrayList<String> excludedIngredientes, String timeRange, String mealType, ArrayList<String> cuisineTypes, ArrayList<String> healthOptions) {
        super(context);
        this.query = query;
        this.excludedIngredientes = excludedIngredientes;
        this.timeRange = timeRange;
        this.mealType = mealType;
        this.cuisineTypes = cuisineTypes;
        this.healthOptions = healthOptions;
        this.api = new RecetaAPI();
    }
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<Receta> loadInBackground() {
        return this.api.getRecetas(this.query, this.excludedIngredientes, this.timeRange, this.mealType, this.cuisineTypes, this.healthOptions);
    }
}
