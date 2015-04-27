package il.ac.jct.nafcha.jctwifilogin;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
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
    public JctWifiLogin(String name) {
        super(name);
    }
    public JctWifiLogin() {
        super("JctWifiLogin");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = getApplicationContext();
        CharSequence text = "Hello wifi";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplicationContext();
        CharSequence text = "Hello toast!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
