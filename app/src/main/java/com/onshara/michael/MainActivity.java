package com.onshara.michael;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    TextView tvSystem;
    TextView tvPort;
    TextView tvService;
    TextView tvDriver;
    TextView tvOnTime;
    TextView tvCost;
    TextView tvDirs;
    TextView tv5min;
    Button btnChange;
    Button btnOnLine;
    Button btnPayment;

    SharedPreferences sp;
    String lastModified;
    String modifiedFile = "http://185.25.119.3/taxoid/shara.apk";
    String scripts_host = "http://185.25.119.3/taxoid/";

    final String SYSTEM = "system";
    final String PORT = "port";
    final String SERVICE = "service";
    final String DRIVER = "driver";
    final String ON_TIME = "on_time";
    final String ON_5MIN = "on_5min";
    final String COST = "cost";
    final String DIRS = "dirs";
    final String SHARA_PREF = "shara_pref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSystem = (TextView) findViewById(R.id.tvSystem);
        tvPort = (TextView) findViewById(R.id.tvPort);
        tvService = (TextView) findViewById(R.id.tvService);
        tvDriver = (TextView) findViewById(R.id.tvDriver);
        tvOnTime = (TextView) findViewById(R.id.tvOnTime);
        tv5min = (TextView) findViewById(R.id.tv5min);
        tvCost = (TextView) findViewById(R.id.tvCost);
        tvDirs = (TextView) findViewById(R.id.tvDirs);

        btnChange = (Button)findViewById(R.id.btnChange);
        btnOnLine = (Button)findViewById(R.id.btnOnLine);
        btnPayment = (Button)findViewById(R.id.btnPayment);
        btnChange.setOnClickListener(this);
        btnOnLine.setOnClickListener(this);
        btnPayment.setOnClickListener(this);

        sp = getSharedPreferences(SHARA_PREF,MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("scripts_host", scripts_host);
        ed.apply();

        if(checkUpdate()) {
            UpdateApp app = new UpdateApp();
            app.setContext(getApplicationContext());
            app.execute(modifiedFile);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnChange:
                startActivity(new Intent(this, SharaPreferencesActivity.class));
                break;
            case R.id.btnOnLine:
                startActivity(new Intent(this, OnLineActivity.class));
                break;
            case R.id.btnPayment:
                startActivity(new Intent(this, PaymentActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSharaPreferences();
    }

    void loadSharaPreferences() {
        String s = "Система: " + sp.getString(SYSTEM, "");
        tvSystem.setText(s);
        s = "Порт: " + sp.getString(PORT, "");
        tvPort.setText(s);
        s = "Служба: " + sp.getString(SERVICE, "");
        tvService.setText(s);
        s = "Позывной: " + sp.getString(DRIVER, "");
        tvDriver.setText(s);
        s = "Сумма: " + sp.getString(COST, "");
        tvCost.setText(s);
        tvDirs.setText(sp.getString(DIRS, " ").substring(1).replace(",",", "));

        if (sp.getBoolean(ON_TIME, false)) { tvOnTime.setText("Предварительные: Да"); }
        else { tvOnTime.setText("Предварительные: Нет"); }

        if (sp.getBoolean(ON_5MIN, false)) { tv5min.setText("На месте за 5мин.: Да"); }
        else { tv5min.setText("На месте за 5мин.: Нет"); }
    }

    boolean checkUpdate() {
        lastModified = sp.getString("lastModified", "");
        try {
            String s = (new LastModified()).execute(modifiedFile).get();
            if(lastModified.equals(s)) {
                return false;
            } else {
                lastModified = s;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        SharedPreferences.Editor ed = sp.edit();
        ed.putString("lastModified", lastModified);
        ed.apply();

        return true;
    }

}
