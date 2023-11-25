package es.ucm.fdi.saborearte;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import java.util.ArrayList;
import java.util.List;

public class RecetaLoaderCallback implements LoaderManager.LoaderCallbacks<List<Receta>> {
    private static final String TAG = RecetaLoaderCallback.class.getSimpleName();

    private final MainActivity mainActivity;

    public RecetaLoaderCallback(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
    @NonNull
    @Override
    public Loader<List<Receta>> onCreateLoader(int id, Bundle args) {
        // INGREDIENTES DISPONIBLES
        String queryString = args.getString(RecetaAPI.QUERY_PARAM);
        String timeString = null;
        String mealtype = null;
        ArrayList<String> listaBloqueados = null;
        ArrayList<String> tiposDeCocina = null;
        ArrayList<String> healthOptions = null;
        // RANGO DE TIEMPO
        if (args.containsKey(RecetaAPI.TIME_RANGE_PARAM)){
            timeString = args.getString(RecetaAPI.TIME_RANGE_PARAM);
        }
        // INGREDIENTES EXCLUIDOS
        if (args.containsKey(RecetaAPI.EXCLUDED_PARAM)){
            listaBloqueados = args.getStringArrayList(RecetaAPI.EXCLUDED_PARAM);
        }
        // ALERGENOS
        if (args.containsKey(RecetaAPI.HEALTH_PARAM)){
            healthOptions = args.getStringArrayList(RecetaAPI.HEALTH_PARAM);
        }
        // TIPO DE COMIDA
        if (args.containsKey(RecetaAPI.MEAL_TYPE_PARAM)){
            mealtype = args.getString(RecetaAPI.MEAL_TYPE_PARAM);
        }
        // TIPO DE COCINA
        if (args.containsKey(RecetaAPI.CUISINE_TYPE_PARAM)){
            tiposDeCocina = args.getStringArrayList(RecetaAPI.CUISINE_TYPE_PARAM);
        }

        return new RecetaLoader(mainActivity, queryString, listaBloqueados, timeString, mealtype, tiposDeCocina, healthOptions);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Receta>> loader, List<Receta> data) {
//        mainActivity.showBooks((data));
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Receta>> loader) {

    }
}
