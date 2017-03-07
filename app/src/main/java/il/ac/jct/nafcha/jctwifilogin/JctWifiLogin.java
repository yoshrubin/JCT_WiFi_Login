package il.ac.jct.nafcha.jctwifilogin;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by haim on 4/27/15.
 */
public class JctWifiLogin extends IntentService{
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    private static final String TAG = "JctWifiLogin";
    private Network mNetwork;
    private Notification mNotification;
    private NotificationManager mNotifMan;
    private static final int LOGIN_ONGOING_ID = 2;

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, ">>>onCreate()");

        mNotifMan = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotification = new Notification(R.drawable.abc_btn_check_material, null, System.currentTimeMillis());

        updateOngoingNotification(false); // Foreground service requires a valid notification
        startForeground(LOGIN_ONGOING_ID, mNotification); // Stopped automatically when onHandleIntent returns
    }
    public JctWifiLogin() {
        super(JctWifiLogin.class.getName());
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        final SharedPreferences prefs = new ObscuredSharedPreferences(
                this, this.getSharedPreferences("Preferences", MODE_PRIVATE) );
        String user = prefs.getString("user", null);
        String password = prefs.getString("password", null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // For Lollipop+, we need to request the Wi-Fi network since
            // connections will go over mobile data by default if a captive
            // portal is detected
            Log.v(TAG, "Requesting Wi-Fi network");

            if (!requestNetwork()) {
                Log.e(TAG, "Unable to request Wi-Fi network");
                return;
            }
        }

        String httpPost = "https://captiveportal-login.jct.ac.il/";


        //Encoding POST data
        Map<String,Object> DataPost = new LinkedHashMap();
        DataPost.put("buttonClicked", 4);
        DataPost.put("err_flag", 0);
        DataPost.put("err_msg", "");
        DataPost.put("info_flag", 1);
        DataPost.put("redirect_url", "");
        DataPost.put("username", user);
        DataPost.put("password", password);

        try {
            String response = POST(httpPost,DataPost);
            // write response to log
            Log.d("Http Post Response:", response);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Report logout successful so Android stops using this network
                // (or at least that should happen, but 5.0.0_r2 doesn't seem to
                // automatically switch to cellular)
                reportStateChange();
            }

        } catch (IOException e) {
            // Log exception
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean requestNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        for (Network net : cm.getAllNetworks()) {
            if (cm.getNetworkInfo(net).getType() == ConnectivityManager.TYPE_WIFI) {
                Log.d(TAG, "Set network to " + net);
                mNetwork = net;
                ConnectivityManager.setProcessDefaultNetwork(net);

                Toast.makeText(this, "On Wi-Fi network", Toast.LENGTH_SHORT).show();
                return true;
            }
        }

        return false;
    }

    /**
     * Report successful to Lollipop's captive portal detector
     *
     * See CaptivePortalLoginActivity in frameworks/base
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void reportStateChange() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // We're reporting "good" network. This function forces Android to
        // re-evaluate the network (and realize it's no longer a captive portal).
        cm.reportBadNetwork(mNetwork);
    }

    private void updateOngoingNotification(boolean notify) {
        mNotification = new NotificationCompat.Builder(this).setContentTitle(getString(R.string.notify_login_ongoing_title))
                .setContentText(getString(R.string.notify_request_wifi_ongoing_text))
                .setSmallIcon(R.drawable.ic_wifi_black_24dp)
                .build();
        if (notify) {
            mNotifMan.notify(LOGIN_ONGOING_ID, mNotification);
        }
    }

    private static String POST(String url, Map<String,Object> params) throws IOException {

        //Convert Map<String,Object> into key=value&key=value pairs.
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String,Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");

        // For POST only - START
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.write(postData.toString().getBytes("UTF-8"));
        os.flush();
        os.close();
        // For POST only - END

        int responseCode = con.getResponseCode();
        System.out.println("POST Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        }
        else return "";
    }

}
