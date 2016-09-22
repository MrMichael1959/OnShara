package com.onshara.michael;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class OnLineActivity extends AppCompatActivity implements OnClickListener {

    final String city = "Харьков";
    final String script_host = "http://138.201.183.102/shara/";
    final String SYSTEM = "system";
    final String PORT = "port";
    final String SERVICE = "service";
    final String DRIVER = "driver";
    final String PASSWORD = "password";
    final String ON_TIME = "on_time";
    final String ON_5MIN = "on_5min";
    final String COST = "cost";
    final String DEVICE_ID = "device_id";
    final String DIRS = "dirs";
    final String SHARA_PREF = "shara_pref";

    TextView tvNET;
    TextView tvGPS;
    TextView tvAddress;
    TextView tvBalance;
    TextView tvPilot;
    TextView tvOnLine;
    TextView tvSectorInfo;
    TextView tvBodyOnLine;
    TextView tvOnPlace;
    Button btnPilot;
    Button btnToSector;
    Button btnFromSector;
    Button btnOnPlace;
    Button btnOnRoad;
    Button btnComplete;
    Button btnCall;
    LinearLayout llOnLine;
    LinearLayout llOnPlace;

    int id = 4;

    int sectorId = 0;
    int orderId = 0;
    boolean clickBtnOnPlace = false;
    boolean clickBtnOnRoad = false;
    boolean clickBtnComplete = false;
    boolean clickBtnToSector = false;
    boolean clickBtnFromSector = false;
    String status = "onLine";

    boolean pilot = false;
    boolean ontime = false;
    boolean on5min = false;

    double latitude = 0.0;
    double longitude = 0.0;
    long locationTime = 0L;
    double currlatitude = 0.0;
    double currlongitude = 0.0;
    long currlocationTime = 0L;
    String callNumber = null;
    String tvOnLineText = "";
    String system = "";
    String port = "";
    String service = "";
    String password = "";
    String driver = "";
    String referer = "";
    String user = "";
    String sdirs = "";
    String device_id = "";
    Double cost = 0.0;
    Double pay = 0.0;
    Double balance = 0.0;
    IdLtLn dirsLtLn[] = null;

    MediaPlayer mp = null;
    SharedPreferences sp;
    private LocationManager locationManager;
    Daemon daemon = new Daemon();

//--------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//--------------------------------------------------------------------------------------------------
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_line);

        mp = MediaPlayer.create(this, R.raw.order_accepted);

        tvNET = (TextView) findViewById(R.id.tvNET);
        tvGPS = (TextView) findViewById(R.id.tvGPS);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvOnLine = (TextView) findViewById(R.id.tvOnLine);
        tvSectorInfo = (TextView) findViewById(R.id.tvSectorInfo);
        tvBodyOnLine = (TextView) findViewById(R.id.tvBodyOnLine);
        tvOnPlace = (TextView) findViewById(R.id.tvOnPlace);
        tvBalance = (TextView) findViewById(R.id.tvBalance);
        tvPilot = (TextView) findViewById(R.id.tvPilot);
        btnPilot = (Button) findViewById(R.id.btnPilot);
        btnToSector = (Button) findViewById(R.id.btnToSector);
        btnFromSector = (Button) findViewById(R.id.btnFromSector);
        btnOnPlace = (Button) findViewById(R.id.btnOnPlace);
        btnOnRoad = (Button) findViewById(R.id.btnOnRoad);
        btnComplete = (Button) findViewById(R.id.btnComplete);
        btnCall = (Button) findViewById(R.id.btnCall);
        llOnLine = (LinearLayout) findViewById(R.id.llOnLine);
        llOnPlace = (LinearLayout) findViewById(R.id.llOnPlace);

        btnPilot.setOnClickListener(this);
        btnToSector.setOnClickListener(this);
        btnFromSector.setOnClickListener(this);
        btnOnPlace.setOnClickListener(this);
        btnOnRoad.setOnClickListener(this);
        btnComplete.setOnClickListener(this);
        btnCall.setOnClickListener(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        sp = getSharedPreferences(SHARA_PREF,MODE_PRIVATE);
        loadSharaPreferences();
        if (device_id.equals("")) {
            device_id = MyDevice.makeDeviceId();
            saveSharaPreferences();
        }

        tvOnLineText = "Предварительные: ";
        if(ontime) { tvOnLineText += "ДА\n"; } else { tvOnLineText += "НЕТ\n"; }
        tvOnLineText += "На месте за 5мин.: ";
        if(on5min) { tvOnLineText += "ДА\n"; } else { tvOnLineText += "НЕТ\n"; }
        tvOnLineText += "Сумма: " + String.valueOf(cost) + "\n\n";
//        tvOnLineText += sdirs.replace(",",", ");
        tvOnLine.setText(tvOnLineText);
//        dirsLtLn = Sector.getDirsLtLn(sdirs);
        dirsLtLn = Sector.getDirsLtLn("");

        daemon.execute();

    }
//--------------------------------------------------------------------------------------------------
    @Override
    protected void onResume() {
//--------------------------------------------------------------------------------------------------
        super.onResume();
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000 * 10, 10, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    1000 * 10, 10, locationListener);
        } catch(SecurityException e){}
    }

//--------------------------------------------------------------------------------------------------
    @Override
    protected void onPause() {
//--------------------------------------------------------------------------------------------------
        super.onPause();
        try {
            locationManager.removeUpdates(locationListener);
        } catch(SecurityException e){}
    }

//--------------------------------------------------------------------------------------------------
    @Override
    public void onBackPressed() {
//--------------------------------------------------------------------------------------------------
        super.onBackPressed();
        daemon.cancel(false);
    }

//--------------------------------------------------------------------------------------------------
    @Override
    public void onClick(View v) {
//--------------------------------------------------------------------------------------------------
        switch (v.getId()) {

            case R.id.btnPilot:
               if (pilot) {
                   pilot = false;
                   tvPilot.setTextColor(0xffcc0000);
                } else {
                   pilot = true;
                   tvPilot.setTextColor(0xFF99CC00);
                }
                break;

            case R.id.btnToSector:
                btnToSector.setVisibility(View.GONE);
                btnFromSector.setVisibility(View.VISIBLE);
                clickBtnToSector = true;
                break;

            case R.id.btnFromSector:
                btnToSector.setVisibility(View.VISIBLE);
                btnFromSector.setVisibility(View.GONE);
                clickBtnFromSector = true;
                break;

            case R.id.btnOnPlace:
                btnOnPlace.setVisibility(View.GONE);
                btnOnRoad.setVisibility(View.VISIBLE);
                btnComplete.setVisibility(View.GONE);
                clickBtnOnPlace = true;
                break;

            case R.id.btnOnRoad:
                btnOnPlace.setVisibility(View.GONE);
                btnOnRoad.setVisibility(View.GONE);
                btnComplete.setVisibility(View.VISIBLE);
                clickBtnOnRoad = true;
                break;

            case R.id.btnComplete:
                btnOnPlace.setVisibility(View.VISIBLE);
                btnOnRoad.setVisibility(View.GONE);
                btnComplete.setVisibility(View.GONE);
                clickBtnComplete = true;
                break;

            case R.id.btnCall:
                if (callNumber != null) {
                    String number = String.format("tel:%s", callNumber);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(number)));
                }
                break;

            default:
                break;
        }
    }

//--------------------------------------------------------------------------------------------------
    void saveSharaPreferences() {
//--------------------------------------------------------------------------------------------------
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(DEVICE_ID, device_id);
        ed.commit();
    }

//--------------------------------------------------------------------------------------------------
    void loadSharaPreferences() {
//--------------------------------------------------------------------------------------------------
        system = sp.getString(SYSTEM, "");
        port = sp.getString(PORT, "");
        service = sp.getString(SERVICE, "");
        driver = sp.getString(DRIVER, "");
        password = sp.getString(PASSWORD, "");
        cost = Double.valueOf(sp.getString(COST, "0.0"));
        sdirs = sp.getString(DIRS, ",").substring(1);
        ontime = sp.getBoolean(ON_TIME, false);
        on5min = sp.getBoolean(ON_5MIN, false);
        device_id = sp.getString(DEVICE_ID, "");
    }

//--------------------------------------------------------------------------------------------------
    String getAddress(){
//--------------------------------------------------------------------------------------------------
        if(currlatitude==0.0 || currlongitude==0.0) { return null; };
        Geocoder coder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        String address = null;
        try {
            addresses = coder.getFromLocation(currlatitude, currlongitude, 1);
            if (addresses==null || addresses.size()==0) { return null; }
            address = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

//**************************************************************************************************
    class Daemon extends AsyncTask<Void, String, Void> {
//**************************************************************************************************
        Socket socket = null;
        protected static final String server_IP = "136.243.10.152";
        private static final int server_Port = 9741;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... values) {
            init();
            publishProgress("balance");
            if (balance < 0) {
                publishProgress("badBalance", "У Вас отрицательный баланс: " +
                        String.valueOf(balance) + "\nПополните счет.");
                finish();
                return null;
            }
//            IdLtLn c = getCoords("Героев Труда 28");
//            IdLtLn c2 = getCoords2("Героев Труда 28");

            initSocket();
            requestSocket(MyJson.login(id - 2, driver, password, device_id), false);
            getData();
            requestSocket(MyJson.postLogin(id - 3), false);
            requestSocket(MyJson.getStatus(id - 1), false);
            manager();
            requestSocket(MyJson.sendCurrentLocationEmpty(id), true);
            requestSocket(MyJson.sendLocationStatEmpty(id), true);

            while (true) {
                if(isCancelled()) {
Log.d("Canceled ====>>>","true");
                    break;
                }
                double distance = Sector.getDistance(currlatitude, currlongitude, latitude, longitude);
                if (distance > 1.0) {
                    latitude = currlatitude;
                    longitude = currlongitude;
                    locationTime = currlocationTime;
                    requestSocket(MyJson.sendCurrentLocation(id, locationTime, latitude, longitude), true);
                    requestSocket(MyJson.sendLocationStat(id), true);
                    String sector = Sector.getMySector(latitude, longitude);
                    sectorId = Sector.getSectorsInfo().get(sector).id;
Log.d("Sector ====>>>",sector);
Log.d("SectorId ====>>>",String.valueOf(sectorId));
                }
                if (latitude==0.0 || longitude==0.0){ continue; }

                if (clickBtnToSector) {
                    requestSocket(MyJson.toSector(id, String.valueOf(sectorId)), true);
                    clickBtnToSector = false;
                }
                if (clickBtnFromSector) {
                    requestSocket(MyJson.leaveSector(id), true);
                    clickBtnFromSector = false;
                }

                manager();
                if (clickBtnOnPlace) {
                    String request = MyJson.toWaitingStatus(id, String.valueOf(orderId));
                    requestSocket(request, true);
                    clickBtnOnPlace = false;
                }
                if (clickBtnOnRoad) {
                    String request = MyJson.toDrivingStatus(id, String.valueOf(orderId));
                    requestSocket(request, true);
                    clickBtnOnRoad = false;
                }
                if (clickBtnComplete) {
                    String request = MyJson.completeOrder(id, String.valueOf(orderId));
                    requestSocket(request, true);
                    clickBtnComplete = false;
//                    publishProgress("complete");
                    break;
                }

                try { TimeUnit.MILLISECONDS.sleep(5); }
                catch (InterruptedException e) { e.printStackTrace(); }
            }

Log.d("Canceled ====>>>","Out");
            requestSocket(MyJson.logout(id), true);
            manager();
            finish();

            return null;
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            if(values[0].equals("badBalance")) {
                Toast toast = Toast.makeText(OnLineActivity.this,
                        values[1], Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            if(values[0].equals("mg")) {
                Toast toast = Toast.makeText(OnLineActivity.this,
                        values[1], Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            if(values[0].equals("balance")) {
                tvBalance.setText(tvBalance.getText() + String.valueOf(balance));
            }
            if(values[0].equals("tvSectorInfo")) { tvSectorInfo.setText(values[1]); }
            if(values[0].equals("tvBodyOnLine") && status.equals("onLine")) {
                llOnLine.setVisibility(View.VISIBLE);
                llOnPlace.setVisibility(View.GONE);
                tvBodyOnLine.setText(values[1]);
            }
            if(values[0].equals("tvOnPlace")) {
                llOnLine.setVisibility(View.GONE);
                llOnPlace.setVisibility(View.VISIBLE);
                tvOnPlace.setText(values[1]);
            }
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
        void initSocket() {
            try {
                socket = new Socket(server_IP, server_Port);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        void manager() {
            ArrayList<String> dataList = getData();
            try {
                for(String item : dataList){
                    if(item.equals("")) continue;
                    JSONObject jobj = new JSONObject(item);

                    if(jobj.has("mg"))  {
                        mgHandler(jobj);
                    }
                    if(jobj.has("chsc"))  {
                        chscHandler(jobj);
                    }
                    if(jobj.has("dr"))    {
                        drHandler(jobj);
                    }
                    if(jobj.has("nooa"))  {
                        nooaHandler(jobj);
                    }
                    if(jobj.has("po"))    {
                        poHandler(jobj);
                    }
                    if(jobj.has("npo"))   {
                        npoHandler(jobj);
                    }
                    if(jobj.has("eo"))    {
                        eoHandler(jobj);
                    };
                    if(jobj.has("rid"))   {
                        continue;
                    };

                    String response = MyJson.resp(id, jobj.getInt("id"));
                    requestSocket(response, true);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        void chscHandler(JSONObject obj) {
            try {
                JSONObject chsc = obj.getJSONObject("chsc");
                int sid = chsc.getInt("id");
                int dc = chsc.getInt("dc");
          } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        void drHandler(JSONObject obj) {
            try {
                JSONObject dr = obj.getJSONObject("dr");
                String data = "";
                if(dr.has("dsc")) {
                    String nm = dr.getJSONObject("dsc").getString("nm");
                    int sid = dr.getJSONObject("dsc").getInt("id");
                    int dc = dr.getJSONObject("dsc").getInt("dc");
                    int dsp = dr.getInt("dsp");
                    data = "Сектор: " + nm + " (" + String.valueOf(dc) + " )" + "\n";
                    data += "Очередь: " + String.valueOf(dsp);
                } else {
                    data = "Сектор: по городу";
                }
Log.d("======>>> ", data);
                publishProgress("tvSectorInfo", data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        void mgHandler(JSONObject obj) {
            try {
                String te = obj.getJSONObject("mg").getString("te");
Log.d("======>>> ", te);
                publishProgress("mg", te);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        void nooaHandler(JSONObject obj) {
            if (status.equals("assign")) { return; }
            try {
                String orderId = String.valueOf(obj.getJSONObject("nooa").getInt("id"));
                String op = obj.getJSONObject("nooa").getString("op");
                String oc = obj.getJSONObject("nooa").getString("oc");
                String ot = obj.getJSONObject("nooa").getString("ot");
                String of = obj.getJSONObject("nooa").getString("of");
//                int opt = obj.getJSONObject("nooa").getInt("opt");
Log.d("======>>>OrderType ", "nooa");
Log.d("======>>>OrderId ", orderId);
Log.d("======>>>OrderPrice ", op);
Log.d("======>>>Откуда ", parseAddress(of));
Log.d("======>>>Куда ", parseAddress(ot));
                String data = "Тип ордера: Эфир\n";
                data += "Номер ордера: " + orderId + "\n";
                data += "Стоимость: " + op + "\n";
                data += "Откуда: " + of + "\n";
                data += "Куда: " + ot;
                publishProgress("tvBodyOnLine", data);

                IdLtLn c = getCoords(parseAddress(of));
                if(c.lat==0.0 || c.lon==0.0) {
                    return;
                }
                Double d = Sector.getDistance(latitude, longitude, c.lat, c.lon);
Log.d("======>>>Coords From", String.valueOf(c.lat) + "  " + String.valueOf(c.lon));
Log.d("======>>>Distance ", String.valueOf(d));
                if(!pilot || Double.parseDouble(op)<cost || d>1.5) {
                    return;
                }
                if (dirsLtLn.length == 0) {
                    nooaAssign(id, orderId);
                    return;
                }
                c = getCoords(parseAddress(ot));
                if(c.lat==0.0 || c.lon==0.0) {
                    return;
                }
                for (int i=0; i<dirsLtLn.length; i++) {
                    d = Sector.getDistance(c.lat, c.lon, dirsLtLn[i].lat, dirsLtLn[i].lon);
Log.d("======>>>DirDistance ", String.valueOf(d));
                    if (d < 1.5) {
                        nooaAssign(id, orderId);
                        return;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        void poHandler(JSONObject obj) {
            try {
                String orderId = String.valueOf(obj.getJSONArray("po").getJSONObject(0).getInt("id"));
                String op = obj.getJSONArray("po").getJSONObject(0).getString("op");
                String ot = obj.getJSONArray("po").getJSONObject(0).getString("ot");
                String of = obj.getJSONArray("po").getJSONObject(0).getString("of");
                int opt = obj.getJSONArray("po").getJSONObject(0).getInt("opt");
                String sopt = "";
                if (opt == 1) { sopt = "BEST"; }
                if (opt == 2) { sopt = "PROPOSED"; }
                if (opt == 5) { sopt = "GPS"; }
                if (opt!=2 && opt!=5) { return; }
Log.d("======>>>OrderType ", sopt);
Log.d("======>>>OrderId ", orderId);
Log.d("======>>>OrderPrice ", op);
Log.d("======>>>Откуда ", parseAddress(of));
Log.d("======>>>Куда ", parseAddress(ot));
                String data = "Тип ордера: " + sopt + "\n";
                data += "Номер ордера: " + orderId + "\n";
                data += "Стоимость: " + op + "\n";
                data += "Откуда: " + of + "\n";
                data += "Куда: " + ot;
                publishProgress("tvBodyOnLine", data);

                IdLtLn c = getCoords(parseAddress(of));
                if(c.lat==0.0 || c.lon==0.0 || status.equals("assign")) {
Log.d("======>>>No coords From", "rejecting...");
                    rejectOrder(id, orderId, sopt);
                    return;
                }
                Double d = Sector.getDistance(latitude, longitude, c.lat, c.lon);
Log.d("======>>>Coords From", String.valueOf(c.lat) + "  " + String.valueOf(c.lon));
Log.d("======>>>Distance ", String.valueOf(d));
                if(!pilot || Double.parseDouble(op)<cost || d>1.5) {
                    rejectOrder(id, orderId, sopt);
Log.d("======>>>Order ", "rejecting...");
                    return;
                }
                if (dirsLtLn.length == 0) {
                    poAssign(id, orderId, sopt);
                    return;
                }
                c = getCoords(parseAddress(ot));
                if(c.lat==0.0 || c.lon==0.0) {
Log.d("======>>>No coords To", "rejecting...");
                    rejectOrder(id, orderId, sopt);
                    return;
                }
                for (int i=0; i<dirsLtLn.length; i++) {
                    d = Sector.getDistance(c.lat, c.lon, dirsLtLn[i].lat, dirsLtLn[i].lon);
Log.d("======>>>DirDistance ", String.valueOf(d));
                    if (d < 1.5) {
                        poAssign(id, orderId, sopt);
                        return;
                    }
                    else { rejectOrder(id, orderId, sopt); }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        void npoHandler(JSONObject obj) {
            if (status.equals("assign")) { return; }
//            orderType = "npo";
            try {
                String orderId = String.valueOf(obj.getJSONObject("npo").getInt("id"));
                String orderPrice = obj.getJSONObject("npo").getString("op");
                String ost = obj.getJSONObject("npo").getString("ost");
                String ot = obj.getJSONObject("npo").getString("ot");
                String of = obj.getJSONObject("npo").getString("of");
Log.d("======>>>OrderType ", "npo");
Log.d("======>>>OrderId ", orderId);
Log.d("======>>>OrderPrice ", orderPrice);
Log.d("======>>>Time ", ost);
Log.d("======>>>Of ", parseAddress(of));
Log.d("======>>>Ot ", parseAddress(ot));
                String data = "Тип ордера: Предварительный\n";
                data += "Номер ордера: " + orderId + "\n";
                data += "Стоимость: " + orderPrice + "\n";
                data += "На время: " + ost + "\n";
                data += "Откуда: " + of + "\n";
                data += "Куда: " + ot;
                publishProgress("tvBodyOnLine", data);

                if(!pilot || Double.parseDouble(orderPrice)<cost) { return; }
                if(!checkTime(ost)) { return; }

                IdLtLn c = getCoords(parseAddress(of));
                if(c.lat==0.0 || c.lon==0.0) {
Log.d("======>>>No coords From", "");
                    return;
                }
                Double d = Sector.getDistance(latitude, longitude, c.lat, c.lon);
                if(d > 1.5) {
Log.d("======>>>DirDistance ", String.valueOf(d));
                    return;
                }

                c = getCoords(parseAddress(ot));
                if(c.lat==0.0 || c.lon==0.0) {
Log.d("======>>>No coords To", "");
                    return;
                }
                if (dirsLtLn.length == 0) {
                    npoAssign(id, orderId);
                    return;
                }
                for (int i=0; i<dirsLtLn.length; i++) {
                    d = Sector.getDistance(dirsLtLn[i].lat, dirsLtLn[i].lon, c.lat, c.lon);
Log.d("======>>>DirDistance ", String.valueOf(d));
                    if (d < 1.5) {
                        npoAssign(id, orderId);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        void eoHandler(JSONObject obj) {
            if (status.equals("onLine")) mp.start();
            status = "assign";
            pilot = false;
            try {
                JSONObject eo = obj.getJSONObject("eo");
                orderId = eo.getInt("id");
                String nalPrice = eo.getString("op");
                String bnPrice = eo.getString("oc");
                String of = eo.getString("of");
                String ot = eo.getString("ot");
                String time = eo.getString("ost");
                callNumber = eo.getString("ocp");
                if (eo.has("opp")) {
                    JSONArray opp = eo.getJSONArray("opp");
                    for (int i=0; i<opp.length(); i++) {
                        ot += "\n[через: " + opp.getJSONObject(i).getString("ad") + "]";
                    }
                }
Log.d("======>>>OrderId ", String.valueOf(orderId));
Log.d("======>>>OrderPrice ", nalPrice);
Log.d("======>>>Beznal ", bnPrice);
Log.d("======>>>Of ", of);
Log.d("======>>>Ot ", ot);
Log.d("======>>>Time ", time);
Log.d("======>>>Phone ", callNumber);
                String data = "Номер ордера: " + String.valueOf(orderId) + "\n";
                data += "Стоимость: " + nalPrice + "\n";
                data += "Безнал: " + bnPrice + "\n";
                data += "Откуда: " + of + "\n";
                data += "Куда: " + ot + "\n";
                data += "На время: " + time + "\n";
                data += "Телефон: " + callNumber;
                publishProgress("tvOnPlace", data);

                String sBal = setBalance(String.valueOf(balance), String.valueOf(pay),
                        String.valueOf(orderId), eo.toString());
                balance = Double.parseDouble(sBal);
                publishProgress("balance");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        void rejectOrder(int id, String orderId, String opt) {
            String response = MyJson.rejectOrder(id, orderId, opt);
            requestSocket(response, true);
        }
        void nooaAssign(int id, String orderId) {
Log.d("=======>>","nooaAssign");
            String response = MyJson.acceptOrderAir(id, orderId, on5min);
            requestSocket(response, true);
        }
        void poAssign(int id, String orderId, String opt) {
Log.d("=======>>","poAssign");
            String response = MyJson.informOrderProposalShown(id, orderId);
            requestSocket(response, true);
            response = MyJson.acceptOrder(id, orderId, opt, on5min);
            requestSocket(response, true);
        }
        void npoAssign(int id, String orderId) {
Log.d("=======>>","npoAssign");
            String response = MyJson.bookPreliminary(id, orderId);
            requestSocket(response, true);
            response = MyJson.acceptPreliminary(id, orderId);
            requestSocket(response, true);
        }
        void requestSocket(String request, boolean add) {
            try {
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(request);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(add) id++;
        }
        ArrayList<String> getData() {
            ArrayList<String> list = new ArrayList<String>();
            try {
                DataInputStream in = new DataInputStream(socket.getInputStream());
                while (in.available() > 0) {
                    list.add(in.readUTF());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return list;
        }
        boolean checkTime(String orderTime) {
            String time = new SimpleDateFormat("HH:mm").format(new Date());
            String items[] = time.split(":");
            int i = Integer.parseInt(items[0]);
            String orderItems[] = orderTime.split(":");
            int orderI = Integer.parseInt(items[0]);
            if(orderI < i) orderI += 24;
            int itime1 = i * 60 + Integer.parseInt(items[1]);
            int itime2 = orderI * 60 + Integer.parseInt(orderItems[1]);
            int res = itime2 - itime1;
            if (res < 0) res = 60 + res;
            boolean b = false;
            if(res>=10 && res<=30) b = true;
Log.d("===>>> Запас времени:", String.valueOf(res) + String.valueOf(b));

            return b;
        }
        IdLtLn getCoords(String address) {
            String resultString = "";
            try {
                String myURL = "https://geocode-maps.yandex.ru/1.x/?format=json&geocode=";
                String par = URLEncoder.encode(city + ", " + address, "UTF-8");

                try {
                    URL url = new URL(myURL + par);
                    URLConnection conn = null;
                    conn = url.openConnection();
                    conn.connect();
                    if (((HttpURLConnection)conn).getResponseCode() != 200) {
                        return new IdLtLn(0, 0.0, 0.0);
                    }

                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    while ((bytesRead = conn.getInputStream().read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesRead);
                    }
                    resultString = new String(baos.toByteArray(), "UTF-8");
                } catch (MalformedURLException e) {
                    resultString = "MalformedURLException:" + e.getMessage();
                } catch (IOException e) {
                    resultString = "IOException:" + e.getMessage();
                } catch (Exception e) {
                    resultString = "Exception:" + e.getMessage();
                }
            } catch (Exception e) { e.printStackTrace(); }

            try {
                JSONObject obj = new JSONObject(resultString);
                resultString = obj.getJSONObject("response").
                        getJSONObject("GeoObjectCollection").
                        getJSONArray("featureMember").
                        getJSONObject(0).
                        getJSONObject("GeoObject").
                        getJSONObject("Point").
                        getString("pos");
            } catch (JSONException e) { e.printStackTrace(); }

            String[] coords = resultString.split(" ");

            Double lat = 0.0;
            Double lon = 0.0;
            try {
                lat = Double.parseDouble(coords[1]);
                lon = Double.parseDouble(coords[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return new IdLtLn(0, 0.0, 0.0);
            } catch (NullPointerException e) {
                e.printStackTrace();
                return new IdLtLn(0, 0.0, 0.0);
            }

            return new IdLtLn(0, lat, lon);
        }
    IdLtLn getCoords2(String address) {
        String resultString = "";
        try {
            String myURL = "http://maps.google.com/maps/api/geocode/json?";
            String par = "address=" + city + ", " + address + "&sensor=false";
            par = URLEncoder.encode( par, "UTF-8");
            try {
                URL url = new URL(myURL + par);
                URLConnection conn = null;
                conn = url.openConnection();
                conn.connect();
                int responseCode = ((HttpURLConnection)conn).getResponseCode();
                if ( responseCode != 200) {
                    return new IdLtLn(0, 0.0, 0.0);
                }

                byte[] buffer = new byte[8192];
                int bytesRead;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((bytesRead = conn.getInputStream().read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }
                resultString = new String(baos.toByteArray(), "UTF-8");
            } catch (MalformedURLException e) {
                resultString = "MalformedURLException:" + e.getMessage();
            } catch (IOException e) {
                resultString = "IOException:" + e.getMessage();
            } catch (Exception e) {
                resultString = "Exception:" + e.getMessage();
            }
        } catch (Exception e) { e.printStackTrace(); }

        try {
            JSONObject obj = new JSONObject(resultString);
            resultString = obj.getJSONObject("response").
                    getJSONObject("GeoObjectCollection").
                    getJSONArray("featureMember").
                    getJSONObject(0).
                    getJSONObject("GeoObject").
                    getJSONObject("Point").
                    getString("pos");
        } catch (JSONException e) { e.printStackTrace(); }

        String[] coords = resultString.split(" ");

        Double lat = 0.0;
        Double lon = 0.0;
        try {
            lat = Double.parseDouble(coords[1]);
            lon = Double.parseDouble(coords[0]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return new IdLtLn(0, 0.0, 0.0);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return new IdLtLn(0, 0.0, 0.0);
        }

        return new IdLtLn(0, lat, lon);
    }
        String toScript(String script, String pars) {
            String resultString = "";
            try {
                URL url = new URL(script);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                byte[] data = pars.getBytes("UTF-8");
                os.write(data); os.flush(); os.close();

                data = null;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                InputStream is = conn.getInputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) { baos.write(buffer, 0, bytesRead); }
                data = baos.toByteArray();
                baos.flush(); baos.close(); is.close();
                resultString = new String(data, "UTF-8");
                conn.disconnect();
            } catch (MalformedURLException e) { resultString = "MalformedURLException:" + e.getMessage();
            } catch (IOException e) { resultString = "IOException:" + e.getMessage();
            } catch (Exception e) { resultString = "Exception:" + e.getMessage();
            }
            return resultString;
        }
        void  init() {
            String script = script_host + "init.php";
            String pars = "par1=" + driver + "&" + "par2=" + password;
            String _user = toScript(script, pars);
            JSONObject juser;
            String sBal = "";
            String sPay = "";

            try {
                juser = new JSONObject(_user);
                sBal = juser.getString("balance");
                sPay = juser.getString("pay");
                user = juser.getString("user");
                referer = juser.getString("referer");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            balance = Double.parseDouble(sBal);
            pay = Double.parseDouble(sPay);
        }
        String  setBalance(String bal, String pay, String ord_id, String logs) {
            String script = script_host + "set_balance.php";
            String s = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss").format(new Date());
            String pars = "par1=" + user + "&"  + "par2=" + referer + "&" + "par3=" + s + "&"
                        + "par4=" + bal +  "&"  + "par5=" + pay +     "&" + "par6=" + ord_id + "&"
                        + "par7=" + logs;
            return toScript(script, pars);
        }
        boolean checkNumber(String address) {
            boolean b = false;
            for(int i=0; i<address.length(); i++) {
                String s = address.substring(i, i+1);
                if(s.equals("1") || s.equals("2") || s.equals("3") ||
                        s.equals("4") || s.equals("5") || s.equals("6") ||
                        s.equals("7") || s.equals("8") || s.equals("9")) {
                    b = true;
                    break;
                }
            }
            return b;
        }
        String parseAddress(String addr) {
            int begin1 = addr.indexOf("(");
            int end1 = addr.lastIndexOf(")");
            if (begin1==-1 || end1==-1) {
                return addr;
            }
            String sub = addr.substring(begin1 + 1, end1);
            int begin2 = sub.indexOf("(");
            int end2 = sub.indexOf(")");
            if (begin2==-1 || end2==-1) {
                if(checkNumber(sub)) return sub;
                return addr;
            }
            if(begin2 > end2) {
                String s1 = sub.substring(0, end2);
                String s2 = sub.substring(begin2 + 1);
                if(checkNumber(s1)) return s1;
                if(checkNumber(s2)) return s2;
            } else {
                return sub;
            }
            return "";
        }
    }
//**************************************************************************************************
    private LocationListener locationListener = new LocationListener() {
//**************************************************************************************************
        @Override
        public void onLocationChanged(Location location) {
            if (location == null) return;
            currlatitude = location.getLatitude();
            currlongitude = location.getLongitude();
            currlocationTime = location.getTime();
            String addr = getAddress();
            if(addr != null) {
                tvAddress.setText(addr);
            }
            if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
                tvGPS.setTextColor(0xFF99CC00);
                if(addr == null) {
                    String gps = "GPS: " + String.valueOf(currlatitude) + ", "
                                         + String.valueOf(currlongitude);
                    tvAddress.setText(gps);
                }
            }
            if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
                tvNET.setTextColor(0xFF99CC00);
                if (addr == null) {
                    String net = "NETWORK: " + String.valueOf(currlatitude) + ", "
                                             + String.valueOf(currlongitude);
                    tvAddress.setText(net);
                }
            }
        }
        @Override
        public void onProviderDisabled(String provider) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };
//**************************************************************************************************
}