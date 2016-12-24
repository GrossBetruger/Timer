package prosta.timer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.util.Log;


import static java.lang.Thread.sleep;

/**
 * Created by oren on 17/12/16.
 */

public class AlarmReceiver extends BroadcastReceiver {


    public static final String NUMBER_OF_RINGS_KEY = "NUMBER_OF_RINGS";
    int ringsLeft = 0;

    @Override
    public void onReceive(Context context, Intent intent){
        final MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.bell);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

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
