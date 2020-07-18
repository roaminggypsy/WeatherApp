package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Map;

public class SearchableActivity extends AppCompatActivity {

    private static final String TAG = "Search" ;

    private JSONObject weather;
    private String city;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        // bind variables to view elements
        card1 = findViewById(R.id.card1);

        temperature = (TextView) findViewById(R.id.temperature);
        summary = (TextView) findViewById(R.id.summary);
        location = (TextView) findViewById(R.id.location);
        location.setText(getIntent().getStringExtra("query"));
        currentIcon = (ImageView) findViewById(R.id.currentIcon);

        humidity = (TextView) findViewById(R.id.humidity);
        windSpeed = (TextView) findViewById(R.id.windSpeed);
        visibility = (TextView) findViewById(R.id.visibility);
        pressure = (TextView) findViewById(R.id.pressure);

        dates = new ArrayList<>();
        lows = new ArrayList<>();
        highs = new ArrayList<>();
        icons = new ArrayList<>();

        favBtn =  (FloatingActionButton) findViewById(R.id.favBtn);
        favBtn.setColorFilter(Color.BLACK);
        favBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPref.edit();

                if (sharedPref.contains(location.getText().toString())) {
                    editor.remove(location.getText().toString());
                    editor.commit();
                    Toast.makeText(SearchableActivity.this, location.getText() + " was removed from favorites", Toast.LENGTH_SHORT).show();
                    favBtn.setImageResource(R.drawable.fav_add);
                } else {
                    try {
                        Log.d("test shared city", location.getText().toString());
                        Log.d("test shared", weather.getString("latitude") + "," + weather.getString("longitude"));
                        editor.putString(location.getText().toString(), weather.getString("latitude") + "," + weather.getString("longitude"));
                        editor.commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(SearchableActivity.this, location.getText() + " was add to favorites", Toast.LENGTH_SHORT).show();
                    favBtn.setImageResource(R.drawable.fav_remove);
                }
            }
        });

        // change icon according to favorite status
        sharedPref = SearchableActivity.this.getSharedPreferences("favs", Context.MODE_PRIVATE);
        Log.d("favs", "contains " + sharedPref.contains(location.getText().toString()));
        if (sharedPref.contains(location.getText().toString())) {
            favBtn.setImageResource(R.drawable.fav_remove);
        }

        for (int i = 1; i <= 8; i++) {
            int dateID = getResId("date" + i, R.id.class);
            dates.add((TextView) findViewById(dateID));

            int lowID = getResId("low" + i, R.id.class);
            lows.add((TextView) findViewById(lowID));

            int highID = getResId("high" + i, R.id.class);
            highs.add((TextView) findViewById(highID));

            int iconID = getResId("day" + i + "Icon", R.id.class);
            icons.add((ImageView) findViewById(iconID));
        }

        // handleIntent()
        handleIntent(getIntent());

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(card1.getContext(), DetailActivity.class);
                intent.putExtra("weather", weather.toString());
                intent.putExtra("city", getIntent().getStringExtra("query"));
                startActivity(intent);
            }
        });

        // set toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
            setSupportActionBar(myToolbar);
//
//        myToolbar.setTitle(getIntent().getStringExtra(SearchManager.QUERY));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("query"));
//        getSupportActionBar().setTitle(getIntent().getStringExtra(SearchManager.QUERY));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG, "handleIntent: query" + query);

            try {
                weather = new JSONObject(intent.getStringExtra("weather"));

                try {
                    DecimalFormat df = new DecimalFormat("#.00");
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

                } catch (final JSONException e) {
                    Log.e(TAG, "onResponse: Json parsing error: " + e.getMessage() );
                };

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "handleIntent: weather" + weather);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(SearchableActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
}
