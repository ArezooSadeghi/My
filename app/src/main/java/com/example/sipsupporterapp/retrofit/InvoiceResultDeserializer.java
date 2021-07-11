package com.example.sipsupporterapp.retrofit;

import com.example.sipsupporterapp.model.InvoiceResult;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class InvoiceResultDeserializer implements JsonDeserializer<InvoiceResult> {

    @Override
    public InvoiceResult deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject bodyObject = json.getAsJsonObject();
        Gson gson = new Gson();
        InvoiceResult invoiceResult = gson.fromJson(bodyObject.toString(), InvoiceResult.class);

        return invoiceResult;
    }
}
