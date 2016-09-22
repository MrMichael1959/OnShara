package com.onshara.michael;

import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SendPayment extends AsyncTask<String,Void,String> {
    @Override
    protected String doInBackground(String... args) {
        String resultString = "";
        String pars =   "driver="   + args[0] + "&" +
                        "password=" + args[1] + "&" +
                        "dt="       + args[2] + "&" +
                        "card="     + args[3] + "&" +
                        "cost="     + args[4];
        try {
            URL url = new URL("http://cities.j.scaleforce.net/shara/send_payment.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
//            conn.connect();
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
}