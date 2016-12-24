package prosta.timer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    public static final String INTERVAL_PREF = "interval";
    public static final String COUNT_DOWN_KEY = "COUNT_DOWN";
    public static final String NUMBER_OF_RINGS_KEY = "NUMBER_OF_RINGS";
    public static final int SECOND = 1000;
    public static long interval;
    public final static int MILLIS_IN_HOUR = 3600000;
    private PendingIntent pendingIntent;
    private AlarmManager manager;
    private PowerManager.WakeLock wakeLock;
    int ringsLeft = 0;
    CountDownTimer countdown;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Retrieve a PendingIntent that will perform a broadcast
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

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




//        Long countDownLong = interval / (3600 * 1000);
//        countDown = countDownLong.intValue();
//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.putInt(COUNT_DOWN_KEY, countDown);

//        long MINUTE_TO_MSEC = 60000;
//        long DAY_TO_MSEC = MINUTE_TO_MSEC * 60 * 24;

//
//        Timer timer = new Timer();
//        TimerTask countdownTask = new TimerTask() {
//            public void run() {
//        /* your function goes here */
//                ringing();
//            }
//        };
//        timer.scheduleAtFixedRate(countdownTask, 0, interval);

//        final TextView title = (TextView) findViewById(R.id.textView);
//
//        wakeLock.acquire();
//
//        countdown = new CountDownTimer(interval, SECOND) {
//
//            public void onTick(long millisUntilFinished) {
//                String timeLeft = "seconds remaining: " + millisUntilFinished / 1000;
//                String minuteLeft = "minutes remaning: " + millisUntilFinished / 1000 / 60;
//                String hourLeft = "hours remaning: " + millisUntilFinished / 1000 / 60 / 60;
//                String message = timeLeft + "\n" + minuteLeft + "\n" + hourLeft;
//                Log.d("time remaining", timeLeft);
//                title.setText(message);
//
//            }
//
//            public void onFinish() {
//                title.setText("you can eat, fat boy!");
//                ringing();
//                try {
//                    sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                countdown.start();
//            }
//        }.start();


//
//

//
//        manager = (AlarmManager) getSystemService(this.ALARM_SERVICE);
//        manager.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + interval, interval, pendingIntent);

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



//        manager = (AlarmManager) getSystemService(this.ALARM_SERVICE);
//        manager.cancel(pendingIntent);
//

//
//        Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }


    public void ringing() {
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.bell);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPref.getInt(NUMBER_OF_RINGS_KEY, 0) == 0) {
            ringsLeft = 3;
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(NUMBER_OF_RINGS_KEY, ringsLeft);
            editor.commit();
        }

        else {
            ringsLeft = sharedPref.getInt(NUMBER_OF_RINGS_KEY, 0);
        }


        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)

            {
                if (ringsLeft > 1) {
                    Log.d("rings left:", String.valueOf(ringsLeft-1));
                    mp.start();
                    try {
                        sleep(2500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ringsLeft--;
                }
            }
        });
    }
}
