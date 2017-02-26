package se.runnsjo.gothere.data;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.Date;

/**
 * Created by Staffan on 2016-04-26.
 */
public class Goal {
	private LatLng position;
	private LatLng positionWhenMarkedAsDone;

	private int points;

	private transient Marker marker;

	public Goal(LatLng position) {
		this.position = position;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getPoints() {
		return points;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}

	public Marker getMarker() {
		return marker;
	}

	public LatLng getPosition() {
		return position;
	}

	public boolean isClickable() {
		return positionWhenMarkedAsDone == null;
	}

	public void setPositionWhenMarkedAsDone(LatLng positionWhenMarkedAsDone) {
		this.positionWhenMarkedAsDone = positionWhenMarkedAsDone;
	}

	public int getMetersToTarget() {
		if(position == null || positionWhenMarkedAsDone == null) {
			return Integer.MAX_VALUE;
		}

		long now = new Date().getTime();
		Location goalLocation = new Location("GoalLocation");
		goalLocation.setLatitude(position.latitude);
		goalLocation.setLongitude(position.longitude);
		goalLocation.setTime(now);

		Location userLocation = new Location("GoalLocation");
		userLocation.setLatitude(positionWhenMarkedAsDone.latitude);
		userLocation.setLongitude(positionWhenMarkedAsDone.longitude);
		userLocation.setTime(now);

		return Math.round(userLocation.distanceTo(goalLocation));
	}
}
