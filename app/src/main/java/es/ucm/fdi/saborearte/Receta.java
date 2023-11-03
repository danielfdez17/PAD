package es.ucm.fdi.saborearte;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Receta {

    private final String titulo;
    private final String image_uri;
    private final String source_uri;    // enlace a instrucciones
    private final ArrayList<String> ingredientes;
    private final int tiempoPreparacion;  // en minutos
    private final String cuisine;   // pais
    private final String mealTypes; // comida/cena/etc...

    // Constructor
    public Receta(String titulo, String image_uri, String source_uri, ArrayList<String> ingredientes, int tiempoPreparacion, String cuisine, String mealTypes) {
        this.titulo = titulo;
        this.image_uri = image_uri;
        this.source_uri = source_uri;
        this.ingredientes = ingredientes;
        this.tiempoPreparacion = tiempoPreparacion;
        this.cuisine = cuisine;
        this.mealTypes = mealTypes;
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

    public ArrayList<String> getIngredientes() {
        return ingredientes;
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

    public static List<Receta> fromJsonResponse(String jsonStringList) throws JSONException {
        if(jsonStringList == null)
            return null;

        List<Receta> recetas = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(jsonStringList);
        int start = jsonObject.getInt("from");
        int end = jsonObject.getInt("to");

        JSONArray recipes = jsonObject.getJSONArray("hits");
        for(int i = 0; i < end-start; i++){
            JSONObject index = recipes.getJSONObject(i);
            JSONObject recipe = index.getJSONObject("recipe");

            String uri = recipe.getString("uri");
            String title = recipe.getString("label");
            String image_uri = recipe.getString("image");
            String source_uri = recipe.getString("url");
            ArrayList<String> ingredients = new ArrayList<>();
            JSONArray ingredientsArray = recipe.getJSONArray("ingredients");
            for(int x = 0; x < ingredientsArray.length(); x++){
                ingredients.add(ingredientsArray.getString(i));
            }
            int total_time = recipe.getInt("totalTime");
            String cuisine = recipe.getJSONArray("cuisineType").getString(0);
            String mealTypes = recipe.getJSONArray("mealType").getString(0);

            recetas.add(new Receta(title, image_uri, source_uri, ingredients, total_time, cuisine, mealTypes));
        }

        return recetas;
    }
}
