package il.ac.jct.nafcha.jctwifilogin;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.util.Log;
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
        Log.d(TAG , ">>>onCreate()");

        mNotifMan = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotification = new Notification(R.drawable.abc_btn_check_material, null, System.currentTimeMillis());
        updateOngoingNotification(getString(R.string.notify_request_wifi_ongoing_text), false); // Foreground service requires a valid notification
        startForeground(LOGIN_ONGOING_ID, mNotification); // Stopped automatically when onHandleIntent returns
    }
    public JctWifiLogin() {
        super(JctWifiLogin.class.getName());
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
//    }

    @Override
    protected void onHandleIntent(Intent intent) {
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

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("https://wireless-login.jct.ac.il/login.html");
        //Encoding POST data
        List<NameValuePair> DataPost = new ArrayList<NameValuePair>();
        DataPost.add(new BasicNameValuePair("buttonClicked", "4"));
        DataPost.add(new BasicNameValuePair("err_flag", "0"));
        DataPost.add(new BasicNameValuePair("err_msg", ""));
        DataPost.add(new BasicNameValuePair("info_flag", "1"));
        DataPost.add(new BasicNameValuePair("redirect_url", ""));
        DataPost.add(new BasicNameValuePair("username", "nafcha"));
        DataPost.add(new BasicNameValuePair("password", "HNafha11"));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(DataPost));

        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        try {
            HttpResponse response = httpClient.execute(httpPost);
            // write response to log
            Log.d("Http Post Response:", response.toString());
        } catch (ClientProtocolException e) {
            // Log exception
            e.printStackTrace();
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

    private void updateOngoingNotification(String message, boolean notify) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent() , 0);
        mNotification.setLatestEventInfo(this, getString(R.string.notify_login_ongoing_title), message, contentIntent);

        if (notify) {
            mNotifMan.notify(LOGIN_ONGOING_ID, mNotification);
        }
    }
}
