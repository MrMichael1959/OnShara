package com.tests.michael.admin2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etLogin;
    EditText etPassword;
    Button button;

    String login;
    String password;

    SharedPreferences sp;
    String lastModified;
    String modifiedFile = "http://sumo-ua.com/etaxi/admin2.apk";
    String scripts_host = "http://138.201.183.102/shara/admin/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("admin_pref",MODE_PRIVATE);

        etLogin = (EditText) findViewById(R.id.etLogin);
        etPassword = (EditText) findViewById(R.id.etPassword);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);

        if(checkUpdate()) {
            UpdateApp app = new UpdateApp();
            app.setContext(getApplicationContext());
            app.execute(modifiedFile);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAdminPreferences();
    }

    @Override
    public void onClick(View v) {
        login = etLogin.getText().toString();
        password = etPassword.getText().toString();
        String script = scripts_host + "check_admin.php";
        String result = "";
        try {
            result = (new MyScript()).execute(script, login, password).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(result.equals("error")) {
            result = "Вы не имеете доступа к админке !!!";
            Toast toast = Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            saveAdminPreferences();
            startActivity(new Intent(this, MenuActivity.class));
        }
    }

    void loadAdminPreferences() {
        login = sp.getString("login", "");
        password = sp.getString("password", "");
        etLogin.setText(login);
        etPassword.setText(password);
    }

    void saveAdminPreferences() {
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("login", login);
        ed.putString("password", password);
        ed.putString("scripts_host", scripts_host);
        ed.commit();
    }

    public class MyScript extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... args) {
            return Script.toScript(args);
        }
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        SharedPreferences.Editor ed = sp.edit();
        ed.putString("lastModified", lastModified);
        ed.commit();

        return true;
    }

}
