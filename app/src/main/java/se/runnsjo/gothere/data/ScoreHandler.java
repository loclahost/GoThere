package se.runnsjo.gothere.data;

import android.content.Context;
import android.content.SharedPreferences;

import se.runnsjo.gothere.GoToPointActivity;
import se.runnsjo.gothere.R;

/**
 * Created by Staffan on 2015-12-19.
 */
public class ScoreHandler {

    private static final int POINTS_CUTOFF_DISTANCE = 100;

    private GoToPointActivity activity;

    public ScoreHandler(GoToPointActivity activity) {
        this.activity = activity;
    }

    public int addScoreBasedOnDistance(int distance) {
        SharedPreferences savedScore = activity.getPreferences(Context.MODE_PRIVATE);

        int score;
        if(distance > POINTS_CUTOFF_DISTANCE){
            score = -40;
        } else {
            score = (POINTS_CUTOFF_DISTANCE - distance) * 2;
        }

        int oldTotalScore = savedScore.getInt(activity.getString(R.string.total_score), 0);
        int newTotalScore = oldTotalScore + score;

        SharedPreferences.Editor editor = savedScore.edit();
        editor.putInt(activity.getString(R.string.total_score), newTotalScore);

        int oldHighscore = savedScore.getInt(activity.getString(R.string.highscore), 0);
        if(score > oldHighscore) {
            editor.putInt(activity.getString(R.string.highscore), score);
        }

        editor.commit();

        return score;
    }

    public int getTotalScore() {
        SharedPreferences savedScore = activity.getPreferences(Context.MODE_PRIVATE);
        return savedScore.getInt(activity.getString(R.string.total_score), 0);
    }

    public int getHighScore() {
        SharedPreferences savedScore = activity.getPreferences(Context.MODE_PRIVATE);
        return savedScore.getInt(activity.getString(R.string.highscore), 0);
    }
}
