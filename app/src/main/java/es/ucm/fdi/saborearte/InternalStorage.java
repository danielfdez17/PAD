package es.ucm.fdi.saborearte;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;




public class InternalStorage {
    private final Context context;
    private final String archivo="Recetas.json";

    public InternalStorage (Context context){
        this.context =context;
    }
    public void saveJSON(Receta receta){

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream=context.openFileOutput("Recetas.json",Context.MODE_PRIVATE);
            fileOutputStream.write(receta.toJSONObject().toString().getBytes());


        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
    public ArrayList<Receta> readJSON(){
        try {
            Path filePath = context.getFilesDir().toPath().resolve(archivo);
            String jsonContent = new String(Files.readAllBytes(filePath));
            ObjectMapper objectMapper = null;
            return objectMapper.readTree(jsonContent);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


        return null;
    }


}
