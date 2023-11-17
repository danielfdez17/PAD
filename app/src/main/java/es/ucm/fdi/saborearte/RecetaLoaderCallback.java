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
        // Gestionar parametros opcionales
        if(args.containsKey(RecetaAPI.TIME_RANGE_PARAM)){
            timeString = args.getString(RecetaAPI.TIME_RANGE_PARAM);
        }
        return new RecetaLoader(mainActivity, queryString, new ArrayList<String>(), "", "", new ArrayList<String>(), new ArrayList<String>());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Receta>> loader, List<Receta> data) {
//        mainActivity.showBooks((data));
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Receta>> loader) {

    }
}
