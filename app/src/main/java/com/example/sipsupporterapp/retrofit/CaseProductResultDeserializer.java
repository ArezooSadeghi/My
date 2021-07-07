package com.example.sipsupporterapp.retrofit;

import com.example.sipsupporterapp.model.CaseProductResult;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class CaseProductResultDeserializer implements JsonDeserializer<CaseProductResult> {

    @Override
    public CaseProductResult deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject bodyObject = json.getAsJsonObject();
        Gson gson = new Gson();
        CaseProductResult caseProductResult = gson.fromJson(bodyObject.toString(), CaseProductResult.class);

        return caseProductResult;
    }
}
