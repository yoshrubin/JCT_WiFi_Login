package il.ac.jct.nafcha.jctwifilogin;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class NetworkStateChanged extends BroadcastReceiver {

    static final String TAG = "NetworkStateChanged";
    static final String[] SSID = {"JCT - Lev", "JCT - Tal"};
//    private Network mNetwork;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");

        // Check network connected
        NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        Log.d(TAG, "onReceive: netInfo = " + netInfo);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean enable_login = sharedPref.getBoolean("enable_login", true);
        Log.d(TAG, "enable_login " + enable_login);
        if (!sharedPref.getBoolean("enable_login", true) || !netInfo.isConnected()) {
            return;
        }

        // Check SSID
        String ssid;
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        try {
            ssid = wifi.getConnectionInfo().getSSID();
        }
        catch (NullPointerException e) {
            // So many things can be null here when network is not connected
            Log.d(TAG, "Exception getting SSID", e);
            ssid = null;
        }
        Log.d(TAG, "ssid = " + ssid);
        boolean validSsid = false;
        for (String checkSsid : SSID) {
            if (checkSsid.equalsIgnoreCase(ssid) || ("\"" + checkSsid + "\"").equalsIgnoreCase(ssid)) {
                // JB 4.2 puts quote around SSID
                validSsid = true;
                break;
            }
        }
        if (!validSsid) {
            Log.d(TAG, "Invalid SSID");
            return;
        }

        Log.v(TAG, "Connected to the correct network");

        //do the login

        context.startService(new Intent(context, JctWifiLogin.class));
//        Toast.makeText(context, "works!", Toast.LENGTH_SHORT).show();
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            // For Lollipop+, we need to request the Wi-Fi network since
//            // connections will go over mobile data by default if a captive
//            // portal is detected
//            Log.v(TAG, "Requesting Wi-Fi network");
//
//            if (!requestNetwork(context)) {
//                Log.e(TAG, "Unable to request Wi-Fi network");
//                Toast.makeText(context, "Unable to request Wi-Fi network", Toast.LENGTH_SHORT).show();
//                return;
//            }
//        }
//
//        HttpClient httpClient = new DefaultHttpClient();
//        HttpPost httpPost = new HttpPost("https://wireless-login.jct.ac.il/login.html");
//        //Encoding POST data
//        List<NameValuePair> DataPost = new ArrayList<NameValuePair>();
//        DataPost.add(new BasicNameValuePair("buttonClicked", "4"));
//        DataPost.add(new BasicNameValuePair("err_flag", "0"));
//        DataPost.add(new BasicNameValuePair("err_msg", ""));
//        DataPost.add(new BasicNameValuePair("info_flag", "1"));
//        DataPost.add(new BasicNameValuePair("redirect_url", ""));
//        DataPost.add(new BasicNameValuePair("username", "nafcha"));
//        DataPost.add(new BasicNameValuePair("password", "HNafha11"));
//        try {
//            httpPost.setEntity(new UrlEncodedFormEntity(DataPost));
//
//        } catch (UnsupportedEncodingException e)
//        {
//            e.printStackTrace();
//        }
//
//        try {
//            HttpResponse response = httpClient.execute(httpPost);
//            // write response to log
//            Log.d("Http Post Response:", response.toString());
//        } catch (ClientProtocolException e) {
//            // Log exception
//            e.printStackTrace();
//        } catch (IOException e) {
//            // Log exception
//            e.printStackTrace();
//        }


    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    private boolean requestNetwork(Context context) {
//        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        for (Network net : cm.getAllNetworks()) {
//            if (cm.getNetworkInfo(net).getType() == ConnectivityManager.TYPE_WIFI) {
//                Log.d(TAG, "Set network to " + net);
//                mNetwork = net;
//                ConnectivityManager.setProcessDefaultNetwork(net);
//
//                Toast.makeText(context, "On Wi-Fi network", Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        }
//
//        return false;
//    }

}