package se.runnsjo.gothere;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import se.runnsjo.gothere.data.DataHandler;
import se.runnsjo.gothere.data.Goal;
import se.runnsjo.gothere.data.ScoreHandler;

public class GoToPointActivity extends FragmentActivity {
    public static final String TAG = "GoToPoint";

    private GoogleMap mMap;
    private MapHandler mapHandler;
    private DataHandler dataHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_to_point);

        dataHandler = new DataHandler(this);
        mapHandler = new MapHandler(this, getSupportFragmentManager(), new LocationHandler(this), dataHandler.createGoalHandler());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapHandler.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapHandler.onStop();
    }

    public void goalClicked(Goal goal) {
	    int distance = goal.getMetersToTarget();
	    ScoreHandler scoreHandler = dataHandler.createScoreHandler();

	    int scoreForThisGoal = scoreHandler.addScoreBasedOnDistance(distance);
	    goal.setPoints(scoreForThisGoal);

	    goal.getMarker().remove();
	    goal.setMarker(null);
	    mapHandler.createGoalMarker(goal);

	    Toast.makeText(getApplicationContext(), "Distance: " + distance + "m. Score: " + scoreForThisGoal, Toast.LENGTH_SHORT).show();
    }

	public void calculateNewGoals(View view) {
		int missingGoals = mapHandler.createGoals();
		ScoreHandler scoreHandler = dataHandler.createScoreHandler();
		int totalPenalty = 0;
		for(int i = 0; i < missingGoals; i++) {
			totalPenalty += scoreHandler.addScoreBasedOnDistance(Integer.MAX_VALUE);
		}

		if(totalPenalty < 0) {
			Toast.makeText(getApplicationContext(), missingGoals + " goals not taken. Penalty points: "+ totalPenalty, Toast.LENGTH_SHORT).show();
		}
	}

    public void showScoreClickHandler(View view) {
        ScoreHandler scoreHandler = dataHandler.createScoreHandler();
        Toast toast = Toast.makeText(getApplicationContext(), "Total score: " + scoreHandler.getTotalScore() + ". Highscore: " + scoreHandler.getHighScore(), Toast.LENGTH_SHORT);
        toast.show();

    }
}
