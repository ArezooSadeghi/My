package com.example.sipsupporterapp.retrofit;

import com.example.sipsupporterapp.model.AssignResult;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class AssignResultDeserializer implements JsonDeserializer<AssignResult> {

    @Override
    public AssignResult deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject bodyObject = json.getAsJsonObject();
        Gson gson = new Gson();
        AssignResult assignResult = gson.fromJson(bodyObject.toString(), AssignResult.class);

        return assignResult;
    }
}
