package se.runnsjo.gothere.data;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import se.runnsjo.gothere.GoToPointActivity;

/**
 * Created by Staffan on 2015-12-19.
 */
public class GoalHandler {

	private static final int NUMBER_OF_GOALS = 5;
	private static final String GOAL_FILE_NAME = "goals.json";

    private GoToPointActivity activity;
	private ScoreHandler scoreHandler;

	private List<Goal> goals = new ArrayList<>();

    public GoalHandler(GoToPointActivity activity) {
        this.activity = activity;
	    scoreHandler = new ScoreHandler(activity);
	    loadGoals();
    }

    public void generateGoalSet(LatLng currentPosition) {
	    for(Goal goal : goals) {
		    Marker goalMarker = goal.getMarker();
		    if(goalMarker != null) {
			    goalMarker.remove();
		    }
	    }

	    goals.clear();

	    for(int i = 0; i < NUMBER_OF_GOALS; i++) {
		    goals.add(new Goal(generateGoalPosition(currentPosition)));
	    }

	    saveGoalSet();
    }

	public Goal getGoal(Marker marker) {
		for(Goal goal : goals) {
			if(goal.getMarker() == null) {
				continue;
			}
			if(goal.getMarker().getPosition().equals(marker.getPosition())) {
				return goal;
			}
		}
		return null;
	}

	public List<Goal> getGoalSet() {
		return goals;
	}

	public void saveGoalSet() {
		File file = new File(activity.getFilesDir(), GOAL_FILE_NAME);
		try (FileWriter outputStream = new FileWriter(file,false)){
			new Gson().toJson(goals, outputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadGoals() {
		Type collectionType = new TypeToken<List<Goal>>(){}.getType();
		File file = new File(activity.getFilesDir(), GOAL_FILE_NAME);
		try (FileReader outputStream = new FileReader(file)){
			goals = new Gson().fromJson(outputStream, collectionType);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private LatLng generateGoalPosition(LatLng currentPosition) {
		double newLatitude = currentPosition.latitude + (Math.random() > 0.5 ? 1 : -1) * Math.random() * 0.01;
		double newLongitude = currentPosition.longitude + (Math.random() > 0.5 ? 1 : -1) * Math.random() * 0.01;
		return new LatLng(newLatitude, newLongitude);
	}
}

