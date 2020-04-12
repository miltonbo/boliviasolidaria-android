package com.jcode.apps.boliviasolidaria;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jcode.apps.boliviasolidaria.entity.BankAccount;
import com.jcode.apps.boliviasolidaria.entity.Receptor;
import com.jcode.apps.boliviasolidaria.web.Api;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoneyHelpActivity extends Activity {

    private LinearLayout lyItems;
    private ProgressDialog progres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_help);
        lyItems = findViewById(R.id.lyItems);

        progres = new ProgressDialog(this);
        progres.setTitle("Cargando los receptores de donaciones...");
        progres.show();

        Call<List<Receptor>> call = Api.getService().listReceptores();
        call.enqueue(new Callback<List<Receptor>>() {
            @Override
            public void onResponse(Call<List<Receptor>> call, Response<List<Receptor>> response) {
                progres.cancel();
                if (response.body() != null) {
                    List<Receptor> list = response.body();
                    lyItems.removeAllViews();
                    for (Receptor re : list) {
                        View view = getLayoutInflater().inflate(R.layout.item_receptor, null, true);
                        ((TextView) view.findViewById(R.id.tvNameReceptor)).setText(re.getEmpresa());
                        Glide.with(MoneyHelpActivity.this).load(Api.URL + "receptores-donacion/" + re.getId() + "/logo").into((ImageView) view.findViewById(R.id.ivReceptor));
                        LinearLayout lyAcc = view.findViewById(R.id.lyAccounts);
                        for (BankAccount ba : re.getCuentas()) {
                            View viewAcc = getLayoutInflater().inflate(R.layout.item_bank_account, null, true);
                            ((TextView) viewAcc.findViewById(R.id.tvTypeAccount)).setText(ba.getTipoCuenta());
                            ((TextView) viewAcc.findViewById(R.id.tvNameBank)).setText(ba.getBanco());
                            ((TextView) viewAcc.findViewById(R.id.tvNroAccount)).setText(ba.getNroCuenta());
                            ((TextView) viewAcc.findViewById(R.id.tvNameTitular)).setText(ba.getTitular());
                            ((TextView) viewAcc.findViewById(R.id.tvTypeDoc)).setText(ba.getDocumento() + ":");
                            ((TextView) viewAcc.findViewById(R.id.tvNroDc)).setText(ba.getNroDocumento());

                            viewAcc.findViewById(R.id.btnCopyNroAccount).setTag(ba.getNroCuenta());
                            viewAcc.findViewById(R.id.btnCopyNameTitular).setTag(ba.getTitular());
                            viewAcc.findViewById(R.id.btnCopyDoc).setTag(ba.getNroDocumento());
                            viewAcc.findViewById(R.id.btnCopyNroAccount).setOnClickListener(onClickCopy);
                            viewAcc.findViewById(R.id.btnCopyNameTitular).setOnClickListener(onClickCopy);
                            viewAcc.findViewById(R.id.btnCopyDoc).setOnClickListener(onClickCopy);

                            lyAcc.addView(viewAcc);
                        }

                        lyItems.addView(view);
                    }
                } else {
                    Toast.makeText(MoneyHelpActivity.this, "Fallo al descargar los receptores de donación.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Receptor>> call, Throwable t) {
                progres.cancel();
                Toast.makeText(MoneyHelpActivity.this, "Fallo al descargar los receptores de donación.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private View.OnClickListener onClickCopy = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String value = (String) v.getTag();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Texto copiado", value);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(MoneyHelpActivity.this, "Texto copiado", Toast.LENGTH_SHORT).show();
        }
    };

}
