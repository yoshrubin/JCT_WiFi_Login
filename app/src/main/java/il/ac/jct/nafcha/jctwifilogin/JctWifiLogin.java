package il.ac.jct.nafcha.jctwifilogin;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

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

    public void onCreate() {
        super.onCreate();
        Log.d(TAG , ">>>onCreate()");
    }
    public JctWifiLogin() {
        super(JctWifiLogin.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Toast.makeText(this,"onHandleIntent works!", Toast.LENGTH_SHORT).show();
    }
}
