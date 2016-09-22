package com.tests.michael.admin2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvDriver;
    ListView lvDrivers;
    EditText etBalance;
    Button button;

    String referer;
    String driver;
    String add;
    String[] driversList;
    String[] driverBalanceList;

    SharedPreferences sp;
    String scripts_host;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        sp = getSharedPreferences("admin_pref",MODE_PRIVATE);
        referer = sp.getString("login", "");
        scripts_host = sp.getString("scripts_host", "");

        tvDriver = (TextView) findViewById(R.id.tvDriver);
        etBalance = (EditText) findViewById(R.id.etBalance);
        lvDrivers = (ListView) findViewById(R.id.lvDrivers);
        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(this);
        tvDriver.setOnClickListener(this);

        lvDrivers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                driver = driversList[position];
                tvDriver.setText(driver);
                lvDrivers.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvDriver:
                getDrivers();
                lvDrivers.setVisibility(View.VISIBLE);
                adapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1, driverBalanceList);
                lvDrivers.setAdapter(adapter);
                break;
            case R.id.button:
                add = etBalance.getText().toString();
                String script = scripts_host + "add_balance.php";
                String result = "";
                try {
                    result = (new MyScript()).execute(script, referer, driver, add).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                if(result.equals("error")) {
                    result = "Ошибка !!!";
                    Toast toast = Toast.makeText(AddActivity.this, result, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    result = "Баланс пополнен на " + add + " грн.";
                    Toast toast = Toast.makeText(AddActivity.this, result, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    public void getDrivers() {
        String script = scripts_host + "get_drivers.php";
        String result = "";
        try {
            result = (new MyScript()).execute(script, referer).get();
            JSONObject jresult = new JSONObject(result);
            JSONArray jarrDrivers = jresult.getJSONArray("users");
            JSONArray jarrBalances = jresult.getJSONArray("balances");
            int count = jarrDrivers.length();
            driversList = new String[count];
            driverBalanceList = new String[count];
            for (int i=0; i<count; i++) {
                driversList[i] = jarrDrivers.getString(i);
                driverBalanceList[i] = driversList[i] + " [" + jarrBalances.getString(i) + "]";
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (result.equals("error")) {
            result = "Ошибка !!!";
            Toast toast = Toast.makeText(AddActivity.this, result, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public class MyScript extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... args) {
            return Script.toScript(args);
        }
    }

}
