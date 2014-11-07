package de.sachinpan.clevelandbrowns;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.sachinpan.clevelandbrowns.adapters.ViewPagerAdapter;


public class HomeFragment extends Fragment {
    private ViewPager pager;
    public HomeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        pager = (ViewPager) root.findViewById(R.id.pager);

        // Set the ViewPagerAdapter into ViewPager
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);
        pager.setCurrentItem(adapter.getCount()-1);
        return root;
    }

}
