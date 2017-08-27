package com.example.jules.familytrackr;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class AskName extends AppCompatActivity {

    private EditText t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //boolean bo = checkIfDoneAlready();

        if(!checkIfDoneAlready()){
            setContentView(R.layout.activity_ask_name);
            t = (EditText) findViewById(R.id.editText);

            Button b = (Button) findViewById(R.id.button4);

            b.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    createUserData();
                }
            });
        }

    }



    private boolean checkIfDoneAlready(){
        File file = new File(getApplicationContext().getFilesDir(),"TrackrData");
        File gpxfile = new File(file, "data.txt");


        if(gpxfile.exists()) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            return true;
        }else{
            return false;
        }
    }

    private void createUserData(){
        String name = t.getText().toString();
        try{
            File file = new File(getApplicationContext().getFilesDir(),"TrackrData");
            File gpxfile = new File(file, "data.txt");


            String cle = UUID.randomUUID().toString();
            WifiManager manager =(WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            WifiInfo info = manager.getConnectionInfo();
            String id = info.getMacAddress();

            file.mkdir();
            FileWriter writer = new FileWriter(gpxfile);
            writer.append("cle&" + cle + "=" + "id&" + id + "=" + "name&" + name);
            writer.flush();
            writer.close();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

        }catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
