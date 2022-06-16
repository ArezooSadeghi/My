package com.example.sipsupporterapp.retrofit;

import com.example.sipsupporterapp.model.CommentResult;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class CommentResultDeserializer implements JsonDeserializer<CommentResult> {

    @Override
    public CommentResult deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject bodyObject = json.getAsJsonObject();
        Gson gson = new Gson();
        return gson.fromJson(bodyObject.toString(), CommentResult.class);
    }
}
