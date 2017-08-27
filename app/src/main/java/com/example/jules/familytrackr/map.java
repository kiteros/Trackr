package com.example.jules.familytrackr;

import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.zxing.WriterException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import android.os.*;

public class map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int mInterval = 5000; // 5 seconds by default, can be changed later
    private Handler mHandler;
    int x = 0;
    Marker m;
    MarkerOptions a = new MarkerOptions().position(new LatLng(50,6));
    double[] lat = new double[100];
    double[] lon = new double[100];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mHandler = new Handler();


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     *
     *
     */

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPos();
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                updateStatus(); //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void resetPos() {
        mStatusChecker.run();
    }

    void stopPos() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    void updateStatus(){
        x += 10;




        RequestTask getfollow = new RequestTask(getApplicationContext());
        if(variables.getLinks(0) != null){
            String urlGetFollow = variables.getLinks(0);
            String result = null;
            try {
                result = getfollow.execute(urlGetFollow).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if(result != null){
                if(result.length() > 0){
                    lat[0] = Double.valueOf(result.split("'")[0]);
                    lon[0] = Double.valueOf(result.split("'")[1]);
                    m.setPosition(new LatLng(lat[0], lon[0]));
                    m.setVisible(true);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat[0], lon[0])));
                }

            }


        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        m = mMap.addMarker(a);
        m.setTitle(variables.getNames(0));
        m.setVisible(false);
        resetPos();
        // Add a marker in Sydney and move the camera
        /**/


        try{
            File t = new File(getApplicationContext().getFilesDir(),"TrackrData/dataUser.txt");
            FileReader f = new FileReader(t);
            BufferedReader br = new BufferedReader(f);

            if(t.exists() && t.length() > 0){

                String s = br.readLine();
                int nbPeople = s.split("^").length;

                for(int i = 1; i < nbPeople; i++){
                    variables.setLinks(s.split("^")[i].split("%")[0].split(";")[1], i-1);
                    variables.setNames(s.split("^")[i].split("%")[1].split(";")[1].replace("^", ""), i-1);
                }

            }

        }catch (IOException ioe) {
            ioe.printStackTrace();
        }






    }
}
