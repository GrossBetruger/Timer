package prosta.timer;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static prosta.timer.ForegroundTimer.NUMBER_OF_RINGS_PREF;
import static prosta.timer.ForegroundTimer.INTERVAL_PREF;


public class SettingsActivity extends AppCompatActivity {

    EditText intervalSetter;
    EditText numberOfRingsSetter;
    Button apply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        intervalSetter = (EditText) findViewById(R.id.sethour);
        numberOfRingsSetter = (EditText) findViewById(R.id.setrings);
        apply = (Button) findViewById(R.id.apply);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setInterval();
                setNumberOfRings();
            }
        });

    }

    private void setInterval(){
        String intervalText = intervalSetter.getText().toString();
        Log.d("interval text", intervalText);
        if (intervalText.equals("")) {
            return ;
        }

        Double intervalHours = Double.valueOf(intervalText);
        Double timeMili = intervalHours * (3600 * 1000.0);
        String message = "alarm interval set to: " + intervalHours + " hours (" + timeMili +") miliseconds";

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(INTERVAL_PREF, timeMili.longValue());
        editor.commit();
        Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();

    }

    private void setNumberOfRings(){
        String numOfRingsString = numberOfRingsSetter.getText().toString();
        if (numOfRingsString.equals("")) {
            return;
        }

        int numberOfRingsInput = Integer.valueOf(numOfRingsString);
        String message = "number of rings set to: " + numberOfRingsInput;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(NUMBER_OF_RINGS_PREF, numberOfRingsInput);
        editor.commit();
        Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
