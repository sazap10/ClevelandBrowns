package de.sachinpan.clevelandbrowns;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeHeaderFragment extends Fragment {

    private ImageView leftTeamLogo;
    private ImageView rightTeamLogo;

    private int leftTeamImage;
    private int leftTeamColour;
    private int righttTeamImage;
    private int rightTeamColour;

    public HomeHeaderFragment() {
        // Required empty public constructor
    }

    public HomeHeaderFragment(int week) {
        switch (week){
            case 0:
                leftTeamImage = R.drawable.tampa_bay_buccaneers;
                leftTeamColour = R.color.tampa_bay_buccaneers;

                righttTeamImage = R.drawable.cleveland_browns;
                rightTeamColour = R.color.cleveland_browns;
                break;
            case 1:
                leftTeamImage = R.drawable.cincinnati_bengals;
                leftTeamColour = R.color.cincinnati_bengals;

                righttTeamImage = R.drawable.cleveland_browns;
                rightTeamColour = R.color.cleveland_browns;
                break;
            case 2:
                leftTeamImage = R.drawable.cleveland_browns;
                leftTeamColour = R.color.cleveland_browns;

                righttTeamImage = R.drawable.houston_texans;
                rightTeamColour = R.color.houston_texans;
                break;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home_header, container, false);

        //setup left team logo imageview
        leftTeamLogo = (ImageView) root.findViewById(R.id.leftTeamLogoIV);

        //setup right team logo imageview
        rightTeamLogo = (ImageView) root.findViewById(R.id.rightTeamLogoIV);

        //setup imagviews temporary with static images
        leftTeamLogo.setBackgroundResource(leftTeamColour);
        leftTeamLogo.setImageResource(leftTeamImage);

        rightTeamLogo.setBackgroundResource(rightTeamColour);
        rightTeamLogo.setImageResource(righttTeamImage);
        return root;
    }


}
