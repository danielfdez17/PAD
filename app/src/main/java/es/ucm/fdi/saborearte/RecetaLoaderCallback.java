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
        String queryString = args.getString(RecetaAPI.QUERY_PARAM);
        String timeString = null;
        String excluded=null;
        String health=null;
        String mealtype=null;
        String cuisineType= null;
        ArrayList<String> listaBloqueados =null;
        ArrayList<String> tiposDeCocina=null;
        ArrayList<String> healthOptions=null;
        // Gestionar parametros opcionales
        if(args.containsKey(RecetaAPI.TIME_RANGE_PARAM)){
            timeString = args.getString(RecetaAPI.TIME_RANGE_PARAM);
        }
        if(args.containsKey(RecetaAPI.EXCLUDED_PARAM)){
            listaBloqueados=new ArrayList<>();

            excluded= args.getString(RecetaAPI.EXCLUDED_PARAM);
            String[] strSplit = excluded.split(",");
            for (String s : strSplit) {
                listaBloqueados.add(s);
            }

        }
        if(args.containsKey(RecetaAPI.HEALTH_PARAM)){
            healthOptions=new ArrayList<>();
            health = args.getString(RecetaAPI.HEALTH_PARAM);
            String[] strSplit = health.split(",");
            for (String s : strSplit) {
                healthOptions.add(s);
            }
        }
        if(args.containsKey(RecetaAPI.MEAL_TYPE_PARAM)){
            mealtype = args.getString(RecetaAPI.MEAL_TYPE_PARAM);
        }
        if(args.containsKey(RecetaAPI.CUISINE_TYPE_PARAM)){
            tiposDeCocina=new ArrayList<>();
            cuisineType = args.getString(RecetaAPI.CUISINE_TYPE_PARAM);
            String[] strSplit = cuisineType.split(",");
            for (String s : strSplit) {
               tiposDeCocina.add(s);
            }
        }

        return new RecetaLoader(mainActivity, queryString, listaBloqueados, timeString,mealtype, tiposDeCocina, healthOptions);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Receta>> loader, List<Receta> data) {
//        mainActivity.showBooks((data));
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Receta>> loader) {

    }
}
