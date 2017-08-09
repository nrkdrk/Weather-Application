package nd.yazilim.weather.fragment;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import nd.yazilim.weather.R;
import nd.yazilim.weather.activity.GlobalActivity;
import nd.yazilim.weather.activity.WeatherActivity;
import nd.yazilim.weather.preferences.Preferences;

public class FirstLaunchFragment extends Fragment {

    View rootView;
    EditText cityInput;
    TextView message;
    Preferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_first_launch, container, false);
        preferences = new Preferences(getContext());
        cityInput = (EditText) rootView.findViewById(R.id.city_input);
        message = (TextView) rootView.findViewById(R.id.intro_text);
        if (GlobalActivity.i == 0) {
            message.setText(getString(R.string.pick_city));
        }
        else {
            message.setText(getString(R.string.uh_oh));
        }
        Button goButton = (Button) rootView.findViewById(R.id.go_button);
        goButton.setText(getString(R.string.first_go_text));
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cityInput.getText().length() > 0) {
                    preferences.setCity(cityInput.getText().toString());
                    Intent intent = new Intent(getActivity(), WeatherActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    Log.i("Loaded", "Weather");
                    startActivity(intent);
                    Log.i("Changed", "City");
                }
                else {
                    Snackbar.make(rootView , "Enter a City Name First" , Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        return rootView;
    }
}
