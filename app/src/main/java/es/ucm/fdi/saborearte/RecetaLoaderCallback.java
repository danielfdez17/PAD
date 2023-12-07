package es.ucm.fdi.saborearte;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import java.util.ArrayList;
import java.util.List;

public class RecetaLoaderCallback implements LoaderManager.LoaderCallbacks<List<Receta>> {
    private static final String TAG = RecetaLoaderCallback.class.getSimpleName();

    private final ResultsActivity resultsActivity;

    public RecetaLoaderCallback(ResultsActivity resultsActivity) {
        this.resultsActivity = resultsActivity;
    }
    @NonNull
    @Override
    public Loader<List<Receta>> onCreateLoader(int id, Bundle args) {
        // INGREDIENTES DISPONIBLES
        String queryString = args.getString(RecetaAPI.QUERY_PARAM);
        // INGREDIENTES EXCLUIDOS
        ArrayList<String> listaBloqueados = args.getStringArrayList(RecetaAPI.EXCLUDED_PARAM);
        // RANGO DE TIEMPO
        String timeString = args.getString(RecetaAPI.TIME_RANGE_PARAM);
        // ALERGENOS
        String healthOption = args.getString(RecetaAPI.HEALTH_PARAM);
        // TIPO DE COMIDA
        String mealtype = args.getString(RecetaAPI.MEAL_TYPE_PARAM);
        // TIPO DE COCINA
        ArrayList<String> tiposDeCocina = args.getStringArrayList(RecetaAPI.CUISINE_TYPE_PARAM);

        return new RecetaLoader(resultsActivity, queryString, listaBloqueados, timeString, mealtype, tiposDeCocina, healthOption);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Receta>> loader, List<Receta> data) {
        ((ResultsActivity) resultsActivity).updateBooksResultList(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Receta>> loader) {

    }
}
