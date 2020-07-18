package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class DetailActivity extends AppCompatActivity implements PhotoFragment.OnListFragmentInteractionListener  {

    private static final String TAG = "DetailActivity";
    private String weather;
    private String city;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        weather = getIntent().getStringExtra("weather");
        city = getIntent().getStringExtra("city");
        Log.d(TAG, "onCreate: get weather: " + weather);

        // set toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("city"));

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.setWeather(weather);
        adapter.setCity(city);
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.tab_today);
        tabLayout.getTabAt(1).setIcon(R.drawable.tab_weekly);
        tabLayout.getTabAt(2).setIcon(R.drawable.tab_photos);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: twitter id" + item.getItemId() + ", while:" + R.id.twitter );
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.twitter:
                String temp = "";
                try {
                    JSONObject data = new JSONObject(weather);
                    temp = Math.round(data.getJSONObject("currently").getDouble("temperature")) + "°F";
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String text = "Check Out " + city + "'s Weather! It is " + temp + "! #CSCI571WeatherSearch";
                Intent browserIntent = null;
                try {
                    browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/intent/tweet?text=" + URLEncoder.encode(text, "UTF-8")));
                    startActivity(browserIntent);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                Log.d(TAG, "onOptionsItemSelected: option default");
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListFragmentInteraction(PictureItem item) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.toolbarmenu2, menu);
            return true;
    }
}
