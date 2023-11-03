package es.ucm.fdi.saborearte;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

public class RecetaLoader extends AsyncTaskLoader<List<Receta>> {
    private String type;
    private String query;

    public RecetaLoader(Context context, String type, String query) {
        super(context);
        this.type = type;
        this.query = query;
    }
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<Receta> loadInBackground() {
        return null;
    }
}
