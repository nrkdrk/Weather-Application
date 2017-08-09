package nd.yazilim.weather.fragment;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import nd.yazilim.weather.R;
import nd.yazilim.weather.preferences.Preferences;

public class CustomBottomSheetDialogFragment extends BottomSheetDialogFragment {

    TextView windIcon , rainIcon , snowIcon , humidityIcon , pressureIcon;
    TextView windText , rainText , snowText , humidityText , pressureText;
    TextView nightValue , mornValue , dayValue , eveValue;
    TextView condition;
    View rootView;
    Preferences preferences;
    Bundle bundle;
    Typeface weatherFont;
    JSONObject json;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = this.getArguments();
        try {
            json = new JSONObject(bundle.getString("json"));
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_modal , container, false);
        condition = (TextView) rootView.findViewById(R.id.description);
        preferences = new Preferences(getContext());
        nightValue = (TextView) rootView.findViewById(R.id.night_temperature);
        mornValue = (TextView) rootView.findViewById(R.id.morning_temperature);
        dayValue = (TextView) rootView.findViewById(R.id.day_temperature);
        eveValue = (TextView) rootView.findViewById(R.id.evening_temperature);
        windIcon = (TextView) rootView.findViewById(R.id.wind_icon);
        windIcon.setTypeface(weatherFont);
        windIcon.setText(getString(R.string.speed_icon));
        rainIcon = (TextView) rootView.findViewById(R.id.rain_icon);
        rainIcon.setTypeface(weatherFont);
        rainIcon.setText(getString(R.string.rain));
        snowIcon = (TextView) rootView.findViewById(R.id.snow_icon);
        snowIcon.setTypeface(weatherFont);
        snowIcon.setText(getString(R.string.snow));
        humidityIcon = (TextView) rootView.findViewById(R.id.humidity_icon);
        humidityIcon.setTypeface(weatherFont);
        humidityIcon.setText(getString(R.string.humidity_icon));
        pressureIcon = (TextView) rootView.findViewById(R.id.pressure_icon);
        pressureIcon.setTypeface(weatherFont);
        pressureIcon.setText(getString(R.string.pressure_icon));
        windText = (TextView) rootView.findViewById(R.id.wind);
        rainText = (TextView) rootView.findViewById(R.id.rain);
        snowText = (TextView) rootView.findViewById(R.id.snow);
        humidityText = (TextView) rootView.findViewById(R.id.humidity);
        pressureText = (TextView) rootView.findViewById(R.id.pressure);
        updateElements();
        return rootView;
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @Override
    public void setupDialog(Dialog dialog, int style) {
        //super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.dialog_modal, null);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);

        }
    }

    public void updateElements() {
        setCondition();
        setOthers();
        setTemperatures();
    }

    public void setCondition() {
        try {
            String cond = json.getJSONArray("weather").getJSONObject(0).getString("description");
            String[] strArray = cond.split(" ");
            final StringBuilder builder = new StringBuilder();
            for (String s : strArray) {
                String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                builder.append(cap.concat(" "));
            }
            condition.setText(builder.toString());
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public void setOthers() {
        try {
            String wind = "Hız : " + json.getDouble("speed") + " m/";
            if (preferences.getUnits().equals("imperial"))
                wind = wind + "h";
            else
                wind = wind + "s";
            windText.setText(wind);
            try {
                rainText.setText("Yağmur : " + json.getDouble("rain") + " mm");
            }
            catch (JSONException ex) {
                rainText.setText("Yağmur : 0 mm");
            }
            try {
                snowText.setText("Kar : " + json.getDouble("snow") + " mm");
            }
            catch (JSONException ex) {
                snowText.setText("Kar : 0 mm");
            }
            humidityText.setText("Nem : " + json.getDouble("humidity") + " %");
            pressureText.setText("Basınç : " + json.getDouble("pressure") + " hPa");
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public void setTemperatures() {
        try {
            dayValue.setText(Double.toString(json.getJSONObject("temp").getDouble("day")) + "°");
            mornValue.setText(Double.toString(json.getJSONObject("temp").getDouble("morn")) + "°");
            eveValue.setText(Double.toString(json.getJSONObject("temp").getDouble("eve")) + "°");
            nightValue.setText(Double.toString(json.getJSONObject("temp").getDouble("night")) + "°");
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
}