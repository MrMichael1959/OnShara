package com.tests.michael.admin2;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class UserLogsActivity extends Activity implements View.OnClickListener {
    String referer;
    String driver;
    String day;
    String[] ordersList;
    String[] driversList;
    String[] driverBalanceList;
    String scripts_host;

    TextView tvDriver;
    TextView tvDate;
    ListView lvDrivers;
    ListView lvLogs;

    SharedPreferences sp;
    Calendar dateAndTime = Calendar.getInstance();
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_logs);

        sp = getSharedPreferences("admin_pref",MODE_PRIVATE);
        referer = sp.getString("login", "");
        scripts_host = sp.getString("scripts_host", scripts_host);

        tvDriver = (TextView) findViewById(R.id.tvDriver);
        tvDate = (TextView) findViewById(R.id.tvDate);
        lvDrivers = (ListView) findViewById(R.id.lvDrivers);
        lvLogs = (ListView) findViewById(R.id.lvLogs);

        tvDate.setOnClickListener(this);
        tvDriver.setOnClickListener(this);

        lvDrivers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                driver = driversList[position];
                tvDriver.setText(driver);
                getDriverLogs();
                showDriverLogs();
            }
        });
        setInitialDateTime();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvDate:
                setDate();
                break;
            case R.id.tvDriver:
                getDrivers();
                lvDrivers.setVisibility(View.VISIBLE);
                lvLogs.setVisibility(View.GONE);
                adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1, driverBalanceList);
                lvDrivers.setAdapter(adapter);
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
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
        if (result.equals("error")) {
            result = "Ошибка !!!";
            Toast toast = Toast.makeText(UserLogsActivity.this, result, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public void showDriverLogs() {
        lvDrivers.setVisibility(View.GONE);
        lvLogs.setVisibility(View.VISIBLE);
        ArrayAdapter<String> a = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, ordersList);
        lvLogs.setAdapter(a);
    }

    public void getDriverLogs() {
        String script = scripts_host + "driver_logs.php";
        String result = "";
        int count;
        try {
            result = (new MyScript()).execute(script, driver, referer, day).get();
            JSONObject jresult;
            JSONArray jarrBalance;
            JSONArray jarrRoute;
            try {
                jresult = new JSONObject(result);
                jarrRoute = jresult.getJSONArray("route");
                jarrBalance = jresult.getJSONArray("balance");
                count = jarrRoute.length();
                ordersList = new String[count];
                JSONObject eo;
                for (int i=0; i<count; i++) {
                    try {
                        eo = new JSONObject(jarrRoute.getString(i));
                        String nalPrice = eo.getString("op");
                        String bnPrice = eo.getString("oc");
                        String of = eo.getString("of");
                        String ot = eo.getString("ot");
                        String time = eo.getString("ost");
                        String callNumber = eo.getString("ocp");
                        if (eo.has("opp")) {
                            JSONArray opp = eo.getJSONArray("opp");
                            for (int j=0; j<opp.length(); j++) {
                                ot += "\n[через: " + opp.getJSONObject(j).getString("ad") + "]";
                            }
                        }
                        String balance = jarrBalance.getString(i);
                        String item = String.valueOf(i + 1) + ". ";
                        item += "[баланс: " + balance + "]\n";
                        item += "Откуда: " + of + "\n";
                        item += "Куда: " + ot + "\n";
                        item += "Нал: " + nalPrice + "\n";
                        item += "Безнал: " + bnPrice + "\n";
                        item += "На время: " + time + "\n";
                        item += "Телефон: " + callNumber+ "\n";
                        ordersList[i] = item;
                    } catch (JSONException e) {
                        ordersList[i] = "Error of data ...";
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (result.equals("error")) {
            result = "Ошибка !!!";
            Toast toast = Toast.makeText(UserLogsActivity.this, result, Toast.LENGTH_LONG);
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

    // отображаем диалоговое окно для выбора даты
    public void setDate() {
        new DatePickerDialog(UserLogsActivity.this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }
    // установка начальных даты и времени
    private void setInitialDateTime() {
        day = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dateAndTime.getTimeInMillis());
        String s = "Дата: " + day;
        tvDate.setText(s);
    }
    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
        }
    };

}
