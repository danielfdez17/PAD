package es.ucm.fdi.saborearte;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import com.google.gson.Gson;



public class InternalStorage {
    private final Context context;
    private final String archivo = "Recetas.json";
    private static final String TAG = InternalStorage.class.getSimpleName();

    public InternalStorage (Context context){
        this.context = context;
    }

    public List<Receta> readRecetas() {
        try {
            FileInputStream fileInputStream = this.context.openFileInput(archivo);
            int size = fileInputStream.available();
            byte[] buffer = new byte[size];
            fileInputStream.read(buffer);
            fileInputStream.close();

            String jsonString = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(jsonString);

            if (jsonString.isEmpty()) {
                Log.d(TAG, "El archivo está vacío o no se pudo leer correctamente");
            } else {
                return Receta.readRecetasFromStorage(context, archivo);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al leer recetas: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void deleteReceta(Receta receta) {
        List<Receta> recetas = readRecetas();
        if (recetas != null && recetas.contains(receta)) {
            recetas.remove(receta);
            saveRecetas(recetas);
        }
    }
    public void saveRecetas(List<Receta> recetas) {
        FileOutputStream fileOutputStream = null;
        try {
            Gson gson = new Gson();
            String json = gson.toJson(recetas);

            fileOutputStream = this.context.openFileOutput(archivo, Context.MODE_PRIVATE);
            fileOutputStream.write(json.getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
