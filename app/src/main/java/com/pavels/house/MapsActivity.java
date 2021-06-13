package com.pavels.house;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pavels.house.databinding.ActivityMapsBinding;

import java.util.Arrays;

public class MapsActivity extends MainActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    //private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //binding = ActivityMapsBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());

        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        // 49.0411547N, 16.3078331E
        // 49°2'28.207"N, 16°18'28.373"E

        double[][] souradnice = {
                {43, 49, 2, 28.29, 16, 18, 28.205}, {18, 49, 2, 52.85, 15, 49, 0.012}, {34, 49, 4, 4.218, 16, 6, 49.625}, {19, 49, 5, 47.794, 16, 15, 22.484}, {20, 49, 6, 48.566, 15, 37, 5.577}, {14, 49, 7, 34.438, 15, 58, 57.029}, {23, 49, 8, 44.539, 15, 50, 36.483}, {41, 49, 8, 45.966, 15, 52, 49.475}, {15, 49, 12, 11.548, 16, 3, 53.246}, {28, 49, 12, 24.439, 15, 52, 55.94}, {24, 49, 12, 27.917, 15, 51, 26.672}, {32, 49, 12, 30.869, 15, 53, 27.611}, {31, 49, 12, 35.303, 15, 52, 52.261}, {30, 49, 12, 38.803, 15, 53, 13.103}, {11, 49, 12, 57.446, 15, 53, 22.224}, {36, 49, 12, 59.758, 15, 53, 50.721}, {12, 49, 13, 3.268, 15, 52, 33.777}, {26, 49, 13, 9.079, 15, 53, 52.238}, {27, 49, 13, 14.698, 15, 52, 54.344}, {5, 49, 13, 18.501, 15, 43, 45.319}, {29, 49, 13, 22.285, 15, 53, 24.705}, {35, 49, 13, 29.539, 15, 51, 58.924}, {33, 49, 13, 34.779, 16, 0, 5.417}, {10, 49, 13, 35.055, 15, 52, 10.204}, {6, 49, 13, 43.879, 15, 43, 57.975}, {42, 49, 15, 28.969, 15, 55, 15.923}, {7, 49, 15, 50.543, 16, 5, 33.576}, {8, 49, 15, 59.717, 16, 5, 41.591}, {22, 49, 16, 12.087, 16, 1, 44.178}, {39, 49, 16, 23.595, 16, 1, 37.836}, {9, 49, 16, 25.851, 16, 3, 48.549}, {40, 49, 17, 8.673, 16, 1, 11.61}, {3, 49, 17, 26.589, 16, 2, 1.561}, {4, 49, 18, 11.552, 15, 57, 1.48}, {17, 49, 18, 16.523, 16, 33, 29.236}, {25, 49, 20, 10.118, 15, 57, 23.147}, {38, 49, 21, 13.911, 16, 4, 28.978}, {13, 49, 21, 20.527, 15, 59, 55.755}, {37, 49, 21, 22.844, 15, 59, 41.241}, {21, 49, 27, 2.814, 16, 31, 40.246}, {2, 50, 13, 50.065, 15, 44, 42.75}
                };

        int souradnice_od = 1;
        for (double[] doubles : souradnice) {
            double N = doubles[souradnice_od] + doubles[souradnice_od + 1] / 60 + doubles[souradnice_od + 2] / 3600;
            Log.d("N-souradnice", String.valueOf(N));
            double E = doubles[souradnice_od + 3] + doubles[souradnice_od + 4] / 60 + doubles[souradnice_od + 5] / 3600;
            Log.d("E-souradnice", String.valueOf(E));
            Integer ID = ((Long) Math.round(doubles[0])).intValue();
            String[] DB = GetText4Title(doubles, ID);
            String title = DB[0];
            float color = Float.parseFloat(DB[1]);
            mMap.addMarker( new MarkerOptions()
                            .position(new LatLng(N, E))
                            //.icon(BitmapDescriptorFactory.defaultMarker(color))
                            .title(title) ).setTag(ID);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(49.7437506, 15.3386478), 6.0f)); //Mapa na střed česka
        mMap.setOnMarkerClickListener(this);
    }

    public String[] GetText4Title(double[] souradnice, Integer ID) {
        Log.d("GT4T", Arrays.toString(souradnice));
        return new String[]{"Test", String.valueOf(240.0f)};
    }

    /** Called when the user clicks a marker. */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        Toast.makeText(this, marker.getTag()+"\n"+marker.getTitle(), Toast.LENGTH_LONG).show();

        // Retrieve the data from the marker.
        //Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        /*if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this, marker.getTag()+marker.getTitle(), Toast.LENGTH_SHORT).show();
            /*+" has been clicked " + clickCount + " times."*//*
        }*/
        //else Toast.makeText(this, "WHAT THE HELL? :D", Toast.LENGTH_LONG).show();

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }
    public void ShowMain(View view){

        Intent myIntent = new Intent(view.getContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);

    }
}