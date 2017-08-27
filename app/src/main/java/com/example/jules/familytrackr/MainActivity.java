package com.example.jules.familytrackr;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jules.familytrackr.barcode.BarcodeCaptureActivity;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static android.R.attr.width;
import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int BARCODE_READER_REQUEST_CODE = 1;
    private Handler mHandler;
    private int mInterval = 5000;
    private String link_perso = "";
    private Location loc = null;
    String _cle = "";
    String _id = "";
    String _name = "";

    private Button b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageView = (ImageView) findViewById(R.id.qrCode);
        b = (Button) findViewById(R.id.button2);
        Button map = (Button) findViewById(R.id.button3);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                loc = location;
                updateStatus();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
                startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), map.class);
                startActivity(intent);
            }
        });



        try{
            File t = new File(getApplicationContext().getFilesDir(),"TrackrData/data.txt");
            FileReader f = new FileReader(t);
            BufferedReader br = new BufferedReader(f);

            if(t.exists() && t.length() > 0){

                String s = br.readLine();
                _cle = s.split("=")[0].split("&")[1];
                _id = s.split("=")[1].split("&")[1];
                _name = s.split("=")[2].split("&")[1];
                //tv.setText(s);

                String lat = "";
                String lon = "";
                if(loc == null){
                    lat = "0";
                    lon = "0";
                }else{
                    lat = String.valueOf(loc.getLatitude());
                    lon = String.valueOf(loc.getLongitude());
                }

                try {
                    link_perso = "http://juleseschbach.com/itracku/keyUpdate.php?id=" + _id + "&pos=" + lat + "'" + lon + "&key=" + _cle;
                    Bitmap bitmap = TextToImageEncode("http://www.juleseschbach.com/itracku/keyShare.php?id=" + _id + "&key=" + _cle + "NOM=" + _name);
                    imageView.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }

            }

        }catch (IOException ioe) {
            ioe.printStackTrace();
        }

        mHandler = new Handler();
        resetPos();


    }

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

        String lat = "";
        String lon = "";
        if(loc == null){
            lat = "0";
            lon = "0";
        }else{
            lat = String.valueOf(loc.getLatitude());
            lon = String.valueOf(loc.getLongitude());
        }
        link_perso = "http://juleseschbach.com/itracku/keyUpdate.php?id=" + _id + "&pos=" + lat + "'" + lon + "&key=" + _cle;


        RequestTask getfollow = new RequestTask(getApplicationContext());
        String urlGetFollow = link_perso;
        String result = null;
        try {
            result = getfollow.execute(urlGetFollow).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);//Menu Resource, Menu
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:

                refresh();
                return true;
            case R.id.item2:

                DeleteData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;
                    try {
                        saveUserData(barcode.displayValue);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else b.setText("no bar code");
            } else Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format),
                    CommonStatusCodes.getStatusCodeString(resultCode)));
        } else super.onActivityResult(requestCode, resultCode, data);
    }


    private void DeleteData(){
        File file = new File(getApplicationContext().getFilesDir(),"TrackrData");
        File gpxfile = new File(file, "dataUser.txt");
        gpxfile.delete();
    }


    private void refresh(){
        File file = new File(getApplicationContext().getFilesDir(),"TrackrData");
        File gpxfile = new File(file, "data.txt");
        gpxfile.delete();

        Intent intent = new Intent(getApplicationContext(), AskName.class);
        startActivity(intent);


    }


    private void saveUserData(String s) throws IOException {
        File file = new File(getApplicationContext().getFilesDir(),"TrackrData");
        String userName = s.split("NOM=")[1];
        s = s.split("NOM=")[0];

        if(!file.exists()) {

            file.mkdir();
            File gpxfile = new File(file, "dataUser.txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append("link;" + s + "%nom;" + userName + "^");
            writer.flush();
            writer.close();

        }else{
            //partie a déleter quand tout donctionne
            File gpxfile = new File(file, "dataUser.txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append("link;" + s + "%nom;" + userName + "^");
            writer.flush();
            writer.close();

        }

        Intent intent = new Intent(getApplicationContext(), map.class);
        startActivity(intent);

    }



    private Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    500, 500, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.black):getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }
}
