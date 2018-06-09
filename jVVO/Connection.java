package jVVO;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;


import org.json.JSONException;
import org.json.JSONObject;


import java.util.Date;
import java.util.Map;
import java.util.Optional;

import philippmatthes.com.manni.jVVO.Deserializers.SAPDateDeserializer;
import philippmatthes.com.manni.jVVO.Models.Stop;
import philippmatthes.com.manni.jVVO.Deserializers.StopDeserializer;

public class Connection {

    public static void makeRequest(
            String url,
            Map<String, Object> data,
            Response.Listener<JSONObject> listener,
            Response.ErrorListener errorListener,
            RequestQueue queue) {
        String jsonedDataString = new Gson().toJson(data);
        try {
            JSONObject jsonData = new JSONObject(jsonedDataString);
            JsonObjectRequest request = new JsonObjectRequest(url, jsonData, listener, errorListener);
            queue.add(request);
        } catch (UnsatisfiedLinkError | JSONException e) {
            errorListener.onErrorResponse(new VolleyError(e.getMessage()));
        }
    }

    public static <T> void post(
            String url,
            Map<String, Object> data,
            Response.Listener<Result<T>> responseListener,
            Class<T> clazz,
            RequestQueue queue
    ) {
        // Register custom deserializers
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);

        gsonBuilder.registerTypeAdapter(Stop.class, new StopDeserializer());
        gsonBuilder.registerTypeAdapter(Date.class, new SAPDateDeserializer());

        Gson gson = gsonBuilder.create();

        makeRequest(url, data,
                response -> {
                    JsonElement element = gson.fromJson(response.toString(), JsonElement.class);
                    System.out.println("Received JSON: " + element.toString());
                    T typeResponse = gson.fromJson(element, clazz);
                    Result<T> result = new Result<>(Optional.of(typeResponse), Optional.empty());
                    responseListener.onResponse(result);
                }, error -> {
                    Result<T> result = new Result<>(Optional.empty(), Optional.of(DVBError.response));
                    responseListener.onResponse(result);
                },
                queue
        );
    }
}
