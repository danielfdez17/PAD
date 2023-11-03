package es.ucm.fdi.saborearte;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RecipeAPI {
    private static final String TAG = RecipeAPI.class.getSimpleName();
    private final String API_KEY = "a0e3b768775cb4511efd7220925707bb";
    private final String API_APP = "3e6762bf";
    private final String QUERY_PARAM = "q";
    private final String APP_ID_PARAM = "app_id";
    private final String APP_KEY_PARAM = "app_key";
    private final String MEAL_TYPE_PARAM = "mealType";
    private final String CUISINE_TYPE_PARAM = "cuisineType";
    private final String TIME_RANGE_PARAM = "time";
    private final String EXCLUDED_PARAM = "excluded";
    private final String HEALTH_PARAM = "health";
    private final String FIELDS_PARAM = "field=uri&field=label&field=image&field=images&field=source&field=url&field=ingredientLines&field=calories&field=totalTime&field=cuisineType&field=mealType&field=healthLabels";
    private final String API_ENDPOINT = "https://www.edamam.com/#!/Recipe_Search/get_api_recipes_v2";

    public List<Receta> getRecetas(String ingredients, ArrayList<String> cuisineTypes, String mealType, String timeRange, ArrayList<String> excludedIngredientes, ArrayList<String> healthOptions) {
        Uri.Builder builder = Uri.parse(API_ENDPOINT).buildUpon()
                .appendQueryParameter(QUERY_PARAM, ingredients)
                .appendQueryParameter(APP_ID_PARAM, API_APP)
                .appendQueryParameter(APP_KEY_PARAM, API_KEY)
                .appendQueryParameter(MEAL_TYPE_PARAM, mealType)
                .appendQueryParameter(TIME_RANGE_PARAM, timeRange);

        for (String s : cuisineTypes) {
            builder = builder.appendQueryParameter(CUISINE_TYPE_PARAM, s);
        }
        for (String s : excludedIngredientes) {
            builder = builder.appendQueryParameter(EXCLUDED_PARAM, s);
        }
        for (String s : healthOptions) {
            builder = builder.appendQueryParameter(HEALTH_PARAM, s);
        }

        Uri builtURI = builder.appendEncodedPath(FIELDS_PARAM).build();

        Log.i(TAG, "API CALL: " + builtURI.toString());

        URL url = null;
        InputStream is = null;
        HttpURLConnection urlConnection = null;

        try {
            url = new URL(builtURI.toString());

            /* Open a connection to that URL. */
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);

            urlConnection.connect();

            int response = urlConnection.getResponseCode();

            is = urlConnection.getInputStream();
            String contentAsString = convertIsToString(is);

            return Receta.fromJsonResponse(contentAsString);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public String convertIsToString(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line + "\n");
        }
        if (builder.length() == 0) {
            return null;
        }
        return builder.toString();
    }
}
