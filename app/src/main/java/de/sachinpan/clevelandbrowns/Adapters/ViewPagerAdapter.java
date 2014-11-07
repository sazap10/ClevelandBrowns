package de.sachinpan.clevelandbrowns.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import de.sachinpan.clevelandbrowns.HomeHeaderFragment;


public class ViewPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 3;
    // Tab Titles
    private String tabtitles[] = new String[]{"Week 9","Week 10", "Week 11"};
    //Context context;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
          return new HomeHeaderFragment(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }
}
