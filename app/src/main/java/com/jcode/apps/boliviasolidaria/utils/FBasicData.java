package com.jcode.apps.boliviasolidaria.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Simple and Basic Data persistence for small Data, Its using Preferences for persistence,
 * It creates an emulated database with a Name and some basic methods and properties,
 * don`t allow querys.
 */
public abstract class FBasicData {

    private Context context;
    protected String name = "";
    private SharedPreferences prefs;
    private Gson gson;
    protected String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    protected FBasicData(Context context) {
        this.name = getNameDataBase();
        this.context = context;
        prefs = this.context.getSharedPreferences(name, Context.MODE_PRIVATE);
        this.gson = new GsonBuilder().setDateFormat(DATE_FORMAT).create();
    }

    protected abstract String getNameDataBase();

    public Object readObject(String nameObject, Class typeObject) throws JsonSyntaxException {
        String strValue = prefs.getString(nameObject, null);
        if (strValue != null) {
            return this.gson.fromJson(strValue, typeObject);
        }
        return null;
    }

    public ArrayList readListObject(String nameListObject, Type typeClass) throws JsonSyntaxException {
        String strValue = prefs.getString(nameListObject, "[]");
        return (ArrayList) this.gson.fromJson(strValue, typeClass);
    }

    public void writeObject(String nameObject, Object object) {
        String strValue = gson.toJson(object);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(nameObject, strValue);
        editor.commit();
    }

    public void addObjectToList(String nameListObject, Object newObject) {
        String strValue = prefs.getString(nameListObject, "[]");
        JsonArray jsArray = new JsonParser().parse(strValue).getAsJsonArray();
        jsArray.add(gson.toJsonTree(newObject));
        writeObject(nameListObject, jsArray.toString());
    }


    public JsonObject readJsonObject(String nameObject) throws JsonSyntaxException {
        String strValue = prefs.getString(nameObject, null);
        if (strValue != null) {
            return new JsonParser().parse(strValue).getAsJsonObject();
        }
        return null;
    }

    public JsonArray readJsonArray(String nameListObject) throws JsonSyntaxException {
        String strValue = prefs.getString(nameListObject, "[]");
        return new JsonParser().parse(strValue).getAsJsonArray();
    }

    public void writeJsonObject(String nameObject, JsonObject jsonObject) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(nameObject, jsonObject.toString());
        editor.commit();
    }

    public void addJsonObjectToArray(String nameListObject, JsonObject newJsonObject) {
        String strValue = prefs.getString(nameListObject, "[]");
        JsonArray jsArray = new JsonParser().parse(strValue).getAsJsonArray();
        JsonElement jsEle = new JsonParser().parse(newJsonObject.toString());
        jsArray.add(jsEle);
        writeObject(nameListObject, jsArray.toString());
    }
}
