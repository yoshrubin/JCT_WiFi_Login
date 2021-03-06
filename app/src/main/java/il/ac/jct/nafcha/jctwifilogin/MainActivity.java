package il.ac.jct.nafcha.jctwifilogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the language
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String language = sharedPref.getString("language", "");
        if (language != "default") {
            Locale myLocale = new Locale(language);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
        }

        setContentView(R.layout.activity_main);

        final SharedPreferences prefs = new ObscuredSharedPreferences(
                this, this.getSharedPreferences("Preferences", MODE_PRIVATE) );

        final EditText userText = (EditText) findViewById(R.id.userText);
        final EditText passwordText = (EditText) findViewById(R.id.passwordText);

        String user = prefs.getString("user", null);
        String password = prefs.getString("password", null);
        if (user != null)
            userText.setText(user);
        if (password != null)
            passwordText.setText(password);

        Button submitB = (Button) findViewById(R.id.submit);
        submitB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = userText.getText().toString();
                String password = passwordText.getText().toString();
                prefs.edit().putString("user", user).commit();
                prefs.edit().putString("password", password).commit();
                Toast.makeText(MainActivity.this, "saved!", Toast.LENGTH_SHORT).show();
            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id)
        {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_about:
                startActivity(new Intent(this, About.class));
                return true;
            case R.id.action_update:
                startService(new Intent(this, UpdateApp.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}