package nd.yazilim.weather.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Preferences {
    private static SharedPreferences prefs;
    private final String USER_PREFS = "Prefs";

    public Preferences(Context context) {
        prefs = context.getSharedPreferences(USER_PREFS , Context.MODE_PRIVATE);
    }

    public SharedPreferences getPrefs() {
        return prefs;
    }

    public String getCity() {
        return prefs.getString("city", null);
    }

    public void setCity(String city) {
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString("city", city);
        prefsEditor.apply();
    }

    public void setLaunched() {
        prefs.edit().putBoolean("first" , false).apply();
    }

    public boolean getLaunched() {
        return prefs.getBoolean("first" , true);
    }

    public void setLastCity(String city) {
        prefs.edit().putString("lcity" , city).apply();
    }

    public String getLastCity() {
        return prefs.getString("lcity" , null);
    }

    public void setLatitude(float lat) {
        prefs.edit().putFloat("lat" , lat).apply();
    }

    public float getLatitude() {
        return prefs.getFloat("lat" , 0);
    }

    public void setLongitude(float lon) {
        prefs.edit().putFloat("lon" , lon).apply();
    }

    public float getLongitude() {
        return prefs.getFloat("lon" , 0);
    }

    public void setUnits(String string) {
        prefs.edit().putString("units" , string).apply();
        Log.i("units" , string);
    }

    public String getUnits() {
        return prefs.getString("units" , "metric");
    }

    public void setNotifs(Boolean bool) {
        prefs.edit().putBoolean("notif" , bool).apply();
    }

    public Boolean getNotifs() {
        return prefs.getBoolean("notif" , false);
    }
}
