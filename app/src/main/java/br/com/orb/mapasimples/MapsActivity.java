package br.com.orb.mapasimples;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, MapLocationProvider.MapLocationListener {

    private GoogleMap mapa;
    private MapLocationProvider locationProvider;
    private MarkerOptions positionMarker;
    private ProgressDialog dialog;
    private float zoom = 12.0f;
    private boolean isShowingMarker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        locationProvider = new MapLocationProvider(getApplicationContext(), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMap();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationProvider.stop();
    }

    private void setUpMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        locationProvider.start();
    }

    @Override
    public void onLocationError() {
        Toast.makeText(MapsActivity.this, "Erro ao tentar obter sua localizacao", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUpdatedLocationAcquired(Location location) {
        LatLng position = setPositionMarker(location);
        Toast.makeText(MapsActivity.this, "Nova localizacao obtida!", Toast.LENGTH_SHORT).show();
        mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(position, zoomIn()));
    }

    @Override
    public void onInitialLocationAcquired(Location location) {
        LatLng position = setPositionMarker(location);
        mapa.moveCamera(CameraUpdateFactory.newLatLng(position));
        Toast.makeText(MapsActivity.this, "Buscando localizacao atualizada", Toast.LENGTH_LONG).show();
    }

    @NonNull
    private LatLng setPositionMarker(Location location) {
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        if (positionMarker == null) {
            positionMarker = new MarkerOptions().position(position).title("Você está aqui!");
        } else {
            positionMarker.position(position);
        }
        addMarkerIfNotShowing();
        return position;
    }

    private void addMarkerIfNotShowing() {
        if (!isShowingMarker) {
            mapa.addMarker(positionMarker);
            isShowingMarker = true;
        }
    }

    private float zoomIn() {
        if (zoom < 17) {
            zoom++;
        }
        return zoom;
    }
}
