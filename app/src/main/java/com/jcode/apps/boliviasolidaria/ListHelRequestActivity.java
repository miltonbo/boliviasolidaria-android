package com.jcode.apps.boliviasolidaria;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jcode.apps.boliviasolidaria.entity.BankAccount;
import com.jcode.apps.boliviasolidaria.entity.HelpRequest;
import com.jcode.apps.boliviasolidaria.entity.Receptor;
import com.jcode.apps.boliviasolidaria.web.Api;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListHelRequestActivity extends Activity {

    private LinearLayout lyItems;
    private ProgressDialog progres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_help_request);
        lyItems = findViewById(R.id.lyItems);

        progres = new ProgressDialog(this);
        progres.setTitle("Cargando solicitudes...");

    }

    private void loadRequests() {
        lyItems.removeAllViews();
        List<HelpRequest> list = new Prefs(this).listRequests();
        if (list.isEmpty()) {
            startActivity(new Intent(this, RequestActivity.class));
            finish();
            return;
        }

        for (HelpRequest hr : list) {
            View view = getLayoutInflater().inflate(R.layout.item_help_request, null, true);
            ((TextView) view.findViewById(R.id.tvName)).setText(hr.getNombre());
            ((TextView) view.findViewById(R.id.tvDesc)).setText(hr.getNecesidad());
            ((TextView) view.findViewById(R.id.tvCIPhone)).setText("CI: " + hr.getCi() + "  Telf: " + hr.getContacto());
            ((TextView) view.findViewById(R.id.tvAddress)).setText(hr.getDireccion());
            Glide.with(this).load(hr.getFotoCarnetLocal()).into((ImageView) view.findViewById(R.id.ivCIRequest));
            lyItems.addView(view);
        }
    }

    public void onClickNew(View view) {
        startActivity(new Intent(this, RequestActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRequests();
    }
}
