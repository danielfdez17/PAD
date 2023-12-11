package es.ucm.fdi.saborearte;
import android.media.Image;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;


public class Receta implements Serializable {
    private static final String TITULO = "titulo";
    private static final String IMAGE_URI = "image uri";
    private static final String SOURCE_URI = "source uri";
    private static final String INGREDIENTES = "ingredientes";
    private static final String TIEMPO_PREPARACION = "tiempo preparacion";
    private static final String CUISINE = "cuisine";
    private static final String MEAL_TYPE = "meal type";
    private static final String HEALTH_LABELS = "healthLabels";
    private static final String INSTRUCCIONES = "instrucciones";
    private static final String TAG = Receta.class.getSimpleName();
    private final String titulo;
    private final String image_uri;
    private final String source_uri;    // enlace a instrucciones
    private final ArrayList<String> ingredientes;
    private final int tiempoPreparacion;  // en minutos
    private final String cuisine;   // pais
    private final String mealTypes; // comida/cena/etc...
    private final ArrayList<String> healthLabels;
    private final ArrayList<String> instructions;

    // Constructor
    public Receta(String titulo, String image_uri, String source_uri, List<String> ingredientes, int tiempoPreparacion, String cuisine, String mealTypes, List<String> healthLabels, List<String> instructions) {
        this.titulo = titulo;
        this.image_uri = image_uri;
        this.source_uri = source_uri;
        this.ingredientes = new ArrayList<>(ingredientes);
        this.tiempoPreparacion = tiempoPreparacion;
        this.cuisine = cuisine;
        this.mealTypes = mealTypes;
        this.healthLabels = new ArrayList<>(healthLabels);
        this.instructions = new ArrayList<>(instructions);
    }
    // Getters
    public String getTitulo() {
        return titulo;
    }
    public String getImage_uri() {
        return image_uri;
    }
    public String getSource_uri() {
        return source_uri;
    }
    public int getTiempoPreparacion() {
        return tiempoPreparacion;
    }
    public String getCuisine() {
        return cuisine;
    }
    public String getMealTypes() {
        return mealTypes;
    }
    public ArrayList<String> getHealthLabels() {
        return healthLabels;
    }
    public String getIngredientes() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < ingredientes.size(); i++){
            sb.append("- " + ingredientes.get(i));
            sb.append("\n");
            sb.append("\n");
        }
        return sb.toString();
    }
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jo = new JSONObject();

        jo.put(TITULO, this.titulo);
        jo.put(IMAGE_URI,this.image_uri);
        jo.put(SOURCE_URI,this.source_uri);

        JSONArray ingredientesArray = new JSONArray(ingredientes);
        jo.put("ingredientes", ingredientesArray);

        jo.put(TIEMPO_PREPARACION,this.tiempoPreparacion);
        jo.put(CUISINE,this.cuisine);
        jo.put(MEAL_TYPE,this.mealTypes);
        JSONArray healthLabelsArray = new JSONArray(healthLabels);
        jo.put("health_labels", healthLabelsArray);

        JSONArray instructionsArray = new JSONArray(instructions);
        jo.put("instructions", instructionsArray);

        return jo;
    }
    public static Receta fromJSONObject(JSONObject jo) throws JSONException {
        String titulo = jo.getString(TITULO);
        String image_uri = jo.getString(IMAGE_URI);
        String source_uri = jo.getString(SOURCE_URI);
        JSONArray ingredientesArray = jo.getJSONArray("ingredientes");
        List<String> ingredientes = new ArrayList<>();
        for (int i = 0; i < ingredientesArray.length(); i++) {
            ingredientes.add(ingredientesArray.getString(i));
        }

        int tiempoPreparacion = jo.getInt(TIEMPO_PREPARACION);
        String cuisine = jo.getString(CUISINE);
        String mealType = jo.getString(MEAL_TYPE);

        JSONArray healthLabelsArray = jo.getJSONArray("health_labels");
        List<String> healthLabels = new ArrayList<>();
        for (int i = 0; i < healthLabelsArray.length(); i++) {
            healthLabels.add(healthLabelsArray.getString(i));
        }

        JSONArray instructionsArray = jo.getJSONArray("instructions");
        List<String> instructions = new ArrayList<>();
        for (int i = 0; i < instructionsArray.length(); i++) {
            instructions.add(instructionsArray.getString(i));
        }
        return new Receta(titulo, image_uri, source_uri, ingredientes, tiempoPreparacion, cuisine, mealType, healthLabels, instructions);
    }

    public static List<Receta> readRecetasFromStorage(Context context, String fileName) {
        List<Receta> recetas = new ArrayList<>();

        try {
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            String json = sb.toString();
            Gson gson = new Gson();

            Type listType = new TypeToken<ArrayList<Receta>>() {}.getType();
            recetas = gson.fromJson(json, listType);

            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return recetas;
    }

    public String getInstructions() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < instructions.size(); i++){
            sb.append((i + 1) + ". " + instructions.get(i));
            sb.append("\n");
            sb.append("\n");
        }
        return sb.toString();
    }
    public static List<Receta> fromJsonResponse(String jsonStringList) throws JSONException {
        if (jsonStringList == null || jsonStringList.isEmpty()) {
            return new ArrayList<>();
        }

        List<Receta> recetas = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(jsonStringList);

        int start = jsonObject.getInt("from");
        int end = jsonObject.getInt("to");

        JSONArray recipes = jsonObject.getJSONArray("hits");
        for(int i = 0; i < end-start; i++) {
            JSONObject index = recipes.getJSONObject(i);
            JSONObject recipe = index.getJSONObject("recipe");

            String uri = recipe.getString("uri");
            String title = recipe.getString("label");
            String image_uri = recipe.getString("image");
            String source_uri = recipe.getString("url");

            // Ingredientes
            ArrayList<String> ingredients = new ArrayList<>();
            JSONArray ingredientsArray = recipe.getJSONArray("ingredients");
            for(int x = 0; x < ingredientsArray.length(); x++){
                JSONObject ingredientInfo = ingredientsArray.getJSONObject(x);
                ingredients.add(ingredientInfo.getString("text"));
            }

            // Health Labels
            ArrayList<String> healthLabels = new ArrayList<>();
            JSONArray healthLabelsArray = recipe.getJSONArray("healthLabels");
            for(int x = 0; x < healthLabelsArray.length(); x++){
                healthLabels.add(healthLabelsArray.getString(x));
            }
            // Tiempo
            int total_time = recipe.getInt("totalTime");
            // Cuisine
            String cuisine = recipe.getJSONArray("cuisineType").getString(0);
            // Tipo
            String mealTypes = recipe.getJSONArray("mealType").getString(0);

            // Instructions
            ArrayList<String> instructions = new ArrayList<>();
            JSONArray instructionsArray = recipe.getJSONArray("instructionLines");
            for(int x = 0; x < instructionsArray.length(); x++){
                instructions.add(instructionsArray.getString(x));
            }

            recetas.add(new Receta(title, image_uri, source_uri, ingredients, total_time, cuisine, mealTypes, healthLabels, instructions));
        }

        return recetas;
    }
}
