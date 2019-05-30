package ro.vodafone.mcare.android.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

import ro.vodafone.mcare.android.client.Hosts;

/**
 * Created by Victor Radulescu on 12/29/2016.
 */

public class NetworkUtils {

    public static enum ConnectionType {
        IOYM,
        WIFI,
        Other
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
    public static TelephonyManager build(Context context){

        return context!=null ? (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE) : null;

    }

    public static boolean isIOYM(Context context){

        ConnectionType type = checkNetworkType(context);
        return ConnectionType.IOYM.equals(type);

    }

    public static String getNetworkType(Context context){
        TelephonyManager manager = build(context);
        if(manager != null) {
            int networkType = manager.getNetworkType();

            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    return "Unknown";
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return "GPRS";
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return "EDGE";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return "UMTS";
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return "CDMA";
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return "EVDO rev. 0";
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return "EVDO rev. A";
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return "1xRTT";
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return "HSDPA";
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return "HSUPA";
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return "HSPA";
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return "iDen";
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    return "EVDO rev. B";
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return "LTE";
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    return "eHRPD";
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return "HSPA+";
                case 16:
                    return "GSM";
                case 17:
                    return "TD_SCDMA";
                case 18:
                    return "IWLAN";

                default:
                    return "Unknown";
            }
        }
        return null;
    }

    public static boolean isDebugEnvironment() {
        if (Hosts.getEnviroment() == Hosts.Environment.ST1
                || Hosts.getEnviroment() == Hosts.Environment.ST2
                || Hosts.getEnviroment() == Hosts.Environment.ST3
                || Hosts.getEnviroment() == Hosts.Environment.ST4
                || Hosts.getEnviroment() == Hosts.Environment.DEV
                || Hosts.getEnviroment() == Hosts.Environment.DEV2
                ){
            return true;
        }
        return false;
    }

    public static ConnectionType checkNetworkType(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String carrierName = manager.getNetworkOperatorName();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try{
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();

            // type = 0 is mobile; type = bannerone is wifi;
            int type = networkInfo.getType();


            //seamless workaround for testing
            if (isDebugEnvironment()){
               return ConnectionType.IOYM;
            }

            if (carrierName != null && carrierName.toLowerCase().contains("vodafone") && type == 0) {
                return ConnectionType.IOYM;
            }

            if(type==1){
                return ConnectionType.WIFI;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return ConnectionType.Other;

    }

    public static boolean isWifiConnection(Context context) {
        boolean haveConnectedWifi = false;


        if (isDebugEnvironment()) {
            return false;
        }

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Network[] networks = cm.getAllNetworks();
            for (Network network : networks) {
                NetworkInfo networkInfo = cm.getNetworkInfo(network);
                if (networkInfo.getTypeName().equalsIgnoreCase("WIFI"))
                    if (networkInfo.isConnected())
                        haveConnectedWifi = true;
            }
        } else {
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo) {
                if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                    if (ni.isConnected())
                        haveConnectedWifi = true;
            }
        }
        return haveConnectedWifi;
    }

    public static boolean isMobileNetworkConnection(Context context) {
        boolean haveConnectedMobile = false;
        if(isDebugEnvironment()) {
            return true;
        }

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedMobile;
    }
}
