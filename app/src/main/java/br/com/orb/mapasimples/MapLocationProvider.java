package br.com.orb.mapasimples;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by -Bernardo on 2016-08-10.
 */
public class MapLocationProvider implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public interface MapLocationListener {

        void onLocationError();

        void onUpdatedLocationAcquired(Location location);

        void onInitialLocationAcquired(Location location);
    }

    private GoogleApiClient apiClient;
    private MapLocationListener listener;
    private LocationRequest locationRequest;

    public MapLocationProvider(Context context, MapLocationListener listener) {
        this.listener = listener;
        setUpApiClient(context);
        setUpLocationRequest();
    }

    private void setUpLocationRequest() {
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(5 * 1000)
                .setFastestInterval(1000);
    }

    private void setUpApiClient(Context context) {
        apiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void start() {
        apiClient.connect();
    }

    public void stop() {
        if (apiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, this);
            apiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        if (location != null) {
            listener.onInitialLocationAcquired(location);
        } else {
            listener.onLocationError();
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, locationRequest, MapLocationProvider.this);
            }
        }, 6000);
    }

    @Override
    public void onConnectionSuspended(int i) {
        listener.onLocationError();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        listener.onLocationError();
    }

    @Override
    public void onLocationChanged(Location location) {
        listener.onUpdatedLocationAcquired(location);
    }
}
