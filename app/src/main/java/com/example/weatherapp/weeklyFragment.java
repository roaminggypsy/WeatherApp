package com.example.weatherapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class weeklyFragment extends Fragment {

    private String weather;

    public static Fragment getInstance(String weather) {
        Bundle bundle = new Bundle();
        bundle.putString("weather", weather);
        weeklyFragment weeklyFragment = new weeklyFragment();
        weeklyFragment.setArguments(bundle);
        return weeklyFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.weather = getArguments().getString("weather");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weekly, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);





        // chart
        LineChart chart = (LineChart) view.findViewById(R.id.chart);

        int[] minTemp = new int[8];
        int[] maxTemp = new int[8];

        List<Entry> minTemps = new ArrayList<>();
        List<Entry> maxTemps = new ArrayList<>();

        try {
            // card
            JSONObject daily = (new JSONObject(weather)).getJSONObject("daily");

            ImageView icon = view.findViewById(R.id.weeklyIcon);
            String iconStr = daily.getString("icon");

            TextView summary = view.findViewById(R.id.weeklySummary);
            summary.setText(daily.getString("summary"));

            switch (iconStr) {
                case "clear-day":
                    icon.setImageResource(R.drawable.weather_sunny);
                    break;
                case "clear-night":
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
                case "partly-cloudy-night":
                    icon.setImageResource(R.drawable.weather_night_partly_cloudy);
                    break;
                case "partly-cloudy-day":
                    icon.setImageResource(R.drawable.weather_partly_cloudy);
                    break;
            }

            JSONArray data = daily.getJSONArray("data");
            for (int i = 0; i <= 7; i++) {
                minTemps.add(new Entry(i, (int) Math.round(data.getJSONObject(i).getDouble("temperatureMin"))));
                maxTemps.add(new Entry(i, (int) Math.round(data.getJSONObject(i).getDouble("temperatureMax"))));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LineDataSet minSet = new LineDataSet(minTemps, "Minimum Temperature");
        minSet.setColor(Color.rgb(255, 166, 58));

        LineDataSet maxSet = new LineDataSet(maxTemps, "Maximum Temperature");
        maxSet.setColor(Color.rgb(192, 136, 246));

        Legend l = chart.getLegend();
        l.setTextColor(Color.WHITE);
        l.setFormSize(15);
        l.setTextSize(15);

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextSize(10f);
        leftAxis.setTextColor(Color.WHITE);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setTextSize(10f);
        rightAxis.setTextColor(Color.WHITE);




        LineData lineData = new LineData(minSet, maxSet);

        chart.setData(lineData);
        chart.invalidate();





    }
}