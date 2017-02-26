package se.runnsjo.gothere;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Staffan on 2015-11-08.
 */
public class LocationHandler implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient googleApiClient;
    private Location lastLocation;

    private List<LocationAvailableListener> locationAvailableListeners;

    public LocationHandler(Context context) {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        locationAvailableListeners = new ArrayList<>();
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void start() {
        googleApiClient.connect();
    }

    public void stop() {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastLocation != null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, createLocationRequest(), this);
        }

        for(LocationAvailableListener listener : locationAvailableListeners) {
            listener.locationAvailable();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(GoToPointActivity.TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(GoToPointActivity.TAG, "Connection suspended");
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
    }

    public boolean isLocationAvailable() {
        return lastLocation != null;
    }

    public void addLocationAvailableListener(LocationAvailableListener listener) {
        locationAvailableListeners.add(listener);
    }

    private LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    public interface LocationAvailableListener {
        public void locationAvailable();
    }
}
