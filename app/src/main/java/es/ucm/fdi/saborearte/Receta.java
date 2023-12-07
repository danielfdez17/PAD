package es.ucm.fdi.saborearte;
import android.media.Image;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

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
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jo = new JSONObject();
        JSONArray ja = new JSONArray();
        jo.put(TITULO, this.titulo);
        jo.put(IMAGE_URI,this.image_uri);
        jo.put(SOURCE_URI,this.source_uri);
        for (int i=0; i<this.ingredientes.size();i++){
            ja.put(this.ingredientes.get(i));
        }
        ja=new JSONArray();
        jo.put(INGREDIENTES,ja);
        jo.put(TIEMPO_PREPARACION,this.tiempoPreparacion);
        jo.put(CUISINE,this.cuisine);
        jo.put(MEAL_TYPE,this.mealTypes);
        for (int i=0; i<this.healthLabels.size();i++){
            ja.put(this.healthLabels.get(i));
        }
        jo.put(HEALTH_LABELS,ja);
        ja=new JSONArray();
        for (int i=0; i<this.instructions.size();i++){
            ja.put(this.instructions.get(i));
        }
        jo.put(INSTRUCCIONES,ja);
        return jo;
    }
    public static Receta toReceta(JSONObject jo) throws JSONException {
        Receta receta = null;
        String titulo = jo.getString(TITULO);
        String image_uri = jo.getString(IMAGE_URI);
        String source_uri = jo.getString(SOURCE_URI);
        ArrayList<String> ingredientes = new ArrayList<>();
        JSONArray ja = jo.getJSONArray(INGREDIENTES);
        for (int i = 0; i < ja.length(); ++i) {
            ingredientes.add(ja.getString(i));
        }
        int tiempo = jo.getInt(TIEMPO_PREPARACION);
        String cuisine = jo.getString(CUISINE);
        String mealType = jo.getString(MEAL_TYPE);
        ArrayList<String> healthLabels = new ArrayList<>();
        ja = jo.getJSONArray(HEALTH_LABELS);
        for (int i = 0; i < ja.length(); ++i) {
            healthLabels.add(ja.getString(i));
        }
        ArrayList<String> instrucciones = new ArrayList<>();
        ja = jo.getJSONArray(INSTRUCCIONES);
        for (int i = 0; i < ja.length(); ++i) {
            instrucciones.add(ja.getString(i));
        }
        return new Receta(titulo, image_uri, source_uri, ingredientes, tiempo, cuisine, mealType, healthLabels, instrucciones);
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
        if(jsonStringList == null)
            return null;

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
