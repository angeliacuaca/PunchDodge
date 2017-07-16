package com.lagxen.punchdodge.controller.cloud;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.lagxen.punchdodgeg.backend.myApi.MyApi;
import java.io.IOException;

/**
 * Created by angeliacuaca on 8/10/16.
 */
public class GenerateRequestAsyncTask extends AsyncTask<Pair<Context, String>,
        Void, String> {
    private static MyApi myApiService = null;
    private Context context;

    @Override
    protected String doInBackground(Pair<Context, String>... params) {


        if(myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp
                    .newCompatibleTransport(), new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local
                    // devappserver

                    .setRootUrl("https://lagxen-punchdodge.appspot.com/_ah/api/")

//                    .setRootUrl("http://localhost:8080/_ah/api/")

                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?>
                                                       abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent
                                    (true);
                        }
                    });
            // end options for devappserver

            myApiService = builder.build();
        }

        //get user input
        context = params[0].first;
        String name = params[0].second;

        try{
        //get data from the API
        return myApiService.sayHi(name).execute().getData();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(context, "Game Ready!", Toast.LENGTH_LONG).show();
    }
}