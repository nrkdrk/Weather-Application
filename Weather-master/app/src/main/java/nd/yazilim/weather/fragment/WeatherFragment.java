package nd.yazilim.weather.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import nd.yazilim.weather.R;
import nd.yazilim.weather.activity.GlobalActivity;
import nd.yazilim.weather.activity.FirstLaunch;
import nd.yazilim.weather.activity.WeatherActivity;
import nd.yazilim.weather.internet.CheckConnection;
import nd.yazilim.weather.internet.FetchWeather;
import nd.yazilim.weather.permissions.Permissions;
import nd.yazilim.weather.preferences.Preferences;
import nd.yazilim.weather.service.CurrentWeatherService;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class WeatherFragment extends Fragment {
    Typeface weatherFont;
    TextView button;
    TextView detailsField[] = new TextView[10] , weatherIcon[] = new TextView[11];
    TextView windView , humidityView , directionView, dailyView, updatedField, cityField, sunriseView , sunsetView;
    TextView sunriseIcon , sunsetIcon , windIcon , humidityIcon;
    double tc;
    Handler handler;
    BottomSheetDialogFragment bottomSheetDialogFragment;
    JSONObject json1 , json0;
    SwipeRefreshLayout swipeView;
    CheckConnection cc;
    int Clicks = 0;
    JSONObject[] json;
    MaterialDialog pd;
    FetchWeather wt;
    Preferences preferences;
    Bundle bundle;
    View rootView;

    public WeatherFragment() {
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        cityField = (TextView)rootView.findViewById(R.id.city_field);
        cityField.setTextColor(ContextCompat.getColor(getContext() , R.color.textColor));
        updatedField = (TextView)rootView.findViewById(R.id.updated_field);
        updatedField.setTextColor(ContextCompat.getColor(getContext() , R.color.textColor));
        humidityView = (TextView) rootView.findViewById(R.id.humidity_view);
        humidityView.setTextColor(ContextCompat.getColor(getContext() , R.color.textColor));
        sunriseIcon = (TextView) rootView.findViewById(R.id.sunrise_icon);
        sunriseIcon.setTextColor(ContextCompat.getColor(getContext() , R.color.textColor));
        sunriseIcon.setTypeface(weatherFont);
        sunriseIcon.setText(getActivity().getString(R.string.sunrise_icon));
        sunsetIcon = (TextView) rootView.findViewById(R.id.sunset_icon);
        sunsetIcon.setTextColor(ContextCompat.getColor(getContext() , R.color.textColor));
        sunsetIcon.setTypeface(weatherFont);
        sunsetIcon.setText(getActivity().getString(R.string.sunset_icon));
        windIcon = (TextView) rootView.findViewById(R.id.wind_icon);
        windIcon.setTextColor(ContextCompat.getColor(getContext() , R.color.textColor));
        windIcon.setTypeface(weatherFont);
        windIcon.setText(getActivity().getString(R.string.speed_icon));
        humidityIcon = (TextView) rootView.findViewById(R.id.humidity_icon);
        humidityIcon.setTextColor(ContextCompat.getColor(getContext() , R.color.textColor));
        humidityIcon.setTypeface(weatherFont);
        humidityIcon.setText(getActivity().getString(R.string.humidity_icon));
        humidityIcon.setText(getActivity().getString(R.string.humidity_icon));
        windView = (TextView) rootView.findViewById(R.id.wind_view);
        windView.setTextColor(ContextCompat.getColor(getContext() , R.color.textColor));
        directionView = (TextView)rootView.findViewById(R.id.direction_view);
        directionView.setTextColor(ContextCompat.getColor(getContext() , R.color.textColor));
        swipeView = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe);
        swipeView.setColorSchemeResources(R.color.red, R.color.green , R.color.blue , R.color.yellow , R.color.orange);

        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        changeCity(preferences.getCity());
                    }
                });
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeView.setRefreshing(false);
                    }
                }, 10000);
            }
        });

        ((WeatherActivity) getActivity()).showFab();
        directionView.setTypeface(weatherFont);
        directionView.setTextColor(ContextCompat.getColor(getContext() , R.color.textColor));
        dailyView = (TextView)rootView.findViewById(R.id.daily_view);
        dailyView.setText("Günler");
        dailyView.setTextColor(ContextCompat.getColor(getContext() , R.color.textColor));
        sunriseView = (TextView) rootView.findViewById(R.id.sunrise_view);
        sunriseView.setTextColor(ContextCompat.getColor(getContext() , R.color.textColor));
        sunsetView = (TextView) rootView.findViewById(R.id.sunset_view);
        sunsetView.setTextColor(ContextCompat.getColor(getContext() , R.color.textColor));
        button = (TextView) rootView.findViewById(R.id.button1);
        button.setTextColor(ContextCompat.getColor(getContext() , R.color.textColor));
        pd.show();

        for (int i = 0; i < 11; ++i)
        {
            String f = "details_view" + (i + 1) , g = "weather_icon" + (i + 1);
            if (i != 10) {
                int resID = getResources().getIdentifier(f, "id", getContext().getPackageName());
                detailsField[i] = (TextView) rootView.findViewById(resID);
                detailsField[i].setTextColor(ContextCompat.getColor(getContext() , R.color.textColor));
            }
            int resIDI = getResources().getIdentifier(g, "id" , getContext().getPackageName());
            weatherIcon[i] = (TextView)rootView.findViewById(resIDI);
            weatherIcon[i].setTypeface(weatherFont);
            weatherIcon[i].setTextColor(ContextCompat.getColor(getContext() , R.color.textColor));
        }
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this.getActivity())
                .title("Lütfen bekleyiniz")
                .content("Yükleniyor")
                .cancelable(false)
                .progress(true , 0);
        pd = builder.build();
        setHasOptionsMenu(true);
        preferences = new Preferences(getContext());
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
        updateWeatherData(preferences.getCity(), null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu , MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_weather, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_city : showInputDialog();
                break;
            case R.id.refresh : changeCity(GlobalActivity.cp.getCity());
                break;
            case R.id.location :
                Permissions permission = new Permissions(getContext());
                permission.checkPermission();
                break;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            cc = new CheckConnection(getContext());
            if (!cc.isNetworkAvailable())
                showNoInternet();
            else {
                pd.show();
                updateWeatherData(preferences.getCity(), null, null);
            }
        }
    }

    private void updateWeatherData(final String city, final String lat, final String lon) {
        wt = new FetchWeather(getContext());
        new Thread() {
            public void run() {
                try {
                    if (lat == null && lon == null) {
                        json = wt.execute(city).get();
                    } else if (city == null) {
                        json = wt.execute(lat, lon).get();
                    }
                }
                catch (InterruptedException iex) {
                    Log.e("InterruptedException" , "iex");
                }
                catch (ExecutionException eex) {
                    Log.e("ExecutionException" , "eex");
                }
                pd.dismiss();
                if (swipeView != null && swipeView.isRefreshing())
                    swipeView.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeView.setRefreshing(false);
                        }
                    });
                if (json == null) {
                    preferences.setCity(preferences.getLastCity());
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                            GlobalActivity.i = 1;
                            if (preferences.getLaunched()) {
                                FirstStart();
                            } else {
                                cc = new CheckConnection(getContext());
                                if (!cc.isNetworkAvailable()) {
                                    showNoInternet();
                                }
                                else {
                                    pd.dismiss();
                                    showInputDialog();
                                }
                            }
                        }
                    });
                }
                else {
                    handler.post(new Runnable() {
                        public void run() {
                            getActivity().startService(new Intent(getActivity() , CurrentWeatherService.class));
                            preferences.setLaunched();
                            renderWeather(json);
                            Snackbar snackbar = Snackbar.make(rootView, "Hava durumu verileri yüklendi", 1000);
                            snackbar.show();
                            pd.dismiss();
                            preferences.setLastCity(city);
                        }
                    });
                }
            }
        }.start();
    }

    public void FirstStart() {
        pd.dismiss();
        Intent intent = new Intent(getActivity(), FirstLaunch.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Log.i("Loaded", "Weather");
        startActivity(intent);
    }

    public JSONObject getDailyJson() {
        return json[1];
    }

    public void changeCity(String city)
    {
        if (!swipeView.isRefreshing())
            pd.show();
        updateWeatherData(city, null, null);
        preferences.setCity(city);
    }

    public void changeCity(String lat , String lon)
    {
        pd.show();
        updateWeatherData(null, lat, lon);
    }

    private void showInputDialog() {
        new MaterialDialog.Builder(this.getActivity())
                .title("Şehri değiştir")
                .content("Lütfen yeni bir tane şehir girin:\n")
                .negativeText("Vazgeç")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog , @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .input(null, null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, @NonNull CharSequence input) {
                        changeCity(input.toString());
                    }
                })
                .cancelable(false)
                .show();
    }

    public void showNoInternet() {
        new MaterialDialog.Builder(getContext())
                .title("İnternet yok")
                .cancelable(false)
                .content("Hava durumu verilerini göstermek için internet gerekli")
                .positiveText("Mobile veri")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$DataUsageSummaryActivity"));
                        startActivityForResult(intent , 0);
                    }
                })
                .negativeText("Wifi")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS) , 0);
                    }
                })
                .show();
    }

    private void setWeatherIcon(int id , int i) {
        String icon = "";
        if (i == 10) {
            Log.i("Here" , "Entered");
            if (checkDay())
                switch (id) {
                    case 501:
                        icon = getActivity().getString(R.string.day_drizzle);
                        break;
                    case 500:
                        icon = getActivity().getString(R.string.day_drizzle);
                        break;
                    case 502:
                        icon = getActivity().getString(R.string.day_rainy);
                        break;
                    case 503:
                        icon = getActivity().getString(R.string.day_rainy);
                        break;
                    case 504:
                        icon = getActivity().getString(R.string.day_rainy);
                        break;
                    case 511:
                        icon = getActivity().getString(R.string.day_rain_wind);
                        break;
                    case 520:
                        icon = getActivity().getString(R.string.day_rain_drizzle);
                        break;
                    case 521:
                        icon = getActivity().getString(R.string.day_drizzle);
                        break;
                    case 522:
                        icon = getActivity().getString(R.string.day_thunder);
                        break;
                    case 531:
                        icon = getActivity().getString(R.string.day_thunder);
                        break;
                    case 200:
                        icon = getActivity().getString(R.string.day_thunder);
                        break;
                    case 201:
                        icon = getActivity().getString(R.string.day_thunder);
                        break;
                    case 202:
                        icon = getActivity().getString(R.string.day_thunder);
                        break;
                    case 210:
                        icon = getActivity().getString(R.string.day_thunder);
                        break;
                    case 211:
                        icon = getActivity().getString(R.string.day_thunder);
                        break;
                    case 212:
                        icon = getActivity().getString(R.string.day_thunder);
                        break;
                    case 221:
                        icon = getActivity().getString(R.string.day_thunder);
                        break;
                    case 230:
                        icon = getActivity().getString(R.string.day_thunder);
                        break;
                    case 231:
                        icon = getActivity().getString(R.string.day_thunder);
                        break;
                    case 232:
                        icon = getActivity().getString(R.string.day_thunder);
                        break;
                    case 300:
                        icon = getActivity().getString(R.string.day_rain_drizzle);
                        break;
                    case 301:
                        icon = getActivity().getString(R.string.day_rain_drizzle);
                        break;
                    case 302:
                        icon = getActivity().getString(R.string.day_heavy_drizzle);
                        break;
                    case 310:
                        icon = getActivity().getString(R.string.day_rain_drizzle);
                        break;
                    case 311:
                        icon = getActivity().getString(R.string.day_rain_drizzle);
                        break;
                    case 312:
                        icon = getActivity().getString(R.string.day_heavy_drizzle);
                        break;
                    case 313:
                        icon = getActivity().getString(R.string.day_rain_drizzle);
                        break;
                    case 314:
                        icon = getActivity().getString(R.string.day_heavy_drizzle);
                        break;
                    case 321:
                        icon = getActivity().getString(R.string.day_heavy_drizzle);
                        break;
                    case 600:
                        icon = getActivity().getString(R.string.day_snowy);
                        break;
                    case 601:
                        icon = getActivity().getString(R.string.day_snowy);
                        break;
                    case 602:
                        icon = getActivity().getString(R.string.snow);
                        break;
                    case 611:
                        icon = getActivity().getString(R.string.sleet);
                        break;
                    case 612:
                        icon = getActivity().getString(R.string.day_snowy);
                        break;
                    case 903:
                    case 615:
                        icon = getActivity().getString(R.string.day_snowy);
                        break;
                    case 616:
                        icon = getActivity().getString(R.string.day_snowy);
                        break;
                    case 620:
                        icon = getActivity().getString(R.string.day_snowy);
                        break;
                    case 621:
                        icon = getActivity().getString(R.string.day_snowy);
                        break;
                    case 622:
                        icon = getActivity().getString(R.string.day_snowy);
                        break;
                    case 701:
                    case 702:
                    case 721:
                        icon = getActivity().getString(R.string.smoke);
                        break;
                    case 751:
                    case 761:
                    case 731:
                        icon = getActivity().getString(R.string.dust);
                        break;
                    case 741:
                        icon = getActivity().getString(R.string.fog);
                        break;
                    case 762:
                        icon = getActivity().getString(R.string.volcano);
                        break;
                    case 771:
                    case 900:
                    case 781:
                        icon = getActivity().getString(R.string.tornado);
                        break;
                    case 904:
                        icon = getActivity().getString(R.string.day_clear);
                        break;
                    case 800:
                        icon = getActivity().getString(R.string.day_clear);
                        break;
                    case 801:
                        icon = getActivity().getString(R.string.day_cloudy);
                        break;
                    case 802:
                        icon = getActivity().getString(R.string.day_cloudy);
                        break;
                    case 803:
                        icon = getActivity().getString(R.string.day_cloudy);
                        break;
                    case 804:
                        icon = getActivity().getString(R.string.day_cloudy);
                        break;
                    case 901:
                        icon = getActivity().getString(R.string.storm_showers);
                        break;
                    case 902:
                        icon = getActivity().getString(R.string.hurricane);
                        break;
                }
            else
                switch (id) {
                    case 501:
                        icon = getActivity().getString(R.string.night_drizzle);
                        break;
                    case 500:
                        icon = getActivity().getString(R.string.night_drizzle);
                        break;
                    case 502:
                        icon = getActivity().getString(R.string.night_rainy);
                        break;
                    case 503:
                        icon = getActivity().getString(R.string.night_rainy);
                        break;
                    case 504:
                        icon = getActivity().getString(R.string.night_rainy);
                        break;
                    case 511:
                        icon = getActivity().getString(R.string.night_rain_wind);
                        break;
                    case 520:
                        icon = getActivity().getString(R.string.night_rain_drizzle);
                        break;
                    case 521:
                        icon = getActivity().getString(R.string.night_drizzle);
                        break;
                    case 522:
                        icon = getActivity().getString(R.string.night_thunder);
                        break;
                    case 531:
                        icon = getActivity().getString(R.string.night_thunder);
                        break;
                    case 200:
                        icon = getActivity().getString(R.string.night_thunder);
                        break;
                    case 201:
                        icon = getActivity().getString(R.string.night_thunder);
                        break;
                    case 202:
                        icon = getActivity().getString(R.string.night_thunder);
                        break;
                    case 210:
                        icon = getActivity().getString(R.string.night_thunder);
                        break;
                    case 211:
                        icon = getActivity().getString(R.string.night_thunder);
                        break;
                    case 212:
                        icon = getActivity().getString(R.string.night_thunder);
                        break;
                    case 221:
                        icon = getActivity().getString(R.string.night_thunder);
                        break;
                    case 230:
                        icon = getActivity().getString(R.string.night_thunder);
                        break;
                    case 231:
                        icon = getActivity().getString(R.string.night_thunder);
                        break;
                    case 232:
                        icon = getActivity().getString(R.string.night_thunder);
                        break;
                    case 300:
                        icon = getActivity().getString(R.string.night_rain_drizzle);
                        break;
                    case 301:
                        icon = getActivity().getString(R.string.night_rain_drizzle);
                        break;
                    case 302:
                        icon = getActivity().getString(R.string.night_heavy_drizzle);
                        break;
                    case 310:
                        icon = getActivity().getString(R.string.night_rain_drizzle);
                        break;
                    case 311:
                        icon = getActivity().getString(R.string.night_rain_drizzle);
                        break;
                    case 312:
                        icon = getActivity().getString(R.string.night_heavy_drizzle);
                        break;
                    case 313:
                        icon = getActivity().getString(R.string.night_rain_drizzle);
                        break;
                    case 314:
                        icon = getActivity().getString(R.string.night_heavy_drizzle);
                        break;
                    case 321:
                        icon = getActivity().getString(R.string.night_heavy_drizzle);
                        break;
                    case 600:
                        icon = getActivity().getString(R.string.night_snowy);
                        break;
                    case 601:
                        icon = getActivity().getString(R.string.night_snowy);
                        break;
                    case 602:
                        icon = getActivity().getString(R.string.snow);
                        break;
                    case 611:
                        icon = getActivity().getString(R.string.sleet);
                        break;
                    case 612:
                        icon = getActivity().getString(R.string.night_snowy);
                        break;
                    case 903:
                    case 615:
                        icon = getActivity().getString(R.string.night_snowy);
                        break;
                    case 616:
                        icon = getActivity().getString(R.string.night_snowy);
                        break;
                    case 620:
                        icon = getActivity().getString(R.string.night_snowy);
                        break;
                    case 621:
                        icon = getActivity().getString(R.string.night_snowy);
                        break;
                    case 622:
                        icon = getActivity().getString(R.string.night_snowy);
                        break;
                    case 701:
                    case 702:
                    case 721:
                        icon = getActivity().getString(R.string.smoke);
                        break;
                    case 751:
                    case 761:
                    case 731:
                        icon = getActivity().getString(R.string.dust);
                        break;
                    case 741:
                        icon = getActivity().getString(R.string.fog);
                        break;
                    case 762:
                        icon = getActivity().getString(R.string.volcano);
                        break;
                    case 771:
                    case 900:
                    case 781:
                        icon = getActivity().getString(R.string.tornado);
                        break;
                    case 904:
                        icon = getActivity().getString(R.string.night_clear);
                        break;
                    case 800:
                        icon = getActivity().getString(R.string.night_clear);
                        break;
                    case 801:
                        icon = getActivity().getString(R.string.night_cloudy);
                        break;
                    case 802:
                        icon = getActivity().getString(R.string.night_cloudy);
                        break;
                    case 803:
                        icon = getActivity().getString(R.string.night_cloudy);
                        break;
                    case 804:
                        icon = getActivity().getString(R.string.night_cloudy);
                        break;
                    case 901:
                        icon = getActivity().getString(R.string.storm_showers);
                        break;
                    case 902:
                        icon = getActivity().getString(R.string.hurricane);
                        break;
                }
        }
        else {
            switch(id) {
                case 501 : icon = getActivity().getString(R.string.weather_drizzle);
                    break;
                case 500 : icon = getActivity().getString(R.string.weather_drizzle);
                    break;
                case 502 : icon = getActivity().getString(R.string.weather_rainy);
                    break;
                case 503 : icon = getActivity().getString(R.string.weather_rainy);
                    break;
                case 504 : icon = getActivity().getString(R.string.weather_rainy);
                    break;
                case 511 : icon = getActivity().getString(R.string.weather_rain_wind);
                    break;
                case 520 : icon = getActivity().getString(R.string.weather_shower_rain);
                    break;
                case 521 : icon = getActivity().getString(R.string.weather_drizzle);
                    break;
                case 522 : icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 531 : icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 200 : icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 201 : icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 202 : icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 210 : icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 211 : icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 212 : icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 221 : icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 230 : icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 231 : icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 232 : icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 300 : icon = getActivity().getString(R.string.weather_shower_rain);
                    break;
                case 301 : icon = getActivity().getString(R.string.weather_shower_rain);
                    break;
                case 302 : icon = getActivity().getString(R.string.weather_heavy_drizzle);
                    break;
                case 310 : icon = getActivity().getString(R.string.weather_shower_rain);
                    break;
                case 311 : icon = getActivity().getString(R.string.weather_shower_rain);
                    break;
                case 312 : icon = getActivity().getString(R.string.weather_heavy_drizzle);
                    break;
                case 313 : icon = getActivity().getString(R.string.weather_rain_drizzle);
                    break;
                case 314 : icon = getActivity().getString(R.string.weather_heavy_drizzle);
                    break;
                case 321 : icon = getActivity().getString(R.string.weather_heavy_drizzle);
                    break;
                case 600 : icon = getActivity().getString(R.string.weather_snowy);
                    break;
                case 601 : icon = getActivity().getString(R.string.weather_snowy);
                    break;
                case 602 : icon = getActivity().getString(R.string.weather_heavy_snow);
                    break;
                case 611 : icon = getActivity().getString(R.string.weather_sleet);
                    break;
                case 612 : icon = getActivity().getString(R.string.weather_heavy_snow);
                    break;
                case 903 :
                case 615 : icon = getActivity().getString(R.string.weather_snowy);
                    break;
                case 616 : icon = getActivity().getString(R.string.weather_snowy);
                    break;
                case 620 : icon = getActivity().getString(R.string.weather_snowy);
                    break;
                case 621 : icon = getActivity().getString(R.string.weather_snowy);
                    break;
                case 622 : icon = getActivity().getString(R.string.weather_snowy);
                    break;
                case 701 :
                case 702 :
                case 721 : icon = getActivity().getString(R.string.weather_smoke);
                    break;
                case 751 :
                case 761 :
                case 731 : icon = getActivity().getString(R.string.weather_dust);
                    break;
                case 741 : icon = getActivity().getString(R.string.weather_foggy);
                    break;
                case 762 : icon = getActivity().getString(R.string.weather_volcano);
                    break;
                case 771 :
                case 900 :
                case 781 : icon = getActivity().getString(R.string.weather_tornado);
                    break;
                case 904 : icon = getActivity().getString(R.string.weather_sunny);
                    break;
                case 800 : icon = getActivity().getString(R.string.weather_sunny);
                    break;
                case 801 : icon = getActivity().getString(R.string.weather_cloudy);
                    break;
                case 802 : icon = getActivity().getString(R.string.weather_cloudy);
                    break;
                case 803 : icon = getActivity().getString(R.string.weather_cloudy);
                    break;
                case 804 : icon = getActivity().getString(R.string.weather_cloudy);
                    break;
                case 901 : icon = getActivity().getString(R.string.weather_storm);
                    break;
                case 902 : icon = getActivity().getString(R.string.weather_hurricane);
                    break;
            }
        }

        Log.i(Integer.toString(id) , Integer.toString(i));
        weatherIcon[i].setText(icon);
    }

    private boolean checkDay() {
        Calendar c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR_OF_DAY);

        return !(hours >= 18 || hours <= 6);
    }

    private void renderWeather(JSONObject[] jsonObj){
        try {
            Clicks = 0;
            Log.i("Showed" , "Done");
            json0 = jsonObj[0];
            Log.i("Json 0" , json0.toString());
            json1 = jsonObj[1];
            Log.i("Json 1" , json1.toString());
            tc = json0.getJSONObject("main").getDouble("temp");
            preferences.setLatitude((float) json1.getJSONObject("city").getJSONObject("coord").getDouble("lat"));
            Log.i("Lat", Float.toString((float) json1.getJSONObject("city").getJSONObject("coord").getDouble("lat")));
            preferences.setLongitude((float) json1.getJSONObject("city").getJSONObject("coord").getDouble("lon"));
            Log.i("Lon", Float.toString((float) json1.getJSONObject("city").getJSONObject("coord").getDouble("lon")));
            preferences.setCity(json1.getJSONObject("city").getString("name"));
            int a = (int) Math.round(json0.getJSONObject("main").getDouble("temp"));                        //℃
            cityField.setText(json1.getJSONObject("city").getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json1.getJSONObject("city").getString("country"));
            final String city = json1.getJSONObject("city").getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json1.getJSONObject("city").getString("country");
            cityField.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v) {
                    Snackbar.make(v, city, Snackbar.LENGTH_SHORT)
                            .show();
                }
            });

            Log.i("Location" , "Location Received");
            JSONObject details[] = new JSONObject[10];
            for (int i = 0; i < 10; ++i)
            {
                details[i] = json1.getJSONArray("list").getJSONObject(i);
            }
            Log.i("Objects" , "JSON Objects Created");
            for (int i = 0; i < 10; ++i)
            {
                final JSONObject J = details[i];
                String date1 = details[i].getString("dt");
                Date expiry = new Date(Long.parseLong(date1) * 1000);
                String date = new SimpleDateFormat("EE, dd" , Locale.US).format(expiry);
                SpannableString ss1 = new SpannableString(date + "\n"
                        + details[i].getJSONObject("temp").getLong("max") + "°" + "      "
                        + details[i].getJSONObject("temp").getLong("min") + "°" + "\n");
                ss1.setSpan(new RelativeSizeSpan(1.1f) , 0 , 7 , 0); // set size
                ss1.setSpan(new RelativeSizeSpan(1.4f) , 8 , 12 , 0);
                detailsField[i].setText(ss1);
                Log.i("Details[" + Integer.toString(i) + "]", "Information String " + Integer.toString(i + 1) + " loaded");
                setWeatherIcon(details[i].getJSONArray("weather").getJSONObject(0).getInt("id") , i);
                detailsField[i].setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        bundle = new Bundle();
                        bundle.putString("json" , J.toString());
                        bottomSheetDialogFragment = new CustomBottomSheetDialogFragment();
                        bottomSheetDialogFragment.setArguments(bundle);
                        bottomSheetDialogFragment.show(getActivity().getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                    }
                });
                weatherIcon[i].setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v)
                    {
                        bundle = new Bundle();
                        bundle.putString("json" , J.toString());
                        bottomSheetDialogFragment = new CustomBottomSheetDialogFragment();
                        bottomSheetDialogFragment.setArguments(bundle);
                        bottomSheetDialogFragment.show(getActivity().getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                    }
                });
            }
            final String d1 = new java.text.SimpleDateFormat("hh:mm a" , Locale.US).format(new Date(json0.getJSONObject("sys").getLong("sunrise") * 1000));
            final String d2 = new java.text.SimpleDateFormat("hh:mm a" , Locale.US).format(new Date(json0.getJSONObject("sys").getLong("sunset") * 1000));
            sunriseView.setText(d1);
            sunsetView.setText(d2);
            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = "Son Güncelleme: " + df.format(new Date(json0.getLong("dt")*1000));
            updatedField.setText(updatedOn);
            String humidity = json0.getJSONObject("main").getInt("humidity") + "%";
            humidityView.setText(humidity);

            humidityIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(rootView , "Nem : " + humidityView.getText() , Snackbar.LENGTH_SHORT).show();
                }
            });

            humidityView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(rootView , "Nem : " + humidityView.getText() , Snackbar.LENGTH_SHORT).show();
                }
            });

            Log.i("Humidity Loaded" , "Done");
            String wind = json0.getJSONObject("wind").getDouble("speed") + " m/";
            if (preferences.getUnits().equals("imperial"))
                wind = wind + "h";
            else
                wind = wind + "s";
            windView.setText(wind);
            windIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(rootView , "Rüzgar hızı : " + windView.getText() , Snackbar.LENGTH_SHORT).show();
                }
            });
            windView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(rootView , "Rüzgar hızı : " + windView.getText() , Snackbar.LENGTH_SHORT).show();
                }
            });
            humidityIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(rootView , "Nem : " + humidityView.getText() , Snackbar.LENGTH_SHORT).show();
                }
            });
            humidityView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(rootView , "Nem : " + humidityView.getText() , Snackbar.LENGTH_SHORT).show();
                }
            });
            sunriseIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(rootView , "Gündoğumu : " + sunriseView.getText() , Snackbar.LENGTH_SHORT).show();
                }
            });
            sunriseView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(rootView , "Gündoğumu : " + sunriseView.getText() , Snackbar.LENGTH_SHORT).show();
                }
            });
            sunsetIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(rootView , "Günbatımı : " + sunsetView.getText() , Snackbar.LENGTH_SHORT).show();
                }
            });
            sunsetView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(rootView , "Günbatımı : " + sunsetView.getText() , Snackbar.LENGTH_SHORT).show();
                }
            });

            Log.i("Wind Loaded" , "Done");
            Log.i("10" , "Weather Icon 11 Set");
            setWeatherIcon(json0.getJSONArray("weather").getJSONObject(0).getInt("id"),10);
            Log.i("Set" , "Main Weather Icon");
            weatherIcon[10].setOnClickListener(new View.OnClickListener()
            {
                public void onClick (View v)
                {
                    try {
                        String rs = json0.getJSONArray("weather").getJSONObject(0).getString("description");
                        String[] strArray = rs.split(" ");
                        StringBuilder builder = new StringBuilder();
                        for (String s : strArray) {
                            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                            builder.append(cap.concat(" "));
                        }
                        Snackbar.make(v , "Hey there, " + builder.toString() + "here right now!", Snackbar.LENGTH_SHORT)
                                .show();
                    }
                    catch (Exception e) {
                        Log.e("Error", "Main Weather Icon OnClick Details could not be loaded");
                    }
                }
            });

            String r1 = Integer.toString(a) + "°";
            button.setText(r1);
            int deg = json0.getJSONObject("wind").getInt("deg");
            setDeg(deg);
        }catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

    private void setDeg(int deg) {
        int index = Math.abs(Math.round(deg % 360) / 45);
        switch (index) {
            case 0 : directionView.setText(getActivity().getString(R.string.top));
                directionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view , "Wind blowing towards North" , Snackbar.LENGTH_SHORT).show();
                    }
                });
                break;
            case 1 : directionView.setText(getActivity().getString(R.string.top_right));
                directionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view , "Wind blowing in the North-East direction" , Snackbar.LENGTH_SHORT).show();
                    }
                });
                break;
            case 2 : directionView.setText(getActivity().getString(R.string.right));
                directionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view , "Wind blowing towards East" , Snackbar.LENGTH_SHORT).show();
                    }
                });
                break;
            case 3 : directionView.setText(getActivity().getString(R.string.bottom_right));
                directionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view , "Wind blowing in the South-East direction" , Snackbar.LENGTH_SHORT).show();
                    }
                });
                break;
            case 4 : directionView.setText(getActivity().getString(R.string.down));
                directionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view , "Wind blowing towards South" , Snackbar.LENGTH_SHORT).show();
                    }
                });
                break;
            case 5 : directionView.setText(getActivity().getString(R.string.bottom_left));
                directionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view , "Wind blowing in the South-West direction" , Snackbar.LENGTH_SHORT).show();
                    }
                });
                break;
            case 6 : directionView.setText(getActivity().getString(R.string.left));
                directionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view , "Wind blowing towards West" , Snackbar.LENGTH_SHORT).show();
                    }
                });
                break;
            case 7 : directionView.setText(getActivity().getString(R.string.top_left));
                directionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view , "Wind blowing in the North-West direction" , Snackbar.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }
}
