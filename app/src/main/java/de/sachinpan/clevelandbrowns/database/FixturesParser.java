package de.sachinpan.clevelandbrowns.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.sachinpan.clevelandbrowns.util.TeamName;

public class FixturesParser extends AsyncTask<Void, Void, List<FixtureResult>> {
    Context context;
    TeamName teamName;

    public FixturesParser(Context context) {
        this.context = context;
        teamName = new TeamName(context);
    }

    @Override
    protected List<FixtureResult> doInBackground(Void... params) {
        FixturesResultsDatabase db = new FixturesResultsDatabase(context);
//        db.deleteStory("5 PM: Merrill Reese On Eagles 360");
//        db.deleteStory("Headlines: Defense Looking To Rally For Ryans");
//        Log.e("Eagles", "" + deleted);
        //db.deleteAllStories();
        List<FixtureResult> fixtures = new ArrayList<FixtureResult>();
        try {
            String url = "http://espn.go.com/nfl/schedule/_/week/";
            for (int currentWeek = 1; currentWeek <= 17; currentWeek++) {
                String weekUrl = url + currentWeek;
                Document doc = Jsoup.connect(weekUrl).get();
                String year = doc.select("h1.h2").first().text().split("-")[1].trim();
                Elements tableRows = doc.select("tr.stathead ~ tr");
                String date = "";
                for (Element tableRow : tableRows) {
                    if (tableRow.text().contains("Bye")) {
                        continue;
                    }
                    if (tableRow.hasClass("colhead")) {
                        //extract date
                        date = tableRow.select("td").first().text();
                    } else {
                        String fixtureRowText = tableRow.select("td").first().text();
                        if (fixtureRowText.contains(",")) {
                            String[] scores = fixtureRowText.split(",");
                            String pattern = "(\\D*)(\\d+)(.*)";
                            String leftTeamString = scores[0];
                            String rightTeamString = scores[1];
                            String leftTeam = teamName.placeNameToFullName(leftTeamString.replaceAll(pattern, "$1").trim());
                            String rightTeam = teamName.placeNameToFullName(rightTeamString.replaceAll(pattern, "$1").trim());
                            Integer leftTeamScore = Integer.valueOf(leftTeamString.replaceAll(pattern, "$2"));
                            Integer rightTeamScore = Integer.valueOf(rightTeamString.replaceAll(pattern, "$2"));
                            String dateTime = date + " " + year + " 0:00 AM";
                            FixtureResult fixture = new FixtureResult();
                            fixture.setAwayTeam(leftTeam);
                            fixture.setWeek(String.valueOf(currentWeek));
                            fixture.setAwayScore(leftTeamScore);
                            fixture.setDate(dateTime);
                            fixture.setHomeTeam(rightTeam);
                            fixture.setHomeScore(rightTeamScore);
                            fixtures.add(fixture);
                            //Log.d("FixtureParser",fixture.toString());
                        } else {
                            String[] scores = fixtureRowText.split(" at ");
                            String leftTeam = teamName.placeNameToFullName(scores[0].trim());
                            String rightTeam = teamName.placeNameToFullName(scores[1].trim());
                            String time = tableRow.select("td").get(1).text().trim();
                            if (time.equalsIgnoreCase("TBD")) {
                                time = "0:00 AM";
                            }
                            String dateTime = date + " " + year + " " + time;
                            FixtureResult fixture = new FixtureResult();
                            fixture.setAwayTeam(leftTeam);
                            fixture.setWeek(String.valueOf(currentWeek));
                            fixture.setDate(dateTime);
                            fixture.setHomeTeam(rightTeam);
                            fixtures.add(fixture);
                            // Log.d("FixtureParser",fixture.toString());
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return fixtures;
    }

    @Override
    protected void onPostExecute(List<FixtureResult> fixtureList) {
        FixturesResultsDatabase db = new FixturesResultsDatabase(context);
        db.deleteAllFixtures();
        db.insertFixtures(fixtureList);
        Log.d("Parser", "done");
        Toast.makeText(context,"Database loaded",Toast.LENGTH_LONG).show();
    }
}
