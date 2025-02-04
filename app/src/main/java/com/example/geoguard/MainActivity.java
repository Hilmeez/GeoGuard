package com.example.geoguard;

import static androidx.core.content.ContextCompat.startActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference userRef, locationRef;
    private TextView welcomeTextView;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;

    private FusedLocationProviderClient fusedLocationClient;
    private Button btnOpenMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");
        locationRef = FirebaseDatabase.getInstance().getReference("UserLocations");
        btnOpenMap = findViewById(R.id.btnOpenMap);

        // Initialize UI components
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        welcomeTextView = findViewById(R.id.tvWelcome);

        // Set up the toolbar
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Set up drawer navigation
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Handle navigation menu item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                Toast.makeText(MainActivity.this, "Home clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, MainActivity.class));
            } else if (item.getItemId() == R.id.about) {
                startActivity(new Intent(MainActivity.this, About.class));
                Toast.makeText(MainActivity.this, "About clicked", Toast.LENGTH_SHORT).show();
            } else if (item.getItemId() == R.id.logout) {
                handleLogout();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
        ImageView gifImageView = findViewById(R.id.imgGuardGif);
        Glide.with(this).asGif().load(R.drawable.penguin).into(gifImageView);

        // Fetch and display user data
        fetchAndDisplayUserData();

        // Initialize Location Services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Get User Location
        getUserLocation();

        btnOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void fetchAndDisplayUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            userRef.child(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    String name = task.getResult().child("name").getValue(String.class);
                    String email = task.getResult().child("email").getValue(String.class);

                    if (name != null) {
                        welcomeTextView.setText("Welcome, " + name + "!");
                    } else {
                        welcomeTextView.setText("Welcome!");
                    }

                    View headerView = navigationView.getHeaderView(0);
                    TextView headerName = headerView.findViewById(R.id.nav_header_name);
                    TextView headerEmail = headerView.findViewById(R.id.nav_header_email);

                    if (name != null) headerName.setText(name);
                    if (email != null) headerEmail.setText(email);
                } else {
                    Toast.makeText(MainActivity.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        }
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                saveLocationToFirebase(location);
            } else {
                Toast.makeText(MainActivity.this, "Location not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveLocationToFirebase(Location location) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        String userId = Objects.requireNonNull(user.getUid());
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String userAgent = System.getProperty("http.agent");

        // Get user name from Firebase
        userRef.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                String name = task.getResult().child("name").getValue(String.class);
                if (name == null) {
                    name = "Unknown User"; // Default if name is missing
                }

                // Create UserLocationData with name
                UserLocationData userLocation = new UserLocationData(name, location.getLatitude(), location.getLongitude(), timestamp, userAgent);

                // Save to Firebase
                locationRef.child(userId).setValue(userLocation)
                        .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this, "Location saved!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Failed to save location", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(MainActivity.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
            }
        });
    }




//logout
    private void handleLogout() {
        mAuth.signOut();
        startActivity(new Intent(MainActivity.this, Login.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

   /* @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

// Data Model Class for User Location
class UserLocationData {
    public String name;
    public double latitude;
    public double longitude;
    public String timestamp;
    public String userAgent;

    public UserLocationData(String name,double latitude, double longitude, String timestamp, String userAgent) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.userAgent = userAgent;
    }
}
