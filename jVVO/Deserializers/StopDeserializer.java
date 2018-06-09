package jVVO.Deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import philippmatthes.com.manni.jVVO.Models.Stop;
import philippmatthes.com.manni.jVVO.WGSCoordinate;

public class StopDeserializer implements JsonDeserializer<Stop> {
    @Override
    public Stop deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String jsonString = json.getAsString();
        String[] components = jsonString.split("\\|");
        if (components.length != 7) {
            throw new JsonParseException("Illegal number of parameters for a Stop: " + components.length);
        }
        String id = components[0];
        String region = components[2].isEmpty() ? null : components[2];
        String name = components[3];
        try {
            Double x = Double.valueOf(components[5]);
            Double y = Double.valueOf(components[4]);
            WGSCoordinate location;
            if (x != 0 && y != 0) {
                location = new WGSCoordinate(x, y);
            } else {
                location = null;
            }
            return new Stop(id, name, region, location);
        } catch (NumberFormatException e) {
            throw new JsonParseException("Stop coordinates should be numeric values.");
        }
    }
}
