package il.ac.jct.nafcha.jctwifilogin;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.net.Network;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import android.os.Handler;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.LogRecord;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Haim on 27/08/2015.
 */
public class UpdateApp extends IntentService {

    private Network mNetwork;
    private Notification mNotification;
    private NotificationManager mNotifMan;
    private static final int ONGOING_ID = 2;
    private static final int ONGOING_UPDATED_ID = 3;

    @Override
    public void onCreate() {
        super.onCreate();

        mNotifMan = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        updateOngoingNotification(false); // Foreground service requires a valid notification
        startForeground(ONGOING_ID, mNotification); // Stopped automatically when onHandleIntent returns
    }

    public UpdateApp() {
        super(UpdateApp.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;

            JSONObject response = new JSONObject(GET("https://api.github.com/repos/haimn/JCT_WiFi_Login/releases/latest"));

            String latestVersion = response.getString("tag_name");

            if (latestVersion.compareTo(version) < 0)
            {
                String downloadURL = response.getJSONArray("assets").getJSONObject(0).getString("browser_download_url");
                downloadapk(downloadURL);
                installApk();
            }
            else {
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.ic_check_circle_black_24dp)
                                .setContentTitle("There is no update")
                                .setContentText("JCT WiFi Login is updated");
                // Gets an instance of the NotificationManager service
                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                // Builds the notification and issues it.
                mNotifyMgr.notify(ONGOING_UPDATED_ID, mBuilder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String GET(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            return response.toString();
        } else {
            return "";
        }
    }

    private void downloadapk(String urlString){
        try {
            URL url = new URL(urlString);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            //urlConnection.setDoOutput(true);
            urlConnection.connect();

            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File(sdcard + "/Download/", "JctWifiLogin.apk");
            FileOutputStream fileOutput = new FileOutputStream(file);
            InputStream inputStream = urlConnection.getInputStream();

            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                fileOutput.write(buffer, 0, bufferLength);
            }
            fileOutput.close();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void installApk(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard + "/Download/", "JctWifiLogin.apk");
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void updateOngoingNotification(boolean notify) {
        mNotification = new NotificationCompat.Builder(this).setContentTitle(getString(R.string.notify_update_title))
                .setContentText(getString(R.string.notify_update_text))
                .setSmallIcon(R.drawable.ic_get_app_black_24dp)
                .build();

        if (notify) {
            mNotifMan.notify(ONGOING_ID, mNotification);
        }
    }

}
