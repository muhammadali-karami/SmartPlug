package com.muhammadalikarami.smartplug;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Admin on 4/30/2016.
 */
public class PagerAdapter extends FragmentPagerAdapter {

    FragmentSimple fragmentSimple = new FragmentSimple();
    FragmentSchedule fragmentSchedule = new FragmentSchedule();

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                return fragmentSimple;
            case 1:
                return fragmentSchedule;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
