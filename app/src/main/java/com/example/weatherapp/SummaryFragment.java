package com.example.weatherapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SummaryFragment extends Fragment {

    private String weather;
    private String city;

    private static PagerAdapter theAdapter;
    private static List<SummaryFragment> theWeathers;

    // card 1
    private CardView card1;
    private TextView temperature;
    private TextView summary;
    private TextView location;
    private ImageView currentIcon;

    // card 2
    private TextView humidity;
    private TextView windSpeed;
    private TextView visibility;
    private TextView pressure;

    // card 3
    private List<TextView> dates;
    private List<TextView> lows;
    private List<TextView> highs;
    private List<ImageView> icons;

    private FloatingActionButton favBtn;
    private SharedPreferences sharedPref;

    public SummaryFragment() {

    }

    public static SummaryFragment newInstance(String weather, String city, MainActivity.SummaryPagerAdapter adapter, List<SummaryFragment> weathers) {
        
        Bundle args = new Bundle();

        theAdapter = adapter;
        theWeathers = weathers;

        SummaryFragment fragment = new SummaryFragment();
        args.putString("weather", weather);
        args.putString("city", city);

        Log.d("Initial Summary", "weather: " + weather);
        Log.d("Initial Summary", "city: " + city);

        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.summary, container, false);

        this.city = getArguments().getString("city");

        card1 = rootView.findViewById(R.id.card1);

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(card1.getContext(), DetailActivity.class);
                intent.putExtra("weather", getArguments().getString("weather"));
                intent.putExtra("city", getArguments().getString("city"));
                Log.d("Summary Card clicked", "weather: " + getArguments().getString("weather"));
                Log.d("Summary Card clicked", "city: " + getArguments().getString("city"));
                startActivity(intent);
            }
        });

        temperature = (TextView) rootView.findViewById(R.id.temperature);
        summary = (TextView) rootView.findViewById(R.id.summary);
        location = (TextView) rootView.findViewById(R.id.location);
        currentIcon = (ImageView) rootView.findViewById(R.id.currentIcon);

        location.setText(getArguments().getString("city"));

        humidity = (TextView) rootView.findViewById(R.id.humidity);
        windSpeed = (TextView) rootView.findViewById(R.id.windSpeed);
        visibility = (TextView) rootView.findViewById(R.id.visibility);
        pressure = (TextView) rootView.findViewById(R.id.pressure);

        dates = new ArrayList<>();
        lows = new ArrayList<>();
        highs = new ArrayList<>();
        icons = new ArrayList<>();

        for (int i = 1; i <= 8; i++) {
            int dateID = getResId("date" + i, R.id.class);
            dates.add((TextView) rootView.findViewById(dateID));

            int lowID = getResId("low" + i, R.id.class);
            lows.add((TextView) rootView.findViewById(lowID));

            int highID = getResId("high" + i, R.id.class);
            highs.add((TextView) rootView.findViewById(highID));

            int iconID = getResId("day" + i + "Icon", R.id.class);
            icons.add((ImageView) rootView.findViewById(iconID));
        }

        String weatherStr = getArguments().getString("weather");
        JSONObject weather = null;
        try {
            Log.d("Fragment", "" + weatherStr);
            weather = new JSONObject(weatherStr);

            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.CEILING);

            JSONObject currently = weather.getJSONObject("currently");

            // set card 1 data
            int currentIconID = getIconID(currently.getString("icon"));
            currentIcon.setImageResource(currentIconID);

            int temperatureVal = (int) Math.round(currently.getDouble("temperature"));
            temperature.setText(temperatureVal + "°F");

            String summaryVal = currently.getString("summary");
            summary.setText(summaryVal);

            // set card 2 data
            String humidityVal = df.format(Math.round(currently.getDouble("humidity") * 100));
            String windSpeedVal = df.format(currently.getDouble("windSpeed"));
            String visibilityVal = df.format(currently.getDouble("visibility"));
            String pressureVal = df.format(currently.getDouble("pressure"));

            humidity.setText(humidityVal + "%");
            windSpeed.setText(windSpeedVal + " mph");
            visibility.setText(visibilityVal + " km");
            pressure.setText(pressureVal + " mb");

            // set card3 data
            JSONArray data = weather.getJSONObject("daily").getJSONArray("data");

            for (int i = 0; i <= 7; i++) {
                Date date = new Date(Long.parseLong(data.getJSONObject(i).getString("time")) * 1000);
                dates.get(i).setText(new SimpleDateFormat("MM/dd/yyyy").format(date));

                int low = (int) Math.round(data.getJSONObject(i).getDouble("temperatureLow"));
                lows.get(i).setText(low + "");

                int high = (int) Math.round(data.getJSONObject(i).getDouble("temperatureHigh"));
                highs.get(i).setText(high + "");

                int iconID = getIconID(data.getJSONObject(i).getString("icon"));
                icons.get(i).setImageResource(iconID);
            }


            favBtn =  (FloatingActionButton) rootView.findViewById(R.id.favBtn);
            favBtn.setColorFilter(Color.BLACK);
            favBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = sharedPref.edit();

                        editor.remove(location.getText().toString());
                        editor.commit();
                        Toast.makeText(getActivity(), location.getText() + " was removed from favorites", Toast.LENGTH_SHORT).show();
                        ((MainActivity)getActivity()).removeFragment(location.getText().toString());

                }
            });

            sharedPref = getActivity().getSharedPreferences("favs", Context.MODE_PRIVATE);
            if (!sharedPref.contains(location.getText().toString())) {
                favBtn.hide();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rootView;
    }


    public int getIconID(String iconName) {
        switch (iconName) {
            case "clear-day":
                return getResId("weather_sunny", R.drawable.class);
            case "clear-night":
                return getResId("weather_night", R.drawable.class);
            case "rain":
                return getResId("weather_rainy", R.drawable.class);
            case "sleet":
                return getResId("weather_snowy_rainy", R.drawable.class);
            case "snow":
                return getResId("weather_snowy", R.drawable.class);
            case "wind":
                return getResId("weather_windy_variant", R.drawable.class);
            case "fog":
                return getResId("weather_fog", R.drawable.class);
            case "cloudy":
                return getResId("weather_cloudy", R.drawable.class);
            case "partly-cloudy-night":
                return getResId("weather_night_partly_cloudy", R.drawable.class);
            case "partly-cloudy-day":
                return getResId("weather_partly_cloudy", R.drawable.class);
            default:
                return getResId("weather_sunny", R.drawable.class);
        }
    }

    public int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public String getCity() {
        return city;
    }
}
