package se.runnsjo.gothere;

import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import se.runnsjo.gothere.data.Goal;
import se.runnsjo.gothere.data.GoalHandler;

/**
 * Created by Staffan on 2015-11-08.
 */
public class MapHandler implements OnMapReadyCallback, LocationHandler.LocationAvailableListener, GoogleMap.OnInfoWindowClickListener {
	private GoToPointActivity activity;
    private GoogleMap map;
    private GoalHandler goalHandler;
    private LocationHandler locationHandler;

    public MapHandler(GoToPointActivity activity, FragmentManager fragmentManager, LocationHandler locationHandler, GoalHandler goalHandler) {
	    this.activity = activity;
        this.locationHandler = locationHandler;
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        this.goalHandler = goalHandler;

	    locationHandler.addLocationAvailableListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        map.setMyLocationEnabled(true);
	    map.setOnInfoWindowClickListener(this);
	    map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

		    @Override
		    public View getInfoWindow(Marker arg0) {
			    return null;
		    }

		    @Override
		    public View getInfoContents(Marker marker) {

			    LinearLayout info = new LinearLayout(activity);
			    info.setOrientation(LinearLayout.VERTICAL);

			    TextView title = new TextView(activity);
			    title.setTextColor(Color.BLACK);
			    title.setGravity(Gravity.CENTER);
			    title.setTypeface(null, Typeface.BOLD);
			    title.setText(marker.getTitle());

			    TextView snippet = new TextView(activity);
			    snippet.setTextColor(Color.GRAY);
			    snippet.setText(marker.getSnippet());

			    info.addView(title);
			    info.addView(snippet);

			    return info;
		    }
	    });
    }

    @Override
    public void locationAvailable() {
        for(Goal goal : goalHandler.getGoalSet()) {
	        createGoalMarker(goal);
        }

	    if (locationHandler.isLocationAvailable()) {
		    updateViewport(locationHandler.getLastLocation());
	    }
    }

    protected void onStart() {
        locationHandler.start();
    }

    protected void onStop() {
        locationHandler.stop();
    }

    public int createGoals() {
	    if (!locationHandler.isLocationAvailable()) {
		    return 5;
	    }

	    int notDoneGoals = 0;
	    for(Goal goal : goalHandler.getGoalSet()) {
		    notDoneGoals += goal.isClickable() ? 1 : 0;
	    }

	    Location userLocation = locationHandler.getLastLocation();
	    goalHandler.generateGoalSet(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));

	    for(Goal goal : goalHandler.getGoalSet()) {
		    createGoalMarker(goal);
	    }

	    updateViewport(userLocation);

	    return notDoneGoals;
    }

    public void createGoalMarker(Goal goal) {
        if(goal.getMarker() != null) {
	        return;
        }

	    if(goal.isClickable()) {
		    goal.setMarker(map.addMarker(new MarkerOptions()
				    .position(goal.getPosition())
				    .title("Click here to mark as done")));
	    } else {
		    goal.setMarker(map.addMarker(new MarkerOptions()
				    .position(goal.getPosition())
				    .alpha(0.7f)
				    .title("This one is already done")
				    .snippet("\nDistance: " + goal.getMetersToTarget() + "m, points: " + goal.getPoints())));
	    }
    }

	private void updateViewport(Location userLocation) {
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		for(Goal goal : goalHandler.getGoalSet()) {
			builder.include(goal.getPosition());
		}
		builder.include(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));
		LatLngBounds bounds = builder.build();

		map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
	}

    @Override
    public void onInfoWindowClick(Marker marker) {
	    Goal goal = goalHandler.getGoal(marker);
	    if(goal != null && goal.isClickable()) {
		    if (!locationHandler.isLocationAvailable()) {
			    return;
		    }
		    Location userLocation = locationHandler.getLastLocation();
		    goal.setPositionWhenMarkedAsDone(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));
		    activity.goalClicked(goal);

		    goalHandler.saveGoalSet();
	    }
    }
}
