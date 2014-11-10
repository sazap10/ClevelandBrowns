package de.sachinpan.clevelandbrowns.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.sachinpan.clevelandbrowns.exception.FixturesNotFoundException;

public class FixturesResultsDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "browns.db";
    private static int DATABASE_VERSION = 1;

    private static String TABLE_FIXTURES_RESULTS = "FixturesResults";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_DATE = "Date";
    private static final String COLUMN_HOME_TEAM = "HomeTeam";
    private static final String COLUMN_AWAY_TEAM = "AwayTeam";
    private static final String COLUMN_HOME_TEAM_SCORE = "HomeTeamScore";
    private static final String COLUMN_AWAY_TEAM_SCORE = "AwayTeamScore";
    private static final String COLUMN_WEEK = "WEEK";

    private static final String MATCH_TEAM = COLUMN_HOME_TEAM + " = ? OR " + COLUMN_AWAY_TEAM + " = ?";

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_FIXTURES_RESULTS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_DATE
            + " integer not null, " + COLUMN_HOME_TEAM + " text not null, "
            + COLUMN_HOME_TEAM_SCORE + " integer null, " + COLUMN_AWAY_TEAM +
            " text not null, " + COLUMN_AWAY_TEAM_SCORE + " integer null, " +
            COLUMN_WEEK + " text not null);";

    public FixturesResultsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(FixturesResultsDatabase.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FIXTURES_RESULTS);
        onCreate(db);
    }

    /**
     * Takes a single <code>FixtureResult</code> and adds it to the database.
     * If the fixture already exists then it replaces the found record.
     * @param item The FixtureResult to insert.
     * @return If the insert was successful.
     */
    public boolean insertFixtureResult(FixtureResult item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_DATE, FixtureResult.dateStringToEpoch(item.getDate()));
        contentValues.put(COLUMN_HOME_TEAM, item.getHomeTeam());
        contentValues.put(COLUMN_AWAY_TEAM, item.getAwayTeam());
        contentValues.put(COLUMN_WEEK, item.getWeek());
        if (item.getHomeScore() != null) {
            contentValues.put(COLUMN_HOME_TEAM_SCORE, item.getHomeScore());
        }
        if (item.getAwayScore() != null) {
            contentValues.put(COLUMN_AWAY_TEAM_SCORE, item.getAwayScore());
        }
        int id = getFixture(item);
        boolean success;
        if(id != -1){
            contentValues.put(COLUMN_ID,id);
            success = (db.replace(TABLE_FIXTURES_RESULTS, null, contentValues) != -1);
        }else{
            success = (db.insert(TABLE_FIXTURES_RESULTS, null, contentValues) != -1);
        }
        db.close();
        return success;
    }

    /**
     * Gets the next game for the specified team.
     * @param teamName team to retrieve the next game for.
     * @return The object containing the fixture.
     */
    private FixtureResult getNextGame(String teamName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String where = COLUMN_DATE + " > ? AND ( " + MATCH_TEAM + ")";
        String[] selectionArgs = new String[]{String.valueOf(new Date().getTime()), teamName, teamName};
        String orderBy = COLUMN_DATE + " ASC";
        Cursor cursor = db.query(TABLE_FIXTURES_RESULTS, null, where, selectionArgs, null, null, orderBy, "1");
        FixtureResult fixture = null;
        if (!cursor.moveToFirst()) {
            fixture = cursorToFixture(cursor);
        }
        cursor.close();
        db.close();
        return fixture;
    }

    /**
     * Gets the previous two games for the specified team.
     * @param teamName team to retrieve the previous two games for.
     * @return A list of the previous two results.
     */
    private List<FixtureResult> getLastTwoGames(String teamName) {
        List<FixtureResult> lastTwoGames = new ArrayList<FixtureResult>();
        SQLiteDatabase db = this.getReadableDatabase();
        String where = COLUMN_DATE + " < ? AND ( " + MATCH_TEAM + ")";
        String[] selectionArgs = new String[]{String.valueOf(new Date().getTime()), teamName, teamName};
        String orderBy = COLUMN_DATE + " DESC";
        Cursor cursor = db.query(TABLE_FIXTURES_RESULTS, null, where, selectionArgs, null, null, orderBy, "2");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            FixtureResult fixture = cursorToFixture(cursor);
            lastTwoGames.add(fixture);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return lastTwoGames;
    }

    /**
     * Convenience method for getting the three games required for the home header
     * @param teamName team to retrieve fixtures fo.
     * @return A list of 2 previous results and next game.
     * @throws FixturesNotFoundException if any data is wrong.
     */
    public List<FixtureResult> getHomeHeaderGames(String teamName) throws FixturesNotFoundException {
        List<FixtureResult> lastTwoGames = getLastTwoGames(teamName);
        if (lastTwoGames.isEmpty() || lastTwoGames.size() != 2) {
            throw new FixturesNotFoundException();
        }
        Collections.reverse(lastTwoGames);
        FixtureResult nextGame = getNextGame(teamName);
        if (nextGame == null) {
            throw new FixturesNotFoundException();
        }
        lastTwoGames.add(nextGame);
        return lastTwoGames;
    }

    /**
     * Gets the fixtures or results for a specified team.
     * @param teamName Team to retrieve fixtures and results for.
     * @param isResult Determines what is returned, true if results required
     * @return A list of fixtures or results for the specified team
     */
    public List<FixtureResult> getFixturesResultsForTeam(String teamName, boolean isResult){
        List<FixtureResult> fixtureList = new ArrayList<FixtureResult>();
        SQLiteDatabase db = this.getReadableDatabase();
        String comparator = isResult? "<":">";
        String where = COLUMN_DATE + " "+ comparator+" ? AND ( " + MATCH_TEAM + ")";
        String[] selectionArgs = new String[]{String.valueOf(new Date().getTime()), teamName, teamName};
        String orderBy = COLUMN_DATE + " ASC";
        Cursor cursor = db.query(TABLE_FIXTURES_RESULTS, null, where, selectionArgs, null, null, orderBy);
        while (!cursor.isAfterLast()) {
            FixtureResult fixture = cursorToFixture(cursor);
            fixtureList.add(fixture);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return fixtureList;
    }

    /**
     * Gets the fixtures or results for all teams.
     * @param isResult  Determines what is returned, true if results required
     * @return  A list of fixtures or results for all teams.
     */
    public List<FixtureResult> getFixturesForAll(boolean isResult){
        List<FixtureResult> fixtureList = new ArrayList<FixtureResult>();
        SQLiteDatabase db = this.getReadableDatabase();
        String comparator = isResult? "<":">";
        String where = COLUMN_DATE + " " + comparator+" ?";
        String[] selectionArgs = new String[]{String.valueOf(new Date().getTime())};
        String orderBy = COLUMN_DATE + " ASC";
        Cursor cursor = db.query(TABLE_FIXTURES_RESULTS, null, where, selectionArgs, null, null, orderBy);
        while (!cursor.isAfterLast()) {
            FixtureResult fixture = cursorToFixture(cursor);
            fixtureList.add(fixture);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return fixtureList;
    }

    /**
     * Converts the cursor returned from a query into a FixtureResult object.
     * @param cursor Cursor to convert
     * @return Converted FixtureResult
     */
    private FixtureResult cursorToFixture(Cursor cursor) {
        FixtureResult fixture = new FixtureResult();
        fixture.setDate(FixtureResult.epochToDateString(cursor.getLong(1)));
        fixture.setHomeTeam(cursor.getString(2));
        if (!cursor.isNull(3)) {
            fixture.setHomeScore(cursor.getInt(3));
        }
        fixture.setAwayTeam(cursor.getString(4));
        if (!cursor.isNull(5)) {
            fixture.setAwayScore(cursor.getInt(5));
        }
        fixture.setWeek(cursor.getString(6));
        return fixture;
    }

    /**
     * Gets the fixture with the specified values if exists.
     * @param fixture Fixture to check for.
     * @return The id of the fixtures, if fixture doesn't exist returns -1.
     */
    private int getFixture(FixtureResult fixture) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_DATE + " = ? AND "+COLUMN_HOME_TEAM + " = ? AND " + COLUMN_AWAY_TEAM + " = ?";
        String epoch = String.valueOf(FixtureResult.dateStringToEpoch(fixture.getDate()));
        String[] selectionArgs = new String[]{epoch, fixture.getHomeTeam(),fixture.getAwayTeam()};
        Cursor cursor = db.query(TABLE_FIXTURES_RESULTS,null,selection,selectionArgs,null,null,null);
        int id=-1;
        if(cursor.moveToNext()){
            id = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return id;
    }
}
