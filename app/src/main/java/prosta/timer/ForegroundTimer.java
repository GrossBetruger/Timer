package prosta.timer;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import static java.lang.Thread.sleep;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class ForegroundTimer extends Service {
    // TODO: Rename actions, choose action names that describe tasks that this
    public static final String INTERVAL = "INTERVAL";
    public static final String TIME_MESSAGE = "TIME_MESSAGE";
    public static final String ACTION_SEND_TIME_MESSAGE = "SEND_TIME_MESSAGE";
    public static final String NUMBER_OF_RINGS_PREF = "NUMBER_OF_RINGS";
    public static final String INTERVAL_PREF = "INTERVAL";
    public static final int SECOND = 1000;
    public static int ringsLeft;
    CountDownTimer countdown;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final long interval = intent.getExtras().getLong(INTERVAL);
            Log.d("interval is", String.valueOf(interval));
            Log.d("clcocktick is", String.valueOf(SECOND));

            countdown = new CountDownTimer(interval, 1000) {

                public void onTick(long millisUntilFinished) {
                    String timeLeft = "seconds remaining: " + millisUntilFinished / 1000;
                    String minuteLeft = "minutes remaning: " + millisUntilFinished / 1000 / 60;
                    String hourLeft = "hours remaning: " + millisUntilFinished / 1000 / 60 / 60;
                    String message = timeLeft + "\n" + minuteLeft + "\n" + hourLeft;
                    Log.d("time remaining", message);
                    Intent intent = new Intent(ACTION_SEND_TIME_MESSAGE);
                    intent.putExtra(TIME_MESSAGE, message);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                }

                public void onFinish() {
                    Log.d("onFinish:", "countdown done");

                    ring();
                    Log.d("onFinish:", "rang");

                    countdown.start();
                }
            }.start();

        }
        return super.onStartCommand(intent, flags, startId);


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        countdown.cancel();
        super.onDestroy();
    }

    public void ring() {
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.bell);
        int duration = mediaPlayer.getDuration();
        Log.d("audio file duration:", String.valueOf(duration));

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPref.getInt(NUMBER_OF_RINGS_PREF, 0) == 0) {
            ringsLeft = 3;
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(NUMBER_OF_RINGS_PREF, ringsLeft);
            editor.commit();
        }

        else {
            ringsLeft = sharedPref.getInt(NUMBER_OF_RINGS_PREF, 0);
        }


        new Thread( new Runnable() {
            @Override
            public void run() {

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                {
                    @Override
                    public void onCompletion(MediaPlayer mp)

                    {
                        if (ringsLeft > 1) {
                            Log.d("rings left:", String.valueOf(ringsLeft-2));
                            mp.start();
                            ringsLeft--;
                        }
                    }
                });
            }
        }).start();
        mediaPlayer.start();


    }
}
