package se.runnsjo.gothere.data;

import se.runnsjo.gothere.GoToPointActivity;

/**
 * Created by Staffan on 2015-12-19.
 */
public class DataHandler {
    private GoToPointActivity activity;

    public DataHandler(GoToPointActivity activity) {
        this.activity = activity;
    }

    public GoalHandler createGoalHandler() {
        return new GoalHandler(activity);
    }

    public ScoreHandler createScoreHandler() {
        return new ScoreHandler(activity);
    }

}
