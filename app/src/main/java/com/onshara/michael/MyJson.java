package com.onshara.michael;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyJson {

    public static String login(int id, String driver, String password, String device_id){
        JSONObject obj_Params = new JSONObject();
        JSONObject obj_Login = new JSONObject();
        String str = "";
        try {
            obj_Params.put("loginTime",makeTime());
            obj_Params.put("driverCallsign",driver);
            obj_Params.put("cityName","kh");
            obj_Params.put("dsName","shara");
            obj_Params.put("useGpsForSectorAssigning","NEVER");
            obj_Params.put("useGpsTaximeter","false");
            obj_Params.put("clientVersion","ad-1.22.10273");
            obj_Params.put("deviceId",device_id);
            obj_Params.put("password",password);
            obj_Params.put("protocolVersion","11");
            obj_Params.put("useOrderToFieldForSectorAssigning","NEVER");

            obj_Login.put("id", id);
            obj_Login.put("request", "login");
            obj_Login.put("params", obj_Params);
            str = obj_Login.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String postLogin(int id) {
        JSONObject obj_Params = new JSONObject();
        JSONObject obj_PostLogin = new JSONObject();
        String str = "";
        try {
            obj_Params.put("currentTimezone","Europe/Kiev");
            obj_Params.put("deviceInfo","ZGV2aWNlOks5MTBMIGJyYW5kOkxlbm92byBkaXNwbGF5Oks5MTBMX1MyMTlfMTQxMjExIGhhcmR3YXJlOnFjb20gbWFudWZhY3R1cmVyOkxFTk9WTyBtb2RlbDpMZW5vdm8gSzkxMEwgdXNlcjpidWlsZHNsYXZlIHZlcnNpb25DTjpSRUwgdmVyc2lvblNJOjE5IHZlcnNpb25SbDo0LjQuMiBtZW06MjQzNDkxNTIvMjU1NzU0MjQgZGlzcGxheVNpemU6MTA4MHgxOTIwIGRwaToxNjAuNDIxeDE1OS44OTUg");

            obj_PostLogin.put("id", id);
            obj_PostLogin.put("request", "postLogin");
            obj_PostLogin.put("params", obj_Params);
            str = obj_PostLogin.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String sendCurrentLocationEmpty(int id) {
        JSONObject obj_Params = new JSONObject();
        JSONObject obj_CurrLoc = new JSONObject();
        String str = "";
        try {
            obj_Params.put("locationData", "");

            obj_CurrLoc.put("id", id);
            obj_CurrLoc.put("request", "sendCurrentLocation");
            obj_CurrLoc.put("params", obj_Params);
            str = obj_CurrLoc.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String sendCurrentLocation(int id, long time, double lat, double lng) {
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
        try {
            localDataOutputStream.writeLong(time);
            localDataOutputStream.writeFloat((float)lat);
            localDataOutputStream.writeFloat((float)lng);
            localDataOutputStream.writeFloat((float)0.0);
            localDataOutputStream.writeFloat((float)0.0);
            localDataOutputStream.writeFloat((float)1.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] ba = localByteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(ba, 2);

        JSONObject obj_Params = new JSONObject();
        JSONObject obj_CurrLoc = new JSONObject();
        String str = "";
        try {
            obj_Params.put("locationData", encoded);

            obj_CurrLoc.put("id", id);
            obj_CurrLoc.put("request", "sendCurrentLocation");
            obj_CurrLoc.put("params", obj_Params);
            str = obj_CurrLoc.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String sendLocationStatEmpty(int id) {
        JSONObject obj_Params = new JSONObject();
        JSONObject obj_LocStat = new JSONObject();
        String str = "";
        try {
            obj_Params.put("locationStat", "");

            obj_LocStat.put("id", id);
            obj_LocStat.put("request", "sendLocationStat");
            obj_LocStat.put("params", obj_Params);
            str = obj_LocStat.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String sendLocationStat(int id) {
        JSONObject obj_Params = new JSONObject();
        JSONObject obj_LocStat = new JSONObject();
        String str = "";
        try {
            obj_Params.put("locationStat", "gps(<10:2, <30:0, <100:0, <300:0, >300:0, avg:1.0), network(<10:1, <30:0, <100:0, <300:0, >300:0, avg:1.0)");

            obj_LocStat.put("id", id);
            obj_LocStat.put("request", "sendLocationStat");
            obj_LocStat.put("params", obj_Params);
            str = obj_LocStat.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String toWaitingStatus(int id, String orderId) {
        JSONObject obj_Params = new JSONObject();
        JSONObject obj = new JSONObject();
        String str = "";
        try {
            obj_Params.put("orderId", orderId);

            obj.put("id", id);
            obj.put("request", "toWaitingStatus");
            obj.put("params", obj_Params);
            str = obj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String toDrivingStatus(int id, String orderId) {
        JSONObject obj_Params = new JSONObject();
        JSONObject obj = new JSONObject();
        String str = "";
        try {
            obj_Params.put("orderId", orderId);

            obj.put("id", id);
            obj.put("request", "toDrivingStatus");
            obj.put("params", obj_Params);
            str = obj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String completeOrder(int id, String orderId) {
        JSONObject obj_Params = new JSONObject();
        JSONObject obj = new JSONObject();
        String str = "";
        try {
            obj_Params.put("orderId", orderId);
            obj_Params.put("completionStatusId", "62");

            obj.put("id", id);
            obj.put("request", "completeOrder");
            obj.put("params", obj_Params);
            str = obj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String toSector(int id, String sectorId) {
        JSONObject obj_Params = new JSONObject();
        JSONObject obj = new JSONObject();
        String str = "";
        try {
            obj_Params.put("sectorId", sectorId);

            obj.put("id", id);
            obj.put("request", "toSector");
            obj.put("params", obj_Params);
            str = obj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String leaveSector(int id) {
        JSONObject obj = new JSONObject();
        String str = "";
        try {
            obj.put("id", id);
            obj.put("request", "leaveSector");
            obj.put("params", new JSONObject());
            str = obj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String getStatus(int id) {
        JSONObject obj_Status = new JSONObject();
        String str = "";
        try {
            obj_Status.put("id", id);
            obj_Status.put("request", "getStatus");
            obj_Status.put("params", new JSONObject());
            str = obj_Status.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String logout(int id) {
        JSONObject obj_LogOut = new JSONObject();
        String str = "";
        try {
            obj_LogOut.put("id", id);
            obj_LogOut.put("request", "logout");
            obj_LogOut.put("params", new JSONObject());
            str = obj_LogOut.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String resp(int id, int rid) {
        JSONObject obj_Resp = new JSONObject();
        String str = "";
        try {
            obj_Resp.put("id", id);
            obj_Resp.put("rid", rid);
            obj_Resp.put("resp", new JSONObject());
            str = obj_Resp.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String rejectOrder(int id, String orderId, String opt) {
        JSONObject obj_Params = new JSONObject();
        JSONObject obj_rejectOrder = new JSONObject();
        String str = "";
        try {
            obj_Params.put("orderId", orderId);
            obj_Params.put("orderProposalType", opt);

            obj_rejectOrder.put("id", id);
            obj_rejectOrder.put("request", "rejectOrder");
            obj_rejectOrder.put("params", obj_Params);
            str = obj_rejectOrder.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String informOrderProposalShown(int id, String orderId) {
        JSONObject obj_Params = new JSONObject();
        JSONObject obj_IOPS = new JSONObject();
        String str = "";
        try {
            obj_Params.put("orderId", orderId);

            obj_IOPS.put("id", id);
            obj_IOPS.put("request", "informOrderProposalShown");
            obj_IOPS.put("params", obj_Params);
            str = obj_IOPS.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String acceptOrder(int id, String orderId, String opt, boolean time5min) {
        JSONObject obj_Params = new JSONObject();
        JSONObject obj_AcceptOrder = new JSONObject();
        String str = "";
        try {
            obj_Params.put("orderId", orderId);
            obj_Params.put("orderProposalType", opt);
            String stime = "10";
            if (time5min && !opt.equals("BEST")) { stime = "5"; }
            obj_Params.put("time", stime);

            obj_AcceptOrder.put("id", id);
            obj_AcceptOrder.put("request", "acceptOrder");
            obj_AcceptOrder.put("params", obj_Params);
            str = obj_AcceptOrder.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String acceptOrderAir(int id, String orderId, boolean time5min) {
        JSONObject obj_Params = new JSONObject();
        JSONObject obj_AcceptOrder = new JSONObject();
        String str = "";
        try {
            obj_Params.put("orderId", orderId);
            String stime = "10";
            if (time5min) { stime = "5"; }
            obj_Params.put("time", stime);

            obj_AcceptOrder.put("id", id);
            obj_AcceptOrder.put("request", "acceptOrder");
            obj_AcceptOrder.put("params", obj_Params);
            str = obj_AcceptOrder.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String bookPreliminary(int id, String orderId) {
        JSONObject obj_Params = new JSONObject();
        JSONObject obj_AcceptOrder = new JSONObject();
        String str = "";
        try {
            obj_Params.put("orderId", orderId);

            obj_AcceptOrder.put("id", id);
            obj_AcceptOrder.put("request", "bookPreliminary");
            obj_AcceptOrder.put("params", obj_Params);
            str = obj_AcceptOrder.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String acceptPreliminary(int id, String orderId) {
        JSONObject obj_Params = new JSONObject();
        JSONObject obj_AcceptOrder = new JSONObject();
        String str = "";
        try {
            obj_Params.put("orderId", orderId);

            obj_AcceptOrder.put("id", id);
            obj_AcceptOrder.put("request", "acceptPreliminary");
            obj_AcceptOrder.put("params", obj_Params);
            str = obj_AcceptOrder.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String makeTime() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.000+03:00'").format(new Date());
    }
}
