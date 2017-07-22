package mobile.jesus.com.banklocator.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import mobile.jesus.com.banklocator.util.PermissionUtil;
import mobile.jesus.com.banklocator.models.PlacesResponse;
import mobile.jesus.com.banklocator.network.QueryCallback;
import mobile.jesus.com.banklocator.network.QueryMapApi;
import mobile.jesus.com.banklocator.R;
import mobile.jesus.com.banklocator.dialogs.PermissionDeniedDialog;
import mobile.jesus.com.banklocator.models.Place;
import mobile.jesus.com.banklocator.constants.LocatorConstants;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,
        GoogleMap.OnMyLocationButtonClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback, QueryCallback.OnTaskCompleted {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastKnownLocation;
    private Marker currentLocationMarker;
    private boolean permissionDenied = false;
    private PlacesResponse placesResponse;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //location layer
        map.setOnMyLocationButtonClickListener(this);
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override public void onInfoWindowClick(Marker marker) {

                //get selected Place
                Place place = placesResponse.getPlaceByID((String) marker.getTag());
                System.out.println("" + place.getRating() + place.isOpen());
                if (place.isValid()) {
                    Intent intent = new Intent(MapsActivity.this, DetailActivity.class);
                    intent.putExtra("rating", place.getRating());
                    intent.putExtra("address", place.getAddress());
                    intent.putExtra("isOpen", place.isOpen());
                    intent.putExtra("name", place.getName());
                    startActivity(intent);
                }
            }
        });
        enableMyLocation();
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtil.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
        }
    }

    @Override public void onPause() {
        super.onPause();
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtil.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (map != null) {
            // Access to the location has been granted to the app.
            buildGoogleApiClient();
            map.setMyLocationEnabled(true);
        }
    }

    @Override protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }

    private void searchMap() {
        String[] params = {
                LocatorConstants.BBVA_SEARCH_TERM, String.valueOf(lastKnownLocation.getLatitude()),
                String.valueOf(lastKnownLocation.getLongitude()),
                "AIzaSyDlFQOXVCdmzVeHsNXog1fhsDPY2CZEej0"
        };
        //getResources().getString(R.string.google_maps_key)};
        new QueryMapApi(this).execute(params);
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionDeniedDialog.newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "Moving to current location", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(60000);
        locationRequest.setFastestInterval(60000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                    locationRequest, this);
        }
    }

    @Override public void onConnectionSuspended(int i) {

    }

    @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override public void onLocationChanged(Location location) {
        lastKnownLocation = location;
        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        currentLocationMarker = map.addMarker(markerOptions);

        //move map camera
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
        searchMap();
    }

    @Override public void onTaskCompleted(String s) {
        placesResponse = new PlacesResponse(s);
        for (int i = 0; i < placesResponse.getPlaces().size(); i++) {
            map.addMarker(new MarkerOptions().position(
                    new LatLng(placesResponse.getPlaces().get(i).getLocation().getLat(),
                            placesResponse.getPlaces().get(i).getLocation().getLng()))
                    .title(placesResponse.getPlaces().get(i).getName()))
                    .setTag(placesResponse.getPlaces().get(i).getId());
        }
    }
}
