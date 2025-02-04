package com.example.geoguard;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import java.util.HashMap;
import java.util.List;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final Activity context;
    private final HashMap<String, MapsActivity.Hospital> hospitalMap;

    public CustomInfoWindowAdapter(Activity context, List<MapsActivity.Hospital> hospitals) {
        this.context = context;
        this.hospitalMap = new HashMap<>();

        for (MapsActivity.Hospital hospital : hospitals) {
            hospitalMap.put(hospital.name, hospital);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null; // Use default frame
    }

    @Override
    public View getInfoContents(Marker marker) {
        // If it's the user's location marker, use the default info window
        if (marker.getTitle().equals("My Current Location")) {
            return null;
        }

        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_custom_info_window_adapter, null);

        ImageView imageView = view.findViewById(R.id.hospitalImage);
        TextView titleView = view.findViewById(R.id.hospitalName);
        TextView addressView = view.findViewById(R.id.hospitalAddress);
        TextView hoursView = view.findViewById(R.id.hospitalHours);

        MapsActivity.Hospital hospital = hospitalMap.get(marker.getTitle());
        if (hospital != null) {
            imageView.setImageResource(hospital.imageResId);
            titleView.setText(hospital.name);
            addressView.setText(hospital.address);
            hoursView.setText(hospital.openHours);
        }

        return view;
    }

}
