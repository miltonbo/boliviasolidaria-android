package com.jcode.apps.boliviasolidaria;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.jcode.apps.boliviasolidaria.entity.HelpRequest;
import com.jcode.apps.boliviasolidaria.utils.FBasicData;

import java.util.ArrayList;
import java.util.List;

public class Prefs extends FBasicData {

    protected Prefs(Context context) {
        super(context);
    }

    @Override
    protected String getNameDataBase() {
        return "BoliviaSolidaria";
    }

    public void addRequest(HelpRequest hr) {
        addObjectToList("list_help_requests", hr);
    }

    public List<HelpRequest> listRequests() {
        List<HelpRequest> list = readListObject("list_help_requests", new TypeToken<ArrayList<HelpRequest>>() {
        }.getType());
        return list;
    }

}
