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

    private static final String MATCH_TEAM = COLUMN_HOME_TEAM + " = ? OR " + COLUMN_AWAY_TEAM + " = ?";

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_FIXTURES_RESULTS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_DATE
            + " integer not null, " + COLUMN_HOME_TEAM + " text not null, "
            + COLUMN_HOME_TEAM_SCORE + " integer null, " + COLUMN_AWAY_TEAM +
            " text not null, " + COLUMN_AWAY_TEAM_SCORE + " integer null" + ");";

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
     *
     * @param item The FixtureResult to insert.
     * @return If the insert was successful.
     */
    public boolean insertFixtureResult(FixtureResult item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_DATE, FixtureResult.dateStringToEpoch(item.getDate()));
        contentValues.put(COLUMN_HOME_TEAM, item.getHomeTeam());
        contentValues.put(COLUMN_AWAY_TEAM, item.getAwayTeam());
        if (item.getHomeScore() != null) {
            contentValues.put(COLUMN_HOME_TEAM_SCORE, item.getHomeScore());
        }
        if (item.getAwayScore() != null) {
            contentValues.put(COLUMN_AWAY_TEAM_SCORE, item.getAwayScore());
        }
        int id =containsFixture(item);
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
        return fixture;
    }

    public int containsFixture(FixtureResult fixture) {
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

//    public boolean containsStory(String title) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res = db.rawQuery("SELECT * FROM Media WHERE Title = '" + title.replace("'", "[apos]") + "'", null);
//        boolean contained = res.getCount() > 0;
//        res.close();
//        db.close();
//        return contained;
//    }
//
//    public boolean deleteStory(String title) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        boolean success = (db.delete("Media", "Title = ? ", new String[]{title}) != 0);
//        db.close();
//        return success;
//    }
//
//    /**
//     * Deletes a number of old entries from the database
//     *
//     * @param count The number of stories to delete.
//     */
//    public void deleteOldStories(int count) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        SQLiteDatabase dbw = this.getWritableDatabase();
//        Cursor res = db.rawQuery("SELECT * FROM Media", null);
//        res.moveToFirst();
//        List<String> titles = new ArrayList<String>();
//        List<String> imgs = new ArrayList<String>();
//        int i = 0;
//        while (i < count && !res.isAfterLast()) {
//            titles.add(res.getString(res.getColumnIndex("Title")).replace("[apos]", "'"));
//
//            String source = res.getString(res.getColumnIndex("Image"));
//            if (source != null && !"spadaro".equals(source)) {
//                imgs.add(res.getString(res.getColumnIndex("Post_ID")));
//            }
//
//            res.moveToNext();
//            i++;
//        }
//
//        for (String img : imgs) {
//            FileHandler.deleteFile(img);
//        }
//
//        for (i = 0; i < titles.size(); i++) {
//            dbw.delete("Media", "Title = ? ", new String[]{titles.get(i)});
//        }
//
//        res.close();
//        db.close();
//        dbw.close();
//    }
//
//    public boolean deleteAllStories() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        SQLiteDatabase dbw = this.getWritableDatabase();
//        Cursor res = db.rawQuery("SELECT * FROM Media", null);
//        res.moveToFirst();
//
//        List<String> imgNames = new ArrayList<String>();
//        for (int i = 0; i < res.getCount(); i++) {
//            String source = res.getString(res.getColumnIndex("Image"));
//            if (source != null && !"spadaro".equals(source)) {
//                imgNames.add(res.getString(res.getColumnIndex("Post_ID")));
//            }
//        }
//
//        for (String img : imgNames) {
//            FileHandler.deleteFile(img);
//        }
//
//        boolean success = (dbw.delete("Media", "1", null) > 0);
//
//        res.close();
//        db.close();
//        dbw.close();
//
//        return success;
//    }
//
//    public NewsItem[] getStories() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res = db.rawQuery("SELECT * FROM Media", null);
//        res.moveToLast();
//
//        NewsItem[] newsItems = new NewsItem[res.getCount()];
//        for (int i = 0; i < newsItems.length; i++) {
//            String id = res.getString(res.getColumnIndex("Post_ID"));
//            String title = res.getString(res.getColumnIndex("Title")).replace("[apos]", "'");
//            String link = res.getString(res.getColumnIndex("Link")).replace("[apos]", "'");
//            String time = res.getString(res.getColumnIndex("Time")).replace("[apos]", "'");
//            String source = res.getString(res.getColumnIndex("Image"));
//
//            Drawable d = null;
//            d = FileHandler.getDrawableFromFile(source);
//
//            newsItems[i] = new NewsItem(link, title, source, time, (short) 0, d);
//            res.moveToPrevious();
//        }
//
//        res.close();
//        db.close();
//
//        return newsItems;
//    }
}
