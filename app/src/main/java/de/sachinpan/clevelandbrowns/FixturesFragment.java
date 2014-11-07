package de.sachinpan.clevelandbrowns;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


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
        new GetFixtures().execute();
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

    private class GetFixtures extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            List<String> fixtureData = new ArrayList<String>();
            try {
                final String fixtureURL = "http://espn.go.com/nfl/schedule";
                Log.d(LOG_FIXTURES, "Connecting to [" + fixtureURL + "]");
                Document doc = Jsoup.connect(fixtureURL).get();
                Log.d(LOG_FIXTURES, "Connected to [" + fixtureURL + "]");
                // Get Week title
                String title = doc.select(".stathead").first().text().split(":")[0];
                Log.d(LOG_FIXTURES, "Title [" + title + "]");
                fixtureData.add(title);
                //Get the fixtures for the week
                //gets all the fixtures separated by date
                Elements tableRows = doc.select("tr.stathead ~ tr");
               // String dateOfFixture="";
                for(Element tableRow: tableRows){
                    if(tableRow.hasClass("colhead")){
                        //extract date
                        String dateData = tableRow.select("td").first().text();
                        SimpleDateFormat date = new SimpleDateFormat("EEE, MMM d");
                        date.parse(dateData);
                        Log.d(LOG_FIXTURES, "Date [" + date + "]");
                    }
                }


//                buffer.append("Title: " + title + "\r\n");
//
//                // Get meta info
//                Elements metaElems = doc.select("meta");
//                buffer.append("META DATA\r\n");
//                for (Element metaElem : metaElems) {
//                    String name = metaElem.attr("name");
//                    String content = metaElem.attr("content");
//                    buffer.append("name ["+name+"] - content ["+content+"] \r\n");
//                }
//
//                Elements topicList = doc.select("h2.topic");
//                buffer.append("Topic list\r\n");
//                for (Element topic : topicList) {
//                    String data = topic.text();
//
//                    buffer.append("Data ["+data+"] \r\n");
//                }

            } catch (Throwable t) {
                t.printStackTrace();
            }
            return fixtureData;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<String> data) {
            super.onPostExecute(data);
            weekTitle.setText(data.get(0));
            //respText.setText(s);
        }
    }

}
