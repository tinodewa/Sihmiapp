package com.roma.android.sihmi.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.roma.android.sihmi.R;

import androidx.fragment.app.FragmentActivity;

public class AlamatMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private double ext_lat;
    private double ext_long;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alamat_maps);

        getIntentData();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Add a marker in Sydney and move the camera
        LatLng latLng = new LatLng(ext_lat, ext_long);
        googleMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Place"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
    }

    private void getIntentData() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String get_lat = extras.getString("EXTRA_LAT");
            String get_long = extras.getString("EXTRA_LONG");
            if (get_lat != null) {
                ext_lat = Double.parseDouble(get_lat);
            }
            if (get_long != null) {
                ext_long = Double.parseDouble(get_long);
            }
        } else {
            Toast.makeText(this, "Astagfirullah, terjadi kesalahan di aplikasi", Toast.LENGTH_SHORT).show();
        }
    }
}
