package com.example.geoguard;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.geoguard.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private List<Hospital> hospitalList;
    private Marker lastClickedMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize fused location provider
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void initializeHospitals() {
        hospitalList = new ArrayList<>();

        hospitalList.add(new Hospital("Unit Kesihatan Klinik UiTM",
                new LatLng(6.447169127461131, 100.27981787819492),
                "UiTM Cawangan Perlis, Uitm Arau,"+"\n"+" 02600 Arau, Perlis",
                "Open: 9 AM - 1 PM, 2 PM - 5 PM"+"\n"+"Closed: Saturday, Sunday",
                R.drawable.uitmperlis));

        hospitalList.add(new Hospital("Klinik Kesihatan Arau",
                new LatLng(6.432836678376348, 100.27033190153452),
                "Arau, 02600 Arau, Perlis",
                "Open: 8 AM - 5 PM"+"\n"+"Closed: Saturday, Sunday",
                R.drawable.klinik_arau));

        hospitalList.add(new Hospital("MEDIKLINIK RAKYAT DR NAIM"+"\n"+"AHMAD ARAU 16 JAM",
                new LatLng(6.4291529115271375, 100.27087133372642),
                "NO. 21, TINGKAT BAWAH," +"\n"+
                        "JALAN MEDAN ARAU JAYA,"+"\n"+"MEDAN ARAU JAYA,"+"\n"+"02600 Arau, Perlis",
                "Open: 16 Hours",
                R.drawable.mediklinikrakyat));

        hospitalList.add(new Hospital("Klinik Kamil Ariff",
                new LatLng(6.4268578023033625, 100.2731048550408),
                "15, Jalan Syed Hussain, Arau,"+"\n"+" 02600 Arau, Perlis",
                "Open: 9am–12:30pm, 3:30–5:30pm,"+"\n"+" 8–9:30pm",
                R.drawable.klinikarif));

        hospitalList.add(new Hospital("Klinik Haji Adnan",
                new LatLng(6.445834544722235, 100.23770419607413),
                "13, Taman Jejawi, 02600 Arau, Arau, Kangar",
                "Open: 9am–12:30pm, 2.30pm-5pm"+"\n"+
                        "Closed: Saturday,Sunday",
                R.drawable.klinikhaji));

        hospitalList.add(new Hospital("Poliklinik DrAzhar Dan"+"\n"+"Rakan-rakan Caw Jejawi",
                new LatLng(6.445067745332194, 100.23589099622707),
                "no 3, kompleks perniagaan utara," +"\n"+
                        "persiaran utara,"+"\n"+
                        "Kampung Jejawi, 02500 Arau, Perlis",
                " Open: 12am–6am, 9pm-12am",
                R.drawable.azharjawi));

        hospitalList.add(new Hospital("Klinik Megah ",
                new LatLng(6.44511038944594, 100.23535455443469),
                "No 21, Persiaraan Jejawi Utara," +"\n"+
                        "Kompleks Perniagaan Utara," +"\n"+
                        "02600 Arau, Perlis",
                " Open: 9.30am–10:00pm"+"\n"+"Closed: Saturday",
                R.drawable.megah));

        hospitalList.add(new Hospital("Hospital Tuanku Fauziah",
                new LatLng(6.441039629986643, 100.191425594284),
                "Jalan Hospital, Kangar, Perlis",
                "Open: 24 Hours",
                R.drawable.tunku));

        hospitalList.add(new Hospital("Klinik Kesihatan Kangar",
                new LatLng(6.439632361050347, 100.19027760884832),
                "Taman Pengkalan Asam, Kangar, Perlis",
                "Open: 8:00 AM - 5:00 PM"+"\n"+"Closed: Saturday, Sunday",
                R.drawable.klinikkangar));

        hospitalList.add(new Hospital("Poliklinik Dr. Azhar Dan"+"\n"+"Rakan-Rakan Pauh",
                new LatLng( 6.43759849230624, 100.30534599206551),
                "No 13, Tingkat Bawah, Jalan Pauh 2, " +"\n"+
                        "Pusat Perniagaan Pauh, 02600 Arau, Perlis",
                "Open: 9:00 AM - 1:00 PM, 2:00 PM - 10 PM",
                R.drawable.azharpauh));

        hospitalList.add(new Hospital("KPJ Perlis Specialist Hospital",
                new LatLng(6.433416119074118, 100.18644396651996),
                "Jalan Raja Syed Alwi, Kangar, Perlis",
                "Open: 24 Hours",
                R.drawable.kpj));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            getCurrentLocation();
        }

        initializeHospitals();

        for (Hospital hospital : hospitalList) {
            mMap.addMarker(new MarkerOptions()
                    .position(hospital.location)
                    .title(hospital.name)
                    .snippet(hospital.address + "\n" + hospital.openHours)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this, hospitalList));

        mMap.setOnMarkerClickListener(marker -> {
            // Allow clicking on "My Current Location" marker
            if (marker.getTitle().equals("My Current Location")) {
                marker.showInfoWindow();
                return true;
            }

            if (lastClickedMarker != null && lastClickedMarker.equals(marker)) {
                openGoogleMaps(marker.getPosition());
            } else {
                marker.showInfoWindow();
                lastClickedMarker = marker;
            }
            return true;
        });
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng currentLocation = new LatLng(latitude, longitude);

                // Add marker for user's location
                Marker userMarker = mMap.addMarker(new MarkerOptions()
                        .position(currentLocation)
                        .title("My Current Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                // Move camera to user location
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

                // Force InfoWindow to show immediately
                if (userMarker != null) {
                    userMarker.showInfoWindow();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission is required to use this feature", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openGoogleMaps(LatLng destination) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + destination.latitude + "," + destination.longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    class Hospital {
        String name;
        LatLng location;
        String address;
        String openHours;
        int imageResId;

        public Hospital(String name, LatLng location, String address, String openHours, int imageResId) {
            this.name = name;
            this.location = location;
            this.address = address;
            this.openHours = openHours;
            this.imageResId = imageResId;
        }
    }
}
