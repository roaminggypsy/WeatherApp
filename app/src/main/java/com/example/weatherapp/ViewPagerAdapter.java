package com.example.weatherapp;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private String title[] = {"TODAY", "WEEKLY", "PHOTOS"};
    private String weather;
    private String city;

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    public void setWeather(String weather) {
        this.weather = weather;
        notifyDataSetChanged();
    }

    public void setCity(String city) {
        this.city = city;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = todayFragment.getInstance(weather);
                break;
            case 1:
                fragment = weeklyFragment.getInstance(weather);
                break;
            default:
                fragment = PhotoFragment.newInstance(1);
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return title.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }
}
