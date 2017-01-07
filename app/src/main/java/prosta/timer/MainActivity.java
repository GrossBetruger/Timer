package prosta.timer;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static prosta.timer.ForegroundTimer.INTERVAL_PREF;


public class MainActivity extends AppCompatActivity {

    public static long interval;
    private PowerManager.WakeLock wakeLock;
    int ringsLeft = 0;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "dontSleep");

        final TextView title = (TextView) findViewById(R.id.textView);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String timemessage = intent.getStringExtra(ForegroundTimer.TIME_MESSAGE);
                        title.setText(timemessage);
                    }
                }, new IntentFilter(ForegroundTimer.ACTION_SEND_TIME_MESSAGE)
        );



        final Button startButton = (Button) findViewById(R.id.startbutton);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                scheduleAlarm();
            }
        });

        final Button stopButton = (Button) findViewById(R.id.stopbutton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cancelAlarm();
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
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void scheduleAlarm()
    {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPref.getLong(INTERVAL_PREF, 0L)== 0L) {

            interval = 3600 * 1000 * 3;

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putLong(INTERVAL_PREF, interval);
            editor.commit();
        }

        else {
            interval = sharedPref.getLong(INTERVAL_PREF, 0L);
        }

        String strInputMsg = String.valueOf(interval);
        Log.d("interval:", strInputMsg);

        Intent msgIntent = new Intent(this, ForegroundTimer.class);
        msgIntent.putExtra(ForegroundTimer.INTERVAL, interval);
        startService(msgIntent);
        String alarmSetMessage = "Alarm Scheduled for " + interval/1000.0 + " seconds from now";
        Toast.makeText(this, alarmSetMessage, Toast.LENGTH_LONG).show();


    }

    public void cancelAlarm() {
        Intent msgIntent = new Intent(this, ForegroundTimer.class);

        stopService(msgIntent);

        TextView title = (TextView) findViewById(R.id.textView);
        title.setText("ALARM CANCELED");
        try {
            wakeLock.release();
        }
        catch (java.lang.RuntimeException e){
            Log.d("wake lock error", e.getMessage());
        }

    }

}
