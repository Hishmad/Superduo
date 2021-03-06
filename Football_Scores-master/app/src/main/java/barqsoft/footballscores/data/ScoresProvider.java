package barqsoft.footballscores.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import barqsoft.footballscores.R;
import barqsoft.footballscores.data.DatabaseContract;
import barqsoft.footballscores.data.ScoresDBHelper;

/**
 * Created by yehya khaled on 2/25/2015.
 */
public class ScoresProvider extends ContentProvider {


    // Constant
    private static final int MATCHES = 100;
    private static final int MATCHES_WITH_LEAGUE = 101;
    private static final int MATCHES_WITH_ID = 102;
    private static final int MATCHES_WITH_DATE = 103;

    private static final SQLiteQueryBuilder ScoreQuery =
            new SQLiteQueryBuilder();

    private static final String SCORES_BY_LEAGUE = DatabaseContract.scores_table.LEAGUE_COL + " = ?";

    private static final String SCORES_BY_DATE =
            DatabaseContract.scores_table.DATE_COL + " LIKE ?";

    private static final String SCORES_BY_ID =
            DatabaseContract.scores_table.MATCH_ID + " = ?";

    private static final String MATCHER_ONE = "league";
    private static final String MATCHER_TWO = "id";
    private static final String MATCHER_THREE = "date";
    private static final String ID_TAG = "_id";

    // Memeber variable
    private static ScoresDBHelper mOpenHelper;
    private UriMatcher muriMatcher = buildUriMatcher(); // Encapsulate


    /**
     * Static Method
     * @return UriMatcher
     */
    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DatabaseContract.BASE_CONTENT_URI.toString();
        matcher.addURI(authority, null, MATCHES);
        matcher.addURI(authority, MATCHER_ONE, MATCHES_WITH_LEAGUE);
        matcher.addURI(authority, MATCHER_TWO, MATCHES_WITH_ID);
        matcher.addURI(authority, MATCHER_THREE, MATCHES_WITH_DATE);
        return matcher;
    }

    /**
     *
     * @param uri
     * @return -1 if no match
     */
    private int match_uri(Uri uri) {
        String link = uri.toString();
        {
            if (link.contentEquals(DatabaseContract.BASE_CONTENT_URI.toString())) {
                return MATCHES;
            } else if (link.contentEquals(DatabaseContract.scores_table.buildScoreWithDate().toString())) {
                return MATCHES_WITH_DATE;
            } else if (link.contentEquals(DatabaseContract.scores_table.buildScoreWithId().toString())) {
                return MATCHES_WITH_ID;
            } else if (link.contentEquals(DatabaseContract.scores_table.buildScoreWithLeague().toString())) {
                return MATCHES_WITH_LEAGUE;
            }
        }
        return -1;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new ScoresDBHelper(getContext());
        return false;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        final int match = muriMatcher.match(uri);
        switch (match) {
            case MATCHES:
                return DatabaseContract.scores_table.CONTENT_TYPE;
            case MATCHES_WITH_LEAGUE:
                return DatabaseContract.scores_table.CONTENT_TYPE;
            case MATCHES_WITH_ID:
                return DatabaseContract.scores_table.CONTENT_ITEM_TYPE;
            case MATCHES_WITH_DATE:
                return DatabaseContract.scores_table.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.unknown_uri) + uri);
        }
    }

    /**
     * To query from the database
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        int match = match_uri(uri);

        switch (match) {
            case MATCHES:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DatabaseContract.SCORES_TABLE,
                        projection, null, null, null, null, sortOrder);
                break;
            case MATCHES_WITH_DATE:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DatabaseContract.SCORES_TABLE,
                        projection, SCORES_BY_DATE, selectionArgs, null, null, sortOrder);
                break;
            case MATCHES_WITH_ID:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DatabaseContract.SCORES_TABLE,
                        projection, SCORES_BY_ID, selectionArgs, null, null, sortOrder);
                break;
            case MATCHES_WITH_LEAGUE:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DatabaseContract.SCORES_TABLE,
                        projection, SCORES_BY_LEAGUE, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.unknown_uri) + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /**
     * To add into the database
     * @param uri
     * @param values
     * @return
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        return null;
    }

    /**
     * To add more the one record at once
     * @param uri
     * @param values
     * @return
     */
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (match_uri(uri)) {
            case MATCHES:
                db.beginTransaction();
                int returncount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(DatabaseContract.SCORES_TABLE, null, value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returncount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returncount;
            default:
                return super.bulkInsert(uri, values);
        }

    }

    /**
     * To delete a row from the database
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }
}
