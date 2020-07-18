package com.example.weatherapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

public class todayFragment extends Fragment {

    private String weather;

    public static Fragment getInstance(String weather) {
        Bundle bundle = new Bundle();
        bundle.putString("weather", weather);
        todayFragment todayFragment = new todayFragment();
        todayFragment.setArguments(bundle);
        return todayFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.weather = getArguments().getString("weather");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_today, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            JSONObject current = (new JSONObject(weather)).getJSONObject("currently");

            TextView windSpeed = view.findViewById(R.id.windSpeedVal);
            Double windSpeedVal = current.getDouble("windSpeed");
            if (windSpeedVal != null) {
                windSpeed.setText(String.format("%.2f", windSpeedVal) + " mph");
            }

            TextView pressure = view.findViewById(R.id.pressureVal);
            Double pressureVal = current.getDouble("pressure");
            if (pressureVal != null) {
                pressure.setText(String.format("%.2f", pressureVal) + " mb");
            }

            TextView precipitation = view.findViewById(R.id.precipitationVal);
            Double precipitationVal = current.getDouble("precipIntensity");
            if (precipitationVal != null) {
                precipitation.setText(String.format("%.2f", precipitationVal) + " mmph");
            }

            TextView temperature = view.findViewById(R.id.temperatureVal);
            Double temperatureVal = current.getDouble("temperature");
            if (temperatureVal != null) {
                temperature.setText(Math.round(temperatureVal) + "°F");
            }

            TextView summary = view.findViewById(R.id.summaryText);
            String iconStr = current.getString("icon").replace("-", " ");
            if (iconStr.equals("partly cloudy day")) {
                iconStr = "cloudy day";
            }
            if (iconStr.equals("partly cloudy night")) {
                iconStr = "cloudy night";
            }
            if (iconStr != null) {
                summary.setText(iconStr);
            }


            ImageView icon = view.findViewById(R.id.summaryIcon);
            switch (iconStr) {
                case "clear day":
                    icon.setImageResource(R.drawable.weather_sunny);
                    break;
                case "clear night":
                    icon.setImageResource(R.drawable.weather_night);
                    break;
                case "rain":
                    icon.setImageResource(R.drawable.weather_rainy);
                    break;
                case "sleet":
                    icon.setImageResource(R.drawable.weather_snowy_rainy);
                    break;
                case "snow":
                    icon.setImageResource(R.drawable.weather_snowy);
                    break;
                case "wind":
                    icon.setImageResource(R.drawable.weather_windy_variant);
                    break;
                case "fog":
                    icon.setImageResource(R.drawable.weather_fog);
                    break;
                case "cloudy":
                    icon.setImageResource(R.drawable.weather_cloudy);
                    break;
                case "cloudy night":
                    icon.setImageResource(R.drawable.weather_night_partly_cloudy);
                    break;
                case "cloudy day":
                    icon.setImageResource(R.drawable.weather_partly_cloudy);
                    break;
            }

            TextView humidity = view.findViewById(R.id.humidityVal);
            Double humidityVal = current.getDouble("humidity");
            if (humidityVal != null) {
                humidity.setText(String.format("%.2f", humidityVal) + "%");
            }

            TextView visibility = view.findViewById(R.id.visibilityVal);
            Double visibilityVal = current.getDouble("visibility");
            if (visibilityVal != null) {
                visibility.setText(String.format("%.2f", visibilityVal) + " km");
            }

            TextView cloudCover = view.findViewById(R.id.cloudCoverVal);
            Double cloudCoverVal = current.getDouble("cloudCover");
            if (cloudCoverVal != null) {
                cloudCover.setText(String.format("%.2f", cloudCoverVal) + "%");
            }

            TextView ozone = view.findViewById(R.id.ozoneVal);
            Double ozoneVal = current.getDouble("ozone");
            if (ozoneVal != null) {
                ozone.setText(String.format("%.2f", ozoneVal) + " DU");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
