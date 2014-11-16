package de.sachinpan.clevelandbrowns;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FixturesFragment extends Fragment {
    //list containing the fixtures for the week
    //private ListView fixturesList;
    //title containing the week number
    private TextView weekTitle;


    private final static String LOG_FIXTURES = "FIXTURES";

    public FixturesFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_fixtures, container, false);
        //setup list view
        //fixturesList = (ListView) rootView.findViewById(R.id.fixtures_list_view);

        //setup week title
        weekTitle = (TextView) rootView.findViewById(R.id.week_title);


        return rootView;
    }


}
