package de.sachinpan.clevelandbrowns.database;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FixtureResult {
    private String homeTeam;
    private String awayTeam;
    private Integer homeScore;
    private Integer awayScore;
    private String date;
    private String week;

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public Integer getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(int homeScore) {
        this.homeScore = homeScore;
    }

    public Integer getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(int awayScore) {
        this.awayScore = awayScore;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Converts the string representation of a date to epoch time for database
     *
     * @param date string to be converted, example: THU, NOV 6 2014 1:00 PM
     * @return epoch time
     */
    public static long dateStringToEpoch(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d yyyy h:mm a");
        try {
            Date parsedDate = formatter.parse(date);
            return parsedDate.getTime();
        } catch (ParseException e) {
            Log.e("FixtureDateException", "Unable to parse date string to date: " + date);
            return 0;
        }

    }

    /**
     * Converts the epoch time long to a String representation
     *
     * @param epochTime epoch time to be converted
     * @return Date string, example:
     */
    public static String epochToDateString(long epochTime) {
        Date epochDate = new Date(epochTime);
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, d MMMMM yyyy h:mm a");
        return formatter.format(epochDate);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Fixture{[ HomeTeam: ");
        builder.append(getHomeTeam());
        builder.append(", Score: ");
        builder.append(getHomeScore());
        builder.append("],[ AwayTeam: ");
        builder.append(getAwayTeam());
        builder.append(", Score: ");
        builder.append(getAwayScore());
        builder.append("], [ Date: ");
        builder.append(getDate());
        builder.append("], [ Week ");
        builder.append(getWeek());
        builder.append("]}");
        return builder.toString();
    }
}
