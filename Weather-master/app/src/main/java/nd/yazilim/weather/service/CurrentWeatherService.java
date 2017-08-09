package nd.yazilim.weather.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import nd.yazilim.weather.preferences.Preferences;
import nd.yazilim.weather.receiver.MyReceiver;

public class CurrentWeatherService extends IntentService {

    private static final String TAG = "CurrentWeatherService";
    PendingIntent pendingIntent;
    Preferences preferences;

    public CurrentWeatherService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        preferences = new Preferences(this);

        Intent myIntent = new Intent(this, MyReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent,0);

        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        if (preferences.getNotifs())
            am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_HOUR,
                    AlarmManager.INTERVAL_HOUR,
                    pendingIntent);
        else {
            am.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }
}
