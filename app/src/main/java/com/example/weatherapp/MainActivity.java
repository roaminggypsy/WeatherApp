package com.example.weatherapp;
import android.Manifest;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private double localLatitude;
    private double localLongitude;

    private RequestQueue requestQueue;


    private LocationManager locationManager;
    private String provider;

    private static final String TAG = "MainActivity";
    private static final int REQUEST_ALL = 888;

//    // card 1
//    private TextView temperature;
//    private TextView summary;
//    private TextView location;
//    private ImageView currentIcon;
//
//    // card 2
//    private TextView humidity;
//    private TextView windSpeed;
//    private TextView visibility;
//    private TextView pressure;
//
//    // card 3
//    private List<TextView> dates;
//    private List<TextView> lows;
//    private List<TextView> highs;
//    private List<ImageView> icons;

    private String localLocation;
    private String localWeather;
    private List<SummaryFragment> weathers;

    // autocomplete
    private static final int TRIGGER_AUTO_COMPLETE = 50;
    private static final long AUTO_COMPLETE_DELAY = 100;
    private Handler handler;
    private AutoSuggestAdapter autoSuggestAdapter;

    private SharedPreferences sharedPref;

    // view pager related
    private static final int NUM_PAGES = 5;
    private ViewPager mPager;
    private SummaryPagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // set launcher screen
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // set shared preferences
        sharedPref = this.getSharedPreferences("favs", Context.MODE_PRIVATE);
        weathers = new ArrayList<>();

        // permission check and request
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
               getLocation();

            } else {

                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
                || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // false: a) Don't ask me again. b) Permission disabled on the device
                    // yes: Previously rejected and try to access the feature again (user confused)
                    Toast.makeText(this, "Location permission needed", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_ALL);
            }
        }

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new SummaryPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, weathers);
        mPager.setAdapter(pagerAdapter);


        // bind variables to view elements
//        temperature = (TextView) findViewById(R.id.temperature);
//        summary = (TextView) findViewById(R.id.summary);
//        location = (TextView) findViewById(R.id.location);
//        currentIcon = (ImageView) findViewById(R.id.currentIcon);
//
//        humidity = (TextView) findViewById(R.id.humidity);
//        windSpeed = (TextView) findViewById(R.id.windSpeed);
//        visibility = (TextView) findViewById(R.id.visibility);
//        pressure = (TextView) findViewById(R.id.pressure);
//
//        dates = new ArrayList<>();
//        lows = new ArrayList<>();
//        highs = new ArrayList<>();
//        icons = new ArrayList<>();
//
//        for (int i = 1; i <= 8; i++) {
//            int dateID = getResId("date" + i, R.id.class);
//            dates.add((TextView) findViewById(dateID));
//
//            int lowID = getResId("low" + i, R.id.class);
//            lows.add((TextView) findViewById(lowID));
//
//            int highID = getResId("high" + i, R.id.class);
//            highs.add((TextView) findViewById(highID));
//
//            int iconID = getResId("day" + i + "Icon", R.id.class);
//            icons.add((ImageView) findViewById(iconID));
//        }

        // auto-suggest
        autoSuggestAdapter = new AutoSuggestAdapter(this, android.R.layout.simple_dropdown_item_1line);
    }



    public class SummaryPagerAdapter extends FragmentStatePagerAdapter {

        private List<SummaryFragment> fragments;
        private long baseId = 0;

        public SummaryPagerAdapter(FragmentManager fm, int behavior, List<SummaryFragment> fragments) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        public void notifyChangeInPosition(int n) {
            baseId += getCount() + n;
        }

        @Override
        public int getCount() {
            Log.d(TAG, "getCount: 1 +  number of favs " + sharedPref.getAll().size());
            return fragments.size();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // instantiate the RequestQueue
        requestQueue = VolleySingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        // get up-to-date favorites (key: 'city' -> value: 'lat,lon')
        Map<String, ?> allFavs = sharedPref.getAll();

        // get weather for favorite cities
        int i = 0;
        for (final Map.Entry<String, ?> entry : allFavs.entrySet()) {
            String latLonStr = (String) entry.getValue();
            String[] coords = latLonStr.split(",");

            String url = "http://angularweather-env.bjpg59c8ps.us-east-2.elasticbeanstalk.com/currentWeather?lat=" + coords[0] + "&lon=" + coords[1];

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    SummaryFragment newFragment = SummaryFragment.newInstance(response.toString(), entry.getKey(), pagerAdapter, weathers);
                    if (!weathers.contains(newFragment)) {
                        weathers.add(newFragment);
                        pagerAdapter.notifyDataSetChanged();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        }


    }

    private void getLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> list = locationManager.getProviders(true);

        if (list.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(this, "请检查网络或GPS是否打开", Toast.LENGTH_LONG).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                localLatitude = location.getLatitude();
                localLongitude = location.getLongitude();

                String url = "http://ip-api.com/json";

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "ipapi: " + response.toString());

                            String city = response.getString("city");
                            String state = response.getString("region");
                            String country = response.getString("countryCode");
                            localLocation = city + ", " + state + ", " + country;

//                            MainActivity.this.location.setText(city + ", " + state + ", " + country);

                        } catch (final JSONException e) {
                            Log.e(TAG, "onResponse: Json parsing error: " + e.getMessage() );
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "ipapi: " + error.toString());
                    }
                });

                VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

                getLocalWeather();
            }

            locationManager.requestLocationUpdates(provider, 2000, 2, this);
        }

    }

    private void getLocalWeather() {
        String url = "http://angularweather-env.bjpg59c8ps.us-east-2.elasticbeanstalk.com/currentWeather?lat=" + localLatitude + "&lon=" + localLongitude;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("weather", response.toString());
                localWeather = response.toString();
                SummaryFragment fragment = SummaryFragment.newInstance(localWeather, localLocation, pagerAdapter, weathers);
                if (!weathers.contains(fragment)) {
                    weathers.add(0, SummaryFragment.newInstance(localWeather, localLocation, pagerAdapter, weathers));
                }
                pagerAdapter.notifyDataSetChanged();
//                try {
//                    DecimalFormat df = new DecimalFormat("#.##");
//                    df.setRoundingMode(RoundingMode.CEILING);
//
//                    JSONObject currently = response.getJSONObject("currently");
//
//                    // set card 1 data
//                    int currentIconID = getIconID(currently.getString("icon"));
//                    currentIcon.setImageResource(currentIconID);
//
//                    int temperatureVal = (int) Math.round(currently.getDouble("temperature"));
//                    temperature.setText(temperatureVal + "°F");
//
//                    String summaryVal = currently.getString("summary");
//                    summary.setText(summaryVal);
//
//                    // set card 2 data
//                    String humidityVal = df.format(Math.round(currently.getDouble("humidity") * 100));
//                    String windSpeedVal = df.format(currently.getDouble("windSpeed"));
//                    String visibilityVal = df.format(currently.getDouble("visibility"));
//                    String pressureVal = df.format(currently.getDouble("pressure"));
//
//                    humidity.setText(humidityVal + "%");
//                    windSpeed.setText(windSpeedVal + " mph");
//                    visibility.setText(visibilityVal + " km");
//                    pressure.setText(pressureVal + " mb");
//
//                    // set card3 data
//                    JSONArray data = response.getJSONObject("daily").getJSONArray("data");
//
//                    for (int i = 0; i <= 7; i++) {
//                        Date date = new Date(Long.parseLong(data.getJSONObject(i).getString("time")) * 1000);
//                        dates.get(i).setText(new SimpleDateFormat("MM/dd/yyyy").format(date));
//
//                        int low = (int) Math.round(data.getJSONObject(i).getDouble("temperatureLow"));
//                        lows.get(i).setText(low + "");
//
//                        int high = (int) Math.round(data.getJSONObject(i).getDouble("temperatureHigh"));
//                        highs.get(i).setText(high + "");
//
//                        int iconID = getIconID(data.getJSONObject(i).getString("icon"));
//                        icons.get(i).setImageResource(iconID);
//                    }
//
//                } catch (final JSONException e) {
//                    Log.e(TAG, "onResponse: Json parsing error: " + e.getMessage() );
//                };

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbarmenu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchableActivity.class)));
//        searchView.setSubmitButtonEnabled(true);

        // Get SearchView autocomplete object
        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        searchAutoComplete.setThreshold(1);
        searchAutoComplete.setBackgroundColor(Color.rgb(29, 29, 29)); // 29

        // Create a new ArrayAdapter and add data to search auto complete object.
        searchAutoComplete.setAdapter(autoSuggestAdapter);
        searchAutoComplete.setDropDownBackgroundResource(R.color.colorWhite);

        searchAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(searchAutoComplete.getText())) {
                        String url = "http://angularweather-env.bjpg59c8ps.us-east-2.elasticbeanstalk.com/place?input=" + searchAutoComplete.getText();

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                List<String> stringList = new ArrayList<>();

                                try {
                                    Log.d(TAG, "onResponse: predictions" + response);
                                    JSONArray array = response.getJSONArray("predictions");
                                    for (int i = 0; i < array.length(); i++) {
                                        stringList.add(array.getJSONObject(i).getString("description"));
                                    }

                                } catch (final JSONException e) {
                                    Log.e(TAG, "onResponse: Json parsing error: " + e.getMessage() );
                                }

                                autoSuggestAdapter.setData(stringList);
                                autoSuggestAdapter.notifyDataSetChanged();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG, "ipapi: " + error.toString());
                            }
                        });

                        VolleySingleton.getInstance(MainActivity.this).addToRequestQueue(jsonObjectRequest);
                    }
                }
                return false;
            }
        });

        // Listen to search view item on click event.
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                String queryString=(String)adapterView.getItemAtPosition(itemIndex);
                searchAutoComplete.setText("" + queryString);
            }
        });

        // Below event is triggered when submit search query.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                String url = "http://angularweather-env.bjpg59c8ps.us-east-2.elasticbeanstalk.com/currentWeather?street=&city=" + query + "&state=?";

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "got a response :" + response.toString());
                            if (!response.isNull("error")) {
                                new AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                                        .setTitle("Error")
                                        .setMessage("This is an invalid city. Please enter correct a city name.")

                                        // Specifying a listener allows you to take an action before dismissing the dialog.
                                        // The dialog is automatically dismissed when a dialog button is clicked.
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Continue with delete operation
                                            }
                                        })

                                        // A null listener allows the button to dismiss the dialog and take no further action.
                                        .setNegativeButton(android.R.string.no, null)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            } else {
                                Intent intent = new Intent(getApplicationContext(), SearchableActivity.class);
                                intent.putExtra(SearchManager.QUERY, query);
                                intent.putExtra("weather", response.toString() );
                                intent.setAction(Intent.ACTION_SEARCH);
                                startActivity(intent);
                            }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "error : " + error.toString());
                    }
                });

                VolleySingleton.getInstance(MainActivity.this).addToRequestQueue(jsonObjectRequest);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {



            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case REQUEST_ALL:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            new AlertDialog.Builder(MainActivity.this).
                                    setMessage("These permissions are mandatory to get your location. You need to allow them.").
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, REQUEST_ALL);
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null).create().show();

                        }
                        getLocation();
                    }
                }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
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

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    public void removeFragment(String city) {

        for (int i = 0; i < weathers.size(); i++) {
            if (weathers.get(i).getCity().equals(city)) {
                weathers.remove(i);
                pagerAdapter.notifyDataSetChanged();
            }
        }
    }
}


