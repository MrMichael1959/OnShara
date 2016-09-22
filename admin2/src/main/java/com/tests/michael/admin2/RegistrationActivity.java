package com.tests.michael.admin2;

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

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etName;
    EditText etLogin;
    EditText etPassword;
    EditText etBalance;
    EditText etPhone;
    Button btnRegistration;
    ProgressBar progressBar;

    String user_name;
    String user_login;
    String user_password;
    String user_balance;
    String user_phone;
    String login;
    String password;
    String scripts_host;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        etName = (EditText) findViewById(R.id.etName);
        etLogin = (EditText) findViewById(R.id.etLogin);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etBalance = (EditText) findViewById(R.id.etBalance);
        etPhone = (EditText) findViewById(R.id.etPhone);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnRegistration = (Button) findViewById(R.id.btnRegistration);
        btnRegistration.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        user_name = etName.getText().toString();
        user_login = etLogin.getText().toString();
        user_password = etPassword.getText().toString();
        user_balance = etBalance.getText().toString();
        user_phone = etPhone.getText().toString();
        String script = scripts_host + "registration.php";
        String result = "";
        try {
            result = (new MyScript()).execute(script, login, password,
                    user_name, user_login, user_password, user_balance, user_phone).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (result.equals("exist")) {
            result = "Такой водитель уже существует !!!";
            Toast toast = Toast.makeText(RegistrationActivity.this, result, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        if (result.equals("error")) {
            result = "Ошибка. Повторите попытку !!!";
            Toast toast = Toast.makeText(RegistrationActivity.this, result, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        if (result.equals("success")) {
            result = "Регистрация прошла успешно !!!";
            Toast toast = Toast.makeText(RegistrationActivity.this, result, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sp = getSharedPreferences("admin_pref",MODE_PRIVATE);
        login = sp.getString("login", "");
        password = sp.getString("password", "");
        scripts_host = sp.getString("scripts_host", scripts_host);
    }

    public class MyScript extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... args) {
            return Script.toScript(args);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
        }
    }
}
