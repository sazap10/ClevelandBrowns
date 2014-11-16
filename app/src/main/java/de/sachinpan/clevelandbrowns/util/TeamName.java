package de.sachinpan.clevelandbrowns.util;

import android.content.Context;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import de.sachinpan.clevelandbrowns.R;

public class TeamName {
    private final Context context;

    public TeamName(Context context){
        this.context = context;
    }

    public String placeNameToFullName(String placeName){
        List<String> placeNameArray = Arrays.asList(context.getResources().getStringArray(R.array.team_place_names));
        String[] nicknamesArray = context.getResources().getStringArray(R.array.team_nicknames);
        if(placeName.startsWith("NY")){
            return placeName.replaceAll("NY","New York");
        }
        int index;
        if((index = placeNameArray.indexOf(placeName))!= -1){
            String nickName = nicknamesArray[index];
            return placeName + " " + nickName;
        }
        Log.d("TeamName: ",placeName);
        return null;
    }

    public String fullTeamNameToResourceName(String teamName){
        List<String> resourceArray = Arrays.asList(context.getResources().getStringArray(R.array.team_resource_string));
        String resourceName = teamName.replaceAll("[. ] ","_").toLowerCase();
        if(resourceArray.contains(resourceName)){
            return resourceName;
        }
        return null;
    }
}
