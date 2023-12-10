package es.ucm.fdi.saborearte;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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




public class InternalStorage {
    private final Context context;
    private final String archivo = "Recetas.json";
    private static final String TAG = InternalStorage.class.getSimpleName();

    public InternalStorage (Context context){
        this.context = context;
    }
    public void saveReceta(Receta receta){

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = this.context.openFileOutput(archivo, Context.MODE_PRIVATE);
            fileOutputStream.write(receta.toJSONObject().toString().getBytes());


        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
                Log.i(TAG, "fileOuptutStream closed");
            } catch (IOException e) {
                Log.i(TAG, "Exception when saving recipe\n" + e.getMessage());
                e.printStackTrace();
            }
        }


    }
    public List<Receta> readRecetas() {
        try {
            FileInputStream fileInputStream = this.context.openFileInput(archivo);
            JSONObject jo = new JSONObject(new JSONTokener(fileInputStream.toString()));
            return Receta.fromJsonResponse(jo.toString());
        }
        catch (Exception e) {

        }
        return null;
    }

    public void deleteReceta(Receta receta) {
        List<Receta> lista = this.readRecetas();
        if (lista.contains(receta)) {
            lista.remove(receta);
        }
    }


}
