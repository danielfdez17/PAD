package es.ucm.fdi.saborearte;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Receta implements Serializable {
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
    public Receta(String titulo, String image_uri, String source_uri, ArrayList<String> ingredientes, int tiempoPreparacion, String cuisine, String mealTypes, ArrayList<String> healthLabels, ArrayList<String> instructions) {
        this.titulo = titulo;
        this.image_uri = image_uri;
        this.source_uri = source_uri;
        this.ingredientes = ingredientes;
        this.tiempoPreparacion = tiempoPreparacion;
        this.cuisine = cuisine;
        this.mealTypes = mealTypes;
        this.healthLabels = healthLabels;
        this.instructions = instructions;
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
        List<Receta> recetas = new ArrayList<>();
        if(jsonStringList == null)
            return recetas;

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
