package barqsoft.footballscores.support;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import barqsoft.footballscores.R;
import barqsoft.footballscores.support.Utilies;
import barqsoft.footballscores.support.ViewHolder;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ScoresAdapter extends CursorAdapter {
    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_DATE = 1;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_ID = 8;
    public static final int COL_MATCHTIME = 2;
    private static final String INTENT_SHARE_TYPE = "text/plain";


    public double detail_match_id = 0;
    private String FOOTBALL_SCORES_HASHTAG = "#Football_Scores";

    public ScoresAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View mItem = LayoutInflater.from(context).inflate(R.layout.scores_list_item, parent, false);
        ViewHolder mHolder = new ViewHolder(mItem);
        mItem.setTag(mHolder);
        //Log.v(FetchScoreTask.LOG_TAG,"new View inflated");
        return mItem;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        if((cursor.getInt(COL_HOME_GOALS) < 0) || (cursor.getInt(COL_AWAY_GOALS) < 0)){
            view.setContentDescription(cursor.getString(COL_HOME) + context.getString(R.string.versus) +  cursor.getString(COL_AWAY)
                    + ". " +  cursor.getString(COL_MATCHTIME) );
        } else {
            view.setContentDescription(cursor.getString(COL_HOME) + " " + cursor.getInt(COL_HOME_GOALS)
                    + ", " + cursor.getString(COL_AWAY) + " " + cursor.getInt(COL_AWAY_GOALS)
                    + ". " +  cursor.getString(COL_MATCHTIME) );
        }

        final ViewHolder mHolder = (ViewHolder) view.getTag();

        mHolder.homeName.setText(cursor.getString(COL_HOME));
        mHolder.awayName.setText(cursor.getString(COL_AWAY));
        mHolder.date.setText(cursor.getString(COL_MATCHTIME));
        mHolder.score.setText(Utilies.getScores(cursor.getInt(COL_HOME_GOALS), cursor.getInt(COL_AWAY_GOALS)));
        mHolder.match_id = cursor.getDouble(COL_ID);
        mHolder.homeCrest.setImageResource(Utilies.getTeamCrestByTeamName(
                cursor.getString(COL_HOME)));
        mHolder.homeCrest.setContentDescription(context.getString(R.string.home_crest) + cursor.getString(COL_HOME));
        mHolder.awayCrest.setImageResource(Utilies.getTeamCrestByTeamName(
                cursor.getString(COL_AWAY)
        ));
        mHolder.homeCrest.setContentDescription(context.getString(R.string.away_crest) + cursor.getString(COL_AWAY));
        //Log.v(FetchScoreTask.LOG_TAG,mHolder.homeName.getText() + " Vs. " + mHolder.awayName.getText() +" id " + String.valueOf(mHolder.match_id));
        //Log.v(FetchScoreTask.LOG_TAG,String.valueOf(detail_match_id));
        LayoutInflater vi = (LayoutInflater) context.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.detail_fragment, null);

        ViewGroup container = (ViewGroup) view.findViewById(R.id.details_fragment_container);
        if (mHolder.match_id == detail_match_id) {
            //Log.v(FetchScoreTask.LOG_TAG,"will insert extraView");

            container.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT));

            TextView match_day = (TextView) v.findViewById(R.id.matchday_textview);
            match_day.setText(Utilies.getMatchDay(context, cursor.getInt(COL_MATCHDAY),
                    cursor.getInt(COL_LEAGUE)));

            TextView league = (TextView) v.findViewById(R.id.league_textview);
            league.setText(Utilies.getLeague(context, cursor.getInt(COL_LEAGUE)));

            Button shareButton = (Button) v.findViewById(R.id.share_button);
            shareButton.setContentDescription(context.getString(R.string.content_description_share_button));
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //add Share Action
                    context.startActivity(createShareForecastIntent(mHolder.homeName.getText() + " "
                            + mHolder.score.getText() + " " + mHolder.awayName.getText() + " "));
                }
            });
        }
        else {
            container.removeAllViews();
        }
    }

    /**
     * Shre with other app using implicit intent.
     * @param ShareText
     * @return
     */
    public Intent createShareForecastIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType(INTENT_SHARE_TYPE);
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + FOOTBALL_SCORES_HASHTAG);
        return shareIntent;
    }

}
