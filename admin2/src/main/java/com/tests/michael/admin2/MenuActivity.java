package com.tests.michael.admin2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    String login;
    String password;

    Button btnRegistration;
    Button btnAdd;
    Button btnUserLogs;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        sp = getSharedPreferences("admin_pref",MODE_PRIVATE);

        btnRegistration = (Button) findViewById(R.id.btnRegistration);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnUserLogs = (Button) findViewById(R.id.btnUserLogs);
        btnRegistration.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnUserLogs.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegistration:
                startActivity(new Intent(this, RegistrationActivity.class));
                break;
            case R.id.btnAdd:
                startActivity(new Intent(this, AddActivity.class));
                break;
            case R.id.btnUserLogs:
                startActivity(new Intent(this, UserLogsActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAdminPreferences();
    }

    void loadAdminPreferences() {
        login = sp.getString("login", "");
        password = sp.getString("password", "");
    }
}
