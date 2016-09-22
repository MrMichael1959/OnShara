package com.onshara.michael;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Button;

public class SharaPreferencesActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etSystem;
    EditText etPort;
    EditText etService;
    EditText etDriver;
    EditText etPassword;
    EditText etCost;
    CheckBox cbOnTime;
    CheckBox cb5min;
    Button btnDirs;

    SharedPreferences sp;

    final String SYSTEM = "system";
    final String PORT = "port";
    final String SERVICE = "service";
    final String DRIVER = "driver";
    final String PASSWORD = "password";
    final String ON_TIME = "on_time";
    final String ON_5MIN = "on_5min";
    final String COST = "cost";
    final String SHARA_PREF = "shara_pref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shara_preferences);

        etSystem = (EditText) findViewById(R.id.etSystem);
        etPort = (EditText) findViewById(R.id.etPort);
        etService = (EditText) findViewById(R.id.etService);
        etDriver = (EditText) findViewById(R.id.etDriver);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etCost = (EditText) findViewById(R.id.etCost);
//        tvOnTime = (TextView) findViewById(R.id.tvOnTime);
//        tvDirs = (TextView) findViewById(R.id.tvDirs);
        cbOnTime = (CheckBox) findViewById(R.id.cbOnTime);
//        cbToEnd = (CheckBox) findViewById(R.id.cbToEnd);
        cb5min = (CheckBox) findViewById(R.id.cb5min);
        btnDirs = (Button) findViewById(R.id.btnDirs);

        btnDirs.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(this, MyDirectionsActivity.class));
    }

    @Override
    protected void onPause() {
        saveSharaPreferences();
        super.onPause();
    }

    @Override
    protected void onResume() {
        loadSharaPreferences();
        super.onResume();
    }


    void saveSharaPreferences() {
        sp = getSharedPreferences(SHARA_PREF,MODE_PRIVATE);
        Editor ed = sp.edit();
        ed.putString(SYSTEM, etSystem.getText().toString());
        ed.putString(PORT, etPort.getText().toString());
        ed.putString(SERVICE, etService.getText().toString());
        ed.putString(DRIVER, etDriver.getText().toString());
        ed.putString(PASSWORD, etPassword.getText().toString());
        ed.putBoolean(ON_TIME, cbOnTime.isChecked());
        ed.putBoolean(ON_5MIN, cb5min.isChecked());
            String s = etCost.getText().toString();
            if(s.equals("") || s==null) s = "0.0";
        ed.putString(COST, s);
        ed.commit();
    }

    void loadSharaPreferences() {
        sp = getSharedPreferences(SHARA_PREF,MODE_PRIVATE);
        etSystem.setText(sp.getString(SYSTEM, "kh"));
        etPort.setText(sp.getString(PORT, "9741"));
        etService.setText(sp.getString(SERVICE, "shara"));
        etDriver.setText(sp.getString(DRIVER, ""));
        etPassword.setText(sp.getString(PASSWORD, ""));
        cbOnTime.setChecked(sp.getBoolean(ON_TIME, false));
        cb5min.setChecked(sp.getBoolean(ON_5MIN, false));
        etCost.setText(sp.getString(COST, ""));
    }

}
