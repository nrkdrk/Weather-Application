package nd.yazilim.weather.internet;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;


import nd.yazilim.weather.R;
import nd.yazilim.weather.preferences.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchWeather extends AsyncTask<String , Void , JSONObject[]> {

    private static final String OPEN_WEATHER_MAP_FORECAST_API = "http://api.openweathermap.org/data/2.5/forecast/daily?";
    private static final String OPEN_WEATHER_MAP_DAILY_API = "http://api.openweathermap.org/data/2.5/weather?";
    private final String LOG_TAG = FetchWeather.class.getSimpleName();

    private Context context;
    private final String QUERY_PARAM = "q";
    private final String FORMAT_PARAM = "mode";
    private final String FORMAT_VALUE = "json";
    final String LAT_PARAM = "lat";
    final String LON_PARAM = "lon";
    private final String UNITS_PARAM = "units";
    private String UNITS_VALUE;
    private final String DAYS_PARAM = "cnt";
    private Uri builtDay , builtFort;
    private Preferences preferences;

    public FetchWeather(Context mContext) {
        context = mContext;
        preferences = new Preferences(context);
    }

    @Override
    public JSONObject[] doInBackground(String... params) {
        if (params.length == 1)
            city(params);
        else
            coordinates(params);
        try {
            Log.d(LOG_TAG , "Execution");
            URL day = new URL(builtDay.toString());
            Log.i("day" , day.toString());
            URL fort = new URL(builtFort.toString());
            Log.i("fort" , fort.toString());
            Log.d(LOG_TAG , "URI Ready");
            HttpURLConnection connection0 = (HttpURLConnection)day.openConnection();
            HttpURLConnection connection1 = (HttpURLConnection)fort.openConnection();
            connection0.addRequestProperty("x-api-key", context.getString(R.string.open_weather_maps_app_id));
            connection1.addRequestProperty("x-api-key", context.getString(R.string.open_weather_maps_app_id));
            BufferedReader reader;
            StringBuilder json = new StringBuilder(1024) , json1 = new StringBuilder(1024);

            reader= new BufferedReader(new InputStreamReader(connection0.getInputStream()));
            String tmp;
            while((tmp = reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            reader = new BufferedReader(new InputStreamReader(connection1.getInputStream()));
            while((tmp = reader.readLine())!=null)
                json1.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString()) , data1 = new JSONObject(json1.toString());

            // This value will be 404 if the request was not successful
            if(data.getInt("cod") != 200 || data1.getInt("cod") != 200){
                Log.e(LOG_TAG , "Execution Failed");
                return null;
            }

            JSONObject array[] = new JSONObject[2];
            array[0] = data;
            array[1] = data1;
            Log.d(LOG_TAG , "Array Ready");
            return array;
        }catch(JSONException e){
            Log.e(LOG_TAG , "Execution Failed JSON");
            return null;
        }
        catch(IOException e){
            Log.e(LOG_TAG , "Execution Failed IO");
            e.printStackTrace();
            return null;
        }
    }

    private void city(String... params) {
        UNITS_VALUE = preferences.getUnits();
        System.out.println(UNITS_VALUE);
        builtDay = Uri.parse(OPEN_WEATHER_MAP_DAILY_API).buildUpon()
                .appendQueryParameter(QUERY_PARAM , params[0])
                .appendQueryParameter(FORMAT_PARAM , FORMAT_VALUE)
                .appendQueryParameter(UNITS_PARAM , UNITS_VALUE)
                .appendQueryParameter(DAYS_PARAM , Integer.toString(10))
                .build();
        builtFort = Uri.parse(OPEN_WEATHER_MAP_FORECAST_API).buildUpon()
                .appendQueryParameter(QUERY_PARAM , params[0])
                .appendQueryParameter(FORMAT_PARAM , FORMAT_VALUE)
                .appendQueryParameter(UNITS_PARAM , UNITS_VALUE)
                .appendQueryParameter(DAYS_PARAM , Integer.toString(10))
                .build();
    }

    private void coordinates(String... params) {
        UNITS_VALUE = preferences.getUnits();
        builtDay = Uri.parse(OPEN_WEATHER_MAP_DAILY_API).buildUpon()
                .appendQueryParameter(LAT_PARAM , params[0])
                .appendQueryParameter(LON_PARAM , params[1])
                .appendQueryParameter(FORMAT_PARAM , FORMAT_VALUE)
                .appendQueryParameter(UNITS_PARAM , UNITS_VALUE)
                .appendQueryParameter(DAYS_PARAM , Integer.toString(10))
                .build();
        builtFort = Uri.parse(OPEN_WEATHER_MAP_FORECAST_API).buildUpon()
                .appendQueryParameter(LAT_PARAM , params[0])
                .appendQueryParameter(LON_PARAM , params[1])
                .appendQueryParameter(FORMAT_PARAM , FORMAT_VALUE)
                .appendQueryParameter(UNITS_PARAM , UNITS_VALUE)
                .appendQueryParameter(DAYS_PARAM , Integer.toString(10))
                .build();
    }
}
