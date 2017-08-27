package com.example.jules.familytrackr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.R.attr.data;
import static android.R.attr.targetActivity;
import static android.R.attr.variablePadding;

/**
 * Created by jules on 21/12/16.
 */

public class RequestTask extends AsyncTask<String, String, String> {

    String retour = "";
    private Context mycontext;
    private int method = 0;
    private Activity aa = null;
    private LinearLayout l = null;
    ProgressDialog progDailog = null;
    SwipeRefreshLayout sw = null;
    ProgressDialog pr;
    //1 = login
    //2 = inscript

    public RequestTask(Context cont){
        mycontext = cont;
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();


    }

    @Override
    protected String doInBackground(String... uri) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;

        try {
            response = httpclient.execute(new HttpGet(uri[0]));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();
                out.close();
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            retour = "problem";
        } catch (IOException e) {
            retour = "problem";
        }
        retour = responseString;

        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);



    }


}